package activity;

import android.content.Intent;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.example.snoy.helen.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import HConstants.HConstants;
import Utils.DButils;
import Utils.HTimeUtils;
import adapter.AdapterPhoto;
import base.HBaseActivity;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;

/**
 * Created by Helen on 2017/5/24.
 */
public class PhotoActivity extends HBaseActivity {


    private AdapterPhoto adapterPhoto;

    private ArrayList<String> selectedPhotos = new ArrayList<>();


    @Override
    public int getContentView() {
        return R.layout.activity_photo;
    }

    @Override
    public void findViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapterPhoto = new AdapterPhoto(this, selectedPhotos);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(adapterPhoto);
    }

    @Override
    public void initData() {
        contentView.findViewById(R.id.photo_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setGridColumnCount(4)
                        .start(PhotoActivity.this);
            }
        });
    }

    @Override
    public void setListeners() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }

            L("图片" + photos.get(0));
//            FileUpLoadService service = new FileUpLoadService(PhotoActivity.this, "Helen.png", photos.get(0), "");
//            service.upload();
            File file = new File(photos.get(0));
//            OkHttpUtils
//                    .postFile()
//                    .url("http://192.168.1.131:8080/fileUplod/file/upload2")
//                    .file(file)
//                    .build()
//                    .execute(new MyStringCallback());

            //  String filename = file.getName();
            String userCode = DButils.get(HConstants.KEY.userCode);

            String name = "4" +"_" + HTimeUtils.getCurrentTime()+".png";
            L(name);


            String url = HConstants.URL.uploadPicFile + "?userCode=4";

            OkHttpUtils.post()
                    .addFile("mFile", name , file)
                    .url(url)
                    .build()
                    .execute(new MyStringCallback());


            selectedPhotos.clear();

            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            adapterPhoto.notifyDataSetChanged();
        }
    }

    class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e, int id) {
            L("失败");
        }

        @Override
        public void onResponse(String response, int id) {
            L(response);
            L("成功");
        }
    }

}
