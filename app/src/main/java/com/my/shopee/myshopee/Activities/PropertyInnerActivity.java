package com.my.shopee.myshopee.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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
import com.my.shopee.myshopee.Adapters.PropertyInnerAdapter;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyInnerActivity extends AppCompatActivity {

    TextView propertyInnerHeaderText, cityName;
    SharedPreferences userPreferences;
    RecyclerView propertiesList;
    PropertyInnerAdapter propertyInnerAdapter;
    String resultName;
    List<String> propertyName, propertyAddress, propertyFeatures, propertyPrice, propertyContact, propertyOwnerID, propertyImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_inner);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.property_inner_toolbar_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cityName = toolbar.findViewById(R.id.toolbar_city_name);
        cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PropertyInnerActivity.this, locationActivity.class));
            }
        });
        userPreferences = getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);
        if(!userPreferences.getString("currentCity", "empty").equals("empty")) {
            cityName.setText(userPreferences.getString("currentCity", "No city selected"));
        }

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        Intent prevIntent = getIntent();
        resultName = prevIntent.getStringExtra("queryID");
        propertyInnerHeaderText = findViewById(R.id.property_inner_header_text);
        propertyInnerHeaderText.setText("Showing Results - "+resultName);
        getPropertyData();

        propertiesList = findViewById(R.id.stores_inner_recycler_view);

        setLists();
    }

    private void setLists() {
        propertyName = new ArrayList<>();
        propertyAddress = new ArrayList<>();
        propertyContact = new ArrayList<>();
        propertyFeatures = new ArrayList<>();
        propertyImage = new ArrayList<>();
        propertyOwnerID = new ArrayList<>();
        propertyPrice = new ArrayList<>();
    }

    private void getPropertyData() {
        StringRequest propertyRequest = new StringRequest(Request.Method.POST, Constants.baseURL + Constants.propertyURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("data", response);
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
                Map<String, String> propertyMap = new HashMap<>();
                propertyMap.put("property_type", resultName);
                Log.i("type", resultName);
                propertyMap.put("location", userPreferences.getString("currentCity", "none"));
                return propertyMap;
            }
        };
        Volley.newRequestQueue(this).add(propertyRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        propertyInnerAdapter.onActivityResult(requestCode, resultCode, data);
    }

    private void showNoResultFound() {
    }

    private void loadRecycler(String data) throws JSONException {
        JSONArray dataArray = new JSONArray(data);
        Log.i("propertyData", data);
        for (int i=0; i<dataArray.length(); i++) {
            JSONObject propertyDetails = dataArray.getJSONObject(i);
            propertyName.add(propertyDetails.getString("property_name"));
            propertyPrice.add(propertyDetails.getString("property_amount"));
            propertyOwnerID.add(propertyDetails.getString("property_owner_id"));
            propertyAddress.add(propertyDetails.getString("property_address"));
            propertyContact.add(propertyDetails.getString("contact_no"));
            propertyImage.add(propertyDetails.getString("property_header_image"));
            propertyFeatures.add(propertyDetails.getString("property_features"));
        }
        LinearLayoutManager recyclerViewLayout = new LinearLayoutManager(this);
        propertiesList.setLayoutManager(recyclerViewLayout);
        propertyInnerAdapter = new PropertyInnerAdapter(this, propertyName, propertyAddress, propertyFeatures, propertyPrice, propertyImage, propertyContact, propertyOwnerID);
        propertiesList.setAdapter(propertyInnerAdapter);
    }

}
