package com.my.shopee.myshopee.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.my.shopee.myshopee.Activities.MainActivity;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;

import java.io.File;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class Tab1 extends Fragment {

    AppCompatButton logoutButton;
    SharedPreferences userPreferences;
    private View view;
    public Tab1() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab1, container, false);
        initView();
        return view;
    }

    private void initView() {
        logoutButton = view.findViewById(R.id.logout_button);
        userPreferences = getContext().getSharedPreferences(Constants.userDetailsSharedPreferences, MODE_PRIVATE);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void logout() {
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
            dialog.setTitle(R.string.logout_title);
            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // A method to clear all the application data, returns if files are deleted or not
                    boolean successful = clearApplicationData();
                    if(successful) {
                        // A method to clear shared preferences loaded on cache
                        userPreferences.edit().clear().apply();
                        Log.i("username", userPreferences.getString("username", "null"));
                        Intent i = new Intent(getContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        // Toast error if it is un successful
                        Toast.makeText(getContext(), "Error code 3001", Toast.LENGTH_LONG).show();
                        Log.i("Logout error", "unable to delete files");
                    }
                }
            });
            dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // To dismiss the dialog
                    dialog.dismiss();
                }
            });
            // Set warning message to delete un synced forms message
            dialog.setMessage("Are you sure you want to logout?");
            dialog.show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error code 3002", Toast.LENGTH_LONG).show();
            Log.i("Logout error", e.toString());
        }
    }

    public boolean clearApplicationData() {
        boolean successfullyDeleted = true;
        // Get the cache directory
        File cacheDirectory = Objects.requireNonNull(getContext()).getCacheDir();
        // Get back to find the application folder
        File applicationDirectory = new File(cacheDirectory.getParent());
        // Check for application directory if it exists
        if (applicationDirectory.exists()) {
            // Get all the file names of that directory
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    // Delete all the files if it is not lib directory
                    successfullyDeleted = deleteFile(new File(applicationDirectory, fileName)) && successfullyDeleted;
                }
            }
        }
        return successfullyDeleted;
    }

    // A method to delete that file or directory
    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            // If the given file is a directory
            if (file.isDirectory()) {
                // Get all the files inside that directory
                String[] children = file.list();
                for (String aChildren : children) {
                    // Delete children file
                    deletedAll = deleteFile(new File(file, aChildren)) && deletedAll;
                }
            } else {
                // if it is a file then directly delete it
                deletedAll = file.delete();
            }
        }
        return deletedAll;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
