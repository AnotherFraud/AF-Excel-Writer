package org.AF.SeleniumFire.FireHelper;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class FireHelper {
	
	
	public static By locatorSwitch(String locatorString, String findBy) throws IOException {
		By by;
        switch(findBy){
        case "ById":
        	by = By.id(locatorString);
            break;
        case "ByName":
        	by = By.name(locatorString);
            break;
        case "ByClassName":
        	by = By.className(locatorString);
            break;
        case "ByXPath":
        	by = By.xpath(locatorString);
            break;
        case "ByCssSelector":
        	by = By.cssSelector(locatorString);
            break;
        case "ByLinkText":
        	by = By.linkText(locatorString);
            break;
        case "ByPartialLinkText":
        	by = By.partialLinkText(locatorString);
            break;   
        case "ByTagName":
        	by = By.tagName(locatorString);
            break; 
        default:
        	throw new IOException("Unknown findBy"); 
        }
		return by;
	}
	
	
	public static WebElement locatorOrCurrentWebWelement(String searchIn, WebElement currentElement, By by, FirefoxDriver driver)
			throws IOException {
		WebElement element;
		
        switch(searchIn){
        case "with locator":
        	element = driver.findElement(by);
            break;
        case "current element":
        	element = currentElement;
            break;
        case "with locator in current element":
        	element = currentElement.findElement(by);
            break;            
        default:
        	throw new IOException("Unknown findBy"); 
        	
        }
		return element;
	}
	
}
