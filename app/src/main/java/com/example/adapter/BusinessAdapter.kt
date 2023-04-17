package com.example.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.example.db.SQLDBhelper
import com.example.model.BusinessModel
import com.example.myapplication.DayActivity
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
                timeEnd.text = "${business.hour_start}:${business.min_start}"
                timeStart.text = "${business.hour_end}:${business.min_end}"
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
        holder.itemView.tag = position

    }

    @SuppressLint("NotifyDataSetChanged")
    fun addBusiness(business: BusinessModel) {
        business_list.add(business)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteBusiness(business: BusinessModel) {
        fun equals(yeah: BusinessModel, other: BusinessModel) : Boolean {
            return yeah.name_of_business == other.name_of_business &&
                    yeah.year == other.year &&
                    yeah.month == other.month &&
                    yeah.day == other.day &&
                    yeah.hour_start == other.hour_start && yeah.min_start == other.min_start &&
                    yeah.hour_end == other.hour_end &&
                    yeah.min_end == other.min_end
        }

        for (x in business_list) {
            if (equals(x, business)) {
                business_list.remove(x)
            }
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteDayTimeBusinesses() {
        business_list.clear()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBusinessList(_bs_list: ArrayList<BusinessModel>) {
        business_list = _bs_list
        notifyDataSetChanged()
    }
}