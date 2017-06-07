package bean;

/**
 * Created by SNOY on 2017/5/13.
 */
public class MessageEvent<T> {
    private int type;
    private T data;

    public MessageEvent(int type, T data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
