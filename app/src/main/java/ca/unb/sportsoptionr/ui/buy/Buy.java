package ca.unb.sportsoptionr.ui.buy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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

import ca.unb.sportsoptionr.R;

public class Buy extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_buy, container, false);

        final LinearLayout lay = (LinearLayout) root.findViewById(R.id.LinearOptions);


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="http://5e17926a505bb50014720d41.mockapi.io/option";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray resp = new JSONArray(response);
                            for(int i =0;i<resp.length();i++){
                                final JSONObject oneObject = resp.getJSONObject(i);
                                final Button test = new Button(getContext());
                                final String optionNum = oneObject.getString("optionId");
                                final String optionPrice = oneObject.getString("Price");
                                final String optionOwner = oneObject.getString("owner");
                                final String optionDate = oneObject.getString("Date").substring(0,oneObject.getString("Date").indexOf('T'));


                                test.setText("Option: "+optionNum+"\nPrice: $"+optionPrice);
                                test.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Purchase Option "+optionNum)
                                                .setMessage("Option ID: "+optionNum+"\nPrice: $"+optionPrice+"\nOriginal Owner: "+optionOwner+"\nDate of Event: "+optionDate)
                                                .setCancelable(false)
                                                .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        try {
                                                            UserOptions(root,optionPrice,optionOwner,oneObject.getString("Date"));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        RequestQueue queue = Volley.newRequestQueue(getContext());
                                                        String url = "http://5e17926a505bb50014720d41.mockapi.io/option/"+optionNum;
                                                        StringRequest dr = new StringRequest(Request.Method.DELETE, url,
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
                                                                        // error.

                                                                    }
                                                                }
                                                        );
                                                        queue.add(dr);
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
                                });
                                lay.addView(test);

                                //start basic activity and use put extra for all the important info
                            }
                        } catch (JSONException e) {
Log.e("ww",e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

        return root;
    }

    public void UserOptions(final View root, final String optionPrice, final String optionOwner, final String optionDate){
        final RequestQueue queue = Volley.newRequestQueue(getContext());
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
                                int logged = oneObject.getInt("logged");

                                if(logged==-1){
                                    String id = oneObject.getString("id");
                                    RequestQueue queue = Volley.newRequestQueue(getContext());

                                    String url = "http://5e17926a505bb50014720d41.mockapi.io/Users/"+id+"/option";
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

                                            params.put("owner", optionOwner);
                                            params.put("Price",optionPrice);
                                            params.put("Date", optionDate);

                                            return params;
                                        }
                                    };
                                    queue.add(postRequest);


                                }
                            }
                        } catch (JSONException e) {
                            Log.e("Error",e.getMessage());
                            e.printStackTrace();
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

}