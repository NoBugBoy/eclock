package com.yu.eclock.core;

import com.yu.eclock.config.TimeWheelStartConfig;
import com.yu.eclock.enums.PersistenceEnum;
import com.yu.eclock.enums.PersistenceStrategyEnum;
import com.yu.eclock.exception.FixTaskException;
import com.yu.eclock.exception.PersistenceNameException;
import com.yu.eclock.exception.PersistenceStrategyException;
import com.yu.eclock.persistence.DataModel;
import com.yu.eclock.persistence.Persistence;
import com.yu.eclock.persistence.PersistenceFactory;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
    private final TimeWheelStartConfig timeWheelStartConfig;
    private final AtomicInteger currentSlot = new AtomicInteger(0) ;
    private final ReentrantLock al = new ReentrantLock();
    private final ReentrantLock rl = new ReentrantLock();
    public TimeWheel(boolean lazyInit,TimeWheelStartConfig timeWheelStartConfig){
        tasks = new CopyOnWriteArraySet[timeSlot];
        this.lazyInit = lazyInit;
        this.timeWheelStartConfig = timeWheelStartConfig;
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
     * 是否持久化任务
     * @return boolean persistence
     */
    public boolean isPersistence() { return timeWheelStartConfig.getPersistence().isEnabled(); }
    public String getPersistenceName(){return timeWheelStartConfig.getPersistence().getTypeName(); }

    /**
     * @return 获取当前solt中所有的任务
     */
    public Set<AbstractTask<?>> getTaskBySlotPoint(){ return this.tasks[currentSlot.get() - 1]; }

    /**
     * 添加一个任务到轮盘，下次执行的时间由当前slot指针控制
     * 如果超过轮盘最大周期3600，则计算下次执行圈次
     * @param task 执行的自定义任务
     */
    public final void addTask(AbstractTask<?> task){
        int seconds = task.getSeconds();
        if(seconds < 0 || task.isDone()) return;
        try {
            al.lock();
            int slot = currentSlot.get() + seconds;
            if(slot > timeSlot){
                slot = slot % timeSlot;
                int rounds = slot / timeSlot ;
                if (lazyInit) lazyInit(slot);
                task.setSlotAndRounds(slot,rounds);
            }else{
                if (lazyInit) lazyInit(slot);
                task.setSlotAndRounds( slot,1);
            }
            //循环任务持久化意义不大
            if(isPersistence() && task instanceof DefaultTask<?>){
                persistenceAdd(task);
            }
            this.tasks[slot].add(task);
            // LOGGER.info("add task {} successful ...",task.getTaskName());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("add task {} error :{}",task.getTaskName(),e.getMessage());
        } finally {
            al.unlock();
        }

    }
    public String getStrategy(){
        TimeWheelStartConfig.Persistence persistence = timeWheelStartConfig.getPersistence();
        if(PersistenceEnum.MONGO.name().equalsIgnoreCase(persistence.getTypeName())){
            return persistence.getMongo().getStrategy();
        }
        if(PersistenceEnum.REDIS.name().equalsIgnoreCase(persistence.getTypeName())){
            return persistence.getRedis().getStrategy();
        }
        return "";
    }
    public final void addAndFixTask(DataModel dataModel,long startTime){
        long timestamp = dataModel.getTimestamp();
        int  rounds    = dataModel.getRounds();
        long time;
        if(rounds > 1){
            time = dataModel.getSeconds() + ((rounds - 1) * 3600);
        }else{
            time = dataModel.getSeconds() ;
        }
        String strategy = getStrategy();
        PersistenceStrategyEnum persistenceStrategyEnum;
        try {
            persistenceStrategyEnum = PersistenceStrategyEnum.valueOf(strategy.toUpperCase());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new PersistenceStrategyException("Strategy not found exception");
        }
        if(((startTime - timestamp) / 1000) <= time ){
            //直接恢复
            if (persistenceStrategyEnum != PersistenceStrategyEnum.DISCARD_ALL) {
                AbstractTask<?> task = buildClass(dataModel);
                if (task  == null) throw new FixTaskException("Resume task exception");
                PersistenceFactory.getPersistence(getPersistenceName()).remove(task.getId());
                addTask(task);
            }
            PersistenceFactory.getPersistence(getPersistenceName()).remove(dataModel.getTaskId());
        }else {
            switch (persistenceStrategyEnum){
                case DISCARD:
                    PersistenceFactory.getPersistence(getPersistenceName()).remove(dataModel.getTaskId());
                case NOW:
                    AbstractTask<?> now = buildClass(dataModel);
                    PersistenceFactory.getPersistence(getPersistenceName()).remove(dataModel.getTaskId());
                    if (now  == null) throw new FixTaskException("Resume task exception");
                    now.run();
                case INIT:
                    AbstractTask<?> init = buildClass(dataModel);
                    PersistenceFactory.getPersistence(getPersistenceName()).remove(dataModel.getTaskId());
                    if (init  == null) throw new FixTaskException("Resume task exception");
                    init.joinTimeWheel();
                case DISCARD_ALL:
                    PersistenceFactory.getPersistence(getPersistenceName()).remove(dataModel.getTaskId());
                default:
                    PersistenceFactory.getPersistence(getPersistenceName()).remove(dataModel.getTaskId());
            }
            //判断是否需要重新加入
            LOGGER.debug(dataModel.getTaskId() + "已经过期");
        }
    }
    private AbstractTask<?> buildClass(DataModel dataModel){
        try {
            Class<?> aClass = Class.forName(dataModel.getClazz());
            Constructor<?>[] constructors = aClass.getConstructors();
            for (Constructor<?> constructor : constructors) {
                if(constructor.getParameterCount() == 3){
                    AbstractTask<?> task = (AbstractTask<?>)constructor.newInstance(this, dataModel.getTaskName(),
                        dataModel.getSeconds());
                    Class<?> superclass = aClass.getSuperclass().getSuperclass();
                    Field data = superclass.getDeclaredField("data");
                    Field uuid = superclass.getDeclaredField("uuid");
                    data.setAccessible(true);
                    data.set(task,dataModel.getData());
                    uuid.setAccessible(true);
                    uuid.set(task,dataModel.getTaskId());
                    task.setRollback(dataModel.isRollback());
                    task.setRetryCount(dataModel.getRetryCount());
                    return task;
                }
            }
            return null;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void persistenceAdd(AbstractTask<?> task){
        if(Strings.isBlank(getPersistenceName())){
            throw new PersistenceNameException("persistence name can not be null or '' ");
        }

        DataModel convert = task.convert();
        PersistenceFactory.getPersistence(getPersistenceName()).add(convert);
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
     */
    public void removeTask(int slot , AbstractTask<?> task){
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
        }finally {
            rl.unlock();
        }
    }

    /**
     * 删除当前任务
     * @param slot 位置
     * @param task 任务对象
     */
    public void removeCurrentTask(int slot,AbstractTask<?> task){
        if(isPersistence()){
            if(Strings.isBlank(getPersistenceName())){
                throw new PersistenceNameException("persistence name can not be null or '' ");
            }
            Persistence persistence = PersistenceFactory.getPersistence(getPersistenceName());
            assert persistence != null;
            persistence.remove(task.getId());
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("移除{}位置上的任务 >>>>> {}",slot,task.getTaskName());
            }
        }
        this.tasks[slot].remove(task);

    }

    /**
     * 默认初始化
     */
    private synchronized void init(){
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new CopyOnWriteArraySet<>();
        }
    }
}
