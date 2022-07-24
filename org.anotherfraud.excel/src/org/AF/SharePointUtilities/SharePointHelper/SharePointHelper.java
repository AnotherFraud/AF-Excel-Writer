package org.AF.SharePointUtilities.SharePointHelper;

import static java.lang.Math.toIntExact;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

import org.json.JSONObject;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;
import org.knime.core.data.def.BooleanCell.BooleanCellFactory;
import org.knime.core.data.def.IntCell.IntCellFactory;

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
	
	public static String getJsonString (JSONObject json, String key)
	{

        if (json.has(key))
		{  
    	Object value = json.get(key);
        String dataType = value.getClass().getSimpleName();
            
	        if (dataType.equalsIgnoreCase("Integer")) {	
	        	return String.valueOf((int) value);
	        } else if (dataType.equalsIgnoreCase("Long")) {
	        	
	        	return String.valueOf((long) value);
	        	
	        } else if (dataType.equalsIgnoreCase("Float")) {
	        	return String.valueOf((float) value);
	        } else if (dataType.equalsIgnoreCase("Double")) {
	        	return String.valueOf((double) value);
	
	        } else if (dataType.equalsIgnoreCase("Boolean")) {
	        	return String.valueOf((boolean) value);
	        	
	        } else if (dataType.equalsIgnoreCase("String")) {
	        	return (String) value;
	        }	
	        else if (dataType.equalsIgnoreCase("Null")) {
	        	return "";
	        }
	        else
	        {
	        	return new String("not defined type " + dataType);
	        }
		}
		return new String("not found " + key);
		
	}

	
	public static String getStringFromNullableJsonObject (JSONObject json, String key, String secondaryKey)
	{

        if (json.has(key))
		{  
    	Object value = json.get(key);
        String dataType = value.getClass().getSimpleName();
            

	        if(dataType.equalsIgnoreCase("JSONObject")) {
	        	return getJsonString(json.getJSONObject(key), secondaryKey);
			
	        }
	        else
	        {
	        	return getJsonString(json, key);
	        }
	        
		}
		return new String("not found " + key);
		
	}
	
	
	public static Integer getJsonInt (JSONObject json, String key)
	{
	
		



        if (json.has(key))
		{  
    	Object value = json.get(key);
        String dataType = value.getClass().getSimpleName();
            
	        if (dataType.equalsIgnoreCase("Integer")) {	
	        	return (int) value;
	        	
	        } else if (dataType.equalsIgnoreCase("Long")) {
	        	
	        	return toIntExact((long) value);
	        	
	        } else if (dataType.equalsIgnoreCase("Float")) {
	        	return Math.round((float) value);
	        	
	        	
	        } else if (dataType.equalsIgnoreCase("Double")) {
	        	return (int) Math.round((double) value);
	
	        } else if (dataType.equalsIgnoreCase("Boolean")) {
	        	return (boolean) value ? 1 : 0; 
	        	
	        } else if (dataType.equalsIgnoreCase("String")) {
	        	if (NumberUtils.isNumber(((String) value)))
	        	{
	        		return NumberUtils.toInt((String) value);
	        	}
	        	else
	        	{
	        		return null;
	        	}
	        }					
		}
		return null;
		
	}

	
	
	public static Double getJsonDouble (JSONObject json, String key)
	{
	
		



        if (json.has(key))
		{  
    	Object value = json.get(key);
        String dataType = value.getClass().getSimpleName();
	        if (dataType.equalsIgnoreCase("Integer")) {	
	        	return Double.valueOf((int) value);
	        	
	        } else if (dataType.equalsIgnoreCase("Long")) {
	        	
	        	return Double.valueOf((long) value);
	       
	        	
	        } else if (dataType.equalsIgnoreCase("Float")) {
	        	return Double.valueOf((float) value);
	        	
	        } else if (dataType.equalsIgnoreCase("Double")) {
	        	return (double) value;
	
	        } else if (dataType.equalsIgnoreCase("Boolean")) {
	        	return Double.valueOf((boolean) value ? 1 : 0); 
	        	
	        } else if (dataType.equalsIgnoreCase("String")) {
	        	if (NumberUtils.isNumber(((String) value)))
	        	{
	        		return NumberUtils.toDouble((String) value);
	        	}
	        	else
	        	{
	        		return null;
	        	}
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
	
	public static Boolean getJsonBoolean (JSONObject json, String key)
	{
	
		


        if (json.has(key))
		{ 
        Object value = json.get(key);
        String dataType = value.getClass().getSimpleName();
	        if (dataType.equalsIgnoreCase("Integer")) {	
	        	return intToBooleanOrNull((int) value);
	        	
	        } else if (dataType.equalsIgnoreCase("Long")) {
	        	return intToBooleanOrNull(toIntExact((long) value));
	        	
	        } else if (dataType.equalsIgnoreCase("Float")) {
	        	float f = (float) value;
	        	
	        	if(f % 1 == 0)
	        	{
	        		return intToBooleanOrNull(Math.round(f));
	        	}
	        	else
	        	{
	        		return null;
	        	}

	        } else if (dataType.equalsIgnoreCase("Double")) {
	        	
	        	double f = (double) value;
	        	
	        	if(f % 1 == 0)
	        	{
	        		return intToBooleanOrNull((int) Math.round(f));
	        	}
	        	else
	        	{
	        		return null;
	        	}	        	
	        	

	        } else if (dataType.equalsIgnoreCase("Boolean")) {
	        	return (boolean) value; 
	        	
	        } else if (dataType.equalsIgnoreCase("String")) {
	        	
	        	return BooleanUtils.toBooleanObject((String) value);
	        		
	        	
	        }					
		}
		return null;
	
	}	

	
}
