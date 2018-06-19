# CanvasChart
自定义图表，可滑动，通过adapter设置图表中的数据
![截图](https://img-blog.csdn.net/2018061512061392?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3UwMTEzMTU5NjA=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)  
## 使用代码示例：
在需要使用过的module的gradle文件中添加:
```
implementation 'com.lzp:CanvasChartView:1.0.1'
```
如果找不到库，请在工程的gradle文件中添加：
```
allprojects {
    repositories {
        google()
        jcenter()
        maven{url 'https://dl.bintray.com/lizp/maven/'}
    }
}
```
xml中使用CanvasChartView：
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <com.lzp.chart.CanvasChartView
        android:id="@+id/canvas_chart_4"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:chartLineColor="@color/colorPrimary"
        app:chartLineStyle="curve"
        app:dashLineColor="@color/colorPrimaryDark"
        app:dataDotGravity="center"
        app:lineColor="@color/colorAccent"
        app:onlyFirstArea="true"
        app:showDataDot="true"
        app:showMarkText="true" />

</LinearLayout>
```
代码设置Adapter:
```
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
```
## 自定义属性列表
```
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <!-- BaseScrollerView的自定义属性 -->
    <declare-styleable name="BaseScrollerView">
        <!-- x轴显示的刻度的个数 -->
        <attr name="xLineMarkCount" format="integer" />
        <!-- y轴显示的刻度的个数 -->
        <attr name="yLineMarkCount" format="integer" />
        <!-- X轴和Y轴的宽度 -->
        <attr name="lineWidth" format="dimension" />
        <!-- 数据圆点的位置 -->
        <attr name="dataDotGravity" format="enum" >
            <enum name="line" value="0" />
            <enum name="center" value="1" />
        </attr>
    </declare-styleable>

    <!-- CanvasChartView的自定义属性 -->
    <declare-styleable name="CanvasChartView">
        <!-- 是否只显示第一象限 -->
        <attr name="onlyFirstArea" format="boolean" />
        <!-- x、y轴的颜色 -->
        <attr name="lineColor" format="color" />
        <!-- 图表的连线颜色 -->
        <attr name="chartLineColor" format="color" />
        <!-- 图表的连线宽度-->
        <attr name="chartLineWidth" format="dimension" />
        <!-- 图表的连线样式：直线或曲线 -->
        <attr name="chartLineStyle" format="enum">
            <enum name="linear" value="0" />
            <enum name="curve" value="1" />
        </attr>
        <!-- 圆点的宽度 -->
        <attr name="dotWidth" format="dimension" />
        <!-- 圆点的颜色 -->
        <attr name="dotColor" format="color" />
        <!-- 是否显示圆点 -->
        <attr name="showDataDot" format="boolean" />
        <!-- 虚线的颜色 -->
        <attr name="dashLineColor" format="color" />
        <!-- 虚线的宽度 -->
        <attr name="dashLineWidth" format="dimension" />
        <!-- y轴的最大刻度 -->
        <attr name="yLineMax" format="integer" />
        <!-- 绘制文字的大小 -->
        <attr name="textSize" format="dimension" />
        <!-- 绘制文字的颜色 -->
        <attr name="textColor" format="color" />
        <!-- 文字和圆点之间的间距 -->
        <attr name="textSpace" format="dimension" />
        <!-- 是否显示刻度文字 -->
        <attr name="showMarkText" format="boolean"/>
        <!-- 刻度文字大小 -->
        <attr name="markTextSize" format="dimension"/>
        <!-- 刻度文字颜色 -->
        <attr name="markTextColor" format="color"/>
    </declare-styleable>

</resources>
```
