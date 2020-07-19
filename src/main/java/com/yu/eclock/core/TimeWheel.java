package com.yu.eclock.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 时间轮控制器，控制整个轮盘的执行周期和slot指针移动
 */
public class TimeWheel {
    private final Logger LOGGER = LoggerFactory.getLogger(TimeWheel.class);
    private final int timeSlot = 3600;
    private final CopyOnWriteArraySet<AbstractTask<?>>[] tasks;
    private final boolean lazyInit;
    private final AtomicInteger currentSlot = new AtomicInteger(0) ;
    private final ReentrantLock al = new ReentrantLock();
    private final ReentrantLock rl = new ReentrantLock();
    public TimeWheel(boolean lazyInit){
        tasks = new CopyOnWriteArraySet[timeSlot];
        this.lazyInit = lazyInit;
        if(!lazyInit){
            init();
        }
    }

    /**
     * @return 获取当前solt位置
     */
    public int getCurrentSlotPoint(){ return currentSlot.get(); }

    /**
     * 如果是懒加载方式，将不会完全实例化时间轮的slot
     * 建议数量少，覆盖面小的任务时启用
     * @return boolean lazyInit
     */
    public boolean isLazyInit(){ return lazyInit; }

    /**
     * @return 获取当前solt中所有的任务
     */
    public Set<AbstractTask<?>> getTaskBySlotPoint(){ return this.tasks[currentSlot.get() - 1]; }

    /**
     * 添加一个任务到轮盘，下次执行的时间由当前slot指针控制
     * 如果超过轮盘最大周期3600，则计算下次执行圈次
     * @param task 执行的自定义任务
     * @return 添加是否成功
     */
    public final boolean addTask(AbstractTask<?> task){
        int seconds = task.getSeconds();
        if(seconds < 0 || task.isDone()) return false;
        try {
            al.lock();
            int slot = currentSlot.get() + seconds;
            if(slot > timeSlot){
                slot = (currentSlot.get() + seconds) % timeSlot;
                int rounds = (currentSlot.get() + seconds) / timeSlot ;
                if (lazyInit) lazyInit(slot);
                task.setSlotAndRounds(slot,rounds);
            }else{
                if (lazyInit) lazyInit(slot);
                task.setSlotAndRounds( slot,1);
            }
            this.tasks[slot].add(task);
            // LOGGER.info("add task {} successful ...",task.getTaskName());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("add task {} error :{}",task.getTaskName(),e.getMessage());
        } finally {
            al.unlock();
        }

        return true;
    }

    /**
     * 移动slot指针，超过最大周期，将从头执行
     * 首尾相连
     * @return 当前slot位置
     */
    public int moveCurrentSlotPoint(){
        if(currentSlot.get() >=  timeSlot - 1){
            currentSlot.set(0);
        }
        return currentSlot.getAndIncrement();
    }

    /**
     * 懒加载
     * @param slot 指针位置
     */
    private void lazyInit(int slot){
        if (tasks[slot] == null){
            tasks[slot] = new CopyOnWriteArraySet<>();
        }
    }

    /**
     * 移除已经done掉的task
     * @param slot 当前位置
     * @param task 任务对象
     * @return 是否成功移除任务
     */
    public boolean removeTask(int slot , AbstractTask<?> task){
        try{
            rl.lock();
            if(null == task){
                LOGGER.warn("clear slot{} all task",slot);
                this.tasks[slot].clear();
            }else{
                if(task.isDone()){
                    if(LOGGER.isDebugEnabled()){
                        LOGGER.debug("clear slot{} task , taskName is {}",slot,task.getTaskName());
                    }
                    this.tasks[slot].remove(task);
                }
            }
        }catch (Exception e){
            LOGGER.error("remove task exception {}",e.getMessage());
            e.printStackTrace();
            return false;
        }finally {
            rl.unlock();
        }
        return true;
    }

    /**
     * 删除当前任务
     * @param slot
     * @param task
     */
    public void removeCurrentTask(int slot,AbstractTask<?> task){
            LOGGER.info("移除{}位置上的任务 >>>>> {}",slot,task.getTaskName());
            this.tasks[slot].remove(task);
    }

    /**
     * 默认初始化
     */
    private void init(){
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new CopyOnWriteArraySet<>();
        }
    }
}
