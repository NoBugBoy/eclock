package com.yu.eclock.test;

import com.yu.eclock.core.DefaultTask;
import com.yu.eclock.core.TimeWheel;

public class Task1 extends DefaultTask<String> {
    public Task1(TimeWheel timeWheel, String taskName, int seconds) {
        super(timeWheel, taskName, seconds);
    }

    @Override
    protected void execute(String data) {
        System.out.println(data);
    }

    @Override
    public void exceptionCallBack(Exception e, int count) {
        e.printStackTrace();
        System.out.println(count);
    }

    @Override
    public void completeCallBack(String data, long timeMillis, int count) {
        System.out.println(count);
        this.unLock();
        this.joinTimeWheel();
    }

    @Override
    public void rollbackCallBack(int currentRollbackCount) {

    }
}
