package edu.uga.cs.dawgride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that manages two tabs: one for accepted rides and another for unaccepted posted rides.
 */
public class AcceptedTabFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public AcceptedTabFragment() {}

    /**
     * Inflates the layout for this fragment and sets up the tab layout and view pager.
     *
     * @param inflater           LayoutInflater used to inflate the layout.
     * @param container          Parent view that the fragment's UI should attach to.
     * @param savedInstanceState Previously saved state if available.
     * @return The inflated view for the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accepted_tabs, container, false);

        viewPager = view.findViewById(R.id.accepted_view_pager);
        tabLayout = view.findViewById(R.id.accepted_tab_layout);

        // Create and attach tab fragments
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new AcceptedRidesFragment());
        fragmentList.add(new UnacceptedPostedRidesFragment());

        // Tab titles
        List<String> tabTitles = new ArrayList<>();
        tabTitles.add("Accepted Rides");
        tabTitles.add("Unaccepted Posts");

        // Set the adapter (must be customized to handle the fragment list)
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Link tabs and view pager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles.get(position));
        }).attach();

        return view;
    }
}