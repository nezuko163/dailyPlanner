package com.example.myapplication

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adapter.BusinessAdapter
import com.example.db.SQLDBhelper
import com.example.model.BusinessModel
import com.example.myapplication.databinding.ActivityDayBinding

class DayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDayBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val adapter = BusinessAdapter()
    private val db = SQLDBhelper(this)


    companion object {
        const val ADDBUSINESS = 1000
        const val REDACTBUSINESS = 1001
        const val DELETEBUSINESS = 1002

        private var bs_list: ArrayList<BusinessModel> = ArrayList()
        private var date: String? = null
        private var day: Int? = null
        private var month: Int? = null
        private var year: Int? = null
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
        binding = ActivityDayBinding.inflate(layoutInflater)
        getData()
        onIconsClick()
        initRcView()
        createLauncher()
        getDataFromDb()
    }

    private fun getDataFromDb() {
        bs_list = db.getBusinesses("year = $year AND month = $month AND day = $day")
        adapter.setBusinessList(bs_list)
    }

    private fun initRcView() {
        binding.rcView.layoutManager = LinearLayoutManager(this)
        adapter.setBusinessList(bs_list)
        adapter.onItemClick = { business: BusinessModel ->
            goToAddActivityForRedact(business)
        }
        binding.rcView.adapter = adapter
    }

    private fun createLauncher() {
        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                when (result.resultCode) {
                    ADDBUSINESS -> addBusiness(result)
                    REDACTBUSINESS -> redactBusiness(result)
                    DELETEBUSINESS -> deleteBusiness(result)
                }
            }
    }

    private fun getResultFromAddActivity(result: ActivityResult): BusinessModel {
        val name_of_business = result.data?.getStringExtra("business")
        val hour_start = result.data?.getIntExtra("hour_start", -1)
        val min_start = result.data?.getIntExtra("min_start", -1)
        val hour_end = result.data?.getIntExtra("hour_end", -1)
        val min_end = result.data?.getIntExtra("min_end", -1)

        return BusinessModel(
            name_of_business!!,
            year!!,
            month!!,
            day!!,
            hour_start!!,
            min_start!!,
            hour_end!!,
            min_end!!
        )
    }

    private fun deleteBusiness(result: ActivityResult) {
        val business = getResultFromAddActivity(result)
        db.deleteBusiness(business)
        adapter.deleteBusiness(business)
    }

    private fun redactBusiness(result: ActivityResult) {
        val new_business = getResultFromAddActivity(result)

        val old_name_of_business = result.data?.getStringExtra("old_business")
        val old_hour_start = result.data?.getIntExtra("old_hour_start", -1)
        val old_min_start = result.data?.getIntExtra("old_min_start", -1)
        val old_hour_end = result.data?.getIntExtra("old_hour_end", -1)
        val old_min_end = result.data?.getIntExtra("old_min_end", -1)

        val old_business = BusinessModel(
            old_name_of_business!!,
            year!!,
            month!!,
            day!!,
            old_hour_start!!,
            old_min_start!!,
            old_hour_end!!,
            old_min_end!!
        )

        db.redactBusiness(old_business, new_business)
        adapter.redactBusiness(old_business, new_business)
    }

    private fun addBusiness(result: ActivityResult) {
        val business = getResultFromAddActivity(result)
        db.addBusiness(business)
        adapter.addBusiness(business)
    }


    private fun onIconsClick() {
        binding.backArrow.setOnClickListener { goBack() }
        binding.plus.setOnClickListener { goToAddActivity() }
        binding.delete.setOnClickListener { delete() }
    }


    private fun getData() {
        year = intent.getIntExtra("year", -1)
        month = intent.getIntExtra("month", -1)
        day = intent.getIntExtra("day", -1)
        date = intent.getStringExtra("date")
        binding.textView.text = date
    }

    private fun delete() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete daytime busiensses")
        builder.setMessage("do you want to delete daytime businesses?")

        builder.setPositiveButton("yes") { dialog: DialogInterface, _ ->
            db.deleteDayTimeBusinesses(year!!, month!!, day!!)
            adapter.deleteDayTimeBusinesses()
            Toast.makeText(this, "business have been deleted", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("no") { dialog: DialogInterface, _ ->
            dialog.dismiss()
        }
        builder.create()
        builder.show()
    }

    private fun goToAddActivity() {
        val i = Intent(this, AddNewThing::class.java)
        i.putExtra("year", year)
            .putExtra("month", "month")
            .putExtra("day", day)
        launcher.launch(i)
    }

    private fun goToAddActivityForRedact(business: BusinessModel) {
        val i = Intent(this, AddNewThing::class.java)
        i.putExtra("year", year)
            .putExtra("month", month)
            .putExtra("day", day)
            .putExtra("business", business.name_of_business)
            .putExtra("hour_start", business.hour_start)
            .putExtra("min_start", business.min_start)
            .putExtra("hour_end", business.hour_end)
            .putExtra("min_end", business.min_end)
        launcher.launch(i)
    }

    private fun goBack() {
        finish()
    }
}