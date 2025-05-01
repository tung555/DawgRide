package edu.uga.cs.dawgride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Fragment that allows users to post either a ride offer or a ride request.
 * Users must input the origin, destination, and select date/time before submitting.
 */
public class PostFragment extends Fragment {

    private EditText inputFrom, inputTo;
    private RadioGroup radioGroupType;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView toggleDatePicker, toggleTimePicker;
    private Button btnSubmit;

    /**
     * Inflates the post layout and sets up UI components and listeners.
     *
     * @param inflater           layout inflater
     * @param container          view container
     * @param savedInstanceState saved state
     * @return the root view of this fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        radioGroupType = view.findViewById(R.id.radioGroupType);
        inputFrom = view.findViewById(R.id.inputFrom);
        inputTo = view.findViewById(R.id.inputTo);
        datePicker = view.findViewById(R.id.datePicker);
        timePicker = view.findViewById(R.id.timePicker);
        toggleDatePicker = view.findViewById(R.id.toggleDatePicker);
        toggleTimePicker = view.findViewById(R.id.toggleTimePicker);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Hide pickers by default
        datePicker.setVisibility(View.GONE);
        timePicker.setVisibility(View.GONE);

        // Toggle date picker visibility
        toggleDatePicker.setOnClickListener(v -> {
            if (datePicker.getVisibility() == View.VISIBLE) {
                datePicker.setVisibility(View.GONE);
                toggleDatePicker.setText("\uD83D\uDCC5 Select Date");
            } else {
                datePicker.setVisibility(View.VISIBLE);
                toggleDatePicker.setText("\uD83D\uDCC5 Hide Date Picker");
            }
        });

        // Toggle time picker visibility
        toggleTimePicker.setOnClickListener(v -> {
            if (timePicker.getVisibility() == View.VISIBLE) {
                timePicker.setVisibility(View.GONE);
                toggleTimePicker.setText("\u23F0 Select Time");
            } else {
                timePicker.setVisibility(View.VISIBLE);
                toggleTimePicker.setText("\u23F0 Hide Time Picker");
            }
        });

        btnSubmit.setOnClickListener(v -> submitPost());

        return view;
    }

    /**
     * Gathers form data and submits a ride offer/request to Firebase.
     * Performs validation and formats data before submission.
     */
    private void submitPost() {
        int selectedId = radioGroupType.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(getContext(), "Please select a post type", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadio = getView().findViewById(selectedId);
        String type = selectedRadio.getText().toString().toLowerCase(Locale.ROOT);
        String from = inputFrom.getText().toString().trim();
        String to = inputTo.getText().toString().trim();

        if (from.isEmpty() || to.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both From and To locations", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected date and time
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);

        // Format date and time string
        String dateTime = String.format(Locale.getDefault(), "%d-%02d-%02d %02d:%02d",
                year, month + 1, day, hour, minute);

        // Data to push to Firebase
        HashMap<String, Object> post = new HashMap<>();
        post.put("rideType", type);
        post.put("from", from);
        post.put("to", to);
        post.put("dateTime", dateTime);
        post.put("posterId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Choose the correct node to push to
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String path = type.equals("request") ? "rideRequests" : "rideOffers";
        ref.child(path).push().setValue(post)
                .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Post submitted!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to submit post", Toast.LENGTH_SHORT).show());
    }
}
