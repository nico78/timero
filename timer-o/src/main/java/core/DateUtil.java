package core;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
public static Date tomorrow(){

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(today());
	calendar.add(Calendar.DAY_OF_MONTH, 1);
	return calendar.getTime();
}
public static Date yesterday(){

	Calendar calendar = Calendar.getInstance();
	calendar.setTime(today());
	calendar.add(Calendar.DAY_OF_MONTH, -1);
	return calendar.getTime();
}


public static Date today() {
	return toBeginningofDay(Calendar.getInstance()).getTime();
}
public static Date dayBefore(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.add(Calendar.DAY_OF_MONTH, -1);
	return calendar.getTime();
}

public static Date dayAfter(Date date) {
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(date);
	calendar.add(Calendar.DAY_OF_MONTH, 1);
	return calendar.getTime();
}

private static Calendar toBeginningofDay(Calendar calendar){
 calendar.set(Calendar.HOUR_OF_DAY, 0);
 calendar.set(Calendar.MINUTE, 0);
 calendar.set(Calendar.SECOND, 0);
 calendar.set(Calendar.MILLISECOND, 0);
 return calendar;
}
}