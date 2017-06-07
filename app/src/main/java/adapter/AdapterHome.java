package adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.snoy.helen.R;

import base.HBaseAdapter;
import bean.HomePersonBean;

/**
 * Created by Helen on 2017/5/23.
 */
public class AdapterHome extends HBaseAdapter<HomePersonBean> {
    public AdapterHome(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.item_home_person;
    }

    @Override
    public void convert(ViewHolder holder, HomePersonBean homePersonBean, int position) {
        ImageView img = holder.findViewById(R.id.home_img);
        TextView tv = holder.findViewById(R.id.home_title);
        img.setImageResource(homePersonBean.img);
        tv.setText(homePersonBean.title);
    }
}
