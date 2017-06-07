package mvp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 类名称：
 * 类描述：
 * 创建人：quzongyang
 * 创建时间：2016/7/19. 19:00
 * 版本：
 */
public abstract class BaseMvpActivity <V,T extends BasePresenter<V>> extends AppCompatActivity {
    public T presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attach((V)this);
    }

    @Override
    protected void onDestroy() {
        presenter.dettach();
        super.onDestroy();
    }

    // 实例化presenter
    public abstract T initPresenter();
}
