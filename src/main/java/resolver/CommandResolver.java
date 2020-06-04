package resolver;

import lombok.AllArgsConstructor;
import manager.ProcessManager;
import struct.PCB;

import java.io.IOException;

/**
 * 命令解析器
 */

@AllArgsConstructor
public class CommandResolver {
    private ProcessManager processManager;

    public void resolve(String s) {
        String[] commands = s.split(" ");
        switch (commands[0]) {
            case "top":
                doTop(commands);
                break;
            case "kill":
                doKill(commands);
                break;
            case "req":
                doRequest(commands);
                break;
            case "create":
                doCreate(commands);
                break;
            case "run":
                doRun(commands);
                break;
            case "clear":
                doClear();
                break;
            case "rel":
                doRelease(commands);
                break;
            case "list":
                doList(commands);
                break;
            case "to":
                doTimeOut(commands);
                break;
            case "to2run":
                doTimeOut2Run(commands);
                break;
            default:
                System.out.println("command not found");
                return;
        }
    }

    /**
     * top 命令
     * top 命令格式
     *      top：显示当前运行的进程
     *      top pid：显示pid
     *      top all：显示全部进程
     */

    public void doTop(String commands[]) {
        // top
        if(commands.length == 1)
        {
            processManager.showProcess();
            return;
        }
        // top pid
        else
            processManager.showProcess(commands[1]);
    }


    /**
     * kill命令
     * kill命令格式，kill pid
     */
    public void doKill(String commands[]) {
        PCB pcb = processManager.findPCB(commands[1]);
        if(pcb != null)
            processManager.dropProcess(pcb);
        else
            System.out.println("no appoint process in this system");
    }

    /**
     * create命令
     * create命令格式 create pid priority
     */
    public void doCreate(String commands[]) {
        processManager.createProcess(commands[1], Integer.valueOf(commands[2]));
    }

    /**
     * req命令
     * req命令 req resourceName count
     */
    public void doRequest(String commands[]) {
        if(processManager.getRunningQueue().size() == 0)
        {
            System.out.println("当前没有进程正在运行，无法申请资源");
            return;
        }
        PCB pcb = processManager.getRunningQueue().peek();
        processManager.reqeustResource(pcb, commands[1], Integer.valueOf(commands[2]));
    }

    /**
     * run命令
     * run命令就一个
     */
    public void doRun(String commands[]){
        processManager.dispatch();
    }

    /**
     * 清屏
     */
    public void doClear()
    {
        int i = 50;
        while(i-- != 0)
            System.out.println();
    }

    /**
     * release：释放资源
     * release命令 release
     */
    public void doRelease(String commands[])
    {
        PCB peek = processManager.getRunningQueue().peek();
        if(commands.length == 1)
            processManager.freeResource(peek);
        else if(commands.length == 2)
            processManager.freeResource(peek, commands[1]);
        else
            processManager.freeResource(peek, commands[1], Integer.valueOf(commands[2]));
    }

    /**
     * list：展示所有资源
     * list 命令 list / list r1
     */
    public void doList(String commands[])
    {
        if(commands.length == 1)
            processManager.showResource();
        else
            processManager.showResource(commands[1]);
    }


    /**
     *
     */
    public void doTimeOut(String commands[])
    {
        PCB peek = processManager.getRunningQueue().peek();
        if(peek == null)
        {
            System.out.println("当前没有进程正在运行，无法申请资源");
            return;
        }

        processManager.timeOut(peek);
    }

    public void doTimeOut2Run(String commands[])
    {
        processManager.timeOut2Ready();
    }

}