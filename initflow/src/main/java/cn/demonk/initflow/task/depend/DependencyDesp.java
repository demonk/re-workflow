package cn.demonk.initflow.task.depend;

/**
 * Created by ligs on 8/6/17.
 */

public @interface DependencyDesp {
    String name();

    Task.TaskPriority priority() default Task.TaskPriority.NORMAL;
}
