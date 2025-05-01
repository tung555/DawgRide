package edu.uga.cs.dawgride;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter to display a list of past rides in the user's ride history.
 * Populates views for ride details such as type, locations, date/time, rider, and driver info.
 */
public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.ViewHolder> {

    private Context context;
    private List<AcceptedRide> rideHistory;

    /**
     * Constructs a new RideHistoryAdapter.
     *
     * @param context      the context where the adapter is used
     * @param rideHistory  the list of accepted rides that make up the user's ride history
     */
    public RideHistoryAdapter(Context context, List<AcceptedRide> rideHistory) {
        this.context = context;
        this.rideHistory = rideHistory;
    }

    @NonNull
    @Override
    public RideHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ride_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideHistoryAdapter.ViewHolder holder, int position) {
        AcceptedRide ride = rideHistory.get(position);

        // Bind ride details to view elements
        holder.txtType.setText("Type: " + ride.rideType);
        holder.txtFromTo.setText("From: " + ride.from + " → To: " + ride.to);
        holder.txtDateTime.setText("Date & Time: " + ride.dateTime);
        holder.txtRider.setText("Rider: " + ride.riderName);
        holder.txtDriver.setText("Driver: " + ride.driverName);
        holder.txtRiderEmail.setText("Email: " + ride.riderEmail);
        holder.txtDriverEmail.setText("Email: " + ride.driverEmail);
    }

    /**
     * Returns the number of ride history entries.
     *
     * @return number of items in ride history list
     */
    @Override
    public int getItemCount() {
        return rideHistory.size();
    }

    /**
     * ViewHolder class that holds and initializes all views for a ride history item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtType, txtFromTo, txtDateTime, txtRider, txtDriver, txtRiderEmail, txtDriverEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtType = itemView.findViewById(R.id.txt_history_type);
            txtFromTo = itemView.findViewById(R.id.txt_history_from_to);
            txtDateTime = itemView.findViewById(R.id.txt_history_datetime);
            txtRider = itemView.findViewById(R.id.txt_history_rider_name);
            txtDriver = itemView.findViewById(R.id.txt_history_driver_name);
            txtRiderEmail = itemView.findViewById(R.id.txt_history_rider_email);
            txtDriverEmail = itemView.findViewById(R.id.txt_history_driver_email);
        }
    }
}
