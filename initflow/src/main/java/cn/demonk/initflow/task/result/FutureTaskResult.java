package cn.demonk.initflow.task.result;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cn.demonk.initflow.task.InitTask;
import cn.demonk.initflow.thread.ThreadMode;

/**
 * Bridge of getting result from tasks
 * Created by guosen.lgs@alibaba-inc.com on 5/30/18.
 */
public class FutureTaskResult {

    private Future<TaskResult> mTask;
    private InitTask mInitTask;

    public FutureTaskResult(InitTask initTask, Future<TaskResult> task) {
        this.mInitTask = initTask;
        this.mTask = task;
    }

    private FutureTaskResult() {
    }

    public boolean isDone() {
        return mTask.isDone();
    }

    public TaskResult get() {
        if (isDone() ||
                mInitTask.getThreadMode() == ThreadMode.POSTING) {
            return syncGet();
        }
        //当任务处于非当前线程运行时，表示结果不关注，直接返回成功
        return TaskResult.success();
    }

    protected TaskResult syncGet() {
        try {
            return mTask.get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
        return TaskResult.makeFailedResult(-1, "future task failed");
    }

    public final static FutureTaskResult makeFailedResult() {
        return makeFailedResult(-1, "");
    }

    public final static FutureTaskResult makeFailedResult(int code, String message) {
        return new FailedFutureTaskResult(code, message);
    }

    private static class FailedFutureTaskResult extends FutureTaskResult {

        private int mCode;
        private String mMsg;

        FailedFutureTaskResult(int code, String message) {
            mCode = code;
            mMsg = message;
        }

        public boolean isDone() {
            return true;
        }

        public TaskResult get() {
            return TaskResult.makeFailedResult(mCode, mMsg);
        }
    }

}
