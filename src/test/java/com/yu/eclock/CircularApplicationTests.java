package com.yu.eclock;

import org.junit.jupiter.api.Test;

// @SpringBootTest
class CircularApplicationTests {
//     // @Autowired
//     // private MongoTemplate mongoTemplate;

    // @Test
    // public void test(){
    //     DataModel dataModel = new DataModel();
    //     dataModel.setSlot(2);
    //     mongoTemplate.save(dataModel);
    //     List<DataModel> all = mongoTemplate.findAll(DataModel.class);
    //     all.stream().forEach(System.out::println);
    //     DeleteResult slot = mongoTemplate.remove(new Query(Criteria.where("slot").gt(0)), DataModel.class);
    //     System.out.println(slot.getDeletedCount());
    //     List<DataModel> all1 = mongoTemplate.findAll(DataModel.class);
    //     all1.stream().forEach(System.out::println);
    //     // BasicDBObject basicDBObject = new BasicDBObject();
    //     // basicDBObject.put("taskName","12312312312312312");
    //     // Query q = new BasicQuery(basicDBObject.toJson());
    //     // Task1 one = mongoTemplate.findOne(q, Task1.class);
    //     // System.out.println(one);
    // }
    @Test
    public void test(){
      int arr[] = {8,11,1,9,10,22,30,4};
      quickSort(arr,0,arr.length-1);
        for (int i : arr) {
            System.out.println(i);
        }
    }
    public void quickSort(int arr[] ,int left ,int right){
        int i = left;
        int j = right;
        if(left > right){
           return;
        }
        int temp = arr[right];
        while(i < j){

            while (temp >= arr[i]  && i < j){
                 i++;
            }
            while (temp <= arr[j] && i < j){
                j--;
            }
            if(i < j){
                int t = arr[i];
                arr[i] = arr[j];
                arr[j] = t;
            }

        }
        // if(){
        //
        // }
            arr[right] = arr[i];
            arr[i] = temp;

        //
        quickSort(arr,left,i - 1);
        quickSort(arr,i + 1,right);
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


}
