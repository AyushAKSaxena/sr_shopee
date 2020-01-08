package com.my.shopee.myshopee.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
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
import com.my.shopee.myshopee.Adapters.servicesInnerAdapter;
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

public class servicesInnerActivity extends AppCompatActivity {

    RecyclerView servicesList;
    TextView servicesInnerHeaderText;
    servicesInnerAdapter servicesListAdapter;
    TextView cityName;
    SharedPreferences userPreferences;
    List<String> serviceImage, serviceAddress, serviceContact, serviceName, serviceTimings;
    String resultName;
    int position;
    List<JSONObject> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_inner);
        Toolbar toolbar =  findViewById(R.id.toolbar);
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
                startActivity(new Intent(servicesInnerActivity.this, locationActivity.class));
            }
        });

        Intent prevIntent = getIntent();
        resultName = prevIntent.getStringExtra("queryID");
        servicesInnerHeaderText = findViewById(R.id.services_inner_header_text);
        servicesInnerHeaderText.setText("Showing Results - "+resultName);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        userPreferences = getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);
        if(!userPreferences.getString("currentCity", "empty").equals("empty")) {
            cityName.setText(userPreferences.getString("currentCity", "No city selected"));
        }

        servicesList = findViewById(R.id.services_inner_recycler_view);

        LinearLayoutManager recyclerViewLayout = new LinearLayoutManager(this);
        servicesList.setLayoutManager(recyclerViewLayout);
        servicesList.setAdapter(servicesListAdapter);
        servicesList.addOnItemTouchListener(new RecyclerItemClickListener(this, servicesList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openDetailActivity(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        setLists();
        getStoreData();
    }

    private void openDetailActivity(int position) {
        Log.i("poster", serviceList.get(position).toString());
        Intent detailActivity = new Intent(servicesInnerActivity.this, PosterDetailsActivity.class);
        detailActivity.putExtra("details", serviceList.get(position).toString());
        startActivity(detailActivity);
    }

    private void callServiceProvider(int position) {
        this.position = position;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String contactNumber = "+91" + serviceContact.get(position);
        callIntent.setData(Uri.parse("tel:" + contactNumber));
        if (ActivityCompat.checkSelfPermission(servicesInnerActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(servicesInnerActivity.this,
                    android.Manifest.permission.CALL_PHONE)) {
            } else {
                ActivityCompat.requestPermissions(servicesInnerActivity.this,
                        new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                return;
            }
        }
        startActivity(callIntent);

    }

    private void setLists() {
        serviceName = new ArrayList<>();
        serviceImage = new ArrayList<>();
        serviceContact = new ArrayList<>();
        serviceAddress = new ArrayList<>();
        serviceTimings = new ArrayList<>();
        serviceList = new ArrayList<>();
    }

    private void getStoreData() {
        Log.i("StoresData", "Function called");
        StringRequest StoreRequest = new StringRequest(Request.Method.POST, Constants.baseURL + Constants.servicesInnerURL,
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
                StoreMap.put("service_type", resultName);
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
        JSONArray dataArray = new JSONArray(data);
        Log.i("Data", data);
        for (int i=0; i<dataArray.length(); i++) {
            JSONObject propertyDetails = dataArray.getJSONObject(i);
            serviceList.add(propertyDetails);
            serviceName.add(propertyDetails.getString("provider_name"));
            serviceAddress.add(propertyDetails.getString("provider_address"));
            serviceTimings.add(propertyDetails.getString("provider_timings"));
            serviceContact.add(propertyDetails.getString("provider_contact"));
            serviceImage.add(propertyDetails.getString("image"));
        }
        LinearLayoutManager recyclerViewLayout = new LinearLayoutManager(this);
        servicesList.setLayoutManager(recyclerViewLayout);
        servicesListAdapter = new servicesInnerAdapter(this, serviceName, serviceAddress, serviceTimings, serviceImage, serviceContact);
        servicesList.setAdapter(servicesListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            callServiceProvider(position);
        }
    }
}
