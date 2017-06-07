package activity;

import android.os.Handler;
import android.text.TextUtils;
import android.widget.ListView;

import com.example.snoy.helen.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import adapter.AdapterFriend;
import base.HBaseActivity;

/**
 * Created by Helen on 2017/5/23.
 */
public class FriendActivity extends HBaseActivity {

    ListView lv;
    AdapterFriend adapterFriend;
    Handler handler = new Handler();

    @Override
    public int getContentView() {
        return R.layout.fragment_friend;
    }

    @Override
    public void findViews() {
       // lv = (ListView) contentView.findViewById(R.id.friend_listview);
    }

    @Override
    public void initData() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("userCode", "4");
//        ControlUtils.getsEveryTime(HConstants.URL.findFriendList, map, ComBean.class, new ControlUtils.OnControlUtils<ComBean>() {
//            @Override
//            public void onSuccess(String url, ComBean comBean, String result) {
//
//                final ArrayList<ComBean> data =  getListFromJson(result, ComBean.class);
//              ComBean comBean1 = data.get(0);
//                final ArrayList<ComBean.FriendListBean> dataTmp = (ArrayList<ComBean.FriendListBean>) comBean1.getFriendList();
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapterFriend = new AdapterFriend(FriendActivity.this);
//                        lv.setAdapter(adapterFriend);
//                        adapterFriend.setData(dataTmp);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailure(String url) {
//
//            }
//        });


    }

    @Override
    public void setListeners() {

    }


    Gson gson = new Gson();
    /**
     * json转成ArrayList
     */
    public  ArrayList<bean.ComBean> getListFromJson(String gsonString, Class<bean.ComBean> tClass) {
        ArrayList<bean.ComBean> list;
        try {
            if (gson != null && !TextUtils.isEmpty(gsonString)) {
                //过滤gson
                gsonString = gsonString.trim();
                if (gsonString.startsWith("ufeff")) {
                    gsonString = gsonString.substring(1);
                }
                TypeToken<ArrayList<bean.ComBean>> tt = new TypeToken<ArrayList<bean.ComBean>>() {
                };
                list = gson.fromJson(gsonString, tt.getType());
                return list;
            }
        } catch (Exception e) {
        }
        return null;
    }

}
