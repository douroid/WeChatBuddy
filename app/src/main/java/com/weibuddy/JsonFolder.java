package com.weibuddy;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

public class JsonFolder {

    @SerializedName("folder_name")
    public LinkedTreeMap<String, String> names;
    @SerializedName("folder_version")
    public LinkedTreeMap<String, Integer> versions;
    @SerializedName("folder_id")
    public LinkedTreeMap<String, String> ids;
    @SerializedName("folder_content")
    public LinkedTreeMap<String, LinkedTreeMap<String, LinkedTreeMap<String, Object>>> contents;

}
