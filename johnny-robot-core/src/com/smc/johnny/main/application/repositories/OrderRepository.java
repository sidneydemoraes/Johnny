package com.smc.johnny.main.application.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.smc.johnny.main.application.dao.TASDB2DAO;
import com.smc.johnny.main.application.factories.DaoFactory;
import com.smc.johnny.main.application.model.CreditCard;
import com.smc.johnny.main.application.model.Order;
import com.smc.johnny.main.application.model.Order.ErrorType;
import com.smc.johnny.main.application.model.Order.OrderType;
import com.smc.johnny.main.application.model.Order.Priority;
import com.smc.johnny.main.communicator.Speaker;


public class OrderRepository{

	private static TASDB2DAO tasDB2 = DaoFactory.getInstance();	
	private static Logger log = Logger.getRootLogger();

	public static List<Order> getPendingOrders()
	{
		log.info("Getting pending orders");
		List<Order> orderList = null;
		
		try{
			//Open connection
			tasDB2.openConnection();

			tasDB2.setPreparedStatement("SELECT * FROM GARS.ORDERS WHERE COMPLETED = 'NO' AND DELETEDDATE IS NULL ORDER BY EMAILSENT ASC, PRIORITY ASC");

			//Executing and retrieving ResultSet
			ResultSet rs = tasDB2.executeQueryPreparedStatement();

			//Mapping resultSet to List of Machines
			try {
				orderList = orderRowMapper(rs);
			} catch (SQLException e) {
				Speaker.error("Error mapping entity Order to list...",e);
			}
			
			//Close current statement
			tasDB2.closeStatement();

			//Close connection
			tasDB2.closeConnection();
		
		}catch(Exception e){
			Speaker.error("Error retrieving Order entity from DB...",e);
		}
		
		Speaker.info("Retrieved " + orderList.size() + " pending orders...");
		
		return orderList;
	}

	public static void add(Order order) throws SQLException
	{
		log.info("Adding order " + order.getNumber() + " to repository");
		
		//Open connection
		tasDB2.openConnection();

		//Create Statement
		tasDB2.setPreparedStatement("INSERT INTO GARS.ORDERS (AMOUNTEPOS,AMOUNTSAP,AUTHORIZATIONCODE,CODE,CREATEDBY,CCNUMBER,CCMONTH,CCYEAR,CCHOLDER,CURRENCY,EMAIL,ERROR,ERRORTYPE,FRAUDRESULT,IBMREFERENCE,NEGATIVELISTRESULT,NUMBER,ORDERTYPE,PODATE,PONUMBER,PRIORITY,SALESAREAID,SHIPPINGCODE,COMPLETED,COMPLETEDDATE,SENTBY,SENTDATE,EMAILSENT,EMAILSENTAT,DELETEDBY,DELETEDDATE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		addOrder(order);

		//Executing
		tasDB2.executePreparedStatement();

		//Close current statement
		tasDB2.closeStatement();

		//Close connection
		tasDB2.closeConnection();
	}
	
	public static void add(List<Order> orderList) throws SQLException
	{
		log.info("Adding order list to repository");
		
		//Open connection
		tasDB2.openConnection();

		//Create Statement
		tasDB2.setPreparedStatement("INSERT INTO GARS.ORDERS (AMOUNTEPOS,AMOUNTSAP,AUTHORIZATIONCODE,CODE,CREATEDBY,CCNUMBER,CCMONTH,CCYEAR,CCHOLDER,CURRENCY,EMAIL,ERROR,ERRORTYPE,FRAUDRESULT,IBMREFERENCE,NEGATIVELISTRESULT,NUMBER,ORDERTYPE,PODATE,PONUMBER,PRIORITY,SALESAREAID,SHIPPINGCODE,COMPLETED,COMPLETEDDATE,SENTBY,SENTDATE,EMAILSENT,EMAILSENTAT,DELETEDBY, DELETEDDATE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		//Inserting list of opportunities
		for (Order order : orderList)
		{
			addOrder(order);

			//Executing
			tasDB2.executePreparedStatement();
		}

		//Close current statement
		tasDB2.closeStatement();

		//Close connection
		tasDB2.closeConnection();
	}

	private static List<Order> orderRowMapper(ResultSet resultSet) throws SQLException
	{
		log.info("Getting order map");
		
		List<Order> auxList = new ArrayList<Order>();

		while(resultSet.next())
		{
			Order order = new Order();

			order.setId(resultSet.getInt("ID"));
			order.setAmountEPOS(resultSet.getString("AMOUNTEPOS"));
			order.setAmountSAP(resultSet.getString("AMOUNTSAP"));
			order.setAuthorizationCode(resultSet.getString("AUTHORIZATIONCODE"));
			order.setCode(resultSet.getString("CODE"));
			order.setCreatedBy(resultSet.getString("CREATEDBY"));
			
			CreditCard cc = new CreditCard();
			
			cc.setNumber(resultSet.getString("CCNUMBER"));
			cc.setMonth(resultSet.getString("CCMONTH"));
			cc.setYear(resultSet.getString("CCYEAR"));
			cc.setHolder(resultSet.getString("CCHOLDER"));
			
			order.setCreditCard(cc);
			order.setCurrency(resultSet.getString("CURRENCY"));
			order.setEmail(resultSet.getString("EMAIL"));
			
			if(resultSet.getString("ERROR").equals("YES"))
				order.setError(true);
			else
				order.setError(false);
			
			if(resultSet.getString("ERRORTYPE").equals(ErrorType.NoIBMReference.toString()))
					order.setErrorType(ErrorType.NoIBMReference);
			else if(resultSet.getString("ERRORTYPE").equals(ErrorType.CreditCardDeclined.toString()))
					order.setErrorType(ErrorType.CreditCardDeclined);
			else if(resultSet.getString("ERRORTYPE").equals(ErrorType.InvalidShipping.toString()))
					order.setErrorType(ErrorType.InvalidShipping);
			else if(resultSet.getString("ERRORTYPE").equals(ErrorType.NegativeListMatch.toString()))
					order.setErrorType(ErrorType.NegativeListMatch);
			else if(resultSet.getString("ERRORTYPE").equals(ErrorType.AmountMismatch.toString()))
				order.setErrorType(ErrorType.AmountMismatch);
			else if(resultSet.getString("ERRORTYPE").equals(ErrorType.AmountAbove100k.toString()))
				order.setErrorType(ErrorType.AmountAbove100k);
			else if(resultSet.getString("ERRORTYPE").equals(ErrorType.CaughtInFraudCheck.toString()))
				order.setErrorType(ErrorType.CaughtInFraudCheck);
			else if(resultSet.getString("ERRORTYPE").equals(ErrorType.InUse.toString()))
				order.setErrorType(ErrorType.InUse);
			else if(resultSet.getString("ERRORTYPE").equals(ErrorType.CanadianSplitOrder.toString()))
				order.setErrorType(ErrorType.CanadianSplitOrder);
			else if(resultSet.getString("ERRORTYPE").equals(ErrorType.CaptureDenied.toString()))
				order.setErrorType(ErrorType.CaptureDenied);
			else
				order.setErrorType(ErrorType.ErrorFree);

			order.setFraudResult(resultSet.getString("FRAUDRESULT"));
			order.setIbmReference(resultSet.getString("IBMREFERENCE"));
			
			if(resultSet.getString("NEGATIVELISTRESULT").equals("YES"))
				order.setNegativeListResult(true);
			else
				order.setNegativeListResult(false);
			
			order.setNumber(resultSet.getString("NUMBER"));
			
			if(resultSet.getString("ORDERTYPE").equals(OrderType.Telesales.toString()))
					order.setOrderType(OrderType.Telesales);
			else if(resultSet.getString("ORDERTYPE").equals(OrderType.Ptx.toString()))
					order.setOrderType(OrderType.Ptx);
					
			order.setPoDate(resultSet.getString("PODATE"));
			order.setPoNumber(resultSet.getString("PONUMBER"));
			
			if(resultSet.getString("PRIORITY").equals(Priority.Normal))
				order.setPriority(Priority.Normal);
			else
				order.setPriority(Priority.Expedite);
				
			order.setSalesAreaId(resultSet.getString("SALESAREAID"));
			order.setShippingCode(resultSet.getString("SHIPPINGCODE"));
			
			if(resultSet.getString("COMPLETED").equals("YES"))
				order.setCompleted(true);
			else
				order.setCompleted(false);
			
			order.setCompletedDate(resultSet.getString("COMPLETEDDATE"));
			order.setSentBy(resultSet.getString("SENTBY"));
			order.setSentDate(resultSet.getString("SENTDATE"));
			
			if(resultSet.getString("EMAILSENT").equals("YES"))
				order.setEmailSent(true);
			else
				order.setEmailSent(false);
			
			order.setEmailSentAt(resultSet.getString("EMAILSENTAT"));
			
			order.setDeleteBy(resultSet.getString("DELETEDBY"));
			order.setDeletedDate(resultSet.getString("DELETEDDATE"));
			
			auxList.add(order);
		}
		return auxList;
	}

	private static void addOrder(Order order)
	{
		log.info("Setting parameters for insertion");
		
		//Setting parameters
		tasDB2.setString(1, order.getAmountEPOS());
		tasDB2.setString(2, order.getAmountSAP());
		tasDB2.setString(3, order.getAuthorizationCode());
		tasDB2.setString(4, order.getCode());
		tasDB2.setString(5, order.getCreatedBy());
		tasDB2.setString(6, order.getCreditCard().getNumber());
		tasDB2.setString(7, order.getCreditCard().getMonth());
		tasDB2.setString(8, order.getCreditCard().getYear());
		tasDB2.setString(9, order.getCreditCard().getHolder());
		tasDB2.setString(10, order.getCurrency());
		tasDB2.setString(11, order.getEmail());
		
		if(order.getError())
			tasDB2.setString(12, "YES");
		else
			tasDB2.setString(12, "NO");
		
		tasDB2.setString(13, order.getErrorType().toString());
		tasDB2.setString(14, order.getFraudResult());	
		tasDB2.setString(15, order.getIbmReference());
		
		if(order.getNegativeListResult())
			tasDB2.setString(16, "YES");
		else
			tasDB2.setString(16, "NO");
		
		tasDB2.setString(17, order.getNumber());
		tasDB2.setString(18, order.getOrderType().toString());
		tasDB2.setString(19, order.getPoDate());
		tasDB2.setString(20, order.getPoNumber());
		tasDB2.setString(21, order.getPriority().toString());
		tasDB2.setString(22, order.getSalesAreaId());
		tasDB2.setString(23, order.getShippingCode());
		
		if(order.getCompleted())
			tasDB2.setString(24, "YES");
		else
			tasDB2.setString(24, "NO");
		
		tasDB2.setString(25, order.getCompletedDate());
		tasDB2.setString(26, order.getSentBy());
		tasDB2.setString(27, order.getSentDate());
		
		if(order.isEmailSent())
			tasDB2.setString(28, "YES");
		else
			tasDB2.setString(28, "NO");
		
		tasDB2.setString(29, order.getEmailSentAt());
		
		tasDB2.setString(30, order.getDeleteBy());
		
		tasDB2.setString(31, order.getDeletedDate());
	}
	
	public static void update(Order order) throws SQLException {
		
		log.info("Updating order " + order.getNumber() + " in repository");
		//Open connection
		tasDB2.openConnection();

		//Create Statement
		tasDB2.setPreparedStatement("UPDATE GARS.ORDERS SET AMOUNTEPOS = ?,AMOUNTSAP = ?, AUTHORIZATIONCODE = ?, CODE = ?, CREATEDBY = ?, CCNUMBER = ?, CCMONTH = ?, CCYEAR = ?, CCHOLDER = ?, CURRENCY = ?, EMAIL = ?, ERROR = ?, ERRORTYPE = ?, FRAUDRESULT = ?, IBMREFERENCE = ?, NEGATIVELISTRESULT = ?, NUMBER = ?, ORDERTYPE = ?, PODATE = ?, PONUMBER = ?, PRIORITY = ?, SALESAREAID = ?, SHIPPINGCODE = ?, COMPLETED = ?, COMPLETEDDATE = ?, SENTBY = ?, SENTDATE = ?, EMAILSENT = ?, EMAILSENTAT = ? , DELETEDBY = ?, DELETEDDATE = ? WHERE ID = ? ");
		
		addOrder(order);
		
		tasDB2.setInt(32, order.getId());		

		//Executing
		tasDB2.executePreparedStatement();

		//Close current statement
		tasDB2.closeStatement();

		//Close connection
		tasDB2.closeConnection();
	}
	
	public static void update(List<Order> orderList) throws SQLException{
		
		log.info("Updating order list in repository");
		//Open connection
		tasDB2.openConnection();

		//Create Statement
		tasDB2.setPreparedStatement("UPDATE GARS.ORDERS SET AMOUNTEPOS = ?,AMOUNTSAP = ?, AUTHORIZATIONCODE = ?, CODE = ?, CREATEDBY = ?, CCNUMBER = ?, CCMONTH = ?, CCYEAR = ?, CCHOLDER = ?, CURRENCY = ?, EMAIL = ?, ERROR = ?, ERRORTYPE = ?, FRAUDRESULT = ?, IBMREFERENCE = ?, NEGATIVELISTRESULT = ?, NUMBER = ?, ORDERTYPE = ?, PODATE = ?, PONUMBER = ?, PRIORITY = ?, SALESAREAID = ?, SHIPPINGCODE = ?, COMPLETED = ?, COMPLETEDDATE = ?, SENTBY = ?, SENTDATE = ?, EMAILSENT = ?, EMAILSENTAT = ? , DELETEDBY = ?, DELETEDDATE = ? WHERE ID = ? ");
		
		for (Order order : orderList) {
		
			addOrder(order);
			
			tasDB2.setInt(32, order.getId());
			
			//Executing
			tasDB2.executePreparedStatement();
		}

		//Close current statement
		tasDB2.closeStatement();

		//Close connection
		tasDB2.closeConnection();
	}
	
	public static void updateByNumber(Order order) {
		
		log.info("Updating order " + order.getNumber() + " querying by number");
		
		//Open connection
		tasDB2.openConnection();

		//Create Statement
		tasDB2.setPreparedStatement("UPDATE GARS.ORDERS SET CCNUMBER = ?, CCMONTH = ?, CCYEAR = ?, CCHOLDER = ?, ERROR = ?, ERRORTYPE = ?, NEGATIVELISTRESULT = ?, ORDERTYPE = ?, PRIORITY = ?, COMPLETED = ?, COMPLETEDDATE = ?, SENTBY = ?, SENTDATE = ?, EMAILSENT = ?, DELETEDBY = ?, DELETEDDATE = ? WHERE NUMBER = ? ");
		
		
		tasDB2.setString(32, order.getNumber());
		tasDB2.setString(32, order.getNumber());
		tasDB2.setString(32, order.getNumber());
		tasDB2.setString(32, order.getNumber());
		tasDB2.setString(32, order.getNumber());
		tasDB2.setString(32, order.getNumber());
		tasDB2.setString(32, order.getNumber());
		tasDB2.setString(32, order.getNumber());
		tasDB2.setString(32, order.getNumber());
		
		/*CCNUMBER = ?
		CCMONTH = ?
CCYEAR = ?
CCHOLDER = ?
ERROR = ?
ERRORTYPE = ?
NEGATIVELISTRESULT = ?
ORDERTYPE = ?
PRIORITY = ?
COMPLETED = ?
COMPLETEDDATE = ?
SENTBY = ?
SENTDATE = ?
EMAILSENT = ?
DELETEDBY = ?
DELETEDDATE = ?
WHERE NUMBER = ?
		order.setPriority(Priority.Expedite);
		order.setSentBy(getUser());
		order.setSentDate(Util.getDate());
		order.setCompleted(false);
		order.setError(false);
		order.setErrorType(ErrorType.ErrorFree);
		order.setDeleteBy(null);
		order.setDeletedDate(null);
		order.setEmailSent(false);
		order.setNegativeListResult(false);
		order.setCreditCard(cc);
		order.setOrderType(OrderType.Broker);
		*/
		tasDB2.setString(32, order.getNumber());
			
		//Executing
		tasDB2.executePreparedStatement();

		//Close current statement
		tasDB2.closeStatement();

		//Close connection
		tasDB2.closeConnection();
	}
	
	public static void deleteByNumber(Order order) {
		
		log.info("Deleting order " + order.getNumber());
		
		//Open connection
		tasDB2.openConnection();

		//Create Statement
		tasDB2.setPreparedStatement("UPDATE GARS.ORDERS SET DELETEDBY = ?, DELETEDDATE = ? WHERE NUMBER = ? ");
		
		tasDB2.setString(1, order.getDeleteBy());
		tasDB2.setString(2, order.getDeletedDate());
			
		tasDB2.setString(3, order.getNumber());
			
		//Executing
		tasDB2.executePreparedStatement();

		//Close current statement
		tasDB2.closeStatement();

		//Close connection
		tasDB2.closeConnection();
	}
	
	public static void updateByNumber(List<Order> orderList) {
		
		log.info("Updating order list querying by number");
		
		//Open connection
		tasDB2.openConnection();

		//Create Statement
		tasDB2.setPreparedStatement("UPDATE GARS.ORDERS SET AMOUNTEPOS = ?,AMOUNTSAP = ?, AUTHORIZATIONCODE = ?, CODE = ?, CREATEDBY = ?, CCNUMBER = ?, CCMONTH = ?, CCYEAR = ?, CCHOLDER = ?, CURRENCY = ?, EMAIL = ?, ERROR = ?, ERRORTYPE = ?, FRAUDRESULT = ?, IBMREFERENCE = ?, NEGATIVELISTRESULT = ?, NUMBER = ?, ORDERTYPE = ?, PODATE = ?, PONUMBER = ?, PRIORITY = ?, SALESAREAID = ?, SHIPPINGCODE = ?, COMPLETED = ?, COMPLETEDDATE = ?, SENTBY = ?, SENTDATE = ?, EMAILSENT = ?, EMAILSENTAT = ? , DELETEDBY = ?, DELETEDDATE = ? WHERE NUMBER = ? ");
		
		for (Order order : orderList) {
		
			addOrder(order);
			
			tasDB2.setString(32, order.getNumber());
			
			//Executing
			tasDB2.executePreparedStatement();
		}

		//Close current statement
		tasDB2.closeStatement();

		//Close connection
		tasDB2.closeConnection();
	}
}
