package in.heythere.heythere.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import in.heythere.heythere.Detailed;
import in.heythere.heythere.EventsMap;
import in.heythere.heythere.Eventslist;
import in.heythere.heythere.R;
import in.heythere.heythere.SearchEvent;
import in.heythere.heythere.Utilities.MySingleton;
import in.heythere.heythere.Utilities.Tools;
import in.heythere.heythere.adapters.SmallAdapter;

public class Events extends Fragment implements View.OnClickListener {

    TextView[] dots;
    LinearLayout dotsLayout;

    public static Events newInstance() {
        return new Events();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        dotsLayout = (LinearLayout)rootView.findViewById(R.id.layoutDots);

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        RecyclerView recent = (RecyclerView)rootView.findViewById(R.id.recent);
        RecyclerView nearby = (RecyclerView)rootView.findViewById(R.id.nearby);
        RecyclerView.LayoutManager recentmanager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        RecyclerView.LayoutManager nearbymanager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        recent.setLayoutManager(recentmanager);
        recent.setFocusable(false);
        nearby.setLayoutManager(nearbymanager);

        Uri uri = Tools.CONTENT_URI;
        Cursor array = getActivity().getContentResolver().query(uri,null,"strftime('%W',"+Tools.TABLE_NAME+"."+Tools.EVENT_DATE+") = strftime('%W','now')",null,null);
        assert array != null;
        recent.setAdapter(new SmallAdapter(getActivity(),array));

        Uri uri1 = Tools.CONTENT_URI;
        Cursor array1 = getActivity().getContentResolver().query(uri1,null,null,null,Tools.LIKE_COUNT+" DESC");
        assert array1 != null;
        nearby.setAdapter(new SmallAdapter(getActivity(),array1));

        rootView.findViewById(R.id.all).setOnClickListener(this);
        rootView.findViewById(R.id.business).setOnClickListener(this);
        rootView.findViewById(R.id.sports).setOnClickListener(this);
        rootView.findViewById(R.id.festival).setOnClickListener(this);
        rootView.findViewById(R.id.parties).setOnClickListener(this);
        rootView.findViewById(R.id.college).setOnClickListener(this);
        rootView.findViewById(R.id.map).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search){
            startActivity(new Intent(getActivity(), SearchEvent.class));
        }
        return super.onOptionsItemSelected(item);
    }

    void addDots(int current){

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getActivity());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.WHITE);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[current].setTextColor(Color.RED);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.all:
                startEvents("All");
                break;
            case R.id.business:
                startEvents("Business");
                break;
            case R.id.college:
                startEvents("College");
                break;
            case R.id.parties:
                startEvents("Parties");
                break;
            case R.id.sports:
                startEvents("Sports");
                break;
            case R.id.festival:
                startEvents("Festival");
                break;
            case R.id.map:
                startActivity(new Intent(getActivity(),EventsMap.class));
                break;
            default:break;
        }
    }

    public void startEvents(String category){
        Intent intent = new Intent(getActivity(),Eventslist.class);
        intent.putExtra("category",category);
        startActivity(intent);
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private Cursor array;

        MyViewPagerAdapter() {
            Uri uri = Tools.CONTENT_URI;
            array = getActivity().getContentResolver().query(uri,null,null,null,Tools.LIKE_COUNT+" DESC LIMIT 5");
            assert array != null;
            array.moveToFirst();
            dots = new TextView[array.getCount()];
            addDots(0);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.slider_view, container, false);
            TextView name = (TextView)view.findViewById(R.id.name);
            ImageView poster = (ImageView)view.findViewById(R.id.poster);

            view.findViewById(R.id.slider).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        startActivity(new Intent(getActivity(),Detailed.class).putExtra("e_id",array.getString(0)));
                }
            });
            ImageLoader mImageLoader = MySingleton.getInstance(getActivity()).getImageLoader();
            mImageLoader.get(Tools.HOME_URL + array.getString(6),
                    ImageLoader.getImageListener(poster, R.drawable.crowd, R.drawable.crowd));
            name.setText(array.getString(1));

            array.moveToNext();
            container.addView(view);

            return view;

        }

        @Override
        public int getCount() {
           return array.getCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}