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
            processManager.showAllProcess();
            return;
        }
        // top all
        if(commands[1].equals("all"))
        {
            processManager.showAllProcess();
            return;
        }
        // top pid
        else
            processManager.showOneProcess(commands[1]);
    }


    /**
     * kill命令
     * kill命令格式，kill pid
     */
    public void doKill(String commands[]) {
        PCB pcb = processManager.findPCB(commands[1]);
        processManager.dropProcess(pcb);
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
        try {
            Runtime.getRuntime().exec("cmd cls");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}