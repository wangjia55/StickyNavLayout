package com.jacob.stickynavlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jacob-wj on 2015/4/5.
 */
public class ViewPagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    /**
     * 传入的Viewpager
     */
    private ViewPager mViewPager;
    /**
     * 传入的Tab的词条和文案
     */
    private List<String> mListTabs;
    /**
     * 绘制提示器三角形的画笔
     */
    private Paint mPaintTriangle = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 默认三角形的宽度是一个tab宽度的1/3
     */
    public static final float RATIO_TRIANGLE = 1f / 6;

    /**
     * 默认最大的可见的Tab的个数是4个
     */
    public int mTabVisibleCount = 3;

    private int mTabWidth;
    /**
     * 三角形的最大宽度
     */
    private final int DIMENSION_TRIANGEL_WIDTH = (int) (getScreenWidth() / 3 * RATIO_TRIANGLE);


    /**
     * 在滑动过程中三角形的偏移量
     */
    private float mTranslationX;

    /**
     * 标题正常时的颜色
     */
    private static final int COLOR_TEXT_NORMAL = 0xFF000000;
    /**
     * 标题选中时的颜色
     */
    private static final int COLOR_TEXT_HIGHLIGHTCOLOR = 0xFF000000;

    private OnPageChangeListener mPagerChangeListener;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setOrientation(HORIZONTAL);
    }

    /**
     * 初始化画笔
     */
    private void initView() {
        mPaintTriangle.setColor(Color.parseColor("#ff6ed887"));
        mPaintTriangle.setDither(true);
        mPaintTriangle.setStyle(Paint.Style.FILL);
        mPaintTriangle.setStrokeWidth(15);
        mPaintTriangle.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate( mTranslationX, getHeight() + 1);
        canvas.drawLine(0,0,mTabWidth,0 ,mPaintTriangle);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTabWidth = getScreenWidth() / mTabVisibleCount;
    }

    /**
     * 传入ViewPager
     */
    public void setViewPager(ViewPager viewPager, int position) {
        this.mViewPager = viewPager;
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(this);
            mViewPager.setCurrentItem(position);
        }

        //重置颜色
        resetTextColor();
        highLightTextColor(position);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {

        // 滚动
        scroll(position, positionOffset);
        if (mPagerChangeListener != null) {
            mPagerChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    /**
     * viewpager滑动过程中，也需要调整三角形的位置
     * 以及TabLayout中的位置
     */
    private void scroll(int position, float positionOffset) {
        mTabWidth = getScreenWidth() / mTabVisibleCount;
        mTranslationX = mTabWidth * (position + positionOffset);
//        Log.e("TAG",tabWidth+"++"+position+"++"+mTranslationX+"++"+getWidth()+"++"+getScreenWidth());
        if ((positionOffset > 0)
                && (position >= mTabVisibleCount - 2)
                && (getChildCount() > mTabVisibleCount)) {
            if (mTabVisibleCount != 1) {
                if (position <= getChildCount() - 3) {
                    //这里需要注意的是，position是从0开始计算的，
                    //如果屏幕可见的是4个tab，那从第三个开始就开始需要滑动整个布局
                    //y轴是不需要滑动的
                    this.scrollTo(((position+1) - (mTabVisibleCount - 1)) * mTabWidth
                            + (int) (mTabWidth * positionOffset), 0);
                }
            } else {
                // 为count为1时 的特殊处理
                this.scrollTo(
                        position * mTabWidth + (int) (mTabWidth * positionOffset), 0);
            }
        }
        invalidate();
    }

    @Override
    public void onPageSelected(int i) {

        if (mPagerChangeListener != null) {
            mPagerChangeListener.onPageSelected(i);
        }
        resetTextColor();
        highLightTextColor(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

        if (mPagerChangeListener != null) {
            mPagerChangeListener.onPageScrollStateChanged(i);
        }
    }


    /**
     * 将选中的页面的文字高亮显示
     */
    private void highLightTextColor(int position) {
        View view = getChildAt(position);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHTCOLOR);
        }
    }

    /**
     * 重置指示器上的文字的颜色
     */
    private void resetTextColor() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }

    /**
     * 传入所有的Tab对应的文案词条，
     * 并且根据传入的数量添加对应的TextView
     */
    public void setTabItems(List<String> listTabs) {
        if (listTabs.size() <= 0) {
            return;
        }
        mListTabs = listTabs;
        removeAllViews();

        int size = listTabs.size();
        int textSize = spToPx(9);
        int width = 0;
        if (size <= 3) {
            width = getScreenWidth() / size;
            mTabVisibleCount = size;
        } else {
            width = getScreenWidth() / 4;
            mTabVisibleCount = 4;
        }
        for (int i = 0; i < size; i++) {
            final int position = i;
            TextView textView = new TextView(getContext());
            textView.setTextSize(textSize);
            textView.setTextColor(COLOR_TEXT_NORMAL);
            LinearLayout.LayoutParams layoutParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.width = width;
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER);
            textView.setText(mListTabs.get(i));
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mViewPager != null) {
                        mViewPager.setCurrentItem(position, true);
                    }
                }
            });
            addView(textView);
        }
    }

    /**
     * 获取的宽度
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    /**
     * 将sp转化成px
     */
    private int spToPx(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }


    /**
     * 对外的接口，这里的接口和原来ViewPager的接口一致
     */
    public interface OnPageChangeListener {
        public void onPageScrolled(int i, float v, int i2);

        public void onPageSelected(int i);

        public void onPageScrollStateChanged(int i);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mPagerChangeListener = listener;
    }
}