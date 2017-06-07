package adapter;

import android.content.Context;
import android.widget.TextView;

import com.example.snoy.helen.R;

import base.HBaseAdapter;
import bean.HomeBean;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Helen on 2017/6/3.
 */
public class AdapterHHome extends HBaseAdapter<HomeBean> {

    private CircleImageView homeHHIcon;
    private TextView homeHHTime;
    private TextView homeHHContent;
    private TextView homeHHToday;
    private TextView homeHHWeek;


    public AdapterHHome(Context context) {
        super(context);
    }


    @Override
    public int getContentView() {
        return R.layout.item_home_h;
    }

    @Override
    public void convert(ViewHolder holder, HomeBean homeBean, int position) {
        homeHHIcon = (CircleImageView) holder.findViewById(R.id.home_h_h_icon);
        homeHHTime = (TextView) holder.findViewById(R.id.home_h_h_time);
        homeHHContent = (TextView) holder.findViewById(R.id.home_h_h_content);
        homeHHToday = (TextView) holder.findViewById(R.id.home_h_h_today);
        homeHHWeek = (TextView) holder.findViewById(R.id.home_h_h_week);
        String remindTime = homeBean.getRemindTime();
        String remindName = homeBean.getRemindName();

        String remindMode = homeBean.getRemindMode();
        String remindType = homeBean.getRemindType();
        String repeatType = homeBean.getRepeatType();
        homeHHTime.setText(remindTime);
        homeHHContent.setText(remindName);
       // homeHHToday.setText(remindMode);
        //homeHHWeek.setText(remindType);


    }
}
