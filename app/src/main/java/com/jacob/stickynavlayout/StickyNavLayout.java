package com.jacob.stickynavlayout;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * Created by jacob-wj on 2015/4/22.
 */
public class StickyNavLayout extends LinearLayout {

    /**
     * 滑动的辅助类
     */
    private OverScroller mScroller;
    /**
     * 滑动速度检测的辅助类
     */
    private VelocityTracker mVelocityTracker;

    private View mTopView;

    private View mIndicator;

    private ViewPager mViewPager;

    private ViewGroup mInnerScrollView;
    /**
     * 最上面的展示的View
     */
    private int mTopViewHeight;
    /**
     * 是否正在滑动
     */
    private boolean isDragging = false;
    /**
     * 滑动的距离
     */
    private float mTouchSlop;
    /**
     * 滑动的速度
     */
    private int mMaxVelocity ,mMinVelocity;
    /**
     * 上次滑动的位置
     */
    private float mLastY;
    /**
     * 最顶部的View是否被隐藏
     */
    private boolean isTopHidden;

    public StickyNavLayout(Context context) {
        this(context, null);
    }

    public StickyNavLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyNavLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        mScroller = new OverScroller(getContext());
        mVelocityTracker  = VelocityTracker.obtain();

        mTouchSlop  = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mMinVelocity = ViewConfiguration.get(getContext()). getScaledMinimumFlingVelocity();
    }

    /**
     * 初始化所有的View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTopView = findViewById(R.id.id_layout_top_view);
        mIndicator = findViewById(R.id.id_layout_indicator);
        View pager = findViewById(R.id.id_layout_viewpager);
        if (pager instanceof  ViewPager){
            mViewPager = (ViewPager) pager;
        }else{
            throw new IllegalArgumentException("id_layout_viewpager 对应的必须是Viewpager");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 这里需要给viewpager进行设置高度，主要是因为viewpager不会为自己的子view计算尺寸
         */
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = getMeasuredHeight()-mIndicator.getMeasuredHeight();
        mViewPager.setLayoutParams(params);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTopView.getMeasuredHeight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int dy = (int) (y -mLastY);

                getCurrentScrollView();
                if (Math.abs(dy) > mTouchSlop){
                    isDragging = true;
                    //如果topView是显示的或者滑动到最上面并且向下拉的时候，需要拦截touch事件

                    if (!isTopHidden || (mInnerScrollView.getScrollY() == 0 && dy >0)){
                        return  true;
                    }
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y -mLastY;

                if (!isDragging && Math.abs(dy) > mTouchSlop){
                    isDragging = true;
                }
                if (isDragging){
                    scrollBy(0, (int) -dy);
                    mLastY = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                isDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000,mMaxVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinVelocity){
                    //flings是滑动的意思，这里代表的是根据给定的初速度会进行速度递减的界面滑动
                    mScroller.fling(0,getScrollY(),0,-velocityY,0,0,0,mTopViewHeight);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                isDragging = false;
                mVelocityTracker.clear();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取底部页面中的ScrollView, 需要通过这个距离来判断是否滑到顶部
     */
    private void getCurrentScrollView() {
        int index = mViewPager.getCurrentItem();
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter instanceof FragmentPagerAdapter){
            Fragment fragment  = ((FragmentPagerAdapter) adapter).getItem(index);
            mInnerScrollView = (ViewGroup) fragment.getView().findViewById(R.id.id_layout_inner_scrollview);
        }
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()){
            scrollTo(0,mScroller.getCurrY());
            invalidate();
        }
    }

    /**
     * 对边界值进行限制
     */
    @Override
    public void scrollTo(int x, int y) {
        if (y < 0){
             y = 0;
        }
        if (y > mTopViewHeight){
            y = mTopViewHeight;
        }
        if (y != getScrollY()){
            super.scrollTo(x, y);
        }
       isTopHidden = (getScrollY() == mTopViewHeight);
    }
}
