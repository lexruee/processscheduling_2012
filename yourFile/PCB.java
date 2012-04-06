
public class PCB  {
		private pssimulator.Simulator sim;
		private String pid;
		private long timer;
		public long arrivalTime;
		private long burstTime;
		private long waitingTime;
		
		public PCB(pssimulator.Simulator sim, String pid, long time ){
			this.sim = sim;
			this.pid = pid;
			this.arrivalTime = time;
			this.burstTime = sim!=null ? sim.queryBurstRemainingTime(this.pid): 0;
		}
		


		long queryOverallTime(){
			return sim.queryOverallTime(pid);
		}
		
		long queryBurstRemainingTime(){
			return sim.queryBurstRemainingTime(pid);
		}
		
		long getTimer(){
			return timer;
		}
		
		public String toString(){
			return this.pid;
		}
		
		public String getId(){
			return pid;
		}


		public void setRemainingBurstTime(long timer) {
			this.burstTime -= timer;
			
		}


		public long getRemainingBurstTime() {
			return this.burstTime;
		}
		
		public long getArrivalTime(){
			return this.arrivalTime;
		}



		public void setArrivalTime(long time) {
			this.arrivalTime = time;
			
		}

		public void setWaitingTime(long time) {
			this.waitingTime = time - this.arrivalTime;
		}
		
		public boolean isIdle(){
			return this.pid.equals("Idle");
		}



		public long getWaitingTime() {
			return this.waitingTime;
		}
		
	}