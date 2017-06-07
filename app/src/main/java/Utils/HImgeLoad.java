package Utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.snoy.helen.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;

/**
 * Created by Helen on 2017/6/5.
 */
public class HImgeLoad {


    public static void setImageByUrl(final String url, final ImageView imageView) {

//        Glide.with(HBaseApplication.context)
//                .load(url)
//                .centerCrop()
//                .placeholder(R.mipmap.ic_launcher)
//                .into(imageView);



        try {
            OkHttpUtils
                    .get()
                    .url(url)
                    .build()
                    .execute(new BitmapCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(Bitmap bitmap, int id) {
                            imageView.setImageBitmap(bitmap);
                        }

                    });
        }catch (Exception e){
            e.printStackTrace();
            imageView.setImageResource(R.mipmap.quila);
        }

    }


}
