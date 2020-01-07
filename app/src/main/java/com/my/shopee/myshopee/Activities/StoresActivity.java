package com.my.shopee.myshopee.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.my.shopee.myshopee.Adapters.MetalRecyclerAdapter;
import com.my.shopee.myshopee.Adapters.StoresAdapter;
import com.my.shopee.myshopee.Adapters.StoresInnerAdapter;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;
import com.my.shopee.myshopee.Utilities.MetalRecyclerViewPager;
import com.my.shopee.myshopee.Utilities.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class StoresActivity extends AppCompatActivity {

    int currentPageMetalList = 0;
    List<String> metalList;
    MetalRecyclerViewPager viewPager;
    Timer PosterTimer;
    LinearLayoutManager shoppingManager;
    RecyclerView storesList;
    List<String> storeIcons, sampleStoreIcons;
    List<String> storeNames, sampleStoreNames;
    StoresAdapter loadStores;
    GridLayoutManager recyclerViewLayout;
    TextView cityName;
    SharedPreferences userPreferences;
    List<JSONObject> posterDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        toolbar.findViewById(R.id.stores_toolbar_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cityName = toolbar.findViewById(R.id.toolbar_city_name);
        cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StoresActivity.this, locationActivity.class));
            }
        });
        userPreferences = getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);
        if(!userPreferences.getString("currentCity", "empty").equals("empty")) {
            cityName.setText(userPreferences.getString("currentCity", "No city selected"));
        }
        loadPosters();
        posterDetails = new ArrayList<>();
        storesList = findViewById(R.id.store_options_recycler_view);
        recyclerViewLayout = new GridLayoutManager(StoresActivity.this, 3);
        storesList.setLayoutManager(recyclerViewLayout);
        storesList.addOnItemTouchListener(new RecyclerItemClickListener(this, storesList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent innerStoreIntent = new Intent(StoresActivity.this, StoreInnerActivity.class);
                innerStoreIntent.putExtra("queryID", storeNames.get(position));
                startActivity(innerStoreIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        storeIcons = new ArrayList<>();
        storeNames = new ArrayList<>();
        sampleStoreIcons = new ArrayList<>();
        sampleStoreNames = new ArrayList<>();

        try {
            loadStoreCategories();
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewPager = findViewById(R.id.viewPager);
        shoppingManager = new LinearLayoutManager(StoresActivity.this);
        shoppingManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewPager.setLayoutManager(shoppingManager);

        viewPager.addOnItemTouchListener(new RecyclerItemClickListener(this, viewPager, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                JSONObject selectedPoster = posterDetails.get(position);
                Intent posterDetails = new Intent(StoresActivity.this, PosterDetailsActivity.class);
                posterDetails.putExtra("details", selectedPoster.toString());
                startActivity(posterDetails);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void loadPosters() {
        StringRequest posterRequest = new StringRequest(Request.Method.POST, Constants.baseURL + Constants.getPosterURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject posters = new JSONObject(response);
                            Log.i("data", response);
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

            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> posterType = new HashMap<>();
                posterType.put("poster_type", "store");
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
        for( int i=0; i< data.length(); i++) {
            JSONObject temp = data.getJSONObject(i);
            posterDetails.add(temp);
            String posterImageLocation = temp.getString("poster_image");
            metalList.add(posterImageLocation);
        }
        DisplayMetrics metrics = getDisplayMetrics();
        MetalRecyclerViewPager.MetalAdapter fullMetalAdapter = new MetalRecyclerAdapter(metrics, metalList, posterDetails, this);
        viewPager.setAdapter(fullMetalAdapter);
        int PosterCount = viewPager.getAdapter().getItemCount();
        PosterTimer = new Timer();
        PosterTimer.schedule(new StoresActivity.PosterTimeChecker(PosterCount), 0, 3500);
    }

    private void loadStoreCategories() {
        StringRequest storeCategory = new StringRequest(Constants.baseURL + Constants.getCategoryURL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            String resultCode = responseObject.getString("result_code");
                            switch (resultCode) {
                                case "111":
                                    loadStoresAdapter(responseObject.getString("data"));
                                    break;
                                case "555":
                                    Toast.makeText(getApplicationContext(), "Sorry, could not load categories", Toast.LENGTH_LONG).show();
                                    break;
                                case "333":
                                    loadNoDataScreen();

                            }
                        } catch (Exception e) {
                            Log.e("error", e.toString());
                            Toast.makeText(getApplicationContext(), "Error code 5001", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(storeCategory);
    }

    private void loadNoDataScreen() {

    }

    private void loadStoresAdapter(String data) throws JSONException {
        JSONArray dataArray = new JSONArray(data);
        for(int i=0; i<dataArray.length(); i++) {
            JSONObject stores = dataArray.getJSONObject(i);
            storeIcons.add(stores.getString("category_image"));
            storeNames.add(stores.getString("category_name"));
        }
        loadStores = new StoresAdapter(storeNames, storeIcons, this);
        storesList.setAdapter(loadStores);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(viewPager.getAdapter() != null) {
            int PosterCount = viewPager.getAdapter().getItemCount();
            PosterTimer = new Timer();
            PosterTimer.schedule(new StoresActivity.PosterTimeChecker(PosterCount), 0, 3500);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(PosterTimer != null) {
            PosterTimer.cancel();
        }
    }

    private DisplayMetrics getDisplayMetrics() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics;
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
}
