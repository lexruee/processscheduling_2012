/* import */
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import pssimulator.Simulator;
import pssimulator.SimulatorStatistics;

public class RR implements pssimulator.Kernel {

    private static final long TIME_QUANTUM = 2;
    public Queue<Process> readyQueue = new LinkedList<Process>();
    public List<Process> terminatedProcess = new LinkedList<Process>();
    public Map<String, IODevice> devices = new HashMap<String, IODevice>();
    public Process idleProcess = new Process(null, "Idle", 0);
    public Process running = idleProcess;

    // accounting information
    private int savesCount;
    private long turnArroundTime;
    private long waitingMeanTime;

    private PrintStream out = System.out;

    /* methods */
    public void systemCallInitIODevice(String deviceID, Simulator simulator) {
	out.println("System Call " + deviceID);
	this.devices.put(deviceID, new IODevice(deviceID));
    }

    /**
     * a new process is created
     */
    public void systemCallProcessCreation(String processId, long timer,
	    Simulator simulator) {
	// create a new process
	Process p = new Process(simulator, processId, timer);
	out.println("Offer: " + p);

	// add this process to the ready queue
	this.readyQueue.offer(p);

    }

    /**
     * a user process makes a system call
     */
    public void systemCallIORequest(String deviceID, long timer,
	    Simulator simulator) {
	out.println("systemCallIORequest pid: " + this.running + ", device: "
		+ deviceID);
	// add process to the io waiting queue
	Process p = this.running;
	IODevice device = this.devices.get(deviceID);
	device.offer(p);

	this.running = this.idleProcess;

	// current running process is place into a waiting queue, save cpu
	// registers
	// count context switches
	this.savesCount++;
	
	this.interruptPreemptionHelper(timer, simulator);
    }

    /**
     * user process terminates
     */
    public void systemCallProcessTermination(long timer, Simulator simulator) {
	Process p = this.running;
	p.setCompletionTime(timer);
	this.terminatedProcess.add(p);
	this.running = this.idleProcess;
	out.println("Terminate pid: " + this.running + "TAT "
		+ this.running.getCompletionTime());
	
	this.interruptPreemptionHelper(timer, simulator);
    }

    /**
     * an io device interrupt occurs
     */
    public void interruptIODevice(String deviceID, long timer,
	    Simulator simulator) {
	out.println("interruptIODevice " + deviceID + ", running pid: "
		+ this.running);

	// get the process from the io waiting queue and put it back into the
	// ready queue
	IODevice device = this.devices.get(deviceID);
	Process p = device.poll();
	this.readyQueue.offer(p);
	


	p.startWaitingTimer(timer);
	this.interruptPreemptionHelper(timer, simulator);
    }

    public void interruptPreemption(long timer, Simulator simulator) {
	out.println("interruptPreemption for running pid: " + this.running
		+ ", current Timer: " + timer);
	// put the running process back into the ready queue
	Process p = this.running;
	if (!this.readyQueue.isEmpty()
		&& simulator.queryBurstRemainingTime(p.getId()) >= 0) {
	    p.startWaitingTimer(timer);
	    this.readyQueue.offer(p);
	    this.running = this.readyQueue.poll();
	    this.running.finishWatingTimer(timer);

	    // count context switches
	    this.savesCount++;

	}

	this.interruptPreemptionHelper(timer, simulator);
    }

    private void interruptPreemptionHelper(long timer, Simulator sim){
	if(!this.readyQueue.isEmpty())
	    sim.schedulePreemptionInterrupt(TIME_QUANTUM);
    }

    /**
     * scheduling user processes
     */
    public String running(long timer, Simulator sim) {
	sim.queryOverallTime(this.running.getId());

	// if system is idle then poll the next process from the ready queue
	if (this.running.equals(idleProcess)) {
	    if (!this.readyQueue.isEmpty()) {
		Process p = this.readyQueue.poll();
		p.finishWatingTimer(timer);
		this.running = p;
	    }
	}
	

	out.println("running pid: " + this.running + ", WT: "
		+ this.running.getWaitingTime() + ", current timer: " + timer);

	return this.running.getId();
    }

    public void terminate(long timer, SimulatorStatistics sim) {

	for (Process p : this.terminatedProcess) {
	    this.waitingMeanTime += p.getWaitingTime();
	    this.turnArroundTime += p.getCompletionTime();
	}
	this.waitingMeanTime /= this.terminatedProcess.size();
	this.turnArroundTime /= this.terminatedProcess.size();

	sim.formatStatistics(System.out, sim.getSystemTime(),
		sim.getUserTime(), sim.getIdleTime(),
		sim.getSystemCallsCount(), this.savesCount,
		this.waitingMeanTime, this.turnArroundTime);
    }
}
