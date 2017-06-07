package view;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.example.snoy.helen.MainActivity;
import com.example.snoy.helen.R;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;

import activity.PersonActivity;
import activity.TwoDimActivity;
import adapter.AdapterHome;
import base.HBaseView;
import bean.HomePersonBean;
import custemview.ScrollDisabledListView;

/**
 * Created by Helen on 2017/5/23.
 */
public class HomePersonView extends HBaseView {

    ScrollDisabledListView lv;
    AdapterHome adapterHome;
    ArrayList<HomePersonBean> dataHome;


    public HomePersonView(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.view_person;
    }

    @Override
    public void findViews() {
        lv = (ScrollDisabledListView) view.findViewById(R.id.person_listview);
    }

    @Override
    public void initData() {
        adapterHome = new AdapterHome(context);
        lv.setAdapter(adapterHome);
        dataHome = new ArrayList<>();
        dataHome.add(new HomePersonBean(R.drawable.ic_setting_user_normal, "个人中心"));
        dataHome.add(new HomePersonBean(R.drawable.ic_setting_add_friend_normal, "添加好友"));
        dataHome.add(new HomePersonBean(R.drawable.ic_menu_time_pressed, "时刻"));
        dataHome.add(new HomePersonBean(R.drawable.ic_setting_history_normal, "历史记录"));
        dataHome.add(new HomePersonBean(R.drawable.ic_setting_strike_normal, "正点报时"));
        dataHome.add(new HomePersonBean(R.drawable.ic_setting_collection_normal, "添加新订阅"));
        dataHome.add(new HomePersonBean(R.drawable.ic_setting_sms_import_normal, "扫描短信"));
        dataHome.add(new HomePersonBean(R.drawable.ic_setting_more_normal, "更多设置"));

        adapterHome.setData(dataHome);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    goToActivityByClass(PersonActivity.class);
                }
                if (position == 1) {
                    customScan();
                }


            }
        });

    }

    @Override
    public void setListeners() {

    }

    public void customScan(){
        MainActivity activity = (MainActivity) context;
        new IntentIntegrator(activity)
                .setOrientationLocked(false)
                .setCaptureActivity(TwoDimActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); // 初始化扫描
    }


}
