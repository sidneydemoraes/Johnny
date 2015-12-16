package com.smc.johnny.main.robot;

import java.awt.Color;

/**
 * <p>This bean represents a coordinate on screen. It's used by the robot to 
 * determine spots on screen that must be checked or clicked by the mouse.</p>
 * 
 * @author smcoelho
 */
public class Coordinate {
	
	private Integer x;
	private Integer y;
	private Color color;
	
	/**
	 * X ordinate getter
	 * @return Integer x
	 */
	
	public Integer getX() {
		return x;
	}
	
	/**
	 * X ordinate setter
	 * @param Integer x
	 */
	public void setX(Integer x) {
		this.x = x;
	}
	
	/**
	 * Y ordinate getter
	 * @return Integer y
	 */
	public Integer getY() {
		return y;
	}
	
	/**
	 * Y ordinate setter
	 * @param Integer y
	 */
	public void setY(Integer y) {
		this.y = y;
	}
	
	/**
	 * Color getter
	 * @return Color color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Color setter
	 * @param Color color
	 */
	public void setColor(Color color) {
		this.color = color;
	}
}
