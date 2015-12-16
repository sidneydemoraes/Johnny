package com.smc.johnny.main.robot;

/**
 * <p>This bean represents an horizontal range for selection on the screen.
 * It holds an x,y position as 1st delta spot and gets a second x position for
 * the 2nd delta spot, keeping the same y ordinate. It's used by the robot for
 * data selection from the screen.</p>
 * 
 * @author smcoelho
 */
public class Delta {

	private Integer x;
	private Integer y;
	private Integer z;

	/**
	 * X ordinate getter - 1st delta spot
	 * @return Integer x
	 */
	public Integer getX() {
		return x;
	}

	/**
	 * X ordinate setter - 1st delta spot
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
	 * Z ordinate getter - 2nd delta spot
	 * @return Integer z
	 */
	public Integer getZ() {
		return z;
	}

	/**
	 * Z ordinate setter - 2nd delta spot
	 * @param Integer z
	 */
	public void setZ(Integer z) {
		this.z = z;
	}

	@Override
	public String toString()
	{
		String delta;

		delta = "X - " + getX() + "\n";
		delta += "Y - " + getY() + "\n";

		return delta;
	}
}
