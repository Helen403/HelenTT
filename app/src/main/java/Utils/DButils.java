package Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import base.HBaseApplication;


/**
 * 向外界推荐使用的方法
 * <p/>
 * 往SQL,SharedPreferences中插入数据
 * public static String get(String url)             -------没有就返回null
 * 往SQL,SharedPreferences中插入数据
 * public static void put(String url, String value)
 * 删除SQL数据库,SharedPreferences的文件
 * public static void delect()
 * <p/>
 * ***************************************************
 * 具体方法的细分
 * 以url为key插入String 用：SQL
 * public static void insertStringBySql(String url, String value)
 * 根据url查询String 用：SQL
 * public static String queryStringBySql(String url)   -------没有就返回null
 * 删除数据库  用：SQL
 * public static void delectTableTmpBySQL()
 * <p/>
 * **********************************************************
 * 已url为key插入String 用：SharedPreferences
 * public void putString(String url, String value)
 * 根据url查询String 用：SharedPreferences             -------没有就返回null
 * public static String getString(String key)
 * 删除数据库  用：SharedPreferences
 * public static void clear()
 * <p/>
 * ***********************************************************************
 * ---------------------------存储到另一张表格  数据不会改变的  提供的 增 删 查 的方法
 * 以url为key插入String 用：SQL
 * public static void insertStringForeverBySql(String url, String value)
 * 根据url查询String 用：SQL
 * public static String queryStringForeverBySql(String url)   -------没有就返回null
 * 删除数据库  用：SQL
 * public static void delectTableForeverBySQL()
 */

public final class DButils {
    //需要配置一下Context
    private static Context context = HBaseApplication.context;
    //数据库名                  ---------根据需求项目名改
    private static final String DB_NAME = "helen_db.db";

    //数据库文件存放在SD卡
//    private static String DB_PATH_SD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/project_helen/db/";
    //数据库文件存放在默认位置
    private static String DB_PATH_LOCATION = "/data/data/" + context.getPackageName() + "/databases/";

    /********************************************************************/

    //数据库表名                 ----------没什么必要不需要改
    private static final String TABLE_NAME = "helen_table";

    //数据库名                  ---------用来存储都不会变的数据
    private static final String TABLE_NAME_FOREVER = "helen_table_forever";


    //SharedPreferences的文件名 ----------没什么必要不需要改
    private static final String FILE_NAME = "sharedpreferencename";


    /*********************************************************************/

    private static MyDBHelper mdbHelper = null;

    private static SharedPreferences share = null;

    private DButils() {
    }


    static {
        if (mdbHelper == null) {
            synchronized (DButils.class) {
                if (mdbHelper == null) {
                    mdbHelper = new MyDBHelper(context);
                    share = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
                }
            }
        }
    }


    /**
     * 已url为key插入String  定时 用：SQL
     */
    public static void insertStringBySql(String url, String value) {
        if (!url.isEmpty() && !value.isEmpty()) {
            SQLiteDatabase db = mdbHelper.getWritableDatabase();
            db.execSQL("insert into " + TABLE_NAME + "(url,value) values(?,?)", new Object[]{url, value});
            mdbHelper.close();
            db.close();
        }
    }

    /**
     * 根据url查询String 定时 用：SQL
     */
    public static String queryStringBySql(String url) {
        SQLiteDatabase db = mdbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where url=?", new String[]{url});
        String value = null;
        // 迭代游标
        if (cursor != null) {
            while (cursor.moveToNext()) {
                value = cursor.getString(cursor.getColumnIndex("value"));
            }
        }
        mdbHelper.close();
        db.close();
        assert cursor != null;
        cursor.close();
        return value;
    }

    /**
     * 删除数据库  定时  用：SQL
     */
    public static void delectTableTmpBySQL() {
        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        db.execSQL("drop table " + TABLE_NAME);
        //这张表用来存储定时的数据
        db.execSQL("create table " + TABLE_NAME + "(_id integer primary key autoincrement ,url text,value text)");
        mdbHelper.close();
        db.close();
    }


    /**
     * 已url为key插入String 永恒  用：SQL
     */
    public static void insertStringForeverBySql(String url, String value) {
        if (!url.isEmpty() && !value.isEmpty()) {
            SQLiteDatabase db = mdbHelper.getWritableDatabase();
            db.execSQL("insert into " + TABLE_NAME_FOREVER + "(url,value) values(?,?)", new Object[]{String.valueOf(url.hashCode()), value});
            mdbHelper.close();
            db.close();
        }
    }

    /**
     * 根据url查询String 永恒  用：SQL
     */
    public static String queryStringForeverBySql(String url) {
        SQLiteDatabase db = mdbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME_FOREVER + " where url=?", new String[]{String.valueOf(url.hashCode())});
        String value = null;
        // 迭代游标
        if (cursor != null) {
            while (cursor.moveToNext()) {
                value = cursor.getString(cursor.getColumnIndex("value"));
            }
        }
        mdbHelper.close();
        db.close();
        assert cursor != null;
        cursor.close();
        return value;
    }

    /**
     * 删除数据库  永恒   用：SQL
     */
    public static void delectTableForeverBySQL() {
        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        db.execSQL("drop table " + TABLE_NAME_FOREVER);
        //这张表用来存储不变的数据
        db.execSQL("create table " + TABLE_NAME_FOREVER + "(_id integer primary key autoincrement ,url text,value text)");
        mdbHelper.close();
        db.close();
    }


    /*********************************************************************/
    /**
     * 已url为key插入String 用：SharedPreferences
     */
    public static void putString(String url, String value) {
        if (!url.isEmpty() && !value.isEmpty()) {
            SharedPreferences.Editor editor = share.edit();
            editor.putString(url, value);
            editor.apply();
        }
    }

    /**
     * 根据url查询String 用：SharedPreferences
     */
    public static String getString(String url) {
        return share.getString(url, null);
    }

    /**
     * 删除数据库  用：SharedPreferences
     */
    public static void clear() {
        SharedPreferences.Editor editor = share.edit();
        editor.clear();
        editor.apply();
    }

    /*****************************************************************/
    /**
     * 往SQL,SharedPreferences中提出数据
     */
    public static String get(String url) {
//        String result = getString(url);
//        if (result == null) {
//            result = queryStringBySql(url);
//        }
//
//        if (result == null){
//            result = "";
//        }
        ACache aCache = ACache.get(context);

        String value = aCache.getAsString(url);
        if (value == null) {
            value = "";
        }

        return value;
    }

    /**
     * 往SQL,SharedPreferences中插入数据
     */
    public static void put(String url, String value) {
//        putString(url, value);
//        insertStringBySql(url, value);
        ACache mCache = ACache.get(context);
        if (TextUtils.isEmpty(value)){
            value = "";
        }
        mCache.put(url, value, 2 * ACache.TIME_DAY);//保存两天，如果超过两天去获取这个key，将为null
    }

    /**
     * 删除SQL数据库,SharedPreferences的文件
     */
    public static void delect() {
        delectTableTmpBySQL();
        clear();
    }

    /****************************************************************/

    static class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context) {
            super(context, DB_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //这张表用来存储定时的数据
            db.execSQL("create table " + TABLE_NAME + "(_id integer primary key autoincrement ,url text,value text)");
            //这张表用来存储固定不变的数据
            db.execSQL("create table " + TABLE_NAME_FOREVER + "(_id integer primary key autoincrement ,url text,value text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table " + TABLE_NAME);
            db.execSQL("drop table " + TABLE_NAME_FOREVER);
            onCreate(db);
        }
    }
}
