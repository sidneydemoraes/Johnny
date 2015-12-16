package com.smc.johnny.main.application.model;

public class Order extends Entity{

	private String number;
	private OrderType orderType;
	private String ibmReference;
	private String code;
	private String shippingCode;
	private CreditCard creditCard;
	private String amountSAP;
	private String amountEPOS;
	private String email;
	private String authorizationCode;
	private String createdBy; 
	private String currency;
	private String fraudResult;
	private String poDate;
	private String poNumber;
	private String salesAreaId;
	private Boolean negativeListResult;
	private Boolean error;
	private ErrorType errorType;
	private Priority priority;
	private Boolean completed;
	private String completedDate;
	private String sentBy;
	private String sentDate;
	private Boolean emailSent;
	private String emailSentAt;
	private String deleteBy;
	private String deletedDate;
	
	public String getDeleteBy() {
		return deleteBy;
	}

	public void setDeleteBy(String deleteBy) {
		this.deleteBy = deleteBy;
	}

	public String getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(String deletedDate) {
		this.deletedDate = deletedDate;
	}

	public enum Priority {Normal, Expedite};
	public enum OrderType {Ptx, Telesales};
	public enum ErrorType {OrderCancelled,AmountMismatch,NoIBMReference,InvalidShipping,CreditCardDeclined,CaptureDenied,NegativeListMatch,AmountAbove100k,ErrorFree,CaughtInFraudCheck,InUse,CanadianSplitOrder,BusinessError};

	public Order(){
		this.negativeListResult = false;
		this.error = false;
		this.completed = false;
		this.emailSent = false;
		this.creditCard = new CreditCard();
	}
	
	public Boolean isEmailSent() {
		return emailSent;
	}
	public void setEmailSent(Boolean emailSent) {
		this.emailSent = emailSent;
	}
	public String getEmailSentAt() {
		return emailSentAt;
	}
	public void setEmailSentAt(String emailSentAt) {
		this.emailSentAt = emailSentAt;
	}
	public String getCompletedDate() {
		return completedDate;
	}
	public void setCompletedDate(String completedDate) {
		this.completedDate = completedDate;
	}
	public String getSentBy() {
		return sentBy;
	}
	public void setSentBy(String sentBy) {
		this.sentBy = sentBy;
	}
	public String getSentDate() {
		return sentDate;
	}
	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}
	public Boolean getCompleted() {
		return completed;
	}
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	public Priority getPriority() {
		return priority;
	}
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public OrderType getOrderType() {
		return orderType;
	}
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
	public String getIbmReference() {
		return ibmReference;
	}
	public void setIbmReference(String ibmReference) {
		this.ibmReference = ibmReference;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getShippingCode() {
		return shippingCode;
	}
	public void setShippingCode(String shippingCode) {
		this.shippingCode = shippingCode;
	}
	public CreditCard getCreditCard() {
		return creditCard;
	}
	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}
	public String getAmountSAP() {
		return amountSAP;
	}
	public void setAmountSAP(String amountSAP) {
		this.amountSAP = amountSAP;
	}
	public String getAmountEPOS() {
		return amountEPOS;
	}
	public void setAmountEPOS(String amountEPOS) {
		this.amountEPOS = amountEPOS;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAuthorizationCode() {
		return authorizationCode;
	}
	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getFraudResult() {
		return fraudResult;
	}
	public void setFraudResult(String fraudResult) {
		this.fraudResult = fraudResult;
	}
	public String getPoDate() {
		return poDate;
	}
	public void setPoDate(String poDate) {
		this.poDate = poDate;
	}
	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	public String getSalesAreaId() {
		return salesAreaId;
	}
	public void setSalesAreaId(String salesAreaId) {
		this.salesAreaId = salesAreaId;
	}
	public Boolean getNegativeListResult() {
		return negativeListResult;
	}
	public void setNegativeListResult(Boolean negativeListResult) {
		this.negativeListResult = negativeListResult;
	}
	public Boolean getError() {
		return error;
	}
	public void setError(Boolean error) {
		this.error = error;
	}
	public ErrorType getErrorType() {
		return errorType;
	}
	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}
	
	@Override
	public String toString() {
		
		String strOrder = this.number + "\n";
		strOrder = strOrder + this.orderType.toString() + "\n";
		strOrder = strOrder + this.ibmReference + "\n";
		strOrder = strOrder + this.code + "\n";
		strOrder = strOrder + this.shippingCode + "\n";
		strOrder = strOrder + this.creditCard.toString() + "\n";
		strOrder = strOrder + this.amountSAP + "\n";
		strOrder = strOrder + this.amountEPOS + "\n";
		strOrder = strOrder + this.email + "\n";
		strOrder = strOrder + this.authorizationCode + "\n";
		strOrder = strOrder + this.createdBy + "\n";
		strOrder = strOrder + this.currency + "\n";
		strOrder = strOrder + this.fraudResult + "\n";
		strOrder = strOrder + this.poDate + "\n";
		strOrder = strOrder + this.poNumber + "\n";
		strOrder = strOrder + this.salesAreaId + "\n";
		strOrder = strOrder + this.negativeListResult + "\n";
		strOrder = strOrder + this.error + "\n";
		strOrder = strOrder + this.errorType.toString() + "\n";
		strOrder = strOrder + this.priority.toString() + "\n";
		strOrder = strOrder + this.completed + "\n";
		strOrder = strOrder + this.completedDate + "\n";
		strOrder = strOrder + this.sentBy + "\n";
		strOrder = strOrder + this.sentDate + "\n";
		
		return strOrder;
	}
	
	@Override
	public boolean equals(Object obj) {

		if(!(obj instanceof Order))
			return false;
	
		Order order = (Order)obj;
		
		if(order.getNumber().equals(this.getNumber()))
			return true;
		
		return false;
	}
}
