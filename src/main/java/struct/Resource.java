package struct;

import event.impl.FreeResourceEvent;
import event.impl.RequestFailEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import observer.Observer;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Resource {
    // 资源名称
    private String name;
    // 资源数量
    private Integer num;

    // 监听者
    private Observer processManager;




    // 回收资源
    public boolean recoveryResource(PCB pcb, int count)
    {
        if(!pcb.getResources().containsKey(name))
        {
            //System.out.println("process not own this resource\n");
            return false;
        }
        if(count > pcb.getResources().get(name))
        {
            //System.out.println(pcb.getPid() + "has not enough resource to release\n");
            return false;
        }
        num += count;
        int i = pcb.getResources().get(name).intValue();
        i -= count;
        pcb.getResources().put(name, i);
        if(pcb.getResources().get(name) == 0)
            pcb.getResources().remove(name);

        System.out.printf("%s released %d %s\n", pcb.getPid(), count, name);
        FreeResourceEvent freeResourceEvent = new FreeResourceEvent(this);
        processManager.onAction(freeResourceEvent);
        return true;
    }


    // 分配资源
    public boolean distributionReousrce(PCB pcb , int count)
    {
        // 资源不足
        if(count > num)
        {
            RequestFailEvent requestFailEvent = new RequestFailEvent(pcb);
            pcb.setBlockReason(name);
            processManager.onAction(requestFailEvent);
            System.out.println("resources not enough, request failed, process is transfered to block queue");
            return false;
        }
        if(!pcb.getResources().containsKey(name))
        {
            pcb.getResources().put(name, count);
            num -= count;
        }
        else
        {
            Map<String, Integer> resources = pcb.getResources();
            resources.put(name, resources.get(name) + count);
            num -= count;
        }
        System.out.println("request resource secceed");
        return true;
    }

}
