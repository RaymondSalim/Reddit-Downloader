package com.raymond.redditdownloader;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.MainThread;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Documented;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.BreakIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.*;


public class redditDownloader extends MainActivity {
    private static Context context;
    private static String redditRegex = "https?://(www.)?\\b(reddit.com|redd.it)\\b([/]?[r]?[/]?)?[-a-zA-Z0-9@:%._+~&?#/=]{1,256}";
    private static String urlRegex = "(https?://){1}[-a-zA-Z0-9@:%_+~&?#./=]{1,512}";
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static String shareURL;
    private static String downloadURL;
    private static File outputFile;



    public redditDownloader(Context context) {
        this.context = context;
    }

    public redditDownloader() {

    }


    public static void download(String urlInput, String fileName) throws IOException, InterruptedException {
        TextView textView = (TextView) ((MainActivity)context).downloadDialog.findViewById(R.id.urlDownload);
        if (isValid(textView.getText().toString())) {
            sendGET(urlInput, fileName);
        }
    }

    public static boolean isValid(String url) throws MalformedURLException {

        if (Looper.myLooper() == null) Looper.prepare();
        if (isMatch(url, urlRegex)) {
            try {
                URL urlInput = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(context, "URL is invalid", Toast.LENGTH_SHORT).show();
                enableButton();
                return false;

            }
            if (!isMatch(url, redditRegex)) {
                Toast.makeText(context, "URL is not a reddit URL", Toast.LENGTH_SHORT).show();
                enableButton();
                return false;
            } else return true;

        } else if (url.length() == 0) {
            Toast.makeText(context, "Please enter the URL", Toast.LENGTH_SHORT).show();
            Log.d("url", "is empty");
            enableButton();
            return false;

        } else if (!url.substring(0,4).equalsIgnoreCase("http")) {
            url = "https://" + url;
            setURL(url);
            isValid(url);
            return false;

        } else {
            return false;
        }
    }


    private static boolean isMatch(String in, String pattern) {
        try {
            Pattern pattern1 = Pattern.compile(pattern);
            Matcher matcher = pattern1.matcher(in);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static void sendGET(String url, final String fileName) throws IOException, InterruptedException {

        final Request request = new Request.Builder()
                .url("https://reddit.tube/parse?url=" + url)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                enableButton();
            }


            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                response = httpClient.newCall(request).execute();
                Gson gson = new Gson();
                String jsonData = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    Log.d("jsonResponse", jsonData);
                    shareURL = jsonObject.getString("share_url");
                    URLConnection urlConnection = new URL(shareURL).openConnection();
                    urlConnection.connect();
                    urlConnection.getInputStream();
                    Log.d("URLCONNECTION", String.valueOf(urlConnection.getURL()));
                    Log.d("replaceURL", replaceURL(urlConnection.getURL()));
                    downloadURL = "https://cdntube2.b-cdn.net/mp4/" + replaceURL(urlConnection.getURL()) ; // Fix
                    Log.d("downloadURL", downloadURL);
                    downloadVideo(downloadURL, fileName);
                    Log.d("downloadVideo", "video downloaded");
                    // ((MainActivity)context).downloadDialog.dismiss();




                } catch (JSONException e) {
                    if (Looper.myLooper() == null) Looper.prepare();
                    e.printStackTrace();
                    Toast.makeText(context, "The reddit URL is invalid", Toast.LENGTH_SHORT).show();
                    enableButton();
                }


            }
        });
    }


    private static String replaceURL(URL url) throws MalformedURLException {
        String temp =  (url.getPath().toString());
        String[] split = temp.split("/");
        return split[2];
    }

    private static void downloadVideo(String url, String fileName) throws IOException {
            // File currentFile = new File(String.valueOf(context.getExternalFilesDir(null) ) ); // TODO! Fix file saving location
        URL dwURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) dwURL.openConnection();
        connection.connect();

        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getExternalFilesDir(null);

        outputFile = new File(directory, fileName);
        Log.d("directory", String.valueOf(directory));

        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        InputStream in = connection.getInputStream();

        // Copy the bits from instream to outstream
        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0) {
            fileOutputStream.write(buf, 0, len);
        }

        in.close();
        fileOutputStream.close();

    }

    private static void enableButton() {
        final Button downloadButton = (Button) ((MainActivity)context).downloadDialog.findViewById(R.id.downloadButton);
        ((MainActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                downloadButton.setEnabled(true);
            }
        });
    }

    private static void setURL(final String url) {
        final TextView textView = (TextView) ((MainActivity) context).downloadDialog.findViewById(R.id.urlDownload);
        ((MainActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(url);
            }
        });
    }





}

