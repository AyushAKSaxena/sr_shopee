package com.my.shopee.myshopee.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.my.shopee.myshopee.Adapters.MetalRecyclerAdapter;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;
import com.my.shopee.myshopee.Utilities.MetalRecyclerViewPager;
import com.my.shopee.myshopee.Utilities.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    int currentPageMetalList = 0;
    List<String> metalList;
    MetalRecyclerViewPager viewPager;
    Timer PosterTimer;
    LinearLayoutManager shoppingManager;
    SharedPreferences userPreferences;
    TextView cityName;
    Button loginLogoutButton, giftCardButton, navBuyGiftCard;
    CardView localStores, property, giftCard, services, contactUs, aboutUs, website;
    boolean isLogin;
    LinearLayout navLocationLayout;
    TextView navLocationCity, navUsername;
    List<JSONObject> posterDetails;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        userPreferences = getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);
        isLogin = userPreferences.getString("userID", "0").equals("0");

        Log.i("userID", Boolean.toString(isLogin));
        posterDetails = new ArrayList<>();
        loadPosters();

        cityName = toolbar.findViewById(R.id.toolbar_city_name);
        cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, locationActivity.class));
            }
        });

        viewPager = findViewById(R.id.viewPager);
        shoppingManager = new LinearLayoutManager(MainActivity.this);
        shoppingManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewPager.setLayoutManager(shoppingManager);

        loginLogoutButton = findViewById(R.id.login_logout_main_button);
        loginLogoutButton.setOnClickListener(this);

        giftCardButton = findViewById(R.id.main_activity_gift_card);
        giftCardButton.setOnClickListener(this);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        int width = (getResources().getDisplayMetrics().widthPixels*9)/10;
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        params.width = width;
        navigationView.setLayoutParams(params);

        website = findViewById(R.id.main_activity_website);
        website.setOnClickListener(this);

        if(isLogin) {
            loginLogoutButton.setText(R.string.login_button_text);
        }

        giftCard = findViewById(R.id.main_activity_gift_secondary);
        giftCard.setOnClickListener(this);
        localStores = findViewById(R.id.main_activity_stores);
        localStores.setOnClickListener(this);
        property = findViewById(R.id.main_activity_property);
        property.setOnClickListener(this);
        services = findViewById(R.id.main_activity_services);
        services.setOnClickListener(this);
        contactUs = findViewById(R.id.main_activity_contact_us);
        contactUs.setOnClickListener(this);
        aboutUs = findViewById(R.id.main_activity_about_us);
        aboutUs.setOnClickListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        navBuyGiftCard = headerLayout.findViewById(R.id.nav_buy_gift_card);
        navBuyGiftCard.setOnClickListener(this);
        navLocationLayout = headerLayout.findViewById(R.id.nav_location_layout);
        navLocationLayout.setOnClickListener(this);
        navLocationCity = headerLayout.findViewById(R.id.nav_location_city);
        navUsername = headerLayout.findViewById(R.id.nav_username_value);

        if(!userPreferences.getString("fullName", "empty").equals("empty")) {
            navUsername.setText("Welcome " + userPreferences.getString("fullName", "Guest"));
        }

        if(!userPreferences.getString("currentCity", "empty").equals("empty")) {
            cityName.setText(userPreferences.getString("currentCity", "No city selected"));
            navLocationCity.setText(userPreferences.getString("currentCity", "No city selected"));
        }

        viewPager.addOnItemTouchListener(new RecyclerItemClickListener(this, viewPager, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                JSONObject selectedPoster = posterDetails.get(position);
                Intent posterDetails = new Intent(MainActivity.this, PosterDetailsActivity.class);
                posterDetails.putExtra("details", selectedPoster.toString());
                startActivity(posterDetails);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                JSONObject selectedPoster = posterDetails.get(position);
                Intent posterDetails = new Intent(MainActivity.this, PosterDetailsActivity.class);
                posterDetails.putExtra("details", selectedPoster.toString());
            }
        }));
    }

    private void loadPosters() {
        Log.i("poster", "loading started");
        StringRequest posterRequest = new StringRequest(Request.Method.POST, Constants.baseURL + Constants.getPosterURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("poster",response);
                        try {
                            JSONObject posters = new JSONObject(response);
                            switch (posters.getString("result_code")) {
                                case "111":
                                    loadPostersDetails(posters.getJSONArray("data"));
                                    break;
                                case "333":
                                    loadDefaultPosters();
                                    break;
                                case "555":
                                    Toast.makeText(getApplicationContext(), "Server Error in loading posters", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (JSONException e) {
                            Log.e("Poster error", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("poster", error.toString());
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> posterType = new HashMap<>();
                posterType.put("poster_type", "all");
                posterType.put("location", userPreferences.getString("currentCity", "none"));
                return posterType;
            }
        };
        Volley.newRequestQueue(this).add(posterRequest);
    }

    private void loadDefaultPosters() {

    }

    private void loadPostersDetails(JSONArray data) throws JSONException {
        metalList = new ArrayList<>();
        Log.i("poster", data.toString());
        for( int i=0; i< data.length(); i++) {
            JSONObject temp = data.getJSONObject(i);
            posterDetails.add(temp);
            String posterImageLocation = temp.getString("poster_image");
            metalList.add(posterImageLocation);
        }
        DisplayMetrics metrics = getDisplayMetrics();
        MetalRecyclerViewPager.MetalAdapter fullMetalAdapter = new MetalRecyclerAdapter(metrics, metalList, posterDetails, this);
        viewPager.setAdapter(fullMetalAdapter);
        Log.i("poster", "Sending poster");
        int PosterCount = viewPager.getAdapter().getItemCount();
        PosterTimer = new Timer();
        PosterTimer.schedule(new PosterTimeChecker(PosterCount), 0, 3500);
    }

    private DisplayMetrics getDisplayMetrics() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
                AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
                exitDialog.setTitle(R.string.exit_application_title);
                exitDialog.setMessage(R.string.exit_application_message);
                exitDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                });
                exitDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                exitDialog.setCancelable(false);
                Dialog exit = exitDialog.create();
                exit.show();
            }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_item_store:
                startActivity(new Intent(MainActivity.this, StoresActivity.class));
                break;
            case R.id.menu_item_properties:
                startActivity(new Intent(MainActivity.this, PropertyTypeActivity.class));
                break;
            case R.id.menu_item_gift_cards:
                startActivity(new Intent(MainActivity.this, giftCardActivity.class));
                break;
            case R.id.menu_item_services:
                startActivity(new Intent(MainActivity.this, servicesActivity.class));
                break;
            case R.id.menu_item_contact_us:
                startActivity(new Intent(MainActivity.this, contactUsActivity.class));
                break;
            case R.id.menu_item_about_us:
                startActivity(new Intent(MainActivity.this, aboutUsActivity.class));
                break;
            case R.id.menu_item_login:
                if(isLogin) {
                    startActivity(new Intent(MainActivity.this, loginActivity.class));
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
                startActivity(new Intent(MainActivity.this, TermsAndConditionsActivity.class));
                break;
            case R.id.menu_item_website:
                connectToWebService("https://wwww.myshopee.co.in");
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
                        finish();
                        startActivity(getIntent());
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

    @Override
    protected void onStart() {
        super.onStart();
        if(viewPager.getAdapter() != null) {
            int PosterCount = viewPager.getAdapter().getItemCount();
            PosterTimer = new Timer();
            PosterTimer.schedule(new PosterTimeChecker(PosterCount), 0, 3500);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(PosterTimer != null) {
            PosterTimer.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_activity_stores:
                startActivity(new Intent(MainActivity.this, StoresActivity.class));
                break;
            case R.id.main_activity_property:
                startActivity(new Intent(MainActivity.this, PropertyTypeActivity.class));
                break;
            case R.id.main_activity_gift_secondary:
                startActivity(new Intent(MainActivity.this, giftCardActivity.class));
                break;
            case R.id.main_activity_gift_card:
                startActivity(new Intent(MainActivity.this, giftCardActivity.class));
                break;
            case R.id.login_logout_main_button:
                if(isLogin) {
                    startActivity(new Intent(MainActivity.this, loginActivity.class));
                } else {
                    logout();
                }
                break;
            case R.id.main_activity_services:
                startActivity(new Intent(MainActivity.this, servicesActivity.class));
                break;
            case R.id.main_activity_contact_us:
                startActivity(new Intent(MainActivity.this, contactUsActivity.class));
                break;
            case R.id.main_activity_about_us:
                startActivity(new Intent(MainActivity.this, aboutUsActivity.class));
                break;

            case R.id.nav_buy_gift_card:
                startActivity(new Intent(MainActivity.this, giftCardActivity.class));
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_location_layout:
                startActivity(new Intent(MainActivity.this, locationActivity.class));
                break;
            case R.id.main_activity_website:
                connectToWebService("https://wwww.myshopee.co.in");
                break;
        }
    }

    class PosterTimeChecker extends TimerTask {

        final int count;
        PosterTimeChecker(int count) {
            this.count = count;
        }

        @Override
        public void run() {
            if(currentPageMetalList < count) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.smoothScrollToPosition(currentPageMetalList);
                        currentPageMetalList++;
                    }
                });
            } else {
                currentPageMetalList = 0;
            }
        }
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
