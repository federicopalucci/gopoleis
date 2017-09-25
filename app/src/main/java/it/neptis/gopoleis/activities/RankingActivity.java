package it.neptis.gopoleis.activities;

import android.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.adapters.RankingCollectionPagerAdapter;
import it.neptis.gopoleis.fragments.RankingFragment;

public class RankingActivity extends AppCompatActivity {

    private static final String TAG = "RankingActivity";
    // private static final int NUMBER_OF_LISTS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        /*
        RankingCollectionPagerAdapter rankingCollectionPagerAdapter = new RankingCollectionPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(rankingCollectionPagerAdapter);
        */
/*
        Fragment newFragment = new RankingFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.ranking_fragment, newFragment).commit();
        */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.rankings);
    }

}