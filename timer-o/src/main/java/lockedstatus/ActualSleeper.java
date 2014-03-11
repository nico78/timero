package lockedstatus;

public class ActualSleeper implements Sleeper {

	private final long milliseconds;

	public ActualSleeper(long milliseconds) {
		this.milliseconds = milliseconds;
	}

	/* (non-Javadoc)
	 * @see lockedstatus.Sleeper#sleep()
	 */
	@Override
	public void sleep() {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}