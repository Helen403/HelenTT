package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.example.snoy.helen.MainActivity;
import com.example.snoy.helen.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import HConstants.HConstants;
import Utils.ControlUtils;
import Utils.DButils;
import Utils.HImgeLoad;
import activity.LoginActivity;
import activity.PersonActivity;
import base.HBaseView;
import bean.HistoryTodayBean;
import bean.WeatherBean;
import de.hdodenhof.circleimageview.CircleImageView;
import me.iwf.photopicker.PhotoPicker;

/**
 * Created by Helen on 2017/6/3.
 */
public class HomeHView extends HBaseView {


    private RelativeLayout homeRlH1;
    private TextView homeHCal;
    private TextView homeHWeek;
    private TextView homeHAdd;
    private TextView homeTem;
    private ImageView homeHSun;
    private ImageView homeBgImg;
    private RelativeLayout homeRlH2;
    private TextView homeHistoryHTitle;
    private TextView homeHistoryHContent;
    private TextView homeHistoryHRemind;
    private TextView homeHistoryHRemindR;
    private CircleImageView homeHIcon;
    private LinearLayout homeLl1;

    public HomeHView(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_home_h;
    }

    @Override
    public void findViews() {
        homeRlH1 = (RelativeLayout) findViewById(R.id.home_rl_h_1);
        homeHCal = (TextView) findViewById(R.id.home_h_cal);
        homeHWeek = (TextView) findViewById(R.id.home_h_week);
        homeHAdd = (TextView) findViewById(R.id.home_h_add);
        homeTem = (TextView) findViewById(R.id.home_tem);
        homeHSun = (ImageView) findViewById(R.id.home_h_sun);
        homeBgImg = (ImageView) findViewById(R.id.home_bg_img);
        homeRlH2 = (RelativeLayout) findViewById(R.id.home_rl_h_2);
        homeHistoryHTitle = (TextView) findViewById(R.id.home_history_h_title);
        homeHistoryHContent = (TextView) findViewById(R.id.home_history_h_content);
        homeHistoryHRemind = (TextView) findViewById(R.id.home_history_h_remind);
        homeHistoryHRemindR = (TextView) findViewById(R.id.home_history_h_remind_r);
        homeHIcon = (CircleImageView) findViewById(R.id.home_h_icon);
        homeLl1 = (LinearLayout)findViewById(R.id.home_ll_1);
    }

    @Override
    public void initData() {
        initTime();
        initHead();
        initHistory();
    }

    @Override
    public void setListeners() {
        homeHIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IconClick();
            }
        });
        homeRlH1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBg();
            }
        });

    }

    private void changeBg() {
        MainActivity activity = (MainActivity) context;
        PhotoPicker.builder()
                .setPhotoCount(1)
                .start(activity);
    }

    private void IconClick() {
        String LoginStatus = DButils.get(HConstants.KEY.loginStatus);
        if (LoginStatus.equals("1")) {
            goToActivityByClass(PersonActivity.class);
        } else {
            goToActivityByClass(LoginActivity.class);
        }
    }


    private void initHistory() {
        ControlUtils.getsEveryTime(HConstants.URL.historyToday, null, HistoryTodayBean.class, new ControlUtils.OnControlUtils<HistoryTodayBean>() {
            @Override
            public void onSuccess(String url, HistoryTodayBean historyTodayBean, String result) {
                if (historyTodayBean != null) {
                    List<HistoryTodayBean.ResultBean> data = historyTodayBean.getResult();
                    Random random = new Random();
                    int index = random.nextInt(data.size());
                    HistoryTodayBean.ResultBean bean = data.get(index);
                    String historyContent = bean.getTitle();
                    homeHistoryHContent.setText(historyContent);
                }
                L("历史上今天获取成功");
            }

            @Override
            public void onFailure(String url) {
                L("历史上今天获取失败");
            }
        });
    }

    public void initHead() {
        setHeadIcon();
    }

    private void initTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String year = sdf.format(System.currentTimeMillis());
        homeHCal.setText(year);
        String week = TimeUtils.getChineseWeek(System.currentTimeMillis());
        homeHWeek.setText(week);

    }


    public void setWeather(WeatherBean weatherBean) {
        if (weatherBean == null) return;
        homeTem.setText(weatherBean.getCurTemperature() + "℃");
        homeHAdd.setText(weatherBean.getCity());
    }

    public void setHeadIcon() {

        String path = DButils.get(HConstants.KEY.figureurl);
        L("执行" + path);

        if (!TextUtils.isEmpty(path)) {
            L("主页头像");
            HImgeLoad.setImageByUrl(path, homeHIcon);
        } else {
            homeHIcon.setImageResource(R.mipmap.quila);
        }
    }

    public void setBgImg() {
        String bgImg = DButils.get(HConstants.KEY.bgImg);
        if (!TextUtils.isEmpty(bgImg)) {
            Bitmap bitmap = BitmapFactory.decodeFile(bgImg);
            homeBgImg.setImageBitmap(bitmap);
        } else {
            homeBgImg.setImageResource(R.color.homebg);
        }

    }

}