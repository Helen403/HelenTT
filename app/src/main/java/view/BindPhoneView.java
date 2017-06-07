package view;

import android.content.Context;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.example.snoy.helen.R;

import java.util.HashMap;

import HConstants.HConstants;
import Utils.ControlUtils;
import Utils.CountDownTimerUtils;
import Utils.EditTextClearTools;
import activity.PhoneActivity;
import base.HBaseView;
import bean.RegisterBean;
import bean.ResultBean;

/**
 * Created by SNOY on 2017/5/11.
 */
public class BindPhoneView extends HBaseView {

    private EditText registerPhone;
    private ImageView registerPhoneV;
    private EditText registerVer;
    public TextView registerCount;
    private EditText registerPwd;
    private ImageView registerPwdV;
    private TextView registerRegister;
    ImageView close;
    CountDownTimerUtils mCountDownTimerUtils;
    private boolean isHidePwd = true;// 输入框密码是否是隐藏的，默认为true
    TextView registerTitle;
    //0为注册  1为忘记
    int type;

    public BindPhoneView(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.view_register;
    }

    @Override
    public void findViews() {
        registerPhone = (EditText) view.findViewById(R.id.register_phone);
        registerPhoneV = (ImageView) view.findViewById(R.id.register_phone_v);
        registerVer = (EditText) view.findViewById(R.id.register_ver);
        registerCount = (TextView) view.findViewById(R.id.register_count);
        registerPwd = (EditText) view.findViewById(R.id.register_pwd);
        registerPwdV = (ImageView) view.findViewById(R.id.register_pwd_v);
        close = (ImageView) view.findViewById(R.id.register_close);
        registerRegister = (TextView) view.findViewById(R.id.register_register);
        registerTitle = (TextView) view.findViewById(R.id.register_title);
    }

    @Override
    public void initData() {
        //添加清楚监听器
        EditTextClearTools.addclerListener(registerPhone, registerPhoneV);
    }

    @Override
    public void setListeners() {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerPhone.getText().length() > 0 || registerPwd.getText().length() > 0) {
                    return;
                }
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    PhoneActivity activity = (PhoneActivity) context;
                    imm.hideSoftInputFromWindow(activity.getWindow().getDecorView()
                            .getWindowToken(), 0);
                }

                mCountDownTimerUtils.cancel();
                registerCount.setText("获取验证码");
                registerCount.setClickable(true);//重新获得点击
                view.setVisibility(View.GONE);
            }
        });
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    PhoneActivity activity = (PhoneActivity) context;
                    imm.hideSoftInputFromWindow(activity.getWindow().getDecorView()
                            .getWindowToken(), 0);
                }

                mCountDownTimerUtils.cancel();
                registerCount.setText("获取验证码");
                registerCount.setClickable(true);//重新获得点击
                view.setVisibility(View.GONE);
            }
        });

        mCountDownTimerUtils = new CountDownTimerUtils(registerCount, 60000, 1000);

        registerCount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (registerPhone.getText().length() == 0) {
                    return;
                }
                mCountDownTimerUtils.start();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("userName", registerPhone.getText().toString());
                ControlUtils.getsEveryTime(HConstants.URL.VER, map, RegisterBean.class, new ControlUtils.OnControlUtils<RegisterBean>() {
                    @Override
                    public void onSuccess(String url, RegisterBean registerBean, String result) {
                        L(result);
                        if (registerBean.result.equals("0")) {
                            T("正常");
                        } else {
                            T("用户已存在");
                        }
                    }

                    @Override
                    public void onFailure(String url) {

                    }
                });


            }
        });
        registerPwdV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pwd();
            }
        });

        //注册
        registerRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0) {
                    register();
                }
            }
        });

    }

    private void register() {
        final String name = registerPhone.getText().toString();
        final String pwd = registerPwd.getText().toString();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            return;
        }

        HashMap<String,String> map = new HashMap<>();
        map.put("userTelphone",registerPhone.getText().toString());
        map.put("userPassword",registerPwd.getText().toString());
        map.put("phoneDeviceCode", PhoneUtils.getIMEI());
        map.put("phoneDeviceName", DeviceUtils.getManufacturer()+" "+DeviceUtils.getModel());
        map.put("isThreeLogin", "0");

        ControlUtils.getsEveryTime(HConstants.URL.Register, map, ResultBean.class, new ControlUtils.OnControlUtils<ResultBean>() {
            @Override
            public void onSuccess(String url, ResultBean resultBean, String result) {
                L(result);
                if (resultBean.result.equals("0")) {
                    T("注册成功");
                    view.setVisibility(View.GONE);
                } else {
                    T("注册失败");
                }
            }

            @Override
            public void onFailure(String url) {

            }
        });


//        ThreadPoolUtils.runTaskInThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    EMClient.getInstance().createAccount(name, pwd);//同步方法
//                } catch (HyphenateException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }


    public void setType(int type) {
        this.type = type;
        switch (type) {
            case 0:
                registerTitle.setText("注册");
                registerRegister.setText("注册");
                break;
            case 1:
                registerTitle.setText("忘记密码");
                registerRegister.setText("修改");
                break;
        }

    }

    private void pwd() {
        isHidePwd = !isHidePwd;
        if (isHidePwd) {
            registerPwdV.setImageResource(R.mipmap.ic_eye_white);
            registerPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            registerPwdV.setImageResource(R.mipmap.ic_eye_white_error);
            registerPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }
}
