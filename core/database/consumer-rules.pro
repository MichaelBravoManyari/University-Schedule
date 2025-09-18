# --- Room Entities ---
-keep class com.studentsapps.database.model.*Entity { *; }

# --- Room Views / POJOs ---
-keep class com.studentsapps.database.model.ScheduleDetailsView { *; }

# --- DAOs ---
-keep interface com.studentsapps.database.dao.* { *; }

# --- Room Database ---
-keep class com.studentsapps.database.UniversityScheduleDatabase { *; }


# --- TypeConverters ---
-keep class com.studentsapps.database.util.* { *; }


# --- Hilt Modules ---
-keep class com.studentsapps.database.DaoModule { *; }
-keep class com.studentsapps.database.DatabaseModule { *; }

