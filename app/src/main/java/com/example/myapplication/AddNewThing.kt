package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.text.isDigitsOnly
import com.example.alarm.AlarmReceiver
import com.example.db.SQLDBhelper
import com.example.model.BusinessModel
import com.example.myapplication.databinding.ActivityAddNewThingBinding
import java.text.SimpleDateFormat
import java.util.*

class AddNewThing : AppCompatActivity() {
    private lateinit var cal: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    private lateinit var binding: ActivityAddNewThingBinding
    private var date: String? = null
    private var year: Int? = null
    private var month: Int? = null
    private var day: Int? = null

    private var name_of_business: String? = null
    private var hour_start: Int? = null
    private var min_start: Int? = null
    private var hour_end: Int? = null
    private var min_end: Int? = null
    private var priority: Int? = null
    private var alarm: Int? = null
    private var old_business: BusinessModel? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setContentView(binding.root)


        if (hour_start != -1) {
            binding.timeStartEt.setText(
                "0".repeat(2 - hour_start.toString().length) + "$hour_start:" + "0".repeat(
                    2 - min_start.toString().length
                ) + "$min_start"
            )
            binding.timeEndEt.setText(
                "0".repeat(2 - hour_end.toString().length) + "$hour_end:" + "0".repeat(
                    2 - min_end.toString().length
                ) + "$min_end"
            )
            binding.businessEt.setText(name_of_business)
            binding.delete.visibility = View.VISIBLE
        }
        if (priority == 1) binding.chbx1.isChecked = true
        if (alarm == 1) binding.chbx2.isChecked = true
    }

    private fun init() {
        binding = ActivityAddNewThingBinding.inflate(layoutInflater)
        setListeners()
        getData()
        connenctIcons()
    }

    private fun setListeners() {
        binding.apply {
            tv.text = date

            delete.setOnClickListener {
                delete()
            }

            businessEt.setOnFocusChangeListener { v, hasFocus ->
                // надо как-то сделать чтобы цвет иконки менялся
            }

            timeEndEt.setOnFocusChangeListener { v, hasFocus ->
                // надо как-то сделать чтобы цвет иконки менялся
                if (timeEndEt.hasFocus()) timeEndEt.performClick()
            }

            timeEndEt.setOnClickListener {
                onTimeClick(timeEndEt)
            }

            timeStartEt.setOnFocusChangeListener { v, hasFocus ->
                // надо как-то сделать чтобы цвет иконки менялся
                if (timeStartEt.hasFocus()) timeStartEt.performClick()
            }

            timeStartEt.setOnClickListener {
                onTimeClick(timeStartEt)
            }

            button.setOnClickListener {
                if (hour_start == -1) {
                    done()
//                    addData()
//                    createNotificationChannel()
//                    setAlarm()
                } else {
                    redact()
                }
            }

            chbx1.setOnCheckedChangeListener { _, checked ->
                priority = if (checked) 1 else 0
            }
            chbx2.setOnCheckedChangeListener { _, checked ->
//                alarm = if (checked) 1 else 0
                if(checked) {
                    alarm = 1
                }
                else {
                    alarm = 0
                }
            }
        }
    }

    private fun getData() {
        date = intent.getStringExtra("date")
        year = intent.getIntExtra("year", 2022)
        day = intent.getIntExtra("day", 2)
        month = intent.getIntExtra("month", 3)

        name_of_business = intent.getStringExtra("business")
        hour_start = intent.getIntExtra("hour_start", -1)
        min_start = intent.getIntExtra("min_start", -1)
        hour_end = intent.getIntExtra("hour_end", -1)
        min_end = intent.getIntExtra("min_end", -1)
        priority = intent.getIntExtra("priority", 0)
        alarm = intent.getIntExtra("alarm", 0)

        if (hour_start != -1) {
            old_business = BusinessModel(
                name_of_business!!,
                year!!,
                month!!,
                day!!,
                hour_start!!,
                min_start!!,
                hour_end!!,
                min_end!!,
                priority!!,
                alarm!!
            )
            binding.textView4.text = resources.getString(R.string.Redact)
        } else {
            binding.textView4.text = resources.getString(R.string.Add)
        }
    }

    private fun onTimeClick(edit_text: EditText) {
        cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val dateFormated = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
            edit_text.setText(dateFormated.toString())
        }
        TimePickerDialog(
            this,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun connenctIcons() {
        binding.backArrow.setOnClickListener {
            finish()
        }
    }

    private fun checkDataFromET(bus_name: String, start: String, end: String): Boolean {
        if (bus_name.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "введите значения", Toast.LENGTH_SHORT).show()
            return false
        }

        if (start.length != 5 || end.length != 5) return false

        if (!(start.slice(0..1).isDigitsOnly() &&
                    start.slice(3..4).isDigitsOnly() &&
                    end.slice(0..1).isDigitsOnly() &&
                    end.slice(3..4).isDigitsOnly())
        ) {
            return false
        }

        return true
    }

    inner class ReturnValues {
        var success: Boolean = true
        var bs: BusinessModel? = null
    }

    private fun getDataFromET(): ReturnValues {
        val check = ReturnValues()

        val bus_name = binding.businessEt.text.toString()
        val start = binding.timeStartEt.text.toString()
        val end = binding.timeEndEt.text.toString()

        check.success = checkDataFromET(bus_name, start, end)
        if (!check.success) return check

        val hour_start = start.slice(0..1).toInt()
        val min_start = start.slice(3..4).toInt()
        val hour_end = end.slice(0..1).toInt()
        val min_end = end.slice(3..4).toInt()

        val priority: Int
        val alarm: Int

        val a = getDataFromChkbx()

        priority = a.first
        alarm = a.second

        check.bs =
            BusinessModel(
                bus_name,
                year!!,
                month!!,
                day!!,
                hour_start,
                min_start,
                hour_end,
                min_end,
                priority,
                alarm
            )

        return check
    }

    private fun getDataFromChkbx(): Pair<Int, Int> =
        Pair(
            if (binding.chbx1.isChecked) 1 else 0,
            if (binding.chbx2.isChecked) 1 else 0
        )

    private fun done() {
        val asd = getDataFromET()

        if (!asd.success) return

        val i = Intent()
            .putExtra("business", asd.bs!!.name_of_business)
            .putExtra("hour_start", asd.bs!!.hour_start)
            .putExtra("min_start", asd.bs!!.min_start)
            .putExtra("hour_end", asd.bs!!.hour_end)
            .putExtra("min_end", asd.bs!!.min_end)
            .putExtra("priority", asd.bs!!.priority)
            .putExtra("alarm", asd.bs!!.alarm)
        setResult(1000, i)
        finish()
    }

    private fun delete() {
        val i = Intent()
            .putExtra("business", name_of_business)
            .putExtra("hour_start", hour_start)
            .putExtra("min_start", min_start)
            .putExtra("hour_end", hour_end)
            .putExtra("min_end", min_end)
            .putExtra("priority", priority)
            .putExtra("alarm", alarm)
        setResult(1002, i)
        finish()
    }

    private fun redact() {
        val new_bs = getDataFromET().bs ?: return
        val i = Intent()
            .putExtra("old_hour_start", old_business?.hour_start)
            .putExtra("old_min_start", old_business?.min_start)
            .putExtra("old_hour_end", old_business?.hour_end)
            .putExtra("old_min_end", old_business?.min_end)
            .putExtra("old_business", old_business?.name_of_business)
            .putExtra("old_priority", old_business?.priority)
            .putExtra("old_alarm", old_business?.alarm)

            .putExtra("hour_start", new_bs.hour_start)
            .putExtra("min_start", new_bs.min_start)
            .putExtra("hour_end", new_bs.hour_end)
            .putExtra("min_end", new_bs.min_end)
            .putExtra("business", new_bs.name_of_business)
            .putExtra("priority", new_bs.priority)
            .putExtra("alarm", new_bs.alarm)

        setResult(1001, i)
        finish()
    }
}