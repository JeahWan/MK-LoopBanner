package com.makise.mk_loopbanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> mList = new ArrayList<>();
        mList.add("");
        mList.add("");
        mList.add("");
        ((ViewpagerWithIndicator) findViewById(R.id.vp_wi)).setData(mList, new ViewpagerWithIndicator.OnItemClickListener() {
            @Override
            public void loadItemContent(int position, ImageView view) {
                //根据url加载网络图片
            }

            @Override
            public void onItemClick(int position) {
                //点击事件
            }
        });
    }
}
