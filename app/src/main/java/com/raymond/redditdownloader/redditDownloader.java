package com.raymond.redditdownloader;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.TextView;
import android.widget.Toast;


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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.*;


public class redditDownloader extends MainActivity {
    private String title = null;
    private URL redditURL;
    private String redditRegex = "https?://(www.)?\\b(reddit.com|redd.it)\\b([/]?[r]?[/]?)?[-a-zA-Z0-9@:%._+~&?#/=]{1,256}";
    private String urlRegex = "(https?://){1}[-a-zA-Z0-9@:%_+~&?#./=]{1,512}";
    private Context context;
    private final OkHttpClient httpClient = new OkHttpClient();
    private String shareURL;
    private String downloadURL;
    private File newFile;



    public redditDownloader(Context context) {
        this.context = context;
    }

    public redditDownloader() {

    }


    public void download (String urlInput) throws IOException, InterruptedException {
        if (isValid(urlInput)) {
            Log.d("redditURL", "works");
            Log.d("redditURL", String.valueOf(redditURL));
            sendGET(urlInput);
        }

    }

    public boolean isValid(String url) throws MalformedURLException {
        if (isMatch(url, urlRegex)) {
            try {
                URL urlInput = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(context, "URL is invalid", Toast.LENGTH_SHORT).show();
                return false;

            }
            if (!isMatch(url, redditRegex)) {
                Toast.makeText(context, "URL is not a reddit URL", Toast.LENGTH_SHORT).show();
                return false;
            } else return true;
        } else {
            url = "https://" + url;
            TextView textView = (TextView) ((MainActivity)context).findViewById(R.id.url);
            textView.setText(url);
            return true;
        }
    }

    private boolean isMatch(String in, String pattern) {
        try {
            Pattern pattern1 = Pattern.compile(pattern);
            Matcher matcher = pattern1.matcher(in);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    private void sendGET(String url) throws IOException, InterruptedException {
        final Request request = new Request.Builder()
                .url("https://reddit.tube/parse?url=" + url)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
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
                    downloadURL = "https://cdntube2.b-cdn.net" + replaceURL(urlConnection.getURL());
                    Log.d("downloadURL", "downloadURL successful");
                    downloadVideo(downloadURL);
                    Log.d("downloadVideo", "video downloaded");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "The reddit URL is invalid", Toast.LENGTH_SHORT).show();
                }


            }
        });
        }


    private String replaceURL(URL url) throws MalformedURLException {
            return (url.getPath());
        }

    private void downloadVideo(String url) throws IOException {
            File currentFile = new File( String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES) ) ); // TODO! Fix file saving location
            String fileName = currentFile.getName();

            ContextWrapper contextWrapper = new ContextWrapper(context);
            File directory = contextWrapper.getDir("videoDir", Context.MODE_PRIVATE);

            newFile = new File(directory, fileName);
            Log.d("directory", String.valueOf(directory));


            URL dwURL = new URL(url);
            InputStream in = dwURL.openStream();
            OutputStream out = new FileOutputStream(newFile);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

        }

    }

