package com.flab.theshop.domain;

import com.flab.theshop.exception.item.ItemException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockQuantity; //재고

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus status;

    @Builder
    public Item(String name, int price, int stockQuantity, String itemDetail, ItemSellStatus status) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.itemDetail = itemDetail;
        this.status = status;
    }

    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw ItemException.ITEM_NOT_ENOUGH_STOCK.get();
        }
        this.stockQuantity = restStock;
    }
}
