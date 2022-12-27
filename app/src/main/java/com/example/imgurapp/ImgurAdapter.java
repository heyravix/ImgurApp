package com.example.imgurapp;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.imgurapp.model.ImgurData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ImgurAdapter extends RecyclerView.Adapter<ImgurAdapter.ViewHolder> {
    private List<ImgurData> imgurData;
    private Context context;

    private boolean isGrid;

    public ImgurAdapter(List<ImgurData> list_data, Context context, boolean isGrid) {
        this.imgurData = list_data;
        this.context = context;
        this.isGrid = isGrid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        /**
         * Setting layout for recyclerview according to GridView and ListView
         */

        if (isGrid) {
            view = inflater.inflate(R.layout.data_grid, parent, false);
        } else {
            view = inflater.inflate(R.layout.data_list, parent, false);
        }
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ImgurData data = imgurData.get(position);
        String imgurTitle = data.getTitle();
        String imgurDateTime = data.getDatetime();
        String imgurimageCount = data.getTotalImages();

        holder.title.setText(imgurTitle);
        if ( Integer.parseInt(imgurimageCount) > 1 ){
            holder.totalImages.setVisibility(View.VISIBLE);

            if (isGrid) {
                holder.totalImages.setText(imgurimageCount);
            } else {
                holder.totalImages.setText(imgurimageCount + " Images");

            }

        }

        Glide.with(context)
                .load(data
                        .getImageUrl())
                 .into(holder.image);


        /**
         * Converting Date of Post into (DD/MM/YY hh:mm am/pm) format
         */

        long timestamp = Long.parseLong(imgurDateTime);
        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm a");
        String formattedDate = dateFormat.format(date);
        holder.datetime.setText(formattedDate);


    }

    @Override
    public int getItemCount() {
        return imgurData.size();
    }

    public void setIsGrid(boolean isGrid) {
        this.isGrid = isGrid;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView datetime;
        private TextView title;
        private TextView totalImages;

         public ViewHolder(View itemView) {
            super(itemView);

             title=(TextView) itemView.findViewById(R.id.title);
             datetime=(TextView) itemView.findViewById(R.id.date_time);
             image = (ImageView) itemView.findViewById(R.id.image);
             totalImages = (TextView) itemView.findViewById(R.id.total_images);

         }
    }
}
