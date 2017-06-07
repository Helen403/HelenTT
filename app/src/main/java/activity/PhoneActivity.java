package activity;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.snoy.helen.R;

import java.util.HashMap;

import HConstants.HConstants;
import Utils.ControlUtils;
import Utils.DButils;
import Utils.EditTextClearTools;
import base.HBaseActivity;
import bean.MessageEvent;
import bean.UserBean;
import view.BindPhoneView;

/**
 * Created by SNOY on 2017/5/11.
 */
public class PhoneActivity extends HBaseActivity {

    private ImageView phoneClose;
    private LinearLayout phoneLl;
    private EditText phonePhone;
    private ImageView phonePhoneV;
    private EditText phonePwd;
    private ImageView phonePwdV;
    private TextView phoneLogin;
    private TextView phoneRegister;
    private TextView phoneForget;

    private boolean isHidePwd = true;// 输入框密码是否是隐藏的，默认为true
    BindPhoneView bindPhoneView;

    @Override
    public int getContentView() {
        return R.layout.activity_phone;
    }

    @Override
    public void findViews() {
        //hideHeadView();
        phoneClose = (ImageView) findViewById(R.id.phone_close);
        phoneLl = (LinearLayout) findViewById(R.id.phone_ll);
        phonePhone = (EditText) findViewById(R.id.phone_phone);
        phonePhoneV = (ImageView) findViewById(R.id.phone_phone_v);
        phonePwd = (EditText) findViewById(R.id.phone_pwd);
        phonePwdV = (ImageView) findViewById(R.id.phone_pwd_v);
        phoneLogin = (TextView) findViewById(R.id.phone_login);
        phoneRegister = (TextView) findViewById(R.id.phone_register);
        phoneForget = (TextView) findViewById(R.id.phone_forget);
    }

    @Override
    protected void onShowMessage(RelativeLayout relativeLayout) {
        super.onShowMessage(relativeLayout);
        bindPhoneView = new BindPhoneView(this);
        bindPhoneView.setVisibility(View.GONE);
        relativeLayout.addView(bindPhoneView);
    }

    @Override
    public void initData() {
        //添加清楚监听器
        EditTextClearTools.addclerListener(phonePhone, phonePhoneV);
    }

    @Override
    public void setListeners() {
        setOnListeners(phoneClose, phonePwdV, phoneRegister, phoneForget, phoneLogin);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.phone_close:
                        close();

                        break;
                    case R.id.phone_pwd_v:
                        pwd();
                        break;
                    case R.id.phone_register:
                        register();

                        break;
                    case R.id.phone_forget:
                        forget();

                        break;
                    case R.id.phone_login:
                        login();
                        break;
                }
            }
        });
    }

    private void forget() {
        bindPhoneView.setType(1);
        bindPhoneView.setVisibility(View.VISIBLE);
    }

    private void register() {

        bindPhoneView.setType(0);
        bindPhoneView.setVisibility(View.VISIBLE);
    }

    private void close() {
        finish();
    }

    public void login() {
        phonePhone.setText("18666159484");
        phonePwd.setText("123456");
        final String name = phonePhone.getText().toString();
        final String pwd = phonePwd.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("userName", name);
        map.put("password", pwd);

        ControlUtils.getsEveryTime(HConstants.URL.LOGIN, map, UserBean.class, new ControlUtils.OnControlUtils<UserBean>() {
            @Override
            public void onSuccess(String url, UserBean userBean, String result) {
                L(result);
                T("登录成功");
                if (userBean.getIsCheck() == 0) {
                    DButils.put(HConstants.KEY.userCode, userBean.getUserCode()+"");

                    //发一个消息给HomeFragment 替换 名字
                    onSendMessage(new MessageEvent(HConstants.EVENT.HOMEREFRESH, userBean));
                    //发一个消息关闭上一个Activity
                    onSendMessage(new MessageEvent(HConstants.EVENT.LOGINACTIVITY_CLOSE, ""));
                    //关闭自己
                    finish();

                } else {
                    T("登录失败");
                }


            }

            @Override
            public void onFailure(String url) {
                T("网络异常");
            }
        });

//        ThreadPoolUtils.runTaskInThread(new Runnable() {
//            @Override
//            public void run() {
//                EMClient.getInstance().login(name, pwd, new EMCallBack() {//回调
//                    @Override
//                    public void onSuccess() {
//                        EMClient.getInstance().groupManager().loadAllGroups();
//                        EMClient.getInstance().chatManager().loadAllConversations();
//                        Log.d("Helen", "登录聊天服务器成功！");
//                        //发一个消息给HomeFragment 替换 名字
//                        onSendMessage(new MessageEvent(HConstants.EVENT.HOMEREFRESH, name));
//                        //发一个消息关闭上一个Activity
//                        onSendMessage(new MessageEvent(HConstants.EVENT.LOGINACTIVITY_CLOSE, ""));
//                        //关闭自己
//                        finish();
//                    }
//
//                    @Override
//                    public void onProgress(int progress, String status) {
//
//                    }
//
//                    @Override
//                    public void onError(int code, String message) {
//                        Log.d("Helen", "登录聊天服务器失败！");
//                    }
//                });
//            }
//        });
    }


    private void pwd() {
        isHidePwd = !isHidePwd;
        if (isHidePwd) {
            phonePwdV.setImageResource(R.mipmap.ic_eye_white);
            phonePwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            phonePwdV.setImageResource(R.mipmap.ic_eye_white_error);
            phonePwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }
}
