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

/**
 * Fragment that displays all unaccepted ride posts (both requests and offers)
 * created by the currently logged-in user.
 */
public class UnacceptedPostedRidesFragment extends Fragment {

    private RecyclerView recyclerView;
    private UnacceptedRideAdapter adapter;
    private List<Ride> rideList;

    private DatabaseReference ref;
    private ValueEventListener listener;

    public UnacceptedPostedRidesFragment() {}

    /**
     * Inflates the layout, sets up RecyclerView and triggers data loading.
     *
     * @param inflater           LayoutInflater used to inflate the layout
     * @param container          Parent view
     * @param savedInstanceState Previous instance state (if any)
     * @return Root view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unaccepted_posted_rides, container, false);

        recyclerView = view.findViewById(R.id.recycler_unaccepted);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rideList = new ArrayList<>();
        adapter = new UnacceptedRideAdapter(getContext(), rideList);
        recyclerView.setAdapter(adapter);

        loadUnacceptedRides();

        return view;
    }

    /**
     * Loads unaccepted rides posted by the current user from both
     * "rideRequests" and "rideOffers" nodes in Firebase.
     */
    private void loadUnacceptedRides() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            if (getContext() != null)
                Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        ref = FirebaseDatabase.getInstance().getReference();

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rideList.clear();
                // Loop through both rideRequests and rideOffers to find unaccepted rides posted by this user
                for (String node : new String[]{"rideRequests", "rideOffers"}) {
                    DataSnapshot nodeSnapshot = snapshot.child(node);
                    for (DataSnapshot snap : nodeSnapshot.getChildren()) {
                        Ride ride = snap.getValue(Ride.class);
                        // Only include rides where the current user is the poster
                        if (ride != null && currentUser.getUid().equals(ride.posterId)) {
                            ride.rideId = snap.getKey();
                            rideList.add(ride);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load your posted rides", Toast.LENGTH_SHORT).show();
                }
                Log.e("UnacceptedRides", "Firebase error", error.toException());
            }
        };

        // Attach listener to the root reference to read both offer/request nodes
        ref.addValueEventListener(listener);
    }

    /**
     * Removes Firebase listener.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (ref != null && listener != null) {
            ref.removeEventListener(listener);
        }
    }
}
