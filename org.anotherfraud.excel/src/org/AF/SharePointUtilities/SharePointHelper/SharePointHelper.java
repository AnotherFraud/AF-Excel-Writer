package org.AF.SharePointUtilities.SharePointHelper;

import static java.lang.Math.toIntExact;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;
import org.knime.core.data.def.BooleanCell.BooleanCellFactory;
import org.knime.core.data.def.IntCell.IntCellFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class SharePointHelper {
	
	
	
	public static String buildCompleteSPPath(String folderpath, String sharePointName) {
		String completeSPPath =
			 "/sites/"
			+ sharePointName 
			+ "/"	
			+ folderpath.replaceAll(" ","%20");
		
		return completeSPPath;
	}
	

	
	public static String formatStringForUrl(String urlString) {		
		return urlString.replaceAll(" ","%20");
	}
	
	
	public static void createProxyRequestConfig(HttpPost post, boolean proxyEnabled, String proxyHost, int proyPort) {
		
		if(proxyEnabled)
		{
        RequestConfig.Builder reqconfigconbuilder= RequestConfig.custom();

        
        
        HttpHost proxy = new HttpHost("http",proxyHost,proyPort);
        
        reqconfigconbuilder = reqconfigconbuilder.setProxy(proxy);
        RequestConfig config = reqconfigconbuilder.build();
        
    	post.setConfig(config);		
		}

	}

	
	public static void createProxyRequestConfig(HttpGet get, boolean proxyEnabled, String proxyHost, int proyPort) {
		
		if(proxyEnabled)
		{
        RequestConfig.Builder reqconfigconbuilder= RequestConfig.custom();
        HttpHost proxy = new HttpHost("http",proxyHost,proyPort);
        reqconfigconbuilder = reqconfigconbuilder.setProxy(proxy);
        RequestConfig config = reqconfigconbuilder.build();
        
        get.setConfig(config);		
		}

	}	
	
	public static void setProxyCredentials(HttpClientBuilder clientbuilder,boolean proxyEnabled, String proxyHost, int proyPort,String proxyUser, String proxyPass) {
		

		   
		if(proxyEnabled)
		{
		BasicCredentialsProvider credentialsPovider = new BasicCredentialsProvider();
        
    	credentialsPovider.setCredentials(new AuthScope(proxyHost, proyPort), new
    		   UsernamePasswordCredentials(proxyUser, proxyPass.toCharArray()));
    		
    	clientbuilder = clientbuilder.setDefaultCredentialsProvider(credentialsPovider);
		}
		
	}	
	
	
	public static DataCell nullableIntCell(Integer i){
        if (i == null) {
            return DataType.getMissingCell();
        }
        return IntCellFactory.create(i);	
		
	}
	
	public static DataCell nullableBoolCell(Boolean b){
        if (b == null) {
            return DataType.getMissingCell();
        }
        return BooleanCellFactory.create(b);	
		
	}	
	
	public static String getStringFromNullableJsonObject(JsonObject json, String key, String secondaryKey) {
	    if (json.has(key)) {
	        JsonElement value = json.get(key);

	        if (value.isJsonObject()) {
	            return getJsonString(value.getAsJsonObject(), secondaryKey);
	        } else {
	            return getJsonString(json, key);
	        }
	    }

	    return "not found " + key;
	}


	public static String getJsonString(JsonObject json, String key) {
	    if (json.has(key)) {
	        JsonElement value = json.get(key);

	        if (value.isJsonNull()) {
	            return "";
	        } else {
	            return value.getAsString();
	        }
	    }

	    return "not found " + key;
	}



	
	public static Integer getJsonInt (JsonObject json, String key)
	{
	


        if (json.has(key))
		{  
        	
        	if(json.get(key) != JsonNull.INSTANCE )
        	{
        		
        		return json.get(key).getAsInt();
        	}
        	else
        	{
        		return null;
        	}

		}
		return null;
		
	}

	
	
	public static Double getJsonDouble (JsonObject json, String key)
	{

        if (json.has(key))
		{  
        	
        	if(json.get(key) != JsonNull.INSTANCE )
        	{
        		
        		return json.get(key).getAsDouble();
        	}
        	else
        	{
        		return null;
        	}

		}
		return null;

	}	
	
	public static Boolean intToBooleanOrNull (int value)
	{
		
		if (value == 1)
    	{
    		return true;
    	}
    	else if (value == 0)
    	{
    		return false;
    	}
    	else
    	{
    		return null;
    	}
	}
	
	public static Boolean getJsonBoolean (JsonObject json, String key)
	{
	
		


        if (json.has(key))
		{  
        	
        	if(json.get(key) != JsonNull.INSTANCE )
        	{
        		
        		return json.get(key).getAsBoolean();
        	}
        	else
        	{
        		return null;
        	}

		}
		return null;
	
	}	

	
}
