package cn.demonk.initflow.task;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import cn.demonk.initflow.task.depend.Task;
import cn.demonk.initflow.task.result.FutureTaskResult;
import cn.demonk.initflow.task.result.TaskResult;
import cn.demonk.initflow.thread.TaskPool;
import cn.demonk.initflow.thread.ThreadMode;
import cn.demonk.initflow.utils.L;
import cn.demonk.initflow.utils.ReflectionUtils;

/**
 * exec task by reflecting method
 * Created by ligs on 8/6/17.
 */

public class InitTaskByMethod extends InitTask {

    private Method mMethod;
    private Object mAttachObj;
    private Task mTask;
    private FutureTaskResult mResult;

    public InitTaskByMethod(Object attachObj, Task task, Method method) {
        super(task.name(), task.threadMode());
        this.mAttachObj = attachObj;
        this.mMethod = method;
        this.mTask = task;
    }

    public Task getTaskAnnotation() {
        return this.mTask;
    }

    @Override
    public FutureTaskResult exec() {
        if (mResult != null) {
            return mResult;
        }

        //FIXME 多线程问题,mResult未创建好，下一个也进来了，但一般不会出现这情况
        final long startTime = System.currentTimeMillis();
        Future<TaskResult> task = null;
        if (ThreadMode.POSTING == this.mThreadMode) {
            task = syncInvoke();
        } else if (ThreadMode.ASYNC == this.mThreadMode) {
            task = asyncinvoke();
        }

        L.d("running: " + this.getName() + ",sync=" + (ThreadMode.POSTING == this.mThreadMode) + ",time=" + (System.currentTimeMillis() - startTime));

        return mResult = new FutureTaskResult(this, task);
    }

    private Future<TaskResult> syncInvoke() {
        final Object result = ReflectionUtils.invoke(mAttachObj, mMethod, null);
        FutureTask<TaskResult> task = new FutureTask<>(new Callable<TaskResult>() {
            @Override
            public TaskResult call() throws Exception {
                if (result instanceof TaskResult) {
                    return (TaskResult) result;
                }
                return TaskResult.makeFailedResult(-1, "method type error");
            }
        });
        task.run();
        return task;
    }

    private Future<TaskResult> asyncinvoke() {
        return TaskPool.submit(new Callable<TaskResult>() {
            @Override
            public TaskResult call() throws Exception {
                return syncInvoke().get();
            }
        });

    }
}
