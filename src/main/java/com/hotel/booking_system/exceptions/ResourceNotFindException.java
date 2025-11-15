package com.hotel.booking_system.exceptions;



public class ResourceNotFindException extends BaseException {

    // ky konstruktor sherben per
    public ResourceNotFindException(String message) {
        super(message, 404, "RESOURCE_NOT_FIND");
    }

    public ResourceNotFindException(String resourceName, Long id) {
        super(resourceName + "not found with id: ", 404, "RESOURCE_NOT_FIND");
    }


    public ResourceNotFindException(String resourceName, String field , Object value) {
        super(
                resourceName + " not found with " + field + ": '" + value + "'",
                404,
                "RESOURCE_NOT_FOUND"
        );
    }
}
