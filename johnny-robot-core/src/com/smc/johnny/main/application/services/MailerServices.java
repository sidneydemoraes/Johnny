package com.smc.johnny.main.application.services;

import java.util.List;

import com.smc.johnny.main.application.model.Order.OrderType;
import com.smc.johnny.main.application.model.User;
import com.smc.johnny.main.application.model.User.UserType;
import com.smc.johnny.main.application.repositories.UserRepository;
import com.smc.johnny.main.mailer.Mailer;
import com.smc.johnny.main.queuer.QueueManager;


public class MailerServices {
	
	private static List<User> sharedInboxList = UserRepository.getUsers(UserType.ApmSharedInbox);
	private static List<User> admins = UserRepository.getUsers(UserType.Admin);
	
	
	public static void CaptureDenied(){
		
		User user = sharedInboxList.get(0);
		
		String mailTo = user.getEmail();
		String messageSubject = QueueManager.getCurrentItem().getOrderType().toString() + " Order: " + QueueManager.getCurrentItem().getNumber() + " - Technical Error.";
		String messageBody = user.getName() + ",\n\nI've noticed that a technical error has occurred with this order.\n" +
							 "This needs to be manually charged.\n\n" +
							 "Cordially,\nGARS Automation Hub\nTAS";
		Boolean includeAttachment = false;
		
		Mailer.send(mailTo, messageSubject, messageBody, includeAttachment);
	}
	
	
	public static void SplitOrder(){
		
		User user = sharedInboxList.get(0);
		
		String mailTo = user.getEmail();
		String messageSubject = QueueManager.getCurrentItem().getOrderType().toString() + " Order: " + QueueManager.getCurrentItem().getNumber() + " - Split Order.";
		String messageBody = user.getName() + ",\n\nI've noticed that this is a split order.\n" +
							 "This needs to be manually charged. Please ignore the note annotation on the PO Number.\n\n" +
							 "Cordially,\nGARS Automation Hub\nTAS";
		Boolean includeAttachment = false;
		
		Mailer.send(mailTo, messageSubject, messageBody, includeAttachment);
	}
	
	public static void OrderRefIsEmpty(){
		
		User user;
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Telesales))
			user = UserRepository.getUser(QueueManager.getCurrentItem().getCreatedBy());
		else
			user = sharedInboxList.get(0);
		
		String mailTo = user.getEmail();
		String messageSubject = QueueManager.getCurrentItem().getOrderType().toString() + " Order: " + QueueManager.getCurrentItem().getNumber() + " - IBM Order Ref. is empty.";
		String messageBody = user.getName() + ",\n\nI've noticed that this order lacks the proper IBM Order Ref.\n" +
							 "When this is fixed, please remove the note annotation from the PO Number.\n\n" +
							 "Cordially,\nGARS Automation Hub\nTAS";
		Boolean includeAttachment = false;
		
		Mailer.send(mailTo, messageSubject, messageBody, includeAttachment);
	}
	
	public static void CaughtInFraudCheck(){
		
		User user = UserRepository.getUser(QueueManager.getCurrentItem().getCreatedBy());
		
		String mailTo = user.getEmail();
		String messageSubject = QueueManager.getCurrentItem().getOrderType().toString() + " Order: " + QueueManager.getCurrentItem().getNumber() + " - Fraud Check Review/Reject.";
		String messageBody = user.getName() + ",\n\nI've noticed that this order didn't fully pass on E-Pos fraud checks.\n" +
							 "After verifying, please include a ver annotation on the PO Number.\n" +
							 "Remove the note annotation from the PO Number.\n\n" +
							 "Cordially,\nGARS Automation Hub\nTAS";
		Boolean includeAttachment = false;
		
		Mailer.send(mailTo, messageSubject, messageBody, includeAttachment);
	}
	
	public static void AmountAbove100k(){
		
		User user;
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Telesales))
			user = UserRepository.getUser(QueueManager.getCurrentItem().getCreatedBy());
		else
			user = sharedInboxList.get(0);
		
		String mailTo = user.getEmail();
		String messageSubject = QueueManager.getCurrentItem().getOrderType().toString() + " Order: " + QueueManager.getCurrentItem().getNumber() + " - Amount Above 100k!";
		String messageBody = user.getName() + ",\n\nI've noticed that this order amount is above of what I'm able to charge.\n" +
							"You will have to manually charge this order.\n\n" +
							"Cordially,\nGARS Automation Hub\nTAS";
		Boolean includeAttachment = false;
		
		Mailer.send(mailTo, messageSubject, messageBody, includeAttachment);
	}
	
	public static void AmountMismatch(){
		
		User user;
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Telesales))
			user = UserRepository.getUser(QueueManager.getCurrentItem().getCreatedBy());
		else
			user = sharedInboxList.get(0);
		
		String mailTo = user.getEmail();
		String messageSubject = QueueManager.getCurrentItem().getOrderType().toString() + " Order: " + QueueManager.getCurrentItem().getNumber() + " - Amount Mismatch!";
		String messageBody = user.getName() + ",\n\nI've noticed that this order amount doesn't match between SAP and E-POS.\n" +
							"When this is fixed, please remove the note annotation from the PO Number.\n\n" +
							"Cordially,\nGARS Automation Hub\nTAS";
		Boolean includeAttachment = false;
		
		Mailer.send(mailTo, messageSubject, messageBody, includeAttachment);
	}
	
	public static void ImStuck(String coordinateName){
		
		User sharedInbox = sharedInboxList.get(0);
	
		String adminEmails = "";
		
		for (User user : admins)
			adminEmails = adminEmails + user.getEmail() + ",";
		
		String mailTo = sharedInbox.getEmail();
		String copyTo = adminEmails;
		String messageSubject = "TAS is Stuck!! Order# " + QueueManager.getCurrentItem().getNumber();
		String messageBody = "Team,\n\nI've been stuck for more than 1 minute. Please help me out.\n\nAn email has been sent to the administration team and the stop cause will be investigated.\nI was waiting for coordinate: " + coordinateName;
		Boolean includeAttachment = true;
		
		Mailer.send(mailTo, copyTo, messageSubject, messageBody, includeAttachment);
	}
	
		
	public static void OrderDeclined(){
		
		User user;
		
		if(QueueManager.getCurrentItem().getOrderType().equals(OrderType.Telesales))
			user = UserRepository.getUser(QueueManager.getCurrentItem().getCreatedBy());
		else
			user = sharedInboxList.get(0);
		
		String mailTo = user.getEmail();
		String messageSubject = QueueManager.getCurrentItem().getOrderType().toString() + " Order: " + QueueManager.getCurrentItem().getNumber() + " - Credit Card Declined!";;
		String messageBody = user.getName() + ",\n\nI've noticed that this order credit card has been declined.\n" +
							"A new authorization with a different credit card is needed.\n" +
							"When this is fixed, please remove the note annotation from the PO Number.\n\n" +
							"Cordially,\nGARS Automation Hub\nTAS";
		Boolean includeAttachment = false;
		
		Mailer.send(mailTo, messageSubject, messageBody, includeAttachment);
	}
	
	public static void InvalidRoute(){
		
		User user = sharedInboxList.get(0);
		
		String mailTo = user.getEmail();
		String messageSubject = QueueManager.getCurrentItem().getOrderType().toString() + " Order: " + QueueManager.getCurrentItem().getNumber() + " - Invalid Shipping Route!";
		String messageBody = user.getName() + ",\n\nI've noticed that this order shipping route is not PUAD.\n" +
		"A new authorization should be created including shipping fees and then charged.\n\n" +
		"Cordially,\nGARS Automation Hub\nTAS";
		Boolean includeAttachment = false;
		
		Mailer.send(mailTo, messageSubject, messageBody, includeAttachment);
	}


	public static void NoIbmReference() {
		
		User user = sharedInboxList.get(0);
		
		String mailTo = user.getEmail();
		String messageSubject = QueueManager.getCurrentItem().getOrderType().toString() + " Order: " + QueueManager.getCurrentItem().getNumber() + " - No IBM Reference!";
		String messageBody = user.getName() + ",\n\nI've noticed that this order has no IBM reference number.\n\n" +
		"Cordially,\nGARS Automation Hub\nTAS";
		Boolean includeAttachment = false;
		
		Mailer.send(mailTo, messageSubject, messageBody, includeAttachment);
		
	}
	
	public static void Error(){
		
		User sharedInbox = sharedInboxList.get(0);
	
		String adminEmails = "";
		
		for (User user : admins)
			adminEmails = adminEmails + user.getEmail() + ",";
		
		String mailTo = sharedInbox.getEmail();
		String copyTo = adminEmails;
		String messageSubject = "TAS had a Fatal Error!!";
		String messageBody = "Team,\n\nA fatal error occurred. Please refer to the attachments below.";
		Boolean includeAttachment = true;
		
		Mailer.send(mailTo, copyTo, messageSubject, messageBody, includeAttachment);
				
	}
}
