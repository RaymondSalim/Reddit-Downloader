package com.raymond.redditdownloader;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class redditDownloader extends MainActivity {
    private static redditDownloader instance = null;
    private static Context context;
    private static String redditRegex = "https?://(www.)?\\b(reddit.com|redd.it)\\b\\b(/r/)\\b[-a-zA-Z0-9@:%._+~&?#/=]{1,512}";
    private static String urlRegex = "(https?://){1}[-a-zA-Z0-9@:%_+~&?#./=]{1,512}";
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static String shareURL;
    private static File outputFile;
    private static AppCompatActivity appCompatActivity;
    private static DownloadsFragment fragment;


    public redditDownloader(Context context) {
        this.context = context;
        appCompatActivity = (AppCompatActivity) context;
        fragment = (DownloadsFragment) ((MainActivity)appCompatActivity).getSupportFragmentManager().findFragmentByTag("1");

    }

    public redditDownloader() {

    }


    public static void download(String urlInput) throws IOException, InterruptedException {
        // DownloadsFragment fragment = (DownloadsFragment) ((MainActivity)appCompatActivity).getSupportFragmentManager().findFragmentByTag("1");
        TextView textView = fragment.downloadDialog.findViewById(R.id.urlDownload);

        if (isValid(textView.getText().toString())) {
            sendGET(urlInput);
        } else enableButton();
    }

    public static boolean isValid(String url) throws MalformedURLException {
        if (isMatch(url, urlRegex)) {
            try {
                URL urlInput = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast("URL is invalid");
                enableButton();
                return false;

            }
            if (!isMatch(url, redditRegex)) {
                Toast("URL is not a reddit URL/post");
                enableButton();
                return false;
            } else return true;

        } else if (url.length() == 0) {
            Toast("Please enter a URL");
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

    private static void sendGET(String url) throws IOException, InterruptedException {
        // Checks if query exists in the url
        if (new URL(url).getQuery() != null) { url = removeQuery(url); }

        final Request request = new Request.Builder()
                .url(url + ".json")
                .build();
        Log.d("urlFinal", (url));

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
                String jsonData = null;
                jsonData = response.body().string();

                try {
                    String fallbackURL = fallbackGrab(jsonData);
                    String fileName = titleGrab(jsonData);
                    downloadVideo(fallbackURL, fileName);
                    fragment.downloadDialog.dismiss();




                } catch (MalformedURLException e) {

                    e.printStackTrace();
                    Toast("The reddit URL is invalid");
                    enableButton();
                } catch (IOException e) {

                    e.printStackTrace();
                    enableButton();
                } catch (JSONException e) {

                    if (e.getMessage().equalsIgnoreCase("Value null at secure_media of type org.json.JSONObject$1 cannot be converted to JSONObject")) {
                        Toast("There is no media in the post");
                    } else Toast("The reddit URL is invalid");

                    enableButton();
                }


            }
        });
    }


    private static void downloadVideo(String url, String fileName) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = httpClient.newCall(request).execute();

        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getExternalFilesDir(null);

        outputFile = new File(directory, fileName + ".mp4");
        Log.d("directory", String.valueOf(directory));

        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        InputStream in = response.body().byteStream();

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
        // DownloadsFragment fragment = (DownloadsFragment) ((MainActivity)appCompatActivity).getSupportFragmentManager().findFragmentByTag("1");
        final Button button = fragment.downloadDialog.findViewById(R.id.downloadButton);

        ((MainActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setEnabled(true);
            }
        });
    }

    private static void setURL(final String url) {
        // DownloadsFragment fragment = (DownloadsFragment) ((MainActivity)appCompatActivity).getSupportFragmentManager().findFragmentByTag("1");
        final TextView textView = fragment.downloadDialog.findViewById(R.id.urlDownload);

        ((MainActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(url);
            }
        });
    }

    private static String removeQuery(String url) {
        return url.substring(0, url.indexOf("?"));
    }

    private static String fallbackGrab(String jsonData) throws JSONException {
        JSONArray mainArray = new JSONArray(jsonData);
        JSONArray children = mainArray.getJSONObject(0).getJSONObject("data").getJSONArray("children");
        return children.getJSONObject(0).getJSONObject("data").getJSONObject("secure_media").getJSONObject("reddit_video").getString("fallback_url");
    }

    private static String titleGrab(String jsonData) throws JSONException {
        JSONArray mainArray = new JSONArray(jsonData);
        JSONArray children = mainArray.getJSONObject(0).getJSONObject("data").getJSONArray("children");
        return children.getJSONObject(0).getJSONObject("data").getString("title");
    }

    private static void Toast(final String toast) {
        ((MainActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

