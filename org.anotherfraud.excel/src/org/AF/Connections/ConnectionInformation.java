package org.AF.Connections;


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

/**
 * Contains the connection Sharepoint information for a connection.
 *
 *
 * @author Anotherfraud
 */
public class ConnectionInformation implements Serializable {

    /**
     * Serial id.
     */
    private static final long serialVersionUID = -618632550017543955L;

    private String m_protocol = null;

    private String m_host = null;

    private int m_port = -1;

    private String m_user = null;

    private String m_password = null;

    private int m_timeout = 30000;

    private boolean m_useProxy = false;
    
    private ConnectionInformation m_Proxy = null;

    private String m_token = null;
            
    
    private String m_sharePointOnlineSiteURL = null;
    
    private String m_tennantID = null;
    

    
    /**
     * Parameterless constructor.
     */
    public ConnectionInformation() {
    }

    /**
     * Constructor to load model settings from {@link ModelContentRO}
     *
     * @param model {@link ModelContentRO} to read model settings from
     * @throws InvalidSettingsException
     * @since 3.3
     */
    protected ConnectionInformation(final ModelContentRO model) throws InvalidSettingsException {
        this.setProtocol(model.getString("protocol"));
        this.setHost(model.getString("host"));
        this.setPort(model.getInt("port"));
        this.setUser(model.getString("user"));
        this.setUseProxy(model.getBoolean("useProxy"));
        this.setTennant(model.getString("tennantID"));
        this.setSharePointOnlineSiteURL(model.getString("sharePointOnlineSiteURL"));

        
        String pass = model.containsKey("xpassword") ?
            model.getPassword("xpassword", "}l?>mn0am8ty1m<+nf") : model.getString("password");
        this.setPassword(pass);
        
        
        this.setToken(model.getPassword("xtoken", "}l?>mn0am8ty1m<+nf", "")); // new option in 4.1
        this.setTimeout(model.getInt("timeout", 30000)); // new option in 2.10
       
        
        if (model.containsKey("SPProxy")) {
            setProxy(new ConnectionInformation());
            ModelContentRO proxyModelContent = model.getModelContent("ftpProxy");
            m_Proxy.setHost(proxyModelContent.getString("host"));
            m_Proxy.setPort(proxyModelContent.getInt("port"));
            m_Proxy.setUser(proxyModelContent.getString("user"));
            m_Proxy.setPassword(proxyModelContent.getPassword("xpassword", "}l?>mn0am8ty1m<+nf"));
        }
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
        model.addString("tennantID", m_tennantID);
        model.addString("sharePointOnlineSiteURL", m_sharePointOnlineSiteURL);
         
        model.addPassword("xpassword", "}l?>mn0am8ty1m<+nf", m_password);
        model.addPassword("xtoken", "}l?>mn0am8ty1m<+nf", m_token);
        model.addInt("timeout", m_timeout);
        if (m_Proxy != null) {
        	m_Proxy.save(model.addModelContent("SPProxy"));
        }
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
    public static ConnectionInformation load(final ModelContentRO model) throws InvalidSettingsException {
        return new ConnectionInformation(model);
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
     * Set the protocol.
     *
     *
     * Will convert the protocol to lower case.
     *
     * @param protocol the protocol to set
     */
    public void setProtocol(final String protocol) {
        m_protocol = protocol.toLowerCase();
//        // Change sftp and scp to ssh
//        if (m_protocol.equals("sftp")) {
//            m_protocol = m_protocol.replace("sftp", "ssh");
//        } else if (m_protocol.equals("scp")) {
//            m_protocol = m_protocol.replace("scp", "ssh");
//        }
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
        m_host = host.toLowerCase();
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

    
    /**
     * Set the setTennant.
     *
     *
     * Tennant may be null to disable user authentication.
     *
     * @param user the user to set
     */   
    public void setTennant(final String tennant) {
        m_tennantID = tennant;
    }  

    
    /**
     * Set the SharePointOnlineSiteURL.
     *
     *
     * SharePointOnlineSiteURL may be null to disable user authentication.
     *
     * @param user the user to set
     */   
    public void setSharePointOnlineSiteURL(final String sharePointOnlineSiteURL) {
        m_sharePointOnlineSiteURL = sharePointOnlineSiteURL;
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
     * Set the token. The token must be encrypted by the {@link KnimeEncryption} class.
     *
     * Token may be <code>null</code> to disable authentication via token.
     *
     * @param token the encrypted token or <code>null</code>
     * @since 4.1
     */
    public void setToken(final String token) {
        m_token = token;
    }


    /**
     * Sets the timeout for the connection.
     *
     * @param timeout the timeout in milliseconds
     */
    public void setTimeout(final int timeout) {
        m_timeout = timeout;
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

    /**
     * Get the encrypted token. Use {@link KnimeEncryption} to decrypt the token.
     *
     * @return the token password
     * @since 4.1
     */
    public String getToken() {
        return m_token;
    }



    /**
     * Returns the timeout for the connection.
     *
     * @return the timeout in milliseconds
     */
    public int getTimeout() {
        return m_timeout;
    }


    /**
     * @param proxyInfo containing the necessary information to connect to an ftp-proxy
     * @since 3.5
     */
    public void setProxy(final ConnectionInformation proxyInfo) {
        m_Proxy = proxyInfo;
    }

    /**
     * @return the ftp-proxy configured for this connection. {@code null} if non configured.
     * @since 3.5
     */
    public ConnectionInformation getProxy() {
        return m_Proxy;
    }
    
    

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(m_protocol);
        hcb.append(m_host);
        hcb.append(m_port);
        hcb.append(m_user);
        hcb.append(m_password);
        hcb.append(m_useProxy);
        hcb.append(m_sharePointOnlineSiteURL);
        hcb.append(m_tennantID);
        hcb.append(m_token);
        return hcb.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ConnectionInformation)) {
            return false;
        }
        final ConnectionInformation ci = (ConnectionInformation)obj;
        final EqualsBuilder eqBuilder = new EqualsBuilder();
        eqBuilder.append(m_protocol, ci.m_protocol);
        eqBuilder.append(m_host, ci.m_host);
        eqBuilder.append(m_port, ci.m_port);
        eqBuilder.append(m_user, ci.m_user);
        eqBuilder.append(m_password, ci.m_password);
        eqBuilder.append(m_useProxy, ci.m_useProxy);
        eqBuilder.append(m_sharePointOnlineSiteURL, ci.m_sharePointOnlineSiteURL);
        eqBuilder.append(m_tennantID, ci.m_tennantID);
        
        
        eqBuilder.append(m_token, ci.m_token);
        return eqBuilder.isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return toURI().toString();
    }


}