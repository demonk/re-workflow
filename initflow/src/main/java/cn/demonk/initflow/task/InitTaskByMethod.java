package cn.demonk.initflow.task;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import cn.demonk.initflow.InitTask;
import cn.demonk.initflow.Task;
import cn.demonk.initflow.TaskPool;
import cn.demonk.initflow.TaskResult;
import cn.demonk.initflow.ThreadMode;
import cn.demonk.initflow.utils.ReflectionUtils;

/**
 * Created by ligs on 8/6/17.
 */

public class InitTaskByMethod extends InitTask {

    private Method mMethod;
    private Object mAttachObj;
    private Task mTask;
    private TaskResult mResult;

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
    public TaskResult exec() {
        if (mResult != null) {
            return mResult;
        }

        //FIXME 多线程问题
        Future<Boolean> task = null;
        if (ThreadMode.POSTING == this.mThreadMode) {
            task = syncInvoke();
        } else if (ThreadMode.ASYNC == this.mThreadMode) {
            task = asyncinvoke();
        }

        return mResult = new TaskResult(task);
    }

    private Future<Boolean> syncInvoke() {
        Object object = ReflectionUtils.invoke(mAttachObj, mMethod, null);
        final boolean ret = object instanceof Boolean && (boolean) object;
        FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ret;
            }
        });
        task.run();
        return task;
    }

    private Future<Boolean> asyncinvoke() {
        return TaskPool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return syncInvoke().get();
            }
        });

    }
}
