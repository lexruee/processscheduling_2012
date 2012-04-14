/* import */
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import pssimulator.Simulator;
import pssimulator.SimulatorStatistics;

public class SRTF implements pssimulator.Kernel {

    public LinkedList<Process> readyQueue = new LinkedList<Process>();
    // note: head of this queue is always the currently running process
    // if this is empty, then the currently running process is the idle process
    public List<Process> terminatedProcess = new LinkedList<Process>();
    public Map<String, IODevice> devices = new HashMap<String, IODevice>();
    public Process idleProcess = new Process("Idle", 0);

    // accounting information
    private int savesCount;
    private long turnArroundTime;
    private long waitingMeanTime;

    private PrintStream out = new PrintStream(new NullOutputStream());
    private boolean isIdle = false;

    /* methods */
    public void systemCallInitIODevice(String deviceID, Simulator simulator) {
	this.devices.put(deviceID, new IODevice(deviceID));
    }

    /*
     * create a new process
     */
    public void systemCallProcessCreation(String processId, long timer,
	    Simulator simulator) {
	// create a new process with pid & arrival time
	Process p = new Process(processId, timer);
	// add this process to the ready queue
	this.readyQueue.offer(p);
	
	this.sortReadyQueue(simulator);
    }

    private void sortReadyQueue(Simulator simulator) {
	if (this.readyQueue.size() > 1) {
	    java.util.Collections.sort(this.readyQueue, new ProcessComparator(
		    simulator));
		this.savesCount++;
	}
	this.out.println("Shortest Jobs: \n" + this.readyQueue);
    }

    /*
     * user process makes a system call
     */
    public void systemCallIORequest(String deviceID, long timer,
	    Simulator simulator) {
	// remove running process from the head of the ready queue
	Process p = this.readyQueue.poll();

	IODevice device = this.devices.get(deviceID);
	// place this process at the end of this io device queue
	device.offer(p);

	// count context switches
	this.savesCount++;
	this.sortReadyQueue(simulator);
    }

    /*
     * user process terminates
     */
    public void systemCallProcessTermination(long timer, Simulator simulator) {
	// get the running process
	Process p = this.readyQueue.poll();
	// set the completion time
	p.setCompletionTime(timer);

	// add the terminated process to a list, for wmt & tat purposes
	this.terminatedProcess.add(p);

	// update the system idle sate
	if (!this.readyQueue.isEmpty())
	    this.isIdle = false; // there are still process
	else
	    // there are no other process
	    this.isIdle = true;
	
	this.sortReadyQueue(simulator);
    }

    /*
     * an io device interrupt occurs
     */
    public void interruptIODevice(String deviceID, long timer,
	    Simulator simulator) {
	// remove process from device queue
	IODevice device = this.devices.get(deviceID);
	Process p = device.poll();
	// and place it in ready queue
	this.readyQueue.offer(p);
	// start waiting timer, thus the process is now waiting
	p.startWaitingTimer(timer);
	
	this.sortReadyQueue(simulator);
    }

    public void interruptPreemption(long timer, Simulator simulator) {
	
    }

    /*
     * return currently running process
     */
    public String running(long timer, Simulator sim) {
	Process current;
	// check if system is in a idle state
	if (this.isIdle == false) {
	  if(this.readyQueue.size()!=0){
	    
	    Process p = this.readyQueue.peek(); // get the head of this queue /
						// current running process
	    if (p.isWaiting()) {
		p.stopWaitingTimer(timer);
	    }
	    current = p;
	  } else {
	      current = this.idleProcess;
	  }
	} else {
	    current = this.idleProcess;
	}

	this.out.println("running pid: " + current + ", WT: "
		+ current.getWaitingTime() + ", current timer: " + timer);
	return current.getId();
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
