package adapter;

import android.content.Context;

import com.example.snoy.helen.R;

import base.HBaseAdapter;
import bean.ComBean;

/**
 * Created by Helen on 2017/5/23.
 */
public class AdapterCallMe  extends HBaseAdapter<ComBean.FriendListBean>{
    public AdapterCallMe(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.item_call_me;
    }

    @Override
    public void convert(ViewHolder holder, ComBean.FriendListBean friendListBean, int position) {

    }
}
