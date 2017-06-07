package activity;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.example.snoy.helen.R;

import java.util.HashMap;

import HConstants.HConstants;
import Utils.ControlUtils;
import Utils.DButils;
import base.HBaseActivity;
import bean.MessageEvent;
import bean.ResultBean;

/**
 * Created by Helen on 2017/6/6.
 */
public class ModifySignActivity extends HBaseActivity {


    private TextView modifySignClose;
    private TextView modifySignSave;
    private EditText modifySignEdit;


    @Override
    public int getContentView() {
        return R.layout.activity_modify_sign;
    }

    @Override
    public void findViews() {
        modifySignClose = (TextView) contentView.findViewById(R.id.modify_sign_close);
        modifySignSave = (TextView) contentView.findViewById(R.id.modify_sign_save);
        modifySignEdit = (EditText) contentView.findViewById(R.id.modify_sign_edit);
    }

    @Override
    public void initData() {

        String name = DButils.get(HConstants.KEY.signName);
        modifySignEdit.setText(name);

        modifySignEdit.addTextChangedListener(textWatcher);
        KeyboardUtils.showSoftInput(modifySignEdit);
    }

    @Override
    public void setListeners() {
        setOnListeners(modifySignClose, modifySignSave);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.modify_sign_close:
                        close();
                        break;
                    case R.id.modify_sign_save:
                        save();
                        break;
                }
            }
        });
    }

    private void save() {
        final String name = modifySignEdit.getText().toString();
        String userCode = DButils.get(HConstants.KEY.userCode);
        HashMap<String, String> map = new HashMap<>();
        map.put("userCode", userCode);
        map.put("signName", name);
        ControlUtils.getsEveryTime(HConstants.URL.updateSignName, map, ResultBean.class, new ControlUtils.OnControlUtils<ResultBean>() {
            @Override
            public void onSuccess(String url, ResultBean resultBean, String result) {
                L(result);
                L("修改成功");
                DButils.put(HConstants.KEY.signName, name);
            }

            @Override
            public void onFailure(String url) {
                L("修改失败");
            }
        });

        DButils.put(HConstants.KEY.signName, name);
        onSendMessage(new MessageEvent(HConstants.EVENT.NAME_REFRESH, name));

        KeyboardUtils.hideSoftInput(ModifySignActivity.this);
        finish();
    }

    private void close() {
        KeyboardUtils.hideSoftInput(ModifySignActivity.this);
        finish();
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = DButils.get(HConstants.KEY.nickName);
            if (name.equals(modifySignEdit.getText().toString()) || TextUtils.isEmpty(modifySignEdit.getText().toString())) {
                modifySignSave.setTextColor(Color.parseColor("#878E96"));
                modifySignSave.setEnabled(false);
            } else {
                modifySignSave.setTextColor(Color.parseColor("#ffffff"));
                modifySignSave.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
