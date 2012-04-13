public class Process {
    private pssimulator.Simulator sim;
    private String pid;
    public long arrivalTime;
    private long waitingTime;
    private long terminationTime;
    private long waitingTimer;

    public Process(pssimulator.Simulator sim, String pid, long timer) {
	this.sim = sim;
	this.pid = pid;
	this.arrivalTime = timer;
    }

    public String toString() {
	return this.pid;
    }

    public String getId() {
	return pid;
    }

    public long getArrivalTime() {
	return this.arrivalTime;
    }

    public boolean isIdle() {
	return this.pid.equals("Idle");
    }

    public long getWaitingTime() {
	return this.waitingTime;
    }

    public void setTerminationTime(long timer) {
	this.terminationTime = timer;

    }

    /**
     * start a waiting timer
     */
    public void startWaitingTimer(long timer) {
	this.waitingTimer = timer;

    }

    /**
     * finish a waiting timer and add the difference to the waiting time of this
     * process
     */
    public void finishWatingTimer(long timer) {
	long timeDifference = timer - this.waitingTimer;
	this.waitingTime += timeDifference;
    }

    public long getCompletionTime() {
	return this.terminationTime - this.arrivalTime;
    }

}