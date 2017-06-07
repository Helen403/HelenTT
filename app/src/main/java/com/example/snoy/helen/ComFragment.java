package com.example.snoy.helen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import HConstants.HConstants;
import Utils.ControlUtils;
import Utils.DButils;
import activity.FindActivity;
import activity.FriendActivity;
import base.HBaseFragment;
import bean.ComBean;
import fragment.CallMeFragment;
import fragment.FriendFragment;
import fragment.MeCallFragment;

/**
 * Created by SNOY on 2017/5/12.
 */
public class ComFragment extends HBaseFragment {

    private RelativeLayout rl3;
    private ImageView comSeachIcon;
    private SlidingTabLayout comSlide;
    private ViewPager comVp;
    private ImageView comAdd;
    private String[] mTitles = {"好友", "叫醒我的", "我叫醒的"};
    ArrayList<HBaseFragment> mFragments;

    @Override
    public int getContentView() {
        return R.layout.fragment_com;
    }

    @Override
    public void findViews() {
        rl3 = (RelativeLayout) contentView.findViewById(R.id.rl_3);
        comSeachIcon = (ImageView) contentView.findViewById(R.id.com_seach_icon);
        comSlide = (SlidingTabLayout) contentView.findViewById(R.id.com_slide);
        comVp = (ViewPager) contentView.findViewById(R.id.com_vp);
        comAdd = (ImageView) contentView.findViewById(R.id.com_seach_add);
    }

    @Override
    public void initData() {

        initListData();
        initTab();
        initFriendList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //不可见
            //还原标题栏
            //取消加载图片的任务
            //ImageUtils.getInstance().cancelTask();
        } else {
            //当前可见
            initData();
        }
    }


    private void initFriendList() {
        String loginStatus = DButils.get(HConstants.KEY.loginStatus);
        String userCode = DButils.get(HConstants.KEY.userCode);
        if (loginStatus.equals("1") && !TextUtils.isEmpty(userCode) && mFragments.size() > 0) {
            HashMap<String, String> map = new HashMap<>();
            map.put("userCode", userCode);
            ControlUtils.getsEveryTime(HConstants.URL.findFriendList, map, ComBean.class, new ControlUtils.OnControlUtils<ComBean>() {
                @Override
                public void onSuccess(String url, ComBean comBean, String result) {
                    L(result);
                    final ArrayList<ComBean> data = getListFromJson(result, ComBean.class);
                    ComBean comBean1 = data.get(0);
                    ComBean comBean2 = data.get(1);
                    ComBean comBean3 = data.get(2);

                    FriendFragment friendFragment = (FriendFragment) mFragments.get(0);
                    CallMeFragment callMeFragment = (CallMeFragment) mFragments.get(1);
                    MeCallFragment meCallFragment = (MeCallFragment) mFragments.get(2);

                    friendFragment.setFriend(comBean1);
                    callMeFragment.setFriend(comBean2);
                    meCallFragment.setFriend(comBean3);
                }

                @Override
                public void onFailure(String url) {

                }
            });

        } else {

        }


    }

    private void initTab() {

        L("一次");
        comSlide.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                comVp.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (position == 0) {
                    //  mTabLayout_2.showMsg(0, mRandom.nextInt(100) + 1);
//                    UnreadMsgUtils.show(mTabLayout_2.getMsgView(0), mRandom.nextInt(100) + 1);
                }
            }
        });

        comVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                comSlide.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // comVp.setCurrentItem(1);
        comVp.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        comVp.setOffscreenPageLimit(3);
        comSlide.setViewPager(comVp);
    }

    private void initListData() {
        mFragments = new ArrayList<>();
        mFragments.add(new FriendFragment());
        mFragments.add(new CallMeFragment());
        mFragments.add(new MeCallFragment());
    }

    @Override
    public void setListeners() {
        setOnListeners(rl3);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.rl_3:
                        search();

                        break;
                }
            }
        });
    }

    private void search() {
        goToActivityByClass(FindActivity.class);
    }


    private void friend() {
        goToActivityByClass(FriendActivity.class);
    }

    private void find() {
        goToActivityByClass(FindActivity.class);
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= mTitles.length) {
                return mTitles[mTitles.length - 1];
            }
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            L("当前指示" + position);
            return mFragments.get(position);
        }
    }


    Gson gson = new Gson();

    /**
     * json转成ArrayList
     */
    public ArrayList<bean.ComBean> getListFromJson(String gsonString, Class<bean.ComBean> tClass) {
        ArrayList<bean.ComBean> list;
        try {
            if (gson != null && !TextUtils.isEmpty(gsonString)) {
                //过滤gson
                gsonString = gsonString.trim();
                if (gsonString.startsWith("ufeff")) {
                    gsonString = gsonString.substring(1);
                }
                TypeToken<ArrayList<ComBean>> tt = new TypeToken<ArrayList<bean.ComBean>>() {
                };
                list = gson.fromJson(gsonString, tt.getType());
                return list;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public void setRefresh() {
        initFriendList();
    }


}
