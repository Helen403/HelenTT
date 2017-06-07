package Utils;

/**
 * 线程池
 */
public final class ThreadPoolUtils {

    private ThreadPoolUtils() {
    }

    /**
     * 在非UI线程中执行
     */
    public static void runTaskInThread(Runnable task) {
        //线程池
        ThreadPoolFactory.getCommonThreadPool().execute(task);
    }
}
