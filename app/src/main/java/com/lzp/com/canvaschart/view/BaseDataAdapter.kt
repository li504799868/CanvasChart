package com.lzp.com.canvaschart.view

import java.util.*

/**
 * Created by li.zhipeng on 2018/5/2.
 *
 *      图标的数据适配器
 */
class BaseDataAdapter : Observable() {

    /**
     * 保存数据
     * */
    private val dataList: ArrayList<List<Int>> = ArrayList()

    /**
     * 添加数据
     * */
    fun addData(data: List<Int>) {
        dataList.add(data)
        notifyDataSetChanged()
    }

    fun removeAt(index: Int) {
        dataList.removeAt(index)
        notifyDataSetChanged()
    }

    fun remove(data: List<Int>) {
        dataList.remove(data)
        notifyDataSetChanged()
    }

    fun getData(): ArrayList<List<Int>> = dataList

    fun notifyDataSetChanged() {
        setChanged()
        notifyObservers()
    }
}