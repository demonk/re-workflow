package cn.demonk.initflow.task.result;

/**
 * Result about the only task
 * Created by guosen.lgs@alibaba-inc.com on 5/30/18.
 */
public class TaskResult {
    private static final TaskResult DEFAULT_SUCC_TASK_RESULT = new TaskResult(true);

    private int resultCode = 0;
    private boolean succ = false;
    private String message = "";

    public TaskResult() {
    }

    private TaskResult(boolean isSucc, int code, String message) {
        this.succ = isSucc;
        this.resultCode = code;
        this.message = message;
    }

    private TaskResult(boolean isSucc) {
        this(isSucc, 0, "");
    }

    public boolean isSucc() {
        return succ;
    }

    public int getCode() {
        return this.resultCode;
    }

    public String getMessage() {
        return this.message;
    }

    public static TaskResult success() {
        return DEFAULT_SUCC_TASK_RESULT;
    }

    public static TaskResult makeFailedResult(int code, String message) {
        return new TaskResult(false, code, message);
    }
}
