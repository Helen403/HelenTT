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
public class LocalService extends Service {

    private MyBilder mBilder;

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
        this.bindService(new Intent(LocalService.this, RemoteService.class),
                connection, Context.BIND_ABOVE_CLIENT);

//        Notification notification = new Notification(R.mipmap.ic_launcher,
//                "安全服务启动中", System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
                0);
//       // notification.setLatestEventInfo(this, "安全服务", "安全服务...", pendingIntent);
//        startForeground(startId, notification);

//        Notification.Builder builder = new Notification.Builder(this);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        startForeground(250, builder.build());
//        startService(new Intent(this, ThirdService.class));
//        startService(new Intent(this, ThirdService.class));
//        LocalService.this.bindService(new Intent(this, SecondService.class),
//                conn, Context.BIND_IMPORTANT);

        Log.d("Helen","LocalService服务器");
        Notification notification = new Notification.Builder(LocalService.this)
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
            Intent localService = new Intent(LocalService.this,
                    RemoteService.class);
            LocalService.this.startService(localService);
            LocalService.this
                    .bindService(new Intent(LocalService.this,
                                    RemoteService.class), connection,
                            Context.BIND_ABOVE_CLIENT);
        }

    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("TAG", "RemoteService被杀死了");
            Intent localService = new Intent(LocalService.this,
                    RemoteService.class);
            LocalService.this.startService(localService);
            LocalService.this
                    .bindService(new Intent(LocalService.this,
                                    RemoteService.class), connection,
                            Context.BIND_ABOVE_CLIENT);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("TAG", "RemoteService链接成功!");
            try {
                if (mBilder != null)
                    mBilder.doSomething();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

}
