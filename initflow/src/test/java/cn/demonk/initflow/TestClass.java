package cn.demonk.initflow;

import cn.demonk.initflow.task.depend.DependencyDesp;
import cn.demonk.initflow.task.depend.Task;
import cn.demonk.initflow.task.result.TaskResult;

/**
 * Created by ligs on 8/10/17.
 */

public class TestClass {

    private static final String ONE = "step_1";
    private static final String TWO = "step_2";
    private static final String THREE = "step_3";
    private static final String FOUR = "step_4";
    private static final String FIVE = "step_5";
    private static final String SIX = "step_6";
    private static final String SEVEN = "step_7";
    public static final String EIGHT = "step_8";

    @Task(name = ONE)
    public TaskResult setp1() {
        System.out.println(ONE);
        return TaskResult.success();
    }

    @Task(name = TWO, depends = {
            @DependencyDesp(name = ONE)
    })
    public TaskResult setp2() {
        System.out.println(TWO);
        return TaskResult.success();
    }

    @Task(name = THREE, depends = {
            @DependencyDesp(name = ONE)
    })
    public TaskResult setp3() {
        System.out.println(THREE);
        return TaskResult.success();
    }

    @Task(name = FOUR, depends = {
            @DependencyDesp(name = TWO)
    })
    public TaskResult setp4() {
        System.out.println(FOUR);
        return TaskResult.success();
    }

    @Task(name = FIVE, depends = {
            @DependencyDesp(name = THREE)
    })
    public TaskResult setp5() {
        System.out.println(FIVE);
        return TaskResult.makeFailedResult(-2, "failed to exec task 5");
    }

    @Task(name = SIX, depends = {
            @DependencyDesp(name = THREE)
    })
    public TaskResult setp6() {
        System.out.println(SIX);
        return TaskResult.success();
    }

    @Task(name = SEVEN, depends = {
            @DependencyDesp(name = FIVE)
    })
    public TaskResult setp7() {
        System.out.println(SEVEN);
        return TaskResult.success();
    }

    @Task(name = EIGHT, depends = {
            @DependencyDesp(name = FOUR, priority = Task.TaskPriority.KEY),
            @DependencyDesp(name = SEVEN, priority = Task.TaskPriority.KEY),
            @DependencyDesp(name = SIX, priority = Task.TaskPriority.KEY),
            @DependencyDesp(name = FIVE)
    })
    public TaskResult setp8() {
        System.out.println(EIGHT);
        return TaskResult.success();
    }

}
