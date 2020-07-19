package com.yu.eclock;

import com.yu.eclock.core.AbstractTask;
import com.yu.eclock.test.Task;
import com.yu.eclock.core.TimeWheel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// @SpringBootTest
class CircularApplicationTests {
    // @Autowired
    // TimeWheel timeWheel;
    // @Autowired
    // @Qualifier(value = "task10")
    // private Task task1;
    // // @Autowired
    // // @Qualifier(value = "task20")
    // // private Task task2;
    // ThreadPoolExecutor clearPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, Runtime.getRuntime().availableProcessors() * 2, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
    //     @Override
    //     public Thread newThread(Runnable r) {
    //         Thread thread = new Thread(r);
    //         thread.setName("xxxx");
    //         return thread;
    //     }
    // });
    // void clear(TimeWheel timeWheel,Set<AbstractTask<?>> taskBySlotPoint){
    //     clearPool.execute(() -> {
    //         if(taskBySlotPoint != null && taskBySlotPoint.size() > 0){
    //             for (AbstractTask<?> task : taskBySlotPoint){
    //                 if(task.isDone()){
    //                     timeWheel.removeTask(timeWheel.getCurrentSlotPoint(),task);
    //                 }
    //             }
    //         }
    //
    //     });
    // }
    // @Test
    // void contextLoads() {
    //     ThreadPoolExecutor startPool = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
    //         @Override
    //         public Thread newThread(Runnable r) {
    //             Thread thread = new Thread(r);
    //             thread.setName("xxxx");
    //             return thread;
    //         }
    //     });
    //
    //     task1.joinTimeWheel();
    //     task1.setTaskData("aaaaaaa");
    //     // task2.joinTimeWheel();
    //     // task2.setTaskData("VVVVVVVV");
    //     // new Task(timeWheel,"3333",20).joinTimeWheel();
    //     // new Task(timeWheel,"444",30);
    //     // new Task(timeWheel,"555",40);
    //
    //     while (true){
    //         System.out.println("当前potin = " + (timeWheel.moveCurrentSlotPoint() + 1));
    //         Set<AbstractTask<?>> taskBySlotPoint = timeWheel.getTaskBySlotPoint();
    //         if(timeWheel.isLazyInit()){
    //             if(taskBySlotPoint != null){
    //                 for (AbstractTask<?> abstractTask : taskBySlotPoint) {
    //                     startPool.execute(abstractTask);
    //                 }
    //
    //
    //             }
    //         }else{
    //             for (AbstractTask<?>  task : taskBySlotPoint) {
    //                 startPool.execute(task);
    //             }
    //
    //         }
    //         clear(timeWheel,taskBySlotPoint);
    //         try {
    //             Thread.sleep(1000);
    //         } catch (InterruptedException e) {
    //             e.printStackTrace();
    //         }
    //
    //     }
    //
    // }
    @Test
    public void a(){
        System.out.println(3618 % 3600);
        System.out.println(3618 / 3600);
    }

}
