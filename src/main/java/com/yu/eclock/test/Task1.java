package com.yu.eclock.test;

import com.yu.eclock.core.DefaultTask;
import com.yu.eclock.core.TimeWheel;

import java.util.Collections;
import java.util.List;

public class Task1 extends DefaultTask<List<Integer>> {
    public Task1(TimeWheel timeWheel, String taskName, int seconds) {
        super(timeWheel, taskName, seconds);
    }

    @Override
    protected void execute(List<Integer> data) {
        for (Integer datum : data) {
            System.out.println(datum);
        }
    }

    @Override
    public void exceptionCallBack(Exception e, int count) {

    }

    @Override
    public void completeCallBack(List<Integer> data, long timeMillis, int count) {

    }

    @Override
    public void rollbackCallBack(int currentRollbackCount) {

    }
}
