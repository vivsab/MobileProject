package ca.unb.sportsoptionr.ui.tools;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import ca.unb.sportsoptionr.R;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        final TextView textView = root.findViewById(R.id.text_tools);
        toolsViewModel.getText().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        final TableLayout lay = root.findViewById(R.id.StatTable);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="https://statsapi.web.nhl.com/api/v1/standings";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject resp = new JSONObject(response);
                            JSONArray rec = (resp.getJSONArray("records"));
                            for(int i =0;i<rec.length();i++){
                                JSONObject oneObject = rec.getJSONObject(i);
                                TableRow rowD = new TableRow(getContext());//division name row
                                TableRow rowH = new TableRow(getContext());//header row

                                //conference
                                TextView conference = new TextView(getContext());
                                conference.setText(oneObject.getJSONObject("division").getString("name"));
                                conference.setTextColor(Color.RED);
                                rowD.addView(conference);
                                lay.addView(rowD);

                                //header
                                String[] header = {"<b>Rank</b>","<b>Team</b>","<b>GP</b>","<b>W</b>","<b>L</b>","<b>OT</b>","<b>PT</b>","<b>GF</b>","<b>GA</b>","<b>Stk</b>"};
                                for(int k=0;k<header.length;k++){
                                    TextView val = new TextView(getContext());
                                    val.setText(Html.fromHtml(header[k]));
                                    rowH.addView(val);

                                }
                                lay.addView(rowH);



                                JSONArray teamRec = oneObject.getJSONArray("teamRecords");
                                for(int j=0;j<teamRec.length();j++){
                                    JSONObject team = teamRec.getJSONObject(j);
                                    TableRow rowT = new TableRow(getContext());

                                    //values for each team
                                    TextView rank = new TextView(getContext());
                                    TextView teamN = new TextView(getContext());
                                    TextView GP = new TextView(getContext());
                                    TextView Wins = new TextView(getContext());
                                    TextView Losses = new TextView(getContext());
                                    TextView OTL = new TextView(getContext());
                                    TextView Pts = new TextView(getContext());
                                    TextView GF = new TextView(getContext());
                                    TextView GA = new TextView(getContext());
                                    TextView Streak = new TextView(getContext());

                                    teamN.setText(team.getJSONObject("team").getString("name"));

                                    rowT.addView(teamN);
                                    lay.addView(rowT);

                                }

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ss",error.getMessage());
                textView.setText("That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

        return root;
    }
}