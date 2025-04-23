package edu.uga.cs.dawgride;

public class Ride {
    public String rideId;
    public String rideType;
    public String from;
    public String to;
    public String dateTime;
    public String posterId;

    public Ride() {}

    public Ride(String rideId, String rideType, String from, String to, String dateTime, String posterId) {
        this.rideId = rideId;
        this.rideType = rideType;
        this.from = from;
        this.to = to;
        this.dateTime = dateTime;
        this.posterId = posterId;
    }
}
