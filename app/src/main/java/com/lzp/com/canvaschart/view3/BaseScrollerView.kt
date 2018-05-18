package com.lzp.com.canvaschart.view3

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
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
     * x轴的刻度间隔
     *
     * 因为x周是可以滑动的，所以只有刻度的数量这一个属性
     * */
    var xLineMarkCount: Int = 5

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
        // 得到数据的数量
        val count = adapter?.maxDataCount ?: 0
        maxWidth = if (count < xLineMarkCount) {
            canScroll = false
            width
        } else {
            canScroll = true
            width / xLineMarkCount * count
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
        // 计算每一个刻度的宽度
        val markWidth = width / xLineMarkCount
        // 计算已经偏移了几个刻度
        val index = (offsetX / markWidth).toInt()
        // 为了绘制第一条能够和前一条有连线，所以我们要减1
        return Math.max(0, index - 1)
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
        val markWidth = width / xLineMarkCount
        // 计算已经偏移了几个刻度
        val index = (offsetX / markWidth).toInt()
        // 如果绘制的是第一个，直接返回偏移值
        return if (index == 0) {
            -offsetX % markWidth
        }
        // // 为了绘制第一条能够和前一条有连线，所以我们要减去刻度值的宽度
        else {
            -offsetX % markWidth - markWidth
        }
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
        else if (offsetX > maxWidth - width) {
            offsetX = maxWidth - width.toFloat()
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
}