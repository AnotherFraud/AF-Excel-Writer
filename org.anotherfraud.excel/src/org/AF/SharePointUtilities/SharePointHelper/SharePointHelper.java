package org.AF.SharePointUtilities.SharePointHelper;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

public class SharePointHelper {
	
	
	
	public static String buildCompleteSPPath(String folderpath, String sharePointName) {
		String completeSPPath =
			 "/sites/"
			+ sharePointName 
			+ "/"	
			+ folderpath.replaceAll(" ","%20");
		
		return completeSPPath;
	}
	
	
	public static void createProxyRequestConfig(HttpPost post, boolean proxyEnabled, String proxyHost, int proyPort) {
		
		if(proxyEnabled)
		{
        RequestConfig.Builder reqconfigconbuilder= RequestConfig.custom();
        HttpHost proxy = new HttpHost(proxyHost, proyPort, "http");
        reqconfigconbuilder = reqconfigconbuilder.setProxy(proxy);
        RequestConfig config = reqconfigconbuilder.build();
        
    	post.setConfig(config);		
		}

	}

	
	
	
	public static void setProxyCredentials(HttpClientBuilder clientbuilder,boolean proxyEnabled, String proxyHost, int proyPort,String proxyUser, String proxyPass) {
		
		if(proxyEnabled)
		{
        CredentialsProvider credentialsPovider = new BasicCredentialsProvider();
    	credentialsPovider.setCredentials(new AuthScope(proxyHost, proyPort), new
    		   UsernamePasswordCredentials(proxyUser, proxyPass));
    		
    	clientbuilder = clientbuilder.setDefaultCredentialsProvider(credentialsPovider);
		}
		
	}	

	
}
