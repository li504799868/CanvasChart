package com.lzp.com.canvaschart.view2

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.lzp.com.canvaschart.base.ChartBean


/**
 * Created by li.zhipeng on 2018/5/2.
 */
class CanvasChartView(context: Context, attributes: AttributeSet?, defStyleAttr: Int)
    : BaseScrollerView(context, attributes, defStyleAttr) {

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

    constructor(context: Context) : this(context, null)

    /**
     * 画笔
     *
     * 设置抗锯齿和防抖动
     * */
    private val paint: Paint by lazy {
        val field = Paint()
        field.isAntiAlias = true
        field.isDither = true
        field
    }

    /**
     * 绘制X轴和Y轴的颜色
     *
     *  默认是系统自带的蓝色
     * */
    var lineColor: Int = Color.BLUE

    /**
     * 绘制X轴和Y轴的宽度
     * */
    var lineWidth = 5f

    /**
     * 图表的颜色
     * */
    var chartLineColor: Int = Color.RED

    /**
     * 图表的宽度
     * */
    var chartLineWidth: Float = 3f

    /**
     * 圆点的宽度
     * */
    var dotWidth = 15f

    /**
     * 圆点的颜色
     * */
    var dotColor: Int = Color.BLACK

    /**
     * 虚线的颜色
     * */
    var dashLineColor: Int = Color.GRAY

    /**
     * 虚线的颜色
     * */
    var dashLineWidth: Float = 2f

    /**
     * y轴的最大刻度
     * */
    var yLineMax: Int = 100

    /**
     * 绘制文字的大小
     * */
    var textSize: Float = 40f

    /**
     * 绘制文字的颜色
     * */
    var textColor: Int = Color.BLACK

    /**
     * 文字和圆点之间的间距
     * */
    var textSpace: Int = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 保存一下canvas的状态
        canvas.save()
        // 绘制X轴和Y轴
        drawXYLine(canvas)

        // 从这里开始，我们要对canvas进行偏移
        canvas.translate(getCanvasOffset(), 0f)

        // 绘制每一条数据之间的间隔虚线
        drawDashLine(canvas)
        // 绘制数据
        drawData(canvas)
        // 恢复一下canvas的状态
        canvas.restore()
    }

    /**
     * 绘制X轴和Y轴
     *
     * x轴位于中心位置，值为0
     * y轴位于最最左边，与x轴交叉，交叉点为0
     * */
    private fun drawXYLine(canvas: Canvas) {
        // 设置颜色和宽度
        paint.color = lineColor
        paint.strokeWidth = lineWidth
        paint.style = Paint.Style.STROKE
        drawXLine(canvas)
        drawYLine(canvas)
    }

    /**
     * 画X轴
     * */
    private fun drawXLine(canvas: Canvas) {
        val width = width.toFloat()
        // 计算y方向上的中心位置
        val yCenter = (height - lineWidth) / 2
        // 绘制X轴
        canvas.drawLine(0f, yCenter, width, yCenter, paint)
    }

    /**
     * 画Y轴
     * */
    private fun drawYLine(canvas: Canvas) {
        // 计算一下Y轴的偏移值
        val offsetY = lineWidth / 2
        // 绘制Y轴
        canvas.drawLine(offsetY, 0f, offsetY, height.toFloat(), paint)
    }

    /**
     * 绘制数据之间
     *
     * 根据偏移值计算要绘制的区域
     * */
    private fun drawDashLine(canvas: Canvas) {
        // 通过x轴的刻度间隔，计算x轴坐标
        val xItemSpace = width / xLineMarkCount.toFloat()
        // 设置画笔的效果
        paint.color = dashLineColor
        paint.strokeWidth = dashLineWidth
        paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 1f)
        // 画条目之间的间隔虚线，从Data的开始位置绘制到结束位置
        val startIndex = getDataStartIndex()
        val endIndex = getDataEndIndex(startIndex)
        var index = startIndex
        while (index < endIndex) {
            val startY = xItemSpace * (index - startIndex)
            val path = Path()
            path.moveTo(startY, 0f)
            path.lineTo(startY, height.toFloat())
            canvas.drawPath(path, paint)
            index++
        }
    }

    /**
     * 绘制数据曲线
     * */
    private fun drawData(canvas: Canvas) {
        // 设置画笔样式
        paint.pathEffect = null
        // 得到数据列表, 如果是null，取消绘制
        val dataList = adapter?.getData() ?: return
        // 绘制每一条数据列表
        for (item in dataList) {
            drawItemData(canvas, item)
        }
    }

    /**
     * 绘制一条数据曲线
     * */
    private fun drawItemData(canvas: Canvas, data: List<ChartBean>) {
        // 通过x轴的刻度间隔，计算x轴坐标
        val xItemSpace = width / xLineMarkCount
        val path = Path()
        val dotPath = Path()
        // 绘制开始位置到结束位置的数据
        val startIndex = getDataStartIndex()
        val endIndex = getDataEndIndex(startIndex)
        var index = startIndex
        while (index < endIndex) {
            // 因为数据的长度不统一，所以这里要做数据的场地检查
            if (index >= data.size){
                break
            }
            // 计算每一个点的位置
            val item = data[index]
            // 计算绘制的x坐标
            val xPos = (xItemSpace / 2 + (index - startIndex) * xItemSpace).toFloat()
            // 计算绘制的y坐标
            val yPos = calculateYPosition(item)
            // 设置Path路径
            if (index == startIndex) {
                path.moveTo(xPos, yPos)
            } else {
                path.lineTo(xPos, yPos)
            }
            dotPath.addCircle(xPos, yPos, dotWidth, Path.Direction.CW)
            // 绘制文字
            drawText(canvas, item, xPos, yPos)
            index++
        }
        // 绘制曲线
        paint.style = Paint.Style.STROKE
        paint.color = chartLineColor
        paint.strokeWidth = chartLineWidth
        canvas.drawPath(path, paint)
        // 绘制圆点
        paint.color = dotColor
        paint.style = Paint.Style.FILL
        canvas.drawPath(dotPath, paint)
    }

    /**
     * 计算每一个数据点在Y轴上的坐标
     * */
    private fun calculateYPosition(value: ChartBean): Float {
        // 计算比例
        val scale = value.number / yLineMax
        // 计算y方向上的中心位置
        val yCenter = (height - lineWidth) / 2
        // 如果小于0
        return yCenter - yCenter * scale
    }

    /**
     * 绘制文字
     * */
    private fun drawText(canvas: Canvas, item: ChartBean, xPos: Float, yPos: Float) {
        val text = item.text
        paint.textSize = textSize
        paint.color = textColor
        paint.style = Paint.Style.FILL
        val textWidth = paint.measureText(text)
        val fontMetrics = paint.fontMetrics
        // 文字自带的间距，不理解的可以查一下：如何绘制文字居中
        val offset = fontMetrics.ascent + (fontMetrics.ascent - fontMetrics.top)
        if (item.number > 0) {
            // 要把文字自带的间距减去，统一和圆点之间的间距
            canvas.drawText(text, xPos - textWidth / 2, yPos - dotWidth - fontMetrics.descent - textSpace, paint)
        } else {
            // 要把文字自带的间距减去，统一和圆点之间的间距
            canvas.drawText(text, xPos - textWidth / 2, yPos + dotWidth - offset + textSpace, paint)
        }
    }

}