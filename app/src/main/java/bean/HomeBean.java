package bean;

/**
 * Created by Helen on 2017/5/25.
 */
public class HomeBean {


   /**
    * remindCode : 35
    * userCode : 2
    * remindName : 太可怜了
    * remindTime : 2017-06-07 15:23
    * remindMode : 铃声
    * remindType : 起床
    * repeatType : 一天2小时
    * isRepeatRemind : 0
    */

   private int remindCode;
   private int userCode;
   private String remindName;
   private String remindTime;
   private String remindMode;
   private String remindType;
   private String repeatType;
   private int isRepeatRemind;

   public int getRemindCode() {
      return remindCode;
   }

   public void setRemindCode(int remindCode) {
      this.remindCode = remindCode;
   }

   public int getUserCode() {
      return userCode;
   }

   public void setUserCode(int userCode) {
      this.userCode = userCode;
   }

   public String getRemindName() {
      return remindName;
   }

   public void setRemindName(String remindName) {
      this.remindName = remindName;
   }

   public String getRemindTime() {
      return remindTime;
   }

   public void setRemindTime(String remindTime) {
      this.remindTime = remindTime;
   }

   public String getRemindMode() {
      return remindMode;
   }

   public void setRemindMode(String remindMode) {
      this.remindMode = remindMode;
   }

   public String getRemindType() {
      return remindType;
   }

   public void setRemindType(String remindType) {
      this.remindType = remindType;
   }

   public String getRepeatType() {
      return repeatType;
   }

   public void setRepeatType(String repeatType) {
      this.repeatType = repeatType;
   }

   public int getIsRepeatRemind() {
      return isRepeatRemind;
   }

   public void setIsRepeatRemind(int isRepeatRemind) {
      this.isRepeatRemind = isRepeatRemind;
   }
}
