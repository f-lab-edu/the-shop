package com.flab.theshop.exception.item;

import lombok.Getter;

@Getter
public class ItemTaskException extends RuntimeException  {

    private final int code;
    private final String message;

    public ItemTaskException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

}
