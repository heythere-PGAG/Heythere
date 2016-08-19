package in.heythere.heythere;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

public class Filters extends AppCompatActivity {

    RadioButton distance,likes,recent;
    JSONObject filter_value = new JSONObject();
    CheckBox free,paid,business,festival,sports,parties,college;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        Intent intent = getIntent();

        distance = (RadioButton)findViewById(R.id.distance);
        recent = (RadioButton)findViewById(R.id.recent);
        likes = (RadioButton)findViewById(R.id.rating);

        free = (CheckBox) findViewById(R.id.free);
        paid = (CheckBox) findViewById(R.id.paid);
        business = (CheckBox) findViewById(R.id.business);
        festival= (CheckBox) findViewById(R.id.festival);
        sports = (CheckBox) findViewById(R.id.sports);
        parties = (CheckBox) findViewById(R.id.parties);
        college = (CheckBox) findViewById(R.id.college);

        try {
            filter_value.put("free",false);
            filter_value.put("paid",false);
            filter_value.put("business",false);
            filter_value.put("sports",false);
            filter_value.put("festival",false);
            filter_value.put("college",false);
            filter_value.put("parties",false);
            filter_value.put("sorting","distance");
            filter_value = new JSONObject(intent.getStringExtra("filters"));
            if (filter_value.optBoolean("free")) free.setChecked(true);
            if (filter_value.optBoolean("paid")) paid.setChecked(true);
            if (filter_value.optBoolean("parties")) parties.setChecked(true);
            if (filter_value.optBoolean("business")) business.setChecked(true);
            if (filter_value.optBoolean("sports")) sports.setChecked(true);
            if (filter_value.optBoolean("festival")) festival.setChecked(true);
            if (filter_value.optBoolean("college")) college.setChecked(true);

            if (filter_value.optString("sorting").equals("distance"))distance.setChecked(true);
            else if (filter_value.optString("sorting").equals("likecount DESC"))likes.setChecked(true);
            else if (filter_value.optString("sorting").equals("event_date"))recent.setChecked(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    filter_value.put("free",false);
                    filter_value.put("paid",false);
                    filter_value.put("business",false);
                    filter_value.put("sports",false);
                    filter_value.put("festival",false);
                    filter_value.put("college",false);
                    filter_value.put("parties",false);
                    filter_value.put("sorting","distance");

                    if (distance.isChecked()) filter_value.put("sorting","distance");
                    else if (likes.isChecked()) filter_value.put("sorting","likecount DESC");
                    else if (recent.isChecked()) filter_value.put("sorting","event_date");

                    if (free.isChecked()) filter_value.put("free",true);
                    if (paid.isChecked()) filter_value.put("paid",true);
                    if (business.isChecked()) filter_value.put("business",true);
                    if (sports.isChecked()) filter_value.put("sports",true);
                    if (festival.isChecked()) filter_value.put("festival",true);
                    if (college.isChecked()) filter_value.put("college",true);
                    if (parties.isChecked()) filter_value.put("parties",true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent();
                intent.putExtra("filters",String.valueOf(filter_value));
                setResult(1256,intent);
                finish();
            }
        });
    }
}
