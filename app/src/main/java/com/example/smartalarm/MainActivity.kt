package com.example.smartalarm


import android.content.ContentValues.TAG
import android.content.Intent
import android.icu.lang.UCharacter.IndicPositionalCategory.RIGHT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartalarm.data.Alarm
import com.example.smartalarm.data.local.AlarmDB
import com.example.smartalarm.adapter.AlarmAdapter
import com.example.smartalarm.data.local.AlarmDao
import com.example.smartalarm.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var alarmAdapter: AlarmAdapter? = null

    private var alarmDao: AlarmDao? = null

    private var alarmService: AlarmService? = null


    override fun onResume() {
        super.onResume()
        alarmDao?.getAlarm()?.observe(this) {
            alarmAdapter?.setDat(it)
            Log.i("GetAlarm", "getAlarm : alarm with $it")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AlarmDB.getDataBase(applicationContext)
        alarmDao = db.alarmDao()

        alarmService = AlarmService()

        initView()

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvReminderAlarm.apply {
            alarmAdapter = AlarmAdapter()
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = alarmAdapter
            swipeToDelete(this)
        }
    }

    private fun initView() {
        binding.apply {
            cvSetOneTime.setOnClickListener {
                startActivity(
                    Intent(
                        applicationContext,
                        OneTimeActivity::class.java
                    )
                )  //bisa pake applicationContext, bisa juga pake this@MainActivity
            }

            cvSetRepeatingTime.setOnClickListener {
                startActivity(Intent(this@MainActivity, RepeatingTimeActivity::class.java))
            }
        }
        getTimeToday()
    }

    private fun getTimeToday() {
        binding.tvTimeToday.format12Hour
        binding.tvTimeToday.format24Hour
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        ItemTouchHelper( object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) { //TODO hapus yang sebaris notifyItemRemove
                val deletedAlarm = alarmAdapter?.listAlarm?.get(viewHolder.adapterPosition)
                CoroutineScope(Dispatchers.IO).launch {
                    deletedAlarm?.let { alarmDao?.deleteAlarm(it) }
                    Log.i("DeletedAlarm", "onSwiped : deletedAlarm $deletedAlarm")
                }
                val alarmType = deletedAlarm?.type
                alarmType?.let { alarmService?.cancelAlarm(baseContext, it) }
            }
        }).attachToRecyclerView(recyclerView)
    }
}