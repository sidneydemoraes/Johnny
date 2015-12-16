package com.smc.johnny.main.application.services;

import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.smc.johnny.main.application.infrastructure.Utilities;
import com.smc.johnny.main.application.model.CompanyInfo;
import com.smc.johnny.main.application.model.CreditCard;
import com.smc.johnny.main.application.model.Order;
import com.smc.johnny.main.application.model.Order.ErrorType;
import com.smc.johnny.main.application.model.Order.OrderType;
import com.smc.johnny.main.application.model.Order.Priority;
import com.smc.johnny.main.application.model.User;
import com.smc.johnny.main.application.model.User.UserType;
import com.smc.johnny.main.application.repositories.OrderRepository;
import com.smc.johnny.main.application.repositories.UserRepository;
import com.smc.johnny.main.communicator.Speaker;
import com.smc.johnny.main.queuer.QueueManager;


public class SapServices extends IServices {
	
	private static Logger log = Logger.getRootLogger();

	public void closeSap(){
		
		Speaker.info("Closing SAP...");
		
		closeWindow();
		
		waitUntil("LogoffWarning");
		
		click("SapLogoff");
	}
	
	public Boolean AtSapScreen(){
		
		return checkFor("SapScreen");
	}
	
	public void login(){
		
		Speaker.info("Login to SAP...");
		
		execute(ConfigurationServices.getSapPath());
		
		waitUntil("SapLogonScreenReady");
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		waitUntil("SapScreenReady");
		
		click("SapUserId");
		
		paste(ConfigurationServices.getSapUser());
		
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(ConfigurationServices.getSapPwd());
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		if(checkFor("AlreadyLoggedInWarning"))
		{
			Speaker.info("Already logged in... overwriting connection...");
			
			click("ContinueWithThisLogin");
			
			keyPressRelease(KeyEvent.VK_ENTER);
		}
		
		waitUntil("SapMainScreenReady");
		
		switchWindow();
		
		closeWindow();
	}
	
	public void gotoTelesaleReportTransaction(){
		
		Speaker.info("Going to transaction VA05...");
		
		click("TransactionField");
		
		paste("va05");
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		waitUntil("TelesalesReportScreenReady");
	}
	
	public void gotoPTXReportTransaction(){
		
		Speaker.info("Going to transaction Y_XD2_06000004...");
		
		click("TransactionField");
		
		paste("Y_XD2_06000004");
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		waitUntil("PtxReportScreenReady");
	}
	
	public void getPTXOrders(){
		
		Speaker.info("Getting PTX orders...");
		
		click("DistributionChannel");
		
		paste("H ");
		
		click("PaymentMethods");
		
		paste("Z");
		
		click("DocumentDate");
		
		paste(Utilities.getLastMonthDate());
		
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(Utilities.getDate());
		
		click("DocumentType");
		
		paste("ZGNP");
		
		click("SalesOrganizationSelection");
		
		waitUntil("SalesOrganizationReady");
		
		paste("0200");
		
		keyPressRelease(KeyEvent.VK_TAB);
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste("0026");
		
		keyPressRelease(KeyEvent.VK_F8);
		
		delay(1000);
		
		select("SalesDocument");
		
		keyPressRelease(KeyEvent.VK_DELETE);
		
		click("DeliveryBlockSelection");
		
		waitUntil("DeliveryBlockReady");
		
		paste("96");
		
		keyPressRelease(KeyEvent.VK_TAB);
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste("Z9");
		
		keyPressRelease(KeyEvent.VK_F8);
		
		delay(3000);
		
		click("SapListViewer");
		
		delay(3000);
		
		keyPressRelease(KeyEvent.VK_F8);
		
		waitUntil("PtxReportReady");
		
		if(checkFor("ScrollDown")){
			
			click("PtxFirstOrder");
		
			while(checkFor("PtxReportScrollDown"))
				keyPressRelease(KeyEvent.VK_PAGE_DOWN);
		}
				
		try {
			OrderRepository.add(getPtxOrderList(selectAndCopy("PtxOrderList")));
		} catch (SQLException e) {
			log.error("Error adding PTX orders to repository.", e);
		}
		
		click("Back");
		
		waitUntil("PtxReportScreenReady");
	}
	
	public void getTelesalesOrders(String salesOrganization, String distributionChannel, String division){
		
		Speaker.info("Getting Telesales orders...");
		
		click("OrganizationalData");
		
		waitUntil("OrganizationalDataReady");
		
		click("SalesOrganization");
		
		paste(salesOrganization);
		
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(distributionChannel);
		
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(division);
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		delay(2000);
		
		click("DisplayVariants");
		
		waitUntil("DisplayVariantsReady");
		
		doubleClick("Requests");
		
		delay(2000);
	
		click("OpenSalesOrder");
		
		delay(2000);
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		waitUntil("TelesalesReportReady");
		
		if(checkFor("ScrollDownTelesales")){
		
			click("TelesalesFirstOrder");
			
			while(checkFor("TelesalesReportScrollDown"))
				keyPressRelease(KeyEvent.VK_PAGE_DOWN);
		}
			
		try {
			OrderRepository.add(getTelesalesOrderList(selectAndCopy("OrderList")));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		click("Back");
		
		waitUntil("TelesalesReportScreenReady");
	}
	
	public void gotoMainWindow(){
		
		click("Back");
		
		waitUntil("SapMainScreenReady");
	}
	
	public void gotoOrderEditTransaction(){
		
		Speaker.info("Going to transaction VA02...");

		click("TransactionField");
		
		paste("va02");
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		waitUntil("OrderTransactionReady");
		
	}
	
	public Boolean editOrder(){
	
		Speaker.info("Querying order " + QueueManager.getCurrentItem().getNumber() + "...");
		
		select("OrderField");
		
		paste(QueueManager.getCurrentItem().getNumber());
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		while(!checkFor("OrderReady")){
			if(checkFor("ConsiderSubsequentDocuments"))
				keyPressRelease(KeyEvent.VK_ENTER);
		
			if(checkFor("InUseAlert"))
				return true;
		}
		
		delay(2000);
		
		return false;
	}
	
	public Boolean retrievePONumber(){
	
		Speaker.info("Retrieving PO Number...");
		
		QueueManager.getCurrentItem().setPoNumber(selectAndCopy("PoNumberField"));
		
		if(QueueManager.getCurrentItem().getPoNumber().contains("_1") || QueueManager.getCurrentItem().getPoNumber().contains("_2")){
			
			Speaker.info("Split order...");
			return true;
		}
		
		if(QueueManager.getCurrentItem().getPoNumber().toLowerCase().contains("cancel")){
			
			Speaker.info("Order cancelled...");
			return true;
		}

		select("PoNumberField");
		
		paste(QueueManager.getCurrentItem().getPoNumber()+"-");
		
		return false;
	}
	
	public boolean retrieveAmountAndCurrency(){
		
		Speaker.info("Retrieving Amount and Currency...");
		
		QueueManager.getCurrentItem().setCurrency(selectAndCopy("Currency"));
		
		QueueManager.getCurrentItem().setAmountSAP(selectAndCopy("AmountSap").replace(",",""));
		
		if(QueueManager.getCurrentItem().getAmountSAP() == "0.00")
		{
			Speaker.info("Order cancelled...");
			return true;
		}
		return false;
	}
	
	public Boolean validateAmount(){
		
		Speaker.info("Validating amount...");
		
		Double amountSap = Double.parseDouble(QueueManager.getCurrentItem().getAmountSAP());
		
		if(	amountSap > 99999){
			Speaker.info("Order amount is above 100k...");
			return true;
		}
		
		return false;
	}
	
	public void retrievePoDate(){
	
		Speaker.info("Retrieving PO Date...");
		
		select("PoDate");
		
		QueueManager.getCurrentItem().setPoDate(Utilities.getDate());
	}
	
	public void fixCanadaOrders(){
		
		if(QueueManager.getCurrentItem().getCurrency().equals("CAD")){
			
			Speaker.info("Fixing Canadian orders...");
			click("Save");
			waitUntil("OrderTransactionReady");
			delay(2000);
			editOrder();
			delay(1000);
		}
	}
	
	public Boolean checkShipping(){
		
		checkDeletedLines();
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Ptx)){
		
			Speaker.info("Checking shipping...");
			
			click("Shipping");
			
			waitUntil("ShippingReady");
			
			QueueManager.getCurrentItem().setShippingCode(selectAndCopy("ShippingMethod"));
			
			if(!QueueManager.getCurrentItem().getShippingCode().equals("PUAD"))
				if(QueueManager.getCurrentItem().getShippingCode().equals("UPSN") &&
				   !checkUpsnTextIsEmpty())
					return false;
				else
				{
					Speaker.info("Invalid Shipping...");
					return true;
				}
		}
		
		return false;
		
	}
	
	/**
	 * If an order has shipping code UPSN, this method checks whether it contains
	 * the proper text at the order header.
	 * @return
	 */
	private Boolean checkUpsnTextIsEmpty() {
		gotoHeaderDetail();
		
		gotoTextsTab();
		
		while(!checkFor("TextsFullRollDownReady")){
			click("TextsRollDown");
		}
		
		click("LastItemFromTextsTab");
		
		if(selectAndCopy("FirstCommentsLine").equals(""))
			return true;
		else
			return false;
	}

	public void backOut(){
		
		click("Back");
		
		while(!checkFor("PaymentCardsReady") && !checkFor("OrderReady") && !checkFor("OrderTransactionReady"))
			if(checkFor("AuthorizationWarning"))
				keyPressRelease(KeyEvent.VK_ENTER);
			else
				checkDeletedLines();
		
		if(checkFor("OrderReady")){

			addNoteAnnotation();
			
			click("Back");
			
			checkDeletedLines();
			
			while(!checkFor("OrderTransactionReady"))
				if(checkFor("SaveOptionReady"))
					click("NotSave");
		}else if(checkFor("PaymentCardsReady")){
			
			click("Back");
			
			checkDeletedLines();
			
			waitUntil("OrderReady");
			
			addNoteAnnotation();
			
			click("Back");
			
			checkDeletedLines();
			
			while(!checkFor("OrderTransactionReady"))
				if(checkFor("SaveOptionReady"))
					click("NotSave");
		}
	}
	
	public void addNoteAnnotation(){
		
		if(!QueueManager.getCurrentItem().getPoNumber().toLowerCase().contains("not"))
			if(QueueManager.getCurrentItem().getPoNumber().length() < 30)
				QueueManager.getCurrentItem().setPoNumber(QueueManager.getCurrentItem().getPoNumber() + "-note");
			else{
				QueueManager.getCurrentItem().setPoNumber(QueueManager.getCurrentItem().getPoNumber().substring(0, 29));
				QueueManager.getCurrentItem().setPoNumber(QueueManager.getCurrentItem().getPoNumber() + "-note");
			}
		
		select("PoNumberField");

		paste(QueueManager.getCurrentItem().getPoNumber());
	}
	
	public void gotoHeaderDetail(){
		
		click("HeaderDetailBox");
		
		waitUntil("HeaderDetailReady");
	}
	
	public void retrieveSalesAreaAndCreatedBy(){
	
		Speaker.info("Retrieving Sales Area and User...");
		
		QueueManager.getCurrentItem().setSalesAreaId(selectAndCopy("SalesArea"));
			
		QueueManager.getCurrentItem().setCreatedBy(selectAndCopy("CreatedBy"));
	}
	
	public void gotoAccounting(){
		
		click("Accounting");
		
		waitUntil("AccountingReady");
	}
	
	public void checkPaymentMethod(){
		
		Speaker.info("Checking Payment Method...");
		
		select("PaymentMethod");
		
		if(!copy().equals("Z")){
			
			select("PaymentMethod");
			
			paste("Z");
		}
	}

	public void gotoAdditionalDataB(){
		
		click("OptionsMenu");
		
		click("AdditionalDataB");
		
		waitUntil("AdditionalDataBReady");
	}
	
	public void gotoTextsTab(){
		
		click("OptionsMenu");
		
		click("TextsMenuOption");
		
		waitUntil("TextsFirstRowReady");
		
	}
	
	public Boolean retrieveIbmOrderRef(){
	
		Speaker.info("Retrieving IBM Order Ref...");
		
		QueueManager.getCurrentItem().setIbmReference(selectAndCopy("IBMOrderRef"));
		
		if(QueueManager.getCurrentItem().getIbmReference().isEmpty() ||
			QueueManager.getCurrentItem().getIbmReference().equals("NOREFNUM")){
			
			if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Telesales)){
			
				Speaker.info("Invalid IBM Order Ref...");
				return true;
			}
		}
		return false;
	}
	
	public void setNewIbmOrderRef(){
		
		select("IBMOrderRef");
		
		paste(QueueManager.getCurrentItem().getIbmReference());
	}
	
	public void gotoPaymentCards(){
	
		click("OptionsMenu");
		
		click("PaymentCards");
		
		delay(1000);
		
		waitUntil("PaymentCardsReady");
	}
	
	public Boolean isCharged(){
		if(checkFor("Charged")){
			Speaker.info("Order already charged...");
			return true;
		}
		
		return false;
	}
	
	public CompanyInfo retrieveCompanyInformation(){
	
		Speaker.info("Retrieving Company Information...");
		
		click("Partners");
		
		waitUntil("PartnersReady");
		
		doubleClick("SoldToName");
		
		waitUntil("SoldToReady");
		
		CompanyInfo info = new CompanyInfo();
		
		info.setCompanyName(selectAndCopy("CompanyName"));
		
		info.setAddress(selectAndCopy("CompanyAddress"));
		
		info.setPostalCode(selectAndCopy("CompanyZipCode"));
		
		info.setCity(selectAndCopy("CompanyCity"));
		
		info.setCountry(selectAndCopy("CompanyCountry"));
		
		info.setState(selectAndCopy("CompanyState"));
		
		click("ClosePartners");
		
		waitUntil("PartnersReady");
		
		return info;
	}
	
	public void gotoManualAuthorization(){
	
		click("ManualAuthorization");
		
		waitUntil("ManualAuthorizationReady");
	}
	
	public void fulfillAuthorization(){
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Telesales))
			paste(QueueManager.getCurrentItem().getAuthorizationCode());
		else
			paste(Utilities.getDate());			
		
		keyPressRelease(KeyEvent.VK_TAB);
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(QueueManager.getCurrentItem().getIbmReference());
		
		keyPressRelease(KeyEvent.VK_TAB);
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(QueueManager.getCurrentItem().getAmountSAP());
	}
	
	public void backToPaymentCards(){
		
		click("Back");
		
		waitUntil("PaymentCardsReady");
	}
	
	public void backToOrderDetail(){
		
		click("Back");
		
		waitUntil("OrderReady");
	}
	
	public void completeOrderDetail(){
	
		if(!QueueManager.getCurrentItem().getPoNumber().contains("*"))
			if(QueueManager.getCurrentItem().getPoNumber().length() < 34)
				QueueManager.getCurrentItem().setPoNumber(QueueManager.getCurrentItem().getPoNumber() + "*");
			else{
				QueueManager.getCurrentItem().setPoNumber(QueueManager.getCurrentItem().getPoNumber().substring(0, 33));
				QueueManager.getCurrentItem().setPoNumber(QueueManager.getCurrentItem().getPoNumber() + "*");
			}
		
		select("PoNumberField");

		paste(QueueManager.getCurrentItem().getPoNumber());
		
		click("Save");
		
		waitUntil("OrderTransactionReady");
	}
	
	public void checkDeletedLines(){
		
		if(AtSapScreen())
			if(checkFor("DeletedInventoryScreen"))
			{
				//if(checkFor("ATP Inventory"))
				//	click("ATPYes");
					
				click("Back");
				delay(1000);
			}
	}
	
	/**
	 * Filters the orders from the Ptx report to a list of orders.
	 *  
	 * @param orders
	 * @return list of ptx orders
	 */
	private List<Order> getPtxOrderList(String orders){
		
		List<Order> orderList = new ArrayList<Order>();
		
		String[] orderArrayList = orders.split("\n");
		
		for(int i=0;i<orderArrayList.length;i++){

			if(!orderArrayList[i].isEmpty()){
				Order order = new Order();
			
				String[] orderInfo = orderArrayList[i].split("\t");
				
				order.setNumber(orderInfo[0]);
				if(orderInfo.length > 11)
					order.setIbmReference(orderInfo[11]);
				order.setOrderType(OrderType.Ptx);
				order.setCode("ZGNP");
				order.setCreditCard(new CreditCard());
				order.setErrorType(ErrorType.ErrorFree);
				order.setPriority(Priority.Expedite);
				order.setSentBy("TAS-Server");
				order.setSentDate(Utilities.getDate());
				
				if(!order.getNumber().isEmpty() &&
				   !orderList.contains(order) &&
				    order.getIbmReference()!= null){
					if(!order.getIbmReference().isEmpty())
						orderList.add(order);
				}
			}
		}
		
		Speaker.info("Retrieved " + orderList.size() + " valid orders...");
		
		return orderList;
		
	}
	
	/**
	 * Filters the orders from the Telesales report to a list of orders.
	 * Calls validateOrders
	 * 
	 * @param orders
	 * @return
	 */
	private List<Order> getTelesalesOrderList(String orders){
		
		List<Order> orderList = new ArrayList<Order>();
		
		String[] orderArrayList = orders.split("\n");
		
		for(int i=0;i<orderArrayList.length;i++){
			
			Order order = new Order();
			
			String[] orderInfo = orderArrayList[i].split("\t");

			order.setPoNumber(orderInfo[0]);
			order.setPoDate(orderInfo[1]);
			order.setCode(orderInfo[2]);
			order.setNumber(orderInfo[3]);
			order.setCreatedBy(orderInfo[4]);
			order.setOrderType(OrderType.Telesales);
			order.setCreditCard(new CreditCard());
			order.setErrorType(ErrorType.ErrorFree);
			order.setSentBy("TAS-Server");
			order.setSentDate(Utilities.getDate());
			
			orderList.add(order);
		}
		
		return validateOrders(orderList);
	}
	
	/**
	 * Validates the given list of orders.
	 * 
	 * @param orderList
	 * @return validOrderList
	 */
	private List<Order> validateOrders(List<Order> orderList){
		
		Speaker.info("Validating orders...");
	
		List<Order> validOrderList = new ArrayList<Order>(); 
		
		List<User> apmList = UserRepository.getUsers(UserType.APM);
		
		List<String> apmNameList = new ArrayList<String>();
		
		for (User user : apmList)
			apmNameList.add(user.getSapId());

		for (Order order : orderList) {
			
			log.debug("Validating order " + (orderList.indexOf(order) + 1) + " of " + orderList.size());
			
			if( !order.getPoNumber().contains("*") &&
				!order.getPoNumber().toUpperCase().contains("NOT") &&
				!(		order.getPoNumber().toUpperCase().startsWith("CN") && 
						order.getPoNumber().length()<=12) &&
				!(		order.getPoNumber().toUpperCase().startsWith("HL") || 
						order.getPoNumber().toUpperCase().startsWith("RO") || 
						order.getPoNumber().toUpperCase().startsWith("DM") ||
						order.getPoNumber().toUpperCase().startsWith("CM") || 
						order.getPoNumber().toUpperCase().startsWith("SWAP") || 
						order.getPoNumber().toUpperCase().startsWith("PO")) &&
				!order.getCreatedBy().equals("RDORAN") &&
				!order.getPoNumber().isEmpty()){
					if(order.getPoNumber().toUpperCase().contains("EXP")){
						order.setPriority(Priority.Expedite);
						validOrderList.add(order);
					}else{
						order.setPriority(Priority.Normal);
						validOrderList.add(order);
					}
			}
		}
		
		Speaker.info("Retrieved " + validOrderList.size() + " valid orders...");
		
		return validOrderList;
	}
}
