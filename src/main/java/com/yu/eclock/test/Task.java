package com.yu.eclock.test;

import com.yu.eclock.core.LoopTask;
import com.yu.eclock.core.TimeWheel;

public class Task extends LoopTask<String> {
    public Task(TimeWheel timeWheel,String taskName,int seconds) {
        super(timeWheel,taskName,seconds);
    }


    // do something
    @Override
    protected void go(String data) {
        System.out.println("任务 " + getTaskName() +"执行了");
    }

    @Override
    public void exceptionCallBack(Exception e, int count) {
        e.printStackTrace();
        System.out.println(count);
    }

    @Override
    public void completeCallBack(String data, long timeMillis, int count) {
        System.out.println("count  " + count);
        this.joinTimeWheel();

    }

    @Override
    public void rollbackCallBack(int currentRollbackCount) {

    }
}
