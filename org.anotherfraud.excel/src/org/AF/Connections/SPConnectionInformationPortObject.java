package org.AF.Connections;


import org.knime.core.node.port.PortType;

/**
 *
 * @author Budi Yanto, KNIME.com
 */
public class SPConnectionInformationPortObject extends ConnectionInformationPortObject {

	/**
	 * @noreference This class is not intended to be referenced by clients.
	 */
	public static final class Serializer
			extends AbstractSimplePortObjectSerializer<SPConnectionInformationPortObject> {
	}

	/**
	 * No-argument constructor for framework
	 */
	public SPConnectionInformationPortObject() {
	}

	/**
	 * Type of this port.
	 */
	@SuppressWarnings("hiding")
	public static final PortType TYPE = ConnectionInformationPortObject.TYPE;

	/**
	 * Type of this optional port.
	 */
	@SuppressWarnings("hiding")
	public static final PortType TYPE_OPTIONAL = ConnectionInformationPortObject.TYPE_OPTIONAL;

	/**
	 * Creates a port object with the given connection information.
	 *
	 * @param connectionInformationPOS The spec wrapping the connection information.
	 */
	public SPConnectionInformationPortObject(final ConnectionInformationPortObjectSpec connectionInformationPOS) {
		super(connectionInformationPOS);
	}


}