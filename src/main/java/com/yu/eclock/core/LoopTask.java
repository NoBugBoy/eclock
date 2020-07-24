package com.yu.eclock.core;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class LoopTask<T> extends AbstractTask<T> implements TimeWheelLoopTask{
    public LoopTask(TimeWheel timeWheel,String taskName,int seconds,boolean rollback) {
        super(timeWheel, taskName,seconds,rollback,true);

    }
    public LoopTask(TimeWheel timeWheel,String taskName,int seconds) {
        super(timeWheel,taskName,seconds,false,true);

    }
    // -1 无限 默认为1
    private final AtomicInteger loopCount  = new AtomicInteger(1);
    //是否开启循环
    private boolean enableLoop = true;

    @Override
    public int minusLoop() {
        if(loopCount.get() - 1 > 0){
            loopCount.decrementAndGet();
        }else{
            loopCount.set(0);
        }
        return loopCount.get();
    }

    @Override
    public void setLoopCount(int loopCount) {
        if(loopCount <= 0){
            this.loopCount.set(-1);
        }else{
            this.loopCount.set(Math.max(loopCount>Integer.MAX_VALUE - 1?Integer.MAX_VALUE:loopCount,1));
        }


    }
    @Override
    public void setEnableLoop(boolean enableLoop) {
        this.enableLoop = enableLoop;
    }


    @Override
    public int getLoopCount() {
        return loopCount.get();
    }

    @Override
    public boolean isInfiniteCycle() {
        return loopCount.get() == -1;
    }

    @Override
    public boolean isEnableLoop() {
        return enableLoop;
    }

}
