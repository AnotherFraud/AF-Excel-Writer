package org.AF.Selenium.Port;


import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.AbstractSimplePortObject;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.util.ViewUtils;

/**
 * Port object containing connection information.
 *
 *
 * @author Patrick Winter, KNIME AG, Zurich, Switzerland
 */
public class SeleniumConnectionInformationPortObject extends AbstractSimplePortObject {
    /**
     * @noreference This class is not intended to be referenced by clients.
     * @since 3.0
     */
    public static final class Serializer extends AbstractSimplePortObjectSerializer<SeleniumConnectionInformationPortObject> {}

    private SeleniumConnectionInformationPortObjectSpec m_connectionInformationPOS;

    /**
     * Type of this port.
     */
    public static final PortType TYPE =
        PortTypeRegistry.getInstance().getPortType(SeleniumConnectionInformationPortObject.class);

    /**
     * Type of this optional port.
     * @since 3.0
     */
    public static final PortType TYPE_OPTIONAL =
        PortTypeRegistry.getInstance().getPortType(SeleniumConnectionInformationPortObject.class, true);

    /**
     * Should only be used by the framework.
     */
    public SeleniumConnectionInformationPortObject() {
        // Used by framework
    }

    /**
     * Creates a port object with the given connection information.
     *
     * @param connectionInformationPOS The spec wrapping the connection
     *            information.
     */
    public SeleniumConnectionInformationPortObject(final SeleniumConnectionInformationPortObjectSpec connectionInformationPOS) {
        if (connectionInformationPOS == null) {
            throw new NullPointerException("Argument must not be null");
        }
        final SeleniumConnectionInformation connInfo = connectionInformationPOS.getConnectionInformation();
        if (connInfo == null) {
            throw new NullPointerException("Connection information must be set (is null)");
        }
        m_connectionInformationPOS = connectionInformationPOS;
    }

    /**
     * Returns the connection information contained by this port object.
     *
     *
     * @return The content of this port object
     */
    public SeleniumConnectionInformation getConnectionInformation() {
        return m_connectionInformationPOS.getConnectionInformation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSummary() {
        return m_connectionInformationPOS.getConnectionInformation().toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent[] getViews() {
        final JPanel f = ViewUtils.getInFlowLayout(new JLabel(getSummary()));
        f.setName("Connection");
        return new JComponent[]{f};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortObjectSpec getSpec() {
        return m_connectionInformationPOS;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    @Override
    protected void save(final ModelContentWO model, final ExecutionMonitor exec) throws CanceledExecutionException {
        // nothing to save; all done in spec, which is saved separately
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unused")
    @Override
    protected void load(final ModelContentRO model, final PortObjectSpec spec, final ExecutionMonitor exec)
            throws InvalidSettingsException, CanceledExecutionException {
        m_connectionInformationPOS = (SeleniumConnectionInformationPortObjectSpec)spec;
    }

}
