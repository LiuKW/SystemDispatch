package event.impl;

import event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import struct.PCB;

@AllArgsConstructor
@Data
public class StatusChangeEvent implements Event<PCB> {
    private PCB pcb;

    @Override
    public PCB getSource() {
        return pcb;
    }
}
