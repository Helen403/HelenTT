package bean;

import java.util.List;

/**
 * Created by Helen on 2017/5/26.
 */
public class ComBean {


    /**
     * lsh : 7
     * userCode : 4
     * classCode : 1
     * className : 好友
     * friendList : [{"lsh":1,"classCode":"1","userCode":4,"friendCode":5,"user":{"userCode":4,"userID":"523206168","userNickName":"游客","userTelphone":"1234","userPassword":"81dc9bdb52d04dc20036dbd8313ed055","userRegistDatetime":"2017-05-25 20:00:28","userExpireDatetime":"2017-05-26 20:00:28","userStatus":0,"userActiveCode":"20d76a72e863428d887fb4466de4bea620170525200028689","phoneDeviceCode":"865528022833267","phoneDeviceName":"1","isThreeLogin":"0"}},{"lsh":2,"classCode":"1","userCode":4,"friendCode":6,"user":{"userCode":4,"userID":"523206168","userNickName":"游客","userTelphone":"1234","userPassword":"81dc9bdb52d04dc20036dbd8313ed055","userRegistDatetime":"2017-05-25 20:00:28","userExpireDatetime":"2017-05-26 20:00:28","userStatus":0,"userActiveCode":"20d76a72e863428d887fb4466de4bea620170525200028689","phoneDeviceCode":"865528022833267","phoneDeviceName":"1","isThreeLogin":"0"}}]
     */

    private int lsh;
    private int userCode;
    private String classCode;
    private String className;
    private List<FriendListBean> friendList;

    public int getLsh() {
        return lsh;
    }

    public void setLsh(int lsh) {
        this.lsh = lsh;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<FriendListBean> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<FriendListBean> friendList) {
        this.friendList = friendList;
    }

    public static class FriendListBean {
        /**
         * lsh : 1
         * classCode : 1
         * userCode : 4
         * friendCode : 5
         * user : {"userCode":4,"userID":"523206168","userNickName":"游客","userTelphone":"1234","userPassword":"81dc9bdb52d04dc20036dbd8313ed055","userRegistDatetime":"2017-05-25 20:00:28","userExpireDatetime":"2017-05-26 20:00:28","userStatus":0,"userActiveCode":"20d76a72e863428d887fb4466de4bea620170525200028689","phoneDeviceCode":"865528022833267","phoneDeviceName":"1","isThreeLogin":"0"}
         */

        private int lsh;
        private String classCode;
        private int userCode;
        private int friendCode;
        private UserBean user;

        public int getLsh() {
            return lsh;
        }

        public void setLsh(int lsh) {
            this.lsh = lsh;
        }

        public String getClassCode() {
            return classCode;
        }

        public void setClassCode(String classCode) {
            this.classCode = classCode;
        }

        public int getUserCode() {
            return userCode;
        }

        public void setUserCode(int userCode) {
            this.userCode = userCode;
        }

        public int getFriendCode() {
            return friendCode;
        }

        public void setFriendCode(int friendCode) {
            this.friendCode = friendCode;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class UserBean {
            /**
             * userCode : 4
             * userID : 523206168
             * userNickName : 游客
             * userTelphone : 1234
             * userPassword : 81dc9bdb52d04dc20036dbd8313ed055
             * userRegistDatetime : 2017-05-25 20:00:28
             * userExpireDatetime : 2017-05-26 20:00:28
             * userStatus : 0
             * userActiveCode : 20d76a72e863428d887fb4466de4bea620170525200028689
             * phoneDeviceCode : 865528022833267
             * phoneDeviceName : 1
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
            private String isThreeLogin;
            private String isShow;
            private String pinyinF;

            public String getPinyinF() {
                return pinyinF;
            }

            public void setPinyinF(String pinyinF) {
                this.pinyinF = pinyinF;
            }



            public String getIsShow() {
                return isShow;
            }

            public void setIsShow(String isShow) {
                this.isShow = isShow;
            }



            public String getUserHead() {
                return userHead;
            }

            public void setUserHead(String userHead) {
                this.userHead = userHead;
            }

            private String userHead;

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

            public String getIsThreeLogin() {
                return isThreeLogin;
            }

            public void setIsThreeLogin(String isThreeLogin) {
                this.isThreeLogin = isThreeLogin;
            }
        }
    }
}
