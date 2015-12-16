package com.smc.johnny.main.application.model;

import com.smc.johnny.main.application.model.services.Validator;


@SuppressWarnings("deprecation")
public class CreditCard {

	private String number;
	private String month;
	private String year;
	private String holder;
	
	public enum CardType {AMEX,VISA,MC};
	
	public CardType getCardType(){
		if(number.startsWith("3"))
			return CardType.AMEX;
		else if(number.startsWith("4"))
			return CardType.VISA;
		else
			return CardType.MC;
	}
	
	public String getLastName(){
		
		String[] name = holder.split("\\s+", 2);
		
		return name[1];
	}
	
	public String getFirstName(){
	
		String[] name = holder.split("\\s+", 2);
		
		return name[0];
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	/**
	 * @deprecated
	 */
	public Boolean validate(){
		
		if(Validator.isNull(number))
			return false;
		
		if(Validator.isNotValidCreditCardNumber(Integer.parseInt(number)))
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {

		String strCreditCard = this.number + "\n";
		strCreditCard = strCreditCard + this.month + "\n";
		strCreditCard = strCreditCard + this.year + "\n";
		strCreditCard = strCreditCard + this.holder + "\n";
		
		return strCreditCard;
	}
}
