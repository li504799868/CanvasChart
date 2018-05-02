package com.lzp.com.canvaschart

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.lzp.com.canvaschart.view.BaseDataAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 为图标view设置数据
        val adapter = BaseDataAdapter()
        adapter.addData(listOf(10, 25, 50, 70, -20, -35, 0, 70, 90, 40))
        canvas_chart.adapter = adapter
    }
}
