package com.hotel.booking_system.exceptions;

public class BadRequestException extends BaseException{

    //throw new BadRequestException("Check-in date cannot be in the past");

    public BadRequestException(String message) {
        super(message , 400 , "Bad Request");
    }

    //throw new BadRequestException("Invalid email format", "INVALID_EMAIL");
    public BadRequestException(String message, String errorCode) {
        super(message, 400 , errorCode);
    }
}
