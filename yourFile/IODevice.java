import java.util.LinkedList;
import java.util.Queue;


public class IODevice {
    
    private String deviceID;
    private Queue<Process> queue = new LinkedList<Process>();

    public IODevice(String deviceID){
	
	this.deviceID = deviceID;
    }
    
    public void offer(Process p){
	this.queue.offer(p);
    }
    
    public Process  poll(){
	return this.queue.poll();
    }
    
    public void remove(Process p){
	this.queue.remove(p);
    }
    
    public String toString(){
	return this.deviceID;
    }

}
