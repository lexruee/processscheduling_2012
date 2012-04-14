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
    // note: head of this queue is always the currently running process
    // if this is empty, then the currently running process is the idle process
    public List<Process> terminatedProcess = new LinkedList<Process>();
    public Map<String, IODevice> devices = new HashMap<String, IODevice>();
    public Process idleProcess = new Process("Idle", 0);
    
    //accounting information
    private int savesCount;
    private long turnArroundTime;
    private long waitingMeanTime;
    
    private PrintStream out =  System.out;
    private boolean isIdle = false;

    /* methods */
    public void systemCallInitIODevice(String deviceID, Simulator simulator) {
	this.devices.put(deviceID, new IODevice(deviceID));
    }

    /*
     * create a  new process
     */
    public void systemCallProcessCreation(String processId, long timer,
	    Simulator simulator) {
	// create a new process with pid & arrival time
	Process p = new Process(processId, timer);
	// add this process to the ready queue
	this.readyQueue.offer(p);
	
	this.interruptPreemptionHelper(simulator);
    }


    /*
     * user process makes a system call
     */
    public void systemCallIORequest(String deviceID, long timer,
	    Simulator simulator) {
	out.println("systemCallIORequest pid: " + this.readyQueue.peek()
		+ ", device: " + deviceID);
	//remove running process from the head of the ready queue
	Process p = this.readyQueue.poll();
	
	IODevice device = this.devices.get(deviceID);
	//place this process at the end of this io device queue
	device.offer(p);

	// count context switches
	this.savesCount++;
    }

    /*
     * user process terminates
     */
    public void systemCallProcessTermination(long timer, Simulator simulator) {
	// get the running process
	Process p = this.readyQueue.poll();
	// set the completion time
	p.setCompletionTime(timer);
	
	//add the terminated process to a list, for wmt & tat purposes
	this.terminatedProcess.add(p);
	
	//update the system idle sate
	if(!this.readyQueue.isEmpty())
	    this.isIdle = false; //there are still process
	else //there are no other process
	    this.isIdle = true;
	
	this.interruptPreemptionHelper(simulator);
    }

    /*
     * an io device interrupt occurs
     */
    public void interruptIODevice(String deviceID, long timer,
	    Simulator simulator) {

	//remove process from device queue
	IODevice device = this.devices.get(deviceID);
	Process p = device.poll();
	// and place it in ready queue
	this.readyQueue.offer(p);
	// start waiting timer, thus the process is now waiting 
	p.startWaitingTimer(timer);
	
	this.interruptPreemptionHelper(simulator);
    }

    public void interruptPreemption(long timer, Simulator simulator) {
	if (this.readyQueue.size() > 1) {
	    Process current = this.readyQueue.peek();
	    if (simulator.queryBurstRemainingTime(current.getId()) >= 0) {
		//swap current process with the next process in the ready queue
		current = this.readyQueue.poll();
		//start waiting timer, since the process is now waiting in the ready queue
		current.startWaitingTimer(timer);
		this.readyQueue.offer(current);
		
		//count context switches
		this.savesCount++;
	    }
	}

	this.interruptPreemptionHelper(simulator);
    }
    
    private void interruptPreemptionHelper(Simulator simulator) {
	if(this.readyQueue.size()!=0)
	    simulator.schedulePreemptionInterrupt(RR.TIME_QUANTUM);
	
    }

    /*
     * return currently running process
     */
    public String running(long timer, Simulator sim) {
	Process current;
	if(this.readyQueue.size()!=0){
	    current = this.readyQueue.peek();
	    //check if head of ready queue was waiting
	    if(current.isWaiting()==true){
		current.stopWaitingTimer(timer);
	    }
	} else {
	    current = this.idleProcess;
	}

        this.out.println("running pid: " + current + ", WT: " + current.getWaitingTime() + ", current timer: "+timer);
        return current.getId();
    }

    public void terminate(long timer, SimulatorStatistics sim) {
	
	for(Process p : this.terminatedProcess){
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
