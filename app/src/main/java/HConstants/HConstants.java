package HConstants;

/**
 * Created by SNOY on 2017/5/13.
 */
public interface HConstants {

    String url = "http://192.168.1.131:8080/timeLang/";
    String url_man = "http://192.168.1.188:8080/timeLang/";

    /**
     * Eventbus 常量
     */
    interface EVENT {
        int HOMEREFRESH = 0;
        int LOGINACTIVITY_CLOSE = 1;
        int NAME_REFRESH = 2;

    }

    interface URL {
        //验证码
        String VER = url + "existsUser";
        //注册
        String Register = url + "saveUser";
        //登录
        String LOGIN = url + "userLogin";

        String Weather = url + "weather";

        String historyToday = url + "historyToday";

        String saveRemind = url_man + "saveRemind";

        String findFriendList = url + "findFriendList";

        String updateRegId = url + "updateRegId";

        String uploadFile = url + "uploadFile";

        String uploadPhoto = url + "uploadPicFile";

        String saveUser = url + "saveUser";

        String updateCancel = url + "updateCancel";


        //上传图片
        String uploadPicFile = url + "uploadPicFile";

        //上传语音
        String uploadAudioFile = url + "uploadAudioFile";

        //修改昵称
        String updateNickName = url + "updateNickName";

        //首页列表
        String findIndexRemind = url + "findIndexRemind";


        //查找朋友
        String findAddFriend = url + "findAddFriend";

        String updateSignName = url + "updateSignName";

    }

    interface KEY {
        String userCode = "userCode";
        String RegId = "regId";
        String nickName = "QQnickName";
        String figureurl = "QQfigureurl";
        String gender = "QQgender";
        String city = "QQcity";
        String QQopenid = "QQopenid";


        //QQ登录就为1 手机登录2 微信为3
        String LoginType = "LoginType";


        String PhoneNum = "PhoneNum";
        String Email = "email";

        String localPhoto = "localPhoto";


        String phone = "Phone";
        String pwd = "pwd";


        //1为登录  0为未登录
        String loginStatus = "loginStatus";

        //
        String userID = "userID";


        String signName = "signName";

        //背景图片
        String bgImg = "bgImg";

    }
}
