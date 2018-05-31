package cn.demonk.initflow.task;

import java.util.ArrayList;
import java.util.List;

import cn.demonk.initflow.task.depend.Dependency;
import cn.demonk.initflow.task.depend.Task;
import cn.demonk.initflow.task.result.TaskResult;
import cn.demonk.initflow.task.result.FutureTaskResult;
import cn.demonk.initflow.thread.ThreadMode;
import cn.demonk.initflow.utils.L;

/**
 * Created by ligs on 8/6/17.
 */

public abstract class InitTask {

    private final String mName;
    protected List<Dependency> mDepends;
    protected ThreadMode mThreadMode = ThreadMode.POSTING;

    public InitTask(String name) {
        this(name, ThreadMode.POSTING);
    }

    public InitTask(String name, ThreadMode threadMode) {
        this.mName = name;
        this.mThreadMode = threadMode;
        mDepends = new ArrayList<>();
    }

    public InitTask dependsOn(Dependency dependency) {
        mDepends.add(dependency);
        return this;
    }

    public FutureTaskResult taskRun() {
        boolean keySucc = true;
        for (Dependency depend : mDepends) {
            L.d(this.mName + " --> " + depend.getName());

            TaskResult taskResult = depend.get();//如果运行于非当前线程，则会直接返回假结果

            if (Task.TaskPriority.KEY == depend.getPriority()) {
                keySucc = keySucc && taskResult.isSucc();
                if (!keySucc) {
                    //关键流程失败，直接返回结果
                    return FutureTaskResult.makeFailedResult(taskResult.getCode(), taskResult.getMessage());
                }
            }
        }
        return exec();
    }

    public final String getName() {
        return mName;
    }

    public final ThreadMode getThreadMode() {
        return this.mThreadMode;
    }

    public abstract FutureTaskResult exec();
}
