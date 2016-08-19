package in.heythere.heythere;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.heythere.heythere.adapters.EventsListAdapter;

public class NearbyMap extends Fragment implements OnMapReadyCallback, ClusterManager.OnClusterItemClickListener<NearbyMap.Person>, View.OnClickListener, ClusterManager.OnClusterClickListener<NearbyMap.Person> {

    private GoogleMap mMap;
    private ClusterManager<NearbyMap.Person> mClusterManager;
    Animation slide_up;
    Animation slide_down;
    LinearLayout list;
    RecyclerView map_items;
    TextView count;
    View root;
    MapView mapView;
    static double lat, lon;
    static JSONArray response = new JSONArray();

    public NearbyMap() {
        // Required empty public constructor
    }

    public static NearbyMap newInstance(JSONArray response1,double lat1,double lon1) {
        response = response1;
        lat = lat1;
        lon = lon1;
        return new NearbyMap();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_nearby_map, container, false);
        MapsInitializer.initialize(this.getActivity());
        mapView = (MapView) root.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        slide_up = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_up);

        slide_down = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_down);

        count = (TextView)root.findViewById(R.id.count);

        list = (LinearLayout) root.findViewById(R.id.content_view);

        map_items = (RecyclerView)root.findViewById(R.id.map_list);

        RecyclerView.LayoutManager recentmanager = new LinearLayoutManager(getActivity());

        map_items.setLayoutManager(recentmanager);

        root.findViewById(R.id.close).setOnClickListener(this);

        return root;
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (mMap != null) {
            return;
        }
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mClusterManager = new ClusterManager<>(getActivity(),mMap);
        mClusterManager.setRenderer(new NearbyMap.PersonRenderer());
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterClickListener(this);
        mMap.setOnCameraChangeListener(mClusterManager);
        try {
            for (int i=0;i<response.length();i++) {
                JSONObject object = response.getJSONObject(i);
                LatLng latLng = new LatLng(object.optDouble("lat"), object.optDouble("lon"));
                Person person = new Person(latLng, object);
                mClusterManager.addItem(person);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), 12));
        mClusterManager.cluster();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.close) {
            mMap.setPadding(0,0,0,0);
            list.startAnimation(slide_down);
            list.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onClusterClick(final Cluster<Person> cluster) {
        count.setText("");
        JSONArray array = new JSONArray();
        RecyclerView.Adapter adapter = new EventsListAdapter(getActivity(),array,R.layout.map_evnets_list);
        for (NearbyMap.Person p : cluster.getItems()) {
            array.put(p.object);
        }
        map_items.setAdapter(adapter);
        count.setText(String.valueOf(array.length())+" EVENTS");
        if (list.getVisibility() != View.VISIBLE) {
            list.startAnimation(slide_up);
            list.setVisibility(View.VISIBLE);
        }else{
            mMap.animateCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()));
        }

        slide_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                View view = root.findViewById(R.id.map_view);
                mMap.setPadding(0,0,0,list.getHeight()-view.getHeight());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(cluster.getPosition()));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return true;
    }

    @Override
    public boolean onClusterItemClick(final Person person) {

        count.setText("");

        JSONArray array = new JSONArray();
        array.put(person.object);
        RecyclerView.Adapter adapter = new EventsListAdapter(getActivity(),array,R.layout.map_evnets_list);
        map_items.setAdapter(adapter);
        if (list.getVisibility() != View.VISIBLE) {
            list.startAnimation(slide_up);
            list.setVisibility(View.VISIBLE);
        }else {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(person.getPosition()));
        }
        slide_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                View view = root.findViewById(R.id.map_view);
                mMap.setPadding(0,0,0,list.getHeight()-view.getHeight());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(person.getPosition()));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return true;
    }

    private class PersonRenderer extends DefaultClusterRenderer<Person> {

        private final IconGenerator mIconGenerator = new IconGenerator(getActivity());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity());
        private final ImageView mImageView;
        private TextView mTextView,mTextViewsingle;

        PersonRenderer() {
            super(getActivity(), mMap, mClusterManager);
            View multiProfile = getActivity().getLayoutInflater().inflate(R.layout.multi_profile, null);
            View single = getActivity().getLayoutInflater().inflate(R.layout.marker, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mTextView = (TextView)multiProfile.findViewById(R.id.amu_text);
            mTextViewsingle = (TextView)single.findViewById(R.id.amu_text);
            mImageView = (ImageView)single.findViewById(R.id.type);
            mIconGenerator.setContentView(single);
        }

        @Override
        protected void onBeforeClusterItemRendered(Person person, MarkerOptions markerOptions) {

            // Draw a single person.
            // Set the info window to show their name.
            JSONObject object = person.object;
            switch (object.optString("event_category")){
                case "Business":
                    mImageView.setImageResource(R.drawable.white_business);
                    mIconGenerator.setColor(getResources().getColor(R.color.business));
                    break;
                case "Parties":
                    mImageView.setImageResource(R.drawable.white_parties);
                    mIconGenerator.setColor(getResources().getColor(R.color.parties));
                    break;
                case "College":
                    mImageView.setImageResource(R.drawable.white_college);
                    mIconGenerator.setColor(getResources().getColor(R.color.college));
                    break;
                case "Sports":
                    mImageView.setImageResource(R.drawable.white_sports);
                    mIconGenerator.setColor(getResources().getColor(R.color.sports));
                    break;
                case "Festival":
                    mImageView.setImageResource(R.drawable.white_festival);
                    mIconGenerator.setColor(getResources().getColor(R.color.festival));
                    break;
                default:
                    break;
            }
            mTextViewsingle.setText(object.optString("likecount"));
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Person> cluster, MarkerOptions markerOptions) {
            mTextView.setText(String.valueOf(cluster.getSize()));
            mClusterIconGenerator.setColor(getResources().getColor(R.color.colorPrimary));
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    public class Person implements ClusterItem {
        final JSONObject object;
        private final LatLng mPosition;

        Person(LatLng position,JSONObject object) {
            this.object = object;
            mPosition = position;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }
    }
}
