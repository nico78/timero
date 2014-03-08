import lockedstatus.ActualSleeper;
import lockedstatus.LockedStatusMonitor;
import lockedstatus.LockedStatusUpdater;
import lockedstatus.Win32LockedStatusChecker;
import time.ActualClock;


public class UnlockNotificationTester {
	
	public static void main(String[] args) {
		LockedStatusMonitor monitor = 
				new LockedStatusMonitor(
						new LockedStatusUpdater(
								new Win32LockedStatusChecker(), 
								new ActualClock(), 
								new SysoutEventPublisher()),
						new ActualSleeper(500l));
		monitor.start();
	}
}
