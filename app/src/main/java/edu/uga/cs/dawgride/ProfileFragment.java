package edu.uga.cs.dawgride;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Fragment that displays user profile information including greeting, ride points,
 * and provides options to view ride history or log out.
 */
public class ProfileFragment extends Fragment {

    private TextView greetingTextView;
    private TextView pointsTextView;
    private Button historyButton;
    private Button logoutButton;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;
    private ValueEventListener userListener;

    public ProfileFragment() {}

    /**
     * Initializes the profile UI and fetches user data from Firebase.
     *
     * @param inflater The LayoutInflater object to inflate views
     * @param container The parent view that the fragment's UI should attach to
     * @param savedInstanceState Bundle for saved state
     * @return The fully initialized view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        greetingTextView = view.findViewById(R.id.txtGreeting);
        pointsTextView = view.findViewById(R.id.txtRidePoints);
        historyButton = view.findViewById(R.id.btnViewHistory);
        logoutButton = view.findViewById(R.id.btnLogout);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

            // Load user profile data once logged in
            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.child("username").getValue(String.class);
                    Integer points = snapshot.child("ridePoints").getValue(Integer.class);

                    greetingTextView.setText("Hello, " + (username != null ? username : "User"));
                    pointsTextView.setText("Ride Points: " + (points != null ? points : 0));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (getContext() != null)
                        Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            };

            userRef.addValueEventListener(userListener);
        }

        // Navigate to ride history
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RideHistoryActivity.class);
            userRef.removeEventListener(userListener);
            startActivity(intent);
        });

        // Log out the current user
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            if (getContext() != null)
                Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    /**
     * Cleans up the Firebase listener when the fragment view is destroyed
     * to prevent memory leaks or redundant callbacks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
        }
    }
}
