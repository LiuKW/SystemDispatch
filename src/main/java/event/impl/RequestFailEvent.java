package event.impl;

import event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import struct.PCB;

/**
 * 请求资源失败事件
 */
@Data
@AllArgsConstructor
public class RequestFailEvent implements Event<PCB> {
    private PCB pcb;

    @Override
    public PCB getSource() {
        return pcb;
    }
}
