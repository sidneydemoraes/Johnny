package com.smc.johnny.main.communicator.factories;

import java.awt.TrayIcon;

import com.smc.johnny.main.communicator.ShoutBox;

/**
 * <p>This class is a factory that returns an instance of a {@link ShoutBox}</p>
 * @author smcoelho
 */
public class ShoutBoxFactory {

	private static TrayIcon trayIcon;

	/**
	 * @return A singleton instance of the {@link ShoutBox}
	 */
	public static TrayIcon getInstance()
	{
		if (trayIcon == null)
		{
			ShoutBox sb = new ShoutBox();

			sb.setTrayTitle("TAS Control Center");

			sb.setIconPath("lib/icon.png");

			sb.addToTray();
			
			sb.addMenus();

			trayIcon = sb.getTrayIcon();
		}

	   return trayIcon;
	}
	
}
