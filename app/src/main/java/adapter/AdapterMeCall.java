package adapter;

import android.content.Context;

import com.example.snoy.helen.R;

import base.HBaseAdapter;
import bean.ComBean;

/**
 * Created by Helen on 2017/5/23.
 */
public class AdapterMeCall extends HBaseAdapter<ComBean.FriendListBean> {
    public AdapterMeCall(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.item_me_call;
    }

    @Override
    public void convert(ViewHolder holder, ComBean.FriendListBean friendListBean, int position) {

    }
}
