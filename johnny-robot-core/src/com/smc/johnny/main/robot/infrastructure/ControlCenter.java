package com.smc.johnny.main.robot.infrastructure;

/**
 * <p>This class is used to turn on/off the auto config mode. It allows users to
 * update the screen coordinates and deltas. </p>
 * @author smcoelho
 */
public class ControlCenter {
	
	private static Boolean autoConfigModeOption = false;
	
	/**
	 * Auto config mode setter.
	 * @param Boolean option
	 */
	public static void setAutoConfigModeOption(Boolean option){
		autoConfigModeOption = option;
	}

	/**
	 * Checks whether the auto config mode is on
	 * @return Boolean option
	 */
	public static Boolean isAutoConfigModeOn(){
		return autoConfigModeOption;
	}
}
