package com.example.smartalarm


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.smartalarm.data.Alarm
import com.example.smartalarm.data.local.AlarmDB
import com.example.smartalarm.data.local.AlarmDao
import com.example.smartalarm.databinding.ActivityRepeatingTimeBinding
import com.example.smartalarm.fragment.TimePickerFragment
import com.example.smartalarm.helper.timeFormatter
import kotlinx.android.synthetic.main.activity_one_time.*
import kotlinx.android.synthetic.main.activity_repeating_time.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepeatingTimeActivity : AppCompatActivity(), TimePickerFragment.TimeDialogListener {

    private var _binding: ActivityRepeatingTimeBinding? = null
    private val binding get() = _binding as ActivityRepeatingTimeBinding

    private var alarmDao: AlarmDao? = null

    private var _alarmService: AlarmService? = null
    private val alarmService get() = _alarmService as AlarmService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRepeatingTimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AlarmDB.getDataBase(this)
        alarmDao = db.alarmDao()

        _alarmService = AlarmService()

        initView()
    }

    private fun initView() {
        binding.apply {
            btn_set_time_repeating.setOnClickListener {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.show(supportFragmentManager, "TimePickerDialog")
            }

            btnAddSetRepeatingAlarm.setOnClickListener {
                val time = tvRepeatingTime.text.toString()
                val message = etNoteRepeating.text.toString()

                if (time != "Time") {
                    alarmService.setRepeatingAlarm(
                        applicationContext,
                        AlarmService.TYPE_Repeating,
                        time,
                        message
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        alarmDao?.addAlarm(
                            Alarm(
                                0,
                                "Repeating Alarm",
                                time,
                                message,
                                AlarmService.TYPE_Repeating
                            )
                        )
                        finish()
                    }
                } else {
                    Toast.makeText(applicationContext, "Set Alarmnya dulu oi", Toast.LENGTH_LONG).show()
                }
            }

            btnCancelSetRepeatingAlarm.setOnClickListener {
                finish()
            }
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        binding.tvRepeatingTime.text = timeFormatter(hourOfDay, minute)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}