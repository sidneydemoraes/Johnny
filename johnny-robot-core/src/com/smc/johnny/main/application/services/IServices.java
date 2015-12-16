package com.smc.johnny.main.application.services;

import com.smc.johnny.main.application.factories.GarsServicesFactory;
import com.smc.johnny.main.communicator.Speaker;
import com.smc.johnny.main.robot.services.RobotServices;


public class IServices {
	
	public void click(String coordinateName){
		RobotServices.click(coordinateName);
	}
	
	public void doubleClick(String coordinateName){
		RobotServices.doubleClick(coordinateName);
	}
	
	public void tripleClick(String coordinateName){
		RobotServices.tripleClick(coordinateName);
	}

	public void waitUntil(String coordinateName){
		try {
			RobotServices.waitUntil(coordinateName);
		} catch (Exception e) {
			Speaker.info("Waiting time exceeded. Restarting application.");
			//Prepares a mail to notify the error
			RobotServices.captureScreen();
			MailerServices.ImStuck(coordinateName);
			GarsServicesFactory.getInstance().close();
		}
		RobotServices.delay(1000);
		
	}
	
	public void execute(String command){
		RobotServices.execute(command);
	}
	
	public Boolean checkFor(String coordinateName){
		return RobotServices.checkFor(coordinateName);
	}

	public void delay(Integer ms){
		RobotServices.delay(ms);
	}
	
	public void paste(String information){
		RobotServices.paste(information);
	}
	
	public String copy(){
		return RobotServices.copy();
	}
	
	public void keyPressRelease(Integer key){
		RobotServices.keyPressRelease(key);
	}
	
	public void keyPressOnly(Integer key){
		RobotServices.keyPressOnly(key);
	}
	
	public void keyReleaseOnly(Integer key){
		RobotServices.keyRealeaseOnly(key);
	}
	
	public String selectAndCopy(String deltaName){
		RobotServices.select(deltaName);
		return RobotServices.copy();
	}
	
	public void select(String deltaName){
		RobotServices.select(deltaName);
	}
	
	public void switchWindow(){
		RobotServices.switchWindow();
		RobotServices.delay(1000);
	}
	
	public void closeWindow(){
		RobotServices.closeWindow();
	}
}
