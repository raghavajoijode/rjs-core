package com.subra.aem.rjs.v.utils.datetime;

import org.joda.time.format.DateTimeFormatter;

public class GSDateTimeFormatter{
	public DateTimeFormatter dtf;
	
	public GSDateTimeFormatter(DateTimeFormatter dateTimeFormatter){
		dtf = dateTimeFormatter;
	}
	
	public String print(GSDateTime dateTime){
		return dtf.print(dateTime.dt);
	}
	
	public String print(GSLocalDateTime localDateTime){
		return dtf.print(localDateTime.ldt);
	}
	
	public GSDateTimeFormatter withZone(GSDateTimeZone dtz){
		return new GSDateTimeFormatter(dtf.withZone(dtz.dtz));
	}
	
	public GSDateTimeFormatter withOffsetParsed(){
		return new GSDateTimeFormatter(dtf.withOffsetParsed());
	}
	
	public GSDateTime parseDateTime(String text){
		return new GSDateTime(dtf.parseDateTime(text));
	}
}