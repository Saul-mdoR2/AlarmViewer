package com.example.alarmviewer

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmviewer.databinding.ActivityMainBinding
import timber.log.Timber
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var layout: ActivityMainBinding
    private lateinit var adapterRV: AlarmsListAdapter
    private lateinit var utils: Utils

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setBindingLayout()

        utils = Utils(this)
        layout.btnGenerateAlarms.setOnClickListener {
            utils.setCancelableAlarm(
                UUID.randomUUID().toString(),
                Random.nextInt(),
                Random.nextInt(0, 3)
            )
        }
        layout.btnRefreshRecyclerView.setOnClickListener {
            initRecyclerView()
        }
    }

    private fun initRecyclerView() {
        Timber.d("MainActivity_TAG: initRecyclerView: ")
        val result = utils.runTerminalCommand("dumpsys alarm")
        utils.extractAlarms(result)
        adapterRV = AlarmsListAdapter { alarm ->
            Timber.d("MainActivity_TAG: initRecyclerView2: itemClicked: ${alarm.alarmTime}")
        }
        adapterRV.itemList = utils.alarmList.map {
            AlarmItemViewModel().apply { alarm = it }
        }
        layout.rvAlarms.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = adapterRV
        }

    }

    private fun setBindingLayout() {
        layout = DataBindingUtil.setContentView(this, R.layout.activity_main)
        layout.lifecycleOwner = this
    }


}