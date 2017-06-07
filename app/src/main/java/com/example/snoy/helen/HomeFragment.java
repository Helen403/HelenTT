package com.example.snoy.helen;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.blankj.utilcode.util.SizeUtils;

import java.util.ArrayList;

import HConstants.HConstants;
import Utils.ControlUtils;
import adapter.AdapterHomeMain;
import base.HBaseApplication;
import base.HBaseFragment;
import bean.HistoryTodayBean;
import bean.HomeMainItemBean;
import bean.HomeRemindBean;
import bean.MessageEvent;
import bean.UserBean;
import bean.WeatherBean;
import custemview.PullToZoomListView;
import view.HomeLastView;
import view.HomePersonView;

/**
 * Created by WangChang on 2016/5/15.
 */
public class HomeFragment extends HBaseFragment {

    PullToZoomListView pullToZoomListView;
    AdapterHomeMain adapter;
    ArrayList<HomeRemindBean> date;

    ImageView person;

    HomePersonView homePersonView;

    HomeLastView homeLastView;

    Handler handler = new Handler();


    @Override
    public int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public void findViews() {
        pullToZoomListView = (PullToZoomListView) contentView.findViewById(R.id.home_listview);
        person = (ImageView) contentView.findViewById(R.id.home_person);
        date = new ArrayList<>();
        ArrayList<HomeMainItemBean> data1 = new ArrayList<>();
        data1.add(new HomeMainItemBean());
        data1.add(new HomeMainItemBean());
        ArrayList<HomeMainItemBean> data2 = new ArrayList<>();
        data2.add(new HomeMainItemBean());
        ArrayList<HomeMainItemBean> data3 = new ArrayList<>();
        data3.add(new HomeMainItemBean());
        ArrayList<HomeMainItemBean> data4 = new ArrayList<>();
        data4.add(new HomeMainItemBean());
        data4.add(new HomeMainItemBean());
        date.add(new HomeRemindBean(false, data1, "今天"));
        date.add(new HomeRemindBean(false, data2, "明天"));
        date.add(new HomeRemindBean(false, data3, "一个月内"));
        date.add(new HomeRemindBean(true, data4, "一年内"));
        adapter = new AdapterHomeMain(getActivity(), null, date);
        pullToZoomListView.setAdapter(adapter);


        pullToZoomListView.getHeaderView().setImageResource(R.drawable.large_pic_customnb_new);
        pullToZoomListView.getHeaderView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        pullToZoomListView.setShadow(R.drawable.shadow_bottom);

        homePersonView = new HomePersonView(getActivity());

        homeLastView = new HomeLastView(getActivity());
        pullToZoomListView.addFooterView(homeLastView);

    }

    @Override
    public void initData() {
        ControlUtils.getsEveryTime(HConstants.URL.historyToday, null, HistoryTodayBean.class, new ControlUtils.OnControlUtils<HistoryTodayBean>() {
            @Override
            public void onSuccess(String url, HistoryTodayBean historyTodayBean,  String result) {
                if (historyTodayBean != null) {
                    adapter.setHistoryTodayBean(historyTodayBean);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
            }

            @Override
            public void onFailure(String url) {

            }
        });
    }

    @Override
    public void setListeners() {
        setOnListeners(person);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.home_person:
                        showPopupWindow(v, homePersonView);
                        break;
                }
            }
        });


    }


    public void setWeather(final WeatherBean weatherBean) {
        if (pullToZoomListView != null) {
            HBaseApplication.handler.post(new Runnable() {
                @Override
                public void run() {
                    pullToZoomListView.setWeather(weatherBean);
                }
            });
        }
    }


    /**
     * 登录成功后 刷新数据
     *
     * @param event
     */
    public void refresh(MessageEvent event) {
        UserBean userBean = (UserBean) event.getData();

         //name.setText(userBean.getUserNickName());
    }


    /**
     * 显示popupWindow
     * parent    需要挂上的View
     * showView  需要显示的View
     */
    public PopupWindow showPopupWindow(View parent, View showView) {
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int screenW = windowManager.getDefaultDisplay().getWidth();
        int screenH = windowManager.getDefaultDisplay().getHeight();
        PopupWindow popupWindow = new PopupWindow(showView, SizeUtils.dp2px(140), screenH);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(parent, 10, 0);
        return popupWindow;
    }

}
