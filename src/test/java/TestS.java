import enums.StatusEnum;
import manager.ProcessManager;
import org.junit.Assert;
import org.junit.Test;
import struct.PCB;

import java.util.*;


public class TestS {


    @Test
    public void test01()
    {
        PriorityQueue<PCB> pcbs = new PriorityQueue<>(11, (pcb1, pcb2)->{
            return pcb2.getPriority() - pcb1.getPriority();
        });
        {
            PCB pcb1 = new PCB().setPid("A").setPriority(1);
            PCB pcb2 = new PCB().setPid("B").setPriority(3);
            PCB pcb3 = new PCB().setPid("C").setPriority(2);
            PCB pcb4 = new PCB().setPid("D").setPriority(4);

            pcbs.add(pcb1);
            pcbs.add(pcb2);
            pcbs.add(pcb3);
            pcbs.add(pcb4);


            while(pcbs.size() != 0)
            {
                System.out.println(pcbs.poll().getPid());
            }
        }
    }


    @Test
    public void test02()
    {
        List<PCB> list = new ArrayList<>();

        List<PCB> list1 = new ArrayList<>();

        PCB pcb = new PCB();
        pcb.setPid("A");

        list.add(pcb);
        list1.add(pcb);

        System.out.println("1");
        list.forEach(System.out::println);
        list1.forEach(System.out::println);

        System.out.println("2");
        list1.get(0).setPid("B");
        list.forEach(System.out::println);
        list1.forEach(System.out::println);
    }




    @Test
    public void test03()
    {

        int status = StatusEnum.RUNNING.getCode();

        /**
         * String result =	a.equals("A") ? "A条件的结果" : (a.equals("B") ? "B条件的结果" : (a.equals("C") ? "C条件的结果" : "A条件的结果"));
         */

        String result = status == StatusEnum.READY.getCode() ? StatusEnum.READY.getMessage() : (status == StatusEnum.BLOCK.getCode() ? StatusEnum.BLOCK.getMessage() : (status == StatusEnum.RUNNING.getCode() ? StatusEnum.RUNNING.getMessage() : StatusEnum.READY.getMessage()));
        System.out.println(result);
    }



    @Test
    public void test04()
    {
        List<PCB> list = new ArrayList<>();

        List<PCB> list1 = new ArrayList<>();

        PCB pcb = new PCB();
        pcb.setPid("A");

        list.add(pcb);
        list1.add(pcb);

        System.out.println("1");
        list.forEach(System.out::println);
        list1.forEach(System.out::println);

        System.out.println("2");
        list1.get(0).setPid("B");
        list.forEach(System.out::println);
        list1.forEach(System.out::println);
    }


    @Test
    public void test05()
    {
        ProcessManager processManager = new ProcessManager();
        Assert.assertNotNull(processManager.getMap());

    }



}
