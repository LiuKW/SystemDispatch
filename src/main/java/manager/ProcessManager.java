package manager;

import enums.StatusEnum;
import event.Event;
import event.impl.FreeResourceEvent;
import event.impl.RequestFailEvent;
import event.impl.StatusChangeEvent;
import lombok.Data;
import observer.Observer;
import org.omg.CORBA.Object;
import struct.PCB;
import struct.Resource;

import java.util.*;


/**
 * 进程管理器
 */
@Data
public class ProcessManager implements Observer {

    // 就绪队列
    private PriorityQueue<PCB> readyQueue = new PriorityQueue<>(11, (pcb1,pcb2)->{return pcb2.getPriority()-pcb1.getPriority();});

    // 阻塞队列
    private PriorityQueue<PCB> blockingQueue = new PriorityQueue<>();

    // 等待队列
    private Queue<PCB> waitQueue = new LinkedList<>();

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
        map.put(StatusEnum.BLOCK.getCode(), blockingQueue);
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
        return true;
    }


    // 进程申请资源
    public void reqeustResource(PCB pcb , String resourceName, Integer count)
    {
//        resources.forEach(item -> {
//            if(item.getName().equals(resourceName))
//                item.distributionReousrce(pcb, count);
//        });
        for (Resource resource : resources) {
            if(resource.getName().equals(resourceName))
                resource.distributionReousrce(pcb, count);
        }
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
            case "blockingQueue":
                freeResource(pcb);
                blockingQueue.remove(pcb);
                break;
            case "waitQueue":
                freeResource(pcb);
                waitQueue.remove(pcb);
                break;
            case "running":
                freeResource(pcb);
                runningQueue.clear();
                break;
        }
        return true;
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

            case "blockingQueue":
                PCB poll1 = blockingQueue.poll();
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
        if(blockingQueue.contains(pcb))
            return "blockingQueue";
        if(waitQueue.contains(pcb))
            return "waitQueue";
        if(runningQueue.size() != 0)
        {
            if(runningQueue.peek() == pcb)
                return "runningQueue";
        }
        return "";

    }


    // 状态转化成中文
    public String convertStatus(int status)
    {
        String convert2Ch = status == StatusEnum.READY.getCode() ? StatusEnum.READY.getMessage() : (status == StatusEnum.BLOCK.getCode() ? StatusEnum.BLOCK.getMessage() : (status == StatusEnum.RUNNING.getCode() ? StatusEnum.RUNNING.getMessage() : StatusEnum.READY.getMessage()));
        return convert2Ch;
    }

    // 展示一个进程的状态
    public void showOneProcess(String pid)
    {
        PCB pcb = pcbMap.get(pid);
        int status = pcb.getStatus();
        String convertCh = convertStatus(status);
        System.out.printf("pid: %s, status: %s, blockReason: %s\n", pid, convertCh, pcb.getBlockReason() == "" ? "isn't blocked" : pcb.getBlockReason());
    }

    // 展示所有进程的状态
    public void showAllProcess()
    {
        Iterator<Map.Entry<String, PCB>> iterator = pcbMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, PCB> next = iterator.next();
            int status = next.getValue().getStatus();
            String convertCh = convertStatus(status);
            System.out.printf("pid: %s, status: %s, blockReason: %s\n", next.getKey(),convertCh, next.getValue().getBlockReason() == "" ? "isn't blocked" : next.getValue().getBlockReason());
        }
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


    // 自动调度
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

    // 自动调度
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
            readyQueue.peek().setStatus(StatusEnum.RUNNING.getCode());
            System.out.printf("%s is running\n", runningQueue.peek().getPid());
            return;
        }
        else
        {
            if(runningQueue.size() != 0)
            {
                System.out.printf("%s is still running\n", runningQueue.peek().getPid());
            }
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
            blockingQueue.forEach(item->{
                item.getBlockReason().equals(resource.getName());
                item.setBlockReason("");
                item.setStatus(StatusEnum.READY.getCode());
            });
            return;
        }

    }

    public String toString() {
        return getClass().getName();
    }

}
