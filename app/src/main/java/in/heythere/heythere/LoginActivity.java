package in.heythere.heythere;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    CallbackManager callbackManager;
    EditText email,password;
    Button login;
    AppCompatButton fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        fb = (AppCompatButton) findViewById(R.id.fb_login);

        assert login != null;
        login.setOnClickListener(this);
        login.setEnabled(false);

        fb.setOnClickListener(this);

        assert email != null;
        email.addTextChangedListener(this);
        assert password != null;
        password.addTextChangedListener(this);

        callbackManager = CallbackManager.Factory.create();LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("FB Response",String.valueOf(object));
                        try {
                            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String emailtext = email.getText().toString();
        String passwordtext = password.getText().toString();
        if (emailtext.length() !=0 && passwordtext.length() != 0){
            login.setEnabled(true);
        }else {
            login.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login){
            String emailtext = email.getText().toString();
            String passwordtext = password.getText().toString();

            if (emailtext.length() != 0 && passwordtext.length() != 0){
                try {
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Loging In...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    JsonObjectRequest request = new JsonObjectRequest("http://www.heythere.in/api/processLogin",
                            new JSONObject().put("heythere_email", emailtext).put("heythere_password", passwordtext), new Response.Listener<JSONObject>() {
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
                            }else if (response.optInt("success") == 0){
                                Toast.makeText(getApplicationContext(),"Invalid email or password",Toast.LENGTH_LONG).show();
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
        }else if (v.getId() == R.id.fb_login){
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile","email"));
        }
    }
}
