package org.AF.Connections;


import javax.swing.JComponent;

import org.apache.commons.lang.ObjectUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.AbstractSimplePortObjectSpec;

/**
 * Spec for the connection information port object.
 *
 *
 * @author Patrick Winter, KNIME AG, Zurich, Switzerland
 */
public class ConnectionInformationPortObjectSpec extends AbstractSimplePortObjectSpec {
    /**
     * @noreference This class is not intended to be referenced by clients.
     * @since 3.0
     */
    public static final class Serializer
        extends AbstractSimplePortObjectSpecSerializer<ConnectionInformationPortObjectSpec> { }

    private ConnectionInformation m_connectionInformation;

    /**
     * Create default port object spec without connection information.
     */
    public ConnectionInformationPortObjectSpec() {
        m_connectionInformation = null;
    }

    /**
     * Create specs that contain connection information.
     *
     *
     * @param connectionInformation The content of this port object
     */
    public ConnectionInformationPortObjectSpec(final ConnectionInformation connectionInformation) {
        if (connectionInformation == null) {
            throw new NullPointerException("List argument must not be null");
        }
        m_connectionInformation = connectionInformation;
    }

    /**
     * Return the connection information contained by this port object spec.
     *
     *
     * @return The content of this port object
     */
    public ConnectionInformation getConnectionInformation() {
        return m_connectionInformation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent[] getViews() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object ospec) {
        if (ospec == this) {
            return true;
        }
        if (!(ospec instanceof ConnectionInformationPortObjectSpec)) {
            return false;
        }
        ConnectionInformationPortObjectSpec oCIPOS = (ConnectionInformationPortObjectSpec)ospec;
        return ObjectUtils.equals(m_connectionInformation, oCIPOS.m_connectionInformation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return m_connectionInformation == null ? 0 : m_connectionInformation.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void save(final ModelContentWO model) {
        m_connectionInformation.save(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void load(final ModelContentRO model) throws InvalidSettingsException {
        m_connectionInformation = ConnectionInformation.load(model);
    }

}
