package ca.unb.sportsoptionr.ui.sell;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ca.unb.sportsoptionr.Login;
import ca.unb.sportsoptionr.R;


public class Sell extends Fragment {
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_sell, container, false);
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        final TextView mDisplayDate = root.findViewById(R.id.DatePick);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date = year + "-" + month + "-" + day;
                mDisplayDate.setText(date);
            }
        };

        Button signUp = root.findViewById(R.id.buttonSell);
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(getContext());

                String url = "http://5e17926a505bb50014720d41.mockapi.io/option";
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                Toast.makeText(getContext(),"Option is now up for sale!",Toast.LENGTH_LONG).show();
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
                        EditText owner = root.findViewById(R.id.ownerS);
                        EditText price = root.findViewById(R.id.PriceS);
                        TextView date = root.findViewById(R.id.Email);

                        params.put("owner", owner.getText().toString());
                        params.put("Price", price.getText().toString());
                        params.put("Date", mDisplayDate.getText().toString()+"T");

                        return params;
                    }
                };
                queue.add(postRequest);

            }
        });


        return root;
    }
}