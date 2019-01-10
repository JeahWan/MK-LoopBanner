package com.makise.mk_loopbanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 带有指示器的viewpager
 * 可自动轮播，可指定指示器位置，可自定义指示器样式
 * Created by Makise on 2016/12/14.
 */
public class ViewpagerWithIndicator extends FrameLayout {

    private Context context;
    private ViewPager view_pager;
    private LinearLayout ll_vp;
    private List dataList;
    private ViewPagerWIAdapter mAdapter;
    private boolean isAutoPlay;
    private Position indicatorPosition;
    private int resIdSelected;
    private int resIdDefault;
    private long period;
    private int resIdBg;
    private Timer delayTimer;
    private float margin;
    private float space;

    public enum Position {
        Center, Left, Right,None
    }

    public ViewpagerWithIndicator(Context context) {
        this(context, null);
    }

    public ViewpagerWithIndicator(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.context = context;
        init(attrs);
    }

    /**
     * 初始化
     */
    private void init(AttributeSet attrs) {

        LayoutInflater.from(context).inflate(R.layout.layout_vp_with_ic, this, true);
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        ll_vp = (LinearLayout) findViewById(R.id.ll_vp);

//        view_pager.setOffscreenPageLimit(3);
//        view_pager.setPageMargin(ViewUtils.dip2px(context,8));
//        view_pager.setPageTransformer(true, new ScrollOffsetTransformer());

        mAdapter = new ViewPagerWIAdapter(context, null);
        view_pager.setAdapter(mAdapter);

        ll_vp.setGravity(Gravity.CENTER);
        ll_vp.setPadding(22, 0, 22, 0);
        isAutoPlay = true;
        resIdSelected = R.drawable.point_seleted;
        resIdDefault = R.drawable.point_default;
        period = 5000;
        resIdBg = R.mipmap.banner_default;

        RelativeLayout rl_container = (RelativeLayout) findViewById(R.id.rl_container);
        rl_container.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return view_pager.dispatchTouchEvent(event);
            }
        });

        handleAttributes(attrs);

    }

    private void handleAttributes(AttributeSet attrs) {
        // 自定义属性处理
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoopLayout);
        if (a.hasValue(R.styleable.LoopLayout_defaultCorner)) {
            mAdapter.setCorner(a.getFloat(R.styleable.LoopLayout_defaultCorner, 0));
        }
        if (a.hasValue(R.styleable.LoopLayout_defaultSpace)) {
            space = a.getFloat(R.styleable.LoopLayout_defaultSpace, 0);
        }
        if (a.hasValue(R.styleable.LoopLayout_defaultMargin)) {
            margin = a.getFloat(R.styleable.LoopLayout_defaultMargin, 0);
        }
        setViewSpace(space,margin);
        if (a.hasValue(R.styleable.LoopLayout_defaultBackground)) {
            //默认背景图
            setDefaultBg(a.getResourceId(R.styleable.LoopLayout_defaultBackground, R.mipmap.banner_default));
        }
        if (a.hasValue(R.styleable.LoopLayout_defaultIndicator)) {
            //默认指示器
            this.resIdDefault = a.getResourceId(R.styleable.LoopLayout_defaultIndicator, R.drawable.point_default);
        }
        if (a.hasValue(R.styleable.LoopLayout_selectIndicator)) {
            //选中指示器
            this.resIdSelected = a.getResourceId(R.styleable.LoopLayout_selectIndicator, R.drawable.point_seleted);
        }
        if (a.hasValue(R.styleable.LoopLayout_indicatorPosition)) {
            //指示器位置
            Position position = Position.Center;
            switch (a.getInteger(R.styleable.LoopLayout_indicatorPosition, 0)) {
                case 0:
                    position = Position.Center;
                    break;
                case 1:
                    position = Position.Left;
                    break;
                case 2:
                    position = Position.Right;
                    break;
                case 3:
                    position = Position.None;
                    break;
                default:
                    break;
            }
            setIndicatorPosition(position);
        }
        if (a.hasValue(R.styleable.LoopLayout_isAutoPlay)) {
            //是否自动轮询
            isAutoPlay = a.getBoolean(R.styleable.LoopLayout_isAutoPlay, true);
        }
        if (a.hasValue(R.styleable.LoopLayout_loopInterval)) {
            //轮询间隔
            period = a.getInteger(R.styleable.LoopLayout_loopInterval, 5000);
        }
        a.recycle();
    }

    /**
     * viewpager跟指示器联动
     */
    private void setLitener() {
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                invalidate();
            }

            @Override
            public void onPageSelected(int position) {
                invalidate();
                int index = position % dataList.size();
                if (dataList.size() > 0) {
                    for (int i = 0; i < ll_vp.getChildCount(); i++) {
                        if (i == index) {
                            ((ImageView) ll_vp.getChildAt(i)).setImageResource(resIdSelected);
                        } else {
                            ((ImageView) ll_vp.getChildAt(i)).setImageResource(resIdDefault);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                invalidate();
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE: //未手动
                        if (isAutoPlay)
                            startLoop();
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING: //手动滑动
                        stopLoop();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 关闭自动轮询
     */
    public void stopLoop() {
        //计时器不为空时 取消计时器并置空
        if (delayTimer != null) {
            delayTimer.cancel();
            delayTimer = null;
        }
    }

    /**
     * 开启自动轮询
     */
    private void startLoop() {
        //为空时 初始化计时器
        if (delayTimer == null) {
            delayTimer = new Timer();
            delayTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ViewpagerWithIndicator.this.post(new Runnable() {
                        @Override
                        public void run() {
                            view_pager.setCurrentItem(view_pager.getCurrentItem() + 1);
                        }
                    });
                }
            }, period, period);
        }
    }

    //为容器添加跟item同样数量的点
    private void createIndicator() {
        ll_vp.removeAllViews();
        for (int i = 0; i < dataList.size(); i++) {
            // 添加点了
            ImageView point = new ImageView(context);
            // 创建了布局的参数 全部都是包裹内容
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 22;
            // 给ImageView 设置参数
            point.setLayoutParams(params);
            if (i == 0) {
                point.setImageResource(resIdSelected);
            } else {
                point.setImageResource(resIdDefault);
            }
            ll_vp.addView(point);
        }
    }

    /**
     * 数据填充
     *
     * @param mDataList
     * @param listener
     */
    public void setData(List mDataList, OnItemClickListener listener) {
        this.dataList = mDataList;
        ViewPagerWIAdapter tempAdapter = mAdapter;
        tempAdapter.setData(mDataList, resIdBg, listener);
        view_pager.setAdapter(tempAdapter);
        //大于1张
        if (dataList.size() > 1) {
            //设置到中间
            view_pager.setCurrentItem(tempAdapter.getCount() / 2, false);
            //创建指示器
            createIndicator();
            //设置监听
            setLitener();
            //设置了自动轮询
            if (isAutoPlay) {
                startLoop();
            }
        } else if (dataList.size() == 1) {
            //只有一张时 清空已设置的点儿
            ll_vp.removeAllViews();
        }
    }

    /**
     * 设置默认的轮播背景
     *
     * @param resIdBg
     * @return
     */
    public ViewpagerWithIndicator setDefaultBg(int resIdBg) {
        this.resIdBg = resIdBg;
        mAdapter.setDefaultBackground(resIdBg);
        return this;
    }

    /**
     * 设置viewpager图片之间的间距
     *
     * @param space
     * @param margin
     * @return
     */
    public ViewpagerWithIndicator setViewSpace(float space,float margin) {
        view_pager.setPageMargin(dip2px(context, space));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.leftMargin = dip2px(context, margin);
        lp.rightMargin = dip2px(context, margin);
        view_pager.setLayoutParams(lp);
        return this;
    }

    /**
     * 自定义指示器的位置（左 中 右三种）
     */
    public ViewpagerWithIndicator setIndicatorPosition(Position pos) {
        this.indicatorPosition = pos;
        switch (indicatorPosition) {
            case Center:
                ll_vp.setGravity(Gravity.CENTER);
                break;
            case Left:
                ll_vp.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                break;
            case Right:
                ll_vp.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                break;
            case None:
                ll_vp.setVisibility(GONE);
                break;
            default:
                ll_vp.setGravity(Gravity.CENTER);
                break;
        }
        return this;
    }

    /**
     * 设置自定义的指示器样式，分选中和非选中两种
     *
     * @param resIdSelected
     * @param resIdDefault
     */
    public ViewpagerWithIndicator setIndicatorResId(int resIdSelected, int resIdDefault) {
        this.resIdSelected = resIdSelected;
        this.resIdDefault = resIdDefault;
        return this;
    }

    /**
     * 设置轮询时间间隔
     *
     * @param period 毫秒值
     */
    public ViewpagerWithIndicator setLoopInterval(long period) {
        this.period = period;
        return this;
    }

    /**
     * 设置控件是否自动轮询
     *
     * @param boo
     * @return
     */
    public ViewpagerWithIndicator setIsAutoPlay(boolean boo) {
        this.isAutoPlay = boo;
        return this;
    }

    /**
     * 操作轮询状态
     *
     * @param boo true表示开启轮询，false表示中断
     */
    public void operationLoop(boolean boo) {
        if (boo) {
            startLoop();
        } else {
            stopLoop();
        }
    }

    public ViewPager getViewPager(){
        return view_pager;
    }

    /**
     * 控件条目点击事件接口
     */
    public interface OnItemClickListener {
        void loadItemContent(int position, ImageView view);

        void onItemClick(int position);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
