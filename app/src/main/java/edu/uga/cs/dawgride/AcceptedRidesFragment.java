package edu.uga.cs.dawgride;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AcceptedRidesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AcceptedRideAdapter adapter;
    private List<AcceptedRide> acceptedRides;

    private DatabaseReference ref;
    private ValueEventListener listener;

    public AcceptedRidesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accepted_rides, container, false);

        recyclerView = view.findViewById(R.id.acceptedRidesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        acceptedRides = new ArrayList<>();
        adapter = new AcceptedRideAdapter(getContext(), acceptedRides);
        recyclerView.setAdapter(adapter);

        loadAcceptedRides();

        return view;
    }

    private void loadAcceptedRides() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            if (getContext() != null)
                Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid())
                .child("acceptedRides");

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                acceptedRides.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    AcceptedRide ride = snap.getValue(AcceptedRide.class);
                    if (ride != null) {
                        ride.rideId = snap.getKey();
                        acceptedRides.add(ride);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load accepted rides", Toast.LENGTH_SHORT).show();
                }
                Log.e("AcceptedRides", "Firebase error", error.toException());
            }
        };

        ref.addValueEventListener(listener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ref != null && listener != null) {
            ref.removeEventListener(listener);
        }
    }
}
