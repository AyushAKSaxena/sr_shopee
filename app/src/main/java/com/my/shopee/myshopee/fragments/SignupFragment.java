package com.my.shopee.myshopee.fragments;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.my.shopee.myshopee.Activities.MainActivity;
import com.my.shopee.myshopee.Activities.loginActivity;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class SignupFragment extends Fragment implements OnClickListener {
    private static View view;
    private static EditText fullName, emailId, mobileNumber,
            password, confirmPassword;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;
    private boolean usernameExists;
    SharedPreferences userDetails;
    AlertDialog.Builder loginDialog, usernameDialog;
    AlertDialog dialog;


    public SignupFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_layout, container, false);
        initViews();
        setListeners();
        return view;
    }

    // Initialize all views
    private void initViews() {
        fullName = view.findViewById(R.id.fullName);
        emailId = view.findViewById(R.id.userEmailId);
        mobileNumber = view.findViewById(R.id.mobileNumber);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        signUpButton = view.findViewById(R.id.signUpBtn);
        login = view.findViewById(R.id.already_user);
        terms_conditions = view.findViewById(R.id.terms_conditions);
        loginDialog = new AlertDialog.Builder(view.getContext());
        usernameDialog = new AlertDialog.Builder(view.getContext());
        userDetails = getActivity().getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);


        usernameExists = false;
        // Setting text selector over textviews
        try {
            @SuppressLint("ResourceType")
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    getResources().getXml(R.drawable.text_selector));

            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:
                // Call checkValidation method
                checkValidation();
                break;

            case R.id.already_user:

                // Replace login fragment
                new loginActivity().replaceLoginFragment();
                break;
        }

    }

    // Check Validation Method
    private void checkValidation() {

        // Get all edittext texts
        String getFullName = fullName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();

        // Pattern match for email id
        boolean emailIdMatcher = Patterns.EMAIL_ADDRESS.matcher(getEmailId).matches();
        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0)

            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_LONG).show();

            // Check if email id valid or not
        else if (!emailIdMatcher)
            Toast.makeText(getActivity(), "Invalid email id", Toast.LENGTH_LONG).show();

            // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword))
            Toast.makeText(getActivity(), "Passwords donot match", Toast.LENGTH_LONG).show();

            // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked())
            Toast.makeText(getActivity(), "Please select terms and conditions", Toast.LENGTH_LONG).show();

            // Else do signup or do your stuff
        else
            try {
                normalSignUpProcess();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error code 1100", Toast.LENGTH_LONG).show();
            }
    }

    private void normalSignUpProcess() throws JSONException {
        if (!usernameExists) {
            showAlertDialog();
            StringRequest registerRequest = new StringRequest(Request.Method.POST, Constants.baseURL + Constants.registerURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                dialog.setMessage(response);
                                Log.i("response", response);
                                JSONObject responseObject = new JSONObject(response);
                                switch (responseObject.getString("result_code")) {
                                    case "111":
                                        JSONObject data = responseObject.getJSONObject("data");
                                        SharedPreferences.Editor userDetailsEditor = userDetails.edit();
                                        userDetailsEditor.putString("userID", data.getString("user_id"));
                                        userDetailsEditor.putString("userName", data.getString("username"));
                                        userDetailsEditor.putString("password", data.getString("password"));
                                        userDetailsEditor.putString("fullName", data.getString("full_name"));
                                        userDetailsEditor.putString("contactNumber", data.getString("contact_no"));
                                        userDetailsEditor.apply();
                                        Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_LONG).show();
                                        hideAlertDialog();
                                        Intent mainActivity = new Intent(getActivity(), MainActivity.class);
                                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainActivity);
                                        break;
                                    case "333":
                                        Toast.makeText(getContext(), responseObject.getString("status_message"), Toast.LENGTH_LONG).show();
                                        break;
                                    case "555":
                                        Toast.makeText(getContext(), responseObject.getString("status_message"), Toast.LENGTH_LONG).show();
                                        break;
                                }
                                hideAlertDialog();
                            } catch (JSONException e) {
                                hideAlertDialog();
                                Toast.makeText(getContext(), "Error code 2002", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Volley Error", error.toString());
                    if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                        Toast.makeText(getContext(), R.string.no_connection_error_message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Error code 2001", Toast.LENGTH_LONG).show();
                    }
                    hideAlertDialog();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    String usernameValue = emailId.getText().toString();
                    String passwordValue = password.getText().toString();
                    String contactNoValue = mobileNumber.getText().toString();
                    String fullNameValue = fullName.getText().toString();
                    Map<String, String> values = new HashMap<>();
                    values.put("username", usernameValue);
                    values.put("password", passwordValue);
                    values.put("fullName", fullNameValue);
                    values.put("contactNo", contactNoValue);
                    Log.i("User values", values.toString());
                    return values;
                }
            };
            Volley.newRequestQueue(getContext()).add(registerRequest);
        } else {
            showUsernameDialog();
        }
    }

    private void showAlertDialog() {
        loginDialog.setTitle("Registering");
        loginDialog.setMessage("Please wait while we connect to server to register you...");
        dialog = loginDialog.create();
        dialog.show();
    }

    private void showUsernameDialog() {
        usernameDialog.setMessage("Username Already Exists, Please try another One.");
        usernameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        usernameDialog.setCancelable(false);
        dialog = usernameDialog.create();
        dialog.show();
    }

    private void hideAlertDialog() {
        dialog.cancel();
    }
}