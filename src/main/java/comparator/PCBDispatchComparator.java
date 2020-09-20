package comparator;

import struct.PCB;

public class PCBDispatchComparator {
    public static int compare(PCB pcb1, PCB pcb2) {
        if(pcb1.getDispatchTime() == pcb2.getDispatchTime())
            return pcb2.getPriority() - pcb1.getPriority();
        return pcb1.getDispatchTime()-pcb2.getDispatchTime();
    }
}
