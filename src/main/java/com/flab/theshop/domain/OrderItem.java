package com.flab.theshop.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public OrderItem(Item item, int orderPrice, int count) {
        this.item = item;
        this.orderPrice = orderPrice;
        this.count = count;

        //주문 시 재고 감소
        item.removeStock(count);
    }

    /**
     * 주문 취소
     */
    public void cancel() {
        item.addStock(count);
    }

    /**
     * 주문 상품 전체 가격 조회
     */
    public int getTotalPrice() {
        return orderPrice * count;
    }
}
