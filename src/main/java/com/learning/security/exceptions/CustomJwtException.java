package com.learning.security.exceptions;

import lombok.Getter;

@Getter
public class CustomJwtException extends RuntimeException {
    private final int statusCode;

    public CustomJwtException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}
