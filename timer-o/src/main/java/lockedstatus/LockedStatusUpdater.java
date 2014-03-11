package lockedstatus;

import java.util.Date;

import time.Clock;
import event.Event;
import event.EventPublisher;

public class LockedStatusUpdater  {

	protected final LockedStatusChecker status;
	protected final Clock clock;
	protected final EventPublisher eventPublisher;
	protected boolean wasLocked;
	protected Date lockTime;
    
	

	public LockedStatusUpdater(LockedStatusChecker status, Clock clock,
			EventPublisher eventPublisher) {
		this.status = status;
		this.clock = clock;
		this.eventPublisher = eventPublisher;
		this.wasLocked = status.isLocked();
	}


	public void check() {
		boolean nowLocked = status.isLocked();
		if(wasLocked == nowLocked) 
			return;
		if(!wasLocked && nowLocked){
			lockTime = clock.currentTime();
			eventPublisher.publishEvent(new Event<LockRecord>(new LockRecord(lockTime, null)));
		}
		if(!nowLocked && wasLocked){
			Date unlockTime = clock.currentTime();
			eventPublisher.publishEvent(new Event<LockRecord>(new LockRecord(lockTime, unlockTime)));
		}
		wasLocked = nowLocked;
	}

}