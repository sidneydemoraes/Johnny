package com.smc.johnny.main.robot.infrastructure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * <p>This class handles all property files used by the robot module</p>
 * @author smcoelho
 */
public class PropertyManager {

	private Properties properties = null;
	private String fileName = null;
	private static Logger log = Logger.getRootLogger();
	
	/**
	 * PropertyManager constructor
	 * @param String fileName
	 */
	public PropertyManager(String fileName){
		this.fileName = fileName;
	}

	/**
	 * Returns a value based on a key name
	 * @param String keyName
	 * @return String value associated to the keyName
	 * @throws Exception
	 */
	public String getKey(String keyName) throws Exception{
		if(properties == null)
			loadProperties();
		
		String keyValue = null;
		
		try{
			keyValue = properties.getProperty(keyName);
		}catch(Exception e){
			log.error("Property " + keyName + " not found");
			throw e;
		}
		
		return keyValue;
	}
	
	/**
	 * Sets a value to a respective key
	 * @param String keyName
	 * @param String keyValue
	 */
	public void setKey(String keyName, String keyValue)
	{
		if(properties == null)
			loadProperties();

		File file = new File(fileName);
		FileOutputStream fisOut = null;
		
		try {
			
			fisOut = new FileOutputStream(file);

			properties.setProperty(keyName, keyValue);
			properties.store(fisOut,null);

			fisOut.close();
			
		}catch (IOException ex) {
			log.error("Error saving coordinates file!", ex);
		}
	}
	
	/**
	 * Loads the properties file.
	 */
	private void loadProperties(){
		
		properties = new Properties();
		
		File file = new File(fileName);
		
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(file);
			properties.load(fis);
			fis.close();
		}catch (IOException e) {
			log.error("Error openning coordinates file!", e);
		}
	}
}
