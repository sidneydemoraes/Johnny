package com.smc.johnny.main.robot.services;

import java.awt.Color;
import com.smc.johnny.main.robot.Coordinate;
import com.smc.johnny.main.robot.Delta;
import com.smc.johnny.main.robot.infrastructure.PropertyManager;

/**
 * <p>This class handles setups and gathering of coordinates and deltas.</p>
 * @author smcoelho
 */
public class CoordinateServices {

	private static PropertyManager pm = new PropertyManager("config/coordinates.properties");
	
	/**
	 * Coordinates getter
	 * @param String coordinateName
	 * @return a {@link Coordinate}
	 * @throws Exception
	 */
	public static Coordinate getCoordinate(String coordinateName) throws Exception {
		
		Coordinate c = new Coordinate();
		
		try {
			
			c.setX(Integer.parseInt(pm.getKey(coordinateName+".x")));
			c.setY(Integer.parseInt(pm.getKey(coordinateName+".y")));
			
			Color col = new Color(
					Integer.parseInt(pm.getKey(coordinateName+".r")),
					Integer.parseInt(pm.getKey(coordinateName+".g")),
					Integer.parseInt(pm.getKey(coordinateName+".b")));

			c.setColor(col);
			
		} catch (Exception e) {
			throw e;
		}
		
		return c;
	}
	
	/**
	 * Coordinates setter
	 * @param String coordinateName
	 */
	public static void setCoordinate(String coordinateName){
		
		Coordinate c = RobotServices.getCoordinate();
		
		pm.setKey(coordinateName+".x", c.getX().toString());
		pm.setKey(coordinateName+".y", c.getY().toString());
		pm.setKey(coordinateName+".r", String.valueOf(c.getColor().getRed()));
		pm.setKey(coordinateName+".g", String.valueOf(c.getColor().getGreen()));
		pm.setKey(coordinateName+".b", String.valueOf(c.getColor().getBlue()));
	}
	
	/**
	 * Delta getter
	 * @param String deltaName
	 * @return a {@link Delta}
	 * @throws Exception
	 */
	public static Delta getDelta(String deltaName) throws Exception {
		
		Delta d = new Delta();
		
		try {
			
			d.setX(Integer.parseInt(pm.getKey(deltaName+".x")));
			d.setY(Integer.parseInt(pm.getKey(deltaName+".y")));
			d.setZ(Integer.parseInt(pm.getKey(deltaName+".z")));
			
		} catch (Exception e) {
			throw e;
		}
		
		return d;
	}
	
	/**
	 * Delta setter
	 * @param String deltaName
	 * @param {@link Delta}
	 */
	public static void setDelta(String deltaName, Delta d){
		
		pm.setKey(deltaName+".x", d.getX().toString());
		pm.setKey(deltaName+".y", d.getY().toString());
		pm.setKey(deltaName+".z", d.getZ().toString());
	}
}