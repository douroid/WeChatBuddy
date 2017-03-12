# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Dev\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-obfuscationdictionary dictionary.txt
-classobfuscationdictionary dictionary.txt
-packageobfuscationdictionary dictionary.txt

-dontwarn com.tencent.**
-keep class com.tencent.** { *; }

-dontwarn org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

-keepclasseswithmembers class * extends android.webkit.WebChromeClient { *; }
-keepclasseswithmembers class * extends android.webkit.WebViewClient { *; }

-keep class * extends com.bumptech.glide.module.GlideModule { *; }
-keep class com.afollestad.materialdialogs.** { *; }

-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn com.google.**
-dontwarn com.bumptech.glide.**
-dontwarn permissions.dispatcher.**
-dontwarn io.mikael.urlbuilder.**
-dontwarn uk.co.senab.photoview.**
-dontwarn com.klinker.android.link_builder.**
-dontwarn com.flipboard.bottomsheet.**
-dontwarn com.soundcloud.android.crop.**
-dontwarn com.hannesdorfmann.adapterdelegates3.**
-dontwarn com.yqritc.recyclerviewflexibledivider.**
-dontwarn com.afollestad.materialdialogs.**
