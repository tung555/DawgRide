package edu.uga.cs.dawgride;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.Calendar;

public class EditRideActivity extends AppCompatActivity {

    private EditText editFrom, editTo;
    private TextView txtDate, txtTime;
    private Button btnSave;
    private String rideId, rideType;
    private DatabaseReference rideRef;

    private int year, month, day, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ride);

        editFrom = findViewById(R.id.edit_from);
        editTo = findViewById(R.id.edit_to);
        txtDate = findViewById(R.id.txt_date);
        txtTime = findViewById(R.id.txt_time);
        btnSave = findViewById(R.id.btn_save);

        rideId = getIntent().getStringExtra("rideId");
        rideType = getIntent().getStringExtra("rideType");
        if (rideId == null || rideType == null) {
            Toast.makeText(this, "Missing ride info", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rideRef = FirebaseDatabase.getInstance().getReference(
                rideType.equals("offer") ? "rideOffers" : "rideRequests"
        ).child(rideId);

        loadRideInfo();

        txtDate.setOnClickListener(v -> showDatePicker());
        txtTime.setOnClickListener(v -> showTimePicker());

        btnSave.setOnClickListener(v -> saveRideChanges());
    }

    private void loadRideInfo() {
        rideRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Ride ride = snapshot.getValue(Ride.class);
                if (ride != null) {
                    editFrom.setText(ride.from);
                    editTo.setText(ride.to);
                    txtDate.setText(ride.dateTime.split(" ")[0]); // crude split, improve as needed
                    txtTime.setText(ride.dateTime.split(" ", 2)[1]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditRideActivity.this, "Failed to load ride", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveRideChanges() {
        String from = editFrom.getText().toString();
        String to = editTo.getText().toString();
        String date = txtDate.getText().toString();
        String time = txtTime.getText().toString();

        if (from.isEmpty() || to.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateTime = date + " " + time;

        rideRef.child("from").setValue(from);
        rideRef.child("to").setValue(to);
        rideRef.child("dateTime").setValue(dateTime)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ride updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    txtDate.setText(year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int displayHour = hourOfDay % 12;
                    if (displayHour == 0) displayHour = 12;
                    txtTime.setText(String.format("%02d:%02d %s", displayHour, minute1, amPm));
                }, hour, minute, false);
        timePickerDialog.show();
    }
}
