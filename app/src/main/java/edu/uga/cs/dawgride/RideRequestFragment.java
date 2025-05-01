package edu.uga.cs.dawgride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.util.Log;

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

/**
 * Fragment that displays a list of ride requests posted by riders.
 * Retrieves data in real-time from Firebase Realtime Database.
 */
public class RideRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private RideAdapter adapter;
    private List<Ride> rideList;
    private DatabaseReference rideRef;
    private ValueEventListener rideListener;

    public RideRequestFragment() {}

    /**
     * Initializes the view with a RecyclerView to list ride requests.
     *
     * @param inflater LayoutInflater object
     * @param container ViewGroup container
     * @param savedInstanceState Previous saved state
     * @return Inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_request, container, false);

        recyclerView = view.findViewById(R.id.rideRequestRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rideList = new ArrayList<>();
        adapter = new RideAdapter(getContext(), rideList);
        recyclerView.setAdapter(adapter);

        loadRideRequests();

        return view;
    }

    /**
     * Loads ride requests from the "rideRequests" node in Firebase.
     * Sets up a listener to update the list in real time.
     */
    private void loadRideRequests() {
        rideRef = FirebaseDatabase.getInstance().getReference("rideRequests");

        rideListener = new ValueEventListener() {
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
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load ride requests", Toast.LENGTH_SHORT).show();
                }
                Log.e("RideHistory", "Firebase error", error.toException());
            }
        };

        rideRef.addValueEventListener(rideListener);
    }

    /**
     * Cleans up the Firebase event listener.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rideRef != null && rideListener != null) {
            rideRef.removeEventListener(rideListener);
        }
    }
}
