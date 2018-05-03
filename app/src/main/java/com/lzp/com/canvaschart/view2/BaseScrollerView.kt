package com.lzp.com.canvaschart.view2

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.Scroller
import com.lzp.com.canvaschart.base.BaseDataAdapter


/**
 * Created by li.zhipeng on 2018/5/3.
 */
open class BaseScrollerView(context: Context, attributes: AttributeSet?, defStyleAttr: Int)
    : View(context, attributes, defStyleAttr) {

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

    constructor(context: Context) : this(context, null)

    companion object {
        /**
         * 摩擦系数，根据速度计算滑行的距离
         * */
        private const val FLING_COEFFICIENT = 25
    }

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
     * 手指按下的X坐标
     */
    private var xDown: Float = 0f

    /**
     * 手指移动的X坐标
     */
    private var xMove: Float = 0f

    /**
     * 滚动器Scroller
     * */
    private val scroller: Scroller = Scroller(context)

    /**
     * 用于计算手指滑动的速度。
     */
    private var velocityTracker: VelocityTracker? = null

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

    /**
     * 创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中。
     *
     * @param event
     * 右侧布局监听控件的滑动事件
     */
    private fun createVelocityTracker(event: MotionEvent) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker!!.addMovement(event)
    }

    /**
     * 获取手指在绑定布局上的滑动速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private fun getScrollVelocity(): Float {
        velocityTracker!!.computeCurrentVelocity(1000)
        val velocity = velocityTracker!!.xVelocity
        return Math.abs(velocity)
    }

    /**
     * 回收VelocityTracker对象。
     */
    private fun recycleVelocityTracker() {
        velocityTracker!!.recycle()
        velocityTracker = null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        calculateMaxWidth()
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
        // 计算滑动的速度
        createVelocityTracker(event)
        when (event.action) {
        // 记录手指按下的坐标
            MotionEvent.ACTION_DOWN -> {
                xDown = event.rawX
            }
        //
            MotionEvent.ACTION_MOVE -> {
                // 更新xDown的坐标
                if (xMove != -1f) {
                    xDown = xMove
                }
                // 记录当前的x坐标
                xMove = event.rawX
                // 计算移动的位置
                val scrolledX = (xDown - xMove).toInt()
                // 如果已经滚动到最左边了，设置scrollTo 为0
                if (scrollX + scrolledX < 0) {
                    scrollTo(0, 0)
                    return true
                }
                // 如果已经滑动到最右边了，设置scrollTo 为宽度
                else if (scrollX + width + scrolledX > maxWidth) {
                    scrollTo(maxWidth - width, 0)
                    return true
                }
                scrollBy(scrolledX, 0)
            }
        // 手势抬起
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val dx = calculateFlingDistance()
                // startScroll()方法来初始化滚动数据并刷新界面
                scroller.startScroll(scrollX, 0, dx, 0)
                invalidate()
                recycleVelocityTracker()
                // 重置配置信息
                reset()
            }
        }
        return true
    }

    /**
     * 手势结束后，重置一些信息
     * */
    private fun reset() {
        xMove = -1f
    }

    /**
     * 计算滑动的惯性距离
     * */
    private fun calculateFlingDistance(): Int {
        // 判断是左滑还是右滑
        val isLeft = (xMove - xDown) >= 0
        val velocity = getScrollVelocity()
        Log.e("lzp", "velocity is :$velocity")
        var dx = Math.abs(velocity / FLING_COEFFICIENT)
        if (isLeft) {
            dx = -dx
        }
        // 如果滑动到了最左边，
        if (scrollX + dx < 0) {
            dx = -scrollX.toFloat()
        }
        // 如果已经滑动到了最右边
        else if (scrollX + dx > maxWidth - width) {
            dx = maxWidth - width - scrollX.toFloat()
        }
        Log.e("lzp", "scrollX is :$scrollX")
        Log.e("lzp", "dx is :$dx")
        return dx.toInt()
//
    }

    override fun computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            invalidate()
        }
    }

}