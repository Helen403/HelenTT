package adapter;

import android.content.Context;

import com.example.snoy.helen.R;

import base.HBaseAdapter;
import bean.CompleteBean;

/**
 * Created by Helen on 2017/5/15.
 */
public class AdapterComplete extends HBaseAdapter<CompleteBean> {
    public AdapterComplete(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.item_complete;
    }

    @Override
    public void convert(ViewHolder holder, CompleteBean completeBean, int position) {

    }
}
