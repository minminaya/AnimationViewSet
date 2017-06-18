package com.minminaya.animationviewset.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;

/**
 * Created by Niwa on 2017/6/17.
 */

public class ConfirmView extends View {
    /**
     * 圆圈的画笔
     */
    private Paint mCirclePaint;
    /**
     * 线的画笔
     */
    private Paint mLinePaint;
    /**
     * 设置线宽，px
     */
    private float mStrokeWidth = 10;

    private float mCenterX, mCenterY;

    /**
     * 为了画出目标圆形的矩形外框
     */
    private RectF mRectF = new RectF();

    /**
     * 圆的半径
     */
    private float mRadius = 300;

    /**
     * 画圆时动态设置的角度
     */
    private Float mDegree = 360f;

    private Float mLeftValue = 150f;
    private Float mRightValue = 150f;
    //默认半径
    private static final int DEFAULT_RADIUS = 150;
    //边距
    private static final float PADDING = 20;

    private AnimatorSet mAnimatorSet = new AnimatorSet();
    private ValueAnimator mCircleAnim;
    private ValueAnimator mLineLeftAnimator;
    private ValueAnimator mLineRightAnimator;


    public ConfirmView(Context context) {
        this(context, null);
    }

    public ConfirmView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfirmView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mCirclePaint = new Paint();
        //抗锯齿
        mCirclePaint.setAntiAlias(true);
        //设置连接点为圆角
        mCirclePaint.setStrokeJoin(Paint.Join.ROUND);
        mCirclePaint.setStrokeWidth(mStrokeWidth);
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setStyle(Paint.Style.STROKE);


        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStrokeWidth(mStrokeWidth);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        reMeasure();
    }

    /**
     * 重新测量宽和高
     */
    private void reMeasure() {
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        mCenterX = viewWidth / 2;
        mCenterY = viewHeight / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.e("current", "X:" + mCenterX + "," + "Y:" + mCenterY);
        mRectF.left = mCenterX - mRadius;
        mRectF.top = mCenterY - mRadius;
        mRectF.right = mCenterX + mRadius;
        mRectF.bottom = mCenterY + mRadius;
        //画圆
        canvas.drawArc(mRectF, 0, mDegree, false, mCirclePaint);
        //画勾左边
        canvas.drawLine(mCenterX - mRadius / 2, mCenterY,
                mCenterX - mRadius / 2 + mLeftValue, mCenterY + mLeftValue,
                mLinePaint);
        //画勾的右边
        //mCenterX + mRightValue, mCenterY + mRadius / 2 - 1.7f * mRightValue这个坐标
        canvas.drawLine(mCenterX, mCenterY + mRadius / 2,
                mCenterX + mRightValue, mCenterY + mRadius / 2 - 1.7f * mRightValue, mLinePaint);
    }

    public void loadCircle(int mRadius) {
        mRadius = mRadius < 0 ? DEFAULT_RADIUS : mRadius;
        this.mRadius = mRadius - PADDING;
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            return;
        }
        resetDegreeAndLeftRightValueAndPaintColor();
        reMeasure();

        //这里其实限定了
        //mDegree，对应更新值的方法是mLeftValue = (Float) animation.getAnimatedValue();，在下面的监听器里
        //mLeftValue
        //mRightValue三个数的取值
        mCircleAnim = ValueAnimator.ofFloat(0, 360);
        mLineLeftAnimator = ValueAnimator.ofFloat(0, this.mRadius / 2f);
        mLineRightAnimator = ValueAnimator.ofFloat(0, this.mRadius / 2f);

        mCircleAnim.setDuration(800);
        mLineLeftAnimator.setDuration(500);
        mLineRightAnimator.setDuration(500);

        mCircleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDegree = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mLineLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLeftValue = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mLineRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRightValue = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mAnimatorSet.play(mCircleAnim).before(mLineLeftAnimator);
        mAnimatorSet.play(mLineRightAnimator).after(mLineLeftAnimator);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                stopPropertyAnimation();
                if (mEndListener != null) {
                    mEndListener.onCircleDone();
                    //放大效果
                    loadEndAnim();
                }
            }
        });
        mAnimatorSet.start();
    }

    /**
     * 加载结束动画
     */
    private void loadEndAnim() {
        //放大的值
        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(this, "scaleX", 1.0f, 1.2f, 1.0f);
        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(this, "scaleY", 1.0f, 1.2f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(3000);
        set.setInterpolator(new BounceInterpolator());
        set.playTogether(scaleXAnim, scaleYAnim);
        set.start();
    }

    private void stopPropertyAnimation() {
        if (null != mCircleAnim) {
            mCircleAnim.end();
        }
        if (null != mLineLeftAnimator) {
            mLineLeftAnimator.end();
        }
        if (null != mLineRightAnimator) {
            mLineRightAnimator.end();
        }
        clearAnimation();
    }

    private void resetDegreeAndLeftRightValueAndPaintColor() {
        mDegree = 0f;
        mLeftValue = 0f;
        mRightValue = 0f;
        mCirclePaint.setColor(Color.WHITE);
        mLinePaint.setColor(Color.WHITE);
    }


    public interface onCircleFinishListener {
        void onCircleDone();
    }

    private onCircleFinishListener mEndListener;

    public void addCircleAnimatorEndListener(onCircleFinishListener endListener) {
        if (mEndListener == null) {
            this.mEndListener = endListener;
        }
    }

    public void setPaintColor(int color) {
        mCirclePaint.setColor(color);
        mLinePaint.setColor(color);
        invalidate();
    }
}
