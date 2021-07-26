package org.AF.Selenium.Port;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebdriverHandler {
    private static final ConcurrentMap<String, WebdriverHandler> WebdriverMap = new ConcurrentHashMap<>();

    private final String key;
    private WebdriverHandler(String key) { this.key = key; }
    private FirefoxDriver driver;
    private WebElement webElement;
    
    
    
    public static WebdriverHandler getInstance(final String key) {
        return WebdriverMap.computeIfAbsent(key, WebdriverHandler::new);
    }
    
    public void setDriver(FirefoxDriver driver)
    {
    	this.driver = driver;
    }
    
    public void setDriver(WebElement webElement)
    {
    	this.webElement = webElement;
    }
    
    
    public FirefoxDriver getDriver()
    {
    	return this.driver;
    }   
    
    public WebElement getWebElement()
    {
    	return this.webElement;
    }   
    

    public String getKey()
    {
    	return this.key;
    }    

    public void removeInstance()
    {
    	if (this.getDriver() != null)
    	{
	    	try {
	    		this.getDriver().quit();
	    	}
	    	catch (Exception e)
	    	{
	    	}
	    }
    	
    	WebdriverMap.remove(this.key);
    }    
       
    
}