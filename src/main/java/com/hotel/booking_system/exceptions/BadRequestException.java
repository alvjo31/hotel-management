package com.hotel.booking_system.exceptions;

public class BadRequestException extends BaseException{


    public BadRequestException(String message) {
        super(message , 400 , "Bad Request");
    }

    //throw new BadRequestException("Invalid email format", "INVALID_EMAIL");
    public BadRequestException(String message, String errorCode) {
        super(message, 400 , errorCode);
    }
}
