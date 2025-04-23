package edu.uga.cs.dawgride;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AcceptedRidesFragment();
            case 1:
                return new UnacceptedPostedRidesFragment();
            default:
                return new AcceptedRidesFragment(); // Fallback
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
