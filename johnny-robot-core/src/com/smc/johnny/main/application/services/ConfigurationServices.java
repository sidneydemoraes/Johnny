package com.smc.johnny.main.application.services;

import com.smc.johnny.main.communicator.Speaker;
import com.smc.johnny.main.robot.infrastructure.PropertyManager;


public class ConfigurationServices {

	private static PropertyManager pm = new PropertyManager("config/config.properties");
	
	public static String getEposAddress(){
		
		String address = null;
		
		try{
			address = pm.getKey("Epos.Address");
		}catch(Exception e) {
			Speaker.error("Error while retrieving epos address", e);
		}
		
		return address;
	}
	
	public static String getEposUser(){
		
		String user = null;
		
		try {
			user = pm.getKey("Epos.Id");
		} catch (Exception e) {
			Speaker.error("Error while retrieving epos user id.", e);
		}
		
		return user;
	}
	
	public static String getSapPath(){
		
		String path = null;
		
		try{
			path = pm.getKey("Sap.Path");
		} catch (Exception e) {
			Speaker.error("Error while retrieving sap path.", e);
		}
		
		return path;
	}
	
	public static String getEposPwd(){
		
		String pwd = null;
		
		try {
			pwd = pm.getKey("Epos.Pwd");
		} catch (Exception e) {
			Speaker.error("Error while retrieving epos pwd.", e);
		}
		
		return pwd;
	}
	
	public static String getSapUser(){
		
		String user = null;
		
		try {
			user = pm.getKey("Sap.Id");
		} catch (Exception e) {
			Speaker.error("Error while retrieving sap user id.", e);
		}
		
		return user;
	}
	
	public static String getSapPwd(){
		
		String pwd = null;
		
		try {
			pwd = pm.getKey("Sap.Pwd");
		} catch (Exception e) {
			Speaker.error("Error while retrieving sap pwd.", e);
		}
		
		return pwd;
	}
	
	public static void setEposUser(String user){
		
		pm.setKey("Epos.User",user);
	}
	
	public static void setEposPwd(String pwd){
		
		pm.setKey("Epos.Pwd",pwd);
	}
	
	public static void setSapUser(String user){
		
		pm.setKey("Sap.User",user);
	}
	
	public static void setSapPwd(String pwd){
		
		pm.setKey("Sap.Pwd",pwd);
	}
}
