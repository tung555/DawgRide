package edu.uga.cs.dawgride;

public class Ride {
    public String requestId;
    public String riderId;
    public String from;
    public String to;
    public String dateTime;
    public String acceptedBy;

    public Ride() {
        // Firebase needs empty constructor
    }

    public Ride(String requestId, String riderId, String from, String to, String dateTime, String acceptedBy) {
        this.requestId = requestId;
        this.riderId = riderId;
        this.from = from;
        this.to = to;
        this.dateTime = dateTime;
        this.acceptedBy = acceptedBy;
    }
}
