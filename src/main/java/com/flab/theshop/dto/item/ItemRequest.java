package com.flab.theshop.dto.item;

import com.flab.theshop.domain.Item;
import com.flab.theshop.domain.ItemSellStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ItemRequest {

    @NotBlank(message = "상품 이름은 필수입니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 10, message = "최소 가격은 10원입니다.")
    private Integer price;

    @NotBlank(message = "상품 상세 설명은 필수입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력입니다.")
    @Min(value = 1, message = "최소 수량은 1개입니다.")
    @Max(value = 999, message = "최대 수량은 999개입니다")
    private Integer stockQuantity;

    private ItemSellStatus status;

    public Item toEntity() {
        return Item.builder()
                .name(this.name)
                .price(this.price)
                .itemDetail(this.itemDetail)
                .stockQuantity(this.stockQuantity)
                .status(this.status)
                .build();
    }
}
