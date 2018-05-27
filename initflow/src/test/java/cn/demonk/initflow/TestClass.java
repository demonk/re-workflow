package cn.demonk.initflow;

import cn.demonk.initflow.depend.DependencyDesp;

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
    public boolean setp1() {
        System.out.println(ONE);
        return true;
    }

    @Task(name = TWO, depends = {
            @DependencyDesp(name = ONE)
    })
    public boolean setp2() {
        System.out.println(TWO);
        return true;
    }

    @Task(name = THREE, depends = {
            @DependencyDesp(name = ONE)
    })
    public boolean setp3() {
        System.out.println(THREE);
        return true;
    }

    @Task(name = FOUR, depends = {
            @DependencyDesp(name = TWO)
    })
    public boolean setp4() {
        System.out.println(FOUR);
        return true;
    }

    @Task(name = FIVE, depends = {
            @DependencyDesp(name = THREE)
    })
    public boolean setp5() {
        System.out.println(FIVE);
        return false;
    }

    @Task(name = SIX, depends = {
            @DependencyDesp(name = THREE)
    })
    public boolean setp6() {
        System.out.println(SIX);
        return true;
    }

    @Task(name = SEVEN, depends = {
            @DependencyDesp(name = FIVE)
    })
    public boolean setp7() {
        System.out.println(SEVEN);
        return true;
    }

    @Task(name = EIGHT, depends = {
            @DependencyDesp(name = FOUR, priority = Task.TaskPriority.KEY),
            @DependencyDesp(name = SEVEN, priority = Task.TaskPriority.KEY),
            @DependencyDesp(name = SIX, priority = Task.TaskPriority.KEY),
            @DependencyDesp(name = FIVE)
    })
    public boolean setp8() {
        System.out.println(EIGHT);
        return true;
    }

}
