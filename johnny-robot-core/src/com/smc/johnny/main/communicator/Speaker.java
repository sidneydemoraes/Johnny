package com.smc.johnny.main.communicator;

import java.awt.TrayIcon.MessageType;
import org.apache.log4j.Logger;

import com.smc.johnny.main.application.factories.GarsServicesFactory;
import com.smc.johnny.main.application.services.MailerServices;
import com.smc.johnny.main.communicator.factories.ShoutBoxFactory;
import com.smc.johnny.main.robot.services.RobotServices;

/**
 * <p>This class is responsible for sending messages to the {@link ShoutBox} so as
 * they can be shown on screen. Messages are categorized as information or errors.</p>
 * @author smcoelho
 */
public class Speaker {
	
	private static Logger log = Logger.getRootLogger();

	/**
	 * Sends a regular information to {@link ShoutBox}
	 * @param String customized message
	 */
	public static void info(String message){
		log.info(message);
		ShoutBoxFactory.getInstance().displayMessage(null, message, MessageType.INFO);
	}
	
	/**
	 * Sends an error message to {@link ShoutBox}
	 * @param String customized message
	 * @param Exception e
	 */
	public static void error(String message,Exception e){
		
		//Prints out detailed information in log
		log.error("Fatal Error: " + message, e);
		
		//Shout box message
		ShoutBoxFactory.getInstance().displayMessage(null, message, MessageType.ERROR);
		
		//Prepares a mail to notify the error
		RobotServices.captureScreen();
		MailerServices.Error();
		GarsServicesFactory.getInstance().close();

	}
}
