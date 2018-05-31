package cn.demonk.initflow.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ligs on 8/6/17.
 */

public class TaskPool {

    private static final int CORE_THREAD = 2;
    private static final int MAX_THREAD = 5;
    private static final int MAX_IDLE_TIME = 15;

    private static ExecutorService mExecutorSerivce;

    static {
        init();
    }

    /**
     * 此线程池是否可用
     */
    private static boolean available() {
        return mExecutorSerivce != null && !mExecutorSerivce.isShutdown();
    }

    /**
     * 初始化线程池,保证只会创建一个
     */
    private static void init() {
        if (!available()) {
            mExecutorSerivce = createExecutor();
        }
    }

    private static ExecutorService createExecutor() {

        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(//
                CORE_THREAD, MAX_THREAD,//
                MAX_IDLE_TIME, TimeUnit.SECONDS,//
                (BlockingQueue) taskQueue
        );

        return executor;
    }

    public static void execute(Runnable task) {
        if (available()) {
            mExecutorSerivce.execute(task);
        }
    }

    public static <T> Future<T> submit(Callable<T> task) {
        if (available()) {
            return mExecutorSerivce.submit(task);
        }
        return null;
    }
}
