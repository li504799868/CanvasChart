package com.lzp.com.canvaschart.view4

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.LruCache
import com.lzp.com.canvaschart.R
import com.lzp.com.canvaschart.base.ChartBean


/**
 * Created by li.zhipeng on 2018/5/2.
 *
 *      绘制图表View
 *
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
    private var lineColor: Int = Color.BLUE

    /**
     * 图表的颜色
     * */
    private var chartLineColor: Int = Color.RED

    /**
     * 图表的宽度
     * */
    private var chartLineWidth: Float = 3f

    /**
     * 图表的线条样式
     *
     * */
    private var chartLineStyle: ChartLineStyle = ChartLineStyle.LINEAR

    /**
     * 圆点的颜色
     * */
    private var dotColor: Int = Color.BLACK

    /**
     * 圆点的宽度
     * */
    private var dotWidth = 15f

    /**
     * 是否显示dot
     * */
    private var showDataDot: Boolean = true

    /**
     * 虚线的颜色
     * */
    private var dashLineColor: Int = Color.GRAY

    /**
     * 虚线的宽度
     * */
    private var dashLineWidth: Float = 2f

    /**
     * y轴的最大刻度
     * */
    private var yLineMax: Int = 100

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
    private var textSpace: Int = 0

    /**
     * 是否只显示第一象限
     * */
    private var onlyFirstArea: Boolean = false

    /**
     * Path缓存管理器
     * */
    private val pathCacheManager = PathCacheManager()

    /**
     * 文字宽度的缓存，这里可以考虑直接使用Lrucache
     * */
    private val textWidthLruCache = LruCache<String, Float>(6)

    /**
     * 是否显示刻度值
     * */
    private var showMarkText = false

    /**
     * 刻度文字的大小
     *
     * */
    private var markTextSize = 40f

    /**
     * 刻度文字的颜色
     * */
    private var markTextColor = Color.BLACK

    init {
        val typedArray = context.obtainStyledAttributes(attributes, R.styleable.CanvasChartView)
        // 绘制X轴和Y轴的颜色
        lineColor = typedArray.getColor(R.styleable.CanvasChartView_lineColor, Color.BLUE)
        // 绘制X轴和Y轴的宽度
        lineWidth = typedArray.getDimensionPixelSize(R.styleable.CanvasChartView_lineWidth, 5).toFloat()
        // 图表的颜色
        chartLineColor = typedArray.getColor(R.styleable.CanvasChartView_chartLineColor, Color.RED)
        // 图表的宽度
        chartLineWidth = typedArray.getDimensionPixelSize(R.styleable.CanvasChartView_chartLineWidth, 3).toFloat()
        // 图表的线条的样式
        val style = typedArray.getInt(R.styleable.CanvasChartView_chartLineStyle, 0)
        if (style == 1) {
            chartLineStyle = ChartLineStyle.CURVE
        }
        // 圆点的颜色
        dotColor = typedArray.getColor(R.styleable.CanvasChartView_dotColor, Color.BLACK)
        // 圆点的宽度
        dotWidth = typedArray.getDimensionPixelSize(R.styleable.CanvasChartView_dotWidth, 10).toFloat()
        // 是否显示数据点
        showDataDot = typedArray.getBoolean(R.styleable.CanvasChartView_showDataDot, true)
        // 虚线的颜色
        dashLineColor = typedArray.getColor(R.styleable.CanvasChartView_dashLineColor, Color.GRAY)
        // 虚线的宽度
        dashLineWidth = typedArray.getDimensionPixelSize(R.styleable.CanvasChartView_dashLineWidth, 2).toFloat()
        // y轴的最大刻度
        yLineMax = typedArray.getInt(R.styleable.CanvasChartView_yLineMax, 100)
        // 绘制文字的大小
        textSize = typedArray.getDimensionPixelSize(R.styleable.CanvasChartView_textSize, 40).toFloat()
        // 绘制文字的颜色
        textColor = typedArray.getColor(R.styleable.CanvasChartView_textColor, Color.BLACK)
        // 绘制文字的大小
        textSpace = typedArray.getDimensionPixelSize(R.styleable.CanvasChartView_textSpace, 0)
        // 是否只显示第一象限
        onlyFirstArea = typedArray.getBoolean(R.styleable.CanvasChartView_onlyFirstArea, false)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 保存一下canvas的状态
        canvas.save()

        // 这里要重置一下缓存，因为要开始绘制新的图标了
        pathCacheManager.resetCache()

        // 绘制X轴和Y轴
        drawXYLine(canvas)
        // 绘制X轴的虚线
        drawXDashLine(canvas)
        // 从这里开始，我们要对canvas进行偏移
        canvas.translate(getCanvasOffset() + lineWidth, -lineWidth)
        // 裁剪要绘制的区域
        // 裁剪的区域坐标记得减去偏移值，修正裁剪的位置
        canvas.clipRect(getRealX(lineWidth - getCanvasOffset()), 0f, width.toFloat() - getCanvasOffset(), getRealY(height.toFloat()))

        // 绘制每一条数据之间的间隔虚线
        drawYDashLine(canvas)
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
        if (onlyFirstArea) {
            // 绘制X轴
            canvas.drawLine(getRealX(0f), getRealY(height - lineWidth / 2), width, getRealY(height - lineWidth / 2), paint)
        } else {
            // 计算y方向上的中心位置
            val yCenter = (height - lineWidth) / 2
            // 绘制X轴
            canvas.drawLine(getRealX(0f), getRealY(yCenter), width, getRealY(yCenter), paint)
        }
    }

    /**
     * 画Y轴
     * */
    private fun drawYLine(canvas: Canvas) {
        // 计算一下X方向的偏移值
        val offsetX = getRealX(lineWidth / 2)
        if (onlyFirstArea) {

        } else {

        }
        // 绘制Y轴
        canvas.drawLine(offsetX, getRealY(0f), offsetX, getRealY(height.toFloat()), paint)
    }

    /**
     * 绘制x轴的虚线
     * */
    private fun drawXDashLine(canvas: Canvas) {
        // 设置画笔的效果
        paint.color = dashLineColor
        paint.strokeWidth = dashLineWidth
        paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 1f)
        // 画条目之间的间隔虚线，从Data的开始位置绘制到结束位置
        val startX = getRealX( lineWidth)
        val path = pathCacheManager.get()
        // 计算每一个y刻度的高度
        val yMarkHeight = getRealY(height - lineWidth) / yLineMarkCount

        for (it in 0..4) {
            path.moveTo(startX, yMarkHeight * it)
            // 减去坐标轴宽度的一半
            path.lineTo(width.toFloat(), yMarkHeight * it)
        }
        canvas.drawPath(path, paint)
    }

    /**
     * 绘制y轴的虚线
     * */
    private fun drawYDashLine(canvas: Canvas) {
        // 画条目之间的间隔虚线，从Data的开始位置绘制到结束位置
        val startIndex = getDataStartIndex()
        val endIndex = getDataEndIndex(startIndex)
        var index = startIndex
        // 这里改为从缓存中取Path对象，并且只使用一个Path绘制所有虚线
        val path = pathCacheManager.get()
        while (index <= endIndex) {
            val startX = markWidth * (index - startIndex + 1) - dashLineWidth / 2
            path.moveTo(startX, 0f)
            // 减去坐标轴宽度的一半
            path.lineTo(startX, height.toFloat() - lineWidth)
            index++
        }
        canvas.drawPath(path, paint)
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
     *
     * 优化点：这里我们绘制都会创建出多个Path对象，会造成内存的浪费，影响绘制效率
     *        可以使用缓存避免这个问题
     * */
    private fun drawItemData(canvas: Canvas, data: List<ChartBean>) {
        val path = pathCacheManager.get()
        val dotPath = pathCacheManager.get()
        // 绘制item之间的连线
        addItemLine(canvas, data, path, dotPath)
        // 绘制曲线
        paint.style = Paint.Style.STROKE
        paint.color = chartLineColor
        paint.strokeWidth = chartLineWidth
        canvas.drawPath(path, paint)
        if (showDataDot) {
            // 绘制圆点
            paint.style = Paint.Style.FILL
            paint.color = dotColor
            canvas.drawPath(dotPath, paint)
        }
    }

    /**
     * 计算数据之间的连线和数据点
     * */
    private fun addItemLine(canvas: Canvas, data: List<ChartBean>, path: Path, dotPath: Path) {
        // 绘制开始位置到结束位置的数据
        val startIndex = getDataStartIndex()
        val endIndex = getDataEndIndex(startIndex)
        var index = startIndex
        while (index < endIndex) {
            // 因为数据的长度不统一，所以这里要做数据的场地检查
            if (index >= data.size) {
                break
            }
            // 计算每一个点的位置
            val item = data[index]
            // 计算绘制的x坐标
            val xPos = calculateXPosition(startIndex, index)
            // 计算绘制的y坐标
            val yPos = calculateYPosition(item)
            // 如果不显示数据圆点，省去添加圆点的操作
            if (showDataDot) {
                // 添加数据点Path的圆点
                dotPath.addCircle(xPos, yPos, dotWidth, Path.Direction.CW)
            }
            // 绘制文字
            drawText(canvas, item, xPos, yPos)

            if (index == startIndex) {
                path.moveTo(xPos, yPos)
            } else {
                // 直线
                if (chartLineStyle == ChartLineStyle.LINEAR) {
                    path.lineTo(xPos, yPos)
                }
                // 曲线
                else if (chartLineStyle == ChartLineStyle.CURVE) {
                    curveTo(index, data, startIndex, xPos, yPos, path)
                }
            }
            index++
        }
    }

    /**
     * 平滑到下一个点
     * */
    private fun curveTo(index: Int, data: List<ChartBean>, startIndex: Int, xPos: Float, yPos: Float, path: Path) {
        // 如果是最后一个点，不需要计算
        if (index + 1 >= data.size) {
            return
        }
        // 结束的点
        val nextXPos = calculateXPosition(startIndex, index + 1)
        val nextYPos = calculateYPosition(data[index + 1])
        val wt = xPos + markWidth / 2
        path.cubicTo(wt, yPos, wt, nextYPos, nextXPos, nextYPos)
    }

    /**
     * 计算item的x坐标
     * */
    private fun calculateXPosition(startIndex: Int, index: Int): Float = (markWidth / 2 + (index - startIndex) * markWidth).toFloat()

    /**
     * 计算每一个数据点在Y轴上的坐标
     * */
    private fun calculateYPosition(value: ChartBean): Float {
        // 计算比例
        val scale = value.number / yLineMax
        // 计算y方向上的中心位置
        val yCenter = if (onlyFirstArea) {
            height - lineWidth
        } else {
            (height - lineWidth) / 2
        }
        // 如果小于0
        return yCenter - yCenter * scale
    }

    /**
     * 绘制文字
     *
     * */
    private fun drawText(canvas: Canvas, item: ChartBean, xPos: Float, yPos: Float) {
        val text = item.text
        paint.textSize = textSize
        paint.color = textColor
        paint.style = Paint.Style.FILL
        // 文字的宽度优先从缓存获取
        val textWidth = getTextWidth(text)
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

    /**
     * 从缓冲中获取文字的宽度
     * */
    private fun getTextWidth(key: String): Float {
        var width = textWidthLruCache.get(key)
        // 如果缓存中没有这个文字的宽度，先测量，然后添加到缓存中
        if (width == null) {
            width = paint.measureText(key)
            textWidthLruCache.put(key, width)
        }
        return width
    }

    /**
     * 线条Style
     * */
    enum class ChartLineStyle(val value: String) {

        /**
         * 直线
         * */
        LINEAR("linear"),

        /**
         * 曲线
         * */
        CURVE("curve")

    }


}