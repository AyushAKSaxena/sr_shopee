package com.my.shopee.myshopee.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.my.shopee.myshopee.R;
import com.my.shopee.myshopee.Utilities.Constants;

import java.util.List;

public class servicesInnerAdapter extends RecyclerView.Adapter<servicesInnerAdapter.servicesInnerViewHolder> {
    private Context context;
    private List<String> storeName, storeAddress, storeTimings, storeImage, storeContact;

    public servicesInnerAdapter(Context context, List<String> storeName, List<String> storeAddress, List<String> storeTimings, List<String> storeImage, List<String> storeContact) {
        this.context = context;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeTimings = storeTimings;
        this.storeImage = storeImage;
        this.storeContact = storeContact;
    }

    @NonNull
    @Override
    public servicesInnerAdapter.servicesInnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_store_inner,parent, false);
        return new servicesInnerAdapter.servicesInnerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final servicesInnerAdapter.servicesInnerViewHolder holder, int position) {
        holder.storeName.setText(storeName.get(position));
        holder.storeAddress.setText(storeAddress.get(position));
        holder.storeContact.setText(storeContact.get(position));

        try {
            String imagePath = Constants.serviceProviderImageURL + storeImage.get(position);
            Glide.with(context).load(imagePath).into(holder.storeImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    class servicesInnerViewHolder extends RecyclerView.ViewHolder {

        ImageView storeImage;
        TextView storeName, storeAddress, storeContact;
        servicesInnerViewHolder(View itemView) {
            super(itemView);
            storeImage = itemView.findViewById(R.id.inner_store_adapter_image);
            storeName = itemView.findViewById(R.id.inner_store_restaurant_name);
            storeContact = itemView.findViewById(R.id.inner_store_adapter_contact);
            storeAddress = itemView.findViewById(R.id.inner_store_restaurant_address);
        }
    }
}
