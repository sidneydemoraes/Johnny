package com.smc.johnny.main.robot.services;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.smc.johnny.main.robot.Coordinate;
import com.smc.johnny.main.robot.Delta;
import com.smc.johnny.main.robot.factories.RobotFactory;
import com.smc.johnny.main.robot.factories.TextTransferFactory;
import com.smc.johnny.main.robot.infrastructure.ControlCenter;
import com.smc.johnny.main.robot.infrastructure.TextTransfer;

/**
 * <p><b>Main class of robot module</b></p>
 * <p>This class provides all robot methods for usage on client modules and
 * applications. It's used to allow mouse and keyboard manipulation.</p>
 * @author smcoelho
 */
public class RobotServices{
	
	private static Robot robot = RobotFactory.getInstance();
	private static TextTransfer textTransfer = TextTransferFactory.getInstance();
	private static Logger log = Logger.getRootLogger();
	private static final int MAX = 120;
	private static final String screenshotFilename = "screenshot.jpg";
	
	/**
	 * Performs a key press and release operation.
	 * @param constant Integer key from KeyEvent
	 */
	public static void keyPressRelease(Integer key){
		log.info("KeyPressRelease key " + KeyEvent.getKeyText(key.intValue()));
		robot.keyPress(key);
		robot.delay(250);
		robot.keyRelease(key);
		robot.delay(250);
	}
	
	/**
	 * Performs a key press only operation.
	 * @param constant Integer key from KeyEvent
	 */
	public static void keyPressOnly(Integer key){
		log.info("KeyPressOnly key " + KeyEvent.getKeyText(key.intValue()));
		robot.keyPress(key);
		robot.delay(250);
	}
	
	/**
	 * Performs a key release only operation.
	 * @param constant Integer key from KeyEvent
	 */
	public static void keyRealeaseOnly(Integer key){
		log.info("KeyReleaseOnly key " + KeyEvent.getKeyText(key.intValue()));
		robot.keyRelease(key);
		robot.delay(250);
	}
	
	/**
	 * Performs a mouse single click operation
	 * @param String coordinateName
	 */
	public static void click(String coordinateName){
		
		Coordinate c = updateCoordinateIfAutoConfigModeOn(coordinateName);
		
		log.info("Clicking at " + coordinateName);
		robot.mouseMove(c.getX(), c.getY());
		robot.delay(250);
		robot.mousePress(MouseEvent.BUTTON1_MASK);
		robot.mouseRelease(MouseEvent.BUTTON1_MASK);
		robot.delay(250);
	}
	
	/**
	 * Performs a mouse double click operation
	 * @param String coordinateName
	 */
	public static void doubleClick(String coordinateName){
		
		Coordinate c = updateCoordinateIfAutoConfigModeOn(coordinateName);
		
		log.info("Double clicking at" + coordinateName);
		robot.mouseMove(c.getX(), c.getY());
		robot.delay(250);
		robot.mousePress(MouseEvent.BUTTON1_MASK);
		robot.mouseRelease(MouseEvent.BUTTON1_MASK);
		robot.mousePress(MouseEvent.BUTTON1_MASK);
		robot.mouseRelease(MouseEvent.BUTTON1_MASK);
		robot.delay(250);
	}
	
	/**
	 * Performs a mouse triple click operation
	 * @param String coordinateName
	 */
	public static void tripleClick(String coordinateName){
		
		Coordinate c = updateCoordinateIfAutoConfigModeOn(coordinateName);
		
		log.info("Triple clicking at " + coordinateName);
		robot.mouseMove(c.getX(), c.getY());
		robot.delay(250);
		robot.mousePress(MouseEvent.BUTTON1_MASK);
		robot.mouseRelease(MouseEvent.BUTTON1_MASK);
		robot.mousePress(MouseEvent.BUTTON1_MASK);
		robot.mouseRelease(MouseEvent.BUTTON1_MASK);
		robot.mousePress(MouseEvent.BUTTON1_MASK);
		robot.mouseRelease(MouseEvent.BUTTON1_MASK);
		robot.delay(250);
	}
	
	/**
	 * Performs a check in order to determine if a given {@link Coordinate} has the
	 * expected color on screen.
	 * @param String coordinateName
	 * @return Boolean result
	 */
	public static Boolean checkFor(String coordinateName){
		
		Coordinate c = updateCoordinateIfAutoConfigModeOn(coordinateName);
		
		log.info("Checking for coordinate " + coordinateName);
		if ((robot.getPixelColor(c.getX(),c.getY())).equals(c.getColor())){
			log.debug("Coordinate checked");
			return true;
		}
		log.debug("Coordinate not checked");
		return false;
	}
	
	/**
	 * Waits until an expected color is found on screen for a given {@link Coordinate} 
	 * @param String coordinateName
	 * @throws Exception
	 */
	public static void waitUntil(String coordinateName) throws Exception{
		
		int timer = 0;

		Coordinate c = updateCoordinateIfAutoConfigModeOn(coordinateName);
		log.info("Waiting until " + coordinateName + " is ready");
		
		//While its not there.
		while(!(robot.getPixelColor(c.getX(),c.getY())).equals(c.getColor()))
		{
			robot.delay(1000);
			timer++;
			
			if(timer>MAX){
				log.error("Waiting time exceeded");
				throw new Exception("Waiting time exceeded");
			}
		}
	}
	
	/**
	 * @deprecated
	 * @param coordinateName
	 * @param coordinateToCheck
	 * @throws Exception
	 */
	public static void waitUntil(String coordinateName,String coordinateToCheck) throws Exception{
		
		int timer = 0;

		Coordinate c = updateCoordinateIfAutoConfigModeOn(coordinateName);
		Coordinate c1 = updateCoordinateIfAutoConfigModeOn(coordinateToCheck);
		log.info("Waiting until " + coordinateName + " and " + coordinateToCheck + " are ready");
		
		while(!(robot.getPixelColor(c.getX(),c.getY())).equals(c.getColor())&&!(robot.getPixelColor(c1.getX(),c1.getY())).equals(c1.getColor())){
			robot.delay(1000);
			timer++;
			
			if(timer>MAX){
				log.error("Waiting time exceeded");
				throw new Exception("Waiting time exceeded");
			}
		}
	}
	
	/**
	 * Updates a {@link Coordinate} case the auto config mode is on.
	 * @param String coordinateName
	 * @return a {@link Coordinate}
	 */
	private static Coordinate updateCoordinateIfAutoConfigModeOn(String coordinateName){

		if(ControlCenter.isAutoConfigModeOn()){
			log.debug("Updating coordinate " + coordinateName);
			
			int option = JOptionPane.showConfirmDialog(null, "Do you want to update the coordinate for " + coordinateName + " ?");
			
			if(option == JOptionPane.CANCEL_OPTION){
				ControlCenter.setAutoConfigModeOption(false);
				log.debug("AutoConfigMode turned off");
			}
			if(option == JOptionPane.YES_OPTION)
				CoordinateServices.setCoordinate(coordinateName);
			else
				log.debug("Coordinate skipped");
		}
		
		delay(1000);
		
		Coordinate c = null;
		
		try {
			c = CoordinateServices.getCoordinate(coordinateName);
		} catch (Exception e) {
			
			log.debug("Updating coordinate " + coordinateName);
			
			JOptionPane.showMessageDialog(null, "Please update the coordinate for " + coordinateName + ".", "Please update this coordinate", JOptionPane.INFORMATION_MESSAGE);
			
			CoordinateServices.setCoordinate(coordinateName);
			
			try {
				c = CoordinateServices.getCoordinate(coordinateName);
			} catch (Exception e1) {
				log.error("Failed to update coordinate " + coordinateName, e1);
				System.exit(0);
			}
		}
		
		return c;
	}
	
	/**
	 * Waits for the supplied amount of time.
	 * 
	 * @param ms Milliseconds. Example, 1000 = 1 second.
	 */
	public static void delay(Integer ms){
		log.info("Delaying " + ms + " miliseconds");
		robot.delay(ms);
	}
	
	/**
	 * Returns Coordinates, which is a representation of axis x, y and its color.
	 * @return a {@link Coordinate}
	 */
	protected static Coordinate getCoordinate(){
		
		Coordinate c = new Coordinate();
		
		c.setX(MouseInfo.getPointerInfo().getLocation().x);
		c.setY(MouseInfo.getPointerInfo().getLocation().y);
		
		robot.mouseMove(1, 1);
		
		robot.delay(500);
		
		c.setColor(robot.getPixelColor(c.getX(), c.getY()));
		
		robot.mouseMove(c.getX(), c.getY());
		
		return c;
	}
	
	/**
	 * Executes a given command. Quits application if execution fails.
	 * @param String command
	 */
	public static void execute(String command){
		
		try {
			log.info("Executing " + command);
			Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " + command);
		} catch (IOException e) {
			log.error("Failed to execute command " + command, e);
			System.exit(0);
		}
	}
	
	/**
	 * Pastes an information from clipboard to the screen.
	 * @param String information
	 */
	public static void paste(String information){
	
		textTransfer.setClipboardContents(information);
		
		log.info("Pasting \"" + information + "\"");
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.delay(250);
		robot.keyPress(KeyEvent.VK_V);
		robot.delay(250);
		robot.keyRelease(KeyEvent.VK_V);
		robot.delay(250);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(250);
	}
	
	/**
	 * Copies an information from screen to the clipboard.
	 * @return String information
	 */
	public static String copy(){
		
		textTransfer.setClipboardContents("");
		
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.delay(250);
		robot.keyPress(KeyEvent.VK_C);
		robot.delay(250);
		robot.keyRelease(KeyEvent.VK_C);
		robot.delay(250);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(250);
		String information = textTransfer.getClipboardContents();
		log.info("Copying \"" + information + "\" to clipboard");
		
		return information;
	}
	
	/**
	 * Performs a selection on screen with the mouse according to a {@link Delta}. 
	 * @param String deltaName
	 */
	public static void select(String deltaName){
		
		Delta d = updateDeltaIfAutoConfigModeOn(deltaName);
		
		log.info("Selecting " + deltaName);
		robot.mouseMove(d.getX(),d.getY());
		robot.delay(50);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.delay(50);
		robot.mouseMove(d.getZ(),d.getY());
		robot.delay(50);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(100);
	}
	
	/**
	 * Updates a Delta if the auto config mode is on.
	 * @param String deltaName
	 * @return a {@link Delta}
	 */
	private static Delta updateDeltaIfAutoConfigModeOn(String deltaName){

		Coordinate c1 = null;
		Coordinate c2 = null;
		
		if(ControlCenter.isAutoConfigModeOn()){
			
			log.debug("Updating delta " + deltaName);
			
			int firstOption = JOptionPane.showConfirmDialog(null, "Do you want to update the first point of delta for " + deltaName + " ?");
			
			if(firstOption == JOptionPane.CANCEL_OPTION){
				ControlCenter.setAutoConfigModeOption(false);
				log.debug("AutoConfigMode turned off");
			} else if(firstOption == JOptionPane.YES_OPTION){
				
				c1 = getCoordinate();

				int secondOption = JOptionPane.showConfirmDialog(null, "Do you want to update the second point of delta for " + deltaName + " ?");

				if(secondOption == JOptionPane.CANCEL_OPTION){
					ControlCenter.setAutoConfigModeOption(false);
					log.debug("AutoConfigMode turned off");
				} else if(secondOption == JOptionPane.YES_OPTION){
					
					c2 = getCoordinate();

					Delta d = new Delta();

					d.setX(c1.getX());
					d.setY(c1.getY());
					d.setZ(c2.getX());

					CoordinateServices.setDelta(deltaName,d);
				} else
					log.debug("Coordinate skipped");
			} else
				log.debug("Coordinate skipped");				
		}
		
		Delta d1 = null;
		
		try {
			
			d1 = CoordinateServices.getDelta(deltaName);
			
		} catch (Exception e) {
			
			JOptionPane.showMessageDialog(null, "Please update the first point of delta for " + deltaName + ".",null, JOptionPane.INFORMATION_MESSAGE);
			
			c1 = getCoordinate();
			
			JOptionPane.showMessageDialog(null, "Please update the second point of delta for " + deltaName + ".",null, JOptionPane.INFORMATION_MESSAGE);
			
			c2 = getCoordinate();
			
			Delta d = new Delta();
			
			d.setX(c1.getX());
			d.setY(c1.getY());
			d.setZ(c2.getX());
			
			CoordinateServices.setDelta(deltaName,d);
			
			try {
				d1 = CoordinateServices.getDelta(deltaName);
			} catch (Exception e1) {
				log.error("Failed to update coordinate " + deltaName, e1);
				System.exit(0);
			}
		}
		
		return d1;
	}
	
	/**
	 * Performs an ALT+TAB operation, switching the current focused window.
	 */
	public static void switchWindow(){

		log.info("Switching window with ALT+TAB");
		robot.keyPress(KeyEvent.VK_ALT);
		robot.delay(100);
		keyPressRelease(KeyEvent.VK_TAB);
		robot.delay(100);
		robot.keyRelease(KeyEvent.VK_ALT);
	}
	
	/**
	 * Performs an ALT+F4 operation, closing the current focused window.
	 */
	public static void closeWindow(){

		log.info("Closing current window");
		robot.keyPress(KeyEvent.VK_ALT);
		robot.delay(100);
		keyPressRelease(KeyEvent.VK_F4);
		robot.delay(100);
		robot.keyRelease(KeyEvent.VK_ALT);
	}
	
	/**
	 * Takes a screen shot and saves as a JPEG file in application folder 'data'
	 */
	public static void captureScreen(){
		log.info("Taking screen shot");	
		
		//finding screen dimension
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		Rectangle rect = new Rectangle(0,0,screenSize.width,screenSize.height);
		
		//getting buffered image
		BufferedImage image = robot.createScreenCapture(rect);
		
		//saving image as a JPEG file
		File file = new File(screenshotFilename);
		try {
			ImageIO.write(image, "jpg", file);
		} catch (Exception e) {
			log.error("Error saving screenshot file.", e);
		}
	}

}
