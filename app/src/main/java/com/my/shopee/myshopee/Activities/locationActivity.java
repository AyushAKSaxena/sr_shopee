package com.my.shopee.myshopee.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;
import com.my.shopee.myshopee.Utilities.GPSTracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class locationActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    ListView cityNames;
    List<String> cityNamesString, cityNamesStringCopy;
    ArrayAdapter<String> cityNamesAdapter;
    EditText cityNamesEditText;
    TextWatcher cityTextWatcher;
    String[] cityNameList;
    TextView close;
    SharedPreferences userPreferences;
    String currentCityName;
    TextView currentLocationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        cityNames = findViewById(R.id.city_names_list);
        cityNamesString = new ArrayList<>();
        cityNamesStringCopy = new ArrayList<>();
        cityNameList = new String[]{"Sathamba", "Modasa", "Bayad", "Ajmer", "Udaipur", "Himmatnagar", "Ahmedabad", "Balasinor"};
        cityNamesString.addAll(Arrays.asList(cityNameList));
        cityNamesStringCopy.addAll(Arrays.asList(cityNameList));
        cityNamesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cityNamesString);
        cityNames.setAdapter(cityNamesAdapter);

        currentLocationTextView = findViewById(R.id.current_location_city);
        cityTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeListContent(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        final float density = getResources().getDisplayMetrics().density;
        cityNamesEditText = toolbar.findViewById(R.id.city_names_edit_text);
        cityNamesEditText.addTextChangedListener(cityTextWatcher);
        Drawable searchButton = getResources().getDrawable(R.drawable.ic_search);
        int searchWidth = Math.round(16 * density);
        int searchHeight = Math.round(16 * density);
        searchButton.setBounds(0, 0, searchWidth, searchHeight);
        cityNamesEditText.setCompoundDrawables(searchButton, null, null, null);

        close = toolbar.findViewById(R.id.toolbar_close_text_view);

        final Drawable closeButton = getResources().getDrawable(R.drawable.ic_cross);
        final int closeWidth = Math.round(18 * density);
        final int closeHeight = Math.round(18 * density);
        closeButton.setBounds(0, 0, closeWidth, closeHeight);
        close.setCompoundDrawables(null, null, closeButton, null);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userPreferences = getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);

        cityNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String city = cityNamesString.get(position);
                Log.i("city name", city);
                userPreferences.edit().putString("currentCity", city).apply();
                Intent mainIntent = new Intent(locationActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
        });
        currentLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPreferences.edit().putString("currentCity", currentLocationTextView.getText().toString()).apply();
                Intent mainIntent = new Intent(locationActivity.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
        });
        getLocation();
        currentCityName = "";
    }

    private void changeListContent(CharSequence s) {
        Log.i("Char", s.toString());
        try {
            cityNamesString.clear();
            if (s.equals("")) {
                cityNamesString = cityNamesStringCopy;
            } else {
                Log.i("city names", Integer.toString(cityNamesStringCopy.size()));
                for (String city : cityNamesStringCopy) {
                    Log.i("City names", city.toLowerCase());
                    if (city.toLowerCase().contains(s.toString().toLowerCase())) {
                        cityNamesString.add(city);
                    }
                }
            }
            cityNamesAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("List error", e.toString());
        }
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            try {
                GPSTracker gps= new GPSTracker(this);
                currentLocationTextView.setVisibility(View.VISIBLE);
                currentLocationTextView.setText(getLocationResult(gps.getLocation()));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("unknown error", e.getMessage());
                currentLocationTextView.setVisibility(View.GONE);
                findViewById(R.id.current_location_banner).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults)  {
        try {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_LOCATION: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED
                            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        GPSTracker gpsTracker = new GPSTracker(this);
                        currentCityName = getLocationResult(gpsTracker.getLocation());
                        currentLocationTextView.setText(currentCityName);
                    } else {
                        currentCityName = "";
                        currentLocationTextView.setVisibility(View.GONE);
                        findViewById(R.id.current_location_banner).setVisibility(View.GONE);
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            currentLocationTextView.setVisibility(View.GONE);
            findViewById(R.id.current_location_banner).setVisibility(View.GONE);
        }
    }

    public String getLocationResult(Location location) {
        String locationCity = "";
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Log.i("address", addresses.get(0).toString());
                locationCity = (addresses.get(0).getLocality());
                if(locationCity == null) {
                    locationCity = addresses.get(0).getSubAdminArea();
                }
            }
            else {
                Toast.makeText(this,"Unable to fetch location",Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Location error", e.getMessage());
        }
        return locationCity;
    }
}
