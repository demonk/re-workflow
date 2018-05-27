package cn.demonk.initflow;

import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.demonk.initflow.depend.DependencyDesp;
import cn.demonk.initflow.task.InitTaskByMethod;

/**
 * 流入口
 * Created by ligs on 8/6/17.
 */
public class InitTaskFlow {

    public static final class InstanceHolder {
        public static final InitTaskFlow instance = new InitTaskFlow();
    }

    public static final InitTaskFlow instance() {
        return InstanceHolder.instance;
    }

    /**
     * 开始执行指定对象上定义的任务
     *
     * @param obj
     */
    public TaskResult run(Object obj, String tail) {
        if (obj == null)
            return TaskResult.makeFaileResule();

        TaskLinkBuilder builder =
                new ReflectionBuilder()
                        .attach(obj)
                        .tail(tail);

        InitTask startPoint = builder.build();
        return startPoint.taskRun();
    }

    private class ReflectionBuilder implements TaskLinkBuilder {

        private Object mAttachObj;
        private String mTail;
        private HashMap<String, InitTaskByMethod> mHashMap;

        public ReflectionBuilder attach(Object obj) {
            this.mAttachObj = obj;
            this.mHashMap = new HashMap<>();
            return this;
        }

        public ReflectionBuilder tail(String name) {
            this.mTail = name;
            return this;
        }

        @Override
        public InitTask build() {
            if (this.mAttachObj == null || TextUtils.isEmpty(this.mTail)) {
                return null;
            }

            scanMethods();
            InitTask tailTask = mHashMap.get(this.mTail);
            if (tailTask != null) {
                processDependency();
            }
            return tailTask;
        }

        private void scanMethods() {
            Method[] methods = this.mAttachObj.getClass().getDeclaredMethods();//TODO 需要考虑父
            for (Method method : methods) {
                Task task = method.getAnnotation(Task.class);
                if (task == null) {
                    continue;
                }

                mHashMap.put(task.name(), new InitTaskByMethod(this.mAttachObj, task, method));
            }
        }

        private void processDependency() {
            for (Map.Entry<String, InitTaskByMethod> entry : mHashMap.entrySet()) {
                InitTaskByMethod task = entry.getValue();

                Task taskAnno = task.getTaskAnnotation();
                for (DependencyDesp desp : taskAnno.depends()) {
                    String name = desp.name();
                    Task.TaskPriority priority = desp.priority();

                    InitTask depTask = mHashMap.get(name);
                    if (depTask == null) {
                        continue;
                    }

                    Dependency dependency = new Dependency(depTask, priority);
                    task.dependsOn(dependency);
                }
            }
        }

//        private String getSignature(Method method) {
//            StringBuilder sb = new StringBuilder();
//            sb.append(method.getName());
//            for (Class paramType : method.getParameterTypes()) {
//                sb.append(paramType.getName());
//            }
//
//            sb.append(method.getReturnType().getName());
//            return HashUtil.sha1(sb.toString());
//        }
    }
}
