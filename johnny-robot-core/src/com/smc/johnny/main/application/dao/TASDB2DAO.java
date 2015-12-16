package com.smc.johnny.main.application.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.smc.johnny.main.communicator.Speaker;
import com.smc.johnny.main.robot.infrastructure.PropertyManager;


public class TASDB2DAO {

	//DATABASE DB2 9.5-C Express Driver
	static final String JDBC_DRIVER = "com.ibm.db2.jcc.DB2Driver";

	//variables that hold the connection to the DB
	private Connection con = null;
	private PreparedStatement pSt = null;
	private ResultSet rs = null;
	private Statement st = null;

	//variables that hold the database configuration
	public String DATABASE_URL;
	public String DATABASE_USERID;
	public String DATABASE_PWD;

	/**
	 * This is a wrapper to connect to TAS DB2 DB.
	 */
	public TASDB2DAO()
	{
		PropertyManager p = new PropertyManager("config/config.properties");

		try {
			this.DATABASE_URL = p.getKey("Database.Url");
			this.DATABASE_USERID = p.getKey("Database.Userid");
			this.DATABASE_PWD = p.getKey("Database.Pwd");
		} catch (Exception e1) {
			Speaker.error("Error while retrieving database information",e1);
		}

		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			Speaker.error("Error Creating DAO",e);
		}
	}

	public void closeConnection()
	{
		try {
			con.close();
		} catch (SQLException e) {
			Speaker.error("Error Closing connection",e);
		}
	}

	public void closeStatement()
	{
		try
		{
			if(!(st==null))
				st.close();
			if(!(pSt==null))
				pSt.close();
		}
		catch(SQLException e){
			Speaker.error("Error Closing statement",e);
		}
	}

	private void createStatement()
	{
		try {
			st = con.createStatement();
		} catch (SQLException e) {
			Speaker.error("Error Creating statement",e);
		}
	}

	public void executePreparedStatement()
	{
		try {
			pSt.execute();
		} catch (SQLException e) {
			Speaker.info("Order already in the database");
		}
	}

	/**
	 * @return ResultSet
	 */
	public ResultSet executeQuery(String sql)
	{
		createStatement();

		try {
			rs = st.executeQuery(sql);
		} catch (SQLException e) {
			Speaker.info("Error Executing Query");
		}
		return rs;
	}

	/**
	 *
	 * @return ResultSet
	 */
	public ResultSet executeQueryPreparedStatement()
	{
		try {
			rs = pSt.executeQuery();
		} catch (SQLException e) {
			Speaker.error("Error Executing Query Prepared Statement",e);
		}
		return rs;
	}

	public void openConnection()
	{
		try {
			con = DriverManager.getConnection(this.DATABASE_URL, this.DATABASE_USERID, this.DATABASE_PWD);
		} catch (SQLException e) {
			Speaker.error("Error Opening Connection",e);
		}
	}

	/**
	 * Set parameters for the current statement
	 */
	public void setInt(int parameterIndex, int x)
	{
		try {
			pSt.setInt(parameterIndex, x);
		} catch (SQLException e) {
			Speaker.error("Error Setting parameters",e);
		}
	}

	/**
	 * Set parameters for the current statement
	 */
	public void setObject(int parameterIndex, Object x)
	{
		try {
			pSt.setObject(parameterIndex, x);
		} catch (SQLException e) {
			Speaker.error("Error Setting parameters",e);
		}
	}

	/**
	 *
	 * @param sql
	 */
	public void setPreparedStatement(String sql)
	{
		try {
			pSt = con.prepareStatement(sql);
		} catch (SQLException e) {
			Speaker.error("Error Setting Prepared Statement",e);
		}
	}

	/**
	 * Set parameters for the current statement
	 */
	public void setString(int parameterIndex, String x)
	{
		try {
			pSt.setString(parameterIndex, x);
		} catch (SQLException e) {
			Speaker.error("Error Setting parameters",e);
		}
	}

	/**
	 * Set parameters for the current statement
	 */
	public void setTimestamp(int parameterIndex, Timestamp x)
	{
		try {
			pSt.setTimestamp(parameterIndex, x);
		} catch (SQLException e) {
			Speaker.error("Error Setting parameters",e);
		}
	}

	/**
	 * Set parameters for the current statement
	 */
	public void setDate(int parameterIndex, Date x)
	{
		try {
			pSt.setDate(parameterIndex, x);
		} catch (SQLException e) {
			Speaker.error("Error Setting parameters",e);
		}
	}
}