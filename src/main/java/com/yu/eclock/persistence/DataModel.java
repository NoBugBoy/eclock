package com.yu.eclock.persistence;

import java.io.Serializable;

public class DataModel implements Serializable {
    private String taskId;
    private long timestamp;
    private int seconds;
    private boolean isLoopTask;
    private Object data;
    private int slot;
    private int rounds;
    private String taskName;
    private boolean rollback;
    private int retryCount;
    private String clazz;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isLoopTask() {
        return isLoopTask;
    }

    public void setLoopTask(boolean loopTask) {
        isLoopTask = loopTask;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isRollback() {
        return rollback;
    }

    public void setRollback(boolean rollback) {
        this.rollback = rollback;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public String toString() {
        return "DataModel{" + "timestamp=" + timestamp + ", seconds=" + seconds + ", isLoopTask=" + isLoopTask
            + ", data=" + data + ", slot=" + slot + ", rounds=" + rounds + ", taskName='" + taskName + '\''
            + ", rollback=" + rollback + ", retryCount=" + retryCount + '}';
    }
}
