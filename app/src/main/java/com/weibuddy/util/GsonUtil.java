package com.weibuddy.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.lang.reflect.Modifier;

public class GsonUtil {

    private static final Gson sGSon = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.STATIC)
            .create();

    public static <T> T fromJson(String json, Class<T> clazz) {
        return sGSon.fromJson(json, clazz);
    }

    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        return sGSon.fromJson(reader, clazz);
    }

}
