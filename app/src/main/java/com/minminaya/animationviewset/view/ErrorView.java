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
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.CycleInterpolator;

/**
 * Created by Niwa on 2017/6/18.
 */

public class ErrorView extends View {

    private static final float PADDING = 20;
    private static final int DEFAULT_RADIUS = 300;


    private Paint circlePaint;
    private Paint linePaint;

    private float mStrokeWidth = 10;
    private float temp;
    private float mCenterX, mCenterY;
    private float mRadius = 300;
    private final RectF mRectF = new RectF();
    private Float mDegree = 360f;
    private Float mLeftValue = 0f;
    private Float mRightValue = 0f;


    private PathMeasure pathLeftMeasure;
    private PathMeasure pathRightMeasure;
    private float[] mLeftPos = new float[2];
    private float[] mRightPos = new float[2];

    private AnimatorSet mAnimatorSet = new AnimatorSet();
    private ValueAnimator mCircleAnim;
    private ValueAnimator mLineLeftAnim;
    private ValueAnimator mLineRightAnim;


    public ErrorView(Context context) {
        this(context, null);
    }

    public ErrorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(mStrokeWidth);
        circlePaint.setStrokeJoin(Paint.Join.ROUND);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(mStrokeWidth);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setColor(Color.WHITE);
        linePaint.setStyle(Paint.Style.STROKE);


        resetDegreeAndLeftRightValueAndPathMeasure();
    }

    private void resetDegreeAndLeftRightValueAndPathMeasure() {
        mDegree = 0f;
        mLeftValue = 0f;
        mRightValue = 0f;
        pathLeftMeasure = null;
        pathRightMeasure = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        reMeasure();
    }

    private void reMeasure() {
        mCenterX = getMeasuredWidth() / 2;
        mCenterY = getMeasuredHeight() / 2;
        Log.e("position", "X:" + mCenterX + "," + "Y:" + mCenterY);
        temp = mRadius / 2;

        Path path = new Path();
        //左上角
        path.moveTo(mCenterX - temp, mCenterY - temp);
        //右下角
        path.lineTo(mCenterX + temp, mCenterY + temp);
        pathLeftMeasure = new PathMeasure(path, false);

        path = new Path();
        //右上角
        path.moveTo(mCenterX + temp, mCenterY - temp);
        //左下角
        path.lineTo(mCenterX - temp, mCenterY + temp);
        pathRightMeasure = new PathMeasure(path, false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mRectF.left = mCenterX - mRadius;
        mRectF.top = mCenterY - mRadius;
        mRectF.right = mRadius + mCenterX;
        mRectF.bottom = mCenterY + mRadius;
        //画圆
        canvas.drawArc(mRectF, 0, mDegree, false, circlePaint);

        if (mLeftPos[1] > (mCenterX - temp) && mRightPos[1] > (mCenterY - temp)) {
            //如果终点大于temp(半圆里才画对勾)
            canvas.drawLine(mCenterX - temp, mCenterY - temp, mLeftPos[0], mLeftPos[1], linePaint);
            canvas.drawLine(mCenterX + temp, mCenterY - temp, mRightPos[0], mRightPos[1], linePaint);
        }

    }

    public void startAnimation(int mRadius) {
        mRadius = mRadius < 0 ? DEFAULT_RADIUS : mRadius;
        this.mRadius = mRadius;
        if (null != mAnimatorSet && mAnimatorSet.isRunning()) {
            return;
        }

        //设定动画变量
        mCircleAnim = ValueAnimator.ofFloat(0, 360);
        mLineLeftAnim = ValueAnimator.ofFloat(0, pathLeftMeasure.getLength());
        mLineRightAnim = ValueAnimator.ofFloat(0, pathRightMeasure.getLength());

        mCircleAnim.setDuration(800);
        mLineRightAnim.setDuration(400);
        mLineLeftAnim.setDuration(400);

        mCircleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //动态回调变量值
                mDegree = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mLineLeftAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLeftValue = (Float) animation.getAnimatedValue();
                //动态获取当前的坐标点存到数组---mLeftPos
                pathLeftMeasure.getPosTan(mLeftValue, mLeftPos, null);
                invalidate();
            }
        });
        mLineRightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mRightValue = (Float) animation.getAnimatedValue();
                pathRightMeasure.getPosTan(mRightValue, mRightPos, null);
                invalidate();
            }
        });

        mAnimatorSet.play(mCircleAnim).before(mLineLeftAnim);
        mAnimatorSet.play(mLineRightAnim).after(mLineLeftAnim);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                stopAnim();
                if (null != onFinishListener) {
                    //这里是监听不为空时才回调，切记调用时候记得加上这个监听器OnPaintFinishListener
                    onFinishListener.onPaintEnd();
                    falureAnim();
                }
            }
        });
        mAnimatorSet.start();
    }

    /**
     * 左右抖动动画
     */
    private void falureAnim() {
        float current = this.getTranslationX();
        ObjectAnimator transXAnim = ObjectAnimator.ofFloat(this, "translationX", current + 30);
        transXAnim.setDuration(2000);

        transXAnim.setInterpolator(new CycleInterpolator(3));
        transXAnim.start();

    }

    private void stopAnim() {
        if (null != mCircleAnim) {
            mCircleAnim.end();
        }
        if (null != mLineLeftAnim) {
            mLineLeftAnim.end();
        }
        if (null != mLineRightAnim) {
            mLineRightAnim.end();
        }
        clearAnimation();
    }


    public interface OnPaintFinishListener {
        void onPaintEnd();
    }

    private OnPaintFinishListener onFinishListener;

    public void addOnPaintFinishListener(OnPaintFinishListener onPaintFinishListener) {

        this.onFinishListener = onPaintFinishListener;
    }

    public void setPaintColor(int color){
        circlePaint.setColor(color);
        linePaint.setColor(color);
        invalidate();
    }
}
