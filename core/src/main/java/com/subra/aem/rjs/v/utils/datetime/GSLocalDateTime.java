package com.subra.aem.rjs.v.utils.datetime;

import org.joda.time.LocalDateTime;

public class GSLocalDateTime{
	public LocalDateTime ldt;
	
	public GSLocalDateTime(LocalDateTime dateTime){
		ldt = dateTime;
	}
	
	public static GSLocalDateTime parse(String str, GSDateTimeFormatter dtf){
		return new GSLocalDateTime(LocalDateTime.parse(str,dtf.dtf));
	}
	
	public long year(){
		return ldt.year().get();
	}
	
	public long getYear(){
		return ldt.year().get();
	}
	
	public long dayOfYear(){
		return ldt.dayOfYear().get();
	}
	
	public int monthOfYear(){
		return ldt.monthOfYear().get();
	}
	
	public int dayOfMonth(){
		return ldt.dayOfMonth().get();
	}
	
	public int hourOfDay(){
		return ldt.hourOfDay().get();
	}
	
	public int minuteOfHour(){
		return ldt.minuteOfHour().get();
	}
}