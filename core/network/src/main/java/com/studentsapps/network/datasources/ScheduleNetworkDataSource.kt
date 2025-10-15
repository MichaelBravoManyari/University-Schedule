package com.studentsapps.network.datasources

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.studentsapps.network.model.NetworkSchedule
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ScheduleNetworkDataSource
    @Inject
    constructor() {
        private val firestore = FirebaseFirestore.getInstance()

        suspend fun updateOrCreateSchedule(
            userId: String,
            schedule: NetworkSchedule,
        ) {
            try {
                val scheduleData =
                    mapOf(
                        "id" to schedule.id,
                        "startTime" to schedule.startTime,
                        "endTime" to schedule.endTime,
                        "classPlace" to schedule.classPlace,
                        "dayOfWeek" to schedule.dayOfWeek,
                        "specificDate" to schedule.specificDate?.toString(),
                        "courseId" to schedule.courseId,
                        "lastModified" to
                            Timestamp(
                                schedule.lastModified.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000,
                                0,
                            ),
                        "userId" to userId,
                    )

                firestore
                    .collection("users")
                    .document(userId)
                    .collection("schedules")
                    .document(schedule.id)
                    .set(scheduleData, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                throw RuntimeException("Error updating or creating schedule: ${e.message}", e)
            }
        }

        suspend fun deleteSchedule(
            userId: String,
            scheduleId: String,
        ) {
            try {
                firestore
                    .collection("users")
                    .document(userId)
                    .collection("schedules")
                    .document(scheduleId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                throw RuntimeException("Error deleting course: ${e.message}", e)
            }
        }

        suspend fun getScheduleById(
            userId: String,
            scheduleId: String,
        ): NetworkSchedule? =
            try {
                val documentSnapshot =
                    firestore
                        .collection("users")
                        .document(userId)
                        .collection("schedules")
                        .document(scheduleId)
                        .get()
                        .await()

                if (documentSnapshot.exists()) {
                    val startTimeMap = documentSnapshot.get("startTime") as? Map<String, Number>
                    val startTime =
                        startTimeMap?.let {
                            LocalTime.of(
                                it["hour"]?.toInt() ?: 0,
                                it["minute"]?.toInt() ?: 0,
                                it["second"]?.toInt() ?: 0,
                                it["nano"]?.toInt() ?: 0,
                            )
                        } ?: LocalTime.MIN
                    val endTimeMap = documentSnapshot.get("endTime") as? Map<String, Number>
                    val endTime =
                        endTimeMap?.let {
                            LocalTime.of(
                                it["hour"]?.toInt() ?: 0,
                                it["minute"]?.toInt() ?: 0,
                                it["second"]?.toInt() ?: 0,
                                it["nano"]?.toInt() ?: 0,
                            )
                        } ?: LocalTime.MIN
                    val classPlace = documentSnapshot.getString("classPlace")
                    val dayOfWeek =
                        documentSnapshot.getString("dayOfWeek").let {
                            DayOfWeek.valueOf(it!!.uppercase())
                        }
                    val specificDate =
                        documentSnapshot
                            .getString("specificDate")
                            ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
                    val courseId = documentSnapshot.getString("courseId") ?: " "
                    val timestamp = documentSnapshot.getTimestamp("lastModified")?.toDate()
                    val lastModified =
                        timestamp?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
                            ?: LocalDateTime.now()

                    NetworkSchedule(
                        id = scheduleId,
                        startTime = startTime,
                        endTime = endTime,
                        classPlace = classPlace,
                        dayOfWeek = dayOfWeek,
                        specificDate = specificDate,
                        lastModified = lastModified,
                        courseId = courseId,
                        userId = userId,
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("ScheduleNetworkDataSource", "Error obteniendo el schedule: ${e.message}", e)
                null
            }

        suspend fun getSchedulesByIds(
            userId: String,
            schedulesIds: List<String>,
        ): List<NetworkSchedule> {
            if (schedulesIds.isEmpty()) return emptyList()

            return try {
                val querySnapshot =
                    firestore
                        .collection("users")
                        .document(userId)
                        .collection("schedules")
                        .whereIn("id", schedulesIds)
                        .get()
                        .await()

                querySnapshot.documents.mapNotNull { documentSnapshot ->
                    val startTimeMap = documentSnapshot.get("startTime") as? Map<String, Number>
                    val startTime =
                        startTimeMap?.let {
                            LocalTime.of(
                                it["hour"]?.toInt() ?: 0,
                                it["minute"]?.toInt() ?: 0,
                                it["second"]?.toInt() ?: 0,
                                it["nano"]?.toInt() ?: 0,
                            )
                        } ?: LocalTime.MIN
                    val endTimeMap = documentSnapshot.get("endTime") as? Map<String, Number>
                    val endTime =
                        endTimeMap?.let {
                            LocalTime.of(
                                it["hour"]?.toInt() ?: 0,
                                it["minute"]?.toInt() ?: 0,
                                it["second"]?.toInt() ?: 0,
                                it["nano"]?.toInt() ?: 0,
                            )
                        } ?: LocalTime.MIN

                    val classPlace = documentSnapshot.getString("classPlace")
                    val dayOfWeek =
                        documentSnapshot.getString("dayOfWeek").let {
                            DayOfWeek.valueOf(it!!.uppercase())
                        }
                    val specificDate =
                        documentSnapshot
                            .getString("specificDate")
                            ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }
                    val courseId = documentSnapshot.getString("courseId") ?: " "
                    val timestamp = documentSnapshot.getTimestamp("lastModified")?.toDate()
                    val lastModified =
                        timestamp?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
                            ?: LocalDateTime.now()

                    NetworkSchedule(
                        id = documentSnapshot.getString("id")!!,
                        startTime = startTime,
                        endTime = endTime,
                        classPlace = classPlace,
                        dayOfWeek = dayOfWeek,
                        specificDate = specificDate,
                        lastModified = lastModified,
                        courseId = courseId,
                        userId = userId,
                    )
                }
            } catch (e: Exception) {
                Log.e("ScheduleNetworkDataSource", "Error obteniendo los horarios: ${e.message}", e)
                emptyList()
            }
        }

        suspend fun getAllSchedules(userId: String): List<NetworkSchedule> =
            try {
                val querySnapshot =
                    firestore
                        .collection("users")
                        .document(userId)
                        .collection("schedules")
                        .get()
                        .await()

                querySnapshot.documents.mapNotNull { documentSnapshot ->
                    val startTimeMap = documentSnapshot.get("startTime") as? Map<String, Number>
                    val startTime =
                        startTimeMap?.let {
                            LocalTime.of(
                                it["hour"]?.toInt() ?: 0,
                                it["minute"]?.toInt() ?: 0,
                                it["second"]?.toInt() ?: 0,
                                it["nano"]?.toInt() ?: 0,
                            )
                        } ?: LocalTime.MIN
                    val endTimeMap = documentSnapshot.get("endTime") as? Map<String, Number>
                    val endTime =
                        endTimeMap?.let {
                            LocalTime.of(
                                it["hour"]?.toInt() ?: 0,
                                it["minute"]?.toInt() ?: 0,
                                it["second"]?.toInt() ?: 0,
                                it["nano"]?.toInt() ?: 0,
                            )
                        } ?: LocalTime.MIN

                    val classPlace = documentSnapshot.getString("classPlace")
                    val dayOfWeek =
                        documentSnapshot.getString("dayOfWeek").let {
                            DayOfWeek.valueOf(it!!.uppercase())
                        }
                    val specificDate =
                        documentSnapshot
                            .getString("specificDate")
                            ?.let { LocalDate.parse(it) }
                    val courseId = documentSnapshot.getString("courseId") ?: " "
                    val timestamp = documentSnapshot.getTimestamp("lastModified")?.toDate()
                    val lastModified =
                        timestamp?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
                            ?: LocalDateTime.now()

                    NetworkSchedule(
                        id = documentSnapshot.getString("id")!!,
                        startTime = startTime,
                        endTime = endTime,
                        classPlace = classPlace,
                        dayOfWeek = dayOfWeek,
                        specificDate = specificDate,
                        lastModified = lastModified,
                        courseId = courseId,
                        userId = userId,
                    )
                }
            } catch (e: Exception) {
                Log.e("ScheduleNetworkDataSource", "Error obteniendo los horarios: ${e.message}", e)
                emptyList()
            }
    }
