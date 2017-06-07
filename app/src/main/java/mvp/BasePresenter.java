package mvp;

/**
 * 类描述：
 * 创建人：qzy
 * 创建时间：2016/7/19. 18:46
 * 版本：
 */
public abstract class BasePresenter<T> {
    public T mView;
    public void attach(T mView) {
        this.mView = mView;
    }

    public void dettach() {
        mView = null;
    }
}
