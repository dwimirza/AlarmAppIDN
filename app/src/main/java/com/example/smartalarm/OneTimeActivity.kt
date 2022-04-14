package com.example.smartalarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.smartalarm.data.Alarm
import com.example.smartalarm.data.local.AlarmDB
import com.example.smartalarm.data.local.AlarmDao
import com.example.smartalarm.databinding.ActivityOneTimeBinding
import com.example.smartalarm.fragment.DatePickerFragment
import com.example.smartalarm.fragment.TimePickerFragment
import com.example.smartalarm.helper.timeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OneTimeActivity : AppCompatActivity(), DatePickerFragment.DateDialogListener,
    TimePickerFragment.TimeDialogListener {

    private var _binding: ActivityOneTimeBinding? = null
    private val binding get() = _binding as ActivityOneTimeBinding

    private var alarmDao: AlarmDao? = null

    private var _alarmService : AlarmService? = null
    private val alarmService get() = _alarmService as AlarmService

//    private val db by lazy { AlarmDB(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOneTimeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val db = AlarmDB.getDataBase(applicationContext)
        alarmDao = db.alarmDao()

        _alarmService = AlarmService()

        initView()
    }

    private fun initView() {
        binding.apply {

            btnSetOne.setOnClickListener {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, "DatePickerDialog")
            }

            btnSetTimeOneTime.setOnClickListener {
                val datePickerFragment = TimePickerFragment()
                datePickerFragment.show(supportFragmentManager, "TimePickerDialog")
            }

            btnAdd.setOnClickListener {
                val date = tvOneDate.text.toString()
                val time = tvOneTime.text.toString()
                val note = editNoteOneTime.text.toString()

                if (date != "Date" && time != "Time") {
                    alarmService.setOneTimeAlarm(applicationContext, 1, date, time, note)
                    CoroutineScope(Dispatchers.IO).launch {
                        alarmDao?.addAlarm(
                            Alarm(
                                0,
                                date,
                                time,
                                note,
                                AlarmService.TYPE_ONETIME
                            )
                        )
                        Log.i("AddAlarm", "Succes set alarm on $date $time with message")
                        finish()
                    }
                } else {
                    Toast.makeText( applicationContext, "You must set the alarm!!", Toast.LENGTH_SHORT ).show()
                }
            }

            btnCancel.setOnClickListener {
                finish()
            }
        }
    }

    override fun onDialogDataSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()

        //mengatur tanggal supaya sama dengan yang sudah di pilih di dialog picker
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        binding.tvOneDate.text = dateFormat.format(calendar.time)
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        binding.tvOneTime.text = timeFormatter(hourOfDay, minute)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}