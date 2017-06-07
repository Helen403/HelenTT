package custemview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.snoy.helen.R;


/**
 * Created by Administrator on 2016/8/26.
 */
public final class MyNetFailView extends RelativeLayout implements View.OnClickListener {
    public View view;
    public TextView refresh;

    public MyNetFailView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        view = View.inflate(getContext(), R.layout.custermview_net_fail_view, this);
        refresh = (TextView) findViewById(R.id.refresh);
        refresh.setOnClickListener(this);
        onNormal();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onNormal() {
        int strokeWidth = dip2px(getContext(), 1); // 3dp 边框宽度
        int roundRadius = dip2px(getContext(), 8); // 8dp 圆角半径
        int strokeColor = Color.parseColor("#B37ED1");//边框颜色
        int fillColor = Color.parseColor("#f2f2f2");//内部填充颜色

        GradientDrawable gd = new GradientDrawable();//创建drawable
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        gd.setStroke(strokeWidth, strokeColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            refresh.setBackground(gd);
        } else {
            refresh.setBackgroundDrawable(gd);
        }
        refresh.setTextColor(Color.parseColor("#B37ED1"));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onPress() {
        int strokeWidth = dip2px(getContext(), 1); // 3dp 边框宽度
        int roundRadius = dip2px(getContext(), 8); // 8dp 圆角半径
        int strokeColor = Color.parseColor("#B37ED1");//边框颜色
        int fillColor = Color.parseColor("#B37ED1");//内部填充颜色

        GradientDrawable gd = new GradientDrawable();//创建drawable
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);
        gd.setStroke(strokeWidth, strokeColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            refresh.setBackground(gd);
        } else {
            refresh.setBackgroundDrawable(gd);
        }
        refresh.setTextColor(Color.WHITE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        onMyNetFailViewClick.onClick();
        onPress();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onNormal();
            }
        }, 210);
    }

    private OnMyNetFailViewClick onMyNetFailViewClick;


    public void setOnrefreshCallBack(OnMyNetFailViewClick onMyNetFailViewClick) {
        this.onMyNetFailViewClick = onMyNetFailViewClick;
    }

    public interface OnMyNetFailViewClick {
        void onClick();
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
