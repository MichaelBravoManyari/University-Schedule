# Mantener todas las clases serializables y sus serializadores
-keep class com.studentsapps.model.** { *; }
-keep class com.studentsapps.model.**$$serializer { *; }

# Asegurar que kotlinx.serialization no pierda metadatos
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}