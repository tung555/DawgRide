package edu.uga.cs.dawgride;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView greetingTextView;
    private TextView pointsTextView;
    private Button historyButton;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        greetingTextView = view.findViewById(R.id.txtGreeting);
        pointsTextView = view.findViewById(R.id.txtRidePoints);
        historyButton = view.findViewById(R.id.btnViewHistory);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.child("username").getValue(String.class);
                    Integer points = snapshot.child("ridePoints").getValue(Integer.class);

                    greetingTextView.setText("Hello, " + username);
                    pointsTextView.setText("Ride Points: " + (points != null ? points : 0));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    greetingTextView.setText("Failed to load profile");
                }
            });
        }

        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RideHistoryActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
