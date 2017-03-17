package com.weibuddy;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("userid")
    public String id;
    @SerializedName("user_name")
    public String name;
    @SerializedName("error")
    public String error;
    @SerializedName("rand_code")
    public String randCode;
    @SerializedName("error_code")
    public String errorCode;
    @SerializedName("msg")
    public String errorMsg;

    public boolean isSuccessful() {
        return "ok".equalsIgnoreCase(error) && !TextUtils.isEmpty(id);
    }

}
