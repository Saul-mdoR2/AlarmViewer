package com.example.alarmviewer

import androidx.lifecycle.ViewModel

class AlarmItemViewModel : ViewModel() {
    var alarm: Alarm? = null
    val alarmTime: String?
        get() = alarm?.alarmTime
}