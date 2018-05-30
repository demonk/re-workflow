package cn.demonk.initflow;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 任务执行与结果的通道
 * Created by ligs on 8/6/17.
 */
public class TaskResult {

    private Future<Boolean> mTask;
    private InitTask mInitTask;

    public TaskResult(InitTask initTask, Future<Boolean> task) {
        this.mInitTask = initTask;
        this.mTask = task;
    }

    private TaskResult() {
    }

    public boolean isDone() {
        return mTask.isDone();
    }

    public boolean get() {
        if (isDone() ||
                mInitTask.mThreadMode == ThreadMode.POSTING) {
            return syncGet();
        }
        //当任务处于非当前线程运行时，表示结果不关注，直接返回成功
        return true;
    }

    public boolean syncGet() {
        try {
            return mTask.get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
        return false;
    }

    public final static TaskResult makeFaileResule() {
        return new FailedTaskresult();
    }

    private static class FailedTaskresult extends TaskResult {

        public boolean isDone() {
            return true;
        }

        public boolean get() {
            return false;
        }
    }
}
