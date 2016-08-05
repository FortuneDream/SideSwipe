package com.example.q.swipelayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by YQ on 2016/8/3.
 * 侧拉删除控件
 */
public class SwipeLayout extends FrameLayout {
    private Status mStatus = Status.Close;
    private OnSwipeLayoutListener listener;
    private ViewDragHelper mDragHelper;
    private View mFrontView;
    private View mBackView;
    private int mHeight;
    private int mWidth;
    private int mRange;

    public static enum Status {
        Close, Open, Dragging;
    }

    public Status getmStatus() {
        return mStatus;
    }

    public void setmStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

    public OnSwipeLayoutListener getListener() {
        return listener;
    }

    public void setListener(OnSwipeLayoutListener listener) {
        this.listener = listener;
    }

    public static interface OnSwipeLayoutListener {
        void onClose(SwipeLayout mSwipeLayout);

        void onOpen(SwipeLayout mSwipeLayout);

        void onDragging(SwipeLayout mSwipeLayout);

        void onStartClose(SwipeLayout mSwipeLayout);//要去关闭

        void onStartOpen(SwipeLayout mSwipeLayout);//要去开启
    }

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                //边界限制
                if (child == mFrontView) {
                    if (left > 0) {
                        return 0;
                    } else if (left < -mRange) {
                        return -mRange;
                    }
                } else if (child == mBackView) {
                    if (left > mWidth) {
                        return mWidth;
                    } else if (left < mWidth - mRange) {
                        return mWidth - mRange;
                    }
                }
                return left;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                //传递偏移
                if (changedView == mFrontView) {
                    mBackView.offsetLeftAndRight(dx);
                } else if (changedView == mBackView) {
                    mFrontView.offsetLeftAndRight(dx);
                }
                dispatchSwipeEvent();
                invalidate();
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                //
                if (xvel == 0 && releasedChild.getLeft() < -mRange / 2.0f) {
                    open();
                } else if (xvel < 0) {
                    open();
                } else {
                    close();
                }
            }
        });
    }

    private void dispatchSwipeEvent() {
        if (listener != null) {
            listener.onDragging(this);
            Status preStatus = mStatus;//上一次
            mStatus = updateStutas();
            if (mStatus != preStatus ) {
                //状态改变
                if (mStatus == Status.Close) {
                    listener.onClose(this);
                } else if (mStatus == Status.Open) {
                    listener.onOpen(this);
                } else if (mStatus == Status.Dragging) {
                    if (preStatus == Status.Close) {
                        listener.onStartOpen(this);
                    } else if (preStatus == Status.Open) {
                        listener.onStartClose(this);
                    }
                }
            }
        }
    }

    private Status updateStutas() {
        int left = mFrontView.getLeft();
        if (left == 0) {
            return Status.Close;
        } else if (left == -mRange) {
            return Status.Open;
        } else {
            return Status.Dragging;
        }
    }

    private void close() {
        ToastUtil.show(getContext(), "close");
        close(true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void close(boolean isSmooth) {
        int finalLeft = -0;
        if (isSmooth) {
            //开始动画
            if (mDragHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)) {
                postInvalidateOnAnimation();
            }
        } else {
            layoutContent(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void open(boolean isSmooth) {
        int finalLeft = -mRange;
        if (isSmooth) {
            //开始动画
            if (mDragHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)) {
                postInvalidateOnAnimation();
            }
        } else {
            layoutContent(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            postInvalidateOnAnimation();
        }
    }

    private void open() {
        ToastUtil.show(getContext(), "open");
        open(true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //摆放初始位置
        layoutContent(false);
    }

    private void layoutContent(boolean isOpen) {
        //摆放前View
        Rect frontRect = computeFrontViewRect(isOpen);
        mFrontView.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);
        //根据前View，摆放后View
        Rect backRect = computeBackViewViaFront(frontRect);
        mBackView.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);
        //bringChildToFront(mFrontView);//把FrontView前置???
        //后View依赖前View

    }

    private Rect computeBackViewViaFront(Rect frontRect) {
        int left = frontRect.right;
        return new Rect(left, 0, left + mRange, mHeight);
    }

    private Rect computeFrontViewRect(boolean isOpen) {
        int left = 0;
        if (isOpen) {
            left = -mRange;
        }
        return new Rect(left, 0, left + mWidth, mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = mFrontView.getMeasuredWidth();
        mHeight = mFrontView.getMeasuredHeight();
        mRange = mBackView.getMeasuredWidth();//BackView的宽度(两个TextView宽度之和)
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBackView = getChildAt(0);
        mFrontView = getChildAt(1);
    }
}
