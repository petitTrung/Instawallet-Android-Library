package com.paymium.instawallet.payment;

import com.google.gson.annotations.SerializedName;

public class GreenAddressPayment 
{
	@SerializedName("address")
	private String address;
	
	@SerializedName("amount")
	private Float amount;
	
	@SerializedName("use_green_address")
	private boolean use_green_address;

	
	
	public GreenAddressPayment() 
	{
		super();
		// TODO Auto-generated constructor stub
	}



	public GreenAddressPayment(String address, Float amount, boolean use_green_address) 
	{
		super();
		this.address = address;
		this.amount = amount;
		this.use_green_address = use_green_address;
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



	public boolean isUse_green_address() 
	{
		return use_green_address;
	}



	public void setUse_green_address(boolean use_green_address) 
	{
		this.use_green_address = use_green_address;
	}
	
	
	
	
}
