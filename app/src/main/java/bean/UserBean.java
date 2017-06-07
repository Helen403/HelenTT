package bean;

/**
 * Created by Helen on 2017/5/16.
 */
public class UserBean {


    /**
     * userCode : 5
     * userID : 501210085
     * userNickName : åæ¬¢
     * userTelphone : 123
     * userPassword : c4ca4238a0b923820dcc509a6f75849b
     * userRegistDatetime : 2017-06-03 15:54:29
     * userExpireDatetime : 2017-06-04 15:54:29
     * userStatus : 0
     * userActiveCode : 2eadb4aff42f40b8a0a71055eb448ac320170603155429707
     * phoneDeviceCode : 866538021681004
     * phoneDeviceName : Xiaomi MINOTELTE
     * isCheck : 0
     * isThreeLogin : 0
     */

    private int userCode;
    private String userID;
    private String userNickName;
    private String userTelphone;
    private String userPassword;
    private String userRegistDatetime;
    private String userExpireDatetime;
    private int userStatus;
    private String userActiveCode;
    private String phoneDeviceCode;
    private String phoneDeviceName;
    private int isCheck;
    private String isThreeLogin;

    public String getUserSignName() {
        return userSignName;
    }

    public void setUserSignName(String userSignName) {
        this.userSignName = userSignName;
    }

    private String userSignName;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    private String userEmail;

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    private  String userHead;

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserTelphone() {
        return userTelphone;
    }

    public void setUserTelphone(String userTelphone) {
        this.userTelphone = userTelphone;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserRegistDatetime() {
        return userRegistDatetime;
    }

    public void setUserRegistDatetime(String userRegistDatetime) {
        this.userRegistDatetime = userRegistDatetime;
    }

    public String getUserExpireDatetime() {
        return userExpireDatetime;
    }

    public void setUserExpireDatetime(String userExpireDatetime) {
        this.userExpireDatetime = userExpireDatetime;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserActiveCode() {
        return userActiveCode;
    }

    public void setUserActiveCode(String userActiveCode) {
        this.userActiveCode = userActiveCode;
    }

    public String getPhoneDeviceCode() {
        return phoneDeviceCode;
    }

    public void setPhoneDeviceCode(String phoneDeviceCode) {
        this.phoneDeviceCode = phoneDeviceCode;
    }

    public String getPhoneDeviceName() {
        return phoneDeviceName;
    }

    public void setPhoneDeviceName(String phoneDeviceName) {
        this.phoneDeviceName = phoneDeviceName;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public String getIsThreeLogin() {
        return isThreeLogin;
    }

    public void setIsThreeLogin(String isThreeLogin) {
        this.isThreeLogin = isThreeLogin;
    }
}
