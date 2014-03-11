package time;

import java.util.Date;

public class ActualClock implements Clock {

	@Override
	public Date currentTime() {
		return new Date();
	}

}
