package com.raymond.redditdownloader;

import android.content.Context;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.*;


public class redditDownloader extends MainActivity{
    private String title = null;
    private URL redditURL;
    private String redditRegex = "https?://(www.)?\\b(reddit.com|redd.it)\\b([/]?[r]?[/]?)?[-a-zA-Z0-9@:%._+~&?#/=]{1,256}";
    private String urlRegex = "(https?://){1}[-a-zA-Z0-9@:%_+~&?#/=]{1,512}";
    private Context context;
    private final OkHttpClient httpClient = new OkHttpClient();
    private String shareURL;


    public redditDownloader(Context context) {
        this.context = context;
    }

    public redditDownloader() {

    }


    public void download (String urlInput) throws IOException {
        if (isValid(urlInput)) {
            redditURL = new URL(urlInput);
            sendGET(redditURL);


        }

    }

    public boolean isValid(String url) {
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
            isValid(url);
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

    private void sendGET(URL url) throws IOException {
        final Request request = new Request.Builder()
                .url("https://reddit.tube/parse")
                .addHeader("url", url.toString())
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
                    shareURL = jsonObject.getString("share_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // shareURL = jsonObject.get("share_url").getAsString();


            }
        });
        }
    }

