
/* import */
import java.util.LinkedList;
import java.util.Queue;

import pssimulator.Simulator;
import pssimulator.SimulatorStatistics;


public class RR implements pssimulator.Kernel {

    /* members */
	Queue<PCB> readyQueue = new LinkedList<PCB>();
	private PCB running;
	private PCB nil;
	private long waitingMeanTime;
	private long turnArroundTime;
	private int savesCount;
	private int ps = 0;
	private int timeQuantum = 2;

    /* methods */

    public void systemCallInitIODevice(String deviceID, Simulator simulator) {
    }

    public void systemCallProcessCreation(String processID, long timer, Simulator simulator) {

    }

    public void systemCallIORequest(String deviceID, long timer, Simulator simulator) {
    }

    public void systemCallProcessTermination(long timer, Simulator simulator) {

    }

    public void interruptIODevice(String deviceID, long timer, Simulator simulator) {
    }

    public void interruptPreemption(long timer, Simulator simulator) {
    }

    public String running(long timer, Simulator simulator) {
    	return "";
    	
    }

    public void terminate(long timer, SimulatorStatistics sim) {
    	
   }
}
