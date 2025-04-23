package edu.uga.cs.dawgride;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.List;

public class AcceptedRideAdapter extends RecyclerView.Adapter<AcceptedRideAdapter.ViewHolder> {

    private Context context;
    private List<AcceptedRide> acceptedRides;
    private String currentUserId;

    public AcceptedRideAdapter(Context context, List<AcceptedRide> acceptedRides) {
        this.context = context;
        this.acceptedRides = acceptedRides;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public AcceptedRideAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_accepted_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AcceptedRideAdapter.ViewHolder holder, int position) {
        AcceptedRide ride = acceptedRides.get(position);

        holder.txtType.setText(ride.rideType.equals("offer") ? "Offer" : "Request");
        holder.txtFromTo.setText(ride.from + " → " + ride.to);
        holder.txtDateTime.setText(ride.dateTime != null ? ride.dateTime : "No date/time");

        // Determine which user info to show
        if (currentUserId.equals(ride.acceptorId)) {
            // I'm the accepter, show the poster's info
            if (ride.rideType.equals("offer")) {
                holder.txtUserName.setText("Driver: " + ride.driverName);
                holder.txtUserEmail.setText("Email: " + ride.driverEmail);
            } else {
                holder.txtUserName.setText("Rider: " + ride.riderName);
                holder.txtUserEmail.setText("Email: " + ride.riderEmail);
            }
        } else {
            // I'm the poster, show the accepter's info
            if (ride.rideType.equals("offer")) {
                holder.txtUserName.setText("Rider: " + ride.riderName);
                holder.txtUserEmail.setText("Email: " + ride.riderEmail);
            } else {
                holder.txtUserName.setText("Driver: " + ride.driverName);
                holder.txtUserEmail.setText("Email: " + ride.driverEmail);
            }
        }

        holder.btnComplete.setOnClickListener(v -> {
            completeRide(ride, position);
        });
    }

    @Override
    public int getItemCount() {
        return acceptedRides.size();
    }

    private void completeRide(AcceptedRide ride, int position) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // Remove from current user's acceptedRides
        dbRef.child("users").child(currentUserId).child("acceptedRides").child(ride.rideId).removeValue();

        // Add to rideHistory for both driver and rider
        dbRef.child("users").child(currentUserId).child("rideHistory").child(ride.rideId).setValue(ride);

        // Update only one user’s points
        boolean isOffer = "offer".equalsIgnoreCase(ride.rideType);

        if (isOffer) {
            if (currentUserId.equals(ride.acceptorId)) {
                // Driver clicked Complete, deduct 50 points
                adjustPoints(dbRef, currentUserId, -50);
            } else {
                // Rider clicked Complete, add 50 points
                adjustPoints(dbRef, currentUserId, +50);
            }
        } else {
            // For request
            if (currentUserId.equals(ride.acceptorId)) {
                // Driver clicked Complete, add 50 points
                adjustPoints(dbRef, currentUserId, +50);
            } else {
                // Rider clicked Complete, deduct 50 points
                adjustPoints(dbRef, currentUserId, -50);
            }
        }

        // Remove from list
        acceptedRides.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, acceptedRides.size());
    }

    private void adjustPoints(DatabaseReference dbRef, String userId, int delta) {
        dbRef.child("users").child(userId).child("ridePoints").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer points = currentData.getValue(Integer.class);
                if (points == null) points = 0;

                points += delta;
                if (points < 0) points = 0;

                currentData.setValue(points);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {}
        });
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtType, txtFromTo, txtUserName, txtUserEmail, txtDateTime;
        Button btnComplete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtType = itemView.findViewById(R.id.txt_type);
            txtFromTo = itemView.findViewById(R.id.txt_from_to);
            txtUserName = itemView.findViewById(R.id.txt_user_name);
            txtUserEmail = itemView.findViewById(R.id.txt_user_email);
            txtDateTime = itemView.findViewById(R.id.txt_datetime);
            btnComplete = itemView.findViewById(R.id.btn_complete);
        }
    }
}