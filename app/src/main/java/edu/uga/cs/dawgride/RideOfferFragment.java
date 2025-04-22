package edu.uga.cs.dawgride;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class RideOfferFragment extends Fragment {
    private RecyclerView recyclerView;
    private RideAdapter adapter;
    private List<Ride> rideList;

    public RideOfferFragment() {}

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
        loadRideOffers();

        return view;
    }

    private void loadRideOffers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rideOffers");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rideList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Ride req = snap.getValue(Ride.class);
                    if (req != null) {
                        req.rideId = snap.getKey();
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
