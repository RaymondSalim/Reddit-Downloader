package com.raymond.redditdownloader;

public class MediaObjects {
    private String title;
    private String mediaPath;
    private String thumbnail;

    public MediaObjects(String title, String mediaPath, String thumbnail) {
        this.title = title;
        this.mediaPath = mediaPath;
        this.thumbnail = thumbnail;
    }

    public MediaObjects() {

    }

    public void setMediaPath(String URI){
        this.mediaPath = URI;
    }

    public String getMediaPath() {
        return mediaPath;
    }

}
