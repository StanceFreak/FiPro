package com.stancefreak.monkob.views.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.stancefreak.monkob.R
import com.stancefreak.monkob.remote.model.response.LastRetrieve

class TypeSpinnerAdapter(
    private val context: Context
): BaseAdapter() {

    private val typeList = ArrayList<LastRetrieve>()
    override fun getCount(): Int {
        return this.typeList.size
    }

    override fun getItem(position: Int): Any {
        return this.typeList[position].label

    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup?): View? {
        var cv = convertView
        if (cv == null) {
            val inflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = inflater.inflate(R.layout.item_list_spinner, viewGroup, false)
        }
        val spItem = cv?.findViewById<TextView>(R.id.sp_item_label)

        spItem?.text = this.typeList[i].label

        return cv
    }

    fun setData(dataList: List<LastRetrieve>) {
        this.typeList.apply {
            clear()
            addAll(dataList)
        }
        notifyDataSetChanged()
    }
}