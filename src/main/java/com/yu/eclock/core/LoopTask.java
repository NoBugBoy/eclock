package com.yu.eclock.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class LoopTask extends AbstractTask<Void> implements TimeWheelLoopTask {

    public LoopTask(TimeWheel timeWheel,String taskName,Integer seconds) {
        super(timeWheel,taskName,seconds,null,false,true);
    }
    public LoopTask(TimeWheel timeWheel,String taskName,Integer seconds ,boolean rollback) {
        super(timeWheel, taskName,seconds,null,rollback,true);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // empty method

    }

    @Override
    protected synchronized void setSlotAndRounds(int slot, int rounds) {
        // empty method
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
