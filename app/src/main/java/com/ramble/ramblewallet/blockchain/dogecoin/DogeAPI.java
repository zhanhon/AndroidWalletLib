package com.ramble.ramblewallet.blockchain.dogecoin;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.ramble.ramblewallet.BuildConfig;
import com.ramble.ramblewallet.activity.MainTRXActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Java DogeAPI Client
 * 
 * @author sci4me
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public final class DogeAPI {
	private String apiUrl;

	public static void balanceOfDoge(Activity context, String address) throws JSONException {
		String url = "https://dogechain.info/api/v1/address/balance/" + address;
		org.json.JSONObject param = new org.json.JSONObject();
		param.put("address", address);
		Call call = getCallGet(url);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				if (context instanceof MainTRXActivity) {
					((MainTRXActivity) context).setTrxBalance(new BigDecimal("0"));
				}
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String string = response.body().string();
				Log.v("-=-=-=->balance：", new Gson().toJson(string));
			}
		});
	}

	@NotNull
	private static Call getCallGet(String url) {
		OkHttpClient okHttpClient = new OkHttpClient();
		Request request = new Request
				.Builder()
				.url(url)//要访问的链接
				.build();
		return okHttpClient.newCall(request);
	}

	private DogeAPI(String key) {
		StringBuilder sb = new StringBuilder();
		sb.append("https://dogeapi.com/wow/v2/?api_key=");
		sb.append(key);
		this.apiUrl = sb.toString();
	}

	private String doRequest(String request) {
		try {
			boolean flag = Boolean.valueOf(System.getProperty("jsse.enableSNIExtension"));
			StringBuilder result = new StringBuilder();
			try {
				System.setProperty("jsse.enableSNIExtension", "false");
				URL url = new URL(this.apiUrl + request);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line;
				while((line = reader.readLine()) != null)
				{
					result.append(line);
				}
				reader.close();
			}
			finally {
				System.setProperty("jsse.enableSNIExtension", String.valueOf(flag));
			}
			return result.toString();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// /wow/v2/?api_key={API_KEY}&a=get_balance
	/**
	 * Gets the current balance of this wallet
	 * 
	 * @return
	 */
	public String getBalance() {
		String result = this.doRequest("&a=get_balance");
		if(result == null)
			return null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONObject data = (JSONObject) object.get("data");
			return String.valueOf(data.get("balance"));
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// /wow/v2/?api_key={API_KEY}&a=withdraw&amount_doge={AMOUNT_DOGE}&pin={PIN}&payment_address={PAYMENT_ADDRESS}
	/**
	 * Returns the transaction ID (if the transaction is successful)
	 * 
	 * @param amount
	 * @param pin
	 * @param address
	 * @return
	 */
	public String withdraw(String amount, String pin, String address) {
		String result = this.doRequest("&a=withdraw&amount_doge=" + amount + "&pin=" + pin + "&payment_address=" + address);
		if(result == null)
			return null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONObject data = (JSONObject) object.get("data");
			return String.valueOf(data.get("txid"));
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// /wow/v2/?api_key={API_KEY}&a=get_new_address
	/**
	 * Gets a new payment address for this wallet
	 * 
	 * @return
	 */
	public String getNewAddress() {
		String result = this.doRequest("&a=get_new_address");
		if(result == null)
			return null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONObject data = (JSONObject) object.get("data");
			return String.valueOf(data.get("address"));
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// /wow/v2/?api_key={API_KEY}&a=get_new_address&address_label={ADDRESS_LABEL}
	/**
	 * Gets a new payment address for this wallet, with the specified label
	 * 
	 * @param label
	 * @return
	 */
	public String getNewAddress(String label) {
		String result = this.doRequest("&a=get_new_address&address_label=" + label);
		if(result == null)
			return null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONObject data = (JSONObject) object.get("data");
			return String.valueOf(data.get("address"));
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	// /wow/v2/?api_key={API_KEY}&a=get_my_addresses
	/**
	 * Returns all the payment addresses of this wallet
	 * 
	 * @return
	 */
	public String[] getMyAddresses() {
		String result = this.doRequest("&a=get_my_addresses");
		if(result == null)
			return null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONObject data = (JSONObject) object.get("data");
			JSONArray addresses = (JSONArray) data.get("addresses");

			String[] ret = new String[addresses.size()];
			for(int i = 0; i < ret.length; i++)
			{
				ret[i] = String.valueOf(addresses.get(i));
			}
			return ret;
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// /wow/v2/?api_key={API_KEY}&a=get_address_received&payment_address={PAYMENT_ADDRESS}
	/**
	 * Returns the amount of DOGE received on this address
	 * 
	 * @param address
	 * @return
	 */
	public String getAddressReceived(String address) {
		String result = this.doRequest("&a=get_address_received&payment_address=" + address);
		if(result == null)
			return null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONObject data = (JSONObject) object.get("data");
			return String.valueOf(data.get("received"));
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	// /wow/v2/?api_key={API_KEY}&a=get_address_by_label&address_label={ADDRESS_LABEL}
	/**
	 * Gets the addresses for a specified label
	 * 
	 * @param label
	 * @return
	 */
	public String[] getAddressByLabel(String label) {
		String result = this.doRequest("&a=get_address_by_label&address_label=" + label);
		if(result == null)
			return null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONObject data = (JSONObject) object.get("data");
			JSONArray addresses = (JSONArray) data.get("addresses");

			String[] ret = new String[addresses.size()];
			for(int i = 0; i < ret.length; i++)
			{
				ret[i] = String.valueOf(addresses.get(i));
			}
			return ret;
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// /wow/v2/?a=get_difficulty
	/**
	 * Gets the current difficulty
	 * 
	 * @return
	 */
	public String getDifficulty() {
		String result = this.doRequest("&a=get_difficulty");
		if(result == null)
			return null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONObject data = (JSONObject) object.get("data");
			return String.valueOf(data.get("difficulty"));
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// /wow/v2/?a=get_current_block
	/**
	 * Gets the current block
	 * 
	 * @return
	 */
	public String getCurrentBlock() {
		String result = this.doRequest("&a=get_current_block");
		if(result == null)
			return null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONObject data = (JSONObject) object.get("data");
			return String.valueOf(data.get("current_block"));
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	// /wow/v2/?a=get_current_price&convert_to=BTC&amount_doge=1000
	/**
	 * Gets the current price of DOGE in a specified unit
	 * 
	 * @param unit
	 * @param amount
	 * @return
	 */
	public String getCurrentPrice(Unit unit, String amount) {
		String result = this.doRequest("&a=get_current_price&convert_to=" + unit.name() + "&amount_doge=" + amount);
		if(result == null)
			return null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result);
			JSONObject data = (JSONObject) object.get("data");
			return String.valueOf(data.get("amount"));
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static DogeAPI newInstance(String apiKey) {
		return new DogeAPI(apiKey);
	}
}