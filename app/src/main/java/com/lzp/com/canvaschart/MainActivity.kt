package com.lzp.com.canvaschart

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.lzp.com.canvaschart.base.BaseDataAdapter
import com.lzp.com.canvaschart.base.ChartBean
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 为图标view设置数据
        val adapter = BaseDataAdapter()
        adapter.addData(listOf(ChartBean(10f, "afgy"),
                ChartBean(25f, "afgy"),
                ChartBean(50f, "afgy"),
                ChartBean(70f, "afgy"),
                ChartBean(20f, "afgy"),
                ChartBean(35f, "afgy"),
                ChartBean(5f, "afgy"),
                ChartBean(70f, "afgy"),
                ChartBean(90f, "afgy"),
                ChartBean(40f, "afgy")))

        adapter.addData(listOf(ChartBean(-15f, "afgy"),
                ChartBean(-27f, "afgy"),
                ChartBean(-20f, "afgy"),
                ChartBean(-70f, "afgy"),
                ChartBean(-20f, "afgy"),
                ChartBean(-35f, "afgy"),
                ChartBean(-5f, "afgy"),
                ChartBean(-70f, "afgy"),
                ChartBean(-90f, "afgy"),
                ChartBean(-40f, "afgy"),
                ChartBean(-27f, "afgy"),
                ChartBean(-20f, "afgy"),
                ChartBean(-70f, "afgy"),
                ChartBean(-20f, "afgy"),
                ChartBean(-35f, "afgy"),
                ChartBean(-5f, "afgy"),
                ChartBean(-70f, "afgy"),
                ChartBean(-90f, "afgy"),
                ChartBean(-40f, "afgy"),
                ChartBean(-27f, "afgy"),
                ChartBean(-20f, "afgy"),
                ChartBean(-70f, "afgy"),
                ChartBean(-20f, "afgy"),
                ChartBean(-35f, "afgy"),
                ChartBean(-5f, "afgy"),
                ChartBean(-70f, "afgy"),
                ChartBean(-90f, "afgy"),
                ChartBean(-40f, "afgy")))

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
