package com.raymond.redditdownloader;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    public String saveDir;
    TextView url;
    final Context context = this;
    public Dialog downloadDialog;
    private Thread download;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final redditDownloader redditDownloader = new redditDownloader(this);
        TextView url = findViewById(R.id.urlDownload);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dialog to request URL
                downloadDialog = new Dialog(context);
                downloadDialog.setContentView(R.layout.downloadalertdialog);
                downloadDialog.setTitle("Download");


                final Button downloadButton = (Button) downloadDialog.findViewById(R.id.downloadButton);
                final TextView url = (TextView) downloadDialog.findViewById(R.id.urlDownload);
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadButton.setEnabled(false);
                        Log.d("url", url.getText().toString());
                        download(url.getText().toString(), "name1231.mp4");
                    }
                });
                downloadDialog.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(); // TODO! Fix Dialog size
                lp.copyFrom(downloadDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                downloadDialog.getWindow().setAttributes(lp);
            }
        });
    }


    private void download(final String url, final String fileName) {
        // Runs on a thread to prevent error
        download = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    redditDownloader.download(url, fileName);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        download.start();
    }

}
