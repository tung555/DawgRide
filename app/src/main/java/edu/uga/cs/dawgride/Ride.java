package edu.uga.cs.dawgride;

/**
 * Model class representing a ride post, either a ride offer or a ride request.
 * Used throughout the app for displaying, editing, and accepting rides.
 */
public class Ride {
    public String rideId;
    public String rideType;
    public String from;
    public String to;
    public String dateTime;
    public String posterId;

    public Ride() {}

    /**
     * Constructs a Ride with all fields populated.
     *
     * @param rideId    Unique ID of the ride.
     * @param rideType  Type of ride ("offer" or "request").
     * @param from      Origin location.
     * @param to        Destination location.
     * @param dateTime  Date and time string.
     * @param posterId  ID of the user who posted the ride.
     */
    public Ride(String rideId, String rideType, String from, String to, String dateTime, String posterId) {
        this.rideId = rideId;
        this.rideType = rideType;
        this.from = from;
        this.to = to;
        this.dateTime = dateTime;
        this.posterId = posterId;
    }
}
