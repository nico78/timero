package lockedstatus;

import java.util.Calendar;
import java.util.Date;

public class LockRecord {
	private Date lockTime;
	private Date unlockTime;
	public LockRecord(Date lockTime, Date unlockTime) {
		this.lockTime = lockTime;
		this.unlockTime = unlockTime;
	}
	public Date getLockTime() {
		return lockTime;
	}
	public Date getUnlockTime() {
		return unlockTime;
	}
	
	public boolean isOvernight(){
		Calendar lockCal = Calendar.getInstance();
		lockCal.setTime(lockTime);
		int lockDay = lockCal.get(Calendar.DAY_OF_YEAR);

		Calendar unlockCal = Calendar.getInstance();
		unlockCal.setTime(unlockTime);
		int unlockDay = unlockCal.get(Calendar.DAY_OF_YEAR);
		
		return lockDay!=unlockDay;
	}
	@Override
	public String toString() {
		return "LockRecord [lockTime=" + lockTime + ", unlockTime="
				+ unlockTime + "]";
	}
	
	
}