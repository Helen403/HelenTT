package Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.example.snoy.helen.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import base.HBaseApplication;

/**
 * Created by Helen on 2017/5/31.
 */
public class HImageUtils {
    //需要配置一下Context
    private Context context = HBaseApplication.context;

    //单例模式
    private volatile static HImageUtils imageDownLoader;

    //目标路径 SD卡路径 用来存储图片
    private static final String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/project_helen/images/";

    //外界传入的ImageView
    private ImageView imageView;

    //加载中显示的图片
    private static final int loading = R.mipmap.ic_launcher;

    //加载失败显示的图片
    private static final int failing = R.mipmap.ic_launcher;

    //线程池的数量  默认2条
    private static final int ThreadPoolNum = 2;

    //内存的分配  默认4M
    private static final int MemoryNum = 4;


    public static HImageUtils getInstance() {
        if (imageDownLoader == null) {
            synchronized (HImageUtils.class) {
                if (imageDownLoader == null) {
                    imageDownLoader = new HImageUtils();
                }
            }
        }
        return imageDownLoader;
    }


    /**
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
     */
    private LruCache<String, Bitmap> lruCache;

    /**
     * 下载Image的线程池
     */
    private ExecutorService mImageThreadPool = null;

    //因为是单例所以只会初始化一次
    private HImageUtils() {
        // 给LruCache分配4M内存
        lruCache = new LruCache<String, Bitmap>(1024 * 1024 * MemoryNum) {
            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (evicted) {
                    //如果被一级缓存移除，则放入二级缓存
                    softCache.put(key, new SoftReference<Bitmap>(oldValue));
                }
            }

            // 必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    /**
     * 二级缓存
     */
    private static HashMap<String, SoftReference<Bitmap>> softCache = new HashMap<String, SoftReference<Bitmap>>();

    /**
     * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
     */
    private ExecutorService getThreadPool() {
        if (mImageThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mImageThreadPool == null) {
                    // 为了下载图片更加的流畅，我们用了2个线程来下载图片
                    mImageThreadPool = Executors.newFixedThreadPool(ThreadPoolNum);
                }
            }
        }
        return mImageThreadPool;
    }

    /**
     * 添加Bitmap到内存缓存
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            lruCache.put(key, bitmap);
        }
    }

    /**
     * 从内存缓存中获取一个Bitmap
     */
    private Bitmap getBitmapFromMemCache(String key) {
        if (key == null) return null;
        return lruCache.get(key);
    }

    /**
     * 先从内存缓存中获取Bitmap,如果没有就从SD卡或者手机缓存中获取，SD卡或者手机缓存 没有就去下载
     */
    private static Handler handler = new Handler();

    private void downloadImage(final String url) {
        getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //从网络获取图片
                final Bitmap bitmap = getBitmapByUrl(url);
                if (bitmap != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setBitmap(bitmap);
                        }
                    });
                    // 保存在SD卡或者手机目录
                    saveBitmapToSDCardByUrl(url, bitmap);
                    // 将Bitmap 加入内存缓存
                    addBitmapToMemoryCache(url, bitmap);
                } else {
                    //加载失败
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setfailingBitmap();
                        }
                    });

                }
            }
        });
    }

    /**
     * 获取Bitmap, 内存中没有就去手机或者sd卡中获取
     */
    public Bitmap getBitmapByCache(String url) {
        //从一级缓存中取
        Bitmap bitmap = getBitmapFromMemCache(url);
        //从二级缓存中取
        if (bitmap == null) {
            if (softCache.containsKey(url)) {
                SoftReference<Bitmap> soft = softCache.get(url);
                bitmap = soft.get();
                if (bitmap != null) {
                    //放回一级缓存
                    lruCache.put(url, bitmap);
                    softCache.remove(url);
                }
            }
        }
        // 从SD卡获取手机里面获取Bitmap
        if (bitmap == null) {
            bitmap = getBitmapToSDCardByUrl(url);
            // 将Bitmap 加入内存缓存
            addBitmapToMemoryCache(url, bitmap);
        }
        return bitmap;
    }


    /**
     * 取消正在下载的任务
     */
    public synchronized void cancelTask() {
        if (mImageThreadPool != null) {
            mImageThreadPool.shutdownNow();
            mImageThreadPool = null;
        }
    }


    /**
     * 异步下载图片的回调设置图片
     */
    private void setBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }


    /**
     * 向外界提供方法  用来加载图片数据
     */
    public void setImageByUrl(String url, ImageView imageView) {
        this.imageView = imageView;
        //加载中显示的图片
        setLoadingBitmap();
        //内存或者SD卡中有图片直接设置
        Bitmap bitmap = getBitmapByCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            downloadImage(url);
        }
    }

    /**
     * 加载中显示的图片
     */
    private void setLoadingBitmap() {
        this.imageView.setImageResource(loading);
    }

    /**
     * 加载失败显示的图片
     */
    private void setfailingBitmap() {
        this.imageView.setImageResource(failing);
    }


    /*******************************************************************************/

    /**
     * 判断拓展卡是否装载
     */
    private static boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    /**
     * 保存图片到拓展卡
     */
    public void saveBitmapToSDCardByUrl(String url, Bitmap bitmap) {
        if (!isMounted())
            return;
        File file = new File(IMAGE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            File temp = new File(file, "" + url.hashCode());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(temp));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片从扩展卡
     */
    public Bitmap getBitmapToSDCardByUrl(String url) {
        if (!isMounted())
            return null;
        File file = new File(IMAGE_PATH, "" + url.hashCode());
        if (file.exists()) {
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            } catch (OutOfMemoryError outOfMemoryError) {
                //超大的图片造成内存溢出 显示默认图片
                bitmap = BitmapFactory.decodeResource(context.getResources(), failing);
            }
            return bitmap;
        }
        return null;
    }


    /**
     * 递归
     * 获取文件的大小
     */
    public long getFileSize() {
        if (!isMounted())
            return 0;
        File file = new File(IMAGE_PATH);
        if (file.exists()) {
            return getFolderSize(file);
        }
        return 0;
    }

    /**
     * 得到文件的大小
     */
    private long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                // 如果下面还有文件
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    /**
     * 删除SD卡缓存图片
     */
    public void deleteFile() {
        if (!isMounted())
            return;
        File dirFile = new File(IMAGE_PATH);
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (String aChildren : children) {
                new File(dirFile, aChildren).delete();
            }
        }
        dirFile.delete();
    }


    /*****************************************************************************/

    /**
     * GET请求获取数据和图片
     */
    private byte[] getByteInfoByUrl(String url) {
        HttpURLConnection conn = null;
        ByteArrayOutputStream out = null;
        InputStream in = null;
        try {
            URL ul = new URL(url);
            conn = (HttpURLConnection) ul.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            out = new ByteArrayOutputStream();
            in = conn.getInputStream();
            int len;
            byte[] buffer = new byte[1024 * 2];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 从网上获取一张Bitmap
     */
    private Bitmap getBitmapByUrl(String url) {
        Bitmap bitmap = null;
        byte[] buffer = getByteInfoByUrl(url);
        if (buffer != null) {
            bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        }
        return bitmap;
    }


    public void getBitmapByUrl(final String url, final OnImageUtilsListener listener) {
        getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = getBitmapByUrl(url);
                if (bitmap != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onImageSuccess(url, bitmap);
                        }
                    });
                } else {
                    //下载失败
                    listener.onImageFailure(url);
                }
            }
        });
    }

    /**
     * 根据view来生成bitmap图片，可用于截图功能
     */
    public Bitmap getBitmapByView(View view) {
        view.clearFocus(); //
        view.setPressed(false); //
        // 能画缓存就返回false
        boolean willNotCache = view.willNotCacheDrawing();
        view.setWillNotCacheDrawing(false);
        int color = view.getDrawingCacheBackgroundColor();
        view.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            view.destroyDrawingCache();
        }
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        view.destroyDrawingCache();
        view.setWillNotCacheDrawing(willNotCache);
        view.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }
    /**************************************************************************/

    /**
     * calculateInSampleSize 计算图片的压缩比率
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * getPictureDegree 获取图片的角度
     */
    public static int getPictureDegree(String imagePath) {
        int i = 0;
        try {
            ExifInterface localExifInterface = new ExifInterface(imagePath);
            int j = localExifInterface.getAttributeInt("Orientation", 1);
            switch (j) {
                case 6:
                    i = 90;
                    break;
                case 3:
                    i = 180;
                    break;
                case 8:
                    i = 270;
                case 4:
                case 5:
                case 7:
                default:
                    break;
            }
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
        return i;
    }


    /**
     * rotaingImageView 旋转图片
     */
    public static Bitmap rotaingImageView(int paramInt, Bitmap paramBitmap) {
        Matrix localMatrix = new Matrix();
        localMatrix.postRotate(paramInt);
        return Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
    }

    /**
     * decodeScaleImage 加载图片并压缩
     */
    public static Bitmap decodeScaleImage(String imagePath, int outWidth, int outHeight) {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, localOptions);
        int i = calculateInSampleSize(localOptions, outWidth, outHeight);
        localOptions.inSampleSize = i;
        localOptions.inJustDecodeBounds = false;
        Bitmap localBitmap1 = BitmapFactory.decodeFile(imagePath, localOptions);
        int j = getPictureDegree(imagePath);
        Bitmap localBitmap2 = null;
        if ((localBitmap1 != null) && (j != 0)) {
            localBitmap2 = rotaingImageView(j, localBitmap1);
            localBitmap1.recycle();
            localBitmap1 = null;
            return localBitmap2;
        }
        return localBitmap1;
    }

    /**
     * getRoundedCornerBitmap 获取圆角图片
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * compressImage 质量压缩
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {
            options -= 10;
            if (options > 0) {
                baos.reset();
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * compressFixBitmap 固定大小压缩
     */
    public static Bitmap compressFixBitmap(Bitmap bitMap, int outWidth, int outHeight) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        float scaleWidth = ((float) outWidth) / width;
        float scaleHeight = ((float) outHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
    }

    /****************************************************************************************/


    /**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     */
    public String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    /****************************************************************************************/


    /**
     * drawable转bitmap
     *
     * @param drawable drawable对象
     * @return bitmap对象
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        return drawable == null ? null : ((BitmapDrawable) drawable).getBitmap();
    }

    /**
     * bitmap转drawable
     *
     * @param res    resources对象
     * @param bitmap bitmap对象
     * @return drawable对象
     */
    public static Drawable bitmap2Drawable(Resources res, Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(res, bitmap);
    }

    /**
     * drawable转byteArr
     *
     * @param drawable drawable对象
     * @param format   格式
     * @return 字节数组
     */
    public static byte[] drawable2Bytes(Drawable drawable, Bitmap.CompressFormat format) {
        return bitmap2Bytes(drawable2Bitmap(drawable), format);
    }

    /**
     * byteArr转drawable
     *
     * @param res   resources对象
     * @param bytes 字节数组
     * @return drawable对象
     */
    public static Drawable bytes2Drawable(Resources res, byte[] bytes) {
        return bitmap2Drawable(res, bytes2Bitmap(bytes));
    }


    /**
     * bitmap转byteArr
     *
     * @param bitmap bitmap对象
     * @param format 格式
     * @return 字节数组
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    /**
     * byteArr转bitmap
     *
     * @param bytes 字节数组
     * @return bitmap对象
     */
    public static Bitmap bytes2Bitmap(byte[] bytes) {
        return (bytes == null || bytes.length == 0) ? null : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    /**
     * 获取bitmap
     *
     * @param context 上下文
     * @param resId   资源id
     * @return bitmap
     */
    public static Bitmap getBitmap(Context context, int resId) {
        if (context == null) return null;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is);
    }

    /**
     * 缩放图片
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return 缩放后的图片
     */
    public static Bitmap scale(Bitmap src, int newWidth, int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /**
     * 缩放图片
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放后的图片
     */
    public static Bitmap scale(Bitmap src, int newWidth, int newHeight, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 缩放图片
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @return 缩放后的图片
     */
    public static Bitmap scale(Bitmap src, float scaleWidth, float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    /**
     * 缩放图片
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @param recycle     是否回收
     * @return 缩放后的图片
     */
    public static Bitmap scale(Bitmap src, float scaleWidth, float scaleHeight, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 裁剪图片
     *
     * @param src    源图片
     * @param x      开始坐标x
     * @param y      开始坐标y
     * @param width  裁剪宽度
     * @param height 裁剪高度
     * @return 裁剪后的图片
     */
    public static Bitmap clip(Bitmap src, int x, int y, int width, int height) {
        return clip(src, x, y, width, height, false);
    }

    /**
     * 裁剪图片
     *
     * @param src     源图片
     * @param x       开始坐标x
     * @param y       开始坐标y
     * @param width   裁剪宽度
     * @param height  裁剪高度
     * @param recycle 是否回收
     * @return 裁剪后的图片
     */
    public static Bitmap clip(Bitmap src, int x, int y, int width, int height, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createBitmap(src, x, y, width, height);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 倾斜图片
     *
     * @param src 源图片
     * @param kx  倾斜因子x
     * @param ky  倾斜因子y
     * @return 倾斜后的图片
     */
    public static Bitmap skew(Bitmap src, float kx, float ky) {
        return skew(src, kx, ky, 0, 0, false);
    }

    /**
     * 倾斜图片
     *
     * @param src     源图片
     * @param kx      倾斜因子x
     * @param ky      倾斜因子y
     * @param recycle 是否回收
     * @return 倾斜后的图片
     */
    public static Bitmap skew(Bitmap src, float kx, float ky, boolean recycle) {
        return skew(src, kx, ky, 0, 0, recycle);
    }

    /**
     * 倾斜图片
     *
     * @param src 源图片
     * @param kx  倾斜因子x
     * @param ky  倾斜因子y
     * @param px  平移因子x
     * @param py  平移因子y
     * @return 倾斜后的图片
     */
    public static Bitmap skew(Bitmap src, float kx, float ky, float px, float py) {
        return skew(src, kx, ky, 0, 0, false);
    }

    /**
     * 倾斜图片
     *
     * @param src     源图片
     * @param kx      倾斜因子x
     * @param ky      倾斜因子y
     * @param px      平移因子x
     * @param py      平移因子y
     * @param recycle 是否回收
     * @return 倾斜后的图片
     */
    public static Bitmap skew(Bitmap src, float kx, float ky, float px, float py, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Matrix matrix = new Matrix();
        matrix.setSkew(kx, ky, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 旋转图片
     *
     * @param src     源图片
     * @param degrees 旋转角度
     * @param px      旋转点横坐标
     * @param py      旋转点纵坐标
     * @return 旋转后的图片
     */
    public static Bitmap rotate(Bitmap src, int degrees, float px, float py) {
        return rotate(src, degrees, px, py, false);
    }

    /**
     * 旋转图片
     *
     * @param src     源图片
     * @param degrees 旋转角度
     * @param px      旋转点横坐标
     * @param py      旋转点纵坐标
     * @param recycle 是否回收
     * @return 旋转后的图片
     */
    public static Bitmap rotate(Bitmap src, int degrees, float px, float py, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        if (degrees == 0) return src;
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 判断bitmap对象是否为空
     *
     * @param src 源图片
     * @return {@code true}: 是<br>{@code false}: 否
     */
    private static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }


    /******************************~~~~~~~~~ 下方和压缩有关 ~~~~~~~~~******************************/

    /**
     * 按缩放压缩
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return 缩放压缩后的图片
     */
    public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /**
     * 按缩放压缩
     *
     * @param src       源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放压缩后的图片
     */
    public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight, boolean recycle) {
        return scale(src, newWidth, newHeight, recycle);
    }

    /**
     * 按缩放压缩
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @return 缩放压缩后的图片
     */
    public static Bitmap compressByScale(Bitmap src, float scaleWidth, float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    /**
     * 按缩放压缩
     *
     * @param src         源图片
     * @param scaleWidth  缩放宽度倍数
     * @param scaleHeight 缩放高度倍数
     * @param recycle     是否回收
     * @return 缩放压缩后的图片
     */
    public static Bitmap compressByScale(Bitmap src, float scaleWidth, float scaleHeight, boolean recycle) {
        return scale(src, scaleWidth, scaleHeight, recycle);
    }

    /**
     * 按质量压缩
     *
     * @param src     源图片
     * @param quality 质量
     * @return 质量压缩后的图片
     */
    public static Bitmap compressByQuality(Bitmap src, int quality) {
        return compressByQuality(src, quality, false);
    }

    /**
     * 按质量压缩
     *
     * @param src     源图片
     * @param quality 质量
     * @param recycle 是否回收
     * @return 质量压缩后的图片
     */
    public static Bitmap compressByQuality(Bitmap src, int quality, boolean recycle) {
        if (isEmptyBitmap(src) || quality < 0 || quality > 100) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 按质量压缩
     *
     * @param src         源图片
     * @param maxByteSize 允许最大值字节数
     * @return 质量压缩压缩过的图片
     */
    public static Bitmap compressByQuality(Bitmap src, long maxByteSize) {
        return compressByQuality(src, maxByteSize, false);
    }

    /**
     * 按质量压缩
     *
     * @param src         源图片
     * @param maxByteSize 允许最大值字节数
     * @param recycle     是否回收
     * @return 质量压缩压缩过的图片
     */
    public static Bitmap compressByQuality(Bitmap src, long maxByteSize, boolean recycle) {
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > maxByteSize && quality >= 0) {
            baos.reset();
            src.compress(Bitmap.CompressFormat.JPEG, quality -= 5, baos);
        }
        if (quality < 0) return null;
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

    /**
     * 按采样大小压缩
     *
     * @param src        源图片
     * @param sampleSize 采样率大小
     * @return 按采样率压缩后的图片
     */
    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize) {
        return compressBySampleSize(src, sampleSize, false);
    }

    /**
     * 按采样大小压缩
     *
     * @param src        源图片
     * @param sampleSize 采样率大小
     * @param recycle    是否回收
     * @return 按采样率压缩后的图片
     */
    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }




/**************************************************************************/

    /**
     * 异步下载图片的回调接口
     */
    public interface OnImageUtilsListener {
        void onImageSuccess(String url, Bitmap bitmap);

        void onImageFailure(String url);
    }


}
