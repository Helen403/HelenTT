package com.example.snoy.helen;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import HConstants.HConstants;
import Utils.ControlUtils;
import Utils.DButils;
import Utils.HImageUtils;
import Utils.HImgeLoad;
import activity.LoginActivity;
import activity.ModifySignActivity;
import activity.PersonActivity;
import activity.SetActivity;
import activity.SoundActivity;
import activity.TwoDimActivity;
import adapter.AdapterMenu1;
import adapter.AdapterMenu2;
import base.HBaseFragment;
import bean.MessageEvent;
import bean.ResultBean;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by SNOY on 2017/5/10.
 */
public class MyMenuFragment extends HBaseFragment {

    private CircleImageView menuCir;
    private TextView menuName;
    private TextView menuAccount;
    private TextView menuSign;
    private ImageView menuModifySign;
    private ListView menuListview1;
    private ListView menuListview2;


    AdapterMenu1 adapter1;
    AdapterMenu2 adapter2;


    @Override
    public int getContentView() {
        return R.layout.fragment_my_menu;
    }

    @Override
    public void findViews() {

        menuCir = (CircleImageView) contentView.findViewById(R.id.menu_cir);
        menuCir.setBorderColor(Color.WHITE);
        menuCir.setBorderWidth(2);
        menuName = (TextView) contentView.findViewById(R.id.menu_name);
        menuAccount = (TextView) contentView.findViewById(R.id.menu_account);
        menuSign = (TextView) contentView.findViewById(R.id.menu_sign);
        menuModifySign = (ImageView) contentView.findViewById(R.id.menu_modify_sign);
        menuListview1 = (ListView) contentView.findViewById(R.id.menu_listview_1);
        menuListview2 = (ListView) contentView.findViewById(R.id.menu_listview_2);

    }

    @Override
    public void initData() {
        initHead();
        initList();
    }


    public void initHead() {

        String loginStatus = DButils.get(HConstants.KEY.loginStatus);
        if (loginStatus.equals("1")) {
            String headUrl = DButils.get(HConstants.KEY.figureurl);

            if (!TextUtils.isEmpty(headUrl)) {
                HImageUtils.getInstance().setImageByUrl(headUrl, menuCir);
            } else {
                menuCir.setImageResource(R.mipmap.quila);
            }

            String name = DButils.get(HConstants.KEY.nickName);
            if (!TextUtils.isEmpty(name)) {
                menuName.setText(name);
            } else {
                menuName.setText("游客");
            }
        } else {
            menuCir.setImageResource(R.mipmap.quila);
            menuName.setText("游客");
        }


    }

    private void initList() {
        ArrayList<String> data1 = new ArrayList<>();
        data1.add("基本信息");
        data1.add("我的二维码");
        data1.add("我的录音");

        adapter1 = new AdapterMenu1(getActivity());
        menuListview1.setAdapter(adapter1);
        adapter1.setData(data1);


        ArrayList<String> data2 = new ArrayList<>();
        data2.add("新消息通知");
        data2.add("功能介绍");
        data2.add("客服中心");
        data2.add("关于我们");
        data2.add("注销登录");

        adapter2 = new AdapterMenu2(getActivity());
        menuListview2.setAdapter(adapter2);
        adapter2.setData(data2);

    }

    @Override
    public void setListeners() {
        setOnListeners(menuCir, menuModifySign);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.menu_cir:
                        cir();
                        break;
                    case R.id.menu_modify_sign:
                        menuModifySign();
                        break;
                }
            }
        });

        menuListview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //基本信息
                    case 0: {
                        cir();
                        break;
                    }
                    //我的二维码
                    case 1: {
                        customScan();
                        break;
                    }
                    //我的录音
                    case 2: {
                        sound();
                        break;
                    }

                }
            }
        });
        menuListview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        break;
                    }
                    case 1: {

                        checkUpdata();
                        break;
                    }
                    case 2: {
                        break;
                    }
                    case 3: {

                        break;
                    }
                    case 4: {
                        exit();
                        break;
                    }

                }
            }
        });

    }

    private void menuModifySign() {
        String LoginStatus = DButils.get(HConstants.KEY.loginStatus);
        if (LoginStatus.equals("1")) {
            goToActivityByClass(ModifySignActivity.class);
        } else {
            goToActivityByClass(LoginActivity.class);
        }

    }


    //    String mCheckUrl = "http://192.168.1.131:8080/timeLang/checkVersion?version=2&type=android";
//
//    String mUpdateUrl = "http://192.168.1.131:8080/timeLang/version/app-debug-0.0.1.apk";
//
    private void checkUpdata() {


    }


    private void exit() {
        String loginStatus = DButils.get(HConstants.KEY.loginStatus);
        if (loginStatus.equals("1")) {

            String userCode = DButils.get(HConstants.KEY.userCode);
            HashMap<String, String> map = new HashMap<>();
            map.put("userCode", userCode);
            ControlUtils.getsEveryTime(HConstants.URL.updateCancel, map, ResultBean.class, new ControlUtils.OnControlUtils<ResultBean>() {
                @Override
                public void onSuccess(String url, ResultBean resultBean, String result) {
                    L(result);
                    if (resultBean.result.equals("1")) {
                        T("退出成功");
                        DButils.put(HConstants.KEY.userCode, "");
                        DButils.put(HConstants.KEY.nickName, "");
                        DButils.put(HConstants.KEY.figureurl, "");
                        DButils.put(HConstants.KEY.loginStatus, "0");
                        DButils.put(HConstants.KEY.Email, "");
                        DButils.put(HConstants.KEY.userID, "");
                        DButils.put(HConstants.KEY.signName,"");

                        EventBus.getDefault().post(new MessageEvent(HConstants.EVENT.HOMEREFRESH, null));
                        EventBus.getDefault().post(new MessageEvent(HConstants.EVENT.NAME_REFRESH, null));

                    } else {
                        T("退出失败");
                    }
                }

                @Override
                public void onFailure(String url) {
                    T("退出网络失败");
                }
            });
        } else {
            T("请登录");
        }

    }

    private void sound() {
        goToActivityByClass(SoundActivity.class);
    }

    private void cir() {
        String LoginStatus = DButils.get(HConstants.KEY.loginStatus);
        if (LoginStatus.equals("1")) {
            goToActivityByClass(PersonActivity.class);
        } else {
            goToActivityByClass(LoginActivity.class);
        }
    }

    private void set() {
        goToActivityByClass(SetActivity.class);
    }

    public void refreshHead() {

        String head = DButils.get(HConstants.KEY.figureurl);
        if (!TextUtils.isEmpty(head)) {
            HImgeLoad.setImageByUrl(head, menuCir);
        } else {
            menuCir.setImageResource(R.mipmap.quila);
        }
    }

    public void refreshName() {
        String userID = DButils.get(HConstants.KEY.userID);
        if (!TextUtils.isEmpty(userID)) {
            menuAccount.setText(userID);
        } else {
            menuAccount.setText("");
        }

        String name = DButils.get(HConstants.KEY.nickName);
        if (!TextUtils.isEmpty(name)) {
            menuName.setText(name);
        } else {
            menuName.setText("游客");
        }

        String signName = DButils.get(HConstants.KEY.signName);

        L(signName+"签名");

        if (!TextUtils.isEmpty(signName)) {
            menuSign.setText(signName);
        } else {
            menuSign.setText("");
        }

    }


    public void customScan() {
        MainActivity activity = (MainActivity) getActivity();
        new IntentIntegrator(activity)
                .setOrientationLocked(false)
                .setCaptureActivity(TwoDimActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }

}
