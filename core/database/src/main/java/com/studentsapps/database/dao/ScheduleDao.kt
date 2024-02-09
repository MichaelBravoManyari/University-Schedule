package com.studentsapps.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.studentsapps.database.model.ScheduleDetailsView
import com.studentsapps.database.model.ScheduleEntity
import java.time.DayOfWeek
import java.time.LocalDate

@Dao
abstract class ScheduleDao : BaseDao<ScheduleEntity> {

    @Query("SELECT * FROM schedules where id = :scheduleId")
    abstract suspend fun getScheduleById(scheduleId: Int): ScheduleEntity

    @Query("SELECT * FROM schedule_details where schedule_id = :scheduleId")
    abstract suspend fun getScheduleDetailsById(scheduleId: Int): ScheduleDetailsView

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
        """
    )
    abstract suspend fun getSchedulesForTimetableInGridMode(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<ScheduleDetailsView>

    @Query(
        value = """
            SELECT * FROM schedule_details
            WHERE
                (day_of_week = :dayOfWeek AND specific_date IS NULL)
            OR
                specific_date = :specificDate
        """
    )
    abstract suspend fun getSchedulesForTimetableInListMode(
        dayOfWeek: DayOfWeek,
        specificDate: LocalDate
    ): List<ScheduleDetailsView>
}