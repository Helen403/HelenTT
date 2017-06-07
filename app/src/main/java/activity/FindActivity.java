package activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.KeyboardUtils;
import com.example.snoy.helen.R;

import java.util.ArrayList;
import java.util.HashMap;

import HConstants.HConstants;
import Utils.ControlUtils;
import adapter.AdapterFriend;
import base.HBaseActivity;
import bean.ComBean;

/**
 * Created by Helen on 2017/5/23.
 */
public class FindActivity extends HBaseActivity {


    AdapterFriend adapterFriend;

    private RelativeLayout findRl3;
    private ImageView findSeachIcon;
    private ImageView findSeach;
    private ListView findListview;
    private EditText editText;


    @Override
    public int getContentView() {
        return R.layout.activity_find;
    }

    @Override
    public void findViews() {
        findRl3 = (RelativeLayout) contentView.findViewById(R.id.find_rl_3);
        findSeachIcon = (ImageView) contentView.findViewById(R.id.find_seach_icon);
        findListview = (ListView) contentView.findViewById(R.id.find_listview);
        editText = (EditText) contentView.findViewById(R.id.find_edit);
        findSeach = (ImageView) contentView.findViewById(R.id.find_seach);
        adapterFriend = new AdapterFriend(this);
        findListview.setAdapter(adapterFriend);
    }

    @Override
    public void initData() {
        KeyboardUtils.showSoftInput(editText);

    }

    @Override
    public void setListeners() {
        setOnListeners(findSeach);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.find_seach:
                        seach();
                        break;
                }
            }
        });
    }

    private void seach() {
        String str = editText.getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("userName", str);

        ControlUtils.getsEveryTime(HConstants.URL.findAddFriend, map, ComBean.FriendListBean.UserBean.class, new ControlUtils.OnControlUtils<ComBean.FriendListBean.UserBean>() {
            @Override
            public void onSuccess(String url, ComBean.FriendListBean.UserBean userBean, String result) {
                L(result);
                T("查询成功");
                ArrayList<ComBean.FriendListBean> date = new ArrayList<ComBean.FriendListBean>();
                ComBean.FriendListBean bean = new ComBean.FriendListBean();
                bean.setUser(userBean);
                if (userBean != null) {
                    userBean.setIsShow("0");
                    userBean.setPinyinF("a");
                    date.add(bean);
                }
                adapterFriend.setData(date);
            }

            @Override
            public void onFailure(String url) {
                T("网络失败");
            }
        });

    }


}
