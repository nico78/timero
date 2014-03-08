package lockedstatus;

public class LockedStatusMonitor extends Thread {
	
	private final Sleeper sleeper;
	private volatile boolean running = true;
	
	private LockedStatusUpdater statusUpdater;
	public LockedStatusMonitor(LockedStatusUpdater statusUpdater, Sleeper sleeper) {
		this.statusUpdater = statusUpdater;
		this.sleeper = sleeper;
	}

	
	public void run() {
		while (isRunning()) {
			sleeper.sleep();
			statusUpdater.check();
		}
	}


	public boolean isRunning() {
		return running;
	}

	public void stopMonitor() {
		this.running = false;
	}
	
	
}
