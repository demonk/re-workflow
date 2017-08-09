package cn.demonk.initflow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cn.demonk.initflow.depend.DependencyDesp;

/**
 * 注解，定义一个任务的信息
 * Created by ligs on 8/6/17.
 */
@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Task {

    String name();

    DependencyDesp[] depends() default @DependencyDesp(name = "");

    ThreadMode threadMode() default ThreadMode.POSTING;

    enum TaskPriority {
        KEY("key"),
        NORMAL("normal");

        public String name;

        TaskPriority(String name) {
            this.name = name;
        }
    }
}
