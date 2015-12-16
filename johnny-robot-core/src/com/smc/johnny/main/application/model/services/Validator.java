package com.smc.johnny.main.application.model.services;

/**
 * @deprecated
 */
public class Validator {
	
	public static Boolean isNull(Object obj){
		if(obj == null)
			return true;
		return false;
	}

	public static Boolean isNotNull(Object obj){
		if(obj != null)
			return true;
		return false;
	}
	
	public static Boolean isNotValidCreditCardNumber(Integer number){

		//TODO regex of credit card number
		//# Visa: ^4[0-9]{12}(?:[0-9]{3})?$ 
		//# MasterCard: ^5[1-5][0-9]{14}$ 
		//# American Express: ^3[47][0-9]{13}$ 
		return true;
	}
}
