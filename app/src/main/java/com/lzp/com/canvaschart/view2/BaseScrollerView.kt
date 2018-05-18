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
 *
 *      图表滑动方案2
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
     * 记录手指划过的距离
     * */
    protected var offsetX: Float = 0f

    /**
     * 手指滑动距离的备份，用于判断是否手指移动了
     * */
    protected var offsetXTemp: Float = 0f

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
                // 备份偏移的位置
                offsetXTemp = offsetX
                // 记录当前的x坐标
                xMove = event.rawX
                // 计算移动的位置
                offsetX += (xDown - xMove)
                // 对移动的位置进行范围检查
                // 如果小于0，那么等于0
                if (offsetX < 0) {
                    offsetX = 0f
                }
                // 如果已经大于了最右边界
                else if (offsetX > maxWidth - width) {
                    offsetX = maxWidth - width.toFloat()
                }
                // 检查偏移值是否发生了改变
                if (offsetX != offsetXTemp) {
                    // 重绘
                    invalidate()
                }
            }
        // 手势抬起
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val dx = calculateFlingDistance()
                // startScroll()方法来初始化滚动数据并刷新界面
                scroller.startScroll(offsetX.toInt(), 0, dx, 0)
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
        // 对移动的位置进行范围检查
        // 如果小于0，那么等于0
        if (offsetX + dx < 0) {
            dx = -offsetX
        }
        // 如果已经大于了最右边界
        else if (offsetX + dx > maxWidth - width) {
            dx = maxWidth - width - offsetX
        }
        Log.e("lzp", "dx is :$dx")
        return dx.toInt()
    }

    override fun computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (scroller.computeScrollOffset()) {
            offsetX = scroller.currX.toFloat()
            Log.e("lzp", "currX is :${scroller.currX}")
            invalidate()
        }
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

}