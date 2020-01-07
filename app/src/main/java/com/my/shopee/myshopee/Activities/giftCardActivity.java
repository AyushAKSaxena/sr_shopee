package com.my.shopee.myshopee.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class giftCardActivity extends AppCompatActivity {

    EditText name, contact, email, address, city, state;
    Button submit;
    SharedPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_card);

        userPreferences = getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);
        if(userPreferences.getString("userID", "null").equals("null")) {
            showLoginDialog();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        name = findViewById(R.id.card_name);
        email = findViewById(R.id.card_email);
        contact = findViewById(R.id.card_contact);
        address = findViewById(R.id.card_address);
        city = findViewById(R.id.card_city);
        state = findViewById(R.id.card_state);
        submit = findViewById(R.id.card_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCardRegistrationRequest();
            }
        });
    }

    private void showLoginDialog() {
        final AlertDialog.Builder loginDialog = new AlertDialog.Builder(this);
        loginDialog.setTitle("Login error");
        loginDialog.setMessage("You must be logged in to buy gift card");
        loginDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(giftCardActivity.this, loginActivity.class));
            }
        });
        loginDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                giftCardActivity.super.onBackPressed();
            }
        });
        AlertDialog dialog = loginDialog.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void sendCardRegistrationRequest() {

        StringRequest cardRequest = new StringRequest(Request.Method.POST, Constants.baseURL + Constants.cardRegisterURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("error", response);
                            JSONObject responseObject = new JSONObject(response);
                            String resultCode = responseObject.getString("result_code");
                            switch (resultCode) {
                                case "111":
                                    Toast.makeText(getApplicationContext(), "Card Request Sent", Toast.LENGTH_LONG).show();
                                    Intent mainIntent = new Intent(giftCardActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(mainIntent);
                                    break;
                                case "555":
                                    Toast.makeText(getApplicationContext(), "Sorry, could not send card request", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        } catch (Exception e) {
                            Log.e("error", e.toString());
                            Toast.makeText(getApplicationContext(), "Error code 6001", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> cardDetails = new HashMap<>();
                String nameOnCard, emailOnCard, contactOnCard, addressOnCard;
                nameOnCard = name.getText().toString();
                emailOnCard = email.getText().toString();
                contactOnCard = contact.getText().toString();
                addressOnCard = address.getText().toString() + ", " + city.getText().toString() + ", " + state.getText().toString();
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);
                cardDetails.put("name", nameOnCard);
                cardDetails.put("email", emailOnCard);
                cardDetails.put("contact", contactOnCard);
                cardDetails.put("address", addressOnCard);
                cardDetails.put("activated_on", formattedDate);
                cardDetails.put("user_id", userPreferences.getString("userID", "null"));
                return cardDetails;
            }
        };
        Volley.newRequestQueue(this).add(cardRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(userPreferences.getString("userID", "null").equals("null")) {
            showLoginDialog();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(userPreferences.getString("userID", "null").equals("null")) {
            showLoginDialog();
        }
    }
}
