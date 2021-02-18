package com.ddastudio.hifivefootball_android.data.model;

import android.text.TextUtils;

/**
 * Created by hongmac on 2017. 10. 1..
 */

public class OpenGraphModel {

    String title;
    String image;
    String description;
    String url;
    String cannonicalUrl;

    public OpenGraphModel() {
        this.title = "";
        this.image = "";
        this.description = "";
        this.url = "";
        this.cannonicalUrl = "";
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        if ( description == null ) {
            return "";
        }

        return description;
    }

    public String getDescription(int limit) {
        if ( description == null ) {
            return "";
        }

        if ( description.length() > limit) {
            return description.substring(0, limit);
        }

        return description;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCannonicalUrl(String cannonicalUrl) {
        this.cannonicalUrl = cannonicalUrl;
    }

    public String getCannonicalUrl() {
        return cannonicalUrl;
    }

    public boolean isValid() {

        if ( !TextUtils.isEmpty(getImage()) && !TextUtils.isEmpty(getUrl()) && !TextUtils.isEmpty(getTitle())) {
            return true;
        } else {
            return false;
        }
    }
}
