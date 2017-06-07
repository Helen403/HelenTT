package com.example.snoy.helen;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import HConstants.HConstants;
import Utils.ControlUtils;
import activity.PhotoActivity;
import activity.SoundActivity;
import base.HBaseFragment;
import bean.ResultBean;
import view.PullAskView;

/**
 * Created by Helen on 2017/5/23.
 */
public class AddFragment extends HBaseFragment {

    ImageView addPhoto;
    TextView tvSound;
    TextView remind;
    TextView project;
    TextView certain;
    TextView time;
    EditText name;
    TextView auto;

    PullAskView pullAskView;


    @Override
    public int getContentView() {
        return R.layout.fragment_add;
    }

    @Override
    public void findViews() {
        addPhoto = (ImageView) contentView.findViewById(R.id.add_phone);
        tvSound = (TextView) contentView.findViewById(R.id.add_sound);
        remind = (TextView) contentView.findViewById(R.id.add_remind);
        project = (TextView) contentView.findViewById(R.id.add_project);
        certain = (TextView) contentView.findViewById(R.id.add_certain);
        time = (TextView) contentView.findViewById(R.id.add_time);
        name = (EditText) contentView.findViewById(R.id.add_name);
        auto = (TextView) contentView.findViewById(R.id.add_auto);
        pullAskView = new PullAskView(getActivity());
    }

    @Override
    public void initData() {

    }

    @Override
    public void setListeners() {
        setOnListeners(addPhoto, tvSound, remind, project, certain, time, auto);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.add_phone:
                        addPhoto();
                        break;
                    case R.id.add_sound:
                        addSound();
                        break;
                    case R.id.add_remind:
                        remind(v);
                        break;
                    case R.id.add_project:
                        project();
                        break;
                    case R.id.add_certain:
                        certain();
                        break;
                    case R.id.add_time:
                        timePick();
                        break;
                    case R.id.add_auto:
                        auto();
                        break;

                }
            }
        });
    }

    private void auto() {
        L("发送");
        int i = 1 / 0;
    }


    private void certain() {
        if (TextUtils.isEmpty(name.getText().toString())) {
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("userCode", "1");
        map.put("remindName", name.getText().toString());
        map.put("remindTime", time.getText().toString());
        map.put("remindMode", "1");
        map.put("remindType", "1");
        map.put("repeatType", "1");
        map.put("remindUnit", "1");
        map.put("adRemindTime", "1");
        map.put("allSharePerson", "");

        ControlUtils.getsEveryTime(HConstants.URL.saveRemind, map, ResultBean.class, new ControlUtils.OnControlUtils<ResultBean>() {
            @Override
            public void onSuccess(String url, ResultBean resultBean, String result) {
                if (resultBean.result.equals("1")) {
                    T("设置成功");
                } else {
                    T("设置失败");
                }
            }

            @Override
            public void onFailure(String url) {
                T("网络异常");
            }
        });


    }

    private void project() {

    }

    private void remind(View v) {
        showPopupWindow(v, pullAskView);
    }

    private void addSound() {
        goToActivityByClass(SoundActivity.class);
    }

    private void addPhoto() {
        goToActivityByClass(PhotoActivity.class);
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
        PopupWindow popupWindow = new PopupWindow(showView, SizeUtils.dp2px(320), SizeUtils.dp2px(480));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        //(screenW - SizeUtils.dp2px(320)) / 2 (screenH - SizeUtils.dp2px(480) / 2)
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(parent, 0, 0);
        return popupWindow;
    }


    public void timePick() {
        DatePickDialog dialog = new DatePickDialog(getActivity());
        //设置上下年分限制
        dialog.setYearLimt(5);
        //设置标题
        dialog.setTitle("选择时间");
        //设置类型
        dialog.setType(DateType.TYPE_ALL);
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat("yyyy-MM-dd HH:mm");
        //设置选择回调
        dialog.setOnChangeLisener(null);
        //设置点击确定按钮回调
        dialog.setOnSureLisener(new OnSureLisener() {
            @Override
            public void onSure(Date date) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String str = sdf.format(date);
                time.setText(str);
            }
        });
        dialog.show();

    }

}
