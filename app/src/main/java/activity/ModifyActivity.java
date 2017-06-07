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
 * Created by Helen on 2017/6/2.
 */
public class ModifyActivity extends HBaseActivity {

    private TextView modifyClose;
    private TextView modifySave;
    private EditText modifyEdit;


    @Override
    public int getContentView() {
        return R.layout.activity_modify;
    }

    @Override
    public void findViews() {
        modifyClose = (TextView) contentView.findViewById(R.id.modify_close);
        modifySave = (TextView) contentView.findViewById(R.id.modify_save);
        modifyEdit = (EditText) contentView.findViewById(R.id.modify_edit);
    }

    @Override
    public void initData() {

        String name = DButils.get(HConstants.KEY.nickName);
        modifyEdit.setText(name);

        modifyEdit.addTextChangedListener(textWatcher);
        KeyboardUtils.showSoftInput(modifyEdit);
    }

    @Override
    public void setListeners() {
        setOnListeners(modifyClose, modifySave);
        setOnClick(new onClick() {
            @Override
            public void onClick(View v, int id) {
                switch (id) {
                    case R.id.modify_close:
                        close();
                        break;
                    case R.id.modify_save:
                        save();
                        break;
                }
            }
        });
    }

    private void save() {
        final String name = modifyEdit.getText().toString();
        String userCode = DButils.get(HConstants.KEY.userCode);
        HashMap<String, String> map = new HashMap<>();
        map.put("userCode", userCode);
        map.put("nickName", name);
        ControlUtils.getsEveryTime(HConstants.URL.updateNickName, map, ResultBean.class, new ControlUtils.OnControlUtils<ResultBean>() {
            @Override
            public void onSuccess(String url, ResultBean resultBean, String result) {
                L(result);
                L("修改成功");
                DButils.put(HConstants.KEY.nickName,name);

            }

            @Override
            public void onFailure(String url) {
                L("修改失败");
            }
        });


        onSendMessage(new MessageEvent(HConstants.EVENT.NAME_REFRESH, name));

        KeyboardUtils.hideSoftInput(ModifyActivity.this);
        finish();
    }

    private void close() {
        KeyboardUtils.hideSoftInput(ModifyActivity.this);
        finish();
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = DButils.get(HConstants.KEY.nickName);
            if (name.equals(modifyEdit.getText().toString()) || TextUtils.isEmpty(modifyEdit.getText().toString())) {
                modifySave.setTextColor(Color.parseColor("#878E96"));
                modifySave.setEnabled(false);
            } else {
                modifySave.setTextColor(Color.parseColor("#ffffff"));
                modifySave.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}
