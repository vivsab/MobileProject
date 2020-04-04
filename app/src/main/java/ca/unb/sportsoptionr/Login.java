package ca.unb.sportsoptionr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.se.omapi.Session;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.sql.DataSource;

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
                                            verifyCode(Urlemail);

                                        }
                                    }
                                } catch (JSONException e) {
                                    Log.e("Error Login",e.getMessage());
                                    e.printStackTrace();
                                }

                                if(validUser ==1){//If correct password and email has been entered
                                    validUser =0;


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

    /**
     * Returns a value between 0 (inclusive) and the value you supply (exclusive).
     * So supplying `10` will result in a number between 0 and 9.
     */
    private int getRandomInt(int ceilingExclusive) {
        Random random = new Random();
        return random.nextInt(ceilingExclusive);
    }

    private String getVerificationCode(){
        int ones = getRandomInt(10);
        int tens = getRandomInt(10)* 10;
        int hunderds = getRandomInt(10)*100;
        int thousands = getRandomInt(10)*1000;

        int value = ones + tens + hunderds + thousands;

        return Integer.toString(value);
    }

    protected void verifyCode(final String Urlemail){
        //sending email
        final String code = getVerificationCode();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String url = "http://34.66.82.113:8000/api/accounts/send/code/";
        StringRequest postRequest = new StringRequest(Request.Method.PUT, url,
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

                params.put("email", Urlemail);
                params.put("code", code);

                return params;
            }
        };
        queue.add(postRequest);


        //setting up alert dialog
        final EditText input = new EditText(Login.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setView(input);
        builder.setTitle("Verification Code")
                .setMessage("A verification code was sent to your email.")
                .setCancelable(false)
                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Verify codes match
                        if(input.getText().toString().equals(code)){
                            setLog(userID);
                            Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(activity2Intent);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        //Creating dialog box
        AlertDialog dialog  = builder.create();
        dialog.show();
    }

}
