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
import org.json.*;


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
    private static String redditRegex = "https?://(www.)?\\b(reddit.com|redd.it)\\b\\b(/r/)\\b[-a-zA-Z0-9@:%._+~&?#/=]{1,512}";
    private static String urlRegex = "(https?://){1}[-a-zA-Z0-9@:%_+~&?#./=]{1,512}";
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static String shareURL;
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
        } else enableButton();
    }

    public static boolean isValid(String url) throws MalformedURLException {
        if (isMatch(url, urlRegex)) {
            try {
                URL urlInput = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                ((MainActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "URL is invalid", Toast.LENGTH_SHORT).show();
                    }
                });
                enableButton();
                return false;

            }
            if (!isMatch(url, redditRegex)) {
                ((MainActivity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "URL is not a reddit URL/post", Toast.LENGTH_SHORT).show();
                    }
                });
                enableButton();
                return false;
            } else return true;

        } else if (url.length() == 0) {
            ((MainActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Please enter a URL", Toast.LENGTH_SHORT).show();
                }
            });
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

        Log.d("urlFinal", removePath(url));
        final Request request = new Request.Builder()
                .url(removePath(url) + ".json")
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
                String jsonData = null;
                jsonData = response.body().string();

                try {
                    String fallbackURL = fallbackGrab(jsonData);
                    Log.d("fallbackURL", fallbackURL);
                    Log.d("fallBackURL", removePath(fallbackURL));
                    downloadVideo(fallbackURL, fileName);
                    Log.d("downloadVideo", "video downloaded");
                    ((MainActivity)context).downloadDialog.dismiss();
                    // enableButton();




                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    ((MainActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "The reddit URL is invalid", Toast.LENGTH_SHORT).show();
                        }
                    });
                    enableButton();
                } catch (IOException e) {
                    e.printStackTrace();
                    enableButton();
                } catch (JSONException e) {
                    ((MainActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "There is no media in the reddit post", Toast.LENGTH_SHORT).show();
                        }
                    });
                    enableButton();
                }


            }
        });
    }


    private static void downloadVideo(String url, String fileName) throws IOException {
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

    private static String removePath(String url) {
        return url.substring(0, url.indexOf("?"));
    }

    private static String fallbackGrab(String jsonData) throws JSONException {
        JSONArray mainArray = new JSONArray(jsonData);
        JSONArray children = mainArray.getJSONObject(0).getJSONObject("data").getJSONArray("children");
        return children.getJSONObject(0).getJSONObject("data").getJSONObject("secure_media").getJSONObject("reddit_video").getString("fallback_url");
    }



}

