package base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Created by SNOY on 2016/8/5.
 */
public final class HBaseApplication extends Application {

    public static Context context;
    public static Handler handler = new Handler();


    public static final String APP_ID = "2882303761517580653";
    public static final String APP_KEY = "5931758052653";
    public static final String TAG = "Helen";


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Utils.init(context);
//        EMOptions options = new EMOptions();
//        // 默认添加好友时，是不需要验证的，改成需要验证
//        options.setAcceptInvitationAlways(false);
//
//        //初始化
//        EMClient.getInstance().init(this, options);
//        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
//        EMClient.getInstance().setDebugMode(true);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 百度地图全局context 的初始化
//        SDKInitializer.initialize(this);
//        //捕获全局异常
        exception.CrashHandler.getInstance().init();
//        PgyCrashManager.register(this);

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
////                .addInterceptor(new LoggerInterceptor("TAG"))
//                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
//                .readTimeout(10000L, TimeUnit.MILLISECONDS)
//                        //其他配置
//                .build();
//
//        OkHttpUtils.initClient(okHttpClient);



        //初始化push推送服务
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
        //打开Log
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}



