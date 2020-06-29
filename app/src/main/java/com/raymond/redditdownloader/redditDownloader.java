package com.raymond.redditdownloader;

import android.content.Context;
import android.widget.Toast;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class redditDownloader extends MainActivity{
    private String title = null;
    private URL redditURL;
    private String redditRegex = "https?://(www.)?\\b(reddit.com|redd.it)\\b([/]?[r]?[/]?)?[-a-zA-Z0-9@:%._+~&?#/=]{1,256}";
    private Context context;

    public redditDownloader(Context context) {
        this.context = context;
    }


    public void download (String urlInput) throws MalformedURLException {

        redditURL = new URL(urlInput);
        isValid(redditURL.toString());
        Toast toast = Toast.makeText(context, "It works!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public boolean isValid(String url) {
        try {
            URL urlInput = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(context,"URL is invalid", Toast.LENGTH_SHORT).show();
            return false;

        }
        if (!isMatch(url, redditRegex)) {
            Toast.makeText(context,  "URL is not a reddit URL", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;

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


}
