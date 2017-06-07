package exception;

import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import HConstants.HConstants;
import Utils.HTimeUtils;
import base.HBaseApplication;
import okhttp3.Call;

/**
 * 捕获全局异常
 * <p/>
 * 开启异常捕获
 * <p/>
 * ApplicationUtil中CrashHandler.getInstance().init();
 */
public final class CrashHandler implements Thread.UncaughtExceptionHandler {
    // 异常存储路径
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/project_att/log/";
    private Thread.UncaughtExceptionHandler defaultCrashHandler;
    private static CrashHandler crashHandler = new CrashHandler();

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return crashHandler;
    }

    public void init() {
        defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        dumpExceptionToSDCard(throwable);
        if (defaultCrashHandler != null) {
            defaultCrashHandler.uncaughtException(thread, throwable);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    // 异常信息的处理
    private void dumpExceptionToSDCard(Throwable throwable) {
        Toast.makeText(HBaseApplication.context, "请反馈崩溃操作，我们会在下次更新中修复！", Toast.LENGTH_LONG).show();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.e("异常捕获", "设备没有SD卡");
            return;
        }
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String deviceName = DeviceUtils.getManufacturer()+" "+DeviceUtils.getModel();
        String networkOperatorName = PhoneUtils.getSimOperatorName();
        String errorInfo = exceptionToString(throwable);
        String fileName = deviceName + "_" + System.currentTimeMillis() + ".txt";
        File file = new File(PATH + fileName);
        try {
            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            printWriter.println(HTimeUtils.getCurrentTime());
            dumpPhoneInfo(printWriter);
            printWriter.println();
            throwable.printStackTrace(printWriter);
            printWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("异常捕获", "保存异常信息的设备出错");
        } finally {

            Log.d("Helen","异常");
            OkHttpUtils.post()//
                    .addFile("mFile", fileName, file)//
                    .url(HConstants.URL.uploadPhoto)
                    .build()//
                    .execute(new MyStringCallback());

        }
    }

    /**
     * 将异常信息转化成字符串
     *
     * @param t
     * @return
     * @throws IOException
     */
    private static String exceptionToString(Throwable t) {
        String error = "";
        try {
            if (t == null)
                return null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                t.printStackTrace(new PrintStream(baos));
            } finally {
                baos.close();
            }
            error = baos.toString();
        } catch (Exception ex) {
        }
        return error;
    }

    // 输出设备信息
    private void dumpPhoneInfo(PrintWriter printWriter) {
        // 设备的版本信息
        printWriter.print("OS Version:");
        printWriter.print(Build.VERSION.RELEASE);
        printWriter.print("_");
        printWriter.println(Build.VERSION.SDK_INT);
        // 设备的型号
        printWriter.print("Vendor:");
        printWriter.println(Build.MANUFACTURER);
        // 设备名称
        printWriter.print("Brand:");
        printWriter.println(Build.BRAND);
    }


    class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e, int id) {
            Log.d("Helen", "失败");
        }

        @Override
        public void onResponse(String response, int id) {
            Log.d("Helen", "成功");
        }
    }


}