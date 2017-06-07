package activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.snoy.helen.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import HConstants.HConstants;
import Utils.ControlUtils;
import Utils.DButils;
import Utils.HImgeLoad;
import Utils.HTimeUtils;
import base.HBaseActivity;
import bean.FilePath;
import bean.MessageEvent;
import bean.ResultBean;
import de.hdodenhof.circleimageview.CircleImageView;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;

/**
 * Created by Helen on 2017/5/25.
 */
public class PersonActivity extends HBaseActivity {

    private RelativeLayout personRl1;
    private CircleImageView personIcon;
    private RelativeLayout personRl2;
    private TextView personName;
    private RelativeLayout personRl3;
    private TextView personAccount;
    private RelativeLayout personRl4;
    private TextView personPhone;
    private RelativeLayout personRl5;
    private TextView personEmail;
    private RelativeLayout personRl6;
    private TextView personQq;
    private RelativeLayout personRl7;
    private TextView personWeixin;
    private TextView personExit;
    private ImageView personClose;


    @Override
    public int getContentView() {
        return R.layout.activity_person;
    }

    @Override
    public void findViews() {
        personRl1 = (RelativeLayout) contentView.findViewById(R.id.person_rl_1);
        personIcon = (CircleImageView) contentView.findViewById(R.id.person_icon);
        personRl2 = (RelativeLayout) contentView.findViewById(R.id.person_rl_2);
        personName = (TextView) contentView.findViewById(R.id.person_name);
        personRl3 = (RelativeLayout) contentView.findViewById(R.id.person_rl_3);
        personAccount = (TextView) contentView.findViewById(R.id.person_account);
        personRl4 = (RelativeLayout) contentView.findViewById(R.id.person_rl_4);
        personPhone = (TextView) contentView.findViewById(R.id.person_phone);
        personRl5 = (RelativeLayout) contentView.findViewById(R.id.person_rl_5);
        personEmail = (TextView) contentView.findViewById(R.id.person_email);
        personRl6 = (RelativeLayout) contentView.findViewById(R.id.person_rl_6);
        personQq = (TextView) contentView.findViewById(R.id.person_qq);
        personRl7 = (RelativeLayout) contentView.findViewById(R.id.person_rl_7);
        personWeixin = (TextView) contentView.findViewById(R.id.person_weixin);
        personExit = (TextView) contentView.findViewById(R.id.person_exit);
        personClose = (ImageView) contentView.findViewById(R.id.person_close);
    }

    @Override
    public void initData() {
        initInfo();
    }


    @Override
    public void setListeners() {
        setOnListeners(personExit, personRl1, personClose, personRl2, personRl4);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.person_exit:
                        exit();
                        break;
                    case R.id.person_rl_1:
                        rll();
                        break;
                    case R.id.person_close:
                        close();
                        break;
                    case R.id.person_rl_2:
                        modify();
                        break;
                    case R.id.person_rl_4:
                        bindPhone();
                        break;
                }
            }
        });
    }

    private void bindPhone() {
        goToActivityByClass(BindPhoneActivity.class);
    }

    private void modify() {
        goToActivityByClass(ModifyActivity.class);
    }

    private void close() {
        finish();
    }

    private void rll() {
        String loginStatus = DButils.get(HConstants.KEY.loginStatus);
        if (loginStatus.equals("1")) {
            PhotoPicker.builder()
                    .setPhotoCount(1)
                    .start(PersonActivity.this);
        } else {
            goToActivityByClass(LoginActivity.class);
        }
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
                        DButils.put(HConstants.KEY.signName, "");


                        onSendMessage(new MessageEvent(HConstants.EVENT.HOMEREFRESH, null));
                        onSendMessage(new MessageEvent(HConstants.EVENT.NAME_REFRESH, null));
                        finish();
                    } else {
                        T("退出失败");
                        DButils.put(HConstants.KEY.loginStatus, "0");
                    }
                }

                @Override
                public void onFailure(String url) {
                    T("退出网络失败");
                    DButils.put(HConstants.KEY.loginStatus, "0");
                }
            });
        } else {
            T("请登录");
        }

    }


    private void initInfo() {

        //QQ登录就为1 手机登录2 微信为3
        String headUrl = DButils.get(HConstants.KEY.figureurl);
        L("个人资料" + headUrl);
        if (!TextUtils.isEmpty(headUrl)) {
            HImgeLoad.setImageByUrl(headUrl, personIcon);
        } else {
            personIcon.setImageResource(R.mipmap.quila);
        }

        String name = DButils.get(HConstants.KEY.nickName);
        personName.setText(name);
        String accout = DButils.get(HConstants.KEY.userID);
        personAccount.setText(accout);

        String phone = DButils.get(HConstants.KEY.phone);
        if (TextUtils.isEmpty(phone)) {
            personPhone.setText("未绑定");
        } else {
            personPhone.setText(phone);
        }

        String email = DButils.get(HConstants.KEY.Email);
        if (TextUtils.isEmpty(email)) {
            personEmail.setText("未绑定");
        } else {
            personEmail.setText(email);
        }

        String type = DButils.get(HConstants.KEY.LoginType);
        if (type.equals("1")) {
            String nameQQ = DButils.get(HConstants.KEY.nickName);
            if (TextUtils.isEmpty(nameQQ)) {
                personQq.setText("未绑定");
            } else {
                personQq.setText(nameQQ);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            String path = photos.get(0);
            updataHead(path);


            Bitmap bitmap = BitmapFactory.decodeFile(path);
            personIcon.setImageBitmap(bitmap);
        }
    }


    public void updataHead(String path) {
        File file = new File(path);

        String userCode = DButils.get(HConstants.KEY.userCode);

        String name = userCode + "_" + HTimeUtils.getCurrentTime() + ".png";
        L(name);

        String url = HConstants.URL.uploadPicFile;

        OkHttpUtils.post()
                .addFile("mFile", name, file)
                .addParams("userCode", userCode)
                .addParams("isUserHead", "1")
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L("上传头像失败");

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        FilePath filePath = ControlUtils.getEntityFromJson(response, FilePath.class);
                        final String path = filePath.getFilePath();
                        L(path);
                        L("上传头像成功");
                        L(response);
                        DButils.put(HConstants.KEY.figureurl, path);


                        onSendMessage(new MessageEvent(HConstants.EVENT.HOMEREFRESH, null));
                    }
                });
    }


    @Override
    public void onReceiveMessage(@NonNull MessageEvent event) {
        super.onReceiveMessage(event);
        //从修改名字回来
        if (event.getType() == HConstants.EVENT.NAME_REFRESH) {
            String name = (String) event.getData();
            DButils.put(HConstants.KEY.nickName, name);
            personName.setText(name);
        }
    }
}
