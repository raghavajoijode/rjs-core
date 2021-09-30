package com.subra.aem.rjs.v.utils.datetime;

import org.joda.time.DateTimeZone;

import java.util.TimeZone;

public class GSDateTimeZone{
	public DateTimeZone dtz;
	private String timeZoneId;
	
	
	public static GSDateTimeZone UTC = new GSDateTimeZone(DateTimeZone.UTC, "UTC");
	
	public GSDateTimeZone(DateTimeZone dateTimeZone, String timeZoneId){
		dtz = dateTimeZone;
		this.timeZoneId = timeZoneId;
		
	}
	
	public String getShortName(long instant){
		return TimeZone.getTimeZone(this.timeZoneId).getDisplayName(false, TimeZone.SHORT);
	}
	
	public static GSDateTimeZone forID(String str){
		return new GSDateTimeZone(DateTimeZone.forID(str), str);
	}
	
}