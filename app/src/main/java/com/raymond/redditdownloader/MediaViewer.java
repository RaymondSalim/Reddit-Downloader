package com.raymond.redditdownloader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

public class MediaViewer extends AppCompatActivity {

    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_viewer);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        filePath = bundle.getString("filePath");

        final Toolbar toolbar = findViewById(R.id.toolbarMedia);





        // Grabs file extension
        String fileType = filePath.substring(filePath.length()-4);
        Log.d("filetype", fileType);

        toolbar.inflateMenu(R.menu.mediaviewer);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            File file = new File(filePath);
            switch (item.getItemId()) {
                case (R.id.delete):
                    // Deletes file
                    file.delete();

                    Toast.makeText(getApplicationContext(), "File deleted", Toast.LENGTH_SHORT).show();

                    // Closes the activity
                    finish();
                    return true;
                case (R.id.share):
                    Intent shareIntent = new Intent();
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("image/* video/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file));
                    startActivity(Intent.createChooser(shareIntent, "Share Media"));
            }
            return false;
        });

        if (fileType.matches(".png|.jpg|.jpeg|.gif")) {
            ImageView imageView = findViewById(R.id.imageFull);
            imageView.setVisibility(View.VISIBLE);

            Glide
                    .with(this)
                    .load(new File(filePath))
                    .listener(requestListener)
                    .error(R.drawable.ic_baseline_image_24)
                    .into(imageView);

        } else {
            VideoView videoView = findViewById(R.id.videoFull);

            final MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);

            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPath(filePath);
            videoView.setMediaController(mediaController);
            videoView.start();
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (toolbar.getVisibility()) {
                        case (View.VISIBLE):
                            toolbar.setVisibility(View.INVISIBLE);
                            mediaController.hide();
                            break;
                        case (View.INVISIBLE):
                            toolbar.setVisibility(View.VISIBLE);
                            mediaController.show();
                            break;
                    }
                }
            });
        }
    }

    // Listener for Glide
    private RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            Toast.makeText(MediaViewer.this, "File is corrupted", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            return false;
        }
    };
}