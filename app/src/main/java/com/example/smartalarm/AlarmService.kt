package com.example.smartalarm


import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.smartalarm.data.Alarm
import java.util.*
import javax.security.auth.login.LoginException


class AlarmService : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra(EXTRA_MESSAGE)
        val type = intent?.getIntExtra(EXTRA_TYPE, 0)

        val title = when (type) {
            TYPE_ONETIME -> "Hii time to $message, Good Luck!!"
            TYPE_Repeating -> "Hii time to $message,dont worry I will remembering you every day"
            else -> {
                "umm, something wrong"
            }
        }
        val requestCode = when (type) {
            TYPE_ONETIME -> ID_ONETIME
            TYPE_Repeating -> ID_REPEATING
            else -> -1
        }
        if (message != null && context != null) {
            showNotification(
                context,
                title,
                "bye",
                requestCode
            )
        }
    }

    fun cancelAlarm(context: Context, type: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmService::class.java)
        val requestCode = when (type) {
            TYPE_ONETIME -> ID_ONETIME
            TYPE_Repeating -> ID_REPEATING
            else -> Log.i("CancelAlarm", "Unknow type of Alarm")
        }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        if (type == TYPE_ONETIME) {
            Toast.makeText(context, "One Time Alarm Canceled", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Repeating Time Alarm Canceled", Toast.LENGTH_LONG).show()
        }
    }

    fun setRepeatingAlarm(context: Context, type: Int, time: String, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmService::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)

        val timeArray = time.split(":").toTypedArray()

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context, "Succes set oneRepeatingAlarm", Toast.LENGTH_SHORT).show()

        Log.i("setAlarmRingin", "setRepeatingAlarm: alarm will ringing on ${calendar.time}")
    }

    fun setOneTimeAlarm(context: Context, type: Int, date: String, time: String, note: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmService::class.java)
        intent.putExtra(EXTRA_MESSAGE, note)
        intent.putExtra(EXTRA_TYPE, type)


        val dateArray = date.split("-").toTypedArray()
        val timeArray = time.split(":").toTypedArray()

        //date
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]))
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1)
        calendar.set(Calendar.DATE, Integer.parseInt(dateArray[0]))
        //time
        calendar.set(Calendar.HOUR, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_ONETIME, intent, 0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context, "Succes set oneTimeAlarm", Toast.LENGTH_SHORT).show()

        Log.i("setAlarmRingin", "setOneTimeAlarm: alarm will ringing on ${calendar.time}")
    }


    private fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationid: Int
    ) {
        val ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val CHANNEL_ID = "chanelId"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_one_time)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(ringtone)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Smart Alarm",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        val notif = builder.build()
        notificationManager.notify(notificationid, notif)

    }

    companion object {
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_TYPE = "type"

        const val ID_ONETIME = 101
        const val ID_REPEATING = 102

        const val TYPE_ONETIME = 1
        const val TYPE_Repeating = 0
    }

}