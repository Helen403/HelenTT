package adapter;

import android.content.Context;

import com.example.snoy.helen.R;

import base.HBaseAdapter;
import bean.HomeClockBean;

/**
 * Created by SNOY on 2017/5/12.
 */
public class AdapterHomeClock extends HBaseAdapter<HomeClockBean> {



    public AdapterHomeClock(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.item_home_history;
    }

    @Override
    public void convert(ViewHolder holder, HomeClockBean homeClockBean, int position) {
//        ImageView  homeClock = (ImageView)holder.findViewById(R.id.home_clock);
//        TextView homeDay = (TextView)holder.findViewById(R.id.home_day);
//        TextView  homeTime = (TextView)holder.findViewById(R.id.home_time);
//        TextView homeClick = (TextView)holder.findViewById(R.id.home_click);
    }
}
