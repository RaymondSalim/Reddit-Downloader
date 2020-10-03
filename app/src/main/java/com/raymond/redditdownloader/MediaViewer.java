package com.raymond.redditdownloader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

public class MediaViewer extends AppCompatActivity {

    private String filePath;
    private int position;
    private float aspectRatio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_viewer);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        filePath = bundle.getString("filePath");
        position = bundle.getInt("position");
        aspectRatio = bundle.getFloat("aspectRatio");


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

                    Intent deleteIntent = new Intent();
                    deleteIntent.putExtra("position", position);
                    setResult(RESULT_OK, deleteIntent);


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
            SimpleDraweeView draweeView = findViewById(R.id.imageFull);
            draweeView.setVisibility(View.VISIBLE);

            draweeView.setAspectRatio(aspectRatio);

            draweeView.setController(
                    Fresco.newDraweeControllerBuilder()
                            .setUri(Uri.fromFile(new File(filePath)))
                            .setAutoPlayAnimations(true)
                            .build());

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
}