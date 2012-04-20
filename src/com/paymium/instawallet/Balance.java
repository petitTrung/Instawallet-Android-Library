package com.paymium.instawallet;

import com.google.gson.annotations.SerializedName;

public class Balance 
{
	@SerializedName("successful")
	private boolean successful;
	
	
	@SerializedName("balance")
	private String balance;


	public Balance() 
	{
		super();
		// TODO Auto-generated constructor stub
	}


	public Balance(boolean successful, String balance) 
	{
		super();
		this.successful = successful;
		this.balance = balance;
	}


	public boolean isSuccessful() 
	{
		return successful;
	}


	public void setSuccessful(boolean successful) 
	{
		this.successful = successful;
	}


	public String getBalance() 
	{
		return balance;
	}


	public void setBalance(String balance) 
	{
		this.balance = balance;
	}
	
	
	
	
}
