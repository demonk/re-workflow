package cn.demonk.initflow.depend;

import cn.demonk.initflow.Task;

/**
 * Created by ligs on 8/6/17.
 */

public @interface DependencyDesp {
    String name();

    Task.TaskPriority priority() default Task.TaskPriority.NORMAL;
}
