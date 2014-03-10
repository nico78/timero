package application;
import core.UnlockPrompter;
import hotkey.HotKeyRegister;
import localdb.HibernateDataManager;
import lockedstatus.ActualSleeper;
import lockedstatus.LockRecord;
import lockedstatus.LockedStatusMonitor;
import lockedstatus.LockedStatusUpdater;
import lockedstatus.Win32LockedStatusChecker;
import time.ActualClock;
import tray.WindowsTrayRunner;
import event.Event;
import event.EventPublisher;
import event.SysoutEventPublisher;


public class RealApplicationBuilder implements ApplicationBuilder {

	@Override
	public Application buildApp(){
		DisplayProvider displayProvider = new RealDisplayProvider();
		UnlockPrompter unlockPrompter = new UnlockPrompter(displayProvider);
		LockedStatusMonitor monitor = 
				new LockedStatusMonitor(
						new LockedStatusUpdater(
								new Win32LockedStatusChecker(), 
								new ActualClock(), //todo share with timer thread 
								new PromptingEventPublisher(unlockPrompter)),//todo actually publish an event
						new ActualSleeper(500l));
		return new Application(new HotKeyRegister(), new WindowsTrayRunner(), monitor, displayProvider,new HibernateDataManager());
	}
	
	private static class PromptingEventPublisher implements EventPublisher<LockRecord> {

		private final UnlockPrompter unlockPrompter;
		
		public PromptingEventPublisher(UnlockPrompter unlockPrompter) {
			this.unlockPrompter = unlockPrompter;
		}

		@Override
		public void publishEvent(Event<LockRecord> event) {
			unlockPrompter.whatHaveYouBeenDoing(event.getObject());
		}
		
	}
}