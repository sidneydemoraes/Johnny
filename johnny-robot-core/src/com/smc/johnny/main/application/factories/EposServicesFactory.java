package com.smc.johnny.main.application.factories;

import com.smc.johnny.main.application.services.EposServices;


public class EposServicesFactory {

	private static EposServices eposServices;
	
	/**
	 * Create an EposServices instance
	 * 
	 * @return Robot
	 */
	public static EposServices getInstance(){
		
		if(eposServices == null)
			eposServices = new EposServices();

		return eposServices;
	}
}