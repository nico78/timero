package lockedstatus;

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
	@Override
	public String toString() {
		return "LockRecord [lockTime=" + lockTime + ", unlockTime="
				+ unlockTime + "]";
	}
	
	
}