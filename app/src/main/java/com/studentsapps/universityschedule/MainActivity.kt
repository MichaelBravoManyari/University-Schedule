package com.studentsapps.universityschedule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.studentsapps.schedule.TimetableDateUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}