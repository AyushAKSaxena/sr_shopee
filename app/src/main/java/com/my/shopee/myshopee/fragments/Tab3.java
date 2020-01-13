package com.my.shopee.myshopee.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.my.shopee.myshopee.R;

import java.util.Objects;

public class Tab3 extends Fragment implements View.OnClickListener {

    private View view;

    private TextInputEditText contactEditText;
    private AppCompatButton sendOTPButton;
    public Tab3() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab3, container, false);
        initViews();
        initListeners();
        return view;
    }

    private void initListeners() {
        sendOTPButton.setOnClickListener(this);
    }

    private void initViews() {
        contactEditText = view.findViewById(R.id.otp_contact_edit_text);
        sendOTPButton = view.findViewById(R.id.send_otp_button);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_otp_button:
                sendOTP();
            default:

        }
    }

    private void sendOTP() {
        boolean contactCorrection = verifyContact();
        if(contactCorrection) {
            Toast.makeText(getContext(), "OTP Sent", Toast.LENGTH_LONG).show();
        }
    }

    private boolean verifyContact() {
        String contactNumber = Objects.requireNonNull(contactEditText.getText()).toString();
        if(contactNumber.equals("") || contactNumber.length() == 0) {
            Toast.makeText(getContext(), "Please enter contact number", Toast.LENGTH_SHORT).show();
        } else if ( contactNumber.length() != 10) {
            Toast.makeText(getContext(), "Invalid contact number", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }
}
