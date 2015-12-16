package com.smc.johnny.main.application.infrastructure;

import org.joda.time.DateTime;

import com.smc.johnny.main.application.model.User.UserType;


public class Utilities {
	
	public static UserType stringToUserType(String userType){
		
		UserType type;
		
		if(userType.equals("APM"))
			type = UserType.APM;
		else
			type = UserType.WebCustomerSupport;
		
		return type;
	}
	
	public static String getDate(){
		return new DateTime().toString("MM/dd/yy");
	}
	
	public static String getLastMonthDate(){
		return new DateTime().minusDays(30).toString("MM/dd/yy");
	}
	
	public static String getCurrentYear(){
		return new DateTime().toString("yy");
	}
}
