package ca.unb.sportsoptionr.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ca.unb.sportsoptionr.R;

import static android.app.Activity.RESULT_OK;

public class Profile extends Fragment {

    private HomeViewModel homeViewModel;
    private String TAG = "External Activity";
    String currentPhotoPath;
    String id;
    ImageView uploadImage;
    View changeRoot;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                Log.i(TAG,"test 1 activity result ");
                galleryAddPic();
                // Do something with the contact here (bigger example below)
            }
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getContext().sendBroadcast(mediaScanIntent);
        Log.i(TAG,"test 1 gallary ");
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        changeRoot = root;
        final TextView textView = root.findViewById(R.id.text_home);

        uploadImage = root.findViewById(R.id.validationImage);

        homeViewModel.getText().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        setProfile(root);

        //Save Button
        Button buttonS = root.findViewById(R.id.SaveP);
        buttonS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                saveProfile(root, true);
                Toast.makeText(getContext(),"Profile changes saved!",Toast.LENGTH_LONG).show();
            }
        });

        //Camera Button
        Button buttonC = root.findViewById(R.id.PassV);
        buttonC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getContext(),"ca.unb.sportsoptionr.provider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, 1);
                        Log.i(TAG,"test 1 activity photo ");
                    }
                }


            }
        });


        return root;
    }

    public void setProfile(final View root){
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
                                    id = oneObject.getString("id");
                                    final EditText FnameP = root.findViewById(R.id.FnameP);
                                    final EditText LnameP = root.findViewById(R.id.LnameP);
                                    final EditText emailP = root.findViewById(R.id.emailP);
                                    final TextView season_pass_validation = root.findViewById(R.id.SeaPassVal);


                                    FnameP.setText(oneObject.getString("FName"));
                                    LnameP.setText(oneObject.getString("LName"));
                                    emailP.setText(oneObject.getString("email"));
                                    season_pass_validation.setText(oneObject.getString("SeasonPassHolder"));

                                }
                            }
                        } catch (JSONException e) {
                            Log.e("Error Login",e.getMessage());
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

    private void uploadImage(final View root){
        File imgFile = new  File(currentPhotoPath);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            uploadImage.setImageBitmap(myBitmap);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://34.66.82.113:8000/api/season_passes/vision/string/create/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            final TextView season_pass_validation = changeRoot.findViewById(R.id.SeaPassVal);
                            season_pass_validation.setText("True");

                            saveProfile(root, false);

                            String Response = jsonObject.getString("response");
                            Toast.makeText(getContext(),"Validated Season Ticket Holder",Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error: Try taking the picture again.",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String, String>  params = new HashMap<String, String>();

                File imgFile = new  File(currentPhotoPath);
                if(imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    String imageBase64String = imageToString(myBitmap);
                    params.put("image", imageBase64String);
                    params.put("user", "root");
                    params.put("team", "");
                    params.put("name_on_season_pass", "");
                    params.put("pass_id", "");
                }

                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQue(stringRequest);
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    public void saveProfile(final View root, boolean firstTime){

        if(firstTime){
            uploadImage(root);
        }

        final RequestQueue queue = Volley.newRequestQueue(getContext());
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
                final EditText FnameP = root.findViewById(R.id.FnameP);
                final EditText LnameP = root.findViewById(R.id.LnameP);
                final EditText emailP = root.findViewById(R.id.emailP);
                final TextView season_pass_validation = root.findViewById(R.id.SeaPassVal);

                params.put("FName", FnameP.getText().toString());
                params.put("LName", LnameP.getText().toString());
                params.put("email", emailP.getText().toString());
                params.put("SeasonPassHolder", season_pass_validation.getText().toString());

                return params;
            }

        };

        queue.add(putRequest);
    }
}