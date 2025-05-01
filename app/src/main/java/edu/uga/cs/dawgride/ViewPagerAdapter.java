package edu.uga.cs.dawgride;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * ViewPager adapter that manages two fragments:
 * AcceptedRidesFragment and UnacceptedPostedRidesFragment.
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    /**
     * Constructs a ViewPagerAdapter using the given parent fragment.
     *
     * @param fragment the parent fragment that hosts this adapter
     */
    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    /**
     * Returns the fragment corresponding to the selected tab position.
     *
     * @param position the position of the selected tab
     * @return the corresponding Fragment instance
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AcceptedRidesFragment();
            case 1:
                return new UnacceptedPostedRidesFragment();
            default:
                return new AcceptedRidesFragment();
        }
    }

    /**
     * Returns the number of fragments (tabs) managed by the adapter.
     *
     * @return total number of pages
     */
    @Override
    public int getItemCount() {
        return 2;
    }
}
