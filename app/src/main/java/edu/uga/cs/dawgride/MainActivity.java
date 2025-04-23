package edu.uga.cs.dawgride;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat.Type;
import androidx.core.graphics.Insets;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Load initial fragment, based on intent
        if (savedInstanceState == null) {
            String fragmentToLoad = getIntent().getStringExtra("loadFragment");
            Fragment initialFragment;

            if ("profile".equals(fragmentToLoad)) {
                initialFragment = new ProfileFragment();
                bottomNavigationView = findViewById(R.id.bottom_nav);
                bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            } else {
                initialFragment = new RideRequestFragment(); // Default
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, initialFragment)
                    .commit();
        }

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();

            if (id == R.id.nav_requests) {
                selected = new RideRequestFragment();
            } else if (id == R.id.nav_offers) {
                selected = new RideOfferFragment();
            } else if (id == R.id.nav_post) {
                selected = new PostFragment();
            } else if (id == R.id.nav_accepted) {
                selected = new AcceptedTabFragment();
            } else {
                selected = new ProfileFragment();
            }

            if (selected != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, selected)
                        .commit();
                return true;
            }

            return false;
        });

        userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "User record not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Could not fetch user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
