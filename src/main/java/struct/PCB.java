package struct;

import event.impl.StatusChangeEvent;
import lombok.*;
import lombok.experimental.Accessors;
import observer.Observer;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class PCB {
    private String pid;
    private Map<String, Integer> resources = new HashMap<>();     // 当前pcb占有哪些资源和资源占有数量
    private Integer priority;
    private Integer status;     // 当前状态
    private String blockReason = ""; //阻塞原因，等待那个资源
    private int dispatchTime = 0;
    private Observer processManager;


    public PCB setStatus(Integer status)
    {
        this.status = status;
        StatusChangeEvent statusChangeEvent = new StatusChangeEvent(this);
        processManager.onAction(statusChangeEvent);
        return this;
    }

    public PCB(Integer status) {
        this.status = status;
    }
}


