package it.neptis.gopoleis.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.neptis.gopoleis.fragments.RankingFragment;


public class RankingCollectionPagerAdapter extends FragmentStatePagerAdapter {

    public RankingCollectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new RankingFragment();
        Bundle args = new Bundle();
        args.putInt("number", i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = "Generale";
                break;
            case 1:
                title = "Carte";
                break;
            case 2:
                title = "Medaglie";
                break;
            case 3:
                title = "Percorsi";
                break;
        }
        return title;
    }

}