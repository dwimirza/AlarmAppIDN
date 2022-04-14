package com.example.smartalarm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.databinding.RowItemAlarmBinding
import com.example.smartalarm.databinding.RowItemRepeatingBinding
import com.example.smartalarm.data.Alarm

class AlarmAdapter : RecyclerView.Adapter<AlarmAdapter.MyViewHolder>() {

    val listAlarm: ArrayList<Alarm> = arrayListOf()

    inner class MyViewHolder(val binding: RowItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        RowItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val alarm = listAlarm[position]

        holder.binding.apply {
            itemDateAlarm.text = alarm.date
            itemNoteAlarm.text = alarm.note
            itemTimeAlarm.text = alarm.time
        }
    }

    override fun getItemCount() = listAlarm.size //TODO 2 perbaharui kode

    fun setDat(list: List<Alarm>){
        val alarmDiffUtil = AlarmDifUtil(listAlarm, list)
        val alarmDiffUtilResult = DiffUtil.calculateDiff(alarmDiffUtil)
        listAlarm.clear()
        listAlarm.addAll(list)
        alarmDiffUtilResult.dispatchUpdatesTo(this)
    }
}