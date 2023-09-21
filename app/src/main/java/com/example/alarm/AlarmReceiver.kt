package com.example.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.db.SQLDBhelper
import com.example.myapplication.DayActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.R

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent) {

        val db = SQLDBhelper(p0)

        val i = Intent(p0, MainActivity::class.java)
        val name = p1!!.getStringExtra("name")
        p1!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        Log.i("TEST_INTENT", "true")

        val bs_list = db.getBusinesses("year = ${p1.getIntExtra("year", 0)} AND month = ${p1.getIntExtra("month", 0)} AND day = ${p1.getIntExtra("day", 0)} AND hour_start = ${p1.getIntExtra("hour_start", 0)} AND hour_end = ${p1.getIntExtra("hour_end", 0)} AND min_start = ${p1.getIntExtra("min_start", 0)} AND min_end = ${p1.getIntExtra("min_end", 0)}")
        Log.i("TEST_INTENT", bs_list.toString())

        if(bs_list[0].alarm == 1) {
            val pendingIntent = PendingIntent.getActivity(p0, 0, i, PendingIntent.FLAG_MUTABLE)


            val builder = NotificationCompat.Builder(p0!!, "0")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Alarm")
                .setContentText("At this time you have scheduled: $name")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)

            val notificationManager = NotificationManagerCompat.from(p0)
            notificationManager.notify(0, builder.build())
        }
    }
}