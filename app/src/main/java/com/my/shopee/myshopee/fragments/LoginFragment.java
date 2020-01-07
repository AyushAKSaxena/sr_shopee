package com.my.shopee.myshopee.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends android.support.v4.app.Fragment implements OnClickListener {
    private View view;

    private EditText emailid, password;
    private Button loginButton;
    private TextView forgotPassword, signUp;
    private CheckBox show_hide_password;
    private LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    SharedPreferences userDetails;
    AlertDialog.Builder loginDialog;
    AlertDialog dialog;


    public LoginFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_layout, container, false);
        initViews();
        setListeners();
        return view;
    }

    // Initiate Views
    private void initViews() {
        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        emailid = view.findViewById(R.id.login_emailid);
        password = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.loginBtn);
        forgotPassword = view.findViewById(R.id.forgot_password);
        signUp = view.findViewById(R.id.createAccount);
        show_hide_password = view.findViewById(R.id.show_hide_password);
        loginLayout = view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);

        loginDialog = new AlertDialog.Builder(getContext());
        userDetails = getActivity().getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);

        // Setting text selector over textviews
        try {
            @SuppressLint("ResourceType") ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    getResources().getXml(R.drawable.text_selector));

            forgotPassword.setTextColor(csl);
            show_hide_password.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                checkValidation();
                break;

            case R.id.forgot_password:

                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_exit)
                        .replace(R.id.frameContainer,
                                new ForgotPasswordFragment(),
                                Constants.ForgotPassword_Fragment).commit();
                break;
            case R.id.createAccount:

                // Replace signup frgament with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_exit)
                        .replace(R.id.frameContainer, new SignupFragment(),
                                Constants.SignUp_Fragment).commit();
                break;
        }

    }

    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        String getEmailId = emailid.getText().toString();
        String getPassword = password.getText().toString();

        // Check patter for email id
        boolean emailIdMatcher = Patterns.EMAIL_ADDRESS.matcher(getEmailId).matches();
        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            Toast.makeText(getActivity(), "Enter email and password", Toast.LENGTH_LONG).show();
        }
        // Check if email id is valid or not
        else if (!emailIdMatcher)
            Toast.makeText(getActivity(), "Invalid email", Toast.LENGTH_LONG).show();
        else
            sendLoginRequest();
    }

    private void sendLoginRequest() {
        showAlertDialog();
        StringRequest loginRequest = new StringRequest(Request.Method.POST,Constants.baseURL + Constants.loginURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            switch (responseObject.getString("result_code")) {
                                case "111":
                                    JSONObject data = responseObject.getJSONObject("data");
                                    int activeStatus = data.getInt("is_active");
                                    int disabledStatus = data.getInt("is_disabled");
                                    if(activeStatus == 0) {
                                        Toast.makeText(getContext(), R.string.inactive_account_message, Toast.LENGTH_LONG).show();
                                    } else if(disabledStatus == 0) {
                                        Toast.makeText(getContext(), R.string.disabled_account_message, Toast.LENGTH_LONG).show();
                                    } else {
                                        SharedPreferences.Editor userDetailsEditor = userDetails.edit();
                                        userDetailsEditor.putString("userID", data.getString("user_id"));
                                        userDetailsEditor.putString("userName", data.getString("username"));
                                        userDetailsEditor.putString("password", data.getString("password"));
                                        userDetailsEditor.putString("fullName", data.getString("full_name"));
                                        userDetailsEditor.putString("contactNumber", data.getString("contact_no"));
                                        userDetailsEditor.apply();
                                        Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_LONG).show();
                                        hideAlertDialog();
                                        startMainActivity();
                                    }
                                    break;
                                case "222":
                                    Toast.makeText(getContext(), "Invalid username or password", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(getContext(), "Error code 1002", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", error.toString());
                if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                    Toast.makeText(getContext(), R.string.no_connection_error_message, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Error code 1001", Toast.LENGTH_LONG).show();
                }
                hideAlertDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                String usernameValue = emailid.getText().toString();
                String passwordValue = password.getText().toString();
                Map<String, String> values = new HashMap<>();
                values.put("username",usernameValue);
                values.put("password", passwordValue);
                Log.i("User values", values.toString());
                return values;
            }
        };
        Volley.newRequestQueue(getContext()).add(loginRequest);
    }

    private void showAlertDialog() {
        loginDialog.setTitle("Logging in");
        loginDialog.setMessage("Please wait while we search your profile...");
        dialog = loginDialog.create();
        dialog.show();
    }

    private void hideAlertDialog() {
        dialog.cancel();
    }

    private void startMainActivity() {
        Intent loginIntent = new Intent(getActivity(), MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }
}