package activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.example.snoy.helen.R;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import HConstants.HConstants;
import Utils.ControlUtils;
import Utils.DButils;
import base.HBaseActivity;
import bean.MessageEvent;
import bean.ResultBean;
import bean.UserBean;


/**
 * Created by SNOY on 2017/5/10.
 */
public class LoginActivity extends HBaseActivity {

    private ImageView loginClose;
    private ImageView loginIcon;
    private LinearLayout loginLl;
    private ImageView loginPhoneIcon1;
    private EditText loginPhone;
    private ImageView loginPhoneIcon2;
    private EditText loginPwd;
    private TextView loginLogin;
    private RelativeLayout loginRl;
    private TextView loginForget;
    private TextView loginRegister;
    private LinearLayout loginLl1;
    private ImageView weixin;
    private ImageView qq;


    private Tencent mTencent;
    private QQLoginListener mListener;
    private UserInfo mInfo;
    private String name, figureurl, city, gender;
    private String openID;

    @Override
    public int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    public void findViews() {
        loginClose = (ImageView) findViewById(R.id.login_close);
        loginIcon = (ImageView) findViewById(R.id.login_icon);
        loginLl = (LinearLayout) findViewById(R.id.login_ll);
        loginPhoneIcon1 = (ImageView) findViewById(R.id.login_phone_icon_1);
        loginPhone = (EditText) findViewById(R.id.login_phone);
        loginPhoneIcon2 = (ImageView) findViewById(R.id.login_phone_icon_2);
        loginPwd = (EditText) findViewById(R.id.login_pwd);
        loginLogin = (TextView) findViewById(R.id.login_login);
        loginRl = (RelativeLayout) findViewById(R.id.login_rl);
        loginForget = (TextView) findViewById(R.id.login_forget);
        loginRegister = (TextView) findViewById(R.id.login_register);
        loginLl1 = (LinearLayout) findViewById(R.id.login_ll_1);
        weixin = (ImageView) findViewById(R.id.weixin);
        qq = (ImageView) findViewById(R.id.qq);
    }


    @Override
    public void initData() {
        initQQ();
        initPhonePwd();
    }

    private void initPhonePwd() {
        String phoneTmp = DButils.get(HConstants.KEY.phone);
        String pwd = DButils.get(HConstants.KEY.pwd);
        if (!TextUtils.isEmpty(phoneTmp) && !TextUtils.isEmpty(pwd)) {
            loginPhone.setText(phoneTmp);
            loginPwd.setText(pwd);
        }
    }


    @Override
    public void setListeners() {
        setOnListeners(weixin, loginClose, qq, loginRegister, loginLogin);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.weixin:

                        break;
                    case R.id.login_close:
                        finish();
                        break;
                    case R.id.login_login:
                        login();
                        break;
                    case R.id.qq:
                        qq();
                        break;
                    case R.id.login_register:
                        register();
                        break;
                }
            }
        });
    }

    private void login() {
        String phone = loginPhone.getText().toString();
        String pwd = loginPwd.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            T("电话不能为空");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            T("密码不能为空");
            return;
        }

        DButils.put(HConstants.KEY.phone, phone);
        DButils.put(HConstants.KEY.pwd, pwd);


        //更改状态
        loginLogin.setBackgroundResource(R.drawable.shape_bg_login_press);
        loginLogin.setEnabled(false);

        HashMap<String, String> map = new HashMap<>();
        map.put("userName", phone);
        map.put("password", pwd);

        ControlUtils.getsEveryTime(HConstants.URL.LOGIN, map, UserBean.class, new ControlUtils.OnControlUtils<UserBean>() {
            @Override
            public void onSuccess(String url, UserBean userBean, String result) {
                loginLogin.setBackgroundResource(R.drawable.shape_bg_login_nomal);
                loginLogin.setEnabled(true);
                L(result);
                T("登录成功");
                if (userBean.getIsCheck() == 1) {
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
                    DButils.put(HConstants.KEY.LoginType,"2");

                    //发一个消息给HomeFragment 替换 名字
                    onSendMessage(new MessageEvent(HConstants.EVENT.HOMEREFRESH, userBean));
                    onSendMessage(new MessageEvent(HConstants.EVENT.NAME_REFRESH, userBean));

                    finish();
                } else {
                    T("登录失败");

                }

            }

            @Override
            public void onFailure(String url) {
                loginLogin.setBackgroundResource(R.drawable.shape_bg_login_nomal);
                loginLogin.setEnabled(true);
                T("网络异常");

            }
        });

    }

    private void register() {
        goToActivityByClass(activity.RegisterActivity.class);
    }

    private void initQQ() {
        mListener = new QQLoginListener();
        // 实例化Tencent
        if (mTencent == null) {
            mTencent = Tencent.createInstance("1106092575", this.getApplicationContext());
        }
    }


    private void iphone() {
        goToActivityByClass(activity.PhoneActivity.class);
    }

    private void qq() {
        T("君哥点击了");
        QQLogin();
    }

    @Override
    public void onReceiveMessage(@NonNull MessageEvent event) {
        super.onReceiveMessage(event);
        if (event.getType() == HConstants.EVENT.LOGINACTIVITY_CLOSE) {
            finish();
        }
    }


    /**
     * 登录方法
     */
    private void QQLogin() {
        //如果session不可用，则登录，否则说明已经登录
        if (!mTencent.isSessionValid()) {
            mTencent.login(this, "all", mListener);
        }
    }

    // 实现登录成功与否的接口
    private class QQLoginListener implements IUiListener {

        @Override
        public void onComplete(Object object) { //登录成功
            //获取openid和token
            initOpenIdAndToken(object);
            //获取用户信息
            getUserInfo();
        }

        @Override
        public void onError(UiError uiError) {  //登录失败
        }

        @Override
        public void onCancel() {                //取消登录
        }
    }


    private void initOpenIdAndToken(Object object) {
        JSONObject jb = (JSONObject) object;
        try {
            String openID = jb.getString("openid");  //openid用户唯一标识
            String access_token = jb.getString("access_token");
            String expires = jb.getString("expires_in");

            DButils.put(HConstants.KEY.QQopenid, openID);
            this.openID = openID;
            L(openID + "666");

            mTencent.setOpenId(openID);
            mTencent.setAccessToken(access_token, expires);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo() {
        QQToken token = mTencent.getQQToken();
        mInfo = new UserInfo(this, token);
        mInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object object) {
                JSONObject jb = (JSONObject) object;
                try {
                    L(jb.toString());
                    name = jb.getString("nickname");
                    DButils.put(HConstants.KEY.nickName, name);
                    L(name);
                    figureurl = jb.getString("figureurl_qq_2");  //头像图片的url
                    DButils.put(HConstants.KEY.figureurl, figureurl);
                    L(figureurl);

                    city = jb.getString("city");
                    DButils.put(HConstants.KEY.city, city);

                    gender = jb.getString("gender");
                    DButils.put(HConstants.KEY.gender, gender);

                    //退出登录页面 设置数据
                    finishA();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResultData(requestCode, resultCode, data, mListener);
    }


    private void finishA() {

        DButils.put(HConstants.KEY.LoginType, "1");
        HashMap<String, String> map = new HashMap<>();
        map.put("phoneDeviceCode", PhoneUtils.getIMEI());
        map.put("phoneDeviceName", DeviceUtils.getManufacturer() + " " + DeviceUtils.getModel());
        map.put("isThreeLogin", 1 + "");
        map.put("threeLoginType", "QQ");
        map.put("threeLoginID", openID);
        map.put("sex", gender);
        map.put("userHead", figureurl);
        map.put("userNickName", name);

        String userCode = DButils.get(HConstants.KEY.userCode);

        if (!TextUtils.isEmpty(userCode)) {
            map.put("useCode", userCode);
        } else {
            map.put("useCode", "");
        }

        ControlUtils.getsEveryTime(HConstants.URL.saveUser, map, ResultBean.class, new ControlUtils.OnControlUtils<ResultBean>() {
            @Override
            public void onSuccess(String url, ResultBean resultBean, String result) {
                L(result);
                if (resultBean.result.equals("1")) {
                    L(resultBean.userCode);
                    DButils.put(HConstants.KEY.userCode, resultBean.userCode);
                    DButils.put(HConstants.KEY.userID, resultBean.userID);
                    nomalLogin();
                } else {
                    if (resultBean.userCode.equals("-1")) {
                        nomalLogin();
                    }

                    L("登录失败");
                }
            }

            @Override
            public void onFailure(String url) {
                L("网络失败");
            }
        });



    }

    //正常登录
    private void nomalLogin() {

        HashMap<String, String> map = new HashMap<>();
        String threeLoginID = DButils.get(HConstants.KEY.QQopenid);
        Log.d("Helen", "threeLoginID" + threeLoginID);
        map.put("threeLoginID", threeLoginID);
        map.put("threeLoginType", "QQ");
        ControlUtils.getsEveryTime(HConstants.URL.LOGIN, map, UserBean.class, new ControlUtils.OnControlUtils<UserBean>() {
            @Override
            public void onSuccess(String url, UserBean userBean, String result) {
                Log.d("Helen", result);
                if (userBean.getIsCheck() == 1) {
                    T("登录成功");
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

                    DButils.put(HConstants.KEY.LoginType, "1");

                    //发送数据到
                    //发一个消息给HomeFragment 替换 名字
                    onSendMessage(new MessageEvent(HConstants.EVENT.HOMEREFRESH, null));
                    onSendMessage(new MessageEvent(HConstants.EVENT.NAME_REFRESH, null));
                    finish();
                } else {
                    DButils.put(HConstants.KEY.loginStatus, "0");
                    T( "登录失败");
                }
            }

            @Override
            public void onFailure(String url) {
                DButils.put(HConstants.KEY.loginStatus, "0");
                Log.d("Helen", "gggg失败");
            }
        });

    }
}
