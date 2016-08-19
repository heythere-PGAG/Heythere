package in.heythere.heythere.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.heythere.heythere.Detailed;
import in.heythere.heythere.R;
import in.heythere.heythere.Utilities.MySingleton;
import in.heythere.heythere.Utilities.Tools;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.MyViewHolder> {

    private Activity context;
    private JSONArray array;
    private int layout = 0;

    public EventsListAdapter(Activity context, JSONArray array,int layout){
        this.context = context;
        this.array = array;
        this.layout = layout;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout card;
        TextView name,venue,date,count,share;
        ImageView poster;
        CheckBox like;

        MyViewHolder(View itemView) {
            super(itemView);
            card = (LinearLayout) itemView.findViewById(R.id.card);
            poster = (ImageView) itemView.findViewById(R.id.poster);
            name = (TextView) itemView.findViewById(R.id.name);
            venue = (TextView) itemView.findViewById(R.id.venue);
            venue = (TextView) itemView.findViewById(R.id.venue);
            date = (TextView) itemView.findViewById(R.id.date);
            count = (TextView) itemView.findViewById(R.id.count);
            share = (TextView) itemView.findViewById(R.id.share);
            like = (CheckBox) itemView.findViewById(R.id.like);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        try {
            final JSONObject object = array.getJSONObject(position);

            if (object.optInt("liked")==0){
                holder.like.setChecked(false);
            }else {
                holder.like.setChecked(true);
            }

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        final boolean isChecked = holder.like.isChecked();

                        String url = "";
                        if (isChecked){
                            url = "http://www.heythere.in/api/likeEvent";
                        }else{
                            url = "http://www.heythere.in/api/dislikeEvent";
                        }

                        JsonObjectRequest request = new JsonObjectRequest(url,
                                new JSONObject().put("heythere_user_id",1).put("heythere_event_id",object.optInt("event_id")),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if (response.optInt("Success") == 1){
                                            int count = Integer.parseInt(holder.count.getText().toString());
                                            if (isChecked){
                                                count = count + 1;
                                            }else {
                                                count = count -1;
                                            }
                                            holder.count.setText(String.valueOf(count));
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("volley error",error.toString());
                            }
                        });
                        MySingleton.getInstance(context).addToRequestQueue(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.name.setText(object.optString("event_name"));
            holder.date.setText(object.optString("event_date"));
            holder.venue.setText(object.optString("event_address")+", "+object.optString("event_city"));
            holder.count.setText(object.optString("likecount"));

            ImageLoader mImageLoader = MySingleton.getInstance(context).getImageLoader();
            mImageLoader.get(Tools.HOME_URL + object.optString("event_poster"),
                    ImageLoader.getImageListener(holder.poster, R.drawable.crowd, R.drawable.crowd));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    context.startActivity(new Intent(context,Detailed.class).putExtra("e_id",
                            EventsListAdapter.this.array.getJSONObject(position).optString("event_id")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.array.length();
    }
}
