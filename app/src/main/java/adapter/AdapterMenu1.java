package adapter;

import android.content.Context;
import android.widget.TextView;

import com.example.snoy.helen.R;

import base.HBaseAdapter;

/**
 * Created by SNOY on 2017/5/10.
 */
public class AdapterMenu1 extends HBaseAdapter<String> {

    public AdapterMenu1(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.item_my_menu;
    }

    @Override
    public void convert(ViewHolder holder, String s, int position) {
        TextView tv = holder.findViewById(R.id.tv_1);
        tv.setText(s);
    }
}
