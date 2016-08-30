package in.heythere.heythere;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import in.heythere.heythere.fragments.Events;
import in.heythere.heythere.fragments.Explore;
import in.heythere.heythere.fragments.Me;
import in.heythere.heythere.fragments.Whatsup;

public class MainActivity extends AppCompatActivity {

    FABToolbarLayout layout;
    View layer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppCompatSpinner cities = (AppCompatSpinner)toolbar.findViewById(R.id.city);
        cities.setAdapter(new ArrayAdapter(this,R.layout.city_menu_dropdown,getResources().getStringArray(R.array.cities)));

        final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        final TabLayout tab = (TabLayout)findViewById(R.id.tab);

        assert tab != null;
        assert mViewPager != null;
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tab.setupWithViewPager(mViewPager);

        layer = findViewById(R.id.back_dark);

        layer.getLayoutParams().height = getWindowManager().getDefaultDisplay().getHeight();
        layer.getLayoutParams().width = getWindowManager().getDefaultDisplay().getWidth();

        layout = (FABToolbarLayout)findViewById(R.id.fabtoolbar);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fabtoolbar_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.show();
                layer.setVisibility(View.VISIBLE);
            }
        });

        layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layer.setVisibility(View.GONE);
                layout.hide();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!layout.isFab()){
            layer.setVisibility(View.GONE);
            layout.hide();
        }else {
            super.onBackPressed();
        }
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
                    return "TALK";
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
