package edu.uga.cs.dawgride;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.ViewHolder> {

    private Context context;
    private List<Ride> rides;

    public RideAdapter(Context context, List<Ride> rides) {
        this.context = context;
        this.rides = rides;
    }

    @NonNull
    @Override
    public RideAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideAdapter.ViewHolder holder, int position) {
        Ride ride = rides.get(position);

        holder.fromTo.setText(ride.from + " → " + ride.to);

        if (ride.dateTime != null && !ride.dateTime.trim().isEmpty()) {
            holder.dateTime.setText(ride.dateTime);
        } else {
            holder.dateTime.setText("No date/time set");
        }

        holder.acceptButton.setOnClickListener(v -> {
            String rideId = rides.get(holder.getAdapterPosition()).rideId; // Make sure you save the key in the Ride model
            acceptRide(rides.get(holder.getAdapterPosition()), rideId, ride.rideType);
        });
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    private void acceptRide(Ride ride, String rideId, String rideType) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String accepterId = currentUser.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        // Get the accepter's info from Firebase
        DatabaseReference usersRef = dbRef.child("users");

        usersRef.child(accepterId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot accepterSnapshot) {
                String accepterName = accepterSnapshot.child("username").getValue(String.class);
                String accepterEmail = accepterSnapshot.child("email").getValue(String.class);

                usersRef.child(ride.posterId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot posterSnapshot) {
                        String posterName = posterSnapshot.child("username").getValue(String.class);
                        String posterEmail = posterSnapshot.child("email").getValue(String.class);

                        AcceptedRide acceptedRide = new AcceptedRide();
                        acceptedRide.rideId = rideId;
                        acceptedRide.rideType = rideType;
                        acceptedRide.from = ride.from;
                        acceptedRide.to = ride.to;
                        acceptedRide.dateTime = ride.dateTime;
                        acceptedRide.acceptorId = accepterId;

                        // Set driver/rider info depending on ride type
                        if ("offer".equals(rideType)) {
                            // Poster is driver, accepter is rider
                            acceptedRide.driverName = posterName;
                            acceptedRide.driverEmail = posterEmail;
                            acceptedRide.riderName = accepterName;
                            acceptedRide.riderEmail = accepterEmail;
                        } else {
                            // Poster is rider, accepter is driver
                            acceptedRide.driverName = accepterName;
                            acceptedRide.driverEmail = accepterEmail;
                            acceptedRide.riderName = posterName;
                            acceptedRide.riderEmail = posterEmail;
                        }

                        // Save under accepter's acceptedRides
                        dbRef.child("users").child(accepterId)
                                .child("acceptedRides").child(rideId)
                                .setValue(acceptedRide);

                        // Save under poster's acceptedRides
                        dbRef.child("users").child(ride.posterId)
                                .child("acceptedRides").child(rideId)
                                .setValue(acceptedRide);

                        // Remove from original ride list
                        String rideNode = rideType.equals("offer") ? "rideOffers" : "rideRequests";
                        dbRef.child(rideNode).child(rideId)
                                .removeValue()
                                .addOnSuccessListener(v -> {
                                    Toast.makeText(context, "Ride accepted!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to remove ride", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed to load poster info", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to load your info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fromTo, dateTime;
        Button acceptButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fromTo = itemView.findViewById(R.id.txt_from_to);
            dateTime = itemView.findViewById(R.id.txt_datetime);
            acceptButton = itemView.findViewById(R.id.btnAccept);
        }
    }
}
