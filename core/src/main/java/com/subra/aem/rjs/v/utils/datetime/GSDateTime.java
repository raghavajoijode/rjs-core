package com.subra.aem.rjs.v.utils.datetime;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//CQ 5.6.1 comes with an old version of Joda. This allows us to use newer functionality
public class GSDateTime{
	public DateTime dt;
	
	public GSDateTime(DateTime dateTime){
		dt = dateTime;
	}
	
	
	public GSDateTime(long instant){
		dt = new DateTime(instant);
	}
	
	public GSDateTime(){
		dt = new DateTime();
	}
	
	public static GSDateTime parse(String str, GSDateTimeFormatter dtf){
		return new GSDateTime(DateTime.parse(str,dtf.dtf));
	}

	public static GSDateTime parse(String str){
		return new GSDateTime(DateTime.parse(str));
	}
	
	public Calendar getCalendar(){
		return dt.toCalendar(Locale.ENGLISH);
	}
	
	public GSDateTime withZone(GSDateTimeZone dtz){
		return new GSDateTime(dt.withZone(dtz.dtz));
	}
	
	public boolean isAfter(GSDateTime gsdt){
		return dt.isAfter(gsdt.dt);
	}
	
	public boolean isEqual(GSDateTime gsdt){
		return dt.isEqual(gsdt.dt);
	}
	
	public long getMillis(){
		return dt.getMillis();
	}
	
	public long year(){
		return dt.year().get();
	}
	
	public long getYear(){
		return dt.year().get();
	}
	
	public long dayOfYear(){
		return dt.dayOfYear().get();
	}
	
	public int monthOfYear(){
		return dt.monthOfYear().get();
	}
	
	public int dayOfMonth(){
		return dt.dayOfMonth().get();
	}
	
	public int hourOfDay(){
		return dt.hourOfDay().get();
	}
	
	public int minuteOfHour(){
		return dt.minuteOfHour().get();
	}
	
	public Date toDate(){
		return dt.toDate();
	}
}