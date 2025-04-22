package edu.uga.cs.dawgride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class RideRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private RideAdapter adapter;
    private List<Ride> rideList;

    public RideRequestFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_request, container, false);

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.rideRequestRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rideList = new ArrayList<>();
        adapter = new RideAdapter(getContext(), rideList);
        recyclerView.setAdapter(adapter);

        // Load ride requests from Firebase
        loadRideRequests();

        return view;
    }

    private void loadRideRequests() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rideRequests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rideList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Ride req = snap.getValue(Ride.class);
                    if (req != null) {
                        Log.d("RideDebug", "from: " + req.from + ", to: " + req.to + ", dateTime: " + req.dateTime);
                        req.rideId = snap.getKey(); // Store the unique key if needed
                        rideList.add(req);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride requests", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
