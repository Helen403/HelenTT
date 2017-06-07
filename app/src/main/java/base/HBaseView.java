package base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public abstract class HBaseView extends RelativeLayout {
    //基本View
    protected View view;
    protected Context context;

    public HBaseView(Context context) {
        super(context);
        this.context = context;
        view = View.inflate(context, getContentView(), this);
        view.setDrawingCacheEnabled(true);
        findViews();
        initData();
        setListeners();
    }


    /***
     * 设置内容区域
     */
    public abstract int getContentView();

    public abstract void findViews();

    public abstract void initData();

    public abstract void setListeners();

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    /***********************************************************************/
    /**
     * 跳转到另一个Activity，不携带数据，不设置flag
     */
    public void goToActivityByClass(Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        context.startActivity(intent);
    }

    /**
     * 跳转到另一个Activity，携带数据
     */
    public void goToActivityByClass(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 延迟去往新的Activity
     */
    public void delayToActivity(final Class<?> cls, long delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                context.startActivity(new Intent(context, cls));
            }
        }, delay);
    }

    /*********************************************************************************************/
    public void T(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void T(float msg) {
        Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
    }

    public void T(double msg) {
        Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
    }

    public void T(int msg) {
        Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
    }

    public void T(boolean msg) {
        Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
    }

    public void L(String msg) {
        Log.d("Helen", "@@" + msg);
    }

    public void L(float msg) {
        Log.d("Helen", "@@" + msg + "");
    }

    public void L(double msg) {
        Log.d("Helen", "@@" + msg + "");
    }

    public void L(int msg) {
        Log.d("Helen", "@@" + msg + "");
    }

    public void L(boolean msg) {
        Log.d("Helen", "@@" + msg + "");
    }
}
