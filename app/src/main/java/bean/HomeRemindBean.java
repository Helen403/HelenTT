package bean;

import java.util.ArrayList;

/**
 * Created by Helen on 2017/5/25.
 */
public class HomeRemindBean {
    public boolean exp;
    public ArrayList<HomeMainItemBean> data;
    public String title;


    public HomeRemindBean(boolean exp, ArrayList<HomeMainItemBean> data, String title) {
        this.exp = exp;
        this.title = title;
        this.data = data;
    }

}
