package com.cts.healthcareappointment.notificationmodule.Entity;


public enum TimeSlots {
    NINE_TO_ELEVEN("9:00 - 11:00"),
    ELEVEN_TO_ONE("11:00 - 1:00"),
    TWO_TO_FOUR("2:00 - 4:00"),
    FOUR_TO_SIX("4:00 - 6:00");
 
    private final String displayName;
 
    TimeSlots(String displayName) {
        this.displayName = displayName;
    }
 
    public String getDisplayName() {
        return displayName;
    }
}
 