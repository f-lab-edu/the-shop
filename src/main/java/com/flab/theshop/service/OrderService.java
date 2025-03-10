package com.flab.theshop.service;

import com.flab.theshop.domain.Item;
import com.flab.theshop.domain.Member;
import com.flab.theshop.domain.Order;
import com.flab.theshop.domain.OrderItem;
import com.flab.theshop.dto.order.OrderRequest;
import com.flab.theshop.exception.item.ItemException;
import com.flab.theshop.exception.member.MemberException;
import com.flab.theshop.respository.ItemRepository;
import com.flab.theshop.respository.MemberRepository;
import com.flab.theshop.respository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(OrderRequest request) {
        //엔티티 조회
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(ItemException.ITEM_NOT_FOUND::get);
        Member member = memberRepository.findByUserId(request.getUserId())
                .orElseThrow(MemberException.MEMBER_NOT_EXISTS::get);

        //주문상품 생성
        OrderItem orderItem = new OrderItem(item, item.getPrice(), request.getCount());

        //주문 생성
        Order order = Order.createOrder(member, orderItem);

        //주문 저장
        orderRepository.save(order);
        return order.getId();
    }


    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        //주문 엔티티 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(ItemException.ORDER_NOT_FOUND::get);

        //주문 취소
        order.cancel();
    }
}
