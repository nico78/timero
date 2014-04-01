package lockedstatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;

public class LockedRecordOvernightTest {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	@Test
	public void determinesIfOvernight() throws ParseException{
		assertOvernight("2014-03-31 17:00:00:00", "2014-04-01 10:00:00", true);
	}
	@Test
	public void determinesIfNotOvernight() throws ParseException{
		assertOvernight("2014-03-31 17:00:00:00", "2014-03-31 17:30:00:00", false);
	}
	
	@Test
	public void determinesIfNotOvernightJustBeforeMidnight() throws ParseException{
		assertOvernight("2014-03-15 23:59:00:00", "2014-03-15 23:59:30:00", false);
	}
	
	@Test
	public void determinesIfNotOvernightJustAroundMidnight() throws ParseException{
		assertOvernight("2014-03-15 23:59:30:00", "2014-03-16 00:00:30:00", true);
	}
	
	private void assertOvernight(String lockTime, String unlockTime,
			boolean expected) throws ParseException {
		LockRecord record = new LockRecord(DATE_FORMAT.parse(lockTime), DATE_FORMAT.parse(unlockTime));
		Assert.assertTrue(record.isOvernight()==expected);
	}
	
}
