package base;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by mcs on 2015/11/3.
 */
public abstract class HBaseFragment extends Fragment implements View.OnClickListener {

    //外面传入的View
    protected View contentView;
    //总布局
    protected RelativeLayout content;
    //填充器
    public LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        dealLogicBeforeFindView();
        //总布局
        content = new RelativeLayout(getActivity());
        LinearLayout.LayoutParams rl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        content.setLayoutParams(rl);
        //外面传入的View
        contentView = inflater.inflate(getContentView(), content, false);
        if (getArguments() != null)
            onGetBundle(getArguments());
        contentView.setClickable(true);
        content.addView(contentView);
        onShowMessage(content);
        return content;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews();
        initData();
        setListeners();
    }


    /**
     * 添加自定义的遮盖提示
     * 特殊提供
     */
    protected void onShowMessage(RelativeLayout relativeLayout) {
    }


    protected void dealLogicBeforeFindView() {
    }

    /***
     * 给调用者
     */
    public abstract int getContentView();

    public abstract void findViews();

    public abstract void initData();

    public abstract void setListeners();


    /****************************************************************************/


    /**
     * 获取上一个Fragment传来的数据
     */
    protected void onGetBundle(Bundle bundle) {

    }

    /**
     * 这种方式只限于再viewpager时使用
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
        } else {
            //相当于Fragment的onPause
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //不可见
            //还原标题栏
            //取消加载图片的任务
            //ImageUtils.getInstance().cancelTask();
        } else {
            //当前可见
        }
    }

    /**
     * 网上说这个方法跟Activity不一样  可能不起作用所以使用上面的方法
     */
    @Override
    public void onPause() {
        super.onPause();
    }
    /****************************************************************************/

    /**
     * 跳转到另一个Activity，不携带数据，不设置flag
     */
    public void goToActivityByClass(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }

    /**
     * 跳转到另一个Activity，携带数据
     */
    public void goToActivityByClass(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }


    /**
     * 延迟去往新的Activity
     */
    public void delayToActivity(final Class<?> cls, long delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().startActivity(new Intent(getActivity(), cls));
            }
        }, delay);
    }

    /*********************************************************************/
    public void T(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void T(float msg) {
        Toast.makeText(getActivity(), msg + "", Toast.LENGTH_SHORT).show();
    }

    public void T(double msg) {
        Toast.makeText(getActivity(), msg + "", Toast.LENGTH_SHORT).show();
    }

    public void T(int msg) {
        Toast.makeText(getActivity(), msg + "", Toast.LENGTH_SHORT).show();
    }

    public void T(boolean msg) {
        Toast.makeText(getActivity(), msg + "", Toast.LENGTH_SHORT).show();
    }

    public void L(String msg) {
        Log.d("Helen", "@@" + msg);
    }

    public void L(float msg) {
        Log.d("Helen", "@@" + msg);
    }

    public void L(double msg) {
        Log.d("Helen", "@@" + msg);
    }

    public void L(int msg) {
        Log.d("Helen", "@@" + msg);
    }

    public void L(boolean msg) {
        Log.d("Helen", "@@" + msg);
    }


    /***********************************************************************/
    /**
     * 判断网络是否连接
     */
    public boolean isConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /*************************************************************************/


    /**
     * 添加点击事件
     */
    protected void setOnListeners(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    onClick click;

    public void setOnClick(onClick click) {
        this.click = click;
    }

    public interface onClick {
        void onClick(View v, int id);
    }

    @Override
    public void onClick(View v) {
        click.onClick(v, v.getId());
    }

    /******************************************************************************/
    /**
     * 通过反射获取资源 R.id
     * 根据给定的类型名和字段名，返回R文件中的字段的值
     *
     * @param typeName  属于哪个类别的属性 （id,layout,drawable,string,color,attr......）
     * @param fieldName 字段名
     * @return 字段的值
     */
    public int getFieldValue(String typeName, String fieldName, Context context) {
        int i;
        try {
            Class<?> clazz = Class.forName(context.getPackageName() + ".R$" + typeName);
            i = clazz.getField(fieldName).getInt(null);
        } catch (Exception e) {
            return -1;
        }
        return i;
    }

}
