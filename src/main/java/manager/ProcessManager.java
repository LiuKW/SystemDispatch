package manager;

import enums.StatusEnum;
import event.Event;
import event.impl.FreeResourceEvent;
import event.impl.RequestFailEvent;
import event.impl.StatusChangeEvent;
import lombok.Data;
import observer.Observer;
import struct.PCB;
import struct.Resource;

import java.util.*;


/**
 * 进程管理器
 */
@Data
public class ProcessManager implements Observer {

    // 就绪队列
    private PriorityQueue<PCB> readyQueue = new PriorityQueue<>(11, (pcb1,pcb2)->{
        if(pcb1.getDispatchTime() == pcb2.getDispatchTime())
            return pcb2.getPriority() - pcb1.getPriority();
        return pcb1.getDispatchTime()-pcb2.getDispatchTime();
    });

    // 阻塞队列
    private PriorityQueue<PCB> blockQueue = new PriorityQueue<>();

    // 等待队列
    private PriorityQueue<PCB> waitQueue = new PriorityQueue<>(11, (pcb1,pcb2)->{
        if(pcb1.getDispatchTime() == pcb2.getDispatchTime())
            return pcb2.getPriority() - pcb1.getPriority();
        return pcb1.getDispatchTime()-pcb2.getDispatchTime();
    });
    /**
     * 这里想用方法引用，但是会报错，报无法推断参数错误。。。why
     * TODO: why cannot infer arguments
     */


    // 正在运行的进程
    private final Queue<PCB> runningQueue = new LinkedList<>();

    // 保存当前系统中所有的线程
    private Map<String, PCB> pcbMap = new HashMap<>();



    // 创建了三个资源
    private List<Resource> resources = new ArrayList<>();
    {
        Resource resource1 = new Resource();
        Resource resource2 = new Resource();
        Resource resource3 = new Resource();
        resource1.setName("r1").setNum(10).setProcessManager(this);
        resource2.setName("r2").setNum(5).setProcessManager(this);
        resource3.setName("r3").setNum(3).setProcessManager(this);

        resources.add(resource1);
        resources.add(resource2);
        resources.add(resource3);
    }


    // 状态，队列映射
    private Map<Integer, Queue> map = new HashMap<>();
    {
        map.put(StatusEnum.READY.getCode(), readyQueue);
        map.put(StatusEnum.BLOCK.getCode(), blockQueue);
        map.put(StatusEnum.WAIT.getCode(), waitQueue);
        map.put(StatusEnum.RUNNING.getCode(), runningQueue);
    }




    // 创建进程
    public boolean createProcess(String pid, Integer priority)
    {
        // 刚创建的线程处于等待队列
        PCB pcb = new PCB(StatusEnum.WAIT.getCode());
        pcb.setPid(pid).setPriority(priority).setProcessManager(this);
        pcbMap.put(pid, pcb);
        waitQueue.add(pcb);
        System.out.println("creat process succeed");
        return true;
    }


    // 进程申请资源
    public void reqeustResource(PCB pcb , String resourceName, Integer count)
    {
        resources.forEach(item -> {
            if(item.getName().equals(resourceName))
                item.distributionReousrce(pcb, count);
        });
    }


    // 进程释放资源
    public void freeResource(PCB pcb)
    {
        resources.forEach(item -> {
            if(pcb.getResources().containsKey(item.getName()))
            {
                item.recoveryResource(pcb, pcb.getResources().get(item.getName()));
            }
        });
    }


    public void freeResource(PCB pcb, String resourceName)
    {
        resources.forEach(item -> {
            if(pcb.getResources().containsKey(resourceName))
            {
                item.recoveryResource(pcb, pcb.getResources().get(item.getName()));
            }
        });
    }

    public void freeResource(PCB pcb, String resourceName, int count)
    {
        resources.forEach(item -> {
            if(pcb.getResources().containsKey(resourceName))
                item.recoveryResource(pcb, count);
        });
    }

    // 销毁进程
    public boolean dropProcess(PCB pcb)
    {
        pcbMap.remove(pcb.getPid(), pcb);

        // 找到在那个队列中
        switch(findProcessLoc(pcb))
        {
            case "readyQueue":
                freeResource(pcb);
                readyQueue.remove(pcb);
                break;
            case "blockQueue":
                freeResource(pcb);
                blockQueue.remove(pcb);
                break;
            case "waitQueue":
                freeResource(pcb);
                waitQueue.remove(pcb);
                break;
            case "runningQueue":
                freeResource(pcb);
                runningQueue.clear();
                break;
        }
        return true;
    }

    // time out
    public void timeOut(PCB pcb)
    {
        pcb.setBlockReason("time out");
        pcb.setStatus(StatusEnum.BLOCK.getCode());
        System.out.printf("%s is blocked,because of time out\n", pcb.getPid());
    }

    // 把因为time out而阻塞的进程调度到就绪队列
    public void timeOut2Ready()
    {
        if(blockQueue.isEmpty())
        {
            System.out.println("block queue is empty");
            return;
        }
        blockQueue.forEach(item->{
            if(item.getBlockReason().equals("time out"))
            {
                item.setBlockReason("");
                item.setStatus(StatusEnum.READY.getCode());
                System.out.printf("%s is tranfered to ready queue\n", item.getPid());
            }
        });
    }


    // 找到pcb
    public PCB findPCB(String pid)
    {
        return pcbMap.get(pid);
    }



    // 状态转化
    public boolean changeStatus(PCB pcb, Integer status)
    {
        pcb.setStatus(status);
        return true;
    }


    // 调度
    public boolean dispatchProcess(PCB pcb, String loc)
    {
        switch (loc){
            case "readyQueue":
                PCB poll = readyQueue.poll();
                Queue queue = map.get(pcb.getStatus());
                queue.add(poll);
                break;

            case "blockQueue":
                PCB poll1 = blockQueue.poll();
                Queue queue1 = map.get(pcb.getStatus());
                queue1.add(poll1);
                break;

            case "waitQueue":
                PCB poll2 = waitQueue.poll();
                Queue queue2 = map.get(pcb.getStatus());
                queue2.add(poll2);
                break;

            case "runningQueue":
                PCB poll3 = runningQueue.poll();
                Queue queue3 = map.get(pcb.getStatus());
                queue3.add(poll3);
                break;
        }
        return true;
    }


    // 寻找进程当前所处的队列
    public String findProcessLoc(PCB pcb)
    {
        if(readyQueue.contains(pcb))
            return "readyQueue";
        if(blockQueue.contains(pcb))
            return "blockQueue";
        if(waitQueue.contains(pcb))
            return "waitQueue";
        if(runningQueue.size() != 0)
        {
            if(runningQueue.peek() == pcb)
                return "runningQueue";
        }
        return "";
    }



    // 展示所有静态资源
    public void showResource()
    {
        resources.forEach(item -> {
            System.out.printf("resource: %s\t\t count: %d\t\t\n", item.getName(), item.getNum());
        });
    }

    // 展示一个资源
    public void showResource(String resourceName)
    {
        resources.forEach(item -> {
            if(item.getName().equals(resourceName))
                System.out.printf("resource: %s\t\t count: %d\t\t\n", item.getName(), item.getNum());
        });
    }


    // 状态转化成英文
    public String convertStatus(int status)
    {
        String convert2Ch = status == StatusEnum.READY.getCode() ? StatusEnum.READY.getMessage() : (status == StatusEnum.BLOCK.getCode() ? StatusEnum.BLOCK.getMessage() : (status == StatusEnum.RUNNING.getCode() ? StatusEnum.RUNNING.getMessage() : StatusEnum.READY.getMessage()));
        return convert2Ch;
    }

    // 展示一个进程的状态
    public void showProcess(String pid)
    {
        PCB pcb = pcbMap.get(pid);
        int status = pcb.getStatus();
        String convertCh = convertStatus(status);
        System.out.printf("pid: %s\t\t status: %s\t\t priority: %d\t\t dispatchTime: %d\t\t blockReason: %s\n", pid, convertCh, pcb.getPriority().intValue(), pcb.getDispatchTime(), pcb.getBlockReason() == "" ? "not blocked" : pcb.getBlockReason());
    }

    // 展示所有进程的状态
    public void showProcess()
    {
        Iterator<Map.Entry<String, PCB>> iterator = pcbMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PCB> next = iterator.next();
            int status = next.getValue().getStatus();
            String convertCh = convertStatus(status);
            System.out.printf("pid: %s\t\t status: %s\t\t priority: %d\t\t dispatchTime: %d\t\t blockReason: %s\n", next.getKey(),convertCh, next.getValue().getPriority().intValue(), next.getValue().getDispatchTime(), next.getValue().getBlockReason() == "" ? "not blocked" : next.getValue().getBlockReason());
        }
        if(pcbMap.size() == 0)
            System.out.println("System has no process");
    }

    // 展示当前正在运行的进程
    public void showRunningProcess()
    {
        if(runningQueue.size() == 0)
        {
            System.out.println("当前系统没有正在执行的进程");
            return;
        }
        PCB pcb = runningQueue.peek();

        System.out.printf("%s is running\n", pcb.getPid());
    }


    // 模拟自动调度
//    public void dispatch()
//    {
//        if(waitQueue.size() != 0)
//        {
//            PCB poll = waitQueue.poll();
//            readyQueue.add(poll);
//        }
//        if(readyQueue.size() != 0)
//        {
//            PCB poll = readyQueue.poll();
//            if(runningList.size() != 0)
//            {
//                PCB pcb = runningList.get(0);
//                if(pcb.getStatus() == StatusEnum.BLOCK.getCode())
//                    runningList.remove(0);
//                else
//                    pcb.setStatus(StatusEnum.READY.getCode());
//                poll.setStatus(StatusEnum.RUNNING.getCode());
//                runningList.remove(runningList.get(0));
//                runningList.add(poll);
//            }
//            else {
//                poll.setStatus(StatusEnum.RUNNING.getCode());
//                runningList.add(poll);
//            }
//            System.out.printf("%s is running\n", poll.getPid());
//            return;
//        }
//        if(runningList.get(0).getStatus() == StatusEnum.BLOCK.getCode())
//            runningList.remove(runningList.get(0));
//        if(runningList.size() != 0)
//            System.out.printf("No process is ready, %s is still running\n", runningList.get(0).getPid());
//        else
//            System.out.println("System is free now\n");
//    }

    // 模拟自动调度
    public void dispatch()
    {
        if(runningQueue.size() != 0)
        {
            runningQueue.peek().setStatus(StatusEnum.WAIT.getCode());
        }
        // 把等待队列中的PCB放到就绪队列
        if(waitQueue.size() != 0)
        {
            waitQueue.peek().setStatus(StatusEnum.READY.getCode());
        }

        // 从就绪队列中拿出一个出来
        if(readyQueue.size() != 0)
        {
            PCB peek = readyQueue.peek();
            peek.setDispatchTime(peek.getDispatchTime()+1).setStatus(StatusEnum.RUNNING.getCode());
            System.out.printf("%s is running\n", runningQueue.peek().getPid());
            return;
        }
        else
        {
            if(runningQueue.size() != 0)
                System.out.printf("%s is still running\n", runningQueue.peek().getPid());
            else
                System.out.println("System free");
        }
    }




    // 事件处理函数
    @Override
    public void onAction(Event event) {

        // 状态改变
        if(event instanceof StatusChangeEvent)
        {
            PCB pcb = (PCB)event.getSource();

            String loc = findProcessLoc(pcb);

            // 调度
            dispatchProcess(pcb, loc);
            return;
        }

        // 请求资源失败
        if(event instanceof RequestFailEvent)
        {
            PCB pcb = (PCB)event.getSource();
            pcb.setStatus(StatusEnum.BLOCK.getCode());
            return;
        }


        // 有资源释放了
        if(event instanceof FreeResourceEvent)
        {
            Resource resource = (Resource)event.getSource();
            blockQueue.forEach(item->{
                item.getBlockReason().equals(resource.getName());
                item.setBlockReason("");
                item.setStatus(StatusEnum.READY.getCode());
            });
            return;
        }
    }

//    public String toString() {
//        return getClass().getName();
//    }

    private ProcessManager() {}
    private static final ProcessManager processManager = new ProcessManager();
    public static ProcessManager getInstance() { return processManager; }
}
