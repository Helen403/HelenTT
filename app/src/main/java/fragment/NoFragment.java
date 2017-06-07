package fragment;

import android.widget.ListView;

import com.example.snoy.helen.R;

import java.util.ArrayList;

import adapter.AdapterNo;
import base.HBaseFragment;
import bean.NoBean;

/**
 * Created by Helen on 2017/5/15.
 */
public class NoFragment extends HBaseFragment {
    ListView listView;
    ArrayList<NoBean> data = new ArrayList<>();
    AdapterNo adapter;

    @Override
    public int getContentView() {
        return R.layout.fragment_no;
    }

    @Override
    public void findViews() {
        listView = (ListView) contentView.findViewById(R.id.no_listview);
    }

    @Override
    public void initData() {
        data.add(new NoBean());
        data.add(new NoBean());
        data.add(new NoBean());
        adapter = new AdapterNo(getActivity());
        listView.setAdapter(adapter);
        adapter.setData(data);
    }

    @Override
    public void setListeners() {

    }
}
