package com.raymond.redditdownloader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

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
    public void onBindViewHolder(@NonNull ImageRecyclerViewAdapter.ViewHolderImage holder, final int position) {
        File file = new File(mediaObjects.get(position).getMediaPath());
        Glide
            .with(context)
            .load(file)
            .error(R.drawable.ic_baseline_image_24)
            .fitCenter()
            .into(ViewHolderImage.img);

        // Opens new activity to display image in fullscreen
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("filePath", mediaObjects.get(position).getMediaPath());
                bundle.putInt("position", position);


                Intent intent = new Intent(context, MediaViewer.class);
                intent.putExtras(bundle);
                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }


    static class ViewHolderImage extends RecyclerView.ViewHolder{

        public static ImageView img;
        public static CardView cardView;

        public ViewHolderImage(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView);
            cardView = itemView.findViewById(R.id.cardView);

        }

    }
}
