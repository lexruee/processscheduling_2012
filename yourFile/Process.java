public class Process {
    private String pid;
    public long arrivalTime;
    private long waitingTime;
    private long completionTime;
    private long waitingTimer;
    private boolean waiting;
    private long rbt;

    public Process(String pid, long timer) {
	this.pid = pid;
	this.arrivalTime = timer;
	this.waiting = true;
    }
    

    public String toString() {
	return this.pid + ", RBT: " + this.rbt;
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

    public void setCompletionTime(long timer) {
	this.completionTime = timer;

    }

    /**
     * start a waiting timer
     */
    public void startWaitingTimer(long timer) {
	this.waitingTimer = timer;
	this.waiting = true;

    }

    /**
     * finish a waiting timer and add the difference to the waiting time of this
     * process
     */
    public void stopWaitingTimer(long timer) {
	this.waiting = false;
	long timeDifference = timer - this.waitingTimer;
	this.waitingTime += timeDifference;
	this.waitingTimer = 0;
    }

    public long getCompletionTime() {
	return this.completionTime - this.arrivalTime;
    }

    public boolean isWaiting() {
	return this.waiting;
    }

    public void setRBT(long rbt) {
	this.rbt = rbt;
	
    }

}