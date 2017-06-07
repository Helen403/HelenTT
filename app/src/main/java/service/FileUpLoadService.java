package service;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;

import HConstants.HConstants;
import Utils.HHttpUtils;
import sun.misc.BASE64Encoder;

/**
 * 文件上传服务
 *
 * @author 徐春福
 */
public class FileUpLoadService {
    private String fileName;
    private String filePath;
    private String fileType;
    private Context context;

    public FileUpLoadService(Context context, String fileName, String filePath, String fileType) {
        super();
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.context = context;
    }

    public void upload() {
        Thread thread = new Thread(new FileUpLoadThread());
        thread.start();
    }

    private class FileUpLoadThread implements Runnable {
        public void run() {
            try {
                // ------------------------------------------------------------------------------------
                String fileContent = convertFileToString(filePath);
                HashMap<String, String> paramsUpFileMap = new HashMap<String, String>();
                Log.d("Helen", fileName);
                paramsUpFileMap.put("fileName", fileName);
                paramsUpFileMap.put("content", fileContent);
                paramsUpFileMap.put("fileType", fileType);
                paramsUpFileMap.put("userCode", "4");
                Log.d("Helen", HConstants.URL.uploadFile);
                HHttpUtils.posts(HConstants.URL.uploadFile, paramsUpFileMap, new HHttpUtils.OnHttpUtilsResultListener() {

                    @Override
                    public void onHttpSuccess(String url, String result) {
                        Log.d("Helen", result);
                    }

                    @Override
                    public void onHttpFailure(String url) {
                        Log.d("Helen", "失败");
                    }

                });


            } catch (Exception ex) {
                //Log.e(this.getClass().getSimpleName(), "上传文件异常");
                ex.printStackTrace();
            }
        }

        /**
         * 文件转化字符流
         */
        public String convertFileToString(String filePathName) {
            String strFileContent = "";
            try {
                FileInputStream fis = new FileInputStream(filePathName);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = fis.read(buffer)) >= 0) {
                    baos.write(buffer, 0, count);
                }
                BASE64Encoder encorder = new BASE64Encoder();
                // 用Base64将字节流转化为字符流
                strFileContent = new String(encorder.encode(baos.toByteArray()));
                Log.i("Helen", "文件转化字符流成功.....");
                fis.close();
            } catch (Exception ex) {
                Log.i("Helen", "文件转化字符流失败.....");
            }
            return strFileContent;
        }
    }
}
