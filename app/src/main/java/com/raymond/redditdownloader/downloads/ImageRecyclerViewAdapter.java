package com.raymond.redditdownloader.downloads;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raymond.redditdownloader.MediaViewer;
import com.raymond.redditdownloader.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolderImage> {
    private final LayoutInflater inflater;
    private ArrayList<File> mediaObjects;
    private final Context context;
    private final Fragment downloadFragment;


    public ImageRecyclerViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mediaObjects = mediaObjects;

        downloadFragment = ((AppCompatActivity)context).getSupportFragmentManager().findFragmentByTag("fragmentDownload");

        mediaObjects = prepareData();

    }

    @NonNull
    @Override
    public ImageRecyclerViewAdapter.ViewHolderImage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = inflater.inflate(R.layout.recyclerview_image, parent, false);
        return new ViewHolderImage(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageRecyclerViewAdapter.ViewHolderImage holder, final int position) {
//        File file = new File(mediaObjects.get(position).getMediaPath());
        File file = mediaObjects.get(position);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.fromFile(file))
                .setAutoPlayAnimations(true)
                .build();

        Float aspectRatio = getAspectRatio(file);

        holder.draweeView.setAspectRatio(aspectRatio);

        holder.draweeView.setController(
                Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.fromFile(file))
                        .setAutoPlayAnimations(true)
                        .build());

        // Opens new activity to display image in fullscreen
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
//            bundle.putString("filePath", mediaObjects.get(position).getMediaPath());
            bundle.putString("filePath", file.getAbsolutePath());
            bundle.putInt("position", position);
            bundle.putFloat("aspectRatio", aspectRatio);


            Intent intent = new Intent(context, MediaViewer.class);
            intent.putExtras(bundle);

            downloadFragment.startActivityForResult(intent, 1);
        });

    }


    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }

    public float getAspectRatio(File file) {
        float width, height;
        String name = file.getName();
        String extension = name.substring(name.length()-3);

        if (extension.contains("mp4")) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(file.getAbsolutePath());

            width = Float.parseFloat(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            height = Float.parseFloat(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

            return (width/height);
        } else {
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        return ((float) options.outWidth / options.outHeight);
        }
    }


    static class ViewHolderImage extends RecyclerView.ViewHolder{

        public CardView cardView;
        public SimpleDraweeView draweeView;


        public ViewHolderImage(@NonNull View itemView) {
            super(itemView);

            draweeView = itemView.findViewById(R.id.imageView);
        }

    }

    private ArrayList prepareData() {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getExternalFilesDir(null);

        File[] files = directory.listFiles();

        ArrayList mediaDirList = new ArrayList<>();

        for (File f: files) {
            mediaDirList.add(f);
        }

        Collections.sort(mediaDirList, Comparator.comparingLong(File::lastModified));
        Collections.reverse(mediaDirList);

        return mediaDirList;
    }

    public void itemRemoved(int position) {
        if (position == -2) {
            // New file is added
            mediaObjects = prepareData();
            this.notifyDataSetChanged();

        } else {
            this.mediaObjects.remove(position);
            this.notifyItemRemoved(position);
            this.notifyDataSetChanged();
        }

    }
}
