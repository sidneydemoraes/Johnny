package com.smc.johnny.main.application.model;

public class User extends Entity {
	
	private String name;
	private String email;
	private UserType userType;
	private String sapId;

	public enum UserType {APM,WebCustomerSupport,Admin,ApmSharedInbox};
	
	public User(String name,String email, UserType userType){
		this.name = name;
		this.email = email;
		this.userType = userType;
	}
	
	public String getSapId() {
		return sapId;
	}

	public void setSapId(String sapId) {
		this.sapId = sapId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof User))
			return false;
		
		User user = (User)obj;
		
		if(!(this.getEmail().equals(user.getEmail())))
			return false;
		
		return true;
	}
	
	@Override
	public String toString(){
		
		return this.email; 
	}
}
