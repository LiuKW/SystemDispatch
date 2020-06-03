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
        if(pcb.getResources().get(name) == null)
        {
            return false;
        }
        Integer resources = pcb.getResources().get(name);
        this.num += count;
        FreeResourceEvent freeResourceEvent = new FreeResourceEvent(this);
        processManager.onAction(freeResourceEvent);
        return true;
    }


    // 分配资源
    public boolean distributionReousrce(PCB pcb , int count)
    {
        // 资源不足
        if(count > this.num)
        {
            RequestFailEvent requestFailEvent = new RequestFailEvent(pcb);
            pcb.setBlockReason(name);
            processManager.onAction(requestFailEvent);
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

        return true;
    }

}
