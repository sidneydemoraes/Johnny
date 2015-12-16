package com.smc.johnny.main.robot.factories;

import com.smc.johnny.main.robot.infrastructure.TextTransfer;

/**
 * <p>This class is a factory that returns an instance of {@link TextTransfer}</p>
 * @author smcoelho
 */
public class TextTransferFactory {
	
	private static TextTransfer tt;
	
	/**
	 * Gets an instance of TextTransfer
	 * @return {@link TextTransfer} tt
	 */
	public static TextTransfer getInstance(){
		
		if(tt == null){
				tt = new TextTransfer();
		}
		return tt;
	}
}
