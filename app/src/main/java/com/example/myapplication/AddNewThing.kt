package com.example.myapplication

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.text.isDigitsOnly
import com.example.model.BusinessModel
import com.example.myapplication.databinding.ActivityAddNewThingBinding
import java.text.SimpleDateFormat
import java.util.*

class AddNewThing : AppCompatActivity() {
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setContentView(binding.root)
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
                done()
            }
        }

        if (hour_start != -1) {
            binding.timeStartEt.setText("$hour_start:$min_start")
            binding.timeEndEt.setText("$hour_end:$min_end")
            binding.businessEt.setText(name_of_business)
            Log.i("zxc", name_of_business!!)
            binding.delete.visibility = View.VISIBLE
        }
    }

    private fun init() {
        binding = ActivityAddNewThingBinding.inflate(layoutInflater)

        getData()
        connenctIcons()
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

    }

    private fun onTimeClick(edit_text: EditText) {
        val cal = Calendar.getInstance()
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
                    end.slice(3..4).isDigitsOnly())) { return false }

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
        if (check.success == false) return check

        val hour_start = start.slice(0..1).toInt()
        val min_start = start.slice(3..4).toInt()
        val hour_end = end.slice(0..1).toInt()
        val min_end = end.slice(3..4).toInt()

        check.bs =
            BusinessModel(bus_name, year!!, month!!, day!!, hour_start, min_start, hour_end, min_end)

        return check
    }

    private fun done() {
        val asd = getDataFromET()

        if (asd.success == false) return

        val i = Intent()
            .putExtra("business", asd.bs!!.name_of_business)
            .putExtra("hour_start", asd.bs!!.hour_start)
            .putExtra("min_start", asd.bs!!.min_start)
            .putExtra("hour_end", asd.bs!!.hour_end)
            .putExtra("min_end", asd.bs!!.min_end)
        setResult(1000, i)
        finish()
    }

    private fun delete() {
        Log.i("zxc", name_of_business!!)
        val i = Intent()
            .putExtra("business", name_of_business)
            .putExtra("hour_start", hour_start)
            .putExtra("min_start", min_start)
            .putExtra("hour_end", hour_end)
            .putExtra("min_end", min_end)
        setResult(1002, i)
        finish()
    }
}