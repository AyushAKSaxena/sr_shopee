package com.my.shopee.myshopee.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

public class StoresAdapter extends RecyclerView.Adapter<StoresAdapter.StoresViewHolder> {

    private List<String> names;
    private List<String> icons;
    private Context context;

    public StoresAdapter(List<String> storesType, List<String> storesIcons, Context context) {
        names = storesType;
        icons = storesIcons;
        this.context = context;
    }

    @NonNull
    @Override
    public StoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stores_list,parent, false);
        return new StoresViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StoresViewHolder holder, int position) {
        holder.storeCard.setVisibility(View.VISIBLE);
        holder.storeName.setText(names.get(position));

        try {
            String url = Constants.storesCategoryBaseURL + icons.get(position);
            Log.i("URL", url);
            Glide.with(context).load(url).into(holder.storeIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    class StoresViewHolder extends RecyclerView.ViewHolder {

        CardView storeCard;
        ImageView storeIcon;
        TextView storeName;
        StoresViewHolder(View itemView) {
            super(itemView);
            storeCard = itemView.findViewById(R.id.stores_activity_card);
            storeIcon = itemView.findViewById(R.id.stores_adapter_icon);
            storeName = itemView.findViewById(R.id.stores_adapter_name);
        }
    }
}
