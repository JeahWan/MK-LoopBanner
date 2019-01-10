#### 轮播布局

**ViewpagerWithIndicator**

    1、基于viewpager的带有联动指示器的无限轮播布局，一般用于banner

    2、API：

        ViewpagerWithIndicator
            |——setData(List mDataList, OnItemClickListener listener)：基础api，默认的自动轮播样式
            |——setDefaultBg(int resIdBg):设置默认背景图片
            |——setIndicatorResId(int resIdSelected, int resIdDefault)：设置指示器的选中和非选中样式
            |——setIndicatorPosition(Position pos)：设置指示器的位置，提供左下，中下，右下三种
            |——setIsAutoPlay(boolean boo)：设置控件是否轮询
            |——setViewSpace(float dpValue)：设置viewpager条目之间的间距
            |——setLoopInterval(long period)：设置轮询的时间间隔（不轮询时无需调用）
            |——operationLoop(boolean boo)：操作控件的开始轮询和停止轮询
            |——getViewPager()：向外提供viewpager对象，以实现自定义需求

    3、用法：

        xml中引用布局com.makise.mk_loopbanner.ViewpagerWithIndicator，然后代码中调用API进行配置。
        示例：mViewpagerWithIndicator.setDefaultBg(R.drawable.banner_default_bg)
                                     .setIndicatorResId(R.drawable.point_seleted,R.drawable.point_default)
                                     .setIndicatorPosition(Position.CENTER)
                                     .setIsAutoPlay(true)
                                     .setLoopInterval(5000)
                                     .setData(mList，listener);

        ** xml自定义属性：

           引入命名空间xmlns:looplayout="http://schemas.android.com/apk/res-auto"

           设置属性：

                默认背景：looplayout:defaultBackground="@drawable/xxxxxx"
                默认指示器图：looplayout:defaultIndicator="@drawable/xxxxxx"
                选中指示器图：looplayout:selectIndicator="@drawable/xxxxxx"
                指示器位置：looplayout:indicatorPosition="center"
                是否自动轮询：looplayout:isAutoPlay="true"
                轮询速度：looplayout:loopInterval="5000"
                默认item间隔：looplayout:defaultSpace="50"
                默认左右边距：looplayout:defaultMargin="100"
                默认圆角：looplayout:defaultCorner="30"
**AutoLoopByViewFlipper**

    1、基于ViewFlipper的无限轮播布局，一般用于公告。

    2、API：

        AutoLoopByViewFlipper
            |——setAnimation(Animation inAnim, Animation outAnim)：设置子view的进入移出动画(动画可以直接使用AnimationHelper提供的四个方向动画)
            |——setInterval(int flipInterval)：设置轮播的时间间隔
            |——setOnItemClickListener(OnItemClickListener listener)：为控件条目点击事件设置监听
            |——setChildViews(List<View> childViews)：为控件赋需要轮播的子布局，一般跟业务数据绑定好之后在设置进来（必须）
            |——startLoop()：开启控件轮播
            |——operationLoop(boolean boo)：操作控件的开始轮播和停止轮播
            |——getViewFlipper()：向外提供viewflipper对象，以实现自定义需求

    3、用法：

        xml中引用布局com.makise.mk_loopbanner.AutoLoopByViewFlipper，然后代码中调用API进行配置。
        示例：mAutoLoopByViewFlipper.setsetAnimation(inAnim,outAnim)
                                    .setInterval(3000)
                                    .setOnItemClickListener(new OnItemClickListener())
                                    .setChildViews(new arraylist<View>())
                                    .startLoop();