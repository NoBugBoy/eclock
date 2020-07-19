package com.yu.eclock.core;

public abstract class DefaultTask<T> extends AbstractTask<T>{

    private volatile boolean lock = true;

    public DefaultTask(TimeWheel timeWheel,String taskName,int seconds,boolean rollback) {
        super(timeWheel, taskName,seconds,rollback,false);

    }
    public DefaultTask(TimeWheel timeWheel,String taskName,int seconds) {
        super(timeWheel,taskName,seconds,false,false);
    }

    public void unLock(){
        this.lock = false;
    }

    public boolean isLock(){
        return this.lock;
    }
}
