package application;
import hotkey.HotKeyRegister;
import localdb.HibernateDataManager;
import lockedstatus.ActualSleeper;
import lockedstatus.LockedStatusMonitor;
import lockedstatus.LockedStatusUpdater;
import lockedstatus.Win32LockedStatusChecker;
import time.ActualClock;
import tray.WindowsTrayRunner;
import event.SysoutEventPublisher;


public class RealApplicationBuilder implements ApplicationBuilder {

	@Override
	public Application buildApp(){
		LockedStatusMonitor monitor = 
				new LockedStatusMonitor(
						new LockedStatusUpdater(
								new Win32LockedStatusChecker(), 
								new ActualClock(), //todo share with timer thread 
								new SysoutEventPublisher()),
						new ActualSleeper(500l));
		return new Application(new HotKeyRegister(), new WindowsTrayRunner(), monitor, new RealDisplayProvider(),new HibernateDataManager());
	}
}