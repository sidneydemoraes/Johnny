package com.smc.johnny.main.application.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.smc.johnny.main.application.infrastructure.Utilities;
import com.smc.johnny.main.application.dao.TASDB2DAO;
import com.smc.johnny.main.application.factories.DaoFactory;
import com.smc.johnny.main.application.model.User;
import com.smc.johnny.main.application.model.User.UserType;
import com.smc.johnny.main.communicator.Speaker;


public class UserRepository{

	private static TASDB2DAO tasDB2 = DaoFactory.getInstance();
	private static Logger log = Logger.getRootLogger();

	public static User getUser(String sapId)
	{
		User user = null;
		
		log.debug("Starting getUser method with sapID " + sapId);
		
		try{
			//Open connection
			tasDB2.openConnection();

			tasDB2.setPreparedStatement("SELECT * FROM GARS.USERS WHERE SAPID = ?");

			tasDB2.setString(1, sapId);
			
			log.debug("Executing and retrieving ResultSet");
			//Executing and retrieving ResultSet
			ResultSet rs = tasDB2.executeQueryPreparedStatement();
			log.debug("ResultSet retrieved");

			log.debug("Getting next element from ResultSet");
			rs.next();
			log.debug("Element retrieved");
			log.debug("User name is " + rs.getString("NAME"));
			log.debug("User email is " + rs.getString("EMAIL"));
			log.debug("User type is " + rs.getString("USERTYPE"));
			
			//Mapping resultSet to List of Machines
			log.debug("Instantiating User object");
			user = new User(
					rs.getString("NAME"),
					rs.getString("EMAIL"),
					Utilities.stringToUserType(rs.getString("USERTYPE")));
			log.debug("User instantiated");
			
			user.setSapId(rs.getString("SAPID"));
			log.debug("User sapid set");
			
			//Close current statement
			log.debug("Closing statement");
			tasDB2.closeStatement();
			log.debug("Statement closed");

			//Close connection
			log.debug("Closing connection");
			tasDB2.closeConnection();
			log.debug("Connection closed");
		
		}catch(Exception e){
			Speaker.error("Error retrieving User entity from DB...",e);
		}
		
		return user;
	}
	
	public static List<User> getUsers(UserType userType)
	{
		List<User> userList = null;
		
		try{
			//Open connection
			tasDB2.openConnection();

			tasDB2.setPreparedStatement("SELECT * FROM GARS.USERS WHERE USERTYPE = ?");

			tasDB2.setString(1, userType.toString());
			
			//Executing and retrieving ResultSet
			ResultSet rs = tasDB2.executeQueryPreparedStatement();

			//Mapping resultSet to List of Machines
			try {
				userList = userRowMapper(rs);
			} catch (SQLException e) {
				Speaker.error("Error mapping entity User to list...",e);
			}
			
			//Close current statement
			tasDB2.closeStatement();

			//Close connection
			tasDB2.closeConnection();
		
		}catch(Exception e){
			Speaker.error("Error retrieving User entity from DB...",e);
		}
		
		return userList;
	}

	public static void add(User user)
	{
		//Open connection
		tasDB2.openConnection();

		//Create Statement
		tasDB2.setPreparedStatement("INSERT INTO GARS.USERS (NAME,EMAIL,USERTYPE,SAPID) VALUES (?,?,?,?)");
		
		addUser(user);

		//Executing
		tasDB2.executePreparedStatement();

		//Close current statement
		tasDB2.closeStatement();

		//Close connection
		tasDB2.closeConnection();
	}

	public static void add(List<User> userList)
	{
		//Open connection
		tasDB2.openConnection();

		//Create Statement
		tasDB2.setPreparedStatement("INSERT INTO GARS.USERS (NAME,EMAIL,USERTYPE,SAPID) VALUES (?,?,?,?)");

		//Inserting list of opportunities
		for (User user : userList)
		{
			addUser(user);
		}

		//Close current statement
		tasDB2.closeStatement();

		//Close connection
		tasDB2.closeConnection();
	}

	private static List<User> userRowMapper(ResultSet resultSet) throws SQLException
	{
		List<User> auxList = new ArrayList<User>();

		while(resultSet.next())
		{
			User user = new User(
					resultSet.getString("NAME"),
					resultSet.getString("EMAIL"),
					Utilities.stringToUserType(resultSet.getString("USERTYPE")));
			
			user.setSapId(resultSet.getString("SAPID"));
			
			auxList.add(user);
		}
		return auxList;
	}

	private static void addUser(User user)
	{
		//Setting parameters
		tasDB2.setString(1, user.getName());
		tasDB2.setString(2, user.getEmail());
		tasDB2.setString(3, user.getUserType().toString());
		tasDB2.setString(4, user.getSapId());
	}
}
