package activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.example.snoy.helen.R;

import java.util.HashMap;

import HConstants.HConstants;
import Utils.ControlUtils;
import Utils.DButils;
import base.HBaseActivity;
import bean.MessageEvent;
import bean.ResultBean;
import bean.UserBean;

/**
 * Created by Helen on 2017/6/3.
 */
public class RegisterActivity extends HBaseActivity {


    private ImageView registerPhoneIcon1;
    private EditText registerPhone;
    private ImageView registerPhoneIcon2;
    private EditText registerVim;
    private TextView registerCount;
    private ImageView registerPhoneIcon3;
    private EditText registerName;
    private ImageView registerPhoneIcon4;
    private EditText registerPwd;
    private TextView registerRegister;
    private ImageView registerClose;


    String pwd;
    String phone;

    @Override
    public int getContentView() {
        return R.layout.activity_register;
    }

    @Override
    public void findViews() {
        registerPhoneIcon1 = (ImageView) contentView.findViewById(R.id.register_phone_icon_1);
        registerPhone = (EditText) contentView.findViewById(R.id.register_phone);
        registerPhoneIcon2 = (ImageView) contentView.findViewById(R.id.register_phone_icon_2);
        registerVim = (EditText) contentView.findViewById(R.id.register_vim);
        registerCount = (TextView) contentView.findViewById(R.id.register_count);
        registerPhoneIcon3 = (ImageView) contentView.findViewById(R.id.register_phone_icon_3);
        registerName = (EditText) contentView.findViewById(R.id.register_name);
        registerPhoneIcon4 = (ImageView) contentView.findViewById(R.id.register_phone_icon_4);
        registerPwd = (EditText) contentView.findViewById(R.id.register_pwd);
        registerRegister = (TextView) contentView.findViewById(R.id.register_register);
        registerClose = (ImageView) contentView.findViewById(R.id.register_close);
    }

    @Override
    public void initData() {

    }

    @Override
    public void setListeners() {

        setOnListeners(registerRegister, registerClose);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.register_register:
                        register();
                        break;
                    case R.id.register_close:
                        close();
                        break;
                }
            }
        });

    }

    private void close() {
        finish();
    }

    private void register() {
        final String name = registerName.getText().toString();
        phone = registerPhone.getText().toString();
        pwd = registerPwd.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            T("手机号不能为空");
            return;
        }

        if (TextUtils.isEmpty(name)) {
            T("昵称不能为空");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            T("密码不能为空");
            return;
        }

        //设置为不能点击状态
        registerRegister.setBackgroundResource(R.drawable.shape_bg_register_press);
        registerRegister.setEnabled(false);

        HashMap<String, String> map = new HashMap<>();
        map.put("userTelphone", phone);
        map.put("userPassword", pwd);
        map.put("phoneDeviceCode", PhoneUtils.getIMEI());
        map.put("phoneDeviceName", DeviceUtils.getManufacturer() + " " + DeviceUtils.getModel());
        map.put("isThreeLogin", "0");
        map.put("userNickName", name);

        ControlUtils.getsEveryTime(HConstants.URL.Register, map, ResultBean.class, new ControlUtils.OnControlUtils<ResultBean>() {
            @Override
            public void onSuccess(String url, ResultBean resultBean, String result) {
                registerRegister.setBackgroundResource(R.drawable.shape_bg_register_nomal);
                registerRegister.setEnabled(true);
                L(result);
                if (resultBean.result.equals("1")) {
                    T("注册成功");
                    DButils.put(HConstants.KEY.userCode, resultBean.userCode);
                    login();

                } else {
                    T("注册失败");
                }
            }

            @Override
            public void onFailure(String url) {
                registerRegister.setBackgroundResource(R.drawable.shape_bg_register_nomal);
                registerRegister.setEnabled(true);
            }
        });
    }

    private void login() {
        HashMap<String, String> map = new HashMap<>();
        map.put("userName", phone);
        map.put("password", pwd);

        ControlUtils.getsEveryTime(HConstants.URL.LOGIN, map, UserBean.class, new ControlUtils.OnControlUtils<UserBean>() {

            @Override
            public void onSuccess(String url, UserBean userBean, String result) {
                L(result);
                T("登录成功");
                if (userBean.getIsCheck() == 1) {
                    String nickName = userBean.getUserNickName();
                    String userCode = userBean.getUserCode()+"";
                    String useHead = userBean.getUserHead();
                    String phone = userBean.getUserTelphone();
                    String email = userBean.getUserEmail();

                    DButils.put(HConstants.KEY.userCode, userCode);
                    DButils.put(HConstants.KEY.nickName, nickName);
                    if (!TextUtils.isEmpty(useHead)){
                        DButils.put(HConstants.KEY.figureurl, useHead);
                    }else {
                        DButils.put(HConstants.KEY.figureurl, "");
                    }

                    DButils.put(HConstants.KEY.loginStatus,"1");
                    DButils.put(HConstants.KEY.phone,phone);
                    DButils.put(HConstants.KEY.Email,email);

                    //发一个消息给HomeFragment 替换 名字
                    onSendMessage(new MessageEvent(HConstants.EVENT.HOMEREFRESH, null));
                    onSendMessage(new MessageEvent(HConstants.EVENT.NAME_REFRESH, null));
                    //发一个消息关闭上一个Activity
                    onSendMessage(new MessageEvent(HConstants.EVENT.LOGINACTIVITY_CLOSE, ""));

                    finish();
                } else {
                    T("登录失败");
                    finish();
                }

            }

            @Override
            public void onFailure(String url) {
                T("网络异常");
                finish();
            }
        });

    }


}
