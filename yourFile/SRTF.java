
/* import */
import pssimulator.Simulator;
import pssimulator.SimulatorStatistics;

public class SRTF implements pssimulator.Kernel {

    /* members */

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
        return null;
    }

    public void terminate(long timer, SimulatorStatistics simulator) {
    }
}
