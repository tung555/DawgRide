package edu.uga.cs.dawgride;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UnacceptedRideAdapter extends RecyclerView.Adapter<UnacceptedRideAdapter.ViewHolder> {

    private Context context;
    private List<Ride> unacceptedRides;

    public UnacceptedRideAdapter(Context context, List<Ride> unacceptedRides) {
        this.context = context;
        this.unacceptedRides = unacceptedRides;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_unaccepted_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride ride = unacceptedRides.get(position);

        if (ride.rideType.equals("offer")) {
            holder.txtRideType.setText("Offer");
        } else {
            holder.txtRideType.setText("Request");
        }
        holder.txtFromTo.setText(ride.from + " → " + ride.to);
        holder.txtDateTime.setText(ride.dateTime);

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditRideActivity.class);
            intent.putExtra("rideId", ride.rideId);
            intent.putExtra("rideType", ride.rideType);
            intent.putExtra("from", ride.from);
            intent.putExtra("to", ride.to);
            intent.putExtra("dateTime", ride.dateTime);
            context.startActivity(intent);
        });


        holder.btnDelete.setOnClickListener(v -> {
            String rideId = ride.rideId;
            String rideType = ride.rideType;

            String ridePath = rideType.equals("offer") ? "rideOffers" : "rideRequests";
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(ridePath).child(rideId);

            ref.removeValue().addOnSuccessListener(unused -> {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < unacceptedRides.size()) {
                    unacceptedRides.remove(position);
                    //notifyItemRemoved(currentPosition);
                }
                Toast.makeText(context, "Ride deleted", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to delete ride", Toast.LENGTH_SHORT).show();
            });
        });

    }

    @Override
    public int getItemCount() {
        return unacceptedRides.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFromTo, txtDateTime, txtRideType;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRideType = itemView.findViewById(R.id.txt_type);
            txtFromTo = itemView.findViewById(R.id.txt_from_to);
            txtDateTime = itemView.findViewById(R.id.txt_datetime);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}