package com.example.alarmviewer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import timber.log.Timber
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class Utils(private val context: Context) {
    var alarmList = ArrayList<Alarm>()

    fun setCancelableAlarm(uuid: String, requestCode: Int, alarmType: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val scheduleIntent = Intent(context, RecordingScheduleReceiver::class.java).apply {
            putExtras(Bundle().apply {
                putExtra("UUID", uuid)
                putExtra("alarmType", alarmType)
            })
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context, requestCode, scheduleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val calendar = Calendar.getInstance().apply {
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val randomHour = Random.nextInt(currentHour, 23)
            val randomMinutes = Random.nextInt(0, 59)
            if (randomHour == currentHour && randomHour <= 23) {
                set(Calendar.HOUR, randomHour + 1)
            } else
                set(Calendar.HOUR_OF_DAY, randomHour)

            set(Calendar.MINUTE, randomMinutes)
            set(Calendar.SECOND, 0)
        }
        alarmManager.setExactAndAllowWhileIdle(alarmType, calendar.timeInMillis, pendingIntent)
    }

    fun runTerminalCommand(cmd: String): String {
        Timber.d("Util_TAG: runTerminalCommand: $cmd")
        var result = ""
        var dos: DataOutputStream? = null
        var dis: DataInputStream? = null
        try {
            val p: Process = Runtime.getRuntime().exec("su")
            dos = DataOutputStream(p.outputStream)
            dis = DataInputStream(p.inputStream)
            dos.writeBytes(cmd + "\n")
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            var line: String
            @Suppress("DEPRECATION")
            while (dis.readLine().also { line = it } != null) {
                result += line
            }
            p.waitFor()
        } catch (e: Exception) {
            Timber.d("Util_TAG: runTerminalCommand: exception: $e")
        } finally {
            if (dos != null) {
                try {
                    dos.close()
                } catch (e: IOException) {
                    Timber.d("Util_TAG: runTerminalCommand: ")
                }
            }
            if (dis != null) {
                try {
                    dis.close()
                } catch (e: IOException) {
                    Timber.d("Util_TAG: runTerminalCommand: ")
                }
            }
        }
        Timber.d("_TAG: runTerminalCommand: result: $result")
        return result
    }

    fun extractAlarms(result: String) {
        alarmList.clear()
        val arrayBatch = result.split("Batch")
        for (batch in arrayBatch) {
            if (batch.contains(context.packageName)) {
                val removeText = batch.split("broadcastIntent}")[0]
                val indexOfTimeStart = removeText.indexOf("when=")
                val indexOfTimeEnd = removeText.indexOf("window")
                val alarmTime =
                    removeText.substring(indexOfTimeStart, indexOfTimeEnd).replace("when=", "")
                        .trim()
                if (!alarmTime.contains("d")) {
                    val alarm = Alarm(alarmTime)
                    alarmList.add(alarm)
                }
            }
        }
    }
}