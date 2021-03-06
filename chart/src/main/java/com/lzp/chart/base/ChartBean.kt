package com.lzp.chart.base

/**
 * Created by li.zhipeng on 2018/5/2.
 *
 *  图标的数据bean
 *
 *  number ：数据
 *  text：显示的文字
 */
data class ChartBean(val number: Float, val text: String, val markText: String){

    constructor(number: Float, text: String): this(number, text, "")

}