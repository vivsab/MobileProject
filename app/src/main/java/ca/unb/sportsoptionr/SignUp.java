package ca.unb.sportsoptionr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button signUp = findViewById(R.id.signUpS);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                String url = "http://5e17926a505bb50014720d41.mockapi.io/Users";
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
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
                        EditText FN = findViewById(R.id.FName);
                        EditText LN = findViewById(R.id.LName);
                        EditText email = findViewById(R.id.Email);
                        EditText pass = findViewById(R.id.PassS);

                        params.put("FName", FN.getText().toString());
                        params.put("LName", LN.getText().toString());
                        params.put("email", email.getText().toString());
                        params.put("pass", pass.getText().toString());

                        return params;
                    }
                };
                queue.add(postRequest);

                Intent activity2Intent = new Intent(getApplicationContext(), Login.class);
                startActivity(activity2Intent);
            }
        });

    }
}
