package com.my.shopee.myshopee.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.my.shopee.myshopee.Activities.StoreInnerActivity;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;

import java.util.List;

public class StoresInnerAdapter extends RecyclerView.Adapter<StoresInnerAdapter.StoresInnerViewHolder> {

    private Context context;
    private List<String> storeName, storeAddress, storeTimings, storeImage, storeContact;

    public StoresInnerAdapter(Context context, List<String> storeName, List<String> storeAddress, List<String> storeTimings, List<String> storeImage, List<String> storeContact) {
        this.context = context;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeTimings = storeTimings;
        this.storeImage = storeImage;
        this.storeContact = storeContact;
    }

    @NonNull
    @Override
    public StoresInnerAdapter.StoresInnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_store_inner,parent, false);
        return new StoresInnerAdapter.StoresInnerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final StoresInnerAdapter.StoresInnerViewHolder holder, int position) {
        holder.storeName.setText(storeName.get(position));
        holder.storeAddress.setText(storeAddress.get(position));
        holder.storeContact.setText(storeContact.get(position));
        holder.storeTimings.setText("Timing:"+storeTimings.get(position));
        holder.storeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactNumber = "tel:";
                int position = holder.getAdapterPosition();
                Log.i("contact no", storeContact.get(position));
                if(!storeContact.get(position).contains("+91")) {
                    contactNumber += "+91";
                }
                contactNumber += storeContact.get(position);
                makePhoneCall(contactNumber);
            }
        });

        try {
            String imagePath = Constants.storeImagesBaseURL + storeImage.get(position);
            Glide.with(context).load(imagePath).into(holder.storeImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.storeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                String contactNumber = "+91" + storeContact.get(holder.getAdapterPosition());
                callIntent.setData(Uri.parse("tel:" + contactNumber));
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                            android.Manifest.permission.CALL_PHONE)) {
                    } else {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{android.Manifest.permission.CALL_PHONE}, 3);
                        return;
                    }
                }
                context.startActivity(callIntent);
            }
        });
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
            }
        }
        context.startActivity(callIntent);
    }

    @Override
    public int getItemCount() {
        return storeName.size();
    }

    class StoresInnerViewHolder extends RecyclerView.ViewHolder {

        ImageView storeImage;
        TextView storeName, storeAddress, storeContact, storeTimings;
        StoresInnerViewHolder(View itemView) {
            super(itemView);
            storeImage = itemView.findViewById(R.id.inner_store_adapter_image);
            storeName = itemView.findViewById(R.id.inner_store_restaurant_name);
            storeContact = itemView.findViewById(R.id.inner_store_adapter_contact);
            storeAddress = itemView.findViewById(R.id.inner_store_restaurant_address);
            storeTimings = itemView.findViewById(R.id.inner_store_restaurant_timings);
        }
    }
}
