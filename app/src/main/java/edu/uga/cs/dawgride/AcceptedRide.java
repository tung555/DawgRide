package edu.uga.cs.dawgride;

/**
 * Model class representing a ride that has been accepted by a user.
 * Used for storing and retrieving accepted ride data from Firebase.
 */
public class AcceptedRide {
    public String rideId;
    public String rideType;
    public String from;
    public String to;
    public String dateTime;
    public String acceptorId;
    public String driverName;
    public String driverEmail;
    public String riderName;
    public String riderEmail;

    // Required default constructor for Firebase deserialization
    public AcceptedRide() {}
}
