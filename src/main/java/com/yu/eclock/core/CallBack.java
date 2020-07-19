package com.yu.eclock.core;

public interface CallBack<T> {
    void exceptionCallBack(Exception e,int count);
    void completeCallBack(T data,long timeMillis,int count);
    void rollbackCallBack(int currentRollbackCount);
}
