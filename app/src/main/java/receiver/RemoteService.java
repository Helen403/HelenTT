package receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.snoy.helen.R;

/**
 * Created by Helen on 2017/5/26.
 */
public class RemoteService extends Service {

    private MyBilder mBilder;
    //private final static int GRAY_SERVICE_ID = 1002;

    @Override
    public IBinder onBind(Intent intent) {
        if (mBilder == null)
            mBilder = new MyBilder();
        return mBilder;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 在RemoteService运行后,我们对LocalService进行绑定。 把优先级提升为前台优先级
        this.bindService(new Intent(RemoteService.this, LocalService.class),
                connection, Context.BIND_ABOVE_CLIENT);

//        Notification notification = new Notification(R.mipmap.ic_launcher,
//                "安全服务启动中", System.currentTimeMillis());

//
//
//
//
//        notification.setLatestEventInfo(this, "安全服务", "安全服务...", pendingIntent);
//        startForeground(startId, notification);

//        Notification.Builder builder = new Notification.Builder(this);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        startForeground(250, builder.build());

        Log.d("Helen", "RemoteService服务器");

        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        Notification notification = new Notification.Builder(RemoteService.this)
                .setAutoCancel(true)
                .setContentTitle("title")
                .setContentText("describe")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        startForeground(startId, notification);

        return START_STICKY;
    }

    private class MyBilder extends ProcessService.Stub {

        @Override
        public void doSomething() throws RemoteException {
            Log.i("TAG", "绑定成功!");

        }

    }

    private ServiceConnection connection = new ServiceConnection() {

        /**
         * 在终止后调用,我们在杀死服务的时候就会引起意外终止,就会调用onServiceDisconnected
         * 则我们就得里面启动被杀死的服务,然后进行绑定
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("TAG", "LocalService被杀死了");
            Intent remoteService = new Intent(RemoteService.this,
                    LocalService.class);
            RemoteService.this.startService(remoteService);
            RemoteService.this.bindService(new Intent(RemoteService.this,
                    LocalService.class), connection, Context.BIND_ABOVE_CLIENT);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("TAG", "LocalService链接成功!");
            try {
                if (mBilder != null)
                    mBilder.doSomething();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

}
