package receiver;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import HConstants.HConstants;
import Utils.DButils;
import activity.RemindActivity;
import okhttp3.Call;

/**
 * Created by Helen on 2017/5/25.
 */
public class HReceiver extends PushMessageReceiver {

    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mUserAccount;
    private String mStartTime;
    private String mEndTime;


    //onReceivePassThroughMessage用来接收服务器发送的透传消息
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
        Toast.makeText(context, "66666", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, RemindActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    // onNotificationMessageClicked用来接收服务器发来的通知栏消息（用户点击通知栏时触发）
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
        Log.d("Helen", "用来接收服务器发来的通知栏消息（用户点击通知栏时触发）");

        Log.d("Helen", "用来接收服务器发送的透传消息");
        Intent intent = new Intent(context, RemindActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.d("Helen", "666");

    }


    //onNotificationMessageArrived用来接收服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息）
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
        Log.d("Helen", "用来接收服务器发来的通知栏消息（消息到达客户端时触发，并且可以接收应用在前台时不弹出通知的通知消息）");

        Log.d("Helen", "用来接收服务器发来的通知栏消息");
        Intent intent = new Intent(context, RemindActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.d("Helen", "666");


        Toast.makeText(context, "接受到", Toast.LENGTH_SHORT).show();


    }


    //onCommandResult用来接收客户端向服务器发送命令消息后返回的响应
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }

        Log.d("Helen", "用来接收客户端向服务器发送命令消息后返回的响应");

    }


    //onReceiveRegisterResult用来接受客户端向服务器发送注册命令消息后返回的响应。
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        }
        Log.d("Helen", "用来接受客户端向服务器发送注册命令消息后返回的响应。");

        if (DButils.get(HConstants.KEY.RegId) == null || !DButils.get(HConstants.KEY.RegId).equals(mRegId)) {
            OkHttpUtils
                    .get()
                    .url(HConstants.URL.updateRegId)
                    .addParams("userCode", "1")
                    .addParams("regId", mRegId)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.d("Helen", "失败");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.d("Helen", response);
                        }
                    });
            DButils.put("HConstants.KEY.RegId", mRegId);
        }

    }
}
