/**
 *
 */
package com.smc.johnny.main.communicator;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

/**
 * <p>This class generates an icon on the system tray which will keep users of
 * the application status. It shows customized messages in balloons which allow
 * people to know what is happening with the application.</p>
 * @author smcoelho
 */
public class ShoutBox {

	private TrayIcon trayIcon;
	private SystemTray tray;
	private Image image;
	private String iconPath;
	private String trayTitle;
	// private MenuItem autoConfigCb;

	/**
	 * ShoutBox constructor
	 */
	public ShoutBox(){

	}

	/**
	 * @return the TrayIcon object
	 */
	public TrayIcon getTrayIcon()
	{
		return trayIcon;
	}

	/**
	 * @param the trayTitle to set
	 */
	public void setTrayTitle(String trayTitle) {
		this.trayTitle = trayTitle;
	}

	/**
	 * @param the iconPath to set
	 */
	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	/**
	 * Adds the icon to system tray.
	 */
	public void addToTray()
	{
		tray = SystemTray.getSystemTray();

		image = new ImageIcon(iconPath).getImage();

		trayIcon = new TrayIcon(image, trayTitle);
		trayIcon.setImageAutoSize(true);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("I wasn't able to attach the shoutbox to the tray.");
			e.printStackTrace();
		}
	}

	/**
	 * Adds menus to the system tray's icon.
	 */
	public void addMenus()
	{
		//Create a pop-up menu
        PopupMenu popup = new PopupMenu();

        Menu optionsMenu = new Menu("Options");

        /*autoConfigCb = new MenuItem("Set Auto Config ON");

        autoConfigCb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(ControlCenter.isAutoConfigModeOn())
				{
					autoConfigCb.setLabel("Set Auto Config ON");
					ControlCenter.setAutoConfigModeOption(true);
				}
				else
				{
					autoConfigCb.setLabel("Set Auto Config OFF");
					ControlCenter.setAutoConfigModeOption(true);
				}
			}
		}); 
		optionsMenu.add(autoConfigCb); */
        

        MenuItem exit = new MenuItem("Exit");

        exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});
        

        //Attach the menu components to the pop-up menu
        popup.add(optionsMenu);
        popup.addSeparator();
        popup.add(exit);

        this.trayIcon.setPopupMenu(popup);

	}
}
