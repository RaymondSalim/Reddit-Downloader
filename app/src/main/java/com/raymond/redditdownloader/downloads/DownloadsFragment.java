package com.raymond.redditdownloader.downloads;

import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.raymond.redditdownloader.MainActivity;
import com.raymond.redditdownloader.MediaDownloader;
import com.raymond.redditdownloader.MediaObjects;
import com.raymond.redditdownloader.R;
import com.raymond.redditdownloader.downloads.ImageRecyclerViewAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DownloadsFragment extends Fragment {
    public Dialog downloadDialog;
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
        final MediaDownloader MediaDownloader = new MediaDownloader(getContext(), false);

        Toolbar mToolbar = view.findViewById(R.id.toolbarDownloads);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);


            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView = view.findViewById(R.id.RecyclerViewImage);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new ImageRecyclerViewAdapter(getContext());
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
                final Button downloadButton = downloadDialog.findViewById(R.id.downloadButton);
                final TextView url = downloadDialog.findViewById(R.id.urlDownload);

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

                // Notifies the adapter that new media is inserted
                downloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        notifyAdapter(-2);
                    }
                });
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 1) {
                Log.d("TAG", "onActivityResult: called");
                // Deletes and restarts the adapter so that file is removed from RecyclerView
                // Used when media is deleted from MediaViewer activity
//            imageDirList = prepareData();
//            mAdapter = new ImageRecyclerViewAdapter(getContext());
//            recyclerView.setAdapter(mAdapter);
                int position = data.getIntExtra("position", -1);
                notifyAdapter(position);
            }
        }
    }



    private void download(final String url) {
        // Runs on a thread to prevent error
        Thread download = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MediaDownloader.download(url);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        download.start();
    }



    public void notifyAdapter(int position) {
        if (position != -1) {
            mAdapter.itemRemoved(position);
            mAdapter.notifyItemRemoved(position);
        }
    }

}