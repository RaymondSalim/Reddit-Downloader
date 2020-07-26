package com.raymond.redditdownloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class ShareDownload extends AppCompatActivity {
    private File outputFile;
    private redditDownloader downloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_download);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        downloader = new redditDownloader(this, true);


        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equalsIgnoreCase(type)) {
                try {
                    handleSendText(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    void handleSendText(Intent intent) throws IOException, InterruptedException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText != null) {
            outputFile = redditDownloader.download(sharedText);
        }
        finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "onDestroy: " + outputFile.getName());

        // To share file
        Intent shareIntent = new Intent();
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/* video/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider",outputFile));
        startActivity(Intent.createChooser(shareIntent, "Share Media"));
    }
}