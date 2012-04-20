package com.paymium.instawallet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.paymium.instawallet.exception.ConnectionNotInitializedException;
import com.paymium.instawallet.payment.Payment;


public class Connection 
{
	private String backendUrl;
	
	private static Connection instance;
	
	private Gson gson;
	
	private boolean isInitialized = false;
	
	private String wallet_id;
	
	private boolean isPayment;
	
	private String address;
	private BigDecimal balance;
	
	/**
	 * Instantiates a new connection.
	 * Private constructor prevents instantiation from other classes
	 */
	private Connection() 
	{
		super();
	}

	/**
	 * Gets the single instance of Connection (Singleton)
	 *
	 * @return Singleton Connection
	 */
	public final static Connection getInstance() 
	{
		if (Connection.instance == null) 
		{
			synchronized (Connection.class) 
			{
				if (Connection.instance == null) 
				{
					Connection.instance = new Connection();
				}
			}
		}

		return Connection.instance;
	}
	
	public Boolean isInitialized() 
	{
		return (isInitialized);
	}
	
	public Connection initialize(String backendUrl, String wallet_id)
	{
		this.backendUrl = backendUrl;
		this.wallet_id = wallet_id;

		GsonBuilder gsonBuilder = new GsonBuilder();

		// TODO : Handle timezones properly
		this.gson = gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

		this.isInitialized = true;

		return (Connection.getInstance());
	}
	
	private String getMethod(String path) throws IOException, ConnectionNotInitializedException 
	{
		if (!isInitialized()) 
		{
			throw new ConnectionNotInitializedException("Connection has not been initialized");
		}
		else
		{
			if (Integer.parseInt(Build.VERSION.SDK) <= Build.VERSION_CODES.FROYO) 
			{
		        System.setProperty("http.keepAlive", "false");
		        System.out.println("Android version <= 2.2");
		        
		        HttpClient http_client = new DefaultHttpClient();
	        	
		        HttpGet http_get = new HttpGet(this.backendUrl + path);
		        http_get.setHeader("Accept", "application/json");

		        
		        HttpResponse response = http_client.execute(http_get);
				InputStream content = response.getEntity().getContent();
				
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(content));
				
				StringBuilder responseBuilder = new StringBuilder();
				String line = null;
				
				while ((line = responseReader.readLine()) != null) 
				{
					responseBuilder.append(line);
				}
				System.out.println("Return get : " + responseBuilder.toString());
				
				return (responseBuilder.toString());
		    }
			else
			{
				URL requestURL = new URL(this.backendUrl + path);
				HttpURLConnection backendConnection = (HttpURLConnection) requestURL.openConnection();

				backendConnection.setRequestProperty("Accept", "application/json");
				backendConnection.setRequestMethod(HttpVerb.GET);
				
				if (backendConnection.getResponseCode() > 400)
				{
					InputStream errorStream = backendConnection.getErrorStream();
					BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
					
					StringBuilder errorBuilder = new StringBuilder();
					String line = null;
					
					while ((line = errorReader.readLine()) != null) 
					{
						errorBuilder.append(line);
					}

					//System.out.println("Code d'erreur : " + backendConnection.getResponseCode());
					//System.out.println("Message d'erreur : " + errorBuilder.toString());
					//System.out.println("Return : " + String.valueOf(backendConnection.getResponseCode()) + " " + errorBuilder.toString());
					
					return ("Resulat : " + String.valueOf(backendConnection.getResponseCode()) + " " + errorBuilder.toString());

				}
				
				else
				{
					backendConnection.connect();
					InputStream responseStream = (InputStream) backendConnection.getInputStream();
					BufferedReader responseReader = new BufferedReader(new InputStreamReader(responseStream));

					StringBuilder responseBuilder = new StringBuilder();
					String line = null;
					
					while ((line = responseReader.readLine()) != null) 
					{
						responseBuilder.append(line);
					}
					backendConnection.disconnect();	
					
					//System.out.println("Code de succes :"+backendConnection.getResponseCode());
					//System.out.println("Message de succes :"+responseBuilder.toString());
					//System.out.println("Message de succes de la pagniation  (S'il existe) : "+backendConnection.getHeaderField("Pagination"));
					//this.header = backendConnection.getHeaderField("Pagination");			
					//System.out.println("Return " + responseBuilder.toString());
					
					return (responseBuilder.toString());
				}
			}
		}
		
	}
	
	private String postMethod(String path, JsonObject jsonData) throws IOException, ConnectionNotInitializedException 
	{
		if (!isInitialized()) 
		{
			throw new ConnectionNotInitializedException("Connection has not been initialized");
		}
		else
		{
			if (Integer.parseInt(Build.VERSION.SDK) <= Build.VERSION_CODES.FROYO) 
			{
				if (this.isPayment) 
				{
					if (jsonData == null) 
					{
						throw new IllegalArgumentException("Cannot POST with empty body");
					}
					else
					{
						String jsonString = jsonData.toString();
			        	
			        	HttpClient http_client = new DefaultHttpClient();
			        	http_client.getParams().setParameter("http.protocol.version",HttpVersion.HTTP_1_0);
			        	
						HttpPost http_post = new HttpPost(backendUrl + path);
						http_post.setHeader("Accept", "application/json");
						http_post.setHeader("Content-Type", "application/json");

						//http_post.setHeader("Content-Length", Integer.toString(jsonString.getBytes().length));
						StringEntity s = new StringEntity(jsonString);
						s.setContentType("application/json");
						http_post.setEntity(s);

				
						HttpResponse response = http_client.execute(http_post);
						InputStream content = response.getEntity().getContent();
						
						BufferedReader responseReader = new BufferedReader(new InputStreamReader(content));
						
						StringBuilder responseBuilder = new StringBuilder();
						String line = null;
						
						while ((line = responseReader.readLine()) != null) 
						{
							responseBuilder.append(line);
						}
						//System.out.println("Return post: " + responseBuilder.toString());
					
						return (responseBuilder.toString());
					}
				}
				else
				{
					HttpClient http_client = new DefaultHttpClient();
		        	http_client.getParams().setParameter("http.protocol.version",HttpVersion.HTTP_1_0);
		        	
					HttpPost http_post = new HttpPost(backendUrl + path);
					http_post.setHeader("Accept", "application/json");
			
					HttpResponse response = http_client.execute(http_post);
					InputStream content = response.getEntity().getContent();
					
					BufferedReader responseReader = new BufferedReader(new InputStreamReader(content));
					
					StringBuilder responseBuilder = new StringBuilder();
					String line = null;
					
					while ((line = responseReader.readLine()) != null) 
					{
						responseBuilder.append(line);
					}
					//System.out.println("Return post: " + responseBuilder.toString());
				
					return (responseBuilder.toString());
				}
			}
			else
			{
				URL requestURL = new URL(backendUrl + path);
				HttpURLConnection backendConnection = (HttpURLConnection) requestURL.openConnection();

				backendConnection.setRequestProperty("Accept", "application/json");
				backendConnection.setRequestMethod(HttpVerb.POST);

				String jsonString = null;
				
				if(this.isPayment)
				{
					if (jsonData == null) 
					{
						throw new IllegalArgumentException("Cannot POST with empty body");
					}
					else
					{
						jsonString = jsonData.toString();
						
						backendConnection.setDoOutput(true);
						backendConnection.setRequestProperty("Content-Type", "application/json");
						backendConnection.setRequestProperty("Content-Length", Integer.toString(jsonString.getBytes().length));

					}
				}
				
				// Send request
				DataOutputStream dataOutputStream = new DataOutputStream(backendConnection.getOutputStream());
				dataOutputStream.writeBytes(jsonString);
				dataOutputStream.flush();
				dataOutputStream.close();
				
				if (backendConnection.getResponseCode() > 400)
				{
					InputStream errorStream = backendConnection.getErrorStream();
					BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
					
					StringBuilder errorBuilder = new StringBuilder();
					String line = null;
					
					while ((line = errorReader.readLine()) != null) 
					{
						errorBuilder.append(line);
					}

					//System.out.println("Code d'erreur : " + backendConnection.getResponseCode());
					//System.out.println("Message d'erreur : " + errorBuilder.toString());
					//System.out.println("Return : " + String.valueOf(backendConnection.getResponseCode()) + " " + errorBuilder.toString());
					
					return ("Resulat : " + String.valueOf(backendConnection.getResponseCode()) + " " + errorBuilder.toString());

				}
				
				else
				{
					backendConnection.connect();
					InputStream responseStream = (InputStream) backendConnection.getInputStream();
					BufferedReader responseReader = new BufferedReader(new InputStreamReader(responseStream));

					StringBuilder responseBuilder = new StringBuilder();
					String line = null;
					
					while ((line = responseReader.readLine()) != null) 
					{
						responseBuilder.append(line);
					}
					backendConnection.disconnect();	
					
					//System.out.println("Code de succes :"+backendConnection.getResponseCode());
					//System.out.println("Message de succes :"+responseBuilder.toString());
					//System.out.println("Message de succes de la pagniation  (S'il existe) : "+backendConnection.getHeaderField("Pagination"));
					//this.header = backendConnection.getHeaderField("Pagination");			
					//System.out.println("Return " + responseBuilder.toString());
					
					return (responseBuilder.toString());
				}
			}
		}
	}

	public boolean isPayment() 
	{
		return isPayment;
	}

	public void setPayment(boolean isPayment) 
	{
		this.isPayment = isPayment;
	}
	
	
	
	public String getWallet_id() 
	{
		return wallet_id;
	}

	public void setWallet_id(String wallet_id) 
	{
		this.wallet_id = wallet_id;
	}

	public NewWallet postNewWallet() throws IOException, ConnectionNotInitializedException 
	{
		Pattern pattern;
		Matcher matcher;
		boolean successful,id;
		
		String response = this.postMethod("/new_wallet",null);
		
		pattern = Pattern.compile("true");
		matcher = pattern.matcher(response);
		successful = matcher.find();
		
		pattern = Pattern.compile("wallet_id");
		matcher = pattern.matcher(response);
		id = matcher.find();
		
		if(successful && id)
		{
			System.out.println("SUCCESSFUL !!");
			
			NewWallet a = gson.fromJson(response, NewWallet.class);
			this.setWallet_id(a.getWallet_id());
			
			return a;
		}
		else
		{
			System.out.println("FAIL !!");
			
			return null;
		}
	}
	
	public Address getAddressJson() throws IOException, ConnectionNotInitializedException 
	{
		Pattern pattern;
		Matcher matcher;
		boolean successful,address;
		
		String response = this.getMethod("/w/"+this.getWallet_id()+"/address");
		
		pattern = Pattern.compile("true");
		matcher = pattern.matcher(response);
		successful = matcher.find();
		
		pattern = Pattern.compile("address");
		matcher = pattern.matcher(response);
		address = matcher.find();
		
		if(successful && address)
		{
			System.out.println("SUCCESSFUL !!");
			
			Address a = gson.fromJson(response, Address.class);
			
			this.setAddress(a.getAddress());
			
			return a;
		}
		else
		{
			System.out.println("FAIL !!");
			
			return null;
		}
	}
	
	
	
	public Balance getBalanceJson() throws IOException, ConnectionNotInitializedException 
	{
		Pattern pattern;
		Matcher matcher;
		boolean successful,balance;
		
		String response = this.getMethod("/w/"+this.getWallet_id()+"/balance");
		
		pattern = Pattern.compile("true");
		matcher = pattern.matcher(response);
		successful = matcher.find();
		
		pattern = Pattern.compile("balance");
		matcher = pattern.matcher(response);
		balance = matcher.find();
		
		if(successful && balance)
		{
			System.out.println("SUCCESSFUL !!");
			
			Balance a = gson.fromJson(response, Balance.class);
			
			this.setBalance(a.getBalance());
			
			return a;
		}
		else
		{
			System.out.println("FAIL !!");
			
			return null;
		}
	}
	
	public Payment postPayment() throws IOException, ConnectionNotInitializedException
	{
		return null;
	}

	public void setAddress(String address) 
	{
		this.address = address;
	}

	public void setBalance(BigDecimal balance) 
	{
		this.balance = balance;
	}

	public String getAddress()
	{
		return this.address;
	}
	
	public BigDecimal getBalance()
	{
		return this.balance;
	}
	
	
}
