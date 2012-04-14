import java.util.Comparator;


public class ProcessComparator implements Comparator<Process> {

    private pssimulator.Simulator simulator;

    public ProcessComparator(pssimulator.Simulator simulator) {
	this.simulator = simulator;
    }

    @Override
    public int compare(Process p1, Process p2) {
	long burstTime1 = simulator.queryBurstRemainingTime(p1.getId());
	long burstTime2 = simulator.queryBurstRemainingTime(p2.getId());
	p1.setRBT(burstTime1);
	p2.setRBT(burstTime2);
	return (int)(burstTime1 - burstTime1);
    }

}
