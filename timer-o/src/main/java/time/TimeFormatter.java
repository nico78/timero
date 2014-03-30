package time;

import java.util.concurrent.TimeUnit;

public class TimeFormatter {

	
	public String formatSecsAsTime(int seconds){
		int days = (int)TimeUnit.SECONDS.toDays(seconds);        
		 int hours = (int)(TimeUnit.SECONDS.toHours(seconds) - (days *24));
		 int minutes = (int)(TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60));
		 seconds = (int)(TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60));
		 
		 String comma = ", ";
		 return  (formatAmount(days,"day", comma) + 
				 formatAmount(hours, "hour", comma) +
				 formatAmount(minutes, "minute", comma) +
				 formatAmount(seconds, "second",comma)).replaceAll(", $","");
	}
	

	public static String formatAmount(int amount, String unitDesc, String sep) {
		return amount>0?amount+" " +unitDesc+(amount>1?"s":"") + sep:"";
	}
}
