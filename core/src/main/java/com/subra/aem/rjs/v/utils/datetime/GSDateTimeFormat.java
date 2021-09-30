package com.subra.aem.rjs.v.utils.datetime;

import org.joda.time.format.DateTimeFormat;

public class GSDateTimeFormat{
	public static GSDateTimeFormatter forPattern(String str){
		return new GSDateTimeFormatter(DateTimeFormat.forPattern(str));
	}
}