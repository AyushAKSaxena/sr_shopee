package com.my.shopee.myshopee.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.my.shopee.myshopee.Adapters.PropertyInnerAdapter;
import com.my.shopee.myshopee.Adapters.StoresInnerAdapter;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;
import com.my.shopee.myshopee.Utilities.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreInnerActivity extends AppCompatActivity {

    RecyclerView storesList;
    TextView storeInnerHeaderText;
    StoresInnerAdapter storesListAdapter;
    int CALL_PHONE_PERMISSION = 5;
    TextView cityName;
    SharedPreferences userPreferences;
    List<String> storeImage, storeAddress, storeContact, storeName, storeTimings;
    String resultName;
    int position;
    List<JSONObject> storeDetials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_inner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.stores_inner_toolbar_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cityName = toolbar.findViewById(R.id.toolbar_city_name);
        cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StoreInnerActivity.this, locationActivity.class));
            }
        });

        Intent prevIntent = getIntent();
        resultName = prevIntent.getStringExtra("queryID");
        storeInnerHeaderText = findViewById(R.id.store_inner_header_text);
        storeInnerHeaderText.setText("Showing Results - "+resultName);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        userPreferences = getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);
        if(!userPreferences.getString("currentCity", "empty").equals("empty")) {
            cityName.setText(userPreferences.getString("currentCity", "No city selected"));
        }

        storesList = findViewById(R.id.stores_inner_recycler_view);

        LinearLayoutManager recyclerViewLayout = new LinearLayoutManager(this);
        storesList.setLayoutManager(recyclerViewLayout);
        storesList.setAdapter(storesListAdapter);
        storesList.addOnItemTouchListener(new RecyclerItemClickListener(this, storesList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openActivity(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        setLists();
        getStoreData();
    }

    private void openActivity(int position) {
        JSONObject selectedPoster = storeDetials.get(position);
        Log.i("poster", selectedPoster.toString());
        Intent posterDetails = new Intent(StoreInnerActivity.this, PosterDetailsActivity.class);
        posterDetails.putExtra("details", selectedPoster.toString());
        startActivity(posterDetails);
    }

    private void callShop(int position) {
        this.position = position;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String contactNumber = "+91" + storeContact.get(position);
        callIntent.setData(Uri.parse("tel:" + contactNumber));
        if (ActivityCompat.checkSelfPermission(StoreInnerActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(StoreInnerActivity.this,
                    android.Manifest.permission.CALL_PHONE)) {
            } else {
                ActivityCompat.requestPermissions(StoreInnerActivity.this,
                        new String[]{android.Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION);
                return;
            }
        }
        startActivity(callIntent);
    }

    private void setLists() {
        storeName = new ArrayList<>();
        storeImage = new ArrayList<>();
        storeContact = new ArrayList<>();
        storeAddress = new ArrayList<>();
        storeTimings = new ArrayList<>();
    }

    private void getStoreData() {
        Log.i("StoresData", "Function called");
        StringRequest StoreRequest = new StringRequest(Request.Method.POST, Constants.baseURL + Constants.storeURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("storesData", response);
                            JSONObject responseObject = new JSONObject(response);
                            String responseCode = responseObject.getString("result_code");
                            switch (responseCode) {
                                case "111":
                                    loadRecycler(responseObject.getString("data"));
                                    break;
                                case "555":
                                    Toast.makeText(getApplicationContext(), "Server Error Occured", Toast.LENGTH_LONG).show();
                                    break;
                                case "333":
                                    showNoResultFound();
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Volley error", e.toString());
                            Toast.makeText(getApplicationContext(), "Error code 4001", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> StoreMap = new HashMap<>();
                StoreMap.put("stores_type", resultName);
                StoreMap.put("location", userPreferences.getString("currentCity", "none"));
                return StoreMap;
            }
        };
        Volley.newRequestQueue(this).add(StoreRequest);
    }

    private void showNoResultFound() {
        Log.i("data", "No result found");
        Toast.makeText(getApplicationContext(), "no result found", Toast.LENGTH_LONG).show();
    }

    private void loadRecycler(String data) throws JSONException {
        storeDetials = new ArrayList<>();
        JSONArray dataArray = new JSONArray(data);
        Log.i("Data", data);
        for (int i=0; i<dataArray.length(); i++) {
            JSONObject propertyDetails = dataArray.getJSONObject(i);
            storeDetials.add(propertyDetails);
            storeName.add(propertyDetails.getString("name"));
            storeAddress.add(propertyDetails.getString("address"));
            storeTimings.add(propertyDetails.getString("timings"));
            storeContact.add(propertyDetails.getString("contact"));
            storeImage.add(propertyDetails.getString("image"));
        }
        LinearLayoutManager recyclerViewLayout = new LinearLayoutManager(this);
        storesList.setLayoutManager(recyclerViewLayout);
        storesListAdapter = new StoresInnerAdapter(this, storeName, storeAddress, storeTimings, storeImage, storeContact);
        storesList.setAdapter(storesListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CALL_PHONE_PERMISSION) {
            callShop(position);
        }
    }
}
