package com.my.shopee.myshopee.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;

import java.io.File;
import java.security.Permission;

public class contactUsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    String contactNumber;
    TextView contact1, contact2, email;
    SharedPreferences userPreferences;
    boolean isLogin;
    Button navBuyGiftCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        contact1 = findViewById(R.id.contact_number1);
        contact2 = findViewById(R.id.contact_number2);
        contact1.setOnClickListener(this);
        contact2.setOnClickListener(this);
        userPreferences = getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);
        isLogin = !userPreferences.contains("user_id");

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        int width = (getResources().getDisplayMetrics().widthPixels*9)/10;
        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        params.width = width;
        navigationView.setLayoutParams(params);

        View headerLayout = navigationView.getHeaderView(0);
        navBuyGiftCard = headerLayout.findViewById(R.id.nav_buy_gift_card);
        navBuyGiftCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(contactUsActivity.this, giftCardActivity.class));
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_number1:
                contactNumber = "+919978088321";
                break;
            case R.id.contact_number2:
                contactNumber = "+917359938585";
                break;
        }
        makePhoneCall();
    }

    private void makePhoneCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+contactNumber));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CALL_PHONE)) {
                ActivityCompat.requestPermissions( this,
                        new String[]{android.Manifest.permission.CALL_PHONE}, 3);
            }
        }
        else {
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 3 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+contactNumber));
            try {
                startActivity(callIntent);
            }catch (SecurityException e) {
                e.printStackTrace();

            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_item_store:
                startActivity(new Intent(contactUsActivity.this, StoresActivity.class));
                break;
            case R.id.menu_item_properties:
                startActivity(new Intent(contactUsActivity.this, PropertyTypeActivity.class));
                break;
            case R.id.menu_item_gift_cards:
                startActivity(new Intent(contactUsActivity.this, giftCardActivity.class));
                break;
            case R.id.menu_item_services:
                startActivity(new Intent(contactUsActivity.this, servicesActivity.class));
                break;
            case R.id.menu_item_contact_us:
                startActivity(new Intent(contactUsActivity.this, contactUsActivity.class));
                break;
            case R.id.menu_item_about_us:
                startActivity(new Intent(contactUsActivity.this, aboutUsActivity.class));
                break;
            case R.id.menu_item_login:
                if(isLogin) {
                    startActivity(new Intent(contactUsActivity.this, loginActivity.class));
                } else {
                    logout();
                }
                break;
            case R.id.menu_item_facebook:
                connectToWebService("https://www.facebook.com");
                break;
            case R.id.menu_item_twitter:
                connectToWebService("https://www.twitter.com");
                break;
            case R.id.menu_item_rate:
                connectToWebService("https://play.google.com/store/apps/details?id=com.my.shopee.myshopee");
                break;
            case R.id.menu_item_share:
                shareApp();
                break;
            case R.id.menu_item_terms_and_conditions:
                startActivity(new Intent(contactUsActivity.this, TermsAndConditionsActivity.class));
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.logout_title);
            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // A method to clear all the application data, returns if files are deleted or not
                    boolean successful = clearApplicationData();
                    if(successful) {
                        // A method to clear shared preferences loaded on cache
                        userPreferences.edit().clear().apply();
                        Log.i("username", userPreferences.getString("username", "null"));
                        Intent loginActivity = new Intent(contactUsActivity.this, loginActivity.class);
                        // Add all the flags so that the user cannot get back to this activity using back button
                        startActivity(loginActivity);
                    } else {
                        // Toast error if it is un successful
                        Toast.makeText(getApplicationContext(), "Error code 3001", Toast.LENGTH_LONG).show();
                        Log.i("Logout error", "unable to delete files");
                    }
                }
            });
            dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // To dismiss the dialog
                    dialog.dismiss();
                }
            });
            // Set warning message to delete un synced forms message
            dialog.setMessage("Are you sure you want to logout?");
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error code 3002", Toast.LENGTH_LONG).show();
            Log.i("Logout error", e.toString());
        }
    }

    public boolean clearApplicationData() {
        boolean successfullyDeleted = true;
        // Get the cache directory
        File cacheDirectory = getCacheDir();
        // Get back to find the application folder
        File applicationDirectory = new File(cacheDirectory.getParent());
        // Check for application directory if it exists
        if (applicationDirectory.exists()) {
            // Get all the file names of that directory
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    // Delete all the files if it is not lib directory
                    successfullyDeleted = deleteFile(new File(applicationDirectory, fileName)) && successfullyDeleted;
                }
            }
        }
        return successfullyDeleted;
    }

    // A method to delete that file or directory
    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            // If the given file is a directory
            if (file.isDirectory()) {
                // Get all the files inside that directory
                String[] children = file.list();
                for (String aChildren : children) {
                    // Delete children file
                    deletedAll = deleteFile(new File(file, aChildren)) && deletedAll;
                }
            } else {
                // if it is a file then directly delete it
                deletedAll = file.delete();
            }
        }
        return deletedAll;
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out this app for shopping at: https://play.google.com/store/apps/details?id=com.my.shopee.myshopee");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void connectToWebService(String connectionLink) {
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
