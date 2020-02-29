package ca.unb.sportsoptionr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    int validUser = 0;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText email = findViewById(R.id.emailL);
                final EditText pass = findViewById(R.id.passL);


                final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url ="http://5e17926a505bb50014720d41.mockapi.io/Users";
                // Request a string response from the provided URL.
                final StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONArray resp = new JSONArray(response);
                                    for(int i =0;i<resp.length();i++){
                                        final JSONObject oneObject = resp.getJSONObject(i);
                                        String Urlemail = oneObject.getString("email");
                                        String Urlpass = oneObject.getString("pass");
                                        if(Urlemail.equals(email.getText().toString()) && Urlpass.equals(pass.getText().toString())){
                                            validUser =1;
                                            userID = oneObject.getString("id");
                                            setLog(userID);

                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.e("Error Login",e.getMessage());
                                    e.printStackTrace();
                                }

                                if(validUser ==1){
                                    validUser =0;
                                    Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(activity2Intent);
                                }else{
                                    TextView inval = findViewById(R.id.Inval);
                                    inval.setText("Invalid email/password");
                                    inval.setTextColor(Color.RED);

                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);


            }
        });
        Button signUp = findViewById(R.id.signUpL);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activity2Intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(activity2Intent);
            }
        });


    }

    protected void setLog(String id){
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://5e17926a505bb50014720d41.mockapi.io/Users/"+id;
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("logged", "-1");

                return params;
            }

        };

        queue.add(putRequest);
    }
}
