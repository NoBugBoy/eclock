package com.yu.eclock.core;

import com.yu.eclock.exception.DataNullException;
import com.yu.eclock.exception.TaskDoneException;
import com.yu.eclock.exception.TaskExcitingException;
import com.yu.eclock.persistence.TaskDataConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class AbstractTask<T>  implements Runnable,CallBack<T>,TaskDataConvert<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTask.class);
    private final String uuid;
    private volatile T data;
    private boolean rollback;
    private volatile boolean exception;
    private AtomicInteger retryCount;
    private final AtomicInteger count;
    //Disable instruction reordering
    private volatile boolean done;
    private final int seconds;
    private final TimeWheel timeWheel;
    private final String taskName;
    private volatile boolean startedUp;
    private final boolean isLoopTask;
    private volatile int slot;
    private volatile int rounds;

    public AbstractTask(TimeWheel timeWheel,String taskName,int seconds,boolean rollback,boolean isLoopTask){
        this.rollback = rollback;
        this.exception = false;
        this.done = false;
        this.uuid = UUID.randomUUID().toString().replace("-","");
        //lazy load
        if(rollback){ this.retryCount = new AtomicInteger(0); }
        this.count = new AtomicInteger(0);
        this.timeWheel = timeWheel;
        this.seconds = seconds;
        this.taskName = taskName;
        this.isLoopTask = isLoopTask;

    }

    public String getId() { return uuid; }
    public final T getTaskData(){ return this.data; }
    public final void setTaskData(T data){ this.data = data; }
    public final String getTaskName() { return taskName; }
    public final boolean isDone(){ return this.done; }
    public final void setRollback(boolean rollback) { this.rollback = rollback; }
    public boolean isRollback() { return rollback; }
    public AtomicInteger getRetryCount() { return retryCount; }
    public int getSlot() { return slot; }
    public int getRounds() { return rounds; }

    public final void setRetryCount(int retryCount) {
        if(rollback){
            if (this.retryCount == null){
                this.retryCount = new AtomicInteger(0);
            }
            this.retryCount.getAndSet(Math.max(retryCount, 2));
        }
    }
    public final void setStartedUp(boolean startedUp) { this.startedUp = startedUp; }
    public final boolean isStartedUp() { return startedUp; }
    public final int getSeconds() { return seconds; }

    protected abstract void execute(T data);
    protected synchronized void setSlotAndRounds(int slot , int rounds) {
        this.slot = slot;
        this.rounds = rounds;
    }
    public final synchronized void joinTimeWheel(){
        if(count.get() == 0){
            if(isLoopTask ){
                LoopTask<T> tLoopTask = (LoopTask<T>)this;
                if(!tLoopTask.isEnableLoop()){
                    return;
                }
            }
            //first in
            timeWheel.addTask(this);
        }else{
            if(this instanceof DefaultTask<?>){
                DefaultTask<?> defaultTask = (DefaultTask<?>)this;
                if(!defaultTask.isLock()){
                    LOGGER.warn(" task {} unlock and rejoin ..",taskName);
                    this.done = false;
                    timeWheel.addTask(this);
                }else{
                     LOGGER.error("If you want to join repeatedly, please unlock first, task name is {}",getTaskName());
                    // throw new TaskLockException(""); break current task
                }
            }else{
                LOGGER.error("Loop task join repeatedly {}",getTaskName());
                // throw new TaskDoneException("");
            }
        }

    }
    @Override
    public final void run() {
        beforeRunCheck(data);
        LOGGER.debug("begin run task ...");
        long beginTimeMillis = System.currentTimeMillis();
        try{
            execute(data);
        }catch (Exception e){
            exception = true;
            exceptionCallBack(e,count.get());
            if(LOGGER.isDebugEnabled()){
                e.printStackTrace();
            }
            // help transaction rollback
            throw new TaskExcitingException(e.getMessage());
        }finally {
            count.incrementAndGet();
            if(exception && rollback && checkRollbackCount()){
                LOGGER.warn("Trigger exception to attempt rollback ， task name = {}",taskName);
                this.exception = false;
                LOGGER.warn("rollback started...");
                doFinally();
                reAddTask();
                //rollback callback function
                rollbackCallBack(retryCount.get());
            }else if(exception){
                // done for this
                this.done = true;
                // help gc
                this.data = null;
                if(isLoopTask){
                    LoopTask<T> tLoopTask = (LoopTask<T>)this;
                    tLoopTask.setEnableLoop(false);
                    doFinally();
                }
                LOGGER.error("Trigger exception destruction task ， task name = {}",taskName);
            }else if(isLoopTask){
                this.done = true;
                //先删除当前slot的task再添加，否则slot发生改变
                doFinally();
                doAfter();
                completeCallBack(data,System.currentTimeMillis() - beginTimeMillis,count.get());
            }else{
                this.done = true;
                doFinally();
                completeCallBack(data,System.currentTimeMillis() - beginTimeMillis,count.get());
            }

            LOGGER.debug("run task over...");
        }
    }

    /**
     * 和人生一样，最终都要走向死亡
     * 但任务不同，它还可以重新来过
     */
    private void doFinally(){
        timeWheel.removeCurrentTask(slot,this);
    }

    private void doAfter(){
        LoopTask<T> tLoopTask = (LoopTask<T>) this;
        if(tLoopTask.isEnableLoop() && isDone()){
            if(tLoopTask.isInfiniteCycle()){
                this.done = false;
                reAddTask();
            }else{
                if(tLoopTask.isInfiniteCycle() || tLoopTask.minusLoop() > 0){
                    this.done = false;
                    reAddTask();
                }
            }
        }
    }

    private void reAddTask(){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("reAdd task for name = {}",taskName);
        }
        timeWheel.addTask(this);
    }
    private  void beforeRunCheck(final Object data){
        Optional.ofNullable(data).orElseThrow(() -> new DataNullException("task data not found , please setTaskData"));
        if(isDone()){
            throw new TaskDoneException("The current task has been completed! Waiting for destruction");
        }
    }
    private  boolean checkRollbackCount(){
        return retryCount.getAndDecrement() > 1;
    }
}
