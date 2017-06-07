package Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by mcs on 2015/11/3.
 */
public final class ThreadPoolProxy {

    private ThreadPoolExecutor mExecutor;

    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;

    public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
    }

    public ThreadPoolExecutor initThreadPoolExecutor() {
        if (mExecutor == null) {
            synchronized (ThreadPoolProxy.class) {
                if (mExecutor == null) {
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
                    mExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue, handler);
                }
            }
        }
        return mExecutor;
    }

    /**
     * 执行任务
     */
    public void execute(Runnable task) {
        initThreadPoolExecutor();
        mExecutor.execute(task);
    }

    /**
     * 移除任务
     */
    public void remove(Runnable task) {
        initThreadPoolExecutor();
        mExecutor.remove(task);
    }

    /**
     * 提交任务
     */
    public Future<?> summit(Runnable task) {
        initThreadPoolExecutor();
        return mExecutor.submit(task);
    }

}
