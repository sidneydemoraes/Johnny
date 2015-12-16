package com.smc.johnny.main.application.services;

import java.awt.event.KeyEvent;

import com.smc.johnny.main.application.model.Order.ErrorType;
import com.smc.johnny.main.application.model.Order.OrderType;
import com.smc.johnny.main.communicator.Speaker;
import com.smc.johnny.main.queuer.QueueManager;
import com.smc.johnny.main.robot.services.RobotServices;


public class GarsServices extends IServices {
	
	private SapServices sapServices;
	private EposServices eposServices;
		
	public GarsServices(SapServices sapServices,EposServices eposServices){
		super();
		this.sapServices = sapServices;
		this.eposServices = eposServices;
	}

	public void runReports(){

		Speaker.info("Running reports...");
		
		sapServices.gotoTelesaleReportTransaction();
		
		Speaker.info("Retrieving US Telesales orders...");
		
		sapServices.getTelesalesOrders("0200", "B ", "00");
		
		Speaker.info("Retrieving CA Telesales orders...");
		
		sapServices.getTelesalesOrders("0026", "B ", "00");
		
		sapServices.gotoMainWindow();
		
		Speaker.info("Retrieving PTX US/CA orders...");
		
		sapServices.gotoPTXReportTransaction();
		
		sapServices.getPTXOrders();
		
		sapServices.gotoMainWindow();
	}

	public void loginSystems(){

		Speaker.info("Logging into EPOS...");
		
		eposServices.login();
		
		Speaker.info("Logging into SAP...");
		
		sapServices.login();
	}

	public void gotoEditOrder(){

		sapServices.editOrder();
	}

	/**
	 * Controls the flow of the process of charging an order.
	 */
	public void chargeOrder(){

		if(sapServices.editOrder())
		{
			
			sapServices.gotoMainWindow();
			sapServices.gotoOrderEditTransaction();
			QueueManager.setError(ErrorType.InUse);
			QueueManager.removeCurrentItem();
			return;
		}
		
		if(sapServices.retrieveAmountAndCurrency())
		{
			sapServices.backOut();
			QueueManager.setError(ErrorType.OrderCancelled);
			QueueManager.setCompleted();
			return;
		}
		
		if(sapServices.retrievePONumber())
		{
			sapServices.backOut();
			
			if(QueueManager.getCurrentItem().getCurrency().equals("USD")){
				QueueManager.setError(ErrorType.OrderCancelled);
				QueueManager.setCompleted();
			} else {	
				QueueManager.setError(ErrorType.CanadianSplitOrder);
				MailerServices.SplitOrder();
				QueueManager.setCompleted();
			}
			return;
		}
		
		if(sapServices.validateAmount()){
			
			sapServices.backOut();
			QueueManager.setError(ErrorType.AmountAbove100k);
			MailerServices.AmountAbove100k();
			QueueManager.setCompleted();
			return;
		}
			
		sapServices.retrievePoDate();
		sapServices.fixCanadaOrders();
		
		if(sapServices.checkShipping())
		{
			sapServices.backOut();
			QueueManager.setError(ErrorType.InvalidShipping);
			MailerServices.InvalidRoute();
			QueueManager.setCompleted();
			return;
		}
		
		sapServices.gotoHeaderDetail();
		sapServices.retrieveSalesAreaAndCreatedBy();
		
		sapServices.gotoAdditionalDataB();
		
		if(sapServices.retrieveIbmOrderRef())
		{	
			sapServices.backOut();
			QueueManager.setError(ErrorType.NoIBMReference);
			MailerServices.NoIbmReference();
			QueueManager.setCompleted();
			return;
		}
			
		sapServices.gotoPaymentCards();

		if(sapServices.isCharged()){
		
			sapServices.backToOrderDetail();
			sapServices.completeOrderDetail();
			QueueManager.setCompleted();
			return;
		}

		if(!sapServices.isCharged() && (QueueManager.getCurrentItem().getIbmReference().isEmpty() || QueueManager.getCurrentItem().getIbmReference().equals("NOREFNUM")))
		{
			sapServices.backOut();
			QueueManager.setError(ErrorType.NoIBMReference);
			MailerServices.NoIbmReference();
			QueueManager.setCompleted();
			return;
		}
		
		sapServices.gotoManualAuthorization();
		
		eposServices.queryOrderDetails();
	
		if(eposServices.checkAlreadyCaptured()){
			
			click("EposRollDown");
			delay(1000);
			
			while(!checkFor("EposAlreadyCapturedRollDownReady")){
				keyPressRelease(KeyEvent.VK_PAGE_UP);
				delay(1000);
				click("EposRollDown");
				delay(1000);
			}
			
			doubleClick("EposAlreadyCapturedAuthorizationCode");
			QueueManager.getCurrentItem().setAuthorizationCode(copy());
			
			eposServices.clickEposLink();
			switchWindow();
			sapServices.fulfillAuthorization();
			sapServices.backToPaymentCards();
			sapServices.backToOrderDetail();
			sapServices.completeOrderDetail();
			QueueManager.setCompleted();
			return;
		}
		
		eposServices.retrieveOrderDetails();
		
		if(eposServices.validateAmounts()){
		
			eposServices.clickEposLink();
			switchWindow();
			sapServices.backOut();
			QueueManager.setError(ErrorType.AmountMismatch);
			MailerServices.AmountMismatch();
			QueueManager.setCompleted();
			return;
		}
		
		if(eposServices.validateFraudResult()){
			
			eposServices.clickEposLink();
			switchWindow();
			sapServices.backOut();
			QueueManager.setError(ErrorType.CaughtInFraudCheck);
			MailerServices.CaughtInFraudCheck();
			QueueManager.setCompleted();
			return;
			
		}
			
		eposServices.gotoCapture();
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Ptx) && QueueManager.getCurrentItem().getCurrency().equals("USD"))
			if(eposServices.authorization()){
				
				eposServices.clickEposLink();
				switchWindow();
				sapServices.backOut();
				QueueManager.setError(ErrorType.CreditCardDeclined);
				MailerServices.OrderDeclined();
				QueueManager.setCompleted();
				return;
			}
		
		
		if(eposServices.capture()){
		
			eposServices.clickEposLink();
			switchWindow();
			sapServices.backOut();
			
			if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Telesales)){
				QueueManager.setError(ErrorType.CaptureDenied);
				MailerServices.CaptureDenied();
			} else {
				QueueManager.setError(ErrorType.CreditCardDeclined);
				MailerServices.OrderDeclined();
			}
			QueueManager.setCompleted();
			
			return;
		}
		
		sapServices.fulfillAuthorization();
		sapServices.backToPaymentCards();
		
		if((QueueManager.getCurrentItem().getOrderType().equals(OrderType.Ptx) && QueueManager.getCurrentItem().getCurrency().equals("CAD"))){
			
			sapServices.gotoAdditionalDataB();
			
			sapServices.setNewIbmOrderRef();
		}
		
		sapServices.backToOrderDetail();
		sapServices.completeOrderDetail();
		
		QueueManager.setCompleted();
	}
	
	public void close(){
		
		if(sapServices.AtSapScreen()){
			sapServices.closeSap();
			eposServices.closeEpos();
		}else{
			eposServices.closeEpos();
			if(sapServices.AtSapScreen())
				sapServices.closeSap();
		}
		
		click("Start");
		
		delay(1000);
		
		click("Run");
		
		delay(1000);
		
		RobotServices.keyPressRelease(KeyEvent.VK_ENTER);
		
		System.exit(0);
	}
	
	public void gotoSapMainWindow(){
		sapServices.gotoMainWindow();
	}
	
	public void gotoSapOrderEditTransaction(){
		sapServices.gotoOrderEditTransaction();
	}
}