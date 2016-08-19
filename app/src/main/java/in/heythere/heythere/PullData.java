package in.heythere.heythere;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.heythere.heythere.Utilities.MySingleton;
import in.heythere.heythere.Utilities.Tools;
import in.heythere.heythere.providers.EventDataProvider;

public class PullData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_data);

        JsonArrayRequest request = null;
        try {
            request = new JsonArrayRequest("http://www.heythere.in/api/getevents",new JSONObject().put("heythere_user_id",1),
                    new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.e("response",String.valueOf(response));
                    updateContentProvider(response);
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

    private void updateContentProvider(JSONArray array){
        EventDataProvider.EventDatabase database = new EventDataProvider.EventDatabase(getApplicationContext());
        SQLiteDatabase liteDatabase = database.getWritableDatabase();
        liteDatabase.delete(Tools.TABLE_NAME,null,null);
        for (int i = 0; i< array.length(); i++){
            try {
                JSONObject object = array.getJSONObject(i);
                ContentValues values = new ContentValues();

                values.clear();
                values.put(Tools.EVENT_ID,object.optInt("event_id"));
                values.put(Tools.EVENT_NAME,object.optString("event_name"));
                values.put(Tools.EVENT_VENUE,object.optString("event_address"));
                values.put(Tools.EVENT_CITY,object.optString("event_city"));
                values.put(Tools.EVENT_DATE,object.optString("event_date"));
                values.put(Tools.EVENT_POSTER,object.optString("event_poster"));
                values.put(Tools.LIKE_COUNT,object.optInt("likecount"));
                values.put(Tools.EVENT_INTERESTED,object.optInt("liked"));
                values.put(Tools.EVENT_CATEGORY,object.optString("event_category"));
                values.put(Tools.CREATED_DATE,object.optString("created_date"));
                Uri uri = Tools.CONTENT_URI;
                getApplicationContext().getContentResolver().insert(uri,values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        startActivity(new Intent(PullData.this,MainActivity.class));
        finish();
    }
}
