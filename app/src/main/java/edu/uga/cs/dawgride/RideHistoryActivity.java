package edu.uga.cs.dawgride;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class RideHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RideHistoryAdapter adapter;
    private List<AcceptedRide> historyList;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_history);

        recyclerView = findViewById(R.id.recycler_ride_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        adapter = new RideHistoryAdapter(this, historyList);
        recyclerView.setAdapter(adapter);

        btnBack = findViewById(R.id.btn_back_to_profile);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RideHistoryActivity.this, MainActivity.class);
            intent.putExtra("loadFragment", "profile");
            startActivity(intent);
            finish();
        });

        loadRideHistory();
    }

    private void loadRideHistory() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("rideHistory");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    AcceptedRide ride = snap.getValue(AcceptedRide.class);
                    if (ride != null) {
                        historyList.add(ride);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!RideHistoryActivity.this.isFinishing()) {
                    Toast.makeText(RideHistoryActivity.this, "Failed to load history", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
