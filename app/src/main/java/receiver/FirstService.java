package receiver;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.example.snoy.helen.R;


public class FirstService extends Service {

    private MyBinder binder;
    private MyConn conn;


    private FirstService m_service = null;
    private NotificationManager m_notificationMgr = null;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();

        binder = new MyBinder();
        if (conn == null)
            conn = new MyConn();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        startForeground(250, builder.build());
        startService(new Intent(this, ThirdService.class));
        FirstService.this.bindService(new Intent(this, SecondService.class),
                conn, Context.BIND_IMPORTANT);
    }

    class MyBinder extends ProcessService.Stub {

        @Override
        public void doSomething() throws RemoteException {
            Log.i("TAG", "绑定成功!");
            FirstService.this.startService(new Intent(FirstService.this,
                    SecondService.class));
            // 绑定FirstService
            FirstService.this.bindService(new Intent(FirstService.this,
                    SecondService.class), conn, Context.BIND_IMPORTANT);
        }


    }

    class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder iBinder) {
            // Log.i("Info", "@@@@@@@@@@@@与SecondService连接成功");
            ActivityManager activityManager = (ActivityManager) FirstService.this
                    .getSystemService(Context.ACTIVITY_SERVICE);
            try {
                if (binder != null)
                    binder.doSomething();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(FirstService.this, "SecondService被杀死",
                    Toast.LENGTH_SHORT).show();
            // 启动FirstService
            FirstService.this.startService(new Intent(FirstService.this,
                    SecondService.class));
            // 绑定FirstService
            FirstService.this.bindService(new Intent(FirstService.this,
                    SecondService.class), conn, Context.BIND_IMPORTANT);

        }
    }


}
