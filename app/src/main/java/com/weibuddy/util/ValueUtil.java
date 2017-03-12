package com.weibuddy.util;

import android.text.TextUtils;

public final class ValueUtil {

    public static String value(Object object, String defValue) {
        if (object == null) {
            return defValue;
        }

        String value = object.toString();

        return TextUtils.isEmpty(value) ? defValue : value;
    }

    public static String value(Object object) {
        return value(object, "");
    }

}
