package in.heythere.heythere;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DiscussionArea extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_area);

        Intent intent = getIntent();

        String title = intent.getStringExtra("t_subject");
        String id = intent.getStringExtra("t_id");

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setDisplayHomeAsUpEnabled(true);

        JSONArray array = new JSONArray();

        JSONObject object = new JSONObject();

        try {
            object.put("user_name","Arun kumar");
            object.put("message","I am interested.");
            object.put("type","me");
            array.put(object);

            object = new JSONObject();
            object.put("user_name","Gowtham");
            object.put("message","Cool");
            object.put("type","other");
            array.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecyclerView view = (RecyclerView)findViewById(R.id.chat_bubbles);
        view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        RecyclerView.Adapter adapter = new DiscussionAreaAdapter(array);
        view.setAdapter(adapter);

    }

    class DiscussionAreaAdapter extends RecyclerView.Adapter<DiscussionArea.DiscussionAreaAdapter.MyHolder>{

        JSONArray array = new JSONArray();

        DiscussionAreaAdapter(JSONArray array){
            this.array = array;
        }

        class MyHolder extends RecyclerView.ViewHolder{

            TextView name,message;
            LinearLayout layout,back;

            MyHolder(View itemView) {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.user_name);
                message = (TextView)itemView.findViewById(R.id.message);
                layout = (LinearLayout) itemView.findViewById(R.id.chattie);
                back = (LinearLayout) itemView.findViewById(R.id.chat_bubby);
            }
        }

        @Override
        public DiscussionAreaAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.chat_bubbles,parent,false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(DiscussionAreaAdapter.MyHolder holder, int position) {
            try {
                JSONObject object = this.array.getJSONObject(position);
                if (object.optString("type").equals("me")){
                    holder.back.setBackgroundResource(R.drawable.my_message);
                    holder.layout.setHorizontalGravity(Gravity.END);
                }else if (object.optString("type").equals("other")){
                    holder.back.setBackgroundResource(R.drawable.others_message);
                    holder.layout.setHorizontalGravity(Gravity.START);
                }
                holder.name.setText(object.optString("user_name"));
                holder.message.setText(object.optString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return this.array.length();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
