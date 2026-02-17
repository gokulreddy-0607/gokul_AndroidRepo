package com.example.dapp_b;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.List;

public class SathramAdapter extends RecyclerView.Adapter<SathramAdapter.SathramViewHolder> {

    private final List<Sathram> sathramList;
    private final Context context;

    public SathramAdapter(Context context, List<Sathram> sathramList) {
        this.context = context;
        this.sathramList = sathramList;
    }

    @NonNull
    @Override
    public SathramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sathram, parent, false);
        return new SathramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SathramViewHolder holder, int position) {
        Sathram sathram = sathramList.get(position);
        holder.tvName.setText(sathram.getName());
        holder.tvYear.setText("Year: " + sathram.getYear());
        holder.tvCost.setText("Cost: ₹" + sathram.getStartCost() + " - ₹" + sathram.getEndCost());
        if (sathram.getRating() != null) {
            holder.ratingBar.setRating(sathram.getRating().floatValue());
        }
        holder.tvDescription.setText("Description: " + sathram.getDescription());
        holder.tvWebsite.setText("Website: " + sathram.getWebsite());
        holder.tvPhone.setText("Phone: " + sathram.getPhone());
        holder.tvBusDistance.setText("Bus Distance: " + sathram.getBusDistance());
        holder.tvRailDistance.setText("Rail Distance: " + sathram.getRailDistance());
        holder.tvStayType.setText("Stay Type: " + sathram.getStayType());
        holder.tvCheckInTime.setText("Check-in: " + sathram.getCheckInTime());
        holder.tvCheckOutTime.setText("Check-out: " + sathram.getCheckOutTime());


        holder.cbAc.setChecked(sathram.isAcRooms());
        holder.cbNonAc.setChecked(sathram.isNonAcRooms());
        holder.cbHotWater.setChecked(sathram.isHotWater());
        holder.cbParking.setChecked(sathram.isParking());

        if (sathram.getImageUrls() != null && !sathram.getImageUrls().isEmpty()) {
            ImageViewPagerAdapter adapter = new ImageViewPagerAdapter(context, sathram.getImageUrls());
            holder.viewPager.setAdapter(adapter);

            holder.btnPrev.setOnClickListener(v -> {
                int currentItem = holder.viewPager.getCurrentItem();
                if (currentItem > 0) {
                    holder.viewPager.setCurrentItem(currentItem - 1);
                }
            });

            holder.btnNext.setOnClickListener(v -> {
                int currentItem = holder.viewPager.getCurrentItem();
                if (currentItem < adapter.getItemCount() - 1) {
                    holder.viewPager.setCurrentItem(currentItem + 1);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return sathramList.size();
    }

    static class SathramViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvYear, tvCost, tvDescription, tvWebsite, tvPhone, tvBusDistance, tvRailDistance, tvStayType, tvCheckInTime, tvCheckOutTime;
        RatingBar ratingBar;
        ViewPager2 viewPager;
        ImageButton btnPrev, btnNext;
        AppCompatCheckBox cbAc, cbNonAc, cbHotWater, cbParking;

        public SathramViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvYear = itemView.findViewById(R.id.tv_year);
            tvCost = itemView.findViewById(R.id.tv_cost);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvWebsite = itemView.findViewById(R.id.tv_website);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvBusDistance = itemView.findViewById(R.id.tv_bus_distance);
            tvRailDistance = itemView.findViewById(R.id.tv_rail_distance);
            tvStayType = itemView.findViewById(R.id.tv_stay_type);
            tvCheckInTime = itemView.findViewById(R.id.tv_check_in_time);
            tvCheckOutTime = itemView.findViewById(R.id.tv_check_out_time);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            viewPager = itemView.findViewById(R.id.viewPager);
            btnPrev = itemView.findViewById(R.id.btn_prev);
            btnNext = itemView.findViewById(R.id.btn_next);
            cbAc = itemView.findViewById(R.id.cb_ac);
            cbNonAc = itemView.findViewById(R.id.cb_non_ac);
            cbHotWater = itemView.findViewById(R.id.cb_hot_water);
            cbParking = itemView.findViewById(R.id.cb_parking);
        }
    }
}

class ImageViewPagerAdapter extends RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewHolder> {

    private final Context context;
    private final List<String> imageUrls;

    public ImageViewPagerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ImageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(context).load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}