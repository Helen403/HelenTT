package activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.snoy.helen.R;

/**
 * Created by Helen on 2017/5/26.
 */
public class RemindActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_remind);

        Toast.makeText(this,"推送成功",Toast.LENGTH_SHORT).show();


    }





}
