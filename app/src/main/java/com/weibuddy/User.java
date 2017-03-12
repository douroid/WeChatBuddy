package com.weibuddy;

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

    public boolean isSuccessed() {
        return "ok".equalsIgnoreCase(error);
    }

}
