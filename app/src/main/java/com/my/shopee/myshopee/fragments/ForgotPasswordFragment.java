package com.my.shopee.myshopee.fragments;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.my.shopee.myshopee.Activities.loginActivity;
import com.my.shopee.myshopee.R;

public class ForgotPasswordFragment extends Fragment implements
        OnClickListener {
    private static View view;

    private static EditText emailId;
    private static TextView submit, back;

    public ForgotPasswordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.forget_password_layout, container,
                false);
        initViews();
        setListeners();
        return view;
    }

    // Initialize the views
    private void initViews() {
        emailId = (EditText) view.findViewById(R.id.registered_emailid);
        submit = (TextView) view.findViewById(R.id.forgot_button);
        back = (TextView) view.findViewById(R.id.backToLoginBtn);

        // Setting text selector over textviews
        try {
            @SuppressLint("ResourceType") ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    getResources().getXml(R.drawable.text_selector));

            back.setTextColor(csl);
            submit.setTextColor(csl);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Set Listeners over buttons
    private void setListeners() {
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToLoginBtn:

                // Replace Login Fragment on Back Presses
                new loginActivity().replaceLoginFragment();
                break;

            case R.id.forgot_button:

                // Call Submit button task
                submitButtonTask();
                break;

        }

    }

    private void submitButtonTask() {
        String getEmailId = emailId.getText().toString();

       boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(getEmailId).matches();
        // First check if email id is not null else show error toast
        if (getEmailId.equals("") || getEmailId.length() == 0)

            Toast.makeText(getActivity(), "Enter email ID", Toast.LENGTH_LONG).show();
            // Check if email id is valid or not
        else if (!isValidEmail)
            Toast.makeText(getActivity(), "Invalid Email ID", Toast.LENGTH_LONG).show();
            // Else submit email id and fetch passwod or do your stuff
        else
            Toast.makeText(getActivity(), "We are working on it, kindly to contact on our contact to get a default password",
                    Toast.LENGTH_SHORT).show();
    }
}