package cn.demonk.initflow.task.depend;

import cn.demonk.initflow.task.InitTask;
import cn.demonk.initflow.task.result.TaskResult;

/**
 * Created by ligs on 8/6/17.
 */

public class Dependency {

    private InitTask mInitTask;
    private Task.TaskPriority mPriority = Task.TaskPriority.NORMAL;

    public Dependency(InitTask task) {
        this(task, Task.TaskPriority.NORMAL);
    }

    public Dependency(InitTask task, Task.TaskPriority priority) {
        if (task == null) {
            throw new IllegalArgumentException("task should not be null");
        }

        this.mInitTask = task;
        this.mPriority = priority;
    }

    public String getName() {
        return mInitTask.getName();
    }

    /**
     * 获取依赖的任务的执行结果
     */
    public TaskResult get() {
        return mInitTask.taskRun().get();
    }

    public Task.TaskPriority getPriority() {
        return mPriority;
    }

}
