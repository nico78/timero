package lockedstatus;

import core.UnlockPrompter;

public class LockedStatusMonitor extends Thread {
	
	private final Sleeper sleeper;
	private volatile boolean running = true;
	
	private LockedStatusUpdater statusUpdater;
	private UnlockPrompter unlockPrompter;
	public LockedStatusMonitor(UnlockPrompter unlockPrompter, LockedStatusUpdater statusUpdater, Sleeper sleeper) {
		this.unlockPrompter = unlockPrompter;
		this.statusUpdater = statusUpdater;
		this.sleeper = sleeper;
	}


	public void run() {
		while (isRunning()) {
			sleeper.sleep();
			statusUpdater.check();
		}
		System.out.println("lock monitor stopped..");
	}


	public boolean isRunning() {
		return running;
	}

	public void stopMonitor() {
		this.running = false;
	}


	public UnlockPrompter getUnlockPrompter() {
		return unlockPrompter;
	}
	
	
}
