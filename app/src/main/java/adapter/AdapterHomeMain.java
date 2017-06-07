package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.snoy.helen.R;

import java.util.ArrayList;

import bean.HistoryTodayBean;
import bean.HomeRemindBean;

/**
 * Created by Helen on 2017/5/25.
 */
public class AdapterHomeMain extends BaseAdapter {


    Context context;
    HistoryTodayBean historyTodayBean;
    ArrayList<HomeRemindBean> data;
    LayoutInflater inflate;


    public AdapterHomeMain(Context context, HistoryTodayBean historyTodayBean, ArrayList<HomeRemindBean> data) {
        this.context = context;
        this.historyTodayBean = historyTodayBean;
        this.data = data;
        inflate = LayoutInflater.from(context);
    }


    public void setHistoryTodayBean(HistoryTodayBean historyTodayBean) {
        this.historyTodayBean = historyTodayBean;

    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1 || position == 2) {
            return 1;
        } else {
            HomeRemindBean homeRemindBean = data.get(position - 1);
            if (!homeRemindBean.exp) {
                return 2;
            } else {
                return 3;
            }
        }
    }


    // 布局类型数量
    @Override
    public int getViewTypeCount() {
        return 4;
    }


    @Override
    public int getCount() {
        return 1 + data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolderHistory viewHolderHistory = null;
        ViewHolderRemind viewHolderRemind = null;
        ViewHolderlist viewHolderlist = null;
        ViewHolderlistExp viewHolderlistExp = null;


        // 设置视图
        if (convertView == null) {
            viewHolderHistory = new ViewHolderHistory();
            viewHolderRemind = new ViewHolderRemind();
            viewHolderlist = new ViewHolderlist();
            viewHolderlistExp = new ViewHolderlistExp();

            // 确定布局
            switch (type) {
                case 0: {
                    convertView = inflate.inflate(R.layout.item_home_history, null);
                    viewHolderHistory.title = (TextView) convertView.findViewById(R.id.home_history_title);
                    viewHolderHistory.content = (TextView) convertView.findViewById(R.id.home_history_content);
                    convertView.setTag(viewHolderHistory);
                    break;
                }
                case 1: {
                    convertView = inflate.inflate(R.layout.item_home_today, null);
                    viewHolderRemind.listInner = (ListView) convertView.findViewById(R.id.home_today_inner);
                    viewHolderRemind.time = (TextView) convertView.findViewById(R.id.home_today_time);
                    convertView.setTag(viewHolderRemind);
                    break;
                }
                case 2: {
                    convertView = inflate.inflate(R.layout.item_home_item_list, null);
                    viewHolderlist.time = (TextView) convertView.findViewById(R.id.home_item_list_time);
                    convertView.setTag(viewHolderlist);

                    break;
                }
                case 3: {
                    convertView = inflate.inflate(R.layout.item_home_item_list_exp, null);
                    viewHolderlistExp.listInner = (ListView) convertView.findViewById(R.id.item_home_item_inner);
                    viewHolderlistExp.rl = (RelativeLayout) convertView.findViewById(R.id.item_home_item_rl);
                    viewHolderlistExp.time = (TextView) convertView.findViewById(R.id.home_item_list_exp_time);
                    convertView.setTag(viewHolderlistExp);

                    break;
                }
            }
        } else {
            switch (type) {
                case 0: {
                    viewHolderHistory = (ViewHolderHistory) convertView.getTag();
                    break;
                }
                case 1: {
                    viewHolderRemind = (ViewHolderRemind) convertView.getTag();
                    break;
                }
                case 2: {
                    viewHolderlist = (ViewHolderlist) convertView.getTag();
                    break;
                }
                case 3: {
                    viewHolderlistExp = (ViewHolderlistExp) convertView.getTag();
                    break;
                }
            }
        }
        switch (type) {
            case 0: {
                if (historyTodayBean != null) {
                  //  viewHolderHistory.title.setText(historyTodayBean.getResult().get(0).getDay()+"");
                    viewHolderHistory.content.setText(historyTodayBean.getResult().get(0).getTitle()+"");
                }

                break;
            }
            case 1: {

                HomeRemindBean homeRemindBean = data.get(position - 1);

                //接下来会贴上innerlistview 的adapter
                AdapterHomeMainItem adapter = new AdapterHomeMainItem(context);
                viewHolderRemind.listInner.setAdapter(adapter);
                adapter.setData(homeRemindBean.data);
                //根据innerlistview的高度机损parentlistview item的高度
                setListViewHeightBasedOnChildren(viewHolderRemind.listInner);

                viewHolderRemind.time.setText(homeRemindBean.title);

                break;
            }
            case 2: {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        HomeRemindBean homeRemindBean = data.get(position - 1);
                        homeRemindBean.exp = !homeRemindBean.exp;
                        notifyDataSetChanged();
                    }
                });
                HomeRemindBean homeRemindBean = data.get(position - 1);
                viewHolderlist.time.setText(homeRemindBean.title);

                break;
            }

            case 3: {
                HomeRemindBean homeRemindBean = data.get(position - 1);

                //接下来会贴上innerlistview 的adapter
                AdapterHomeMainItem adapter1 = new AdapterHomeMainItem(context);
                viewHolderlistExp.listInner.setAdapter(adapter1);
                adapter1.setData(homeRemindBean.data);
                //根据innerlistview的高度机损parentlistview item的高度
                setListViewHeightBasedOnChildren(viewHolderlistExp.listInner);

                viewHolderlistExp.time.setText(homeRemindBean.title);


                viewHolderlistExp.rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int tmp = position - 1;
                        HomeRemindBean homeRemindBean1 = data.get(tmp);
                        homeRemindBean1.exp = !homeRemindBean1.exp;
                        notifyDataSetChanged();
                    }
                });

                break;
            }
        }


        return convertView;

    }


    class ViewHolderRemind {
        ListView listInner;
        TextView time;
    }

    class ViewHolderHistory {
        TextView title;
        TextView content;

    }

    class ViewHolderlist {
        TextView time;

    }

    class ViewHolderlistExp {
        ListView listInner;
        RelativeLayout rl;
        TextView time;
    }


    /**
     * 此方法是本次listview嵌套listview的核心方法：计算parentlistview item的高度。
     * 如果不使用此方法，无论innerlistview有多少个item，则只会显示一个item。
     **/
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


}
