package com.paymium.instawallet;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.paymium.instawallet.exception.ConnectionNotInitializedException;
import com.paymium.instawallet.json.NewWallet;
import com.paymium.instawallet.wallet.Connection;
import com.paymium.instawallet.wallet.Wallet;
import com.paymium.instawallet.wallet.WalletsList;

public class InstawalletLibraryActivity extends Activity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView tv = (TextView) findViewById(R.id.textView1);
        
        Wallet wallet = new Wallet();
        WalletsList walletsList = new WalletsList();
        
        Connection connection = Connection.getInstance().initialize();
        
        try 
        {
        	for (int i = 0 ; i < 5 ; i++)
        	{
        		NewWallet newWallet = connection.createNewWallet();
    			
    			wallet.setWallet_id(newWallet.getWallet_id());
    			wallet.setWallet_address(connection.getAddressJson(wallet.getWallet_id()).getAddress());
    			wallet.setWallet_balance(connection.getBalanceJson(wallet.getWallet_id()).getBalance());
    			
    			System.out.println("Wallet " + (i+1) + " : " + wallet.toString());
    			
    			walletsList.add(wallet);
    			
        	}
        	
        	tv.setText(walletsList.toString());
        	
        	wallet.setWallet_id("nJnbGhxgHCkT7dYt1SPaij4R2gMQlg");
			wallet.setWallet_address(connection.getAddressJson(wallet.getWallet_id()).getAddress());
			wallet.setWallet_balance(connection.getBalanceJson(wallet.getWallet_id()).getBalance());
			
			System.out.println(wallet.toString());
			
		} 
        catch (IOException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        catch (ConnectionNotInitializedException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}