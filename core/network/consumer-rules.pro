# Mantener todas las clases con @Serializable y sus serializadores generados
-keep class kotlinx.serialization.** { *; }
-keep class com.studentsapps.network.model.** { *; }
-keepclassmembers class com.studentsapps.network.model.** {
    *** Companion;
}
-keepclassmembers class com.studentsapps.network.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Mantener las clases generadas con $$serializer
-keep class com.studentsapps.network.model.**$$serializer { *; }

-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

-keep class **$$serializer { *; }
-keep class kotlinx.serialization.internal.** { *; }









