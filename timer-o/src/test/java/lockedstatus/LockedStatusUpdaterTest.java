package lockedstatus;

import static jhc.subidler.IsBeanLike.isBeanLike;
import static jhc.subidler.testbuilders.LockRecordBuilder.matchableLockRecord;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

import java.util.Date;

import jhc.subidler.IsBeanLike;
import jhc.subidler.testbuilders.LockRecordBuilder;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import event.Event;
import event.EventPublisher;

import time.Clock;

public class LockedStatusUpdaterTest {

	boolean locked=false;
	long currentTime = 0;
	private EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
	LockedStatusUpdater updater = new LockedStatusUpdater( 
				 new FakeLockStatusChecker(), 
				 new FakeClock(),
				 eventPublisher);
	
	@Test
	public void onUnlockPublishesEventWithLockRecord(){
		pretendSleep(1000l);
		updater.check();
		lock();
		pretendSleep(1000l);
		updater.check();
		pretendSleep(2000l);
		unlock();
		pretendSleep(5000l);
		updater.check();
		
		System.out.println("lock verify");
		ArgumentCaptor<Event> eventArg = ArgumentCaptor.forClass(Event.class);
		verify(eventPublisher).publishEvent(eventArg.capture());
		
		Event actualEvent = eventArg.getValue();
		MatcherAssert.assertThat(actualEvent,hasProperty("object", isBeanLike(matchableLockRecord(new Date(2000l), new Date(9000l)))) );
		System.out.println(currentTime);
	}
	
	
	 boolean isLocked(){
		return locked;
	}
	
	 void setLocked(boolean locked){
		this.locked=locked;
	}
	private final class FakeClock implements Clock {
		@Override
		public Date currentTime() {
			return new Date(currentTime);
		}
	}

	private final class FakeLockStatusChecker implements LockedStatusChecker {
		@Override
		public boolean isLocked() {
			return LockedStatusUpdaterTest.this.isLocked();
		}
	}

	private  void unlock() {
		setLocked(false);
	}
	
	private  void lock() {
		setLocked(true);
	}

	private  void pretendSleep(long sleepTime) {
		currentTime+=sleepTime;
	}
	
}
