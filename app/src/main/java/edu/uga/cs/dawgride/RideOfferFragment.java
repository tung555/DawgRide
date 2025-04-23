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

    // Firebase references for cleanup
    private DatabaseReference offerRef;
    private ValueEventListener offerListener;

    public RideOfferFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_request, container, false); // Consider renaming layout for clarity

        recyclerView = view.findViewById(R.id.rideRequestRecycler); // Same ID used for both request/offer list
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rideList = new ArrayList<>();
        adapter = new RideAdapter(getContext(), rideList);
        recyclerView.setAdapter(adapter);

        loadRideOffers();

        return view;
    }

    private void loadRideOffers() {
        offerRef = FirebaseDatabase.getInstance().getReference("rideOffers");

        offerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rideList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Ride offer = snap.getValue(Ride.class);
                    if (offer != null) {
                        offer.rideId = snap.getKey();
                        rideList.add(offer);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load ride offers", Toast.LENGTH_SHORT).show();
                }
                Log.e("RideOffer", "Firebase error", error.toException());
            }
        };

        offerRef.addValueEventListener(offerListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (offerRef != null && offerListener != null) {
            offerRef.removeEventListener(offerListener);
        }
    }
}
