package adapter;

import com.example.snoy.helen.R;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.adapter.abslistview.base.ItemViewDelegate;

import bean.HomeBean;

/**
 * Created by Helen on 2017/5/25.
 */
public class AdapterHomeRemind implements ItemViewDelegate<HomeBean> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_home_clock;
    }

    @Override
    public boolean isForViewType(HomeBean item, int position) {
        return true;
    }

    @Override
    public void convert(ViewHolder holder, HomeBean homeBean, int position) {

    }
}