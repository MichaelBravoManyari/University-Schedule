# Mantener Workers
-keep class com.studentsapps.data.workers.SyncPendingOperationsWorker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.Worker { *; }

# Hilt Worker Factory
-keep class **_HiltWorkerFactory { *; }

# Mantener interfaces de repositorios e implementaciones
-keep class com.studentsapps.data.repository.**Repository { *; }
-keep class com.studentsapps.data.repository.**RepositoryImpl { *; }

-keep class **_AssistedFactory { *; }
-keep class **_HiltWorker { *; }

-keepclassmembers class com.studentsapps.data.workers.SyncPendingOperationsWorker {
    <init>(...);
}

# Mantener anotaciones de kotlinx.serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

-keep class **$$serializer { *; }
-keep class kotlinx.serialization.internal.** { *; }