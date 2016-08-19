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
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatButton fb_signup;
    CallbackManager callbackManager;
    EditText email,name,password;
    TextView email_err_text,name_err_text,password_err_text;
    Button submit;
    boolean en_name=false,en_mail=false,en_password=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fb_signup = (AppCompatButton)findViewById(R.id.fb_signup);

        email = (EditText) findViewById(R.id.email);
        name = (EditText)findViewById(R.id.name);
        password = (EditText)findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);

        submit.setEnabled(false);

        email_err_text = (TextView) findViewById(R.id.email_error);
        name_err_text = (TextView) findViewById(R.id.name_error);
        password_err_text = (TextView) findViewById(R.id.password_error);

        submit.setOnClickListener(this);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()){
                    email_err_text.setText("please provide valid email");
                    en_mail = false;
                    submit.setEnabled(false);
                }else{
                    email_err_text.setText("");
                    en_mail = true;
                    if (en_name && en_password){
                        submit.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 5){
                    name_err_text.setText("name must have atleast 5 characters");
                    en_name = false;
                    submit.setEnabled(false);
                }else {
                    name_err_text.setText("");
                    en_name = true;
                    if (en_mail && en_password){
                        submit.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 6){
                    password_err_text.setText("password must have atleast 6 characters");
                    en_password = false;
                    submit.setEnabled(false);
                }else {
                    password_err_text.setText("");
                    en_password = true;
                    if (en_name && en_mail){
                        submit.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        assert fb_signup != null;
        fb_signup.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("FB Response",String.valueOf(object));
                        try {
                            final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
                            progressDialog.setMessage("Signing you up...");
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

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
    public void onClick(View v) {
        if (v.getId() == R.id.fb_signup){
            LoginManager.getInstance().logInWithReadPermissions(SignupActivity.this, Arrays.asList("public_profile","email"));
        }else if (v.getId() == R.id.submit){
            String nametext = name.getText().toString();
            String emailtext = email.getText().toString();
            String passwordtext = password.getText().toString();
            if (nametext.length() !=0 && emailtext.length() != 0 && passwordtext.length() != 0){
                try {
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Signing you up...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    JsonObjectRequest request = new JsonObjectRequest("http://www.heythere.in/api/processSignup",
                            new JSONObject().put("heythere_name", nametext).put("heythere_email", emailtext).put("heythere_password", passwordtext), new Response.Listener<JSONObject>() {
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
                            }else if (response.optInt("success") == 2){
                                Toast.makeText(getApplicationContext(),"email already exists",Toast.LENGTH_LONG).show();
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
        }
    }

}
