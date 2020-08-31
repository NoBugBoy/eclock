# eclock 任务调度框架

### 使用时间轮的方式，初版仅支持秒级调度（暂不支持时间粒度调整

### 该框架基于SpirngBootStarter的方式进行开发，仅支持以SpringBoot为框架开发的应用集成（可以再基础上封装api通过rpc或tcp的方式进行二次开发，实现跨平台使用）

### 三大核心

1. TimeWheel(tw)，任务调度器，与任务执行解耦，仅处理计算任务所需要的周期，和slot位置，以及指针的移动，任务的清理
2. TimeWheelStartHandler，任务处理器，本质是一个线程任务，由独立的线程进行控制，调用tw的指针移动获取tw对应slot的任务，并通过work线程执行
3. AbstractTask，任务的实体，控制着整个任务的执行逻辑，项目中通过继承defaultTask和LoopTask两种任务来实现调度，并提供回调，回滚，重试等机制
4. 使用时需开启配置 eternal.clock.enabled=true

#### 配置
```properties
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, RedisAutoConfiguration.class})
```

#### defaultTask

1. 单次任务
2. 创建对象，设置任务数据（data project）并调用joinTimeWheel方法加入调度器
3. 通过回调函数解锁，可以灵活控制重新加入逻辑

最佳实践,先定义一个task并继承defaultTask指定输入数据泛型，每次使用时通过new的方式灵活添加

```java
 Task1 task = new Task1(bean,"test"+i,10+(i * 10));
            task.setTaskData("1231231");
            task.joinTimeWheel();
```

### loopTask

1. 循环执行任务
2. 创建对象并加入spring容器，设置任务数据（data project）并调用joinTimeWheel方法加入调度器
3. 不支持反复套娃

最佳实践,先定义一个task并继承loopTask指定输入数据泛型，通过定义单例bean的方式使用

```java
     @Bean
     public Testx1 testx1(TimeWheel timeWheel){
         Testx1 testx1 = new Testx1(timeWheel,"loop",100);
         testx1.setTaskData("123123");
         testx1.setLoopCount(-1);
         //开机自启，循环任务建议为true
         testx1.setStartedUp(true);
         return testx1;
      }
```

### 持久化（目前仅支持mongodb,redis，默认为内存）

针对持久化提供4种恢复策略，如果程序异常关闭，再次启动时，对于任务执行时间未到的任务，正常加入时间轮等待执行
对于已经超过了执行时间的任务，有如下四种策略：

1. 直接抛弃超过执行时间的任务（默认）
2. 重新初始化超过执行时间的任务（从当前时间从新加入任务队列，执行时间为当前时间+设置的时间）
3. 立即执行这些超过执行时间的任务（已经超过应该执行时间的任务，立刻执行）
4. 抛弃所有任务，包含未超时的任务（不建议）

使用时需开启配置

```properties
eternal.clock.persistence.enabled=true
eternal.clock.persistence.name=mongo
```
