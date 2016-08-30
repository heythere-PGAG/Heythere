package in.heythere.heythere.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.heythere.heythere.LoginInfo;
import in.heythere.heythere.R;
import in.heythere.heythere.Utilities.MySingleton;
import in.heythere.heythere.Utilities.Tools;


public class Whatsup extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    SharedPreferences prf;
    boolean loaded = false;
    RecyclerView hot_talks,recent_talks;

    public Whatsup() {
    }

    public static Whatsup newInstance(int sectionNumber) {
        Whatsup fragment = new Whatsup();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_whatsup, container, false);

        prf = getActivity().getSharedPreferences(Tools.pref, Context.MODE_PRIVATE);
        hot_talks = (RecyclerView)rootView.findViewById(R.id.hot_talks);
        recent_talks = (RecyclerView)rootView.findViewById(R.id.recent_post);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        RecyclerView.LayoutManager recentmanager = new LinearLayoutManager(getActivity());
        hot_talks.setLayoutManager(manager);
        recent_talks.setLayoutManager(recentmanager);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !loaded){
            try {
                int user_id = prf.getInt(Tools.sharedUserIdMap, 0);
                JsonArrayRequest request = new JsonArrayRequest("http://www.heythere.in/api/getDiscussions",
                        new JSONObject().put("heythere_user_id", user_id), new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("res",String.valueOf(response));
                        loaded = true;
                        RecyclerView.Adapter adapter = new HotTalkAdapter(response);
                        RecyclerView.Adapter recentadapter = new RecentTalkAdapter(response);
                        hot_talks.setAdapter(adapter);
                        recent_talks.setAdapter(recentadapter);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                MySingleton.getInstance(getActivity()).addToRequestQueue(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class RecentTalkAdapter extends RecyclerView.Adapter<RecentTalkAdapter.MySmallViewHolder> {

        JSONArray array = new JSONArray();

        class MySmallViewHolder extends RecyclerView.ViewHolder {

            AppCompatTextView name,followers,topics,subject,secondary,like,reply,like_count,response_count;
            AppCompatImageView profile,poster;
            LinearLayout like_layout,reply_layout,share_layout;
            CheckBox like_box;

            MySmallViewHolder(View itemView) {
                super(itemView);
                name = (AppCompatTextView)itemView.findViewById(R.id.user_name);
                like_box = (CheckBox)itemView.findViewById(R.id.like);
                like_count = (AppCompatTextView)itemView.findViewById(R.id.like_count);
                response_count = (AppCompatTextView)itemView.findViewById(R.id.response_count);
                profile = (AppCompatImageView)itemView.findViewById(R.id.user_pic);
                poster = (AppCompatImageView)itemView.findViewById(R.id.topic_poster);
                followers = (AppCompatTextView)itemView.findViewById(R.id.followers_count);
                topics = (AppCompatTextView)itemView.findViewById(R.id.topics_count);
                subject = (AppCompatTextView)itemView.findViewById(R.id.title);
                secondary = (AppCompatTextView)itemView.findViewById(R.id.subject);
                like = (AppCompatTextView)itemView.findViewById(R.id.like_discussion);
                reply = (AppCompatTextView)itemView.findViewById(R.id.reply_discussion);
                like_layout = (LinearLayout)itemView.findViewById(R.id.like_layout);
                reply_layout = (LinearLayout)itemView.findViewById(R.id.reply_layout);
                share_layout = (LinearLayout)itemView.findViewById(R.id.share_layout);
            }
        }

        RecentTalkAdapter(JSONArray response){
            array = response;
        }

        @Override
        public RecentTalkAdapter.MySmallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recent_posts, parent, false);
            return new RecentTalkAdapter.MySmallViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecentTalkAdapter.MySmallViewHolder holder, final int position) {
            try {
                final JSONObject object = array.getJSONObject(position);
                holder.name.setText(object.optString("u_name"));
                holder.followers.setText(object.optString("follow_count")+" FOLLOWERS");
                holder.topics.setText(object.optString("topics_count")+" DISCUSSIONS");
                holder.subject.setText(object.optString("d_subject"));
                holder.secondary.setText(object.optString("d_secondary_subject"));
                holder.like_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int user_id = prf.getInt(Tools.sharedUserIdMap, 0);
                        if (user_id != 0) {
                            if (holder.like_box.isChecked()) {
                                holder.like_box.setChecked(false);
                            } else {
                                holder.like_box.setChecked(true);
                            }
                        }
                        like_discussion(holder,object);
                    }
                });
                holder.like_box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int user_id = prf.getInt(Tools.sharedUserIdMap, 0);
                        if (user_id != 0){
                           like_discussion(holder,object);
                        }else {
                            holder.like_box.setChecked(false);
                            like_discussion(holder,object);
                        }
                    }
                });
                String liked = "Like";
                if (object.optInt("liked")!=0) {
                    liked = "Liked";
                    holder.like_box.setChecked(true);
                }
                //liked = liked + "("+object.optString("likes_count")+")";
                holder.like.setText(liked);
                holder.reply.setText("Talk");
                holder.like_count.setText(object.optString("likes_count")+" Likes . ");
                holder.response_count.setText(object.optString("responses")+" Responses");
                String profile_pic = object.optString("u_profile_pic");
                if (profile_pic.charAt(0) == 'i'){
                    profile_pic = "http://www.heythere.in/"+profile_pic;
                }
                ImageLoader loader = MySingleton.getInstance(getActivity()).getImageLoader();
                loader.get(profile_pic,ImageLoader.getImageListener(holder.profile,R.mipmap.ic_launcher,R.mipmap.ic_launcher));
                if (object.optString("d_subject_image").equals("none")){
                    holder.poster.setVisibility(View.GONE);
                    holder.secondary.setSingleLine(false);
                }else{
                    loader.get("http://www.heythere.in/"+object.optString("d_subject_image"),
                            ImageLoaderPost.getImageListener(holder.poster,R.drawable.crowd,R.drawable.crowd));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return array.length();
        }

    }

    public void like_discussion(final RecentTalkAdapter.MySmallViewHolder holder, final JSONObject object){
        int user_id = prf.getInt(Tools.sharedUserIdMap, 0);
        if (user_id == 0) {
            startActivityForResult(new Intent(getActivity(), LoginInfo.class), 1);
        } else {
            final boolean isChecked = holder.like_box.isChecked();
            String url = "";
            if (isChecked) {
                holder.like.setText("Liked");
                url = "http://www.heythere.in/api/likeDiscussion";
            } else {
                holder.like.setText("Like");
                url = "http://www.heythere.in/api/dislikeDiscussion";
            }
            try {
                JsonObjectRequest request = new JsonObjectRequest(url,
                        new JSONObject().put("heythere_user_id", prf.getInt(Tools.sharedUserIdMap, 0))
                        .put("heythere_discussion_id",object.optInt("d_id")),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.optInt("Success") == 1) {
                                    int count = object.optInt("likes_count");
                                    if (isChecked) {
                                        count = count + 1;
                                    } else {
                                        count = count - 1;
                                    }
                                    try {
                                        object.put("likes_count",count);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    holder.like_count.setText(String.valueOf(count) + " Likes . ");
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                MySingleton.getInstance(getActivity()).addToRequestQueue(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class HotTalkAdapter extends RecyclerView.Adapter<HotTalkAdapter.MySmallViewHolder> {

        JSONArray array = new JSONArray();

        class MySmallViewHolder extends RecyclerView.ViewHolder {

            MySmallViewHolder(View itemView) {
                super(itemView);
            }
        }

        HotTalkAdapter(JSONArray response){
            array = response;
        }

        @Override
        public HotTalkAdapter.MySmallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hot_talks_item, parent, false);
            return new HotTalkAdapter.MySmallViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(HotTalkAdapter.MySmallViewHolder holder, final int position) {

        }

        @Override
        public int getItemCount() {
            return array.length();
        }

    }

    public static class ImageLoaderPost extends ImageLoader{

        public ImageLoaderPost(RequestQueue queue, ImageCache imageCache) {
            super(queue, imageCache);
        }

        public static ImageListener getImageListener(final ImageView view,
                                                     final int defaultImageResId, final int errorImageResId) {
            return new ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (errorImageResId != 0) {
                        view.setImageResource(errorImageResId);
                    }
                }

                @Override
                public void onResponse(ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        view.setImageBitmap(Tools.getCroppedBitmap(response.getBitmap()));
                    } else if (defaultImageResId != 0) {
                        view.setImageResource(defaultImageResId);
                    }
                }
            };
        }
    }
}