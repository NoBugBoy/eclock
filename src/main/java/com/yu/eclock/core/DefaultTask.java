package com.yu.eclock.core;

public abstract class DefaultTask<T> extends AbstractTask<T>{

    private volatile boolean lock = true;
    public DefaultTask(){
    }
    public DefaultTask(TimeWheel timeWheel,String taskName,Integer seconds) {
        super(timeWheel,taskName,seconds,null,false,false);
    }
    public DefaultTask(TimeWheel timeWheel,String taskName,Integer seconds,T data) {
        super(timeWheel,taskName,seconds,data,false,false);
    }
    public DefaultTask(TimeWheel timeWheel,String taskName,Integer seconds,T data,boolean rollback) {
        super(timeWheel, taskName,seconds,data,rollback,false);

    }


    public void unLock(){
        this.lock = false;
    }

    public boolean isLock(){
        return this.lock;
    }
}
