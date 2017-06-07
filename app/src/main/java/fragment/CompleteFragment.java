package fragment;

import android.widget.ListView;

import com.example.snoy.helen.R;

import java.util.ArrayList;

import adapter.AdapterComplete;
import base.HBaseFragment;
import bean.CompleteBean;

/**
 * Created by Helen on 2017/5/15.
 */
public class CompleteFragment extends HBaseFragment {

    ListView listView;
    AdapterComplete adapter;
    ArrayList<CompleteBean> data = new ArrayList<>();


    @Override
    public int getContentView() {
        return R.layout.fragment_complete;
    }

    @Override
    public void findViews() {
        listView = (ListView) contentView.findViewById(R.id.complete_listview);
    }

    @Override
    public void initData() {
        adapter = new AdapterComplete(getActivity());
        listView.setAdapter(adapter);
        data.add(new CompleteBean());
        data.add(new CompleteBean());
        data.add(new CompleteBean());
        adapter.setData(data);
    }

    @Override
    public void setListeners() {

    }
}
