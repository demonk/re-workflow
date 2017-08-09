package cn.demonk.initflow;

import java.util.ArrayList;
import java.util.List;

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

    public TaskResult taskRun() {
        boolean keySucc = true;
        for (Dependency depend : mDepends) {
            L.d(this.mName + " --> " + depend.getName());
            boolean result = depend.get().get();

            if (Task.TaskPriority.KEY == depend.getPriority()) {
                keySucc = keySucc && result;
                if (!keySucc) {
                    break;
                }
            }
        }

        if (keySucc) {
            L.d("running: " + this.mName);
            return exec();
        }

        return TaskResult.makeFaileResule();
    }

    public final String getName() {
        return mName;
    }

    public abstract TaskResult exec();
}
