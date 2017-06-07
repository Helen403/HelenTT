package base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import bean.MessageEvent;


/**
 * Created by SNOY on 2016/8/20.
 */
public abstract class HBaseActivity extends FragmentActivity implements View.OnClickListener {

    public HBaseActivity context;
    /*******************************************************************************************/
    protected RelativeLayout content;
    // 外界传入内容区域的布局
    protected View contentView;

    /*************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        BarUtils.setColor(this, Color.BLACK);
        context = this;
        //注册EventBus
        EventBus.getDefault().register(this);
        dealLogicBeforeFindView();
        getBuildContentView();
        onShowMessage(content);
        setContentView(content);
        findViews();
        initData();
        //有数据就传递
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            onGetBundle(bundle);
        }
        setListeners();
    }

    /**
     * 添加自定义的遮盖提示
     * 特殊提供
     */
    protected void onShowMessage(RelativeLayout relativeLayout) {
    }

    private void getBuildContentView() {
        //总布局
        content = new RelativeLayout(this);
        LinearLayout.LayoutParams rl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        content.setLayoutParams(rl);

        //从外面传来的View添加进入
        LinearLayout.LayoutParams viewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        contentView = (View) getLayoutInflater().inflate(getContentView(), content, false);
        contentView.setLayoutParams(viewLayoutParams);
        content.addView(contentView);

    }


    protected void dealLogicBeforeFindView() {
    }


    /***
     * 设置内容区域
     */
    public abstract int getContentView();

    public abstract void findViews();

    public abstract void initData();

    public abstract void setListeners();


    protected void onGetBundle(Bundle bundle) {
    }

    //没网络的运行
    protected void onNoNetInitData() {
    }

    /******************************************************************/
    /**
     * 处理点击返回按钮--------退出当前的Activity 返回到上一个Activity
     */
    public void goBack() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView()
                    .getWindowToken(), 0);
        }
        finish();


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


    /*********************************************************************/

    public void T(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void T(float msg) {
        Toast.makeText(getApplicationContext(), msg + "", Toast.LENGTH_SHORT).show();
    }

    public void T(double msg) {
        Toast.makeText(getApplicationContext(), msg + "", Toast.LENGTH_SHORT).show();
    }

    public void T(int msg) {
        Toast.makeText(getApplicationContext(), msg + "", Toast.LENGTH_SHORT).show();
    }

    public void T(boolean msg) {
        Toast.makeText(getApplicationContext(), msg + "", Toast.LENGTH_SHORT).show();
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

    /********************************************************************************/
    /**
     * sumScale  总的比例   以竖屏为参考  屏幕宽为比例总
     * ScaleW    宽占总的比例多少
     * ScaleH    高占总的比例多少
     */
    public void setScaleView(View view, int sumScale, int ScaleW, int ScaleH) {
        //屏幕的宽
        int screenW = 0;
        //屏幕的高
        int screenH = 0;
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenW = display.getWidth();
        screenH = display.getHeight();
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
        /************************************************/
        int tmpW = ScaleW / sumScale * screenW;
        int tmpH = ScaleH / sumScale * screenW;
        params.width = tmpW;
        params.height = tmpH;
        view.setLayoutParams(params);
    }


    /**********************************************************************************/

    /**
     * 显示popupWindow
     * parent    需要挂上的View
     * showView  需要显示的View
     */
    public PopupWindow showPopupWindow(View parent, View showView) {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int screenW = windowManager.getDefaultDisplay().getWidth();
        int screenH = windowManager.getDefaultDisplay().getHeight();
        PopupWindow popupWindow = new PopupWindow(showView, screenW, screenH);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(parent, 0, 0);
        return popupWindow;
    }
    //============================================================================

    @CallSuper
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMessage(@NonNull MessageEvent event) {
     //   L("onReceiveMessage..." + (event == null ? "null" : event));
    }



    protected final void onSendMessage(@NonNull MessageEvent event) {
        // 发布EventBus消息事件
        EventBus.getDefault().post(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}