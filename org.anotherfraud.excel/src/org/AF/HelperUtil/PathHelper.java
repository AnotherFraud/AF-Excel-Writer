package org.AF.HelperUtil;

import java.nio.file.Path;

import org.knime.core.node.InvalidSettingsException;
import org.knime.filehandling.core.connections.DefaultFSConnectionFactory;
import org.knime.filehandling.core.connections.FSCategory;
import org.knime.filehandling.core.connections.FSConnection;
import org.knime.filehandling.core.connections.FSLocation;
import org.knime.filehandling.core.connections.FSPath;
import org.knime.filehandling.core.connections.RelativeTo;
import org.knime.filehandling.core.defaultnodesettings.FileSystemChoice;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.ValidationUtils;
import org.knime.filehandling.core.defaultnodesettings.FileSystemChoice.Choice;

public class PathHelper {

    public static final FSConnection retrieveFSConnection(final SettingsModelFileChooser2 settings, final int timeoutInMillis) {

        final FileSystemChoice choice = settings.getFileSystemChoice();
        switch (choice.getType()) {
            case LOCAL_FS:
                return DefaultFSConnectionFactory.createLocalFSConnection();
            case CUSTOM_URL_FS:
                return DefaultFSConnectionFactory.createCustomURLConnection(settings.getPathOrURL(), timeoutInMillis);
            case KNIME_MOUNTPOINT:
                final var mountID = settings.getKnimeMountpointFileSystem();
                return DefaultFSConnectionFactory.createMountpointConnection(mountID);
            case KNIME_FS:
                final RelativeTo type = RelativeTo.fromSettingsValue(settings.getKNIMEFileSystem());
                return DefaultFSConnectionFactory.createRelativeToConnection(type);
            default:
                throw new IllegalArgumentException("Unsupported file system choice: " + choice.getType());
        }
    }

private static FSLocation toCustomUrlFSLocation(final String url, final int timeoutInMillis) {
    return new FSLocation(FSCategory.CUSTOM_URL, Integer.toString(timeoutInMillis), url);
}

public Path getPathFromSettings(SettingsModelFileChooser2 m_settings, FSConnection fs, int defaulttimeoutInSeconds) throws InvalidSettingsException  {
	
	
	
    if (m_settings.getPathOrURL() == null || m_settings.getPathOrURL().isEmpty()) {
        throw new InvalidSettingsException("No path specified");
    }
    
    

    final FSPath pathOrUrl;
    final Choice fileSystemChoice = m_settings.getFileSystemChoice().getType();
    switch (fileSystemChoice) {
        case CONNECTED_FS:
            return fs.getFileSystem().getPath(m_settings.getPathOrURL());
        case CUSTOM_URL_FS:
            return fs.getFileSystem().getPath(toCustomUrlFSLocation(m_settings.getPathOrURL(), defaulttimeoutInSeconds*1000));
        case KNIME_FS:
            pathOrUrl = fs.getFileSystem().getPath(m_settings.getPathOrURL());
            ValidationUtils.validateKnimeFSPath(pathOrUrl);
            return pathOrUrl;
        case KNIME_MOUNTPOINT:
            pathOrUrl = fs.getFileSystem().getPath(m_settings.getPathOrURL());
            if (!pathOrUrl.isAbsolute()) {
                throw new InvalidSettingsException("The path must be absolute, i.e. it must start with '/'.");
            }
            return pathOrUrl;
        case LOCAL_FS:
            ValidationUtils.validateLocalFsAccess();
            pathOrUrl = fs.getFileSystem().getPath(m_settings.getPathOrURL());
            if (!pathOrUrl.isAbsolute()) {
                throw new InvalidSettingsException("The path must be absolute.");
            }
            return pathOrUrl;
        default:
            final String errMsg =
                String.format("Unknown choice enum '%s', make sure the switch covers all cases!", fileSystemChoice);
            throw new RuntimeException(errMsg);
    }
}

}
