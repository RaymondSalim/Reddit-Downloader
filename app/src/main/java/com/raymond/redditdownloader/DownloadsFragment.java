package com.raymond.redditdownloader;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class DownloadsFragment extends Fragment {
    public Dialog downloadDialog;
    private Thread download;
    // public TextView url;
    // public Button downloadButton;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.fragment_downloads, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final redditDownloader redditDownloader = new redditDownloader(((MainActivity)getContext()));

        // FAB
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dialog to request URL
                downloadDialog = new Dialog(getContext());
                downloadDialog.setContentView(R.layout.downloadalertdialog);
                downloadDialog.setTitle("Download");
                final Button downloadButton = (Button) downloadDialog.findViewById(R.id.downloadButton);
                final TextView url = (TextView) downloadDialog.findViewById(R.id.urlDownload);
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadButton.setEnabled(false);
                        Log.d("url", url.getText().toString());
                        download(url.getText().toString());
                    }
                });
                downloadDialog.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(); // TODO! Fix Dialog size
                lp.copyFrom(downloadDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                downloadDialog.getWindow().setAttributes(lp);
            }
        });

    }


    private void download(final String url) {
        // Runs on a thread to prevent error
        download = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    redditDownloader.download(url);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        download.start();
    }

}