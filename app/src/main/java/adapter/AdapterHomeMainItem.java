package adapter;

import android.content.Context;

import com.example.snoy.helen.R;

import base.HBaseAdapter;
import bean.HomeMainItemBean;

/**
 * Created by Helen on 2017/5/25.
 */
public class AdapterHomeMainItem extends HBaseAdapter<HomeMainItemBean> {
    public AdapterHomeMainItem(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.item_home_main_item;
    }

    @Override
    public void convert(ViewHolder holder, HomeMainItemBean homeMainItemBean, int position) {

    }
}
