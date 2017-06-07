package receiver;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * Created by Helen on 2017/6/1.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DService extends NotificationListenerService {
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

}
