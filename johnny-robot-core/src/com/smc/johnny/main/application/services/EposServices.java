package com.smc.johnny.main.application.services;

import java.awt.event.KeyEvent;

import com.smc.johnny.main.application.infrastructure.Utilities;
import com.smc.johnny.main.application.model.CompanyInfo;
import com.smc.johnny.main.application.model.Order;
import com.smc.johnny.main.application.model.Order.OrderType;
import com.smc.johnny.main.communicator.Speaker;
import com.smc.johnny.main.queuer.QueueManager;


public class EposServices extends IServices{

	private boolean manualOrder = false;
	private boolean authorized = false;
		
	public void closeEpos(){
		
		Speaker.info("Closing EPOS...");
		
		closeWindow();
	}
	
	public void login(){
		
		Speaker.info("Logging to EPOS...");
		
		execute("iexplore.exe " + ConfigurationServices.getEposAddress());
		
		waitUntil("EposReady");
		
		delay(2000);
		
		click("EposUserId");
		
		paste(ConfigurationServices.getEposUser());
		
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(ConfigurationServices.getEposPwd());
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		waitUntil("EposLoginComplete");
	}
	
	public void queryOrderDetails(){
	
		Speaker.info("Searching order details...");
		
		switchWindow();
		
		waitUntil("EposMerchantDropDown");
		
		click("EposMerchantDropDown");
		
		keyPressRelease(KeyEvent.VK_PAGE_UP);
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		keyPressRelease(KeyEvent.VK_TAB);
		
		keyPressRelease(KeyEvent.VK_DELETE);
		
		click("EposTransactionId");
		
		paste(QueueManager.getCurrentItem().getIbmReference());
		
		keyPressRelease(KeyEvent.VK_TAB);
		keyPressRelease(KeyEvent.VK_TAB);
		keyPressRelease(KeyEvent.VK_ENTER);

		waitUntil("EposOrderDetailsReady");
	}
	
	public void retrieveOrderDetails(){
		
		Speaker.info("Retrieving " + QueueManager.getCurrentItem().getOrderType() + " details...");
		
		waitUntil("EposPageLoaded");
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Telesales)){
			
			click("EposRollDown");
			
			delay(1000);

			if(authorized){
				tripleClick("EposAmountAuthorized");
			}
			else{
				tripleClick("EPOSAmount");
			}
			
			if( copy().equals("INTERNET") )
				manualOrder = true;

			if(manualOrder)
				tripleClick("EPOSAmountAtManualOrder");
			
			String amount = copy().replace(",","");
			
			QueueManager.getCurrentItem().setAmountEPOS(amount);
			
			if(manualOrder)
				doubleClick("EposAuthorizationCodeAtManualOrder");
			else
				doubleClick("EposAuthorizationCode");

			QueueManager.getCurrentItem().setAuthorizationCode(copy());

			if(manualOrder)
				doubleClick("EposFraudResultAtManualOrder");
			else
				doubleClick("EposFraudResult");

			QueueManager.getCurrentItem().setFraudResult(copy());
			
		}else{
			
			click("EposRollDown");
			delay(1000);
			
			if(authorized)
				tripleClick("EposAmountPtxAuthorized");
			else
				tripleClick("EPOSAmountPtx");
			
			
			String amount = copy().replace(",","");
						
			try {
				Float.parseFloat(amount);
			} catch (NumberFormatException e) {
				Speaker.info("Probably at a wrong position...");
				// Error happens if I'm misplaced at this screen.
				keyPressRelease(KeyEvent.VK_PAGE_UP);
				delay(1000);
				click("EposRollDown");
				delay(1000);
				
				tripleClick("EPOSAmountPtx");
				
				amount = copy().replace(",","");
			}
			
			QueueManager.getCurrentItem().setAmountEPOS(amount);
			
		}
	}
	
	public Boolean checkAlreadyCaptured(){
		
		Speaker.info("Checking if its already captured...");
		
		authorized = false;

		doubleClick("EposFirstCaptureLocation");
		
		if(copy().equals("AUTHORIZE")&&!QueueManager.getCurrentItem().getOrderType().equals(Order.OrderType.Telesales)){
			authorized = true;
			Speaker.info("Order Already authorized...");
		}
		if(copy().equals("CARDVERIFI")&&QueueManager.getCurrentItem().getOrderType().equals(Order.OrderType.Telesales)){
			authorized = true;
			Speaker.info("Order Already authorized...");
		}
		
		if(!copy().equals("CAPTURE")){
			
			doubleClick("EposSecondCaptureLocation");
			
			if(!copy().equals("CAPTURE"))
				return false;
		}
		Speaker.info("Order already captured...");
		return true;
	}
	
	public Boolean validateAmounts(){
		
		Speaker.info("Validating amounts...");
		
		Double amountSap = Double.parseDouble(QueueManager.getCurrentItem().getAmountSAP());
		Double amountEpos = Double.parseDouble(QueueManager.getCurrentItem().getAmountEPOS());
		
		//Ignore check if its a PTX Canada
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Ptx) && QueueManager.getCurrentItem().getCurrency().equals("CAD") )
			return false;
		
		if(	amountSap - amountEpos >= -0.01 && amountSap - amountEpos <= 0.01)
			return false;
		
		Speaker.info("Amount mismatch...");
		return true;
	}
	
	public Boolean validateFraudResult(){
		
		Speaker.info("Checking fraud result...");
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Telesales))
			if(!QueueManager.getCurrentItem().getPoNumber().toLowerCase().contains("ver"))
				if(QueueManager.getCurrentItem().getFraudResult().toLowerCase().trim().equals("review ") ||
						QueueManager.getCurrentItem().getFraudResult().toLowerCase().trim().equals("reject ")){
					Speaker.info("Fraud alert...");
					return true;
				}
					
		
		return false;
	}
	
	public void gotoCapture(){
		
		Speaker.info("Going to capture...");
		
		keyPressRelease(KeyEvent.VK_PAGE_UP);
		
		//US PTX Orders need to be authorized
		if((QueueManager.getCurrentItem().getCurrency().equals("USD") && QueueManager.getCurrentItem().getOrderType().equals(OrderType.Ptx)))
		{
			waitUntil("EposContinueToDropDown");
			click("EposContinueToDropDown");
			
			if(authorized){
				keyPressRelease(KeyEvent.VK_END);
				keyPressRelease(KeyEvent.VK_C);
			} else
				keyPressRelease(KeyEvent.VK_A);

			keyPressRelease(KeyEvent.VK_ENTER);
		}
				
		//Telesales US needs to go to capture
		//PTX Canada will go to collect always
		if((QueueManager.getCurrentItem().getOrderType().equals(OrderType.Telesales) && !QueueManager.getCurrentItem().getFraudResult().equals("ACCEPT ")) || ((QueueManager.getCurrentItem().getCurrency().equals("CAD") && QueueManager.getCurrentItem().getOrderType().equals(OrderType.Ptx)))){
			
			waitUntil("EposContinueToDropDown");
			click("EposContinueToDropDown");

			keyPressRelease(KeyEvent.VK_END);
			keyPressRelease(KeyEvent.VK_C);
			keyPressRelease(KeyEvent.VK_ENTER);
		}
		
		//Telesales Canada just needs to click continue to capture.
		waitUntil("EposContinueTo");
		click("EposContinueTo");
		delay(2000);
	}
	
	public Boolean authorization(){
		
		if(authorized)
			return false;
		
		Speaker.info("Creating authorization...");

		waitUntil("EposPageLoaded");
		
		delay(1000);
		
		click("EposRollDown");
		delay(1000);
		
		while(!checkFor("EposSubmitAuthorization")){
			waitUntil("InsideBrowser");
			click("InsideBrowser");
			keyPressRelease(KeyEvent.VK_PAGE_UP);
			keyPressRelease(KeyEvent.VK_PAGE_UP);
			keyPressRelease(KeyEvent.VK_PAGE_UP);
			delay(1000);
			click("EposRollDown");
			delay(1000);
		}
		
		click("EposBillToShipTo2");
		waitUntil("EposSubmitAuthorization");
		click("EposSubmitAuthorization");
		
		while(!checkFor("EposAuthorizationReady"))
		{
			if(checkFor("EposPtxDeclined")){
				Speaker.info("Ptx Declined...");
				return true;
			}
			if(checkFor("EposBusinessError")){
				Speaker.info("Business Error detected");
				return true;
			}
		}
		
		waitUntil("EposPageLoaded");
		
		click("EposAuthorizationReady");

		delay(500);
		
		for(int i=0;i<3;i++)
		{
			keyPressRelease(KeyEvent.VK_TAB);
			delay(250);
		}
		
		keyPressRelease(KeyEvent.VK_END);
		
		keyPressRelease(KeyEvent.VK_C);
		
		keyPressOnly(KeyEvent.VK_SHIFT);
		
		keyPressRelease(KeyEvent.VK_TAB);
		
		keyReleaseOnly(KeyEvent.VK_SHIFT);
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		waitUntil("EposCaptureScreenReady");
		
		return false;
	}
	
	public Boolean capture(){
		
		waitUntil("EposPageLoaded");
		
		delay(1000);
		
		Speaker.info("Capturing...");
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Ptx) && QueueManager.getCurrentItem().getCurrency().equals("CAD")){
			
			waitUntil("EposAlwaysCollectReady");
			
			click("EposTotalGrossAmount");
			
			paste(QueueManager.getCurrentItem().getAmountSAP());
			
			delay(1000);
			
		} else {
			
			waitUntil("EposCaptureLoadReady");
			
		}
			
		click("EposRollDown");
		delay(1500);
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Ptx) && QueueManager.getCurrentItem().getCurrency().equals("CAD"))
			click("EposBillToShipTo");
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Ptx) && QueueManager.getCurrentItem().getCurrency().equals("CAD"))
			select("EposInvoiceField2");
		else
			select("EposInvoiceField");
		
		paste(QueueManager.getCurrentItem().getNumber());
		
		if(manualOrder){
			keyPressRelease(KeyEvent.VK_TAB);
			paste(QueueManager.getCurrentItem().getNumber());
		}
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Ptx) && QueueManager.getCurrentItem().getCurrency().equals("CAD")){
			waitUntil("EposSubmitCapture2");
			click("EposSubmitCapture2");
		} else {
			waitUntil("EposSubmitCapture");
			click("EposSubmitCapture");
		}
		
		while(!checkFor("EposCaptureReady")){
			
			if(checkFor("EposCaptureDeclined")){
				return true;
			}
			
			if(checkFor("EposBusinessError")){
				Speaker.info("Business Error detected");
				return true;
			}
		}
		
		waitUntil("EposCaptureReady");
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Ptx) && QueueManager.getCurrentItem().getCurrency().equals("CAD")){
			
			doubleClick("EposIBMOrderRefEpos");
			
			QueueManager.getCurrentItem().setIbmReference(copy().substring(0, 13));
		}
		
		Boolean result = validateCapture();
		
		clickEposLink();
		
		switchWindow();
		
		return result;
	}
	
	public void clickEposLink(){
		waitUntil("EposLink");
		click("EposLink");
	}
	
	private Boolean validateCapture(){
	
		if(checkFor("EposCaptureReady"))
			return false;
		
		Speaker.info("Capture declined...");
		return true;
	}
	
	public boolean createAuthorization(CompanyInfo info){
		
		Speaker.info("Creating Authorization...");
		
		switchWindow();
		
		click("EposCreditCard");
		
		delay(1000);
		
		waitUntil("EposPageLoaded");
		
		waitUntil("EposAuthorization");
		
		click("EposAuthorization");
		
		delay(1000);
		
		waitUntil("EposPageLoaded");
		
		waitUntil("EposMerchantCountry");
		
		click("EposMerchantCountry");
		
		if(QueueManager.getCurrentItem().getCurrency().equals("USD"))
			keyPressRelease(KeyEvent.VK_U);
		else
			keyPressRelease(KeyEvent.VK_C);
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		click("EposCardNumber");
		
		paste(QueueManager.getCurrentItem().getCreditCard().getNumber());
		
		waitUntil("EposMonthDropDown");
		
		click("EposMonthDropDown");
		
		for(int i=0;i<Integer.parseInt(QueueManager.getCurrentItem().getCreditCard().getMonth());i++){
			keyPressRelease(KeyEvent.VK_0);
		}
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		waitUntil("EposYearDropDown");
		
		click("EposYearDropDown");
		
		if(!(Integer.parseInt(QueueManager.getCurrentItem().getCreditCard().getYear())>9))
			keyPressRelease(KeyEvent.VK_0);
		else
			for(int i=0;i+Integer.parseInt(Utilities.getCurrentYear())+1<=Integer.parseInt(QueueManager.getCurrentItem().getCreditCard().getYear());i++){
				keyPressRelease(KeyEvent.VK_1);
			}
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		click("EposAuthorizationAmount");
		
		paste(QueueManager.getCurrentItem().getAmountSAP());
	
		QueueManager.getCurrentItem().setAmountEPOS(QueueManager.getCurrentItem().getAmountSAP());
		
		click("EposAuthorizationCurrency");
		
		if(QueueManager.getCurrentItem().getCurrency().equals("USD"))
			keyPressRelease(KeyEvent.VK_U);
		else
			keyPressRelease(KeyEvent.VK_C);
		
		keyPressRelease(KeyEvent.VK_ENTER);
		
		click("EposRollDown");
		
		click("EposAuthCompanyName");
		
		paste(info.getCompanyName());
		
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(QueueManager.getCurrentItem().getCreditCard().getLastName());
		
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(QueueManager.getCurrentItem().getCreditCard().getFirstName());
		
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(info.getAddress());
		
		keyPressRelease(KeyEvent.VK_TAB);
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(info.getCity());
		
		keyPressRelease(KeyEvent.VK_TAB);

		keyPressRelease(KeyEvent.VK_PAGE_UP);
		
		if(QueueManager.getCurrentItem().getCurrency().equals("USD"))
			for(int i=0;i<4;i++)
				keyPressRelease(KeyEvent.VK_U);
		else
			keyPressRelease(KeyEvent.VK_C);
	
		keyPressRelease(KeyEvent.VK_TAB);
		
		chooseState(info);
		
		keyPressRelease(KeyEvent.VK_TAB);
		
		paste(info.getPostalCode());
		
		click("EposAuthBillToShipTo");
		
		click("EposAuthOrderNumber");
		
		paste(QueueManager.getCurrentItem().getNumber());
		
		waitUntil("EposAuthSubmit");
		
		click("EposAuthSubmit");
		
		delay(1500);
		
		waitUntil("EposPageLoaded");
		
		while(!checkFor("EposAuthorizationReady"))
		{
			if(checkFor("EposPtxDeclined")){
				Speaker.info("Ptx Declined...");
				return true;
			}
		}
		
		click("EposAuthorizationReady");

		delay(500);
		
		for(int i=0;i<3;i++)
		{
			keyPressRelease(KeyEvent.VK_TAB);
			delay(250);
		}
		
		keyPressRelease(KeyEvent.VK_C);
		
		waitUntil("EposContinueToPtx");
		
		click("EposContinueToPtx");
		
		waitUntil("EposCaptureScreenReady");
		
		return false;
	}
	
	private void chooseState(CompanyInfo info)
	{
		int i;
		if(QueueManager.getCurrentItem().getCurrency().equals("USD"))
		{
			if(info.getState().equals("AK"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_A);
				return;
			}
			if(info.getState().equals("AL"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_A);
				return;
			}
			if(info.getState().equals("AR"))
			{
				for(i=0;i<3;i++)
					keyPressRelease(KeyEvent.VK_A);
				return;
			}
			if(info.getState().equals("AS"))
			{
				for(i=0;i<4;i++)
					keyPressRelease(KeyEvent.VK_A);
				return;
			}
			if(info.getState().equals("AZ"))
			{
				for(i=0;i<5;i++)
					keyPressRelease(KeyEvent.VK_A);
				return;
			}
			if(info.getState().equals("CA"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_C);
				return;
			}
			if(info.getState().equals("CO"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_C);
				return;
			}
			if(info.getState().equals("CT"))
			{
				for(i=0;i<3;i++)
					keyPressRelease(KeyEvent.VK_C);
				return;
			}
			if(info.getState().equals("CZ"))
			{
				for(i=0;i<4;i++)
					keyPressRelease(KeyEvent.VK_C);
				return;
			}
			if(info.getState().equals("DC"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_D);
				return;
			}
			if(info.getState().equals("DE"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_D);
				return;
			}
			if(info.getState().equals("FL"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_F);
				return;
			}
			if(info.getState().equals("GA"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_G);
				return;
			}
			if(info.getState().equals("GU"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_G);
				return;
			}
			if(info.getState().equals("HI"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_H);
				return;
			}
			if(info.getState().equals("IA"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_I);
				return;
			}
			if(info.getState().equals("ID"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_I);
				return;
			}
			if(info.getState().equals("IL"))
			{
				for(i=0;i<3;i++)
					keyPressRelease(KeyEvent.VK_I);
				return;
			}
			if(info.getState().equals("IN"))
			{
				for(i=0;i<4;i++)
					keyPressRelease(KeyEvent.VK_I);
				return;
			}
			if(info.getState().equals("KS"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_K);
				return;
			}
			if(info.getState().equals("KY"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_K);
				return;
			}
			if(info.getState().equals("LA"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_L);
				return;
			}
			if(info.getState().equals("MA"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_M);
				return;
			}
			if(info.getState().equals("MD"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_M);
				return;
			}
			if(info.getState().equals("ME"))
			{
				for(i=0;i<3;i++)
					keyPressRelease(KeyEvent.VK_M);
				return;
			}
			if(info.getState().equals("MI"))
			{
				for(i=0;i<4;i++)
					keyPressRelease(KeyEvent.VK_M);
				return;
			}
			if(info.getState().equals("MN"))
			{
				for(i=0;i<5;i++)
					keyPressRelease(KeyEvent.VK_M);
				return;
			}
			if(info.getState().equals("MO"))
			{
				for(i=0;i<6;i++)
					keyPressRelease(KeyEvent.VK_M);
				return;
			}
			if(info.getState().equals("MP"))
			{
				for(i=0;i<7;i++)
					keyPressRelease(KeyEvent.VK_M);
				return;
			}
			if(info.getState().equals("MS"))
			{
				for(i=0;i<8;i++)
					keyPressRelease(KeyEvent.VK_M);
				return;
			}
			if(info.getState().equals("MT"))
			{
				for(i=0;i<9;i++)
					keyPressRelease(KeyEvent.VK_M);
				return;
			}
			if(info.getState().equals("NC"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("ND"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("NE"))
			{
				for(i=0;i<3;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("NH"))
			{
				for(i=0;i<4;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("NJ"))
			{
				for(i=0;i<5;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("NM"))
			{
				for(i=0;i<6;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("NV"))
			{
				for(i=0;i<7;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("NY"))
			{
				for(i=0;i<8;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("OH"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_O);
				return;
			}
			if(info.getState().equals("OK"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_O);
				return;
			}
			if(info.getState().equals("OR"))
			{
				for(i=0;i<3;i++)
					keyPressRelease(KeyEvent.VK_O);
				return;
			}
			if(info.getState().equals("PA"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_P);
				return;
			}
			if(info.getState().equals("PR"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_P);
				return;
			}
			if(info.getState().equals("RI"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_R);
				return;
			}
			if(info.getState().equals("SC"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_S);
				return;
			}
			if(info.getState().equals("SD"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_S);
				return;
			}
			if(info.getState().equals("TN"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_T);
				return;
			}
			if(info.getState().equals("TX"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_T);
				return;
			}
			if(info.getState().equals("UT"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_U);
				return;
			}
			if(info.getState().equals("VA"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_V);
				return;
			}
			if(info.getState().equals("VI"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_V);
				return;
			}
			if(info.getState().equals("VT"))
			{
				for(i=0;i<3;i++)
					keyPressRelease(KeyEvent.VK_V);
				return;
			}
			if(info.getState().equals("WA"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_W);
				return;
			}
			if(info.getState().equals("WI"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_W);
				return;
			}
			if(info.getState().equals("WV"))
			{
				for(i=0;i<3;i++)
					keyPressRelease(KeyEvent.VK_W);
				return;
			}
			if(info.getState().equals("WY"))
			{
				for(i=0;i<4;i++)
					keyPressRelease(KeyEvent.VK_W);
				return;
			}
		}
		else
		{
			if(info.getState().equals("AB"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_A);
				return;
			}
			if(info.getState().equals("BC"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_B);
				return;
			}
			if(info.getState().equals("MB"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_M);
				return;
			}
			if(info.getState().equals("NB"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("NL"))
			{
				for(i=0;i<2;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("NS"))
			{
				for(i=0;i<3;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("NT"))
			{
				for(i=0;i<4;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("NU"))
			{
				for(i=0;i<5;i++)
					keyPressRelease(KeyEvent.VK_N);
				return;
			}
			if(info.getState().equals("ON"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_O);
				return;
			}
			if(info.getState().equals("PE"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_P);
				return;
			}
			if(info.getState().equals("QC"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_Q);
				return;
			}
			if(info.getState().equals("SK"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_S);
				return;
			}
			if(info.getState().equals("YT"))
			{
				for(i=0;i<1;i++)
					keyPressRelease(KeyEvent.VK_Y);
				return;
			}
		}
	}
}
