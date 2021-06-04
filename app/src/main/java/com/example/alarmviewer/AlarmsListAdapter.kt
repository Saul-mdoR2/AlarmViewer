package com.example.alarmviewer

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.alarmviewer.databinding.AlarmlistLayoutBinding

class AlarmsListAdapter(listener: (Alarm) -> Unit) :
    BaseRVAdapter<Alarm, AlarmItemViewModel, AlarmlistLayoutBinding>(listener) {
    override fun inflateDataBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AlarmlistLayoutBinding = AlarmlistLayoutBinding.inflate(inflater, container, false)

    override fun getBindItem(itemViewModel: AlarmItemViewModel): Alarm? = itemViewModel.alarm
}