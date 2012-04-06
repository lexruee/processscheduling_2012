//package pssimulator.algorithms;

/* import */
import pssimulator.Simulator;
import pssimulator.SimulatorStatistics;


import java.util.LinkedList;
import java.util.Queue;


import pssimulator.Simulator;
import pssimulator.SimulatorStatistics;


public class FCFS implements pssimulator.Kernel {
	

 
    /* members */
	public Queue<PCB> readyQueue = new LinkedList<PCB>();
	public Queue<PCB> jobQueue = new LinkedList<PCB>();
	public PCB nil = new PCB(null,"Idle", 0);
	public PCB running = nil;
	private int savesCount;
	private long turnArroundTime;
	private long waitingMeanTime;
	private int ps = 0;
	

    /* methods */
    public void systemCallInitIODevice(String deviceID, Simulator simulator) {
    	System.out.println("System Call " + deviceID);
    }

    public void systemCallProcessCreation(String processId, long timer, Simulator simulator) {
    	this.ps++;

    	//create a new pcb
    	PCB p = new PCB(simulator, processId, timer);
    	System.out.println("Offer: " + p);
    	//add this pcb to the queue
    	this.readyQueue.offer(p);
  
    	if(this.running.isIdle()){
    		this.running = this.readyQueue.poll();
    	}
    }

    public void systemCallIORequest(String deviceID, long timer, Simulator simulator) {
    	System.out.println("systemCallIORequest " + deviceID);
    }

    public void systemCallProcessTermination(long timer, Simulator simulator) {
    	
    	
    	this.running = this.nil;
    	
    	if(this.readyQueue.size()>0){ 
    		this.running = this.readyQueue.poll(); 
    		
    		if(this.readyQueue.size()>0)
    			this.waitingMeanTime += timer;
    		
    		this.turnArroundTime += timer;
    		this.running.setWaitingTime(timer);
    		
    	} else {
    		this.running = nil;
    	}
    }

    public void interruptIODevice(String deviceID, long timer, Simulator simulator) {
    	System.out.println("interruptIODevice " + deviceID);
    }

    public void interruptPreemption(long timer, Simulator simulator) {
    	System.out.println("interruptPreemption " + this.running);
    }

    public String running(long timer, Simulator sim) {
    	sim.queryOverallTime(this.running.getId());
    	System.out.println("pid: " + this.running + " waitingTime: "+ this.running.getWaitingTime() + "clock time: " + timer + " Remainig Burst Time: " + this.running.queryBurstRemainingTime() + "Overall Burst Time: " + this.running.queryOverallTime());
    	return this.running.getId();
    }

    public void terminate(long timer, SimulatorStatistics sim) {
    	this.waitingMeanTime = this.waitingMeanTime / this.ps;
    	this.turnArroundTime = this.turnArroundTime / this.ps;
    	sim.formatStatistics(System.out, sim.getSystemTime(), sim.getUserTime(), sim.getIdleTime(), sim.getSystemCallsCount(), this.savesCount, this.waitingMeanTime, this.turnArroundTime);
    }
}
