package com.my.shopee.myshopee.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;

import java.util.List;

public class PropertyInnerAdapter extends RecyclerView.Adapter<PropertyInnerAdapter.propertyViewHolder> {
    private List<String> propertyName, propertyAddress, propertyFeatures, propertyPrice, propertyImage, propertyContact, propertyOwnerID;
    private Context context;
    String contact;

    public PropertyInnerAdapter(Context context, List<String> propertyName, List<String> propertyAddress, List<String> propertyFeatures, List<String> propertyPrice, List<String> propertyImage, List<String> propertyContact, List<String> propertyOwnerID) {
        this.context = context;
        this.propertyName = propertyName;
        this.propertyAddress = propertyAddress;
        this.propertyContact = propertyContact;
        this.propertyFeatures = propertyFeatures;
        this.propertyPrice = propertyPrice;
        this.propertyOwnerID = propertyOwnerID;
        this.propertyImage = propertyImage;
    }

    @NonNull
    @Override
    public PropertyInnerAdapter.propertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_property_inner, parent, false);
        return new propertyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final propertyViewHolder holder, final int position) {
        holder.propertyPriceTextView.setText(propertyPrice.get(position));
        holder.propertyNameTextView.setText(propertyName.get(position));
        holder.propertyFeaturesTextView.setText(propertyFeatures.get(position));
        holder.propertyAddressTextView.setText(propertyAddress.get(position));

        holder.callOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactNumber = "tel:";
                int position = holder.getAdapterPosition();
                Log.i("contact no", propertyContact.get(position));
                if(!propertyContact.get(position).contains("+91")) {
                    contactNumber += "+91";
                }
                contactNumber += propertyContact.get(position);
                contact = contactNumber;
                makePhoneCall(contactNumber);
            }
        });
        holder.viewOwnerDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ownerID = propertyOwnerID.get(holder.getAdapterPosition());
                openOwnerDetails(ownerID);
            }
        });

        try {
            String imagePath = Constants.propertyImageBaseURL + propertyOwnerID.get(position) + "/" + propertyImage.get(position);
            Glide.with(context).load(imagePath).into(holder.propertyImageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return propertyName.size();
    }

    private void makePhoneCall(String contactNumber) {
        Log.i("Button", "call clicked");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(contactNumber));
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale( (Activity) context,
                    android.Manifest.permission.CALL_PHONE)) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{android.Manifest.permission.CALL_PHONE}, 3);
                return;
            }
        }
        context.startActivity(callIntent);
    }

    @SuppressLint("MissingPermission")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 3 && resultCode == 1) {
            Log.i("Button", "call clicked");
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(contact));
            context.startActivity(callIntent);
        }
    }

    private void openOwnerDetails(String ownerID) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setTitle("Owner Information " + ownerID);
    }

    class propertyViewHolder extends RecyclerView.ViewHolder {

        ImageView propertyImageView;
        TextView propertyNameTextView, propertyAddressTextView, propertyFeaturesTextView, propertyPriceTextView;
        Button callOwner, viewOwnerDetails;
        propertyViewHolder(View itemView) {
            super(itemView);
            propertyImageView = itemView.findViewById(R.id.inner_property_adapter_image);
            propertyAddressTextView = itemView.findViewById(R.id.inner_property_adapter_address);
            propertyFeaturesTextView = itemView.findViewById(R.id.inner_property_adapter_features);
            propertyNameTextView = itemView.findViewById(R.id.inner_property_adapter_name);
            propertyPriceTextView = itemView.findViewById(R.id.inner_property_adapter_price);
            callOwner = itemView.findViewById(R.id.property_inner_call);
            viewOwnerDetails = itemView.findViewById(R.id.property_inner_view);

            final float density = context.getResources().getDisplayMetrics().density;
            Drawable callButton = context.getResources().getDrawable(R.drawable.ic_call);
            int searchWidth = Math.round(16*density);
            int searchHeight = Math.round(16 * density);
            callButton.setBounds(0, 0, searchWidth, searchHeight);
            callOwner.setCompoundDrawables(callButton, null, null, null);
            Drawable viewButton = context.getResources().getDrawable(R.drawable.ic_eye);
            viewButton.setBounds(0, 0, searchWidth, searchHeight);
            viewOwnerDetails.setCompoundDrawables(viewButton, null, null, null);
        }
    }
}
