package com.example.snoy.helen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.blankj.utilcode.util.BarUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import HConstants.HConstants;
import Utils.ControlUtils;
import Utils.DButils;
import bean.MessageEvent;
import bean.UserBean;
import bean.WeatherBean;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

public class MainActivity extends FragmentActivity {
    //点击返回键
    private static long mExitTime = 0;
    //点击返回键的时间差
    private static final int EXIT_TIME_GAP = 2000;

    private ArrayList<Fragment> fragments;
    FlowingDrawer mDrawer;
    //侧滑菜单的Fragment
    MyMenuFragment menuFragment;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    String address;

    LinearLayout ll1;
    LinearLayout ll2;
    LinearLayout ll3;
    protected FragmentManager fm;
    protected FragmentTransaction ft;
    protected FragmentTransaction ft1;
    //跳转的记录标记
    protected int indexFragment = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BarUtils.setColor(this, Color.BLACK);

        setContentView(R.layout.activity_main);
        //注册EventBus
        EventBus.getDefault().register(this);
        //抽屉
        //==============================================

        ll1 = (LinearLayout) findViewById(R.id.main_ll_1);
        ll2 = (LinearLayout) findViewById(R.id.main_ll_2);
        ll3 = (LinearLayout) findViewById(R.id.main_ll_3);
        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(0);
            }
        });
        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(1);
            }
        });
        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(2);
            }
        });


        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSED) {
                    Log.i("MainActivity", "Drawer STATE_CLOSED");
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
            }
        });


        fragments = getFragments();
        setDefaultFragment();

        initData();

        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数

        mLocationClient.start();


//        Intent serviceOne = new Intent();
//        serviceOne.setClass(MainActivity.this, LocalService.class);
//        startService(serviceOne);
//
//        Intent serviceTwo = new Intent();
//        serviceTwo.setClass(MainActivity.this, RemoteService.class);
//        startService(serviceTwo);


        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                "4VGgG8uG1DvuWSSlu3Kk8pMV");

        //  DButils.put("Helen", "存取");
        // Log.d("Helen","ssssss"+DButils.get("Helen"));


    }

    private void initData() {
        PermissionGen.with(MainActivity.this)
                .addRequestCode(100)
                .permissions(
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request();


        initLogin();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        menuFragment.initHead();
//        HHomeFragment fragment = (HHomeFragment) fragments.get(0);
//        fragment.refresh();
    }

    private void initLogin() {
        phoneLogin();
    }

    private void WeiChatLogin() {

    }


    private void QQLogin() {
        //QQ登录就为1 手机登录2 微信为3
        if (DButils.get(HConstants.KEY.LoginType).equals("1")) {
            HashMap<String, String> map = new HashMap<>();
            String threeLoginID = DButils.get(HConstants.KEY.QQopenid);
            Log.d("Helen", "threeLoginID" + threeLoginID);
            map.put("threeLoginID", threeLoginID);
            map.put("threeLoginType", "QQ");
            ControlUtils.getsEveryTime(HConstants.URL.LOGIN, map, UserBean.class, new ControlUtils.OnControlUtils<UserBean>() {
                @Override
                public void onSuccess(String url, UserBean userBean, String result) {
                    Log.d("Helen", result);
                    if (userBean == null) {
                        WeiChatLogin();
                        return;
                    }
                    if (userBean.getIsCheck() == 1) {
                        String nickName = userBean.getUserNickName();
                        String userCode = userBean.getUserCode() + "";
                        String useHead = userBean.getUserHead();
                        String userId = userBean.getUserID();
                        String phone = userBean.getUserTelphone();
                        String email = userBean.getUserEmail();
                        String userSignName = userBean.getUserSignName();


                        DButils.put(HConstants.KEY.userCode, userCode);
                        DButils.put(HConstants.KEY.nickName, nickName);
                        DButils.put(HConstants.KEY.figureurl, useHead);
                        DButils.put(HConstants.KEY.loginStatus, "1");
                        DButils.put(HConstants.KEY.phone, phone);
                        DButils.put(HConstants.KEY.Email, email);
                        DButils.put(HConstants.KEY.userID, userId);
                        DButils.put(HConstants.KEY.signName, userSignName);

                        setHomeHHead();
                    } else {
                        Log.d("Helen", "QQ登录失败");
                    }
                }

                @Override
                public void onFailure(String url) {
                    Log.d("Helen", "QQ登录网络异常");
                }
            });
        }
    }


    private void phoneLogin() {
        //先用手机登录
        String phone = DButils.get(HConstants.KEY.phone);
        String pwd = DButils.get(HConstants.KEY.pwd);
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("userName", phone);
            map.put("password", pwd);

            ControlUtils.getsEveryTime(HConstants.URL.LOGIN, map, UserBean.class, new ControlUtils.OnControlUtils<UserBean>() {
                @Override
                public void onSuccess(String url, UserBean userBean, String result) {
                    Log.d("Helen", result);
                    if (userBean == null) {
                        QQLogin();
                        return;
                    }

                    if (userBean.getIsCheck() == 1) {
                        Log.d("Helen", "手机登录成功");
                        String nickName = userBean.getUserNickName();
                        String userCode = userBean.getUserCode() + "";
                        String useHead = userBean.getUserHead();
                        String phone = userBean.getUserTelphone();
                        String email = userBean.getUserEmail();
                        String userId = userBean.getUserID();
                        String userSignName = userBean.getUserSignName();

                        DButils.put(HConstants.KEY.userCode, userCode);
                        DButils.put(HConstants.KEY.nickName, nickName);
                        DButils.put(HConstants.KEY.figureurl, useHead);
                        DButils.put(HConstants.KEY.loginStatus, "1");
                        DButils.put(HConstants.KEY.phone, phone);
                        DButils.put(HConstants.KEY.Email, email);
                        DButils.put(HConstants.KEY.userID, userId);
                        DButils.put(HConstants.KEY.signName, userSignName);
                        DButils.put(HConstants.KEY.LoginType, "2");

                        setHomeHHead();
                    } else {
                        Log.d("Helen", "手机登录失败");
                        DButils.put(HConstants.KEY.loginStatus, "0");
                    }
                }

                @Override
                public void onFailure(String url) {
                    DButils.put(HConstants.KEY.loginStatus, "0");
                    Log.d("Helen", "手机登录网络异常");
                }
            });

        }
    }

    private void setHomeHHead() {
        Log.d("Helen", "头像刷新执行");
        HHomeFragment homeFragment = (HHomeFragment) fragments.get(0);
        if (homeFragment != null) {
            homeFragment.setHeadIcon();
            homeFragment.setList();


            //侧滑菜单刷新数据
            menuFragment.refreshHead();
            menuFragment.refreshName();
        }
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        fm = this.getSupportFragmentManager();
        ft1 = fm.beginTransaction();
        switchFragment(0);
        menuFragment = new MyMenuFragment();
        ft1.replace(R.id.id_container_menu, menuFragment);
        ft1.commit();
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new HHomeFragment());
        fragments.add(new AddFragment());
        fragments.add(new ComFragment());
        return fragments;
    }


    public void switchFragment(int checkIndex) {

        ft = fm.beginTransaction();
        Fragment currentFragment = fragments.get(indexFragment);
        Fragment targetFragment = fragments.get(checkIndex);
        if (currentFragment != targetFragment) {
            if (!targetFragment.isAdded()) {
                ft.hide(currentFragment).add(R.id.main_frame, targetFragment);
            } else {
                ft.hide(currentFragment).show(targetFragment);
            }
        } else {
            if (!targetFragment.isAdded()) {
                ft.add(R.id.main_frame, targetFragment).show(targetFragment);
            }
        }
        ft.commit();
        indexFragment = checkIndex;
    }

    /**
     * 处理返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (SystemClock.uptimeMillis() - mExitTime > EXIT_TIME_GAP) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = SystemClock.uptimeMillis();
            } else {
                //发送广播发送给没finish()的Activity
                MainActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销EventBus
        EventBus.getDefault().unregister(this);
    }


    //=================================================

    /**
     * 接收到EventBus发布的消息事件
     *
     * @param event 消息事件
     */
    @CallSuper
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMessage(@NonNull MessageEvent event) {

        Log.d("Helen", "接受到广播");
        if (event.getType() == HConstants.EVENT.HOMEREFRESH) {
            HHomeFragment fragment = (HHomeFragment) fragments.get(0);
            fragment.setHeadIcon();
            fragment.setList();
            //侧滑菜单刷新数据
            menuFragment.refreshHead();

            ComFragment comFragment = (ComFragment) fragments.get(2);
            // comFragment.setRefresh();
        }

        if (event.getType() == HConstants.EVENT.NAME_REFRESH) {
            menuFragment.refreshName();
        }

    }


    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }


    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            //获取定位结果
            StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型

            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息

            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度

            if (location.getLocType() == BDLocation.TypeGpsLocation) {

                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时

                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数

                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度

                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {

                sb.append("\nhelen : ");
                sb.append(location.getCity());
                address = location.getCity().replace("市", "");

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");

            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");

            } else if (location.getLocType() == BDLocation.TypeServerError) {

                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

            }

            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息

            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }

            Log.i("BaiduLocationApiDem", sb.toString());
            if (mLocationClient != null)
                mLocationClient.stop();
            if (!TextUtils.isEmpty(address))
                weather();

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

    }

    private void weather() {
        HashMap<String, String> map = new HashMap<>();
        map.put("city", address);
        ControlUtils.getsEveryTime(HConstants.URL.Weather, map, WeatherBean.class, new ControlUtils.OnControlUtils<WeatherBean>() {
            @Override
            public void onSuccess(String url, WeatherBean weatherBean, String result) {
                Log.d("Helen", result);
                HHomeFragment homeFragment = (HHomeFragment) fragments.get(0);
                homeFragment.setWeather(weatherBean);
            }

            @Override
            public void onFailure(String url) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @PermissionSuccess(requestCode = 100)
    public void openContact() {
        Toast.makeText(this, "Contact permission is granted", Toast.LENGTH_SHORT).show();
        mLocationClient.start();
    }

    @PermissionFail(requestCode = 100)
    public void failContact() {
        Toast.makeText(this, "Contact permission is not granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    // 通过 onActivityResult的方法获取 扫描回来的 值
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this, "内容为空", Toast.LENGTH_LONG).show();
                Log.d("Helen", "null");
            } else {
                Toast.makeText(this, "扫描成功", Toast.LENGTH_LONG).show();
                // ScanResult 为 获取到的字符串
                String ScanResult = intentResult.getContents();
                Log.d("Helen", ScanResult);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            String path = photos.get(0);
            if (!TextUtils.isEmpty(path)) {
                DButils.put(HConstants.KEY.bgImg, path);
            } else {
                DButils.put(HConstants.KEY.bgImg, "");
            }
            HHomeFragment hHomeFragment = (HHomeFragment) fragments.get(0);
            hHomeFragment.setBgImg();

        }

    }


}
