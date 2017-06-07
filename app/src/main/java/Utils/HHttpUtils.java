package Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * GET请求获取数据
 * public static void gets(final String url,  final OnHttpUtilsResultListener onHttpUtilsResultListener)
 * POST请求获取数据
 * public static void posts(final String url, final HashMap<String, String> map, final OnHttpUtilsResultListener onHttpUtilsResultListener)
 * GET请求图片
 * public static void getBitmapByUrl(final String url, final OnHttpUtilsBitmapListener onHttpUtilsBitmapListener)
 */

public final class HHttpUtils {


    // 线程池的数量  默认2条
    private static final int ThreadPoolNum = 2;

    /******************************************************************************/
    private HHttpUtils() {
    }

    /**
     * GET请求
     */
    private static byte[] getByteInfoByUrl(String url) {
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
     * GET请求获取数据
     */
    private static String getResultByGET(String url, Map<String, String> map) {
        StringBuilder urlTmp = new StringBuilder();
        urlTmp.append(url);
        if (map != null && map.size() > 0) {
            StringBuilder params = new StringBuilder();
            params.append("?");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Object key = entry.getKey();
                Object val = entry.getValue();
                params.append(key).append("=").append(val).append("&");
            }
            params.deleteCharAt(params.length() - 1);
            urlTmp.append(params);
        }
        byte[] buffer = getByteInfoByUrl(urlTmp.toString());
        if (buffer != null) {
            try {
                return new String(buffer, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * POST请求获取数据
     */
    private static String getResultByPOST(String url, Map<String, String> map) {

        HttpURLConnection conn = null;
        ByteArrayOutputStream out = null;
        InputStream in = null;
        StringBuilder params = new StringBuilder();
        try {
            if (map != null && map.size() > 0) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    params.append(key).append("=").append(val).append("&");
                }
                params.deleteCharAt(params.length() - 1);
            }
           // Log.d("Helen",params+"");


            URL ul = new URL(url);
            conn = (HttpURLConnection) ul.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);// 是否输入参数

            byte[] bypes = params.toString().getBytes();
            conn.getOutputStream().write(bypes);// 输入参数
            in = conn.getInputStream();
            out = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[1024 * 2];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            byte[] buffert = out.toByteArray();
            return new String(buffert, "UTF-8");
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
    private static Bitmap getBitmapByUrl(String url) {
        Bitmap bitmap = null;
        byte[] buffer = getByteInfoByUrl(url);
        if (buffer != null) {
            bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        }
        return bitmap;
    }


    /************************************************************/

    /**
     * 下载Image的线程池
     */
    private static ExecutorService mImageThreadPool = null;

    /**
     * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
     */
    private static ExecutorService getThreadPool() {
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

    /**********************************************************/

    //用来返回UI线程
    private static Handler handler = new Handler();

    /**
     * GET网络请求
     */
    public static void gets(final String url, final HashMap<String, String> map, final OnHttpUtilsResultListener onHttpUtilsResultListener) {
        getThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                final String result = getResultByGET(url, map);
                if (!TextUtils.isEmpty(result)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onHttpUtilsResultListener.onHttpSuccess(url, result);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //下载失败
                            onHttpUtilsResultListener.onHttpFailure(url);
                        }
                    });
                }
            }
        });
    }


    /**
     * POST网络请求
     */
    public static void posts(final String url, final HashMap<String, String> map, final OnHttpUtilsResultListener onHttpUtilsResultListener) {
        getThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                final String result = getResultByPOST(url, map);
                if (!TextUtils.isEmpty(result)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onHttpUtilsResultListener.onHttpSuccess(url, result);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //下载失败
                            onHttpUtilsResultListener.onHttpFailure(url);
                        }
                    });
                }
            }
        });
    }

    /**
     * GET请求图片
     */
    public static void getBitmapByUrl(final String url, final OnHttpUtilsBitmapListener onHttpUtilsBitmapListener) {
        getThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                final Bitmap bitmap = getBitmapByUrl(url);
                if (bitmap != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onHttpUtilsBitmapListener.onImageSuccess(url, bitmap);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //下载失败
                            onHttpUtilsBitmapListener.onImageFailure(url);
                        }
                    });

                }
            }
        });
    }

    /************************************************************************/

    /**
     * 异步下载图片的回调接口
     */
    public interface OnHttpUtilsBitmapListener {
        void onImageSuccess(String url, Bitmap bitmap);
        void onImageFailure(String url);
    }


    /**
     * 异步下载数据的回调接口
     */
    public interface OnHttpUtilsResultListener {
        void onHttpSuccess(String url, String result);
        void onHttpFailure(String url);
    }

    /*********************************************************************************/

    /*
            //添加证书 https请求时
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());



    private static class MyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class MyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

//********************************************************************************

            //添加cooker
            CookieManager manager = new java.net.CookieManager();
            manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(manager);

             CookieStore store =  manager.getCookieStore();
             List<HttpCookie> cookies = store.getCookies();



     */
}
