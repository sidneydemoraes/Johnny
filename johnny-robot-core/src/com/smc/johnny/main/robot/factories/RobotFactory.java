package com.smc.johnny.main.robot.factories;

import java.awt.AWTException;
import java.awt.Robot;

import org.apache.log4j.Logger;

/**
 * <p>This class is a factory that returns an instance of {@link Robot}</p>
 * @author smcoelho
 */
public class RobotFactory {
	
	private static Robot robot;
	private static Logger log = Logger.getRootLogger();
	
	/**
	 * Gets an instance of Robot
	 * 
	 * @return {@link Robot} robot
	 */
	public static Robot getInstance(){
		
		if(robot == null){
			try {
				robot = new Robot();
			} catch (AWTException e) {
				log.error("Unable to create instance of Robot", e);
				System.exit(0);
			}
		}
		return robot;
	}
}
