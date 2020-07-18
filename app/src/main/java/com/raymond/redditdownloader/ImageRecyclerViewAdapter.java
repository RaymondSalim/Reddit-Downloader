package com.raymond.redditdownloader;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.internal.$Gson$Preconditions;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolderImage> {
    private LayoutInflater inflater;
    private ArrayList<MediaObjects> mediaObjects;
    private Context context;
    private AppCompatActivity appCompatActivity;

    public ImageRecyclerViewAdapter(Context context, ArrayList<MediaObjects> mediaObjects) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mediaObjects = mediaObjects;
        this.appCompatActivity = (AppCompatActivity) context;

    }

    @NonNull
    @Override
    public ImageRecyclerViewAdapter.ViewHolderImage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = inflater.inflate(R.layout.recyclerview_image, parent, false);
        return new ViewHolderImage(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageRecyclerViewAdapter.ViewHolderImage holder, int position) {
        File file = new File(mediaObjects.get(position).getMediaPath());
        Glide
            .with(context)
            .load(file)
            .fitCenter()
            .into(ViewHolderImage.img);

    }

    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }

    static class ViewHolderImage extends RecyclerView.ViewHolder implements View.OnClickListener{

        public static ImageView img;
        public static CardView cardView;

        public ViewHolderImage(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            img = itemView.findViewById(R.id.imageView);
            cardView = itemView.findViewById(R.id.cardView);

        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
        }
    }
}
