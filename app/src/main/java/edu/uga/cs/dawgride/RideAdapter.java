package edu.uga.cs.dawgride;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        Ride request = rides.get(position);

        // Set the from → to text
        holder.fromTo.setText(request.from + " → " + request.to);

        // Defensive null check for dateTime
        if (request.dateTime != null && !request.dateTime.trim().isEmpty()) {
            holder.dateTime.setText(request.dateTime);
        } else {
            holder.dateTime.setText("No date/time set");
        }
    }


    @Override
    public int getItemCount() {
        return rides.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fromTo, dateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fromTo = itemView.findViewById(R.id.txt_from_to);
            dateTime = itemView.findViewById(R.id.txt_datetime);
        }
    }
}
