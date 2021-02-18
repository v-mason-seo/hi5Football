package com.ddastudio.hifivefootball_android.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class CommonInfoModel {

    @SerializedName("large_code")
    String largeCode;
    @SerializedName("midium_code")
    String midiumCode;
    @SerializedName("small_code")
    String smallCode;
    @SerializedName("codename")
    String codeName;
    @SerializedName("value1")
    String value1;
    @SerializedName("value2")
    String value2;
    @SerializedName("value3")
    String value3;
    @SerializedName("value4")
    String value4;
    @SerializedName("value5")
    String value5;
    @SerializedName("remark")
    String remark;
    @SerializedName("created")
    Date created;

    public String getLargeCode() {
        return largeCode;
    }

    public void setLargeCode(String largeCode) {
        this.largeCode = largeCode;
    }

    public String getMidiumCode() {
        return midiumCode;
    }

    public void setMidiumCode(String midiumCode) {
        this.midiumCode = midiumCode;
    }

    public String getSmallCode() {
        return smallCode;
    }

    public void setSmallCode(String smallCode) {
        this.smallCode = smallCode;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    public String getValue5() {
        return value5;
    }

    public void setValue5(String value5) {
        this.value5 = value5;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
