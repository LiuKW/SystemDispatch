package event.impl;

import event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import struct.Resource;

/**
 * 释放资源事件
 */
@Data
@AllArgsConstructor
public class FreeResourceEvent implements Event<Resource> {
    private Resource resource;
    @Override
    public Resource getSource() {
        return resource;
    }
}
