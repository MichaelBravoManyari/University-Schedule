package com.studentsapps.data.broadcasts

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.studentsapps.data.repository.ScheduleRepository
import com.studentsapps.model.ScheduleDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class AlarmPermissionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var scheduleRepository: ScheduleRepository
    @Inject lateinit var auth: FirebaseAuth

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmPermissionReceiver", "onReceive triggered with action: ${intent.action}")
        val userId = auth.currentUser?.uid
        if(userId != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                reRegisterAllAlarms(context, userId)
            }
        }
    }

    private fun reRegisterAllAlarms(context: Context, userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val allSchedules = scheduleRepository.getAllScheduleDetails(userId)

            allSchedules.forEach { scheduleDetails ->
                if (shouldScheduleAlarm(scheduleDetails)) {
                    scheduleAlarm(context, scheduleDetails)
                }
            }
        }
    }

    private fun shouldScheduleAlarm(scheduleDetails: ScheduleDetails): Boolean {
        val nowDate = LocalDate.now()
        val nowTime = LocalTime.now()

        val specificDate = scheduleDetails.specificDate

        return if (specificDate != null) {
            when {
                specificDate.isBefore(nowDate) -> false
                specificDate.isEqual(nowDate) && scheduleDetails.startTime.isBefore(nowTime) -> false
                else -> true
            }
        } else {
            true
        }
    }
}