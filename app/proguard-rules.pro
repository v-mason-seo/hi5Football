# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/hongmac/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ================================
# fresco
# ================================
# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep,allowobfuscation @interface com.facebook.soloader.DoNotOptimize

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Do not strip any method/class that is annotated with @DoNotOptimize
-keep @com.facebook.soloader.DoNotOptimize class *
-keepclassmembers class * {
    @com.facebook.soloader.DoNotOptimize *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.infer.**

# ================================
# MQTT
# ================================
-keepattributes InnerClasses
-keepattributes EnclosingMethod
#-keepattributes EnclosingMethod
-keep class org.eclipse.paho.** { *; }
-keep public class * extends android.app.Service
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
  public *;
}
#-keepattributes EnclosingMethod
#-keepattributes InnerClasses
-dontoptimize
-keep class org.eclipse.paho.client.mqttv3.persist.** { *; }
-dontwarn org.eclipse.paho.client.mqttv3.persist.**
-keepattributes Exceptions, Signature, InnerClasses
# Paho library logger
-keep class org.eclipse.paho.client.mqttv3.logging.JSR47Logger {
    *;
}

-keep class com.ddastudio.hifivefootball_android.data.model.arena_chat.** { *; }
#-keep class org.eclipse.paho.client.mqttv3.** { *; }
#-keep class org.eclipse.paho.client.mqttv3.*$* { *; }
##-keep class com.chad.library.adapter.** {
##*;
##}
#-keepattributes EnclosingMethod
#-keepattributes InnerClasses
#-dontoptimize
##-keep public class org.eclipse.paho.** {
##	public *;
##}
#-keep public class org.eclipse.paho.client.mqttv3.** {
#*;
#}
#-keep public class org.eclipse.paho.client.mqttv3.*$* { *; }

# ================================
# glide
# ================================
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# ================================
# Matisse
# ================================
-dontwarn com.squareup.picasso.**
-dontwarn com.bumptech.glide.**

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# ================================
# retrofit2
# ================================
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-dontwarn okio.**
-dontwarn javax.annotation.**

# ================================
# Parceler library
# ================================
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }

# ================================
# prettytime
# ================================
-keep class org.ocpsoft.prettytime.i18n.**

# ================================
# 네이버 아이디로 로그인
# ================================
#-libraryjars ../app/libs/3rdparty_login_library_android_4.1.4.jar
-keep public class com.nhn.android.naverlogin.** {
       public protected *;
}

# ================================
# BaseRecyclerViewAdapterHelper
# ================================
#-keep class com.chad.library.adapter.** {
#*;
#}
#-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
#-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
#-keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder {
#     <init>(android.view.View);
#}
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(...);
}

# ================================
# Amazon
# ================================
# Class names are needed in reflection
-keepnames class com.amazonaws.**
-keepnames class com.amazon.**
# Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler
# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**
# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**


# ================================
# Guava
# ================================
-keep class com.google.common.io.Resources {
    public static <methods>;
}
-keep class com.google.common.collect.Lists {
    public static ** reverse(**);
}
-keep class com.google.common.base.Charsets {
    public static <fields>;
}

-keep class com.google.common.base.Joiner {
    public static com.google.common.base.Joiner on(java.lang.String);
    public ** join(...);
}

-keep class com.google.common.collect.MapMakerInternalMap$ReferenceEntry
-keep class com.google.common.cache.LocalCache$ReferenceEntry

# http://stackoverflow.com/questions/9120338/proguard-configuration-for-guava-with-obfuscation-and-optimization
#-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Guava 19.0
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn com.google.errorprone.annotations.*

# ================================
# kakao
# ================================
-keep class com.kakao.** { *; }
-keepattributes Signature
-keepclassmembers class * {
  public static <fields>;
  public *;
}
-dontwarn android.support.v4.**,org.slf4j.**,com.google.android.gms.**

# ================================
# jsoup
# ================================
-keep public class org.jsoup.** {

	public *;

}

-keep class com.hanks.htextview.** {
	public *;
}