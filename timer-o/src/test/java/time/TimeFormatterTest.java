package time;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TimeFormatterTest {

	TimeFormatter tf = new TimeFormatter();
	@Test
	public void shouldFormatSecs(){
		assertFormatsAs(75, "1 minute, 15 seconds");
		assertFormatsAs(60, "1 minute");
		assertFormatsAs(600, "10 minutes");
		assertFormatsAs(24*60*60 +60, "1 day, 1 minute");
		assertFormatsAs(24*60*60 +605, "1 day, 10 minutes, 5 seconds");
	}
	
	
	public void assertFormatsAs(int seconds, String expected) {
		assertEquals(expected, tf.formatSecsAsTime(seconds));
	}
	
}
