package activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.RegexUtils;
import com.example.snoy.helen.R;

import base.HBaseActivity;

/**
 * Created by Helen on 2017/6/2.
 */
public class BindPhoneActivity extends HBaseActivity {

    private ImageView bPhoneClose;
    private TextView bPhoneNext;
    private TextView bPhoneAddress;
    private EditText bPhoneEdit;


    @Override
    public int getContentView() {
        return R.layout.activity_bind_phone;
    }

    @Override
    public void findViews() {
        bPhoneClose = (ImageView) contentView.findViewById(R.id.b_phone_close);
        bPhoneNext = (TextView) contentView.findViewById(R.id.b_phone_next);
        bPhoneAddress = (TextView) contentView.findViewById(R.id.b_phone_address);
        bPhoneEdit = (EditText) contentView.findViewById(R.id.b_phone_edit);
    }

    @Override
    public void initData() {

    }

    @Override
    public void setListeners() {
        setOnListeners(bPhoneClose, bPhoneNext);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.b_phone_close:
                        close();
                        break;
                    case R.id.b_phone_next:
                        next();
                        break;
                }
            }
        });
    }

    private void close() {
        finish();
    }

    private void next() {
        String phone = bPhoneEdit.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            T("请输入手机号");
            return;
        }
        if (RegexUtils.isMobileExact(phone)) {

        }
    }
}
