package com.flab.theshop.exception.item;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum ItemException {

    ITEM_NOT_FOUND(NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    ITEM_NOT_ENOUGH_STOCK(NOT_FOUND, "재고는 0개 미만이 될 수 없습니다."),
    ORDER_NOT_FOUND(NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
    ;

    private final ItemTaskException itemTaskException;

    ItemException(HttpStatus status, String message) {
        this.itemTaskException = new ItemTaskException(status.value(), message);
    }

    public ItemTaskException get() {
        return itemTaskException;
    }
}
