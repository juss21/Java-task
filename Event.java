//package com.playtech.assignment;

public class Event {
    public static final String STATUS_DECLINED = "DECLINED";
    public static final String STATUS_APPROVED = "APPROVED";

    public String transaction_id;
    public String status;
    public String message;

    public Event(String transaction_id, String status, String message) {
        this.transaction_id = transaction_id;
        this.status = status;
        this.message = message;
    }
}
