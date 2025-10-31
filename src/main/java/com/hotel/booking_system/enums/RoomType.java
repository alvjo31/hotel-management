package com.hotel.booking_system.enums;

public enum RoomType {
    SINGLE(1, "Single Room"),
    DOUBLE(2, "Double Room"),
    TWIN(2, "Twin Room"),
    SUITE(4, "Suite"),
    DELUXE(3, "Deluxe Room"),
    PRESIDENTIAL(6, "Presidential Suite");
    private final int capacity;
    private final String displayName;

    RoomType(int capacity, String displayName) {
        this.capacity = capacity;
        this.displayName = displayName;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDisplayName() {
        return displayName;
    }
}
