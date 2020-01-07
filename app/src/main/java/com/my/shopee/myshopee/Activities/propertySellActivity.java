package com.my.shopee.myshopee.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;
import com.my.shopee.myshopee.Utilities.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class propertySellActivity extends AppCompatActivity {

    SharedPreferences userPreferences;
    EditText name, address, city, state, features, sellAmount, rentAmount;
    Button submit, imgSelector;
    CheckBox sellAmountSelector, rentAmountSelector, tncCheckBox;
    Bitmap bitmap;
    String userID;
    TextView tncText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_sell);

        userPreferences = getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);
        if(userPreferences.getString("userID", "null").equals("null")) {
            showLoginDialog();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.findViewById(R.id.property_sell_toolbar_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        userID = userPreferences.getString("userID", "0");
        initializeActivity();
        setSelectors();
        setClickListeners();
    }
    private void showLoginDialog() {
        final android.app.AlertDialog.Builder loginDialog = new android.app.AlertDialog.Builder(this);
        loginDialog.setTitle("Login error");
        loginDialog.setMessage("You must be logged in to sell your property.");
        loginDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(propertySellActivity.this, loginActivity.class));
            }
        });
        loginDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                propertySellActivity.super.onBackPressed();
            }
        });
        android.app.AlertDialog dialog = loginDialog.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void setClickListeners() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSellData();
            }
        });

        imgSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        tncText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(propertySellActivity.this, TermsAndConditionsActivity.class));
            }
        });
    }

    private void initializeActivity() {
        name = findViewById(R.id.sell_property_name);
        address = findViewById(R.id.sell_property_address);
        city = findViewById(R.id.sell_property_city);
        state = findViewById(R.id.sell_property_state);
        features = findViewById(R.id.sell_property_features);
        sellAmount = findViewById(R.id.sell_property_sell_amount);
        rentAmount = findViewById(R.id.sell_property_rent_amount);
        submit = findViewById(R.id.sell_property_submit);
        imgSelector = findViewById(R.id.sell_property_image_upload);
        sellAmountSelector = findViewById(R.id.sell_property_sell_checkbox);
        rentAmountSelector = findViewById(R.id.sell_property_rent_checkbox);
        tncCheckBox = findViewById(R.id.checkbox_tnc);
        tncText = findViewById(R.id.text_view_tnc);
        tncText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(propertySellActivity.this, TermsAndConditionsActivity.class));
            }
        });
    }

    private void setSelectors() {
        sellAmountSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    sellAmount.setVisibility(View.VISIBLE);
                } else {
                    sellAmount.setVisibility(View.GONE);
                }
            }
        });
        rentAmountSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    rentAmount.setVisibility(View.VISIBLE);
                } else {
                    rentAmount.setVisibility(View.GONE);
                }
            }
        });
    }

    private void sendSellData() {
        if(checkFields()) {
            AlertDialog.Builder requestDialog = new AlertDialog.Builder(this);
            requestDialog.setTitle("Sending request");
            requestDialog.setMessage("Please wait while we connect to server");
            requestDialog.setCancelable(false);
            final AlertDialog dialog = requestDialog.create();
            dialog.show();
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Constants.baseURL + Constants.sellPropertyURL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {
                                dialog.cancel();
                                JSONObject data = new JSONObject(new String(response.data));
                                Toast.makeText(getApplicationContext(), data.getString("status_message"), Toast.LENGTH_SHORT).show();
                                Intent mainActivity = new Intent(propertySellActivity.this, MainActivity.class);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainActivity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "An error occured while uploading property information ", Toast.LENGTH_SHORT).show();
                            Log.e("Volley error", error.toString());
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("property_name", name.getText().toString());
                    params.put("property_address", address.getText().toString());
                    params.put("property_city", city.getText().toString());
                    params.put("property_state", state.getText().toString());
                    params.put("property_features", features.getText().toString());
                    params.put("user_id", userID);
                    if (sellAmountSelector.isChecked()) {
                        params.put("sell_amount", sellAmount.getText().toString());
                    }
                    if (rentAmountSelector.isChecked()) {
                        params.put("rent_amount", rentAmount.getText().toString());
                    }
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    String imageName = userID + name.getText().toString();
                    params.put("pic", new DataPart(imageName + ".png", getFileDataFromDrawable()));
                    return params;
                }
            };
            Volley.newRequestQueue(this).add(volleyMultipartRequest);
        }
    }

    private boolean checkFields() {
        if(name.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter Property Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if(address.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Enter address of property", Toast.LENGTH_SHORT).show();
            return false;
        } else if(city.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Enter city of property", Toast.LENGTH_SHORT).show();
            return false;
        } else if(state.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Enter state of property", Toast.LENGTH_SHORT).show();
            return false;
        } else if(features.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Enter features of property", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!sellAmountSelector.isChecked() && !rentAmountSelector.isChecked()) {
            Toast.makeText(this, "Enter either sell or rent or both", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!tncCheckBox.isChecked()) {
            Toast.makeText(this, "Please check to our terms and conditions", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(sellAmountSelector.isChecked() && sellAmount.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Enter amount for selling", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(rentAmountSelector.isChecked() && rentAmount.getText().toString().trim().equals("")) {
            Log.i("rent_amount", rentAmount.getText().toString().trim());
            Toast.makeText(this, "Enter amount for rent", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(bitmap == null) {
            Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void selectImage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        3);
            }
        }
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[]grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == 3) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 100);
        }
    }

    public byte[] getFileDataFromDrawable() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}