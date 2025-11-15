package com.hotel.booking_system.exceptions;

public class DuplicateResourceException extends BaseException {

    public DuplicateResourceException(String message) {
        super(message ,409 , "DuplicateResourceException");
    }


    public DuplicateResourceException(String message, int statusCode, String errorCode) {
        super(message, statusCode, errorCode);
    }


    public DuplicateResourceException(String resourceName , String field , Object value) {
        super(resourceName + "already exists"  + field + ": '" + value + ": '"  ,   409 , "RESOURCE_ALREADY_EXISTS");
    }
}
