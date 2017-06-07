package adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.snoy.helen.R;

import Utils.HImgeLoad;
import base.HBaseAdapter;
import bean.ComBean;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Helen on 2017/5/23.
 */
public class AdapterFriend extends HBaseAdapter<ComBean.FriendListBean> {

    public AdapterFriend(Context context) {
        super(context);
    }

    @Override
    public int getContentView() {
        return R.layout.item_friend;
    }

    @Override
    public void convert(ViewHolder holder, ComBean.FriendListBean comBean, int position) {
        CircleImageView icon = holder.findViewById(R.id.friend_icon);
        TextView name = holder.findViewById(R.id.friend_name);
        TextView tag = holder.findViewById(R.id.friend_tag);

        String nameH = comBean.getUser().getUserNickName() + "";
        String path = comBean.getUser().getUserHead();
        String tagH = comBean.getUser().getPinyinF();
        String isShow = comBean.getUser().getIsShow();

        if (!TextUtils.isEmpty(tagH)) {
            tag.setText(tagH.toUpperCase());
        }

        if (isShow.equals("1")){
            tag.setVisibility(View.VISIBLE);
        }else {
            tag.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(nameH)) {
            name.setText(nameH);
        } else {
            name.setText("游客");
        }

        if (!TextUtils.isEmpty(path)) {
            HImgeLoad.setImageByUrl(path, icon);
        }
    }
}
