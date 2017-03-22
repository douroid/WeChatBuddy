package com.weibuddy;

import android.util.Log;

import java.io.File;

public final class Config {

    private static final String TAG = Config.class.getSimpleName();

    public static final String SITE = "http://www.weibuddy.com/";

    public static final String INTRO_URL = "http://wxbl.applinzi.com/web/summary.html";
    public static final String ABOUT_URL = "http://wxbl.applinzi.com/web/todream.html";
    public static final String SERVICE_URL = "http://wxbl.applinzi.com/web/service.html";

    public static final String API = "http://wxbl.applinzi.com/api/iphone.php";
    public static final String API_PASSWORD = "http://wxbl.applinzi.com/api/modify_pwd.php";

    public static final String KEY_METHOD = "q";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_PWD = "user_pwd";
    public static final String KEY_USER_ID = "userid";
    public static final String KEY_RAND_CODE = "rand_code";
    public static final String KEY_OLD_PWD = "oldpwd";
    public static final String KEY_NEW_PWD = "newpwd";
    public static final String KEY_SIGN = "sign";

    public static final String VALUE_METHOD_SIGN_IN = "playerLogin";
    public static final String VALUE_METHOD_LIST = "getAllData";
    public static final String VALUE_METHOD_SIGN_OUT = "loginOut";

    public static final String JSON_KEY_FILE_ID = "file_id";
    public static final String JSON_KEY_FILE_NAME = "file_name";
    public static final String JSON_KEY_FILE_VERSION = "file_version";
    public static final String JSON_KEY_FILE_CONTENT = "file_content";
    public static final String JSON_KEY_VIDEO_PIC = "video_pic";

    public static final int STATE_FRESH = 1;
    public static final int STATE_NORMAL = 0;

    public static final int IMAGE_LENGTH_LIMIT = 6291456;

    public static final String MD5_KEY = "wxB*(@*JDJ1020!&)#1(YUIsdysd";

    private static File AVATAR_FILE;

    public static void setAvatarFile(File file) {
        AVATAR_FILE = file;
    }

    public static File getAvatarFile() {
        return AVATAR_FILE;
    }

    public static boolean hasAvatarFile() {
        return AVATAR_FILE.exists();
    }

    public static void resetAvatarFile() {
        if (hasAvatarFile()) {
            boolean isDeleted = AVATAR_FILE.delete();
            if (isDeleted && BuildConfig.DEBUG) {
                Log.d(TAG, "avatar file is removed.");
            }
        }
    }
}
