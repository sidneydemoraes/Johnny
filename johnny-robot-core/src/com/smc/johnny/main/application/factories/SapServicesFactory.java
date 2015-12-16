package com.smc.johnny.main.application.factories;

import com.smc.johnny.main.application.services.SapServices;


public class SapServicesFactory {

	private static SapServices sapServices;
	
	/**
	 * Create a SapServices instance
	 * 
	 * @return Robot
	 */
	public static SapServices getInstance(){
		
		if(sapServices == null)
			sapServices = new SapServices();

		return sapServices;
	}
}