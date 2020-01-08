package com.my.shopee.myshopee.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.my.shopee.myshopee.R;

public class aboutUsActivity extends AppCompatActivity implements View.OnClickListener{

    String connectionLink;
    ImageView fb,twitter,instagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        fb = findViewById(R.id.fb_connect);
        instagram = findViewById(R.id.instagram_connect);
        twitter = findViewById(R.id.twitter_connect);
        fb.setOnClickListener(this);
        twitter.setOnClickListener(this);
        instagram.setOnClickListener(this);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fb_connect:
                connectionLink = "https://www.facebook.com/";
                break;
            case R.id.twitter_connect:
                connectionLink = "https://www.twitter.com/";
                break;
            case R.id.instagram_connect:
                connectionLink = "https://www.instagram.com/";
                break;
        }
        connectToWebService();
    }

    private void connectToWebService() {
        try {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(connectionLink));
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request."
                    + " Please install a web browser",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
