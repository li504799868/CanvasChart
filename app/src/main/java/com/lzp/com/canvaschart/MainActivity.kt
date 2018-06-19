package com.lzp.com.canvaschart

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lzp.com.canvaschart.base.BaseDataAdapter
import com.lzp.com.canvaschart.base.ChartBean

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 为图标view设置数据
        val adapter = BaseDataAdapter()
        adapter.addData(listOf(ChartBean(10f, "afgy", "ggg"),
                ChartBean(25f, "afgy", "2"),
                ChartBean(50f, "afgy", "3"),
                ChartBean(70f, "afgy", "4"),
                ChartBean(20f, "afgy","5"),
                ChartBean(35f, "afgy", "6"),
                ChartBean(5f, "afgy", "7"),
                ChartBean(70f, "afgy", "8"),
                ChartBean(90f, "afgy", "9"),
                ChartBean(40f, "afgy", "10")))

//        adapter.addData(listOf(ChartBean(-15f, "afgy"),
//                ChartBean(-27f, "afgy"),
//                ChartBean(-20f, "afgy"),
//                ChartBean(-70f, "afgy"),
//                ChartBean(-20f, "afgy"),
//                ChartBean(-35f, "afgy"),
//                ChartBean(-5f, "afgy"),
//                ChartBean(-70f, "afgy"),
//                ChartBean(-90f, "afgy"),
//                ChartBean(-40f, "afgy"),
//                ChartBean(-27f, "afgy"),
//                ChartBean(-20f, "afgy"),
//                ChartBean(-70f, "afgy"),
//                ChartBean(-20f, "afgy"),
//                ChartBean(-35f, "afgy"),
//                ChartBean(-5f, "afgy"),
//                ChartBean(-70f, "afgy"),
//                ChartBean(-90f, "afgy"),
//                ChartBean(-40f, "afgy"),
//                ChartBean(-27f, "afgy"),
//                ChartBean(-20f, "afgy"),
//                ChartBean(-70f, "afgy"),
//                ChartBean(-20f, "afgy"),
//                ChartBean(-35f, "afgy"),
//                ChartBean(-5f, "afgy"),
//                ChartBean(-70f, "afgy"),
//                ChartBean(-90f, "afgy"),
//                ChartBean(-40f, "afgy")))

//        canvas_chart_1.adapter = adapter
//        canvas_chart_2.adapter = adapter
        canvas_chart_3.adapter = adapter

//        canvas_chart.setOnClickListener({
//            adapter.addData(listOf(ChartBean(-15f, "afgy"),
//                    ChartBean(-27f, "afgy"),
//                    ChartBean(-20f, "afgy"),
//                    ChartBean(-70f, "afgy"),
//                    ChartBean(-20f, "afgy"),
//                    ChartBean(-35f, "afgy"),
//                    ChartBean(-5f, "afgy"),
//                    ChartBean(-70f, "afgy"),
//                    ChartBean(-90f, "afgy"),
//                    ChartBean(-40f, "afgy")))
//            adapter.notifyDataSetChanged()
//        })
    }
}
