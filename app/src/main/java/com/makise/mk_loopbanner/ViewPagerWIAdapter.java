/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
package com.makise.mk_loopbanner;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * banner轮播 adapter
 */
public class ViewPagerWIAdapter extends PagerAdapter {

    private Context context;
    private List dataList;
    private int resIdBg;
    private ViewpagerWithIndicator.OnItemClickListener listener;
    private float corner;

    public ViewPagerWIAdapter(Context context, List dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public void setCorner(float corner) {
        this.corner = corner;
    }

    public void setData(List dataList, int resIdBg, ViewpagerWithIndicator.OnItemClickListener listener) {
        if (isEmpty(dataList)) {
            return;
        }
        this.dataList = dataList;
        this.resIdBg = resIdBg;
        this.listener = listener;
        this.notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        int count;
        //只有一张时 只生成1个count防止滑动
        if (isEmpty(dataList) || dataList.size() == 1) {
            count = 1;
        } else {
            count = dataList.size() * 1000;
        }
        return count;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = View.inflate(context, R.layout.banner_item, null);
        RoundImageView imageView = (RoundImageView) view.findViewById(R.id.iv_banner);
        imageView.setImageResource(resIdBg);
        imageView.setRect_adius(corner);
        final int index = isEmpty(dataList) ? 0 : position % dataList.size();
        if (listener != null) {
            listener.loadItemContent(index, imageView);
        }
//        final Banner banner = dataList.get(index);
//        Glide.with(context)
//                .load(host + banner.pic)
//                .error(R.mipmap.banner_default)
//                .placeholder(R.mipmap.banner_default)
//                .into(imageView);

        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        //http开头才打开
//                        if (!TextUtils.isEmpty(banner.url) && banner.url.startsWith("http")) {
//                            listener.onClick(banner.name, banner.url);
//                        }
                        if (listener != null) {
                            listener.onItemClick(index);
                        }
                    }
                }
        );
        container.addView(view);
        return view;
    }

    public static <V> boolean isEmpty(List<V> sourceList) {
        return (sourceList == null || sourceList.size() == 0);
    }

    public void setDefaultBackground(int resIdBg) {
        this.resIdBg = resIdBg;
    }
}
