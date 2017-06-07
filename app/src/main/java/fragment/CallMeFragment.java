package fragment;

import android.widget.ListView;

import com.blankj.utilcode.util.PinyinUtils;
import com.example.snoy.helen.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import adapter.AdapterFriend;
import base.HBaseFragment;
import bean.ComBean;

/**
 * Created by Helen on 2017/5/23.
 */
public class CallMeFragment extends HBaseFragment {


    ListView lv;
    AdapterFriend adapter;


    @Override
    public int getContentView() {
        return R.layout.fragment_call_me;
    }

    @Override
    public void findViews() {

        lv = (ListView) contentView.findViewById(R.id.call_me_listview);
        adapter = new AdapterFriend(getActivity());
        lv.setAdapter(adapter);

    }

    @Override
    public void initData() {

    }

    @Override
    public void setListeners() {

    }

    public void setFriend(ComBean comBean2) {
        if (comBean2 == null) return;
        if (comBean2.getFriendList() == null) return;
        if (adapter == null) return;
        List<ComBean.FriendListBean> data = matchFriend(comBean2.getFriendList());
        adapter.setData(data);
    }

    private List<ComBean.FriendListBean> matchFriend(List<ComBean.FriendListBean> data) {
        if (data.size() == 0) return null;
        PinyinComparator comparator = new PinyinComparator();
        Collections.sort(data, comparator);
        String tmp = PinyinUtils.getPinyinFirstLetter(data.get(0).getUser().getUserNickName());
        data.get(0).getUser().setIsShow("1");
        data.get(0).getUser().setPinyinF(tmp);
        for (int i = 1; i < data.size(); i++) {
            ComBean.FriendListBean.UserBean bean = data.get(i).getUser();
            String str = PinyinUtils.getPinyinFirstLetter(bean.getUserNickName());
            if (!str.equals(tmp)) {
                bean.setIsShow("1");
                bean.setPinyinF(str);
                tmp = str;
            } else {
                bean.setIsShow("0");
                bean.setPinyinF(str);
            }
        }

        return data;
    }


    class PinyinComparator implements Comparator<ComBean.FriendListBean> {

        /**
         * 比较两个字符串
         *    
         */
        public int compare(ComBean.FriendListBean o1, ComBean.FriendListBean o2) {
            String name1 = o1.getUser().getUserNickName();
            String name2 = o2.getUser().getUserNickName();
            String str1 = getPingYin(name1);
            String str2 = getPingYin(name2);
            int flag = str1.compareTo(str2);
            return flag;
        }

        /**
         *    * 将字符串中的中文转化为拼音,其他字符不变
         *    *
         *    * @param inputString
         *    * @return
         *    
         */
        public String getPingYin(String inputString) {
            String str = PinyinUtils.getPinyinFirstLetter(inputString);
            return str;
        }

    }


}
