package com.ddastudio.hifivefootball_android.data.model;

import org.parceler.Parcel;

/**
 * Created by hongmac on 2017. 9. 19..
 */

@Parcel
public class TaggingModel {

    int id;
    String name;

    public TaggingModel() {

    }

    public TaggingModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
