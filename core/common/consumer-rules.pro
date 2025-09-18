# Mantener anotaciones de kotlinx.serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

-keep class com.studentsapps.common.serializer.** { *; }

-keep class **$$serializer { *; }
-keep class kotlinx.serialization.internal.** { *; }

