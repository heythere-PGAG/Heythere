package in.heythere.heythere;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.heythere.heythere.Utilities.MySingleton;
import in.heythere.heythere.Utilities.Tools;
import in.heythere.heythere.adapters.EventsListAdapter;

public class Eventslist extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventslist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        assert getSupportActionBar()!=null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(intent.getStringExtra("category"));
        final RecyclerView events = (RecyclerView)findViewById(R.id.eventslist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        assert events != null;
        events.setLayoutManager(layoutManager);

        JsonArrayRequest request = null;
        SharedPreferences prf = getSharedPreferences(Tools.pref, Context.MODE_PRIVATE);
        String url = "";
        if (intent.getStringExtra("category").equals("All")){
            url = "http://www.heythere.in/api/getevents";
        }else {
            url = "http://www.heythere.in/api/getEventsCategory";
        }
        try {
            request = new JsonArrayRequest(url,
                    new JSONObject().put("heythere_user_id",prf.getInt(Tools.sharedUserIdMap,0))
                    .put("category",intent.getStringExtra("category")),
                    new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    Log.e("response",String.valueOf(response));
                    RecyclerView.Adapter adapter = new EventsListAdapter(Eventslist.this,response,R.layout.events_list_item);
                    events.setAdapter(adapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("volley error",error.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
