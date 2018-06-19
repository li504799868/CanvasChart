package com.lzp.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.lzp.chart.base.BaseDataAdapter
import com.lzp.chart.base.ChartBean

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
    }
}
