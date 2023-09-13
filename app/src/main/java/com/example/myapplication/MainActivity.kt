package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapter.BusinessAdapter
import com.example.db.SQLDBhelper
import com.example.model.BusinessModel
import com.example.myapplication.databinding.ActivityMainBinding
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var calendar: Calendar
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val adapter = BusinessAdapter()
    private val db = SQLDBhelper(this)
    private var bs_list: ArrayList<BusinessModel> = ArrayList()

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

        const val REDACTBUSINESS = 1001
        const val DELETEBUSINESS = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setContentView(binding.root)
    }

    override fun onDestroy() {
        db.close()
        adapter.deleteDayTimeBusinesses()
        super.onDestroy()
    }

    private fun init() {
        calendar = Calendar.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setListeners()
        initRcView()
        createLauncher()
    }

    private fun setListeners() {
        binding.cview.setOnDateChangeListener { view, year, month, dayOfMonth ->
            selectDate(year, month, dayOfMonth)
            getDataFromDb()
        }

        binding.btn.setOnClickListener {
            goToNextActiv()
        }
    }

    private fun initRcView() {
        binding.rcView.layoutManager = LinearLayoutManager(this)
        adapter.setBusinessList(bs_list)
        adapter.onItemClick = { business: BusinessModel ->
            goToAddActivityForRedact(business)
        }

        adapter.onStarClick = { business: BusinessModel ->
            db.invertPriority(business)
            if (business.priority == 0) {
                adapter.deleteBusiness(business)
            }
        }

        adapter.onClockClick = { business: BusinessModel ->
            db.invertAlarm(business)
        }

        binding.rcView.adapter = adapter
    }

    private fun createLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                when (result.resultCode) {
                    REDACTBUSINESS -> redactBusiness(result)
                    DELETEBUSINESS -> deleteBusiness(result)
                }
            }
    }

    private fun redactBusiness(result: ActivityResult) {
        val new_business = getResultFromAddActivity(result)

        val old_name_of_business = result.data?.getStringExtra("old_business")
        val old_hour_start = result.data?.getIntExtra("old_hour_start", -1)
        val old_min_start = result.data?.getIntExtra("old_min_start", -1)
        val old_hour_end = result.data?.getIntExtra("old_hour_end", -1)
        val old_min_end = result.data?.getIntExtra("old_min_end", -1)
        val old_priority = result.data?.getIntExtra("old_priority", 0)
        val old_alarm = result.data?.getIntExtra("old_alarm", 0)

        try {
            val old_business = BusinessModel(
                old_name_of_business!!,
                _year!!,
                _month!!,
                _day!!,
                old_hour_start!!,
                old_min_start!!,
                old_hour_end!!,
                old_min_end!!,
                old_priority!!,
                old_alarm!!
            )

            db.redactBusiness(old_business, new_business)
            adapter.redactBusiness(old_business, new_business)

        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    private fun deleteBusiness(result: ActivityResult) {
        val business = getResultFromAddActivity(result)
        db.deleteBusiness(business)
        adapter.deleteBusiness(business)
    }

    private fun getResultFromAddActivity(result: ActivityResult): BusinessModel {
        val name_of_business = result.data?.getStringExtra("business")
        val hour_start = result.data?.getIntExtra("hour_start", -1)
        val min_start = result.data?.getIntExtra("min_start", -1)
        val hour_end = result.data?.getIntExtra("hour_end", -1)
        val min_end = result.data?.getIntExtra("min_end", -1)
        val priority = result.data?.getIntExtra("priority", 0)
        val alarm = result.data?.getIntExtra("alarm", 0)


        return BusinessModel(
            name_of_business!!,
            _year!!,
            _month!!,
            _day!!,
            hour_start!!,
            min_start!!,
            hour_end!!,
            min_end!!,
            priority!!,
            alarm!!
        )
    }

    private fun getDataFromDb() {
        bs_list = db.getBusinesses(
            "year = $_year AND " +
                    "month = $_month AND " +
                    "day = $_day AND " +
                    "priority = 1"
        )
        adapter.setBusinessList(bs_list)
    }

    private fun goToAddActivityForRedact(business: BusinessModel) {
        val i = Intent(this, AddNewThing::class.java)
        i.putExtra("year", _year)
            .putExtra("month", _month)
            .putExtra("day", _day)
            .putExtra("business", business.name_of_business)
            .putExtra("hour_start", business.hour_start)
            .putExtra("min_start", business.min_start)
            .putExtra("hour_end", business.hour_end)
            .putExtra("min_end", business.min_end)
            .putExtra("priority", business.priority)
            .putExtra("alarm", business.alarm)
        launcher.launch(i)
    }

    private fun goToNextActiv() {
        if (this.isDateSelected()) {
            startActivity(
                Intent(this, DayActivity::class.java)
                    .putExtra("year", _year)
                    .putExtra("month", _month)
                    .putExtra("day", _day)
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
    }
}