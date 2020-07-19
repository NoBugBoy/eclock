package com.yu.eclock.test;

import com.yu.eclock.core.DefaultTask;
import com.yu.eclock.core.TimeWheel;

import java.util.Map;

public class Task1 extends DefaultTask<Map<String,Integer>> {
    public Task1(TimeWheel timeWheel, String taskName, int seconds) {
        super(timeWheel, taskName, seconds);
    }

    @Override
    protected void go(Map<String, Integer> data) {
        if(data.size() > 0){
            Integer key = data.get("key");
            data.put("key",key + 1);
        }else{
            data.put("key",0);
        }
    }

    @Override
    public void exceptionCallBack(Exception e, int count) {
        e.printStackTrace();
        System.out.println(count);
    }

    @Override
    public void completeCallBack(Map<String, Integer> data, long timeMillis, int count) {
        System.out.println(count);
        if(data.size() == 5){
            this.stop();
        }
        this.unLock();
        this.joinTimeWheel();
    }

    @Override
    public void rollbackCallBack(int currentRollbackCount) {

    }
}
