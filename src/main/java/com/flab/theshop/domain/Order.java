package com.flab.theshop.domain;

import com.flab.theshop.exception.item.ItemException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

/*    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;*/

    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    @Builder
    public Order(Member member, OrderStatus status, LocalDateTime orderDate) {
        this.member = member;
        this.status = status;
        this.orderDate = orderDate;
    }

    /**
     * 주문생성
     */
    public static Order createOrder(Member member, OrderItem... orderItems) {
        Order order = Order.builder()
                .member(member)
                .status(OrderStatus.ORDER)
                .orderDate(LocalDateTime.now())
                .build();
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        return order;
    }

    /**
     * 주문 취소
     */
    public void cancel() {
        //배송상태가 이미 배송완료인 경우
//        if (delivery.getStatus() == DeliveryStatus.COMP) {
//            throw ItemException.ITEM_ALREADY_SHIPPED.get();
//        }

        status = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }
}
