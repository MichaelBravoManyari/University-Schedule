package com.studentsapps.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.studentsapps.database.model.ScheduleDetails
import com.studentsapps.database.model.ScheduleEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
abstract class ScheduleDao : BaseDao<ScheduleEntity> {

    @Query("SELECT * FROM schedules where id = :scheduleId")
    abstract suspend fun getScheduleById(scheduleId: Int): ScheduleEntity

    @Query(
        value = """ 
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
    abstract fun sdfds(
        showSaturday: Boolean,
        showSunday: Boolean,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<ScheduleDetails>>
}