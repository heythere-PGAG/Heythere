package in.heythere.heythere;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import in.heythere.heythere.Utilities.MySingleton;
import in.heythere.heythere.Utilities.Tools;

public class EventsMap extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    FloatingActionButton fab;
    int type;

    protected GoogleApiClient mGoogleApiClient;

    protected Location mLastLocation;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    double lati, longi;
    String sort = "distance";

    JSONArray response = new JSONArray();

    JSONObject filters = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        type = 1;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setImageDrawable(getResources().getDrawable(R.drawable.map));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (type == 2) {
                    type = 1;
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.map));
                    nearby_list f1 = nearby_list.newInstance(response);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, f1); // f1_container is your FrameLayout container
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                } else if (type == 1) {
                    type = 2;
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.list));
                    NearbyMap f1 = NearbyMap.newInstance(response, lati, longi);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, f1); // f1_container is your FrameLayout container
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (type == 2) {
            type = 1;
            fab.setImageDrawable(getResources().getDrawable(R.drawable.map));
            super.onBackPressed();
        } else if (type == 1) {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.search) {
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException ignored) {

            }
        }else if (item.getItemId() == R.id.filter){
            startActivityForResult(new Intent(EventsMap.this,Filters.class).putExtra("filters",String.valueOf(filters)),124);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                    assert getSupportActionBar() != null;
                    getSupportActionBar().setTitle(addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2));
                    Log.e("log", addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getEvent(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            } else {
                Toast.makeText(this, "Not connected", Toast.LENGTH_LONG).show();
            }
        }

    }

    void getEvent(final double latitude, final double longitude) {

        String cost = "",category="";
        if (filters.optBoolean("free")) cost = "free";
        else if (filters.optBoolean("paid")) cost = "paid";

        if (filters.optBoolean("free") && filters.optBoolean("paid"))
            cost="";

        if (filters.optBoolean("business")){ if (category.equals("")) category = "'Business'";else category = ",'Business'";}
        else if (filters.optBoolean("parties")){ if (category.equals("")) category = "'Parties'";else category = ",'Parties'";}
        else if (filters.optBoolean("sports")){ if (category.equals("")) category = "'Sports'";else category = ",'Sports'";}
        else if (filters.optBoolean("festival")){ if (category.equals("")) category = "'Festival'";else category = ",'Festival'";}
        else if (filters.optBoolean("college")){ if (category.equals("")) category = "'College'";else category = ",'College'";}

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        SharedPreferences prf = getSharedPreferences(Tools.pref, Context.MODE_PRIVATE);
        JsonArrayRequest request;
        try {
            request = new JsonArrayRequest("http://www.heythere.in/api/getEventsforMap",
                    new JSONObject().put("heythere_user_id", prf.getInt(Tools.sharedUserIdMap, 0))
                            .put("user_lat_value", latitude)
                            .put("user_lon_value", longitude).put("sorting",sort)
                            .put("cost",cost).put("category",category), new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    dialog.cancel();
                    EventsMap.this.response = response;
                    Log.e("response", String.valueOf(response));
                    lati = latitude;
                    longi = longitude;
                    Fragment f1 = nearby_list.newInstance(response);
                    if (type==1) {
                        f1 = nearby_list.newInstance(response);
                    }else if(type==2){
                        f1 = NearbyMap.newInstance(response,lati,longi);
                    }
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, f1); // f1_container is your FrameLayout container
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.cancel();
                }
            });
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                    assert getSupportActionBar() != null;
                    getSupportActionBar().setTitle(addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2));
                    Log.e("log", addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getEvent(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            } else {
                Toast.makeText(this, "Not connected", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                assert getSupportActionBar() != null;
                getSupportActionBar().setTitle(place.getAddress());
                getEvent(place.getLatLng().latitude,place.getLatLng().longitude);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("log", status.getStatusMessage());

            }
        }else if (requestCode ==2){
            Log.e("log","permission granted");
        }else if (requestCode == 124){
            if (resultCode == 1256){
                Log.e("filters",data.getStringExtra("filters"));
                try {
                    filters = new JSONObject(data.getStringExtra("filters"));
                    sort = filters.optString("sorting");
                    getEvent(lati,longi);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
