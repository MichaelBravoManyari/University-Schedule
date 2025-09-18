# Mantener Firebase Analytics y Crashlytics
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

-keep class com.studentsapps.universityschedule.MyApplication { void <init>(); }
-keep class com.studentsapps.universityschedule.MainActivity { void <init>(); }

-dontwarn android.media.LoudnessCodecController$OnLoudnessCodecUpdateListener
-dontwarn android.media.LoudnessCodecController
