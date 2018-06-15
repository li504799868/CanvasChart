package com.lzp.com.canvaschart.view4

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import com.lzp.com.canvaschart.R
import com.lzp.com.canvaschart.base.BaseDataAdapter


/**
 * Created by li.zhipeng on 2018/5/3.
 *
 *      图片滑动优化
 *
 *      1、优化Fling的效果
 *
 */
open class BaseScrollerView(context: Context, attributes: AttributeSet?, defStyleAttr: Int)
    : View(context, attributes, defStyleAttr) {

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

    constructor(context: Context) : this(context, null)

    /**
     * 绘制X轴和Y轴的宽度
     * */
    protected var lineWidth = 5f

    /**
     * x轴的刻度间隔
     *
     * 因为x周是可以滑动的，所以只有刻度的数量这一个属性
     * */
    var xLineMarkCount: Int = 5
        set(value) {
            field = value
            calculateMaxWidth()
        }

    /**
     * y轴的刻度个数
     *
     * */
    var yLineMarkCount: Int = 5

    /**
     * 绘制圆点的位置
     * */
    protected var dataDotGravity: DataDotGravity = DataDotGravity.LINE

    init {
        val typedArray = context.obtainStyledAttributes(attributes, R.styleable.BaseScrollerView)
        // 绘制X轴和Y轴的宽度
        lineWidth = typedArray.getDimensionPixelSize(R.styleable.BaseScrollerView_lineWidth, 5).toFloat()
        // 得到x轴的刻度数
        xLineMarkCount = typedArray.getInt(R.styleable.BaseScrollerView_xLineMarkCount, 5)
        // 得到y轴的刻度数
        yLineMarkCount = typedArray.getInt(R.styleable.BaseScrollerView_yLineMarkCount, 5)
        // 得到绘制数据点的位置
        dataDotGravity = if (typedArray.getInt(R.styleable.BaseScrollerView_dataDotGravity, 0) == 0) {
            DataDotGravity.LINE
        } else {
            DataDotGravity.CENTER
        }
        typedArray.recycle()
    }

    /**
     * 数据适配器
     * */
    var adapter: BaseDataAdapter? = null
        set(value) {
            field = value
            invalidate()
            value?.addObserver { _, _ ->
                // 当数据发生改变的时候，立刻重绘
                invalidate()
            }
            // 计算最大宽度
            calculateMaxWidth()
        }

    /**
     * 最大宽度，大于等于width
     * */
    private var maxWidth: Int = 0

    /**
     * 每个刻度的宽度
     * */
    protected var markWidth: Float = 0f

    /**
     * 绘制Y轴的偏移值，这个值用来绘制Y轴的文字
     * */
    protected var drawOffsetX = 0f

    /**
     * 绘制X轴的偏移值，这个值用来绘制X轴下面的文字
     * */
    protected var drawOffsetY = 0f

    /**
     * 是否能滑动
     * */
    private var canScroll: Boolean = false

    /**
     * 滚动器Scroller
     * */
    private val scroller: OverScroller = OverScroller(context)

    /**
     * 记录手指划过的距离
     * */
    private var offsetX: Float = 0f

    /**
     * 手势处理
     * */
    private val gestureDetector: GestureDetector = GestureDetector(context, ChartGesture())

    /**
     * 惯性滑动辅助类
     * */
    private val viewFling = ViewFling()

    /**
     * 计算最大宽度
     * */
    private fun calculateMaxWidth() {
        // 计算每一个刻度的宽度
        markWidth = (width - drawOffsetX - lineWidth) / xLineMarkCount
        // 得到数据的数量
        val count = adapter?.maxDataCount ?: 0
        // 如果数据点在中心位置
        if (dataDotGravity == DataDotGravity.CENTER) {
            maxWidth = if (count < xLineMarkCount) {
                canScroll = false
                width
            } else {
                canScroll = true
                width / xLineMarkCount * count
            }
        } else {
            // 如果数据点画在线上，计算是否可以滑动的时候，需要xLineMarkCount - 1
            maxWidth = if (count < xLineMarkCount - 1) {
                canScroll = false
                width
            } else {
                canScroll = true
                width / xLineMarkCount * count
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        calculateMaxWidth()
    }

    /**
     * 根据偏移值，计算绘制的数据的开始位置
     * */
    protected fun getDataStartIndex(): Int {
        // 计算已经偏移了几个刻度
        val index = if (dataDotGravity == DataDotGravity.CENTER) {
            (offsetX - drawOffsetX - markWidth / 2) / (markWidth)
        } else {
            (offsetX - drawOffsetX - markWidth) / (markWidth)
        }
        return if (index < 0) {
            0
        } else {
            index.toInt()
        }
    }


    /**
     * 根据偏移值，计算绘制的数据的结束位置
     * */
    protected fun getDataEndIndex(startIndex: Int): Int {
        // 如果绘制的是第一个，直接返回偏移值
        return Math.min(startIndex + xLineMarkCount + 2, adapter!!.maxDataCount)
    }

    /**
     * 计算canvas绘制的偏移值
     *
     * 偏移值 - 刻度值宽度 * 开始位置，相当于对刻度值宽度取模
     * */
    protected fun getCanvasOffset(): Float {
        // 计算已经偏移了几个刻度
        val index = getDataStartIndex()
        // 计算与第一个刻度的偏移值
        // 请注意这个偏移值值得刻度的虚线的偏移值，不是圆点的偏移值
        val offset = (offsetX - drawOffsetX) % markWidth
        when {
            // 如果是第一个刻度，直接返回偏移值
            index == 0 -> return getRealX(-offsetX)
            // 当绘制数据点的位置刻度的中心
            dataDotGravity == DataDotGravity.CENTER -> return when {
                // 如果正好滑动了当前绘制的第一个点，绘制的第一条虚线变成了之后的第一条虚线
                // 直接返回偏移值就可以了
                offset >= markWidth / 2 -> {
                    getRealX(-offsetX) % markWidth
                }
                // 刻度到下一个圆点的距离，绘制虚线还是上一个刻度
                // 因为要绘制与上一条的连线，所有要多减去一个刻度的宽度
                else -> {
                    getRealX(-offsetX) % markWidth - markWidth
                }
            }
            // 当绘制数据点的位置刻度的线上
            else -> return when (offset) {
                // 如果正好滑动了虚线的位置，不需要偏移值
                0f -> {
                    getRealX(-offsetX) % markWidth
                }
                // 其他情况都要绘制和上一条的虚线，所有要多减去一个刻度的宽度
                else -> {
                    getRealX(-offsetX) % markWidth - markWidth
                }
            }
        }
    }

    /**
     * 把计算的X坐标加上偏移值
     * */
    protected fun getRealX(xPos: Float): Float {
        return xPos + drawOffsetX
    }

    /**
     * 把计算的Y坐标加上偏移值
     */
    protected fun getRealY(yPos: Float): Float {
        return yPos - drawOffsetY
    }

    /**
     * 重写手势
     * */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 如果不能滑动，不处理手势滑动
        if (!canScroll) {
            return false
        }
        return gestureDetector.onTouchEvent(event)
    }

    /**
     * 检查滚动的范围是否已经越界
     *
     * @return 是否已经到了边界，如果已经到了边界，可以停止滚动
     * */
    private fun checkBounds(): Boolean {
        // 如果小于0，那么等于0
        if (offsetX < 0) {
            offsetX = 0f
            return true
        }
        // 如果已经大于了最右边界
        else if (offsetX > maxWidth - width + drawOffsetX) {
            offsetX = maxWidth - width + drawOffsetX
            return true
        }
        return false
    }

    /**
     * View销毁时，停止滑动
     * */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewFling.stop()
    }

    /**
     * 图表手势处理类
     * */
    private inner class ChartGesture : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            // 如果scroller正在滑动, 停止滑动
            if (!scroller.isFinished) {
                viewFling.stop()
            }
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            // 计算移动的位置
            offsetX += distanceX
            // 边界检查
            checkBounds()
            invalidate()
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            Log.e("lzp", "velocity is :$velocityX")
            scroller.fling(offsetX.toInt(), 0,
                    -velocityX.toInt(), velocityY.toInt(),
                    Integer.MIN_VALUE, Integer.MAX_VALUE,
                    0, 0)
            viewFling.postOnAnimation()
            return true
        }
    }

    /**
     * ViewFling滑动辅助类
     * */
    private inner class ViewFling : Runnable {

        override fun run() {
            if (scroller.computeScrollOffset()) {
                offsetX = scroller.currX.toFloat()
                val isBound = checkBounds()
                Log.e("lzp", "offsetX is :$offsetX")
                invalidate()
                if (isBound) {
                    scroller.abortAnimation()
                } else {
                    postOnAnimation()
                }
            }
        }

        /**
         * 开始滑动
         * */
        fun postOnAnimation() {
            ViewCompat.postOnAnimation(this@BaseScrollerView, this)
        }

        /**
         * 停止滑动
         * */
        fun stop() {
            removeCallbacks(this)
            scroller.abortAnimation()
        }

    }

    /**
     * 线条Style
     * */
    enum class DataDotGravity(val value: String) {

        /**
         * 线上
         * */
        LINE("LINE"),

        /**
         * 中心
         * */
        CENTER("center")

    }
}