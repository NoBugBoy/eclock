# eclock 分布式任务调度框架

### 使用时间轮的方式，初版仅支持秒级调度（暂不支持时间粒度调整），任务落地和分布式待在后续版本后陆续实现

### 该框架基于SpirngBootStarter的方式进行开发，仅支持以SpringBoot为框架开发的应用集成（可以再基础上封装api通过rpc或tcp的方式进行二次开发，实现跨平台使用）

### 三大核心
1. TimeWheel(tw)，任务调度器，与任务执行解耦，仅处理计算任务所需要的周期，和slot位置，以及指针的移动，任务的清理
2. TimeWheelStartHandler，任务处理器，本质是一个线程任务，由独立的线程进行控制，调用tw的指针移动获取tw对应slot的任务，并通过work线程执行
3. AbstractTask，任务的实体，控制着整个任务的执行逻辑，项目中通过继承defaultTask和LoopTask两种任务来实现调度，并提供回调，回滚，重试等机制

#### defaultTask
1. 单次任务，执行完后回收
2. 创建对象，设置任务数据（data project）并调用joinTimeWheel方法加入调度器

