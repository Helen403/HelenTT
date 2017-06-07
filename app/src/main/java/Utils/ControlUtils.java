package Utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.HBaseApplication;
import okhttp3.Call;


/**
 * 向外界提供的方法
 * get请求数据并转化
 * public static <T, E> void gets(String url, final E e, final Class<T> cls, final OnControlUtilsListener<T> onControlUtilsListener)
 * post请求数据并转化
 * public static <T, E> void posts(String url, final E e, final Class<T> cls, final OnControlUtilsListener onControlUtilsListener)
 * ***********************************************************************
 * 参数方面请参考demo
 * <p/>
 * <p/>
 * ***********************************************************************
 * ---------------------------存储到另一张表格  数据不会改变的
 * get请求数据并转化
 * public static <T, E> void getsForever(String url, final E e, final Class<T> cls, final OnControlUtilsListener<T> onControlUtilsListener)
 * post请求数据并转化
 * public static <T, E> void postsForever(String url, final E e, final Class<T> cls, final OnControlUtilsListener onControlUtilsListener)
 * ----------------------------不存储到数据库  每一次都请求服务器
 * get请求数据并转化
 * public static <T, E> void getsEveryTime(String url, final E e, final Class<T> cls, final OnControlUtilsListener<T> onControlUtilsListener)
 * post请求数据并转化
 * public static <T, E> void postsEveryTime(String url, final E e, final Class<T> cls, final OnControlUtilsListener onControlUtilsListener)
 */
public final class ControlUtils {

    //配置
    private static Context contextApplication = HBaseApplication.context;


    //是否为本地接口模式  flag为true为本地接口模式  暂时只在post请求中使用
    private static Boolean flag = true;


    //下一页
    int page = 1;
    int row = 10;


    /**
     * 单位是秒  60 * 60 * 12 半日  测试为30秒         --------根据项目需求来自己修改
     * 时间超过规定的时间就删除数据库的数据  图片的数据就不删除  就删除json数据
     * 只是删除临时的表单
     * 最好的办法就是与后台沟通  后台每次修改数据就发个请求判断  根据判断来删除
     */
    private final static int upDataTime = 30;

    private static Gson gson = null;


    private ControlUtils() {
    }

    static {
        if (gson == null) {
            synchronized (ControlUtils.class) {
                if (gson == null) {
                    gson = new Gson();
                    upData();
                }
            }
        }
    }

    /*****************************************************************/


    /**
     * 时间超过规定的时间就删除数据库的数据  图片的数据就不删除  就删除json数据
     * 扩展：做一些定时操作
     */
    private static void upData() {
        if (getBooleanTime()) {
            DButils.delect();
        }
    }


    /**
     * 保存当前时间  获取上次的时间  并返回是否要删除数据库
     */
    private static boolean getBooleanTime() {
        //获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String time = formatter.format(curDate);
        String lastTime = DButils.get("time");
        Boolean UpFlag;
        if (lastTime == null) {
            //存储当前时间
            DButils.put("time", time);
            return false;
        }
        //判读时间差
        UpFlag = difference(time, lastTime);
        //存储当前时间
        DButils.put("time", time);
        return UpFlag;
    }

    /**
     * 判断当前时间和上一次时间之间的差是否大于规定的时间 大于返回true 小于就返回false
     */
    private static boolean difference(String time, String lastTime) {
        boolean result = false;
        if (!lastTime.isEmpty()) {
            try {
                java.util.Date nowdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
                java.util.Date setdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastTime);
                long between = (nowdate.getTime() - setdate.getTime());
                result = between > 1000 * upDataTime;
            } catch (Exception e) {
            }
        }
        return result;
    }


    /*****************************************************************/


    /**
     * json转Bean
     */
    public static <T> T getEntityFromJson(String gsonString, Class<T> cls) {
        T t = null;
        try {
            if (gson != null && !TextUtils.isEmpty(gsonString)) {
                //过滤gson
                gsonString = gsonString.trim();
                if (gsonString.startsWith("ufeff")) {
                    gsonString = gsonString.substring(1);
                }
                t = gson.fromJson(gsonString, cls);
            }
            return t;
        } catch (Exception e) {
        }
        return null;
    }


//    public static <T> List<T> getListFromJson(String s, Class<T[]> clazz) {
//        T[] arr = new Gson().fromJson(s, clazz);
//        return Arrays.asList(arr);
//    }

    /**
     * @param json
     * @param clazz
     * @return
     */
//    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz)
//    {
//        Type type = new TypeToken<ArrayList<JsonObject>>()
//        {}.getType();
//        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);
//
//        ArrayList<T> arrayList = new ArrayList<>();
//        for (JsonObject jsonObject : jsonObjects)
//        {
//            arrayList.add(new Gson().fromJson(jsonObject, clazz));
//        }
//        return arrayList;
//    }


    /**
     * json转成ArrayList
     */
    public static <T> ArrayList<T> getListFromJson(String gsonString, Class<T> tClass) {
        ArrayList<T> list;
        try {
            if (gson != null && !TextUtils.isEmpty(gsonString)) {
                //过滤gson
                gsonString = gsonString.trim();
                if (gsonString.startsWith("ufeff")) {
                    gsonString = gsonString.substring(1);
                }
                TypeToken<ArrayList<T>> tt = new TypeToken<ArrayList<T>>() {
                };
                list = gson.fromJson(gsonString, tt.getType());
                return list;
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 对象转成Json
     */
    private static <T> String getJsonFromEntity(T t) {
        String result = "";
        if (gson != null && t != null) {
            result = gson.toJson(t);
        }
        return result;
    }


    /**
     * Json转成map的
     */
    private static <T> HashMap<String, T> GsonToMaps(String gsonString) {
        HashMap<String, T> map = null;
        if (gson != null && !TextUtils.isEmpty(gsonString)) {
            map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
            }.getType());
        }
        return map;
    }

    /**
     * 集合转成Json
     */
    private static <T> String getJsonFromList(List<T> t) {
        String result = "";
        if (gson != null && t != null) {
            result = gson.toJson(t);
        }
        return result;
    }

    /**
     * map转成Json
     */
    private static String getJsonFromMap(HashMap<String, String> map) {
        String result = "";
        if (gson != null && map != null) {
            result = gson.toJson(map);
        }
        return result;
    }


    /******************************************************************/

    /**
     * get请求数据并转化
     */
    public static <T> void gets(String url, HashMap<String, String> map, final Class<T> cls, final OnControlUtils<T> onControlUtils) {
        final String params = getJsonFromMap(map);
        //先从数据库中获取如果没有再请求
        String tmp = DButils.get(url + params);
        if (tmp != null) {
            onSuccess(url, cls, onControlUtils, tmp);
            return;
        }
        HHttpUtils.gets(url, map, new HHttpUtils.OnHttpUtilsResultListener() {
            @Override
            public void onHttpSuccess(String url, String result) {
                onSuccess(url, cls, onControlUtils, result);
                //数据成功返回就存储到数据库
                DButils.put(url + params, result);
            }

            @Override
            public void onHttpFailure(String url) {
                onFailure(url, onControlUtils);
            }
        });
    }


    /**
     * post请求数据并转化
     */
    public static <T> void posts(String url, HashMap<String, String> map, final Class<T> cls, final OnControlUtils<T> onControlUtils) {
        if (flag) {
            String result = getJsonByAssets(cls);
            onSuccess(url, cls, onControlUtils, result);
            return;
        }

        final String params = getJsonFromMap(map);
        //先从数据库中获取如果没有再请求
        String tmp = DButils.get(url + params);
        if (tmp != null) {
            onSuccess(url, cls, onControlUtils, tmp);
            return;
        }
        HHttpUtils.posts(url, map, new HHttpUtils.OnHttpUtilsResultListener() {
            @Override
            public void onHttpSuccess(String url, String result) {
                onSuccess(url, cls, onControlUtils, result);
                //数据成功返回就存储到数据库
                DButils.put(url + params, result);
            }

            @Override
            public void onHttpFailure(String url) {
                onFailure(url, onControlUtils);
            }
        });
    }

    /**
     * get请求数据并转化    缓存到不变的表
     */
    public static <T> void getsForever(String url, HashMap<String, String> map, final Class<T> cls, final OnControlUtils<T> onControlUtils) {
        final String params = getJsonFromMap(map);
        //先从数据库中获取如果没有再请求
        String tmp = DButils.queryStringForeverBySql(url + params);
        if (tmp != null) {
            onSuccess(url, cls, onControlUtils, tmp);
            return;
        }
        HHttpUtils.gets(url, map, new HHttpUtils.OnHttpUtilsResultListener() {
            @Override
            public void onHttpSuccess(String url, String result) {
                onSuccess(url, cls, onControlUtils, result);
                //数据成功返回就存储到数据库
                DButils.insertStringForeverBySql(url + params, result);
            }

            @Override
            public void onHttpFailure(String url) {
                onFailure(url, onControlUtils);
            }
        });
    }


    /**
     * post请求数据并转化   缓存到不变的表
     */
    public static <T> void postsForever(String url, HashMap<String, String> map, final Class<T> cls, final OnControlUtils<T> onControlUtils) {
        final String params = getJsonFromMap(map);
        //先从数据库中获取如果没有再请求
        String tmp = DButils.queryStringForeverBySql(url + params);
        if (tmp != null) {
            onSuccess(url, cls, onControlUtils, tmp);
            return;
        }
        HHttpUtils.posts(url, map, new HHttpUtils.OnHttpUtilsResultListener() {
            @Override
            public void onHttpSuccess(String url, String result) {
                onSuccess(url, cls, onControlUtils, result);
                //数据成功返回就存储到数据库
                DButils.insertStringForeverBySql(url + params, result);
            }

            @Override
            public void onHttpFailure(String url) {
                onFailure(url, onControlUtils);
            }
        });
    }


    /**
     * get请求数据并转化
     */
    public static <T> void getsEveryTime(final String url, HashMap<String, String> map, final Class<T> cls, final OnControlUtils<T> onControlUtils) {
        String urlTmp = appendString(url, map);
        Log.d("Helen",urlTmp);
        OkHttpUtils
                .get()
                .url(urlTmp).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                onFailure(url, onControlUtils);
            }

            @Override
            public void onResponse(String response, int id) {
                onSuccess(url, cls, onControlUtils, response);
            }
        });
//        HttpUtils.gets(url, map, new HttpUtils.OnHttpUtilsResultListener() {
//            @Override
//            public void onHttpSuccess(String url, String result) {
//                onSuccess(url, cls, onControlUtils, result);
//            }
//
//            @Override
//            public void onHttpFailure(String url) {
//                onFailure(url, onControlUtils);
//            }
//        });
    }


    public static String appendString(String url, HashMap<String, String> map) {
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
        return urlTmp.toString();
    }


    /**
     * post请求数据并转化
     */
    public static <T> void postsEveryTime(String url, HashMap<String, String> map, final Class<T> cls, final OnControlUtils<T> onControlUtils) {
        if (flag) {
            String result = getJsonByAssets(cls);
            onSuccess(url, cls, onControlUtils, result);
            return;
        }

        HHttpUtils.posts(url, map, new HHttpUtils.OnHttpUtilsResultListener() {
            @Override
            public void onHttpSuccess(String url, String result) {
                onSuccess(url, cls, onControlUtils, result);
            }

            @Override
            public void onHttpFailure(String url) {
                onFailure(url, onControlUtils);
            }
        });
    }


    /**
     * 为了方便切换网络请求框架而提出来的方法
     */
    private static <T> void onSuccess(String url, Class<T> cls, OnControlUtils<T> onControlUtils, String result) {
        if (cls != null) {
            T entity = getEntityFromJson(result, cls);
            onControlUtils.onSuccess(url, entity, result);
        } else {
            onControlUtils.onFailure(url);
        }
    }

    private static <T> void onFailure(String url, OnControlUtils<T> onControlUtils) {
        onControlUtils.onFailure(url);
    }

    /*********************************************************************/
    //接口模式下数据 打开本地数据
    public static <T> String getJsonByAssets(Class<T> cls) {
        StringBuilder Tmp = new StringBuilder();
        //拼接字符串
        String[] array = cls.toString().split("\\.");
        Tmp.append(array[array.length - 1]).append(".json");
        StringBuffer arr = new StringBuffer();
        try {
            InputStream is = contextApplication.getAssets().open(Tmp.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                arr.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr.toString();
    }

    /*********************************************************************/

//    /**
//     * post请求数据并转化
//     */
//    public static <T, E> void postsEveryTime(String url, final E e, final Class<T> cls, final OnControlUtils<T> onControlUtils) {
//        HashMap<String, String> map = null;
//        String params = "";
//        if (e != null) {
//            params = getJsonFromEntity(e);
//            map = GsonToMaps(params);
//        }
//        HttpUtils.posts(url, map, new HttpUtils.OnHttpUtilsResultListener() {
//            @Override
//            public void onHttpSuccess(String url, String result) {
//                onSuccess(url, cls, onControlUtils, result);
//            }
//
//            @Override
//            public void onHttpFailure(String url) {
//                onFailure(url, onControlUtils);
//            }
//        });
//    }


    /*********************************************************************/

    /**
     * 异步下载数据的回调接口
     */
    public interface OnControlUtils<T> {
        void onSuccess(String url, T t, String result);

        void onFailure(String url);
    }

}
