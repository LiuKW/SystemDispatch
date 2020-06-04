# SystemDispatch
模拟系统调度
操作系统作业，模拟进程调度
使用了观察者模式、单例模式
***

#### 命令
* 展示进程状态（top）
  * top：展示所有进程状态
  * top pid：展示指定进程状态
  
  
* 展示资源（list）
  * list：展示一个资源
  * list resourceName：展示指定资源
  
  
* 创建进程（create pid priority）
 
 
* 杀死进程（kill pid）
 
 
* 调度一个进程（run）


* 申请指定资源指定个数（req resourceName count）
 
 
* 释放资源（rel）
  * rel：释放正在运行的进程的所有资源
  * rel resourceName：释放指定的所有资源
  * rel resourceName count： 释放指定资源指定个数
  
  
* 清除屏幕（clear）
