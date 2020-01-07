package com.my.shopee.myshopee.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class PosterDetailsActivity extends AppCompatActivity {

    TextView name, address, ownerName, discount, contactNo, timing, email;
    ImageView poster;
    Button callButton;
    int CALL_PHONE_PERMISSION = 5;
    String contactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        name = findViewById(R.id.poster_main_name);
        address = findViewById(R.id.poster_main_address);
        ownerName = findViewById(R.id.poster_owner_name);
        discount = findViewById(R.id.poster_discount);
        contactNo = findViewById(R.id.poster_owner_contact);
        timing = findViewById(R.id.poster_owner_timing);
        email = findViewById(R.id.poster_owner_email);
        poster = findViewById(R.id.poster_main_image);
        callButton = findViewById(R.id.poster_call_button);

        Intent prev = getIntent();
        String jsonString = prev.getStringExtra("details");
        Log.i("posterDetails", jsonString);
        try {
            JSONObject details = new JSONObject(jsonString);
            if(details.has("poster_type")) {
                if (details.getString("poster_type").equals("service")) {
                    name.setText(details.getString("provider_name"));
                    address.setText(details.getString("provider_address"));
                    ownerName.setText("Owner name: " + details.getString("provider_name"));
                    discount.setText(details.getString("discount"));
                    contactNumber = details.getString("provider_contact");
                    contactNo.setText("Contact Number: " + details.getString("provider_contact"));
                    timing.setText("Timings: " + details.getString("provider_timings"));
                    email.setText("Email: " + details.getString("provider_email"));
                    String imagePath = Constants.serviceProviderImageURL + details.getString("image");
                    Glide.with(this).load(imagePath).into(poster);
                    callButton.setText("Call " + details.getString("provider_name"));
                } else {
                    name.setText(details.getString("name"));
                    address.setText(details.getString("address"));
                    ownerName.setText("Owner name: " + details.getString("owner_name"));
                    discount.setText(details.getString("discount"));
                    contactNo.setText("Contact Number: " + details.getString("contact"));
                    contactNumber = details.getString("contact");
                    timing.setText("Timings: " + details.getString("timings"));
                    email.setText("Email: " + details.getString("email"));
                    String imagePath = Constants.storeImagesBaseURL + details.getString("image");
                    Glide.with(this).load(imagePath).into(poster);
                    callButton.setText("Call " + details.getString("name"));
                }
                callButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callShop();
                    }
                });
            } else {
                if (!details.has("name")) {
                    name.setText(details.getString("provider_name"));
                    address.setText(details.getString("provider_address"));
                    ownerName.setText("Owner name: " + details.getString("provider_name"));
                    discount.setText(details.getString("discount"));
                    contactNumber = details.getString("provider_contact");
                    contactNo.setText("Contact Number: " + details.getString("provider_contact"));
                    timing.setText("Timings: " + details.getString("provider_timings"));
                    email.setText("Email: " + details.getString("provider_email"));
                    String imagePath = Constants.serviceProviderImageURL + details.getString("image");
                    Glide.with(this).load(imagePath).into(poster);
                    callButton.setText("Call " + details.getString("provider_name"));
                } else {
                    name.setText(details.getString("name"));
                    address.setText(details.getString("address"));
                    ownerName.setText("Owner name: " + details.getString("owner_name"));
                    discount.setText(details.getString("discount"));
                    contactNo.setText("Contact Number: " + details.getString("contact"));
                    contactNumber = details.getString("contact");
                    timing.setText("Timings: " + details.getString("timings"));
                    email.setText("Email: " + details.getString("email"));
                    String imagePath = Constants.storeImagesBaseURL + details.getString("image");
                    Glide.with(this).load(imagePath).into(poster);
                    callButton.setText("Call " + details.getString("name"));
                }
            }
        } catch (JSONException e) {
            Log.e("posterError", e.toString());
            e.printStackTrace();
        }
    }
    private void callShop() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String contact = "+91" + contactNumber;
        callIntent.setData(Uri.parse("tel:" + contact));
        if (ActivityCompat.checkSelfPermission(PosterDetailsActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PosterDetailsActivity.this,
                    new String[]{android.Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION);
        }
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CALL_PHONE_PERMISSION && grantResults.length >0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callShop();
        }
    }
}
