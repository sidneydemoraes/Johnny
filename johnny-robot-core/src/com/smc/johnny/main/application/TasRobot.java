package com.smc.johnny.main.application;

import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.PropertyConfigurator;

import com.smc.johnny.main.application.factories.GarsServicesFactory;
import com.smc.johnny.main.application.services.GarsServices;
import com.smc.johnny.main.communicator.Speaker;
import com.smc.johnny.main.queuer.QueueManager;
import com.smc.johnny.main.robot.infrastructure.ControlCenter;
import com.smc.johnny.main.robot.services.RobotServices;


public class TasRobot {

	public static void main(String[] args) {

		// Pointing log4j properties file location
		PropertyConfigurator.configure("config/log4j.properties");
		
		//Set Look & Feel
		setLookAndFeel();
		
		// treating application arguments
		for(String argument : args){
			if(argument.equals("-autoconfig"))
				ControlCenter.setAutoConfigModeOption(true);
		}
		
		Boolean intoOrderEditTransaction = false;
		
		QueueManager.buildQueue();
		
		GarsServices garsServices = GarsServicesFactory.getInstance();
		
		garsServices.loginSystems();
		
		//Do forever
		while(true)
		{
			//While there's nothing in the queue
			while(QueueManager.getCurrentItem()==null){
				//If I'm at the order edit transaction, back to main window.
				if(intoOrderEditTransaction){
					garsServices.gotoSapMainWindow();
					intoOrderEditTransaction = false;
				}
				
				//Run All Reports
				garsServices.runReports();
				
				//Build the order queue for pending orders.
				QueueManager.buildQueue();
				
				//If queue is still empty, wait for 5 minutes.
				if(QueueManager.getCurrentItem()==null){
					int minutes = 0;
					while(QueueManager.getCurrentItem()==null){
						Speaker.info("Order Queue has been empty for " + (minutes) + " minutes... Trying again in a minute.");

						RobotServices.delay(60000);
						minutes++;
						
						//Check if any order was sent from apm
						garsServices.runReports();
						QueueManager.buildQueue();
					}
				}
			}
			
			//If I'm not the order edit transaction go to it
			if(!intoOrderEditTransaction){
				garsServices.gotoSapOrderEditTransaction();
				intoOrderEditTransaction = true;
			}
			
			garsServices.chargeOrder();
		}
	}
	
	private static void setLookAndFeel(){

		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
	}
}
