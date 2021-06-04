package com.example.alarmviewer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import timber.log.Timber

class RecordingScheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d(" ALARM RECEIVED!!!")
    }
}