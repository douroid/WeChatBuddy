package com.weibuddy;

import com.google.gson.annotations.SerializedName;

public class Password {

	@SerializedName("error_code")
    public int error_code = -1;
    @SerializedName("msg")
    public String msg;

    public boolean isSuccessed() {
        return error_code == 0;
    }
}
