package com.hotel.booking_system.exceptions;

public abstract class BaseException extends RuntimeException{
    private final int statusCode;
    private final String errorCode;
    public BaseException(String message , int statusCode , String errorCode) {
       super( message );
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
