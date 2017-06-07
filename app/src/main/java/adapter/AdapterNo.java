package adapter;

import android.content.Context;

import com.example.snoy.helen.R;

import base.HBaseAdapter;
import bean.NoBean;

/**
 * Created by Helen on 2017/5/15.
 */
public class AdapterNo extends HBaseAdapter<NoBean> {

    public AdapterNo(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.item_no;
    }

    @Override
    public void convert(ViewHolder holder, NoBean noBean, int position) {

    }
}
