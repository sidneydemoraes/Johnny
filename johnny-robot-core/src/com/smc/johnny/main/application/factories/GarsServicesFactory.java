package com.smc.johnny.main.application.factories;

import com.smc.johnny.main.application.services.GarsServices;


public class GarsServicesFactory {

	private static GarsServices garsServices;
	
	/**
	 * Create a GarsServices instance
	 * 
	 * @return Robot
	 */
	public static GarsServices getInstance(){
		
		if(garsServices == null)
			garsServices = new GarsServices(SapServicesFactory.getInstance(),EposServicesFactory.getInstance());

		return garsServices;
	}
}