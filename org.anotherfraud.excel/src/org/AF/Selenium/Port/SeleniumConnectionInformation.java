package org.AF.Selenium.Port;


import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.NodeLogger;
import org.knime.core.util.KnimeEncryption;
import org.openqa.selenium.support.ui.WebDriverWait;



/**
 * Contains the Selenium firefox driver information.
 *
 *
 * @author Anotherfraud
 */
public class SeleniumConnectionInformation implements Serializable {

    /**
     * Serial id.
     */
    private static final long serialVersionUID = -618632560017543955L;


    
    private String m_webdriverHandlerKey;
    
    private String m_screenShotPath = null;
    private int m_screenShotCounter = 0;
    private String m_downloadPath = null;
    private WebDriverWait m_driverWait = null;
    
    private int m_downloadWaitSeconds = 90;
    private int m_pageWaitSeconds = 90;

    private String m_protocol = null;

    private String m_host = null;

    private int m_port = -1;

    private String m_user = null;

    private String m_password = null;

    private boolean m_useProxy = false;

   
    

    
    /**
     * Parameterless constructor.
     */
    public SeleniumConnectionInformation() {
    }

    /**
     * Constructor to load model settings from {@link ModelContentRO}
     *
     * @param model {@link ModelContentRO} to read model settings from
     * @throws InvalidSettingsException
     * @since 3.3
     */
    protected SeleniumConnectionInformation(final ModelContentRO model) throws InvalidSettingsException {
      

    	
    	this.setHost(model.getString("host"));
        this.setPort(model.getInt("port"));
        
        
        
        this.setUser(model.getString("user"));
        this.setUseProxy(model.getBoolean("useProxy"));
        this.setDownloadPath(model.getString("downloadPath"));
        this.setScreenShotPath(model.getString("screenShotPath"));
        this.setPort(model.getInt("sCounter"));
        this.setDownloadWaitSeconds(model.getInt("downloadWaitSeconds"));
        this.setPageWaitSeconds(model.getInt("pageWaitSeconds"));
        this.setWebdriverHandlerKey(model.getString("driverHandle"));
        
        String pass = model.containsKey("xpassword") ?
            model.getPassword("xpassword", "}l?>mn0am8ty1m<+nf") : model.getString("password");
        this.setPassword(pass);

        this.setPageWaitSeconds(model.getInt("timeout", 90)); // new option in 2.10
        
        
  
       
    }



	/**
     * Save the connection information in a model content object.
     *
     *
     * @param model The model to save in
     */
    public void save(final ModelContentWO model) {
    	


        
        
        model.addString("protocol", m_protocol);
        model.addString("host", m_host);
        model.addInt("port", m_port);
        model.addString("user", m_user);      
        model.addBoolean("useProxy", m_useProxy);     
        model.addPassword("xpassword", "}l?>mn0am8ty1m<+nf", m_password);
        model.addInt("pageWaitSeconds", m_pageWaitSeconds);
        model.addString("driverHandle", m_webdriverHandlerKey);

        
        
        model.addString("downloadPath", m_downloadPath); 
        model.addString("screenShotPath", m_screenShotPath); 
        model.addInt("sCounter", m_screenShotCounter); 
        model.addInt("pageWaitSeconds", m_pageWaitSeconds);
        model.addInt("downloadWaitSeconds", m_downloadWaitSeconds);
        

        
    }

    /**
     * Create a connection information object loaded from the content object.
     *
     *
     * @param model The model to read from
     * @return The created <code>ConnectionInformation</code> object
     * @throws InvalidSettingsException If the model contains invalid
     *             information.
     * @noreference Not to be called by client
     */
    public static SeleniumConnectionInformation load(final ModelContentRO model) throws InvalidSettingsException {
        return new SeleniumConnectionInformation(model);
    }

   
    

    /**
     * Create the corresponding uri to this connection information.
     *
     *
     * @return URI to this connection information
     */
    public URI toURI() {
        URI uri = null;
        try {
            uri = new URI(m_protocol, m_user, m_host, m_port, null, null, null);
        } catch (final URISyntaxException e) {
            // Should not happen
            NodeLogger.getLogger(getClass()).coding(e.getMessage(), e);
        }
        return uri;
    }


    /**
     * Set the host.
     *
     *
     * Will convert the host to lower case.
     *
     * @param host the host to set
     */
    public void setHost(final String host) {
        m_host = host;
    }

    /**
     * Set the port.
     *
     *
     * @param port the port to set
     */
    public void setPort(final int port) {
        m_port = port;
    }

    /**
     * Set the user.
     *
     *
     * User may be null to disable user authentication.
     *
     * @param user the user to set
     */
    public void setUser(final String user) {
        m_user = user;
    }
    

    public void setWebdriverHandlerKey(String key) {
		m_webdriverHandlerKey = key;
		
	}
    

    
    
    
    /**
     * Set the useproxy.
     *
     *
     * useproxy may be true to enable proxy usage
     *
     * @param user the user to set
     */   
    public void setUseProxy(final boolean useProxy) {
        m_useProxy = useProxy;
    }


    
    public void setScreenShotPath (final String screenShotPath) {
    	m_screenShotPath = screenShotPath;
    }     
    
    public void setScreenShotCounter (final int newCounter) {
    	m_screenShotCounter = newCounter;
    }       
    
    public void increaseScreenShotCounter () {
    	m_screenShotCounter++;
    }   
    
    public void setDownloadPath(final String downloadPath) {
    	m_downloadPath = downloadPath;
    }  
    public void setWebDriverWait(final WebDriverWait driverWait) {
    	m_driverWait = driverWait;
    }  
    
    public void setDownloadWaitSeconds(final int downloadWaitSeconds) {
    	m_downloadWaitSeconds = downloadWaitSeconds;
    }  
   


   
    
 
    
    /**
     * Set the password. The password must be encrypted by the {@link KnimeEncryption} class.
     *
     * Password may be <code>null</code> to disable authentication via password.
     *
     * @param password the encrypted password
     */
    public void setPassword(final String password) {
        m_password = password;
    }



    /**
     * Sets the timeout for the connection.
     *
     * @param timeout the timeout in milliseconds
     */
    public void setPageWaitSeconds(final int timeout) {
    	m_pageWaitSeconds = timeout;
    }


    /**
     * Get the protocol.
     *
     *
     * @return the protocol
     */
    public String getProtocol() {
        return m_protocol;
    }

    /**
     * Get the host.
     *
     *
     * @return the host
     */
    public String getHost() {
        return m_host;
    }


    
    /**
     * Get the port.
     *
     *
     * @return the port
     */
    public int getPort() {
        return m_port;
    }

    /**
     * Get the user.
     *
     *
     * @return the user
     */
    public String getUser() {
        return m_user;
    }

    /**
     * Get the encrypted password. Use {@link KnimeEncryption} to decrypt the password.
     *
     * @return the encrypted password
     */
    public String getPassword() {
        return m_password;
    }



    public String getScreenShotPath () {
    	return m_screenShotPath;
    }    
    
    public int getScreenShotCounter () {
    	return m_screenShotCounter;
    }  
    
    public int getScreenShotCounterWithIncrease () {
    	m_screenShotCounter++;
    	return m_screenShotCounter;
    }  
    
    public String getWebdriverHandlerKey() {
		return m_webdriverHandlerKey;
    }
    

    public String getDownloadPath() {
    	return m_downloadPath;
    }  
    public WebDriverWait getWebDriverWait() {
    	return m_driverWait;
    }  
    

    public int getDownloadWaitSeconds() {
    	return m_downloadWaitSeconds;
    }  
    
    /**
     * Returns the timeout for the connection.
     *
     * @return the timeout in milliseconds
     */
    public int getPageWaitSeconds() {
        return m_pageWaitSeconds;
    }
    
    
    /**
     * Returns UseProxy.
     *
     * @return UseProxy
     */ 
    public boolean getUseProxy() {
        return m_useProxy;
    }


    

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(m_webdriverHandlerKey);
        hcb.append(m_protocol);
        hcb.append(m_host);
        hcb.append(m_port);
        hcb.append(m_user);
        hcb.append(m_password);
        hcb.append(m_useProxy);
        return hcb.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SeleniumConnectionInformation)) {
            return false;
        }
        final SeleniumConnectionInformation ci = (SeleniumConnectionInformation)obj;
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(m_webdriverHandlerKey, ci.m_webdriverHandlerKey);
        eqBuilder.append(m_protocol, ci.m_protocol);
        eqBuilder.append(m_host, ci.m_host);
        eqBuilder.append(m_port, ci.m_port);
        eqBuilder.append(m_user, ci.m_user);
        eqBuilder.append(m_password, ci.m_password);
        eqBuilder.append(m_useProxy, ci.m_useProxy);
        return eqBuilder.isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return toURI().toString();
    }

}
