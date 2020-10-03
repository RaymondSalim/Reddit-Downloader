package com.raymond.redditdownloader.share;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import com.raymond.redditdownloader.MediaDownloader;
import com.raymond.redditdownloader.R;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class DownloadDialogFragment extends DialogFragment {

    public DownloadDialogFragment() {}
    
    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.download_dialog, null))
                .show();
        return builder.create();
    }

}