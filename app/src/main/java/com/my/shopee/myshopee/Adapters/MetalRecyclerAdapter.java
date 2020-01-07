package com.my.shopee.myshopee.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.my.shopee.myshopee.Activities.MainActivity;
import com.my.shopee.myshopee.Activities.PosterDetailsActivity;
import com.my.shopee.myshopee.Utilities.Constants;
import com.my.shopee.myshopee.Utilities.MetalRecyclerViewPager;
import com.my.shopee.myshopee.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

    public class MetalRecyclerAdapter extends MetalRecyclerViewPager.MetalAdapter<MetalRecyclerAdapter.FullMetalViewHolder> {

        private final List<String> metalList;
        private final List<JSONObject> posterDetails;
        private Context context;

        public MetalRecyclerAdapter(@NonNull DisplayMetrics metrics, @NonNull List<String> metalList, List<JSONObject> posterDetails, Context context) {
            super(metrics);
            this.metalList = metalList;
            this.context = context;
            this.posterDetails = posterDetails;
        }

        @NonNull
        @Override
        public FullMetalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pager_item, parent, false);
            return new FullMetalViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(@NonNull final FullMetalViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            Log.d("position", Integer.toString(position));
            String imagePath = Constants.posterImageBaseURL + metalList.get(position);
            Glide.with(context).load(imagePath).into(holder.metalText);
            holder.metalCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject selectedPoster = posterDetails.get(holder.getAdapterPosition());
                    Intent posterDetails = new Intent(context, PosterDetailsActivity.class);
                    posterDetails.putExtra("details", selectedPoster.toString());
                    context.startActivity(posterDetails);
                }
            });
        }

        @Override
        public int getItemCount() {
            Log.d("poster", Integer.toString(metalList.size()));
            return metalList.size();
        }

        class FullMetalViewHolder extends MetalRecyclerViewPager.MetalViewHolder {

            ImageView metalText;
            CardView metalCard;
            FullMetalViewHolder(View itemView) {
                super(itemView);
                metalText = itemView.findViewById(R.id.metal_text);
                metalCard = itemView.findViewById(R.id.metal_card);
            }
        }
    }