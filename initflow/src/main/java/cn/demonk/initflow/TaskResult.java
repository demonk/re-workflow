package cn.demonk.initflow;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 任务执行与结果的通道
 * Created by ligs on 8/6/17.
 */
public class TaskResult {

    private Future<Boolean> mTask;

    public TaskResult(Future<Boolean> task) {
        this.mTask = task;
    }

    private TaskResult() {
    }

    public boolean isDone() {
        return mTask.isDone();
    }

    public boolean get() {
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
