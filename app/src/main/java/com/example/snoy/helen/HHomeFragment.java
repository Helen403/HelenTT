package com.example.snoy.helen;

import android.text.TextUtils;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import HConstants.HConstants;
import Utils.ControlUtils;
import Utils.DButils;
import adapter.AdapterHHome;
import base.HBaseFragment;
import bean.HomeBean;
import bean.WeatherBean;
import view.HomeHView;

/**
 * Created by Helen on 2017/6/3.
 */
public class HHomeFragment extends HBaseFragment {

    ListView listView;
    HomeHView homeHView;
    AdapterHHome adapterHHome;

    @Override
    public int getContentView() {
        return R.layout.fragment_home_l;
    }

    @Override
    public void findViews() {
        listView = (ListView) contentView.findViewById(R.id.home_l_listview);
        homeHView = new HomeHView(getActivity());
        listView.addHeaderView(homeHView);
        adapterHHome = new AdapterHHome(getActivity());
        listView.setAdapter(adapterHHome);

    }

    @Override
    public void initData() {

    }

    @Override
    public void setListeners() {

    }

    public void setWeather(WeatherBean weatherBean) {
        homeHView.setWeather(weatherBean);
    }


    public void setHeadIcon() {
        homeHView.setHeadIcon();
    }

    public void setList() {
        String userCode = DButils.get(HConstants.KEY.userCode);
        HashMap<String, String> map = new HashMap<>();
        map.put("userCode", userCode);
        ControlUtils.getsEveryTime(HConstants.URL.findIndexRemind, map, HomeBean.class, new ControlUtils.OnControlUtils<HomeBean>() {
            @Override
            public void onSuccess(String url, HomeBean homeBean, String result) {
                L(result);
                ArrayList<HomeBean> data = getListFromJson(result, HomeBean.class);
                adapterHHome.setData(data);
            }

            @Override
            public void onFailure(String url) {
                L("网络失败");
            }
        });

    }


    Gson gson = new Gson();

    /**
     * json转成ArrayList
     */
    public ArrayList<HomeBean> getListFromJson(String gsonString, Class<HomeBean> tClass) {
        ArrayList<HomeBean> list;
        try {
            if (gson != null && !TextUtils.isEmpty(gsonString)) {
                //过滤gson
                gsonString = gsonString.trim();
                if (gsonString.startsWith("ufeff")) {
                    gsonString = gsonString.substring(1);
                }
                TypeToken<ArrayList<HomeBean>> tt = new TypeToken<ArrayList<HomeBean>>() {
                };
                list = gson.fromJson(gsonString, tt.getType());
                return list;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public void setBgImg(){
        homeHView.setBgImg();
    }


}
