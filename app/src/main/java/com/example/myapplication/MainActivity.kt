package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.db.SQLDBhelper
import com.example.myapplication.databinding.ActivityMainBinding
import java.util.Calendar



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var calendar: Calendar
    private var selected_date: String? = null
    private var _day: Int? = null
    private var _month: Int? = null
    private var _year: Int? = null


    companion object {
        val MONTHS = mapOf(
            1 to "January",
            2 to "February",
            3 to "March",
            4 to "April",
            5 to "May",
            6 to "June",
            7 to "July",
            8 to "August",
            9 to "September",
            10 to "October",
            11 to "November",
            12 to "December"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setContentView(binding.root)

    }

    private fun init() {
        calendar = Calendar.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setListeners()
    }

    private fun setListeners() {
        binding.cview.setOnDateChangeListener { view, year, month, dayOfMonth ->
            this.selectDate(year, month, dayOfMonth)
        }

        binding.btn.setOnClickListener {
            this.goToNextActiv()
        }
    }

    private fun goToNextActiv() {
        if (this.isDateSelected()) {
            startActivity(
                Intent(this, DayActivity::class.java)
                    .putExtra("year", this._year)
                    .putExtra("month", this._month)
                    .putExtra("day", this._day)
                    .putExtra("date", "$_day ${MONTHS[_month!! + 1]}")
            )
        } else {
            Toast.makeText(this, "выберите дату", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isDateSelected(): Boolean {
        return selected_date != null
    }

    private fun selectDate(year: Int, month: Int, day: Int) {
        calendar.set(year, month, day)
        binding.cview.date = calendar.timeInMillis

        this.selected_date = "${year}-${month}-${day}"

        this._day = day
        this._month = month
        this._year = year

        binding.tv.text = "$day ${MONTHS[month + 1]}"
    }
}