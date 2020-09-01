# 时间调度框架E-clock
## 简介
eclock是针对秒级任务调度的开源框架（非传统根据表达式定期执行）,可以在业务逻辑中灵活的添加任务，并提供多种回调方法，在回调方法内可以根据业务再一次灵活的控制后续需要执行业务，该框架基于SpirngBootStarter的方式进行开发，仅支持以SpringBoot为框架开发的应用集成（可以再基础上封装api通过rpc或tcp的方式进行二次开发，实现跨平台使用）,并提供基于Redis和MongoDB的持久化存储的高可用方案
## 如何安装？
只需要依赖jar包即可
源码版本可以去[github查看](https://github.com/NoBugBoy/eclock)
maven依赖版本可以去[maven中央仓库查看](https://mvnrepository.com/artifact/com.github.nobugboy/eclock)
```xml
 <dependency>
    <groupId>com.github.nobugboy</groupId>
    <artifactId>eclock</artifactId>
    <version>0.0.6</version>
</dependency>
```
如果程序没有使用reids和mongo就排除redis和mogo的自动装配，如果需要使用就删除排除即可
```java
@SpringBootApplication(exclude = {
    RedisAutoConfiguration.class,
    MongoAutoConfiguration.class
    })
```
参数对照表
| 配置参数 | 值 | 描述 |
|--|--| -- |
| eternal.clock.enabled | true/false | 是否开启eclock 默认false|
| eternal.clock.persistence.enabled | true/false | 是否开启持久化 默认false|
| eternal.clock.persistence.type-name | redis/mongo | 指定持久化方式 |
| eternal.clock.persistence.strategy | discard/init/now/discard_all | 指定任务恢复策略 |
| eternal.clock.threads.max | int | 最大线程数 默认核心线程2倍 |
| eternal.clock.threads.core | int | 核心线程数 默认为处理器核心数 |
| eternal.clock.threads.keep-alive-seconds | long | 线程存活时间默认10秒 |


1. discard 直接抛弃超过执行时间的任务（默认）
2. init 重新初始化超过执行时间的任务（从当前时间从新加入任务队列，执行时间为当前时间+设置的时间）
3. now 立即执行这些超过执行时间的任务（已经超过应该执行时间的任务，立刻执行）
4. discard_all 抛弃所有任务，包含未超时的任务（不建议）


## 有何特性？

 1. 开启持久化，程序异常关闭，修复任务重新执行
 2. 异步调用，可以配置线程参数
 3. 异常重试（非立刻重试而是等待配置的间隔时间）
 4. 多种回调，灵活使用

## 如何使用？
#### 1. 如果你想在程序启动时,就开始**定时执行任务**，框架提供了LoopTask方式，只需要在构造器中进行一些简单配置即可
```java
@Component
public class MyLoop extends LoopTask {
	//注入TimeWheel任务执行控制器
    public MyLoop(TimeWheel timeWheel) {
    	//传入timeWheel，任务名称，执行间隔
        super(timeWheel,"check order", 1);
        //配置执行次数，-1为无限次
        super.setLoopCount(10);
        //是否随程序启动自动执行，默认是true
        super.setStartedUp(true);
        //异常是否重试
        super.setRetry(true);
        //重试次数
        super.setRetryCount(3);
        
    }
    //业务逻辑的执行方法，一般需要配合注入一些service来写业务
    @Override
    protected void execute(Void data) {
        System.out.println("执行业务逻辑");
    }
    //异常回调
    @Override
    public void exceptionCallBack(Exception e, int count) {
        System.out.println("执行第"+count+"次发生异常" + e.getMessage());
    }
    //完成回调
    @Override
    public void completeCallBack(Void data, long timeMillis, int count) {
        System.out.println("第"+count+"次任务执行完成耗时"+timeMillis+"毫秒");
    }
    //重试回调
    @Override
    public void retryCallBack(int currentRetryCount) {
        System.out.println("当前重试次数"+currentRetryCount);
    }
}
```
#### 2. 如果你想在程序中灵活的添加延时任务的话框架提供了DefaultTask方式，定义一段逻辑模板，每次通过new传递不通的数据来执行
定义任务
```java
//泛型为传递参数的类型
public class MyTask extends DefaultTask<String> {
    //需要将业务service或dao注入
    private UserService userService;
    public MyTask(TimeWheel timeWheel, String taskName, Integer seconds, UserService userService) {
        //需要timeWheel,任务名称，执行间隔
        super(timeWheel,taskName, seconds);
        //构造器注入
        this.userService = userService;
    }
    //传递泛型对应的参数，执行逻辑
    @Override
    protected void execute(String data)
    {
        User user = new User();
        user.setName(data);
        userService.save(user);
    }
    @Override
    public void exceptionCallBack(Exception e, int count) {
        System.err.println("异常了"+e.getMessage());
    }
    @Override
    public void completeCallBack(String data, long timeMillis, int count) {
        //如果在任务运行完成时需要以相同的参数再次加入调度需要先解锁以防误操作
        if(this.isLock()){
            this.unLock();
        }
        this.joinTimeWheel();
    }

    @Override
    public void retryCallBack(int currentRetryCount) {
        System.out.println("重试"+currentRetryCount);
    }
}
```
创建一个保存用户的延迟任务
```java
    @Autowired
    private UserService userService;
    @Autowired
    private TimeWheel timeWheel;
    //创建延时任务
    MyTask myTask = new MyTask(timeWheel,"save user task",5,userService);
    //必须添加执行的参数
    myTask.setTaskData("My name");
    //加入调度
    myTask.joinTimeWheel();
```
