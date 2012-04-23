package com.paymium.instawallet.payment;

import com.google.gson.annotations.SerializedName;

public class NormalPayment 
{
	@SerializedName("address")
	private String address;
	
	@SerializedName("amount")
	private Float amount;

	public NormalPayment() 
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public NormalPayment(String address, Float amount) 
	{
		super();
		this.address = address;
		this.amount = amount;
	}

	public String getAddress() 
	{
		return address;
	}

	public void setAddress(String address) 
	{
		this.address = address;
	}

	public Float getAmount() 
	{
		return amount;
	}

	public void setAmount(Float amount) 
	{
		this.amount = amount;
	}
	
	
	
	
}
