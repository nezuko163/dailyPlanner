package com.example.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.model.BusinessModel
import com.example.myapplication.R
import com.example.myapplication.databinding.BusinessItemBinding

class BusinessAdapter : RecyclerView.Adapter<BusinessAdapter.BusinessHolder>() {
    private var business_list = ArrayList<BusinessModel>()
    var onItemClick: ((BusinessModel) -> Unit)? = null

    inner class BusinessHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = BusinessItemBinding.bind(item)

        init {
            item.setOnClickListener {
                onItemClick?.invoke(business_list[adapterPosition])
            }
        }

        fun bind(business: BusinessModel) {
            binding.apply {
                timeStart.text = "${business.hour_start}:" + "0".repeat(2 - business.min_start.toString().length) + "${business.min_start}"
                timeEnd.text = "${business.hour_end}:" +  "0".repeat(2 - business.min_start.toString().length) + "${business.min_end}"
                businessName.text = business.name_of_business
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.business_item, parent, false)
        return BusinessHolder(view)
    }

    override fun getItemCount() = business_list.size

    override fun onBindViewHolder(holder: BusinessHolder, position: Int) {
        holder.bind(business_list[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addBusiness(business: BusinessModel) {
        business_list.add(business)
        sortBusinessList()
        notifyDataSetChanged()
    }
    fun equals(yeah: BusinessModel, other: BusinessModel) : Boolean {
        return yeah.name_of_business == other.name_of_business &&
                yeah.year == other.year &&
                yeah.month == other.month &&
                yeah.day == other.day &&
                yeah.hour_start == other.hour_start && yeah.min_start == other.min_start &&
                yeah.hour_end == other.hour_end &&
                yeah.min_end == other.min_end
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteBusiness(business: BusinessModel) {
        val iterator = business_list.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (equals(item, business)) iterator.remove()
        }
        sortBusinessList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteDayTimeBusinesses() {
        business_list.clear()
        sortBusinessList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBusinessList(_bs_list: ArrayList<BusinessModel>) {
        business_list = _bs_list
        sortBusinessList()
        for (i in business_list) {
            println(i.name_of_business + " ${i.hour_start} ${i.min_start}")
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun redactBusiness(business_old: BusinessModel, business_new: BusinessModel) {
        this.deleteBusiness(business_old)
        business_list.add(business_new)
        sortBusinessList()
        notifyDataSetChanged()
    }

    private fun sortBusinessList() {
        business_list.sortWith(compareBy({ it.hour_start }, { it.min_start }))

    }
}