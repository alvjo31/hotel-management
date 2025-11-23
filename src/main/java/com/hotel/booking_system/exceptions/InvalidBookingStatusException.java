package com.hotel.booking_system.exceptions;

public class InvalidBookingStatusException extends BaseException {

    public InvalidBookingStatusException(String message) {
        super(message ,409 , "InvalidStatusException");
    }
}
