package com.my.shopee.myshopee.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PropertyTypeActivity extends AppCompatActivity implements View.OnClickListener{

    int currentPageMetalList = 0;
    List<String> metalList;
    MetalRecyclerViewPager viewPager;
    Timer PosterTimer;
    LinearLayoutManager shoppingManager;
    SharedPreferences userPreferences;
    CardView buyCard, rentCard;
    CardView sellCard;
    TextView cityName;
    List<JSONObject> posterDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_type);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.property_type_toolbar_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cityName = toolbar.findViewById(R.id.toolbar_city_name);
        cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PropertyTypeActivity.this, locationActivity.class));
            }
        });

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        userPreferences = getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);
        if(!userPreferences.getString("currentCity", "empty").equals("empty")) {
            cityName.setText(userPreferences.getString("currentCity", "No city selected"));
        }
        posterDetails = new ArrayList<>();
        loadPosters();

        buyCard = findViewById(R.id.property_buy_card);
        sellCard = findViewById(R.id.property_sell_card);
        rentCard = findViewById(R.id.property_rent_card);
        buyCard.setOnClickListener(this);
        sellCard.setOnClickListener(this);
        rentCard.setOnClickListener(this);
        posterDetails = new ArrayList<>();

        viewPager = findViewById(R.id.viewPager);
        shoppingManager = new LinearLayoutManager(PropertyTypeActivity.this);
        shoppingManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        viewPager.setLayoutManager(shoppingManager);
        viewPager.addOnItemTouchListener(new RecyclerItemClickListener(this, viewPager, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                JSONObject selectedPoster = posterDetails.get(position);
                Intent posterDetails = new Intent(PropertyTypeActivity.this, PosterDetailsActivity.class);
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
                            switch (posters.getString("result_code")) {
                                case "111":
                                    loadPostersDetails(posters.getJSONArray("data"));
                                    break;
                                case "333":
                                    loadDefaultPosters();
                                    break;
                                case "555":
                                    Toast.makeText(getApplicationContext(), posters.getString("status_message"), Toast.LENGTH_SHORT).show();
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
                posterType.put("poster_type", "property");
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
        PosterTimer.schedule(new PropertyTypeActivity.PosterTimeChecker(PosterCount), 0, 3500);
    }

    private DisplayMetrics getDisplayMetrics() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (viewPager.getAdapter() != null) {
            int PosterCount = viewPager.getAdapter().getItemCount();
            PosterTimer = new Timer();
            PosterTimer.schedule(new PropertyTypeActivity.PosterTimeChecker(PosterCount), 0, 3500);
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
        Intent innerProperty = new Intent(PropertyTypeActivity.this, PropertyInnerActivity.class);
        switch (v.getId()) {
            case R.id.property_buy_card:
                innerProperty.putExtra("queryID", "buy");
                startActivity(innerProperty);
                break;
            case R.id.property_sell_card:
                Intent propertySellIntent = new Intent(PropertyTypeActivity.this, propertySellActivity.class);
                startActivity(propertySellIntent);
                break;
            case R.id.property_rent_card:
                innerProperty.putExtra("queryID", "rent");
                startActivity(innerProperty);
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

}
