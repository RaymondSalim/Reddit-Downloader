package com.raymond.redditdownloader;

import android.app.Dialog;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DownloadsFragment extends Fragment {
    public Dialog downloadDialog;
    private Thread download;
    private RecyclerView recyclerView;
    private ImageRecyclerViewAdapter mAdapter;
    public StaggeredGridLayoutManager layoutManager;
    public ArrayList imageDirList;


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



        imageDirList = prepareData();
        Log.d("imDirList", String.valueOf(imageDirList.size()));

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView = view.findViewById(R.id.RecyclerViewImage);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ImageRecyclerViewAdapter(getContext(), imageDirList);
        recyclerView.setAdapter(mAdapter);


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

    private ArrayList prepareData() {
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File directory = contextWrapper.getExternalFilesDir(null);

        File[] files = directory.listFiles();

        ArrayList mediaDirList = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            MediaObjects mediaDir = new MediaObjects();
            mediaDir.setMediaPath(files[i].getAbsolutePath());
            mediaDirList.add(mediaDir);
        }
        return mediaDirList;
    }

    public void notifyAdapter() {
        mAdapter.notifyDataSetChanged();
    }

}