package in.heythere.heythere.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import in.heythere.heythere.LoginInfo;
import in.heythere.heythere.R;
import in.heythere.heythere.Settings;
import in.heythere.heythere.Utilities.MySingleton;
import in.heythere.heythere.Utilities.Tools;


public class Me extends Fragment {

    SharedPreferences pref;
    LinearLayout before,after;
    TextView followers,following,login_signup,name;
    ImageView cover,profile;
    boolean loaded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static Me newInstance() {
        return new Me();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_me,container,false);

         login_signup = (TextView)view.findViewById(R.id.login_signup);

        before = (LinearLayout)view.findViewById(R.id.before_login);
        after = (LinearLayout)view.findViewById(R.id.after_login);

        followers = (TextView)view.findViewById(R.id.followers);
        following = (TextView)view.findViewById(R.id.following);
        name = (TextView)view.findViewById(R.id.name);

        cover = (ImageView)view.findViewById(R.id.cover);
        profile = (ImageView)view.findViewById(R.id.dp);

        login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), LoginInfo.class),1);
            }
        });

        pref = getActivity().getSharedPreferences(Tools.pref, Context.MODE_PRIVATE);

        if (pref.getBoolean(Tools.login_boolean,false)){
            after.setVisibility(View.VISIBLE);
            before.setVisibility(View.GONE);
        }else{
            before.setVisibility(View.VISIBLE);
            after.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add){
            startActivityForResult(new Intent(getActivity(), Settings.class),7);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            pref = getActivity().getSharedPreferences(Tools.pref, Context.MODE_PRIVATE);
            if (pref.getBoolean(Tools.login_boolean,false)){
                after.setVisibility(View.VISIBLE);
                before.setVisibility(View.GONE);
                loadProfile();
            }
        }else if (requestCode == 7){
            pref = getActivity().getSharedPreferences(Tools.pref, Context.MODE_PRIVATE);
            if (!pref.getBoolean(Tools.login_boolean,false)){
                before.setVisibility(View.VISIBLE);
                after.setVisibility(View.GONE);
            }
        }
    }

    public void loadProfile(){
        pref = getActivity().getSharedPreferences(Tools.pref, Context.MODE_PRIVATE);
        if (pref.getBoolean(Tools.login_boolean,false)){
            after.setVisibility(View.VISIBLE);
            before.setVisibility(View.GONE);
            try {
                pref = getActivity().getSharedPreferences(Tools.pref, Context.MODE_PRIVATE);
                JsonObjectRequest request = new JsonObjectRequest("http://www.heythere.in/api/profileDetails",
                        new JSONObject().put("heythere_user_id", pref.getInt(Tools.sharedUserIdMap,0)), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("profile",String.valueOf(response));
                        name.setText(response.optString("u_name"));
                        String profile_pic = response.optString("u_profile_pic");
                        String cover_pic = response.optString("u_cover_pic");
                        if (profile_pic.charAt(0) == 'i'){
                            profile_pic = "http://www.heythere.in/"+profile_pic;
                        }
                        if (cover_pic.charAt(0) == 'i'){
                            cover_pic = "http://www.heythere.in/"+cover_pic;
                        }
                        ImageLoader mImageLoader = MySingleton.getInstance(getActivity()).getImageLoader();

                        mImageLoader.get(cover_pic,
                                ImageLoader.getImageListener(cover,R.drawable.crowd,R.drawable.crowd));

                        mImageLoader.get(profile_pic,
                                ImageLoader.getImageListener(profile,R.mipmap.ic_launcher,R.mipmap.ic_launcher));

                        loaded = true;
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !loaded){
            if (pref.getBoolean(Tools.login_boolean,false)) {
                loadProfile();
            }
        }
    }
}
