package in.heythere.heythere;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import in.heythere.heythere.Utilities.MySingleton;
import in.heythere.heythere.Utilities.Tools;

public class LoginInfo extends AppCompatActivity implements View.OnClickListener {

    Button login,signup;
    AppCompatButton fb;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login_info);

        TextView ticket = (TextView)findViewById(R.id.ticket);
        assert ticket != null;
        ticket.setText(Html.fromHtml("<b>Events, Parties, Concerts</b> and much more."));

        TextView offer = (TextView)findViewById(R.id.offer);
        assert offer != null;
        offer.setText(Html.fromHtml("Never miss a deal.<b>Offers</b> all around you."));

        TextView discussion = (TextView)findViewById(R.id.discussion);
        assert discussion != null;
        discussion.setText(Html.fromHtml("<b>Chat</b> on the go.Start a random <b>Discussion</b>."));

        login = (Button)findViewById(R.id.login);
        signup = (Button)findViewById(R.id.signup);
        fb = (AppCompatButton) findViewById(R.id.fb_login);
        fb.setOnClickListener(this);
        assert login != null;
        login.setOnClickListener(this);
        assert signup != null;
        signup.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("FB Response",String.valueOf(object));
                        try {
                            final ProgressDialog progressDialog = new ProgressDialog(LoginInfo.this);
                            progressDialog.setMessage("Getting you In...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            JsonObjectRequest request = new JsonObjectRequest("http://www.heythere.in/api/fbAuthProcess",
                                    new JSONObject().put("heythere_name", object.optString("name"))
                                            .put("heythere_email",object.optString("email"))
                                            .put("heythere_fb_id", object.optString("id"))
                                            .put("heythere_cover_pic",object.getJSONObject("cover").optString("source"))
                                            .put("heythere_profile_pic","https://graph.facebook.com/"+object.optString("id")+"/picture?type=normal"),
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            progressDialog.cancel();
                                            if (response.optInt("success") == 1){
                                                getSharedPreferences(Tools.pref, Context.MODE_PRIVATE).edit()
                                                        .putInt(Tools.sharedUserIdMap,response.optInt("heythere_user_id"))
                                                        .putBoolean(Tools.login_boolean,true)
                                                        .apply();
                                                setResult(1);
                                                finish();
                                            }else{
                                                Toast.makeText(getApplicationContext(),"Sorry, try again",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture,cover");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.login){
            startActivityForResult(new Intent(LoginInfo.this,LoginActivity.class),11);
        }else if (id == R.id.signup){
            startActivityForResult(new Intent(LoginInfo.this,SignupActivity.class),6);
        }else if (id == R.id.fb_login){
            LoginManager.getInstance().logInWithReadPermissions(LoginInfo.this, Arrays.asList("public_profile","email"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 6){
            if (resultCode == 1){
                setResult(1);
                finish();
            }
        }else if (requestCode == 11){
            if (resultCode == 1){
                setResult(1);
                finish();
            }
        }
    }
}
