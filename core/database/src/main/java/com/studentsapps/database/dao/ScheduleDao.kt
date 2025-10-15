package com.studentsapps.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate

@Dao
abstract class ScheduleDao : BaseDao<ScheduleEntity> {
    @Query("SELECT * FROM schedules where id = :scheduleId")
    abstract fun getScheduleById(scheduleId: String): Flow<ScheduleEntity>

    @Query("SELECT * FROM schedule_details where schedule_id = :scheduleId")
    abstract suspend fun getScheduleDetailsById(scheduleId: String): ScheduleDetailsView

    @Query(
        value = """ 
             SELECT * FROM schedule_details
             WHERE
                CASE WHEN NOT :showSaturday
                    THEN day_of_week != 6
                    ELSE 1
                END
             AND
                CASE WHEN NOT :showSunday
                    THEN day_of_week != 7
                    ELSE 1
                END
             AND
                specific_date IS NULL
             AND
                user_id = :userId
             UNION 
             SELECT * FROM schedule_details
             wHERE
                CASE WHEN NOT :showSaturday
                    THEN day_of_week != 6
                    ELSE 1
                END
             AND
                CASE WHEN NOT :showSunday
                    THEN day_of_week != 7
                    ELSE 1
                END
             AND
                specific_date BETWEEN :startDate AND :endDate
             AND
                user_id = :userId
        """,
    )
    abstract fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate,
        userId: String,
    ): Flow<List<ScheduleDetailsView>>

    @Query(
        value = """
            SELECT * FROM schedule_details
            WHERE
                ((day_of_week = :dayOfWeek AND specific_date IS NULL)
            OR
                specific_date = :specificDate)
            AND 
                user_id = :userId
        """,
    )
    abstract fun getSchedulesForTimetableInListMode(
        dayOfWeek: DayOfWeek,
        specificDate: LocalDate,
        userId: String,
    ): Flow<List<ScheduleDetailsView>>

    @Query("SELECT * FROM schedule_details WHERE user_id = :userId")
    abstract suspend fun getAllSchedule(userId: String): List<ScheduleDetailsView>

    @Query("SELECT * FROM schedules WHERE user_id = :userId")
    abstract fun getAllScheduleEntity(userId: String): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules WHERE course_id = :courseId")
    abstract suspend fun getSchedulesByCourseId(courseId: String): List<ScheduleEntity>

    @Query("SELECT * FROM schedules WHERE id IN (:schedulesIds)")
    abstract fun getSchedulesByIds(schedulesIds: List<String>): Flow<List<ScheduleEntity>>
}
