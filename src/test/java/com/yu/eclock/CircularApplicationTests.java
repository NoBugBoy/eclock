package com.yu.eclock;

import com.mongodb.client.MongoDatabase;
import com.yu.eclock.test.Task1;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;

@SpringBootTest
class CircularApplicationTests {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void test(){
        MongoDatabase db = mongoTemplate.getDb();
        System.out.println(db.getName());
        Task1 task = new Task1(null,"12312312312312312",1);
        Map<String, Integer> taskData = task.getTaskData();
        mongoTemplate.save(task,"12312312312312312");

        // BasicDBObject basicDBObject = new BasicDBObject();
        // basicDBObject.put("taskName","12312312312312312");
        // Query q = new BasicQuery(basicDBObject.toJson());
        // Task1 one = mongoTemplate.findOne(q, Task1.class);
        // System.out.println(one);
    }
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
