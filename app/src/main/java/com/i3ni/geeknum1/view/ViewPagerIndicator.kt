package com.i3ni.geeknum1.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View

import com.i3ni.geeknum1.R


/**
 * Created by linWeiJia on 2017/p9/p8.
 */

class ViewPagerIndicator(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val mPath: Path
    private val paintFill: Paint
    private val paintStroke: Paint
    private var mNum: Int = 0//个数
    private var mRadius: Float = 0.toFloat()//半径
    private var mLength: Float = 0.toFloat()//线长
    private val mHeight: Float = 0.toFloat()//线宽
    private var mOffset: Float = 0.toFloat()//偏移量
    private var mSelected_color: Int = 0//选中颜色
    private var mDefault_color: Int = 0//默认颜色
    private var mIndicatorType: Int = 0//点类型
    private var mDistanceType: Int = 0//距离类型
    private var mDistance: Float = 0.toFloat()//间隔距离
    private var mPosition: Int = 0//第几张
    private var mPercent: Float = 0.toFloat()
    private var mIsLeft: Boolean = false
    private var mIsInfiniteCircle: Boolean = false//无限循环
    private var mAnimation: Boolean = false

    private var mSpringPoint = arrayOf<Point>()

    private var mControlPoint = arrayOf<Point>()
    private val mCenterPoint = CenterPoint()

    init {
        setStyleable(context, attrs)
        paintStroke = Paint()
        paintFill = Paint()
        mPath = Path()
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        //实心
        paintFill.style = Paint.Style.FILL_AND_STROKE
        paintFill.color = mSelected_color
        paintFill.isAntiAlias = true
        paintFill.strokeWidth = 3f
        //空心
        paintStroke.style = Paint.Style.FILL
        paintStroke.color = mDefault_color
        paintStroke.isAntiAlias = true
        paintStroke.strokeWidth = 3f
    }

    /**
     * 绘制   invalidate()后 执行
     *
     * @param canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mNum <= 0) {
            return
        }
        val width = canvas.width
        val height = canvas.height
        canvas.translate((width / 2).toFloat(), (height / 2).toFloat())
        //初始化画笔
        initPaint()
        //距离
        when (mDistanceType) {
            DistanceType.BY_DISTANCE -> {
            }
            DistanceType.BY_RADIUS//圆心到 3倍半径 只有一个半径
            -> mDistance = 3 * mRadius
            DistanceType.BY_LAYOUT//布局等分
            -> if (mIndicatorType == IndicatorType.CIRCLE_LINE) {
                mDistance = (width / (mNum + 1)).toFloat()
            } else {
                mDistance = (width / mNum).toFloat()
            }
        }
        when (mIndicatorType) {
            IndicatorType.CIRCLE//圆
            -> {
                for (i in 0 until mNum) {//默认点 -(mNum - p1) * 0.5f * mDistance 第一个点
                    canvas.drawCircle((-(mNum - 1)).toFloat() * 0.5f * mDistance + i * mDistance, 0f, mRadius, paintStroke)
                }
                //选中
                canvas.drawCircle((-(mNum - 1)).toFloat() * 0.5f * mDistance + mOffset, 0f, mRadius, paintFill)
            }
            IndicatorType.LINE//线
            -> {
                paintStroke.strokeWidth = mRadius
                val startX = (-(mNum - 1)).toFloat() * 0.5f * mDistance - mLength / 2
                val stopX = (-(mNum - 1)).toFloat() * 0.5f * mDistance + mLength / 2
                //默认
                for (i in 0 until mNum) {
                    canvas.drawLine(startX + i * mDistance, 0f, stopX + i * mDistance, 0f, paintStroke)
                }
                //选中
                paintFill.strokeWidth = mRadius
                val startF = (-(mNum - 1)).toFloat() * 0.5f * mDistance - mLength / 2 + mOffset
                val stopF = (-(mNum - 1)).toFloat() * 0.5f * mDistance + mLength / 2 + mOffset
                canvas.drawLine(startF, 0f, stopF, 0f, paintFill)
            }
            IndicatorType.CIRCLE_LINE//圆线
            ->

                if (mPosition == mNum - 1) {//最后一个 右滑
                    //第一个 线 选中 消失
                    val leftClose = (-mNum).toFloat() * 0.5f * mDistance - mRadius
                    val rightClose = leftClose + 2 * mRadius + mOffset
                    val topClose = -mRadius
                    val bottomClose = mRadius
                    val rectClose = RectF(leftClose, topClose, rightClose, bottomClose)// 设置个新的长方形
                    canvas.drawRoundRect(rectClose, mRadius, mRadius, paintStroke)
                    //最后一个 线  显示
                    val rightOpen = (-mNum).toFloat() * 0.5f * mDistance + mNum * mDistance + mRadius
                    val leftOpen = rightOpen - 2 * mRadius - mDistance + mOffset
                    val topOpen = -mRadius
                    val bottomOpen = mRadius
                    val rectOpen = RectF(leftOpen, topOpen, rightOpen, bottomOpen)// 设置个新的长方形
                    canvas.drawRoundRect(rectOpen, mRadius, mRadius, paintStroke)
                    //圆
                    for (i in 1 until mNum) {
                        canvas.drawCircle(rightClose - mRadius + i * mDistance, 0f, mRadius, paintStroke)
                    }

                } else {
                    //第一个 线 选中 消失
                    val leftClose = (-mNum).toFloat() * 0.5f * mDistance + mPosition * mDistance - mRadius
                    val rightClose = leftClose + 2 * mRadius + mDistance - mOffset
                    val topClose = -mRadius
                    val bottomClose = mRadius
                    val rectClose = RectF(leftClose, topClose, rightClose, bottomClose)// 设置个新的长方形
                    canvas.drawRoundRect(rectClose, mRadius, mRadius, paintStroke)
                    //第二个 线  显示
                    if (mPosition < mNum - 1) {
                        val rightOpen = (-mNum).toFloat() * 0.5f * mDistance + (mPosition + 2) * mDistance + mRadius
                        val leftOpen = rightOpen - 2 * mRadius - mOffset
                        val topOpen = -mRadius
                        val bottomOpen = mRadius
                        val rectOpen = RectF(leftOpen, topOpen, rightOpen, bottomOpen)// 设置个新的长方形
                        canvas.drawRoundRect(rectOpen, mRadius, mRadius, paintStroke)
                    }
                    //圆
                    for (i in mPosition + 3..mNum) {
                        canvas.drawCircle((-mNum).toFloat() * 0.5f * mDistance + i * mDistance, 0f, mRadius, paintStroke)
                    }
                    for (i in mPosition - 1 downTo 0) {
                        canvas.drawCircle((-mNum).toFloat() * 0.5f * mDistance + i * mDistance, 0f, mRadius, paintStroke)
                    }
                }
            IndicatorType.BEZIER//贝塞尔
            -> {
                for (i in 0 until mNum) {//默认点 -(mNum - p1) * 0.5f * mDistance 第一个点
                    canvas.drawCircle((-(mNum - 1)).toFloat() * 0.5f * mDistance + i * mDistance, 0f, mRadius, paintStroke)
                }
                //选中
                drawCubicBezier(canvas)
            }
            IndicatorType.SPRING//贝塞尔 弹性
            -> {
                for (i in 0 until mNum) {//默认点 -(mNum - p1) * 0.5f * mDistance 第一个点
                    canvas.drawCircle((-(mNum - 1)).toFloat() * 0.5f * mDistance + i * mDistance, 0f, mRadius, paintStroke)
                }
                drawSpringBezier(canvas)
            }
        }
    }

    /**
     * 绘制弹性
     *
     * @param canvas
     */
    private fun drawSpringBezier(canvas: Canvas) {
        //右圆圆心
        val right_circle_x: Float
        //右圆半径
        val right_circle_radius: Float
        //左圆圆心
        val left_circle_x: Float
        //左圆半径
        val left_circle_radius: Float
        //最大半径
        val max_radius = mRadius
        //最小半径
        val min_radius = mRadius / 2
        //控制点
        if (mPosition == mNum - 1 && !mIsLeft) {//第一个 右滑  0---p4
            if (mPercent <= 0.5) {
                right_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (mNum - 1) * mDistance
                left_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (0.5f - mPercent) / 0.5f * (mNum - 1).toFloat() * mDistance
                right_circle_radius = min_radius + (max_radius - min_radius) * (0.5f - mPercent) / 0.5f
            } else {
                right_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (1f - mPercent) / 0.5f * (mNum - 1).toFloat() * mDistance
                left_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance
                right_circle_radius = min_radius
            }
            left_circle_radius = mRadius * mPercent
        } else if (mPosition == mNum - 1 && mIsLeft) {//最后一个 左滑 p4--0
            //0-p1
            if (mPercent >= 0.5) {//左亭
                left_circle_radius = min_radius + (max_radius - min_radius) * (-0.5f + mPercent) / 0.5f
                left_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance
                right_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (1 - mPercent) / 0.5f * (mNum - 1).toFloat() * mDistance
            } else {//左动
                left_circle_radius = min_radius
                left_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (0.5f - mPercent) / 0.5f * (mNum - 1).toFloat() * mDistance
                right_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (mNum - 1) * mDistance
            }
            right_circle_radius = mRadius * (1 - mPercent)
        } else if (mIsLeft) {//中间的 左滑
            mOffset = (mPercent + mPosition) * mDistance
            if (mPercent >= 0.5) {
                left_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + ((mPercent - 0.5f) / 0.5f + mPosition) * mDistance
                right_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (1 + mPosition) * mDistance
                right_circle_radius = min_radius + (max_radius - min_radius) * (mPercent - 0.5f) / 0.5f
            } else {
                right_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (mPercent / 0.5f + mPosition) * mDistance
                left_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + mPosition * mDistance
                right_circle_radius = min_radius
            }
            left_circle_radius = mRadius * (1 - mPercent)
        } else {//右滑
            mOffset = (mPercent + mPosition) * mDistance
            if (mPercent <= 0.5) {
                left_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + mPosition * mDistance
                right_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (mPercent / 0.5f + mPosition) * mDistance
                left_circle_radius = min_radius + (max_radius - min_radius) * (0.5f - mPercent) / 0.5f
            } else {
                left_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + ((mPercent - 0.5f) / 0.5f + mPosition) * mDistance
                right_circle_x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (mPosition + 1) * mDistance
                left_circle_radius = min_radius
            }
            right_circle_radius = mRadius * mPercent
        }
        //右圆
        canvas.drawCircle(right_circle_x, 0f, right_circle_radius, paintFill)
        //左圆
        canvas.drawCircle(left_circle_x, 0f, left_circle_radius, paintFill)
        //贝塞尔
        //控制点
        mSpringPoint[0].x = left_circle_x
        mSpringPoint[0].y = -left_circle_radius
        mSpringPoint[5].x = mSpringPoint[0].x
        mSpringPoint[5].y = left_circle_radius
        //
        mSpringPoint[1].x = (left_circle_x + right_circle_x) / 2
        mSpringPoint[1].y = -left_circle_radius / 2
        mSpringPoint[4].x = mSpringPoint[1].x
        mSpringPoint[4].y = left_circle_radius / 2
        //
        mSpringPoint[2].x = right_circle_x
        mSpringPoint[2].y = -right_circle_radius
        mSpringPoint[3].x = mSpringPoint[2].x
        mSpringPoint[3].y = right_circle_radius

        mPath.reset()
        mPath.moveTo(mSpringPoint[0].x, mSpringPoint[0].y)
        mPath.quadTo(mSpringPoint[1].x, mSpringPoint[1].y, mSpringPoint[2].x, mSpringPoint[2].y)
        mPath.lineTo(mSpringPoint[3].x, mSpringPoint[3].y)
        mPath.quadTo(mSpringPoint[4].x, mSpringPoint[4].y, mSpringPoint[5].x, mSpringPoint[5].y)
        canvas.drawPath(mPath, paintFill)
    }

    /**
     * 绘制贝塞尔曲线
     *
     * @param canvas
     */
    private fun drawCubicBezier(canvas: Canvas) {
        //更换控制点
        changePoint()

        /** 清除Path中的内容
         * reset不保留内部数据结构，但会保留FillType.
         * rewind会保留内部的数据结构，但不保留FillType  */
        mPath.reset()

        //0
        mPath.moveTo(mControlPoint[0].x, mControlPoint[0].y)
        //0-p3
        mPath.cubicTo(mControlPoint[1].x, mControlPoint[1].y, mControlPoint[2].x, mControlPoint[2].y, mControlPoint[3].x, mControlPoint[3].y)
        //p3-p6
        mPath.cubicTo(mControlPoint[4].x, mControlPoint[4].y, mControlPoint[5].x, mControlPoint[5].y, mControlPoint[6].x, mControlPoint[6].y)
        //p6-p9
        mPath.cubicTo(mControlPoint[7].x, mControlPoint[7].y, mControlPoint[8].x, mControlPoint[8].y, mControlPoint[9].x, mControlPoint[9].y)
        //p9-0
        mPath.cubicTo(mControlPoint[10].x, mControlPoint[10].y, mControlPoint[11].x, mControlPoint[11].y, mControlPoint[0].x, mControlPoint[0].y)

        canvas.drawPath(mPath, paintFill)
    }

    /**
     * 控制点
     */
    private fun changePoint() {
        mCenterPoint.y = 0f
        var mc = M
        mControlPoint[2].y = mRadius//底部
        mControlPoint[8].y = -mRadius//顶部

        //圆心位置
        if (mPosition == mNum - 1 && !mIsLeft) {//第一个 右滑  0-->p4

            if (mPercent <= 0.2) { //回弹 圆心到达
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (mNum - 1) * mDistance//最后一个
            } else if (mPercent <= 0.8) {//加速 左凸起 扁平化M 最右端固定不变  圆心移动
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (1 - (mPercent - 0.2f) / 0.6f) * (mNum - 1).toFloat() * mDistance
            } else if (mPercent > 0.8 && mPercent < 1) {//
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance//第一个
            } else if (mPercent == 1f) {//圆
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance
            }
            //控制点位置
            if (mPercent > 0.8 && mPercent <= 1) {//右凸起 圆心不变
                mControlPoint[5].x = mCenterPoint.x + mRadius * (2 - (mPercent - 0.8f) / 0.2f)//右半圆
                mControlPoint[0].x = mCenterPoint.x - mRadius//左半圆
            } else if (mPercent > 0.5 && mPercent <= 0.8) {//加速 左凸起 扁平化M 最右端固定不变  圆心移动
                mControlPoint[5].x = mCenterPoint.x + 2 * mRadius//右半圆
                mControlPoint[0].x = mCenterPoint.x - mRadius * (1 + (0.8f - mPercent) / 0.3f)//左半圆
                mControlPoint[2].y = mRadius * (1 + (mPercent - 0.8f) / 0.3f * 0.1f)//底部
                mControlPoint[8].y = -mRadius * (1 + (mPercent - 0.8f) / 0.3f * 0.1f)//顶部
                mc = mc * (1 + (-mPercent + 0.8f) / 0.3f * 0.3f)
            } else if (mPercent > 0.2 && mPercent <= 0.5) {//左右恢复 变圆M逐渐重置为原来大小  圆心移动
                mControlPoint[5].x = mCenterPoint.x + mRadius * (1 + (mPercent - 0.2f) / 0.3f)//右半圆
                mControlPoint[0].x = mCenterPoint.x - mRadius * (1 + (mPercent - 0.2f) / 0.3f)//左半圆
                mControlPoint[2].y = mRadius * (1 - (mPercent - 0.2f) / 0.3f * 0.1f)//底部
                mControlPoint[8].y = -mRadius * (1 - (mPercent - 0.2f) / 0.3f * 0.1f)//顶部
                mc = mc * (1 + (mPercent - 0.2f) / 0.3f * 0.3f)
            } else if (mPercent > 0.1 && mPercent <= 0.2) {//左凹 圆心到达.0
                mControlPoint[5].x = mCenterPoint.x + mRadius//右半圆
                mControlPoint[0].x = mCenterPoint.x - mRadius * (1 - (0.2f - mPercent) / 0.1f * 0.5f)//左半圆
            } else if (mPercent >= 0 && mPercent <= 0.1) {//回弹 圆心到达
                mControlPoint[5].x = mCenterPoint.x + mRadius//右半圆
                mControlPoint[0].x = mCenterPoint.x - mRadius * (1 - mPercent / 0.1f * 0.5f)//左半圆
            }

        } else if (mPosition == mNum - 1 && mIsLeft) {//最后一个 左滑  p4-->0
            if (mPercent <= 0.2) {//圆
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (mNum - 1) * mDistance
            } else if (mPercent <= 0.8) {//加速 左凸起 扁平化M 最右端固定不变  圆心移动
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (1 - (mPercent - 0.2f) / 0.6f) * (mNum - 1).toFloat() * mDistance
            } else if (mPercent > 0.8 && mPercent < 1) {//回弹 圆心到达
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance//第一个
            } else if (mPercent == 1f) {//圆
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + mPosition * mDistance
            }

            if (mPercent <= 0) {//圆

            } else if (mPercent <= 0.2 && mPercent >= 0) {//左凸起 圆心不变
                mControlPoint[5].x = mCenterPoint.x + mRadius//右半圆
                mControlPoint[0].x = mCenterPoint.x - mRadius * (1 + mPercent / 0.2f)//左半圆
            } else if (mPercent > 0.2 && mPercent <= 0.5) {//加速 右凸起 扁平化M 最左端固定不变  圆心移动
                mControlPoint[5].x = mCenterPoint.x + mRadius * (1 + (mPercent - 0.2f) / 0.3f)//右半圆
                mControlPoint[0].x = mCenterPoint.x - 2 * mRadius//左半圆
                mControlPoint[2].y = mRadius * (1 - (mPercent - 0.2f) / 0.3f * 0.1f)//底部
                mControlPoint[8].y = -mRadius * (1 - (mPercent - 0.2f) / 0.3f * 0.1f)//顶部
                mc = mc * (1 + (mPercent - 0.2f) / 0.3f * 0.3f)
            } else if (mPercent > 0.5 && mPercent <= 0.8) {//左右恢复 变圆M逐渐重置为原来大小  圆心移动
                mControlPoint[5].x = mCenterPoint.x + mRadius * (1 + (0.8f - mPercent) / 0.3f)//右半圆
                mControlPoint[0].x = mCenterPoint.x - mRadius * (1 + (0.8f - mPercent) / 0.3f)//左半圆
                mControlPoint[2].y = mRadius * (1 + (mPercent - 0.8f) / 0.3f * 0.1f)//底部
                mControlPoint[8].y = -mRadius * (1 + (mPercent - 0.8f) / 0.3f * 0.1f)//顶部
                mc = mc * (1 + (0.8f - mPercent) / 0.3f * 0.3f)
            } else if (mPercent > 0.8 && mPercent <= 0.9) {//右凹 圆心到达
                mControlPoint[5].x = mCenterPoint.x + mRadius * (1 - (mPercent - 0.8f) / 0.1f * 0.5f)//右半圆
                mControlPoint[0].x = mCenterPoint.x - mRadius//左半圆
            } else if (mPercent > 0.9 && mPercent <= 1) {//回弹 圆心到达
                mControlPoint[5].x = mCenterPoint.x + mRadius * (1 - (mPercent - 0.9f) / 0.1f * 0.5f)//右半圆
                mControlPoint[0].x = mCenterPoint.x - mRadius//左半圆
            }


        } else {
            if (mPercent <= 0.2) {//圆
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + mPosition * mDistance
            } else if (mPercent <= 0.8) {//加速 左凸起 扁平化M 最右端固定不变  圆心移动
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (mPosition + mPercent) * mDistance
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (mPosition + (mPercent - 0.2f) / 0.6f) * mDistance
            } else if (mPercent > 0.8 && mPercent < 1) {//回弹 圆心到达
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + (mPosition + 1) * mDistance
            } else if (mPercent == 1f) {//圆
                mCenterPoint.x = (-(mNum - 1)).toFloat() * 0.5f * mDistance + mPosition * mDistance
            }
            //控制点位置
            if (mIsLeft)
            //左滑
            {
                if (mPercent >= 0 && mPercent <= 0.2) {//右凸起 圆心不变
                    mControlPoint[5].x = mCenterPoint.x + mRadius * (2 - (0.2f - mPercent) / 0.2f)//右半圆
                    mControlPoint[0].x = mCenterPoint.x - mRadius//左半圆
                } else if (mPercent > 0.2 && mPercent <= 0.5) {//加速 左凸起 扁平化M 最右端固定不变  圆心移动
                    mControlPoint[5].x = mCenterPoint.x + 2 * mRadius//右半圆
                    mControlPoint[0].x = mCenterPoint.x - mRadius * (1 + (mPercent - 0.2f) / 0.3f)//左半圆
                    mControlPoint[2].y = mRadius * (1 - (mPercent - 0.2f) / 0.3f * 0.1f)//底部
                    mControlPoint[8].y = -mRadius * (1 - (mPercent - 0.2f) / 0.3f * 0.1f)//顶部
                    mc = mc * (1 + (mPercent - 0.2f) / 0.3f * 0.3f)
                } else if (mPercent > 0.5 && mPercent <= 0.8) {//左右恢复 变圆M逐渐重置为原来大小  圆心移动
                    mControlPoint[5].x = mCenterPoint.x + mRadius * (1 + (0.8f - mPercent) / 0.3f)//右半圆
                    mControlPoint[0].x = mCenterPoint.x - mRadius * (1 + (0.8f - mPercent) / 0.3f)//左半圆
                    mControlPoint[2].y = mRadius * (1 + (mPercent - 0.8f) / 0.3f * 0.1f)//底部
                    mControlPoint[8].y = -mRadius * (1 + (mPercent - 0.8f) / 0.3f * 0.1f)//顶部
                    mc = mc * (1 + (-mPercent + 0.8f) / 0.3f * 0.3f)
                } else if (mPercent > 0.8 && mPercent <= 0.9) {//左凹 圆心到达
                    mControlPoint[5].x = mCenterPoint.x + mRadius//右半圆
                    mControlPoint[0].x = mCenterPoint.x - mRadius * (1 - (mPercent - 0.8f) / 0.1f * 0.5f)//左半圆
                } else if (mPercent > 0.9 && mPercent <= 1) {//回弹 圆心到达
                    mControlPoint[5].x = mCenterPoint.x + mRadius//右半圆
                    mControlPoint[0].x = mCenterPoint.x - mRadius * (1 - (1.0f - mPercent) / 0.1f * 0.5f)//左半圆
                }
            } else
            //右滑
            {
                if (mPercent <= 1 && mPercent >= 0.8) {//左凸起 圆心不变
                    mControlPoint[5].x = mCenterPoint.x + mRadius//右半圆
                    mControlPoint[0].x = mCenterPoint.x - mRadius * (2 - (mPercent - 0.8f) / 0.2f)//左半圆
                } else if (mPercent > 0.5 && mPercent <= 0.8) {//加速 右凸起 扁平化M 最左端固定不变  圆心移动
                    mControlPoint[5].x = mCenterPoint.x + mRadius * (2 - (mPercent - 0.5f) / 0.3f)//右半圆
                    mControlPoint[0].x = mCenterPoint.x - 2 * mRadius//左半圆
                    mControlPoint[2].y = mRadius * (1 - (0.8f - mPercent) / 0.3f * 0.1f)//底部
                    mControlPoint[8].y = -mRadius * (1 - (0.8f - mPercent) / 0.3f * 0.1f)//顶部
                    mc = mc * (1 + (0.8f - mPercent) / 0.3f * 0.3f)
                } else if (mPercent > 0.2 && mPercent <= 0.5) {//左右恢复 变圆M逐渐重置为原来大小  圆心移动
                    mControlPoint[5].x = mCenterPoint.x + mRadius * (1 + (mPercent - 0.2f) / 0.3f)//右半圆
                    mControlPoint[0].x = mCenterPoint.x - mRadius * (1 + (mPercent - 0.2f) / 0.3f)//左半圆
                    mControlPoint[2].y = mRadius * (1 - (mPercent - 0.2f) / 0.3f * 0.1f)//底部
                    mControlPoint[8].y = -mRadius * (1 - (mPercent - 0.2f) / 0.3f * 0.1f)//顶部
                    mc = mc * (1 + (mPercent - 0.2f) / 0.3f * 0.3f)
                } else if (mPercent > 0.1 && mPercent <= 0.2) {//右凹 圆心到达
                    mControlPoint[5].x = mCenterPoint.x + mRadius * (1 - (0.2f - mPercent) / 0.1f * 0.5f)//右半圆
                    mControlPoint[0].x = mCenterPoint.x - mRadius//左半圆
                } else if (mPercent >= 0 && mPercent <= 0.1) {//回弹 圆心到达
                    mControlPoint[5].x = mCenterPoint.x + mRadius * (1 - mPercent / 0.1f * 0.5f)//右半圆
                    mControlPoint[0].x = mCenterPoint.x - mRadius//左半圆
                }

            }
        }

        //11 0 p1
        mControlPoint[0].y = 0f
        mControlPoint[1].x = mControlPoint[0].x
        mControlPoint[1].y = mRadius * mc
        mControlPoint[11].x = mControlPoint[0].x
        mControlPoint[11].y = -mRadius * mc
        //p2 p3 p4
        mControlPoint[2].x = mCenterPoint.x - mRadius * mc
        mControlPoint[3].x = mCenterPoint.x
        mControlPoint[3].y = mControlPoint[2].y
        mControlPoint[4].x = mCenterPoint.x + mRadius * mc
        mControlPoint[4].y = mControlPoint[2].y
        //p5 p6 p7
        mControlPoint[5].y = mRadius * mc
        mControlPoint[6].x = mControlPoint[5].x
        mControlPoint[6].y = 0f
        mControlPoint[7].x = mControlPoint[5].x
        mControlPoint[7].y = -mRadius * mc
        //p8 p9 10
        mControlPoint[8].x = mCenterPoint.x + mRadius * mc
        mControlPoint[9].x = mCenterPoint.x
        mControlPoint[9].y = mControlPoint[8].y
        mControlPoint[10].x = mCenterPoint.x - mRadius * mc
        mControlPoint[10].y = mControlPoint[8].y
    }

    internal inner class CenterPoint {
        var x: Float = 0.toFloat()
        var y: Float = 0.toFloat()
    }

    internal inner class Point {
        var x: Float = 0.toFloat()
        var y: Float = 0.toFloat()
    }

    /**
     * xml 参数设置  选中颜色 默认颜色  点大小 长度 距离 距离类型 类型 真实个数(轮播)
     *
     * @param context
     * @param attrs
     */
    private fun setStyleable(context: Context, attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator)
        mSelected_color = array.getColor(R.styleable.ViewPagerIndicator_vpi_selected_color, -0x1)
        mDefault_color = array.getColor(R.styleable.ViewPagerIndicator_vpi_default_color, -0x323233)
        mRadius = array.getDimension(R.styleable.ViewPagerIndicator_vpi_radius, 20f)//px
        mLength = array.getDimension(R.styleable.ViewPagerIndicator_vpi_length, 2 * mRadius)//px
        mDistance = array.getDimension(R.styleable.ViewPagerIndicator_vpi_distance, 3 * mRadius)//px
        mDistanceType = array.getInteger(R.styleable.ViewPagerIndicator_vpi_distanceType, DistanceType.BY_RADIUS)
        mIndicatorType = array.getInteger(R.styleable.ViewPagerIndicator_vpi_indicatorType, IndicatorType.CIRCLE)
        mNum = array.getInteger(R.styleable.ViewPagerIndicator_vpi_num, 0)
        mAnimation = array.getBoolean(R.styleable.ViewPagerIndicator_vpi_animation, true)
        array.recycle()
        when (mIndicatorType) {
            IndicatorType.BEZIER -> mControlPoint = arrayOf<Point>(Point(), Point(), Point(), Point(), Point(), Point(), Point(), Point(), Point(), Point(), Point(), Point())
            IndicatorType.SPRING -> mSpringPoint = arrayOf<Point>(Point(), Point(), Point(), Point(), Point(), Point())
        }
        invalidate()
    }

    /**
     * 移动指示点
     *
     * @param percent  比例
     * @param position 第几个
     * @param isLeft   是否左滑
     */
    fun move(percent: Float, position: Int, isLeft: Boolean) {
        mPosition = position
        mPercent = percent
        mIsLeft = isLeft
        when (mIndicatorType) {
            IndicatorType.CIRCLE_LINE//圆线
            -> {
                if (mPosition == mNum - 1 && !isLeft) {//第一个 右滑
                    mOffset = percent * mDistance
                }
                if (mPosition == mNum - 1 && isLeft) {//最后一个 左滑
                    mOffset = percent * mDistance
                } else {//中间
                    mOffset = percent * mDistance
                }
            }
            IndicatorType.CIRCLE//圆
                , IndicatorType.LINE//线
            -> if (mPosition == mNum - 1 && !isLeft) {//第一个 右滑
                mOffset = (1 - percent) * (mNum - 1).toFloat() * mDistance
            } else if (mPosition == mNum - 1 && isLeft) {//最后一个 左滑
                mOffset = (1 - percent) * (mNum - 1).toFloat() * mDistance
            } else {//中间的
                mOffset = (percent + mPosition) * mDistance
            }
            IndicatorType.BEZIER//贝塞尔
            -> {
            }
            IndicatorType.SPRING//弹性
            -> {
            }
        }

        invalidate()
    }

    /**
     * 个数
     *
     * @param num
     */
    fun setNum(num: Int): ViewPagerIndicator {
        mNum = num
        invalidate()
        return this
    }

    /**
     * 类型
     *
     * @param indicatorType
     */
    fun setType(indicatorType: Int): ViewPagerIndicator {
        mIndicatorType = indicatorType
        invalidate()
        return this
    }


    /**
     * 线,圆
     */
    interface IndicatorType {
        companion object {
            val LINE = 0
            val CIRCLE = 1
            val CIRCLE_LINE = 2
            val BEZIER = 3
            val SPRING = 4
        }
    }


    /**
     * 半径
     *
     * @param radius
     */
    fun setRadius(radius: Float): ViewPagerIndicator {
        this.mRadius = radius
        invalidate()
        return this
    }

    /**
     * 距离 在IndicatorDistanceType为BYDISTANCE下作用
     *
     * @param distance
     */
    fun setDistance(distance: Float): ViewPagerIndicator {
        this.mDistance = distance
        invalidate()
        return this
    }

    /**
     * 距离类型
     *
     * @param mDistanceType
     */
    fun setDistanceType(mDistanceType: Int): ViewPagerIndicator {
        this.mDistanceType = mDistanceType
        invalidate()
        return this
    }

    /**
     * 布局,距离,半径
     */
    interface DistanceType { //
        companion object {
            val BY_RADIUS = 0
            val BY_DISTANCE = 1
            val BY_LAYOUT = 2
        }
    }

    /**
     * 一般 不循环 固定
     * @param viewPager 适配的viewpager
     * @return
     */
    fun setViewPager(viewPager: ViewPager): ViewPagerIndicator {
        setViewPager(viewPager, viewPager.adapter!!.count, false)
        return this
    }


    /**
     *
     * @param viewpager 适配的viewpager
     * @param CycleNumber 伪无限循环 真实个数
     * @return
     */
    fun setViewPager(viewpager: ViewPager, CycleNumber: Int): ViewPagerIndicator {

        setViewPager(viewpager, CycleNumber, false)
        return this
    }

    /**
     *
     * @param viewPager 适配的viewpager
     * @param isInfiniteCircle 真无限循环 配合BannerView 通常是true;false为一般 不循环 固定等价于[.setViewPager]
     *
     * @return
     */
    fun setViewPager(viewPager: ViewPager, isInfiniteCircle: Boolean): ViewPagerIndicator {

        if (isInfiniteCircle) {
            setViewPager(viewPager, viewPager.adapter!!.count - 2, isInfiniteCircle)
        } else {
            setViewPager(viewPager, viewPager.adapter!!.count, isInfiniteCircle)
        }
        return this
    }

    /**
     *
     * @param viewpager 适配的viewpager
     * @param CycleNumber 真/伪无限循环都必须输入
     * @param isInfiniteCircle 真无限循环 配合Banner
     * @return
     */
    fun setViewPager(viewpager: ViewPager, CycleNumber: Int, isInfiniteCircle: Boolean): ViewPagerIndicator {
        mNum = CycleNumber
        mIsInfiniteCircle = isInfiniteCircle
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            //记录上一次滑动的positionOffsetPixels值
            private var lastValue = -1

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                var position = position
                if (!mAnimation) {
                    //不需要动画
                    return
                }
                var isLeft = mIsLeft
                if (lastValue / 10 > positionOffsetPixels / 10) {
                    //右滑
                    isLeft = false
                } else if (lastValue / 10 < positionOffsetPixels / 10) {
                    //左滑
                    isLeft = true
                }
                if (mNum > 0 && !mIsInfiniteCircle) {
                    move(positionOffset, position % mNum, isLeft)
                } else if (mNum > 0 && mIsInfiniteCircle) {
                    if (position == 0) {
                        position = mNum - 1
                    } else if (position == mNum + 1) {
                        position = 0
                    } else {
                        position--
                    }
                    move(positionOffset, position, isLeft)
                }
                lastValue = positionOffsetPixels
            }

            override fun onPageSelected(position: Int) {
                var position = position
                if (mAnimation) {
                    //需要动画
                    return
                }
                if (mNum > 0 && !mIsInfiniteCircle) {
                    move(0f, position % mNum, false)
                } else if (mNum > 0 && mIsInfiniteCircle) {
                    if (position == 0) {
                        position = mNum - 1
                    } else if (position == mNum + 1) {
                        position = 0
                    } else {
                        position--
                    }
                    move(0f, position, false)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        return this
    }

    companion object {
        /**
         * 一个常量，用来计算绘制圆形贝塞尔曲线控制点的位置
         */
        private val M = 0.551915024494f
    }


}
