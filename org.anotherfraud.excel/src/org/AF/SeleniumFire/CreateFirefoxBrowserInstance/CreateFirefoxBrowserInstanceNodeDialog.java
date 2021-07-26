package org.AF.SeleniumFire.CreateFirefoxBrowserInstance;

import java.util.Arrays;

import javax.swing.JFileChooser;

import org.AF.SeleniumFire.Utilities.DialogComponentFirefoxPreferences;
import org.AF.SeleniumFire.Utilities.SettingsModelFirefoxSettings;
import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentAuthentication;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.FlowVariable.Type;
import org.knime.filehandling.core.defaultnodesettings.DialogComponentFileChooser2;
import org.knime.filehandling.core.defaultnodesettings.SettingsModelFileChooser2;

/**
 * This is an example implementation of the node dialog of the
 * "CreateFirefoxBrowserInstance" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author Another Fraud
 */
public class CreateFirefoxBrowserInstanceNodeDialog extends DefaultNodeSettingsPane {


   	
    protected CreateFirefoxBrowserInstanceNodeDialog() {
        super();
        
       final SettingsModelAuthentication proxyAuth = CreateFirefoxBrowserInstanceNodeModel.createProxySettingsModel();
        

       	final SettingsModelString useProxyModel = CreateFirefoxBrowserInstanceNodeModel.createUseProxySettingsModel();
       	final SettingsModelIntegerBounded proxyPort = CreateFirefoxBrowserInstanceNodeModel.createProxyPortIndexModel();
       	final SettingsModelString proxyHost = CreateFirefoxBrowserInstanceNodeModel.createProxyHostSettingsModel();
 
        final SettingsModelFileChooser2 downloadPathModel2 = CreateFirefoxBrowserInstanceNodeModel.createDownloadPathSettingsModel();
        final SettingsModelFileChooser2 screenshotPathModel2 = CreateFirefoxBrowserInstanceNodeModel.createScreenshotPathSettingsModel();
        final SettingsModelFileChooser2 firefoxPathModel2 = CreateFirefoxBrowserInstanceNodeModel.createFirefoxPathSettingsModel();
        final SettingsModelBoolean headlessMode = CreateFirefoxBrowserInstanceNodeModel.createHeadlessModeSettingsModel();  
        final SettingsModelFirefoxSettings foxModel = CreateFirefoxBrowserInstanceNodeModel.createFirefoxSettingsModel();  

    
       
       	
       	
        //listener check selection for password usage
       	useProxyModel.addChangeListener(e -> {
            if (useProxyModel.getStringValue().equals("Use Proxy")) {
            	proxyAuth.setEnabled(true);
            	proxyPort.setEnabled(true);
            	proxyHost.setEnabled(true);
            	
            }
            else  {
            	proxyAuth.setEnabled(false);
            	proxyPort.setEnabled(false);
            	proxyHost.setEnabled(false);
            }
        });  
       	
       	
       	
        final FlowVariableModel fvmD = createFlowVariableModel(
                new String[]{downloadPathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);

        final FlowVariableModel fvmS = createFlowVariableModel(
                new String[]{screenshotPathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);
  
        final FlowVariableModel fvmF = createFlowVariableModel(
                new String[]{firefoxPathModel2.getConfigName(), SettingsModelFileChooser2.PATH_OR_URL_KEY},
                Type.STRING);
        
        
        createNewGroup("Firefox Executable File");
        addDialogComponent(new DialogComponentFileChooser2(0, firefoxPathModel2, "Firefox Executable Path", JFileChooser.OPEN_DIALOG,
                JFileChooser.FILES_ONLY, fvmF));
        closeCurrentGroup();
        
        createNewGroup("default screenshot path");
        addDialogComponent(new DialogComponentFileChooser2(0, screenshotPathModel2, "default screenshot path", JFileChooser.SAVE_DIALOG,
                JFileChooser.DIRECTORIES_ONLY, fvmS));
        closeCurrentGroup();
        
        createNewGroup("default download path");
        addDialogComponent(new DialogComponentFileChooser2(0, downloadPathModel2, "default download path", JFileChooser.SAVE_DIALOG,
                JFileChooser.DIRECTORIES_ONLY, fvmD));
        closeCurrentGroup();
        
        addDialogComponent(new DialogComponentBoolean(headlessMode, "Start browser headless?"));
   
        createNewTab("Firefox Preferences");
        createNewGroup("Firefox Preferences"); 
        addDialogComponent(new DialogComponentFirefoxPreferences(foxModel, "Test Fox"));
        
        closeCurrentGroup();
        
        createNewTab("Proxy Options");
        createNewGroup("Proxy Options"); 
        
        addDialogComponent(
        new DialogComponentStringSelection(useProxyModel, "Proxy Config",
        		Arrays.asList( "no Proxy","Use Proxy"),false));
        
        
        addDialogComponent(new DialogComponentString(proxyHost, "Proxy Host", true, 30));
        addDialogComponent(new DialogComponentNumber(proxyPort, "Proxy Port", 1));
        
        addDialogComponent(new  DialogComponentAuthentication(proxyAuth, "Proxy User/Password", AuthenticationType.USER_PWD));

        closeCurrentGroup();
        
        
    }
}

