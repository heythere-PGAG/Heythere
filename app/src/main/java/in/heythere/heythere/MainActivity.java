package in.heythere.heythere;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import in.heythere.heythere.fragments.Events;
import in.heythere.heythere.fragments.Explore;
import in.heythere.heythere.fragments.Me;
import in.heythere.heythere.fragments.Whatsup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        final TabLayout tab = (TabLayout)findViewById(R.id.tab);

        assert tab != null;
        assert mViewPager != null;
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tab.setupWithViewPager(mViewPager);

/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }

     class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:return Events.newInstance();
                case 1:return Explore.newInstance(position);
                case 2:return Whatsup.newInstance(position);
                case 3:return Me.newInstance();
            }

            return Events.newInstance();
        }

        @Override
        public int getCount() {

            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "EVENTS";
                case 1:
                    return "EXPLORE";
                case 2:
                    return "WHATS'UP";
                case 3:
                    return "ME";
            }
            return "EVENTS";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
