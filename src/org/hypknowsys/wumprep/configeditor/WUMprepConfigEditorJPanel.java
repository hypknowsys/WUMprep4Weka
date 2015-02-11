/*
 * WUMprep4Weka - WUMprep for the WEKA data mining environment
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/* 
 * WUMprepConfigEditorJPanel.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep.configeditor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.hypknowsys.wumprep.config.WUMprepConfigComment;
import org.hypknowsys.wumprep.config.WUMprepConfigSection;
import org.hypknowsys.wumprep.config.WUMprepConfigSetting;
import org.hypknowsys.wumprep.config.WUMprepConfigValue;
import org.hypknowsys.wumprep.config.WUMprepConfigValueList;
import org.hypknowsys.wumprep.config.WUMprepConfiguration;
import java.awt.Panel;

/**
 * JavaBean for editing a {@link WUMprepConfiguration} 
 * 
 * @author Carsten Pohle
 * @version $Id: WUMprepConfigEditorJPanel.java,v 1.2 2005/10/16 13:25:42 cpohle Exp $
 */
public class WUMprepConfigEditorJPanel extends JPanel {

	/**
	 * The controller class that synchronizes the WUMprepConfigEditorJPanel view
	 * with an {@link WUMprepConfiguration} model.
	 * 
	 * @author Carsten Pohle
	 */
	public class WUMprepConfigEditorController implements FocusListener,
			ChangeListener {

		private WUMprepConfiguration config = null;

		private boolean configChanged;

		private WUMprepConfigComment contextHelp = null;

		private Vector<ContextHelpChangeListener> contextHelpChangeListeners = null;

		private WUMprepConfiguration defaultConfig = null;

		private String newline = System.getProperty("line.separator");

		private WUMprepConfigEditorJPanel panel = null;

		protected WUMprepConfigEditorController(WUMprepConfigEditorJPanel panel)
				throws IOException, URISyntaxException {
			this(new WUMprepConfiguration(), panel);
		}

		protected WUMprepConfigEditorController(WUMprepConfiguration config,
				WUMprepConfigEditorJPanel panel) {
			super();

			try {
				defaultConfig = new WUMprepConfiguration();
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.contextHelpChangeListeners = new Vector<ContextHelpChangeListener>();
			this.config = config;
			this.panel = panel;

			this.contextHelp = this.defaultConfig.getSection("global").getComment();

			// Add focus listeners for displaying context help for
			// configuration sections (when focus on tabs)

			panel.getJPanelGlobal().setName("section:global");
			panel.getJPanelLogfiles().setName("section:global");
			panel.getJPanelAnonymizer().setName("section:anonymizerSettings");
			panel.getJPanelConversion().setName("section:conversionSettings");
			panel.getJPanelLogFilter().setName("section:filterSettings");
			panel.getJPanelSessionFilter().setName("section:sessionFilterSettings");
			panel.getJPanelDnsLookup().setName("section:reverseLookupSettings");
			panel.getJPanelSessionizer().setName("section:sessionizeSettings");
			panel.getJPanelStatistics().setName("section:statisticsSettings");
			panel.getJPanelRemoveRobots().setName("section:rmRobotsSettings");
			panel.getJPanelTaxonomyMapping().setName("section:mapTaxonomiesSettings");
			panel.getJPanelTransformation().setName("section:transformSettings");
			panel.getJTabbedPane().addChangeListener(this);
		}

		/**
		 * Add a listener to this JavaBean that is notified everytime the current
		 * context help text changes (e.g., another input fied gained focus).
		 * 
		 * @param l An object implementing the ContextHelpChangeListener interface
		 */
		public void addContextHelpChangeListener(ContextHelpChangeListener l) {
			contextHelpChangeListeners.add(l);
		}

		public void focusGained(FocusEvent e) {
			String[] settingInfo = (e.getComponent()).getName().split(":", 3);

			this.contextHelp = this.defaultConfig.getSection(settingInfo[1])
					.getSetting(settingInfo[2]).getComment();
			for (Iterator<ContextHelpChangeListener> i = contextHelpChangeListeners
					.iterator(); i.hasNext();) {
				i.next().contextHelpChanged(contextHelp.getHelpText());
			}

		}

		public void focusLost(FocusEvent e) {
			Component c;
			String settingInfo[];
			WUMprepConfigSetting setting = null;
			String newValue = null;

			c = e.getComponent();
			settingInfo = c.getName().split(":", 3);
			setting = config.getSection(settingInfo[1]).getSetting(settingInfo[2]);

			if (c instanceof JTextField) {
				newValue = ((JTextField) c).getText();
			} else if (c instanceof JComboBox) {
				newValue = ((JComboBox) c).getSelectedItem().toString();
			} else if (c instanceof JTextArea) {
				newValue = ((JTextArea) c).getText();
			} else if (c instanceof JList) {
				newValue = "";
				for (int i = 0; i < ((JList) c).getModel().getSize(); i++) {
					newValue += ((JList) c).getModel().getElementAt(i) + newline;
				}
			}

			if (!setting.getValue().equals(newValue))
				configChanged = true;

			if (settingInfo[0].equals("value")) {
				if (c instanceof JTextField) {
					((WUMprepConfigValue) setting).setValue(newValue);
				} else if (c instanceof JComboBox) {
					((WUMprepConfigValue) setting).setValue(newValue);
				}
			} else if (settingInfo[0].equals("list")) {
				if (c instanceof JTextArea) {
					((WUMprepConfigValueList) setting).setValues(((JTextArea) c)
							.getText());
				} else if (c instanceof JList) {
					ListModel list = ((JList) c).getModel();
					((WUMprepConfigValueList) setting).flush();

					for (int i = 0; i < list.getSize(); i++) {
						((WUMprepConfigValueList) setting).addValue((String) list
								.getElementAt(i));
					}
				}
			}
		}

		/**
		 * Getter for the WUMprepConfiguration displayed by this component.
		 * 
		 * @return A WUMprepConfiguration object
		 */
		public WUMprepConfiguration getConfiguration() {
			return config;
		}

		/**
		 * Get the current context help text.
		 * 
		 * @return The help text
		 */
		public String getContextHelp() {
			return contextHelp.getHelpText();
		}

		private void initControl(JComboBox control, String sectionName,
				String settingName) {
			WUMprepConfigSection section = config.getSection(sectionName);

			if (!section.defined(settingName))
				section.addSetting((WUMprepConfigSetting) defaultConfig.getSection(
						sectionName).getSetting(settingName).getClone());

			for (int i = 0; i < control.getItemCount(); i++) {
				if (control.getItemAt(i).toString().equals(
						section.getSetting(settingName).getValue()))
					control.setSelectedIndex(i);
			}

			control.setName("value:" + sectionName + ":" + settingName);
			control.addFocusListener(this);
		}

		private void initControl(JList control, String sectionName,
				String settingName) {
			DefaultListModel list;
			WUMprepConfigSection section = config.getSection(sectionName);

			if (!section.defined(settingName))
				section.addSetting((WUMprepConfigSetting) defaultConfig.getSection(
						sectionName).getSetting(settingName).getClone());

			list = new DefaultListModel();
			for (String value : ((WUMprepConfigValueList) section
					.getSetting(settingName)).getValues())
				list.addElement(value);

			control.setModel(list);
			control.setName("list:" + sectionName + ":" + settingName);
			control.addFocusListener(this);
		}

		private void initControl(JTextArea control, String sectionName,
				String settingName) {
			WUMprepConfigSection section = config.getSection(sectionName);

			if (!section.defined(settingName))
				section.addSetting((WUMprepConfigSetting) defaultConfig.getSection(
						sectionName).getSetting(settingName).getClone());

			control.setText(section.getSetting(settingName).getValue());
			control.setName("list:" + sectionName + ":" + settingName);
			control.addFocusListener(this);
		}

		private void initControl(JTextField control, String sectionName,
				String settingName) {
			WUMprepConfigSection section = config.getSection(sectionName);

			if (!section.defined(settingName)) {
				section.addSetting((WUMprepConfigSetting) defaultConfig.getSection(
						sectionName).getSetting(settingName).getClone());
			}

			control.setText(section.getSetting(settingName).getValue());
			control.setName("value:" + sectionName + ":" + settingName);
			control.addFocusListener(this);
		}

		/**
		 * Check whether the configuration has been changed via the GUI.
		 * 
		 * Whenever a component using this WUMprepConfigEditorJPanel saves the
		 * underlying WUMprepConfiguration to a file, it should reset the changed
		 * status by calling <tt>setConfigChanged(false)</tt>.
		 * 
		 * @return True if the configuration has been modified by this
		 * 		WUMprepConfigEditorJPanel, else false
		 * @see #setConfigChanged(boolean)
		 */
		public boolean isConfigChanged() {
			return configChanged;
		}

		/**
		 * Remove a listener for changes of the current context help text.
		 * 
		 * @param l A class implementing the ContextHelpChangeListener interface
		 */
		public void removeContextHelpChangeListener(ContextHelpChangeListener l) {
			contextHelpChangeListeners.remove(l);
		}

		/**
		 * Sets the current configChanged status. Use this method to set the
		 * configChanged status to <tt>false</tt> whenever the edited WUMprep
		 * configuration gets saved to disk.
		 * 
		 * @param configChanged
		 * @see #isConfigChanged()
		 */
		public void setConfigChanged(boolean configChanged) {
			this.configChanged = configChanged;
		}

		/**
		 * Load a WUMprep configuration into the editor.
		 * 
		 * @param config The configuration to load
		 */
		public void setConfiguration(WUMprepConfiguration config) {
			this.config = config;

			// === global === (section)
			initControl(panel.getJTextFieldDomain(), "global", "domain");
			initControl(panel.getJComboBoxDnsLookup(), "global", "DNSlookups");
			initControl(panel.getJComboBoxOperationMode(), "global", "operationMode");
			initControl(panel.getJTextFieldOutputDirectory(), "global",
					"outputDirectory");
			initControl(panel.getJTextFieldHTMLTemplateDir(), "global",
					"htmlTemplateDir");
			initControl(panel.getJTextAreaServerFamily(), "global", "serverFamily");

			// === inputLogs ===
			initControl(panel.getJListLogfiles(), "global", "inputLogs");

			// === anonymizerSettings ===
			initControl(panel.getJTextFieldAnonymizerOutputExtension(),
					"anonymizerSettings", "anonOutputExtension");
			initControl(panel.getJTextFieldAnonymizerKeyFile(), "anonymizerSettings",
					"anonKeyFile");

			// === conversionSettings ===
			initControl(panel.getJTextFieldConversionOutputExtension(),
					"conversionSettings", "conversionOutputExtension");

			// === filterSettings ===
			initControl(panel.getJTextFieldLogfilterDuplicatesTimeout(),
					"filterSettings", "filterDuplicateTimeout");
			initControl(panel.getJTextFieldLogfilterOutputExtension(),
					"filterSettings", "filterOutputExtension");
			initControl(panel.getJTextAreaLogfilterPath(), "filterSettings",
					"filterPath");
			initControl(panel.getJTextAreaLogfilterHosts(), "filterSettings",
					"filterHosts");
			initControl(panel.getJTextAreaLogfilterStatusCodes(), "filterSettings",
					"filterStatusCodes");
			initControl(panel.getJTextAreaLogfilterDuplicatesExtensions(),
					"filterSettings", "filterDuplicatesExtensions");

			// === sessionFilterSettings ===
			initControl(panel.getJTextFieldSessionFilterOutputExtension(),
					"sessionFilterSettings", "sessionFilterOutputExtension");

			// sessionFilterRepeatedRequests
			initControl(panel.getJComboBoxSessionFilterRepeatedRequests(),
					"sessionFilterSettings", "sessionFilterRepeatedRequests");

			initControl(panel.getJTextAreaSessionFilterHostIP(),
					"sessionFilterSettings", "sessionFilterHostIp");
			initControl(panel.getJTextAreaSessionFilterHostName(),
					"sessionFilterSettings", "sessionFilterHostName");
			initControl(panel.getJTextAreaSessionFilterPath(),
					"sessionFilterSettings", "sessionFilterPath");

			// === reverseLookupSettings ===
			initControl(panel.getJTextFieldDnsLookupOutputExtension(),
					"reverseLookupSettings", "rLookupOutputExtension");
			initControl(panel.getJTextFieldDnsReverseLookupCache(),
					"reverseLookupSettings", "rLookupCacheFile");

			// === sessionizeSettings ===
			initControl(panel.getJTextFieldSessionizerOutputExtension(),
					"sessionizeSettings", "sessionizeOutputExtension");
			initControl(panel.getJTextFieldSessionizerMaxPageViewTime(),
					"sessionizeSettings", "sessionizeMaxPageViewTime");
			initControl(panel.getJTextFieldSessionizerSeparator(),
					"sessionizeSettings", "sessionizeSeparator");
			initControl(panel.getJTextFieldSessionizerIdCookie(),
					"sessionizeSettings", "sessionizeIdCookie");
			initControl(panel.getJComboBoxSessionizerInsertReferrerHits(),
					"sessionizeSettings", "sessionizeInsertReferrerHits");
			initControl(panel.getJTextFieldSessionizerQueryReferrerName(),
					"sessionizeSettings", "sessionizeQueryReferrerName");
			initControl(panel.getJComboBoxSessionizerForeignReferrerStartsSession(),
					"sessionizeSettings", "sessionizeInsertReferrerHits");

			// === statisticsSettings ===
			initControl(panel.getJTextFieldStatisticsOutputExtension(),
					"statisticsSettings", "statisticsOutputExtension");
			initControl(panel.getJComboBoxStatisticsExport(), "statisticsSettings",
					"statisticsExport");
			initControl(panel.getJTextFieldStatisticsTemplateFile(),
					"statisticsSettings", "statisticsTemplateFile");

			// === rmRobotsSettings
			initControl(panel.getJTextFieldRmRobotsDB(), "rmRobotsSettings",
					"rmRobotsDB");
			initControl(panel.getJTextFieldRmRobotsOutputExtension(),
					"rmRobotsSettings", "rmRobotsOutputExtension");
			initControl(panel.getJTextFieldRmRobotsMaxViewTime(), "rmRobotsSettings",
					"rmRobotsMaxViewTime");

			// === mapTaxonomiesSettings ===
			initControl(panel.getJTextFieldTaxonomyMapOutputExtension(),
					"mapTaxonomiesSettings", "taxonoMapOutputExtension");
			initControl(panel.getJTextFieldTaxonomyMapLogfile(),
					"mapTaxonomiesSettings", "taxonoMapLog");
			initControl(panel.getJTextAreaTaxonomyMapDefs(), "mapTaxonomiesSettings",
					"taxonomyDefs");

			// === transformSettings ===
			initControl(panel.getJTextFieldTransformOutputExtension(),
					"transformSettings", "transformOutputExtension");
			initControl(panel.getJComboBoxConversionTransformMode(),
					"transformSettings", "transformMode");
			initControl(panel.getJTextFieldTransformTemplate(), "transformSettings",
					"transformTemplate");
			initControl(panel.getJTextFieldTransformSessonVectorFile(),
					"transformSettings", "transformSessionVectorFile");

			panel.getJTabbedPane().setSelectedIndex(0);
			panel.getJPanelGlobal().grabFocus();
		}

		public void stateChanged(ChangeEvent arg0) {
			if (arg0.getSource() == panel.getJTabbedPane()) {
				// Set the context help to the description of the focused
				// section-tab
				String[] settingInfo = ((JTabbedPane) arg0.getSource())
						.getSelectedComponent().getName().split(":", 2);
				((JTabbedPane) arg0.getSource()).grabFocus();

				this.contextHelp = this.defaultConfig.getSection(settingInfo[1])
						.getComment();

				for (Iterator<ContextHelpChangeListener> i = contextHelpChangeListeners
						.iterator(); i.hasNext();) {
					i.next().contextHelpChanged(contextHelp.getHelpText());
				}
			}
		}
	}

	// private WUMprepConfiguration config = null;

	private static final long serialVersionUID = 8391624090881526029L;

	private WUMprepConfigEditorController controller = null;

	private JFileChooser fc = null;

	private FileFilter htmlFileFilter = null;

	private FileFilter logFileFilter = null;

	private FileFilter sessionVectorFileFilter = null;

	private JButton jButtonAnonymizerKeyFile = null;

	private JButton jButtonLogfileAdd = null;

	private JButton jButtonLogfileRemove = null;

	private JButton jButtonRmRobotsDbSelect = null;

	private JButton jButtonSelectStatisticsTemplateFile = null;

	private JButton jButtonTransformSessionVectorFileSelect = null;

	private JComboBox jComboBoxConversionTransformMode = null;

	private JComboBox jComboBoxDnsLookup = null;

	private JComboBox jComboBoxOperationMode = null;

	private JComboBox jComboBoxSessionFilterRepeatedRequests = null;

	private JComboBox jComboBoxSessionizerForeignReferrerStartsSession = null;

	private JComboBox jComboBoxSessionizerInsertReferrerHits = null;

	private JComboBox jComboBoxStatisticsExport = null;

	private JLabel jLabel = null;

	private JLabel jLabelAnonymizerKeyFile = null;

	private JLabel jLabelAnonymizerOutputExtenstion = null;

	private JLabel jLabelDnsLookup = null;

	private JLabel jLabelDnsLookupOutputExtension = null;

	private JLabel jLabelDnsReverseLookupCache = null;

	private JLabel jLabelDomain = null;

	private JLabel jLabelHTMLTemplateDir = null;

	private JLabel jLabelLogfiles = null;

	private JLabel jLabelLogfilterDuplateTimeout = null;

	private JLabel jLabelLogfilterDupliatesExtensions = null;

	private JLabel jLabelLogfilterHosts = null;

	private JLabel jLabelLogfilterOutputExtension = null;

	private JLabel jLabelLogfilterPath = null;

	private JLabel jLabelLogfilterStatusCodes = null;

	private JLabel jLabelOperationMode = null;

	private JLabel jLabelOutputDirectory = null;

	private JLabel jLabelRmRobotsDB = null;

	private JLabel jLabelRmRobotsMaxViewTime = null;

	private JLabel jLabelRmRobotsOutputExtension = null;

	private JLabel jLabelServerFamily = null;

	private JLabel jLabelSessionFilterHostIp = null;

	private JLabel jLabelSessionFilterHostName = null;

	private JLabel jLabelSessionFilterOutputExtension = null;

	private JLabel jLabelSessionFilterPath = null;

	private JLabel jLabelSessionFilterRepeatedRequests = null;

	private JLabel jLabelSessionizerForeignReferrerStartsSession = null;

	private JLabel jLabelSessionizerIdCookie = null;

	private JLabel jLabelSessionizerInsertReferrerHits = null;

	private JLabel jLabelSessionizerMaxPageViewTime = null;

	private JLabel jLabelSessionizerOutputExtension = null;

	private JLabel jLabelSessionizerQueryReferrerName = null;

	private JLabel jLabelSessionizerSeparator = null;

	private JLabel jLabelStatisticsExport = null;

	private JLabel jLabelStatisticsOutputExtension = null;

	private JLabel jLabelStatisticsTemplateFile = null;

	private JLabel jLabelTaxonomyMapDefs = null;

	private JLabel jLabelTaxonomyMapOutputExtension = null;

	private JLabel jLabelTaxonoyMapLogfile = null;

	private JLabel jLabelTransformMode = null;

	private JLabel jLabelTransformOutputExtension = null;

	private JLabel jLabelTransformSessionVectorFile = null;

	private JLabel jLabelTransformTemplate = null;

	private JList jListLogfiles = null;

	private JPanel jPanelAnonymizer = null;

	private JPanel jPanelConversion = null;

	private JPanel jPanelDnsLookup = null;

	private JPanel jPanelGlobal = null;

	private JPanel jPanelLogfiles = null;

	private JPanel jPanelLogFilter = null;

	private JPanel jPanelRemoveRobots = null;

	private JPanel jPanelSessionFilter = null;

	private JPanel jPanelSessionizer = null;

	private JPanel jPanelStatistics = null;

	private JPanel jPanelTaxonomyMapping = null;

	private JPanel jPanelTransformation = null;

	private JScrollPane jScrollPaneLogfiles = null;

	private JScrollPane jScrollPaneLogfilterDuplicatesExtensions = null;

	private JScrollPane jScrollPaneLogfilterHosts = null;

	private JScrollPane jScrollPaneLogfilterPath = null;

	private JScrollPane jScrollPaneLogfilterStatusCodes = null;

	private JScrollPane jScrollPaneServerFamily = null;

	private JScrollPane jScrollPaneSessionFilterHostIP = null;

	private JScrollPane jScrollPaneSessionFilterHostName = null;

	private JScrollPane jScrollPaneSessionFilterPath = null;

	private JScrollPane jScrollPaneTaxonomyMapDefs = null;

	private JTabbedPane jTabbedPane = null;

	private JTextArea jTextAreaLogfilterDuplicatesExtensions = null;

	private JTextArea jTextAreaLogfilterHosts = null;

	private JTextArea jTextAreaLogfilterPath = null;

	private JTextArea jTextAreaLogfilterStatusCodes = null;

	private JTextArea jTextAreaServerFamily = null;

	private JTextArea jTextAreaSessionFilterHostIP = null;

	private JTextArea jTextAreaSessionFilterHostName = null;

	private JTextArea jTextAreaSessionFilterPath = null;

	private JTextArea jTextAreaTaxonomyMapDefs = null;

	private JTextField jTextFieldAnonymizerKeyFile = null;

	private JTextField jTextFieldAnonymizerOutputExtension = null;

	private JTextField jTextFieldConversionOutputExtension = null;

	private JTextField jTextFieldDnsLookupOutputExtension = null;

	private JTextField jTextFieldDnsReverseLookupCache = null;

	private JTextField jTextFieldDomain = null;

	private JTextField jTextFieldHTMLTemplateDir = null;

	private JTextField jTextFieldLogfilterDuplicatesTimeout = null;

	private JTextField jTextFieldLogfilterOutputExtension = null;

	private JTextField jTextFieldOutputDirectory = null;

	private JTextField jTextFieldRmRobotsDB = null;

	private JTextField jTextFieldRmRobotsMaxViewTime = null;

	private JTextField jTextFieldRmRobotsOutputExtension = null;

	private JTextField jTextFieldSessionFilterOutputExtension = null;

	private JTextField jTextFieldSessionizerIdCookie = null;

	private JTextField jTextFieldSessionizerMaxPageViewTime = null;

	private JTextField jTextFieldSessionizerOutputExtension = null;

	private JTextField jTextFieldSessionizerQueryReferrerName = null;

	private JTextField jTextFieldSessionizerSeparator = null;

	private JTextField jTextFieldStatisticsOutputExtension = null;

	private JTextField jTextFieldStatisticsTemplateFile = null;

	private JTextField jTextFieldTaxonomyMapLogfile = null;

	private JTextField jTextFieldTaxonomyMapOutputExtension = null;

	private JTextField jTextFieldTransformOutputExtension = null;

	private JTextField jTextFieldTransformSessonVectorFile = null;

	private JTextField jTextFieldTransformTemplate = null;

	/**
	 * This is the default constructor
	 */
	public WUMprepConfigEditorJPanel() {
		super();
		initialize();
	}

	/**
	 * Getter for the controller of the editor GUI. Use this controller for
	 * interacting with the editor bean.
	 * 
	 * @return The controller
	 */
	public WUMprepConfigEditorController getController() {
		return controller;
	}

	private JFileChooser getFileChooser() {
		if (fc == null) {
			this.fc = new JFileChooser();
		}
		return fc;
	}

	private javax.swing.filechooser.FileFilter getHtmlFileFilter() {
		if (htmlFileFilter == null) {
			htmlFileFilter = new FileFilter() {
				public boolean accept(File f) {
					if (f.getAbsolutePath().matches(".*\\.html?"))
						return true;

					if (f.isDirectory())
						return true;

					return false;
				}

				public String getDescription() {
					return "HTML file (*.html, *.htm)";
				}
			};
		}
		return this.htmlFileFilter;
	}

	private javax.swing.filechooser.FileFilter getSessionVectorFileFilter() {
		if (sessionVectorFileFilter == null) {
			sessionVectorFileFilter = new FileFilter() {
				public boolean accept(File f) {
					if (f.getAbsolutePath().matches(".*\\.(csv|txt)"))
						return true;

					if (f.isDirectory())
						return true;

					return false;
				}

				public String getDescription() {
					return "Session vector files (*.csv, *.txt)";
				}
			};
		}
		return this.sessionVectorFileFilter;
	}

	
	private javax.swing.filechooser.FileFilter getRobotsDbFileFilter() {
		if (htmlFileFilter == null) {
			htmlFileFilter = new FileFilter() {
				public boolean accept(File f) {
					if (f.getAbsolutePath().matches(".*\\.db"))
						return true;

					if (f.isDirectory())
						return true;

					return false;
				}

				public String getDescription() {
					return "Robots database files (*.db)";
				}
			};
		}
		return this.htmlFileFilter;
	}

	/**
	 * This method initializes jButtonAnonymizerKeyFile
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonAnonymizerKeyFile() {
		if (jButtonAnonymizerKeyFile == null) {
			jButtonAnonymizerKeyFile = new JButton();
			jButtonAnonymizerKeyFile.setText("...");
			jButtonAnonymizerKeyFile
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser fc = getFileChooser();
							fc.setFileFilter(fc.getAcceptAllFileFilter());
							fc.setDialogTitle("Select anonymizer key file");
							fc.setApproveButtonText("Select");

							if (fc.showOpenDialog(WUMprepConfigEditorJPanel.this) == JFileChooser.APPROVE_OPTION) {
								getJTextFieldAnonymizerKeyFile().setText(
										fc.getSelectedFile().getAbsolutePath());
							}
						}
					});
		}
		return jButtonAnonymizerKeyFile;
	}

	/**
	 * This method initializes jButtonLogfileAdd
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonLogfileAdd() {
		if (jButtonLogfileAdd == null) {
			jButtonLogfileAdd = new JButton();
			jButtonLogfileAdd.setText("Add...");
			jButtonLogfileAdd.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser fc = getFileChooser();
					fc.setFileFilter(getLogFileFilter());
					fc.setDialogTitle("Select log file");
					fc.setApproveButtonText("Select");
					fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

					if (fc.showOpenDialog(WUMprepConfigEditorJPanel.this) == JFileChooser.APPROVE_OPTION) {
						DefaultListModel list = (DefaultListModel) getJListLogfiles().getModel();
						list.addElement(fc.getSelectedFile().getAbsolutePath());
					}
				}
			});
		}
		return jButtonLogfileAdd;
	}

	/**
	 * This method initializes jButtonLogfileRemove
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonLogfileRemove() {
		if (jButtonLogfileRemove == null) {
			jButtonLogfileRemove = new JButton();
			jButtonLogfileRemove.setText("Remove");
			jButtonLogfileRemove
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							DefaultListModel model = (DefaultListModel) getJListLogfiles().getModel();
							for (int i = model.getSize() - 1; i >= 0; i--) {
								if (getJListLogfiles().isSelectedIndex(i))
									model.remove(i);
							}
						}
					});
		}
		return jButtonLogfileRemove;
	}

	/**
	 * This method initializes jButtonRmRobotsDbSelect
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonRmRobotsDbSelect() {
		if (jButtonRmRobotsDbSelect == null) {
			jButtonRmRobotsDbSelect = new JButton();
			jButtonRmRobotsDbSelect.setText("...");
			jButtonRmRobotsDbSelect
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser fc = getFileChooser();
							fc.setFileFilter(getRobotsDbFileFilter());
							fc.setDialogTitle("Select robots dabase");
							fc.setApproveButtonText("Select");

							if (fc.showOpenDialog(WUMprepConfigEditorJPanel.this) == JFileChooser.APPROVE_OPTION) {
								getJTextFieldRmRobotsDB().setText(
										fc.getSelectedFile().getAbsolutePath());
							}
						}
					});
		}
		return jButtonRmRobotsDbSelect;
	}

	/**
	 * This method initializes jButtonSelectStatisticsTemplateFile
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonSelectStatisticsTemplateFile() {
		if (jButtonSelectStatisticsTemplateFile == null) {
			jButtonSelectStatisticsTemplateFile = new JButton();
			jButtonSelectStatisticsTemplateFile.setText("...");
			jButtonSelectStatisticsTemplateFile
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser fc = getFileChooser();
							fc.setFileFilter(getHtmlFileFilter());
							fc.setDialogTitle("Select statistics template file");
							fc.setApproveButtonText("Select");

							if (fc.showOpenDialog(WUMprepConfigEditorJPanel.this) == JFileChooser.APPROVE_OPTION) {
								getJTextFieldStatisticsTemplateFile().setText(
										fc.getSelectedFile().getAbsolutePath());
							}
						}
					});
		}
		return jButtonSelectStatisticsTemplateFile;
	}

	/**
	 * This method initializes jButtonTransformSessionVectorFileSelect
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonTransformSessionVectorFileSelect() {
		if (jButtonTransformSessionVectorFileSelect == null) {
			jButtonTransformSessionVectorFileSelect = new JButton();
			jButtonTransformSessionVectorFileSelect.setText("...");
			jButtonTransformSessionVectorFileSelect
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							JFileChooser fc = getFileChooser();
							fc.setFileFilter(getSessionVectorFileFilter());
							fc.setDialogTitle("Select session vector file");
							fc.setApproveButtonText("Select");

							if (fc.showOpenDialog(WUMprepConfigEditorJPanel.this) == JFileChooser.APPROVE_OPTION) {
								getJTextFieldTransformSessonVectorFile().setText(
										fc.getSelectedFile().getAbsolutePath());
							}
						}
					});
		}
		return jButtonTransformSessionVectorFileSelect;
	}

	/**
	 * This method initializes jComboBoxConversionTransformMode
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxConversionTransformMode() {
		if (jComboBoxConversionTransformMode == null) {
			String[] options = { "SEQUENCE", "SESSION_VECTOR" };
			jComboBoxConversionTransformMode = new JComboBox(options);
		}
		return jComboBoxConversionTransformMode;
	}

	/**
	 * This method initializes jComboBoxDnsLookup
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxDnsLookup() {
		if (jComboBoxDnsLookup == null) {
			String[] options = { "yes", "no" };
			jComboBoxDnsLookup = new JComboBox(options);
		}
		return jComboBoxDnsLookup;
	}

	/**
	 * This method initializes jComboBoxOperationMode
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxOperationMode() {
		if (jComboBoxOperationMode == null) {
			String[] options = { "File" };
			jComboBoxOperationMode = new JComboBox(options);
			jComboBoxOperationMode.setSelectedIndex(0);
		}
		return jComboBoxOperationMode;
	}

	/**
	 * This method initializes jComboBoxSessionFilterRepeatedRequests
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxSessionFilterRepeatedRequests() {
		if (jComboBoxSessionFilterRepeatedRequests == null) {
			String[] options = { "yes", "no" };
			jComboBoxSessionFilterRepeatedRequests = new JComboBox(options);
		}
		return jComboBoxSessionFilterRepeatedRequests;
	}

	/**
	 * This method initializes jComboBoxSessionizerForeignReferrerStartsSession
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxSessionizerForeignReferrerStartsSession() {
		if (jComboBoxSessionizerForeignReferrerStartsSession == null) {
			String[] options = { "yes", "no" };
			jComboBoxSessionizerForeignReferrerStartsSession = new JComboBox(options);
		}
		return jComboBoxSessionizerForeignReferrerStartsSession;
	}

	/**
	 * This method initializes jComboBoxSessionizerInsertReferrerHits
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxSessionizerInsertReferrerHits() {
		if (jComboBoxSessionizerInsertReferrerHits == null) {
			String[] options = { "yes", "no" };
			jComboBoxSessionizerInsertReferrerHits = new JComboBox(options);
		}
		return jComboBoxSessionizerInsertReferrerHits;
	}

	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBoxStatisticsExport() {
		if (jComboBoxStatisticsExport == null) {
			String[] options = { "yes", "no" };
			jComboBoxStatisticsExport = new JComboBox(options);
		}
		return jComboBoxStatisticsExport;
	}

	/**
	 * This method initializes jListLogfiles
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJListLogfiles() {
		if (jListLogfiles == null) {
			jListLogfiles = new JList();
		}
		return jListLogfiles;
	}

	/**
	 * This method initializes jPanelAnonymizer
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelAnonymizer() {
		if (jPanelAnonymizer == null) {
			jPanelAnonymizer = new JPanel();
			jPanelAnonymizer.setLayout(new GridBagLayout());
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 1;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.insets = new java.awt.Insets(11, 12, 12, 0);
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.NORTH;
			gridBagConstraints12.weighty = 1.0D;
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.insets = new java.awt.Insets(11, 12, 12, 0);
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints11.gridy = 1;
			jLabelAnonymizerOutputExtenstion = new JLabel();
			jLabelAnonymizerOutputExtenstion.setText("Output extenstion:");
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 2;
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints10.insets = new java.awt.Insets(12, 5, 0, 12);
			gridBagConstraints10.gridy = 0;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.insets = new java.awt.Insets(12, 12, 0, 0);
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.gridx = 1;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.insets = new java.awt.Insets(12, 12, 0, 0);
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints5.gridy = 0;
			jLabelAnonymizerKeyFile = new JLabel();
			jLabelAnonymizerKeyFile.setText("Key file:");
			jPanelAnonymizer.add(jLabelAnonymizerOutputExtenstion, gridBagConstraints11);
			jPanelAnonymizer.add(getJTextFieldAnonymizerOutputExtension(), gridBagConstraints12);
			jPanelAnonymizer.add(jLabelAnonymizerKeyFile, gridBagConstraints5);
			jPanelAnonymizer.add(getJTextFieldAnonymizerKeyFile(),
					gridBagConstraints6);
			jPanelAnonymizer.add(getJButtonAnonymizerKeyFile(), gridBagConstraints10);
		}
		return jPanelAnonymizer;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelConversion() {
		if (jPanelConversion == null) {
			GridBagConstraints gridBagConstraints68 = new GridBagConstraints();
			gridBagConstraints68.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints68.anchor = GridBagConstraints.FIRST_LINE_END;
			gridBagConstraints68.gridx = 1;
			gridBagConstraints68.gridy = 0;
			gridBagConstraints68.weightx = 1.0;
			gridBagConstraints68.weighty = 1.0;
			gridBagConstraints68.insets = new java.awt.Insets(11, 12, 0, 12);
			GridBagConstraints gridBagConstraints67 = new GridBagConstraints();
			gridBagConstraints67.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints67.anchor = GridBagConstraints.FIRST_LINE_START;
			gridBagConstraints67.gridy = 0;
			gridBagConstraints67.gridx = 0;
			jLabel = new JLabel();
			jLabel.setText("Output extension:");
			jLabel.setEnabled(false);
			jPanelConversion = new JPanel();
			jPanelConversion.setLayout(new GridBagLayout());
			jPanelConversion.add(jLabel, gridBagConstraints67);
			jPanelConversion.add(getJTextFieldConversionOutputExtension(),
					gridBagConstraints68);
		}
		return jPanelConversion;
	}

	/**
	 * This method initializes jPanelDnsLookup
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelDnsLookup() {
		if (jPanelDnsLookup == null) {
			GridBagConstraints gridBagConstraints78 = new GridBagConstraints();
			gridBagConstraints78.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints78.anchor = GridBagConstraints.NORTHWEST;
			gridBagConstraints78.gridy = 2;
			gridBagConstraints78.weightx = 1.0;
			gridBagConstraints78.weighty = 1.0;
			gridBagConstraints78.insets = new java.awt.Insets(11, 12, 12, 12);
			gridBagConstraints78.gridx = 1;
			GridBagConstraints gridBagConstraints77 = new GridBagConstraints();
			gridBagConstraints77.gridx = 0;
			gridBagConstraints77.insets = new java.awt.Insets(11, 0, 12, 0);
			gridBagConstraints77.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints77.gridy = 2;
			jLabelDnsLookupOutputExtension = new JLabel();
			jLabelDnsLookupOutputExtension.setText("Output extension:");
			GridBagConstraints gridBagConstraints76 = new GridBagConstraints();
			gridBagConstraints76.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints76.gridy = 1;
			gridBagConstraints76.weightx = 1.0;
			gridBagConstraints76.insets = new java.awt.Insets(12, 12, 0, 12);
			gridBagConstraints76.gridx = 1;
			GridBagConstraints gridBagConstraints75 = new GridBagConstraints();
			gridBagConstraints75.gridx = 0;
			gridBagConstraints75.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints75.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints75.gridy = 1;
			jLabelDnsReverseLookupCache = new JLabel();
			jLabelDnsReverseLookupCache.setText("Reverse lookup cache:");
			GridBagConstraints gridBagConstraints74 = new GridBagConstraints();
			//gridBagConstraints74.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints74.anchor = GridBagConstraints.LINE_START;
			gridBagConstraints74.gridy = 0;
			gridBagConstraints74.weightx = 1.0;
			gridBagConstraints74.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints74.gridx = 1;
			GridBagConstraints gridBagConstraints73 = new GridBagConstraints();
			gridBagConstraints73.gridx = 0;
			gridBagConstraints73.insets = new java.awt.Insets(12, 0, 0, 0);
			gridBagConstraints73.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints73.gridy = 0;
			jLabelDnsLookup = new JLabel();
			jLabelDnsLookup.setText("DNS lookups:");
			jPanelDnsLookup = new JPanel();
			jPanelDnsLookup.setLayout(new GridBagLayout());
			jPanelDnsLookup.add(jLabelDnsLookup, gridBagConstraints73);
			jPanelDnsLookup.add(getJComboBoxDnsLookup(), gridBagConstraints74);
			jPanelDnsLookup.add(jLabelDnsReverseLookupCache, gridBagConstraints75);
			jPanelDnsLookup.add(getJTextFieldDnsReverseLookupCache(),
					gridBagConstraints76);
			jPanelDnsLookup.add(jLabelDnsLookupOutputExtension, gridBagConstraints77);
			jPanelDnsLookup.add(getJTextFieldDnsLookupOutputExtension(),
					gridBagConstraints78);
		}
		return jPanelDnsLookup;
	}

	/**
	 * This method initializes jPanelAnonymizer
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelGlobal() {
		if (jPanelGlobal == null) {
			GridBagConstraints gridBagConstraints410 = new GridBagConstraints();
			gridBagConstraints410.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints410.gridy = 5;
			gridBagConstraints410.weightx = 1.0;
			gridBagConstraints410.insets = new java.awt.Insets(11, 12, 12, 12);
			gridBagConstraints410.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints410.gridx = 1;
			GridBagConstraints gridBagConstraints310 = new GridBagConstraints();
			gridBagConstraints310.gridx = 0;
			gridBagConstraints310.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints310.gridy = 5;
			jLabelHTMLTemplateDir = new JLabel();
			jLabelHTMLTemplateDir.setText("HTML templates:");
			GridBagConstraints gridBagConstraints210 = new GridBagConstraints();
			gridBagConstraints210.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints210.gridy = 4;
			gridBagConstraints210.weightx = 1.0;
			gridBagConstraints210.gridx = 1;
			gridBagConstraints210.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints210.insets = new java.awt.Insets(11, 12, 0, 12);
			GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
			gridBagConstraints110.gridx = 0;
			gridBagConstraints110.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints110.gridy = 4;
			jLabelOutputDirectory = new JLabel();
			jLabelOutputDirectory.setText("Output directory:");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints.gridx = 1;
			GridBagConstraints gbcJComboBoxOperationMode = new GridBagConstraints();
			//gbcJComboBoxOperationMode.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbcJComboBoxOperationMode.gridy = 2;
			gbcJComboBoxOperationMode.weightx = 1.0;
			gbcJComboBoxOperationMode.insets = new java.awt.Insets(11, 12, 0, 0);
			gbcJComboBoxOperationMode.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gbcJComboBoxOperationMode.gridx = 1;
			GridBagConstraints gbcJLabelOperationMode = new GridBagConstraints();
			gbcJLabelOperationMode.gridx = 0;
			gbcJLabelOperationMode.anchor = java.awt.GridBagConstraints.EAST;
			gbcJLabelOperationMode.insets = new java.awt.Insets(11, 12, 0, 0);
			gbcJLabelOperationMode.gridy = 2;
			jLabelOperationMode = new JLabel();
			jLabelOperationMode.setText("Operation mode:");
			jLabelOperationMode.setLabelFor(getJComboBoxOperationMode());
			GridBagConstraints gbcJLabelServerFamily = new GridBagConstraints();
			gbcJLabelServerFamily.gridx = 0;
			gbcJLabelServerFamily.insets = new java.awt.Insets(11, 12, 0, 0);
			gbcJLabelServerFamily.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gbcJLabelServerFamily.fill = java.awt.GridBagConstraints.NONE;
			gbcJLabelServerFamily.gridy = 3;
			jLabelServerFamily = new JLabel();
			jLabelServerFamily.setLabelFor(jTextAreaServerFamily);
			jLabelServerFamily.setText("Server family:");
			GridBagConstraints gbcJTextFieldDomain = new GridBagConstraints();
			gbcJTextFieldDomain.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbcJTextFieldDomain.gridy = 0;
			gbcJTextFieldDomain.weightx = 1.0;
			gbcJTextFieldDomain.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gbcJTextFieldDomain.gridx = 1;
			gbcJTextFieldDomain.insets = new java.awt.Insets(12, 12, 0, 12);
			GridBagConstraints gbcJLabelDomain = new GridBagConstraints();
			gbcJLabelDomain.gridx = 0;
			gbcJLabelDomain.anchor = java.awt.GridBagConstraints.EAST;
			gbcJLabelDomain.gridy = 0;
			gbcJLabelDomain.insets = new java.awt.Insets(12, 12, 0, 0);
			jLabelDomain = new JLabel();
			jLabelDomain.setText("Domain:");
			jLabelDomain.setLabelFor(jTextFieldDomain);
			jPanelGlobal = new JPanel();
			GridBagLayout layout = new GridBagLayout();
			jPanelGlobal.setLayout(layout);
			jPanelGlobal.add(jLabelDomain, gbcJLabelDomain);
			jPanelGlobal.add(getJTextFieldDomain(), gbcJTextFieldDomain);
			jPanelGlobal.add(jLabelServerFamily, gbcJLabelServerFamily);
			jPanelGlobal.add(jLabelOperationMode, gbcJLabelOperationMode);
			jPanelGlobal.add(getJComboBoxOperationMode(), gbcJComboBoxOperationMode);
			jPanelGlobal.add(getJScrollPaneServerFamily(), gridBagConstraints);
			jPanelGlobal.add(jLabelOutputDirectory, gridBagConstraints110);
			jPanelGlobal.add(getJTextFieldOutputDirectory(), gridBagConstraints210);
			jPanelGlobal.add(jLabelHTMLTemplateDir, gridBagConstraints310);
			jPanelGlobal.add(getJTextFieldHTMLTemplateDir(), gridBagConstraints410);
		}
		return jPanelGlobal;
	}

	/**
	 * This method in jLabelLogfiles.setText("JLabel"); itializes jPanelLogFilter
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelLogfiles() {
		if (jPanelLogfiles == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.gridy = 1;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.insets = new java.awt.Insets(5, 12, 12, 0);
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.gridheight = 2;
			gridBagConstraints1.gridx = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 1;
			gridBagConstraints9.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gridBagConstraints9.insets = new java.awt.Insets(12, 12, 0, 0);
			gridBagConstraints9.gridy = 0;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.anchor = java.awt.GridBagConstraints.NORTH;
			gridBagConstraints8.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 2;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 2;
			gridBagConstraints7.insets = new java.awt.Insets(5, 12, 0, 12);
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 1;
			jPanelLogfiles = new JPanel();
			GridBagLayout layout = new GridBagLayout();
			jPanelLogfiles.setLayout(layout);
			jPanelLogfiles.add(getJButtonLogfileAdd(), gridBagConstraints7);
			jPanelLogfiles.add(getJButtonLogfileRemove(), gridBagConstraints8);
			jLabelLogfiles = new JLabel();
			jLabelLogfiles.setText("Logfiles to process:");
			jLabelLogfiles.setLabelFor(jListLogfiles);
			jPanelLogfiles.add(jLabelLogfiles, gridBagConstraints9);
			jPanelLogfiles.add(getJScrollPaneLogfiles(), gridBagConstraints1);
		}
		return jPanelLogfiles;
	}

	/**
	 * This method initializes jPanelLogFilter
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelLogFilter() {
		if (jPanelLogFilter == null) {
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints33.gridy = 5;
			gridBagConstraints33.weightx = 1.0;
			gridBagConstraints33.insets = new java.awt.Insets(11, 12, 12, 12);
			gridBagConstraints33.gridx = 1;
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.gridx = 0;
			gridBagConstraints32.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints32.insets = new java.awt.Insets(11, 12, 12, 0);
			gridBagConstraints32.gridy = 5;
			jLabelLogfilterOutputExtension = new JLabel();
			jLabelLogfilterOutputExtension.setText("Output extension:");
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints31.gridy = 4;
			gridBagConstraints31.weightx = 1.0;
			gridBagConstraints31.weighty = 1.0;
			gridBagConstraints31.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints31.gridx = 1;
			GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
			gridBagConstraints30.gridx = 0;
			gridBagConstraints30.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints30.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints30.gridy = 4;
			jLabelLogfilterDupliatesExtensions = new JLabel();
			jLabelLogfilterDupliatesExtensions
					.setText("Duplicate filter extensions:");
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			gridBagConstraints29.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints29.gridy = 3;
			gridBagConstraints29.weightx = 1.0;
			gridBagConstraints29.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints29.gridx = 1;
			GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
			gridBagConstraints28.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints28.gridy = 2;
			gridBagConstraints28.weightx = 1.0;
			gridBagConstraints28.weighty = 1.0;
			gridBagConstraints28.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints28.gridx = 1;
			GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
			gridBagConstraints27.gridx = 0;
			gridBagConstraints27.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints27.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints27.gridy = 3;
			jLabelLogfilterDuplateTimeout = new JLabel();
			jLabelLogfilterDuplateTimeout.setText("Duplicate filter timeout:");
			GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
			gridBagConstraints26.gridx = 0;
			gridBagConstraints26.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints26.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints26.gridy = 2;
			jLabelLogfilterStatusCodes = new JLabel();
			jLabelLogfilterStatusCodes.setText("Status codes:");
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints25.gridy = 1;
			gridBagConstraints25.weightx = 1.0;
			gridBagConstraints25.weighty = 1.0;
			gridBagConstraints25.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints25.gridx = 1;
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.gridx = 0;
			gridBagConstraints23.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints23.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints23.gridy = 1;
			jLabelLogfilterHosts = new JLabel();
			jLabelLogfilterHosts.setText("Hosts:");
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints24.gridy = 0;
			gridBagConstraints24.weightx = 1.0;
			gridBagConstraints24.weighty = 1.0;
			gridBagConstraints24.insets = new java.awt.Insets(12, 12, 0, 12);
			gridBagConstraints24.gridx = 1;
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints22.insets = new java.awt.Insets(12, 12, 0, 0);
			gridBagConstraints22.gridy = 0;
			jLabelLogfilterPath = new JLabel();
			jLabelLogfilterPath.setText("Paths:");
			jPanelLogFilter = new JPanel();
			jPanelLogFilter.setLayout(new GridBagLayout());
			jPanelLogFilter.add(jLabelLogfilterPath, gridBagConstraints22);
			jPanelLogFilter.add(getJScrollPaneLogfilterPath(), gridBagConstraints24);
			jPanelLogFilter.add(jLabelLogfilterHosts, gridBagConstraints23);
			jPanelLogFilter.add(jLabelLogfilterDupliatesExtensions,
					gridBagConstraints30);
			jPanelLogFilter.add(getJScrollPaneLogfilterDuplicatesExtensions(),
					gridBagConstraints31);
			jPanelLogFilter.add(jLabelLogfilterOutputExtension, gridBagConstraints32);
			jPanelLogFilter.add(getJTextFieldLogfilterOutputExtension(),
					gridBagConstraints33);
			jPanelLogFilter.add(getJScrollPaneLogfilterHosts(), gridBagConstraints25);
			jPanelLogFilter.add(jLabelLogfilterStatusCodes, gridBagConstraints26);
			jPanelLogFilter.add(jLabelLogfilterDuplateTimeout, gridBagConstraints27);
			jPanelLogFilter.add(getJScrollPaneLogfilterStatusCodes(),
					gridBagConstraints28);
			jPanelLogFilter.add(getJTextFieldLogfilterDuplicatesTimeout(),
					gridBagConstraints29);
		}
		return jPanelLogFilter;
	}

	/**
	 * This method initializes jPanelRemoveRobots
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelRemoveRobots() {
		if (jPanelRemoveRobots == null) {
			GridBagConstraints gridBagConstraints60 = new GridBagConstraints();
			gridBagConstraints60.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints60.gridy = 3;
			gridBagConstraints60.weightx = 1.0;
			gridBagConstraints60.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints60.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gridBagConstraints60.weighty = 1.0D;
			gridBagConstraints60.gridx = 1;
			GridBagConstraints gridBagConstraints59 = new GridBagConstraints();
			gridBagConstraints59.gridx = 0;
			gridBagConstraints59.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints59.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints59.gridy = 3;
			jLabelRmRobotsOutputExtension = new JLabel();
			jLabelRmRobotsOutputExtension.setText("Output extension:");
			GridBagConstraints gridBagConstraints58 = new GridBagConstraints();
			gridBagConstraints58.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints58.gridy = 1;
			gridBagConstraints58.weightx = 1.0;
			gridBagConstraints58.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints58.gridx = 1;
			GridBagConstraints gridBagConstraints57 = new GridBagConstraints();
			gridBagConstraints57.gridx = 0;
			gridBagConstraints57.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints57.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints57.gridy = 1;
			jLabelRmRobotsMaxViewTime = new JLabel();
			jLabelRmRobotsMaxViewTime.setText("Max. view time:");
			GridBagConstraints gridBagConstraints55 = new GridBagConstraints();
			gridBagConstraints55.gridx = 2;
			gridBagConstraints55.insets = new java.awt.Insets(12, 5, 0, 12);
			gridBagConstraints55.gridy = 0;
			GridBagConstraints gridBagConstraints54 = new GridBagConstraints();
			gridBagConstraints54.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints54.gridy = 0;
			gridBagConstraints54.weightx = 1.0;
			gridBagConstraints54.insets = new java.awt.Insets(12, 12, 0, 0);
			gridBagConstraints54.gridx = 1;
			GridBagConstraints gridBagConstraints53 = new GridBagConstraints();
			gridBagConstraints53.gridx = 0;
			gridBagConstraints53.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints53.insets = new java.awt.Insets(12, 12, 0, 0);
			gridBagConstraints53.gridy = 0;
			jLabelRmRobotsDB = new JLabel();
			jLabelRmRobotsDB.setText("Robots DB:");
			jPanelRemoveRobots = new JPanel();
			jPanelRemoveRobots.setLayout(new GridBagLayout());
			jPanelRemoveRobots.add(jLabelRmRobotsDB, gridBagConstraints53);
			jPanelRemoveRobots.add(getJTextFieldRmRobotsDB(), gridBagConstraints54);
			jPanelRemoveRobots
					.add(getJButtonRmRobotsDbSelect(), gridBagConstraints55);
			jPanelRemoveRobots.add(jLabelRmRobotsMaxViewTime, gridBagConstraints57);
			jPanelRemoveRobots.add(getJTextFieldRmRobotsMaxViewTime(),
					gridBagConstraints58);
			jPanelRemoveRobots.add(jLabelRmRobotsOutputExtension, gridBagConstraints59);
			jPanelRemoveRobots.add(getJTextFieldRmRobotsOutputExtension(), gridBagConstraints60);
		}
		return jPanelRemoveRobots;
	}

	/**
	 * This method initializes jPanelSessionFilter
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSessionFilter() {
		if (jPanelSessionFilter == null) {
			GridBagConstraints gridBagConstraints72 = new GridBagConstraints();
			gridBagConstraints72.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints72.gridy = 3;
			gridBagConstraints72.weightx = 1.0;
			gridBagConstraints72.weighty = 1.0;
			gridBagConstraints72.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints72.gridx = 1;
			GridBagConstraints gridBagConstraints71 = new GridBagConstraints();
			gridBagConstraints71.gridx = 0;
			gridBagConstraints71.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints71.insets = new java.awt.Insets(11, 0, 0, 0);
			gridBagConstraints71.gridy = 3;
			jLabelSessionFilterPath = new JLabel();
			jLabelSessionFilterPath.setText("Filter path:");
			GridBagConstraints gridBagConstraints70 = new GridBagConstraints();
			gridBagConstraints70.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints70.gridy = 4;
			gridBagConstraints70.weightx = 1.0;
			gridBagConstraints70.insets = new java.awt.Insets(11, 12, 12, 12);
			gridBagConstraints70.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints70.gridx = 1;
			GridBagConstraints gridBagConstraints69 = new GridBagConstraints();
			gridBagConstraints69.gridx = 0;
			gridBagConstraints69.insets = new java.awt.Insets(11, 12, 12, 0);
			gridBagConstraints69.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints69.gridy = 4;
			jLabelSessionFilterOutputExtension = new JLabel();
			jLabelSessionFilterOutputExtension.setText("Output extension:");
			GridBagConstraints gridBagConstraints56 = new GridBagConstraints();
			gridBagConstraints56.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints56.gridy = 2;
			gridBagConstraints56.weightx = 1.0;
			gridBagConstraints56.weighty = 1.0;
			gridBagConstraints56.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints56.gridx = 1;
			GridBagConstraints gridBagConstraints52 = new GridBagConstraints();
			gridBagConstraints52.gridx = 0;
			gridBagConstraints52.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints52.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints52.gridy = 2;
			jLabelSessionFilterHostName = new JLabel();
			jLabelSessionFilterHostName.setText("Filter host names:");
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints51.gridy = 1;
			gridBagConstraints51.weightx = 1.0;
			gridBagConstraints51.weighty = 1.0;
			gridBagConstraints51.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints51.gridx = 1;
			GridBagConstraints gridBagConstraints50 = new GridBagConstraints();
			gridBagConstraints50.gridx = 0;
			gridBagConstraints50.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints50.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints50.gridy = 1;
			jLabelSessionFilterHostIp = new JLabel();
			jLabelSessionFilterHostIp.setText("Filter host IPs:");
			GridBagConstraints gridBagConstraints49 = new GridBagConstraints();
			//gridBagConstraints49.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints49.anchor = GridBagConstraints.WEST;
			gridBagConstraints49.gridy = 0;
			gridBagConstraints49.weightx = 1.0;
			gridBagConstraints49.insets = new java.awt.Insets(12, 12, 0, 12);
			gridBagConstraints49.gridx = 1;
			GridBagConstraints gridBagConstraints48 = new GridBagConstraints();
			gridBagConstraints48.gridx = 0;
			gridBagConstraints48.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints48.insets = new java.awt.Insets(12, 12, 0, 0);
			gridBagConstraints48.gridy = 0;
			jLabelSessionFilterRepeatedRequests = new JLabel();
			jLabelSessionFilterRepeatedRequests.setText("Filter repeated requests:");
			jPanelSessionFilter = new JPanel();
			jPanelSessionFilter.setLayout(new GridBagLayout());
			jPanelSessionFilter.add(jLabelSessionFilterRepeatedRequests,
					gridBagConstraints48);
			jPanelSessionFilter.add(getJComboBoxSessionFilterRepeatedRequests(),
					gridBagConstraints49);
			jPanelSessionFilter.add(jLabelSessionFilterHostIp, gridBagConstraints50);
			jPanelSessionFilter.add(getJScrollPaneSessionFilterHostIP(),
					gridBagConstraints51);
			jPanelSessionFilter
					.add(jLabelSessionFilterHostName, gridBagConstraints52);
			jPanelSessionFilter.add(getJScrollPaneSessionFilterHostName(),
					gridBagConstraints56);
			jPanelSessionFilter.add(jLabelSessionFilterOutputExtension,
					gridBagConstraints69);
			jPanelSessionFilter.add(getJTextFieldSessionFilterOutputExtension(),
					gridBagConstraints70);
			jPanelSessionFilter.add(jLabelSessionFilterPath, gridBagConstraints71);
			jPanelSessionFilter.add(getJScrollPaneSessionFilterPath(),
					gridBagConstraints72);
		}
		return jPanelSessionFilter;
	}

	/**
	 * This method initializes jPanelSessionizer
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelSessionizer() {
		if (jPanelSessionizer == null) {
			GridBagConstraints gridBagConstraints47 = new GridBagConstraints();
			gridBagConstraints47.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints47.gridy = 6;
			gridBagConstraints47.weightx = 1.0;
			gridBagConstraints47.insets = new java.awt.Insets(11, 12, 12, 12);
			gridBagConstraints47.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gridBagConstraints47.weighty = 1.0D;
			gridBagConstraints47.gridx = 1;
			GridBagConstraints gridBagConstraints46 = new GridBagConstraints();
			gridBagConstraints46.gridx = 0;
			gridBagConstraints46.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints46.insets = new java.awt.Insets(11, 12, 12, 0);
			gridBagConstraints46.gridy = 6;
			jLabelSessionizerOutputExtension = new JLabel();
			jLabelSessionizerOutputExtension.setText("Output extension:");
			GridBagConstraints gridBagConstraints45 = new GridBagConstraints();
			//gridBagConstraints45.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints45.anchor = GridBagConstraints.WEST;
			gridBagConstraints45.gridy = 5;
			gridBagConstraints45.weightx = 1.0;
			gridBagConstraints45.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints45.gridx = 1;
			GridBagConstraints gridBagConstraints44 = new GridBagConstraints();
			gridBagConstraints44.gridx = 0;
			gridBagConstraints44.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints44.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints44.gridy = 5;
			jLabelSessionizerForeignReferrerStartsSession = new JLabel();
			jLabelSessionizerForeignReferrerStartsSession
					.setText("New session on foreign referrer:");
			GridBagConstraints gridBagConstraints43 = new GridBagConstraints();
			gridBagConstraints43.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints43.gridy = 4;
			gridBagConstraints43.weightx = 1.0;
			gridBagConstraints43.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints43.gridx = 1;
			GridBagConstraints gridBagConstraints42 = new GridBagConstraints();
			gridBagConstraints42.gridx = 0;
			gridBagConstraints42.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints42.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints42.gridy = 4;
			jLabelSessionizerQueryReferrerName = new JLabel();
			jLabelSessionizerQueryReferrerName.setText("Referrer name in query:");
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			//gridBagConstraints41.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints41.anchor = GridBagConstraints.WEST;
			gridBagConstraints41.gridy = 3;
			gridBagConstraints41.weightx = 1.0;
			gridBagConstraints41.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints41.gridx = 1;
			GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
			gridBagConstraints40.gridx = 0;
			gridBagConstraints40.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints40.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints40.gridy = 3;
			jLabelSessionizerInsertReferrerHits = new JLabel();
			jLabelSessionizerInsertReferrerHits.setText("Insert referrer hits:");
			GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
			gridBagConstraints39.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints39.gridy = 2;
			gridBagConstraints39.weightx = 1.0;
			gridBagConstraints39.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints39.gridx = 1;
			GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
			gridBagConstraints38.gridx = 0;
			gridBagConstraints38.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints38.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints38.gridy = 2;
			jLabelSessionizerIdCookie = new JLabel();
			jLabelSessionizerIdCookie.setText("ID cookie:");
			GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
			gridBagConstraints37.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints37.gridy = 1;
			gridBagConstraints37.weightx = 1.0;
			gridBagConstraints37.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints37.gridx = 1;
			GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
			gridBagConstraints36.gridx = 0;
			gridBagConstraints36.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints36.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints36.gridy = 1;
			jLabelSessionizerSeparator = new JLabel();
			jLabelSessionizerSeparator.setText("Session-ID separator:");
			GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
			gridBagConstraints35.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints35.gridy = 0;
			gridBagConstraints35.weightx = 1.0;
			gridBagConstraints35.insets = new java.awt.Insets(12, 12, 0, 12);
			gridBagConstraints35.gridx = 1;
			GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
			gridBagConstraints34.gridx = 0;
			gridBagConstraints34.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints34.insets = new java.awt.Insets(12, 12, 0, 0);
			gridBagConstraints34.gridy = 0;
			jLabelSessionizerMaxPageViewTime = new JLabel();
			jLabelSessionizerMaxPageViewTime.setText("Max. page view time:");
			jPanelSessionizer = new JPanel();
			jPanelSessionizer.setLayout(new GridBagLayout());
			jPanelSessionizer.add(jLabelSessionizerMaxPageViewTime,
					gridBagConstraints34);
			jPanelSessionizer.add(getJTextFieldSessionizerMaxPageViewTime(),
					gridBagConstraints35);
			jPanelSessionizer.add(jLabelSessionizerSeparator, gridBagConstraints36);
			jPanelSessionizer.add(getJTextFieldSessionizerSeparator(),
					gridBagConstraints37);
			jPanelSessionizer.add(jLabelSessionizerIdCookie, gridBagConstraints38);
			jPanelSessionizer.add(getJTextFieldSessionizerIdCookie(),
					gridBagConstraints39);
			jPanelSessionizer.add(jLabelSessionizerInsertReferrerHits,
					gridBagConstraints40);
			jPanelSessionizer.add(getJComboBoxSessionizerInsertReferrerHits(),
					gridBagConstraints41);
			jPanelSessionizer.add(jLabelSessionizerQueryReferrerName,
					gridBagConstraints42);
			jPanelSessionizer.add(getJTextFieldSessionizerQueryReferrerName(),
					gridBagConstraints43);
			jPanelSessionizer.add(jLabelSessionizerForeignReferrerStartsSession,
					gridBagConstraints44);
			jPanelSessionizer.add(
					getJComboBoxSessionizerForeignReferrerStartsSession(),
					gridBagConstraints45);
			jPanelSessionizer.add(jLabelSessionizerOutputExtension, gridBagConstraints46);
			jPanelSessionizer.add(getJTextFieldSessionizerOutputExtension(), gridBagConstraints47);
		}
		return jPanelSessionizer;
	}

	/**
	 * This method initializes jPanelStatistics
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelStatistics() {
		if (jPanelStatistics == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 2;
			gridBagConstraints4.insets = new java.awt.Insets(12, 5, 12, 12);
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints4.gridy = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new java.awt.Insets(11, 12, 12, 0);
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.weighty = 0.0D;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new java.awt.Insets(11, 12, 12, 0);
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints2.gridy = 1;
			jLabelStatisticsTemplateFile = new JLabel();
			jLabelStatisticsTemplateFile.setText("Template file:");
			jLabelStatisticsTemplateFile
					.setLabelFor(getJTextFieldStatisticsTemplateFile());
			jPanelStatistics = new JPanel();
			jPanelStatistics.setLayout(new GridBagLayout());
			jPanelStatistics.setEnabled(true);
			GridBagConstraints gbcJComboBoxStatisticsExport = new GridBagConstraints();
			//gbcJComboBoxStatisticsExport.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbcJComboBoxStatisticsExport.gridy = 0;
			gbcJComboBoxStatisticsExport.weightx = 1.0;
			gbcJComboBoxStatisticsExport.anchor = java.awt.GridBagConstraints.WEST;
			gbcJComboBoxStatisticsExport.insets = new java.awt.Insets(12, 12, 0, 0);
			gbcJComboBoxStatisticsExport.weighty = 0.0D;
			gbcJComboBoxStatisticsExport.gridx = 1;
			GridBagConstraints gbcJLabelStatisticsExport = new GridBagConstraints();
			gbcJLabelStatisticsExport.gridx = 0;
			gbcJLabelStatisticsExport.insets = new java.awt.Insets(12, 12, 0, 0);
			gbcJLabelStatisticsExport.anchor = java.awt.GridBagConstraints.EAST;
			gbcJLabelStatisticsExport.gridy = 0;
			jLabelStatisticsExport = new JLabel();
			jLabelStatisticsExport.setText("Export statistics:");
			jLabelStatisticsExport.setLabelFor(jComboBoxStatisticsExport);
			GridBagConstraints gbcStatisticsFilenameExtension = new GridBagConstraints();
			gbcStatisticsFilenameExtension.gridx = 0;
			gbcStatisticsFilenameExtension.insets = new java.awt.Insets(11, 12, 0, 0);
			gbcStatisticsFilenameExtension.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gbcStatisticsFilenameExtension.gridy = 2;
			GridBagConstraints gbcJTextFieldStatisticsFilenameExt = new GridBagConstraints();
			gbcJTextFieldStatisticsFilenameExt.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gbcJTextFieldStatisticsFilenameExt.gridy = 2;
			gbcJTextFieldStatisticsFilenameExt.weightx = 1.0;
			gbcJTextFieldStatisticsFilenameExt.insets = new java.awt.Insets(11, 12,
					0, 0);
			gbcJTextFieldStatisticsFilenameExt.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gbcJTextFieldStatisticsFilenameExt.weighty = 1.0D;
			gbcJTextFieldStatisticsFilenameExt.gridx = 1;
			jLabelStatisticsOutputExtension = new JLabel();
			jLabelStatisticsOutputExtension.setText("Output extension:");
			jLabelStatisticsOutputExtension
					.setLabelFor(getJTextFieldStatisticsOutputExtension());
			jPanelStatistics.add(jLabelStatisticsExport, gbcJLabelStatisticsExport);
			jPanelStatistics.add(getJTextFieldStatisticsOutputExtension(), gbcJTextFieldStatisticsFilenameExt);
			jPanelStatistics.add(jLabelStatisticsTemplateFile, gridBagConstraints2);
			jPanelStatistics.add(getJTextFieldStatisticsTemplateFile(), gridBagConstraints3);
			jPanelStatistics.add(getJButtonSelectStatisticsTemplateFile(), gridBagConstraints4);
			jPanelStatistics.add(jLabelStatisticsOutputExtension, gbcStatisticsFilenameExtension);
			jPanelStatistics.add(getJComboBoxStatisticsExport(), gbcJComboBoxStatisticsExport);
		}
		return jPanelStatistics;
	}

	/**
	 * This method initializes jPanelTaxonomyMapping
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelTaxonomyMapping() {
		if (jPanelTaxonomyMapping == null) {
			GridBagConstraints gridBagConstraints66 = new GridBagConstraints();
			gridBagConstraints66.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints66.gridy = 2;
			gridBagConstraints66.weightx = 1.0;
			gridBagConstraints66.insets = new java.awt.Insets(12, 12, 12, 12);
			gridBagConstraints66.gridx = 1;
			GridBagConstraints gridBagConstraints65 = new GridBagConstraints();
			gridBagConstraints65.gridx = 0;
			gridBagConstraints65.insets = new java.awt.Insets(11, 12, 12, 0);
			gridBagConstraints65.gridy = 2;
			jLabelTaxonomyMapOutputExtension = new JLabel();
			jLabelTaxonomyMapOutputExtension.setText("Output extension:");
			GridBagConstraints gridBagConstraints64 = new GridBagConstraints();
			gridBagConstraints64.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints64.gridy = 1;
			gridBagConstraints64.weightx = 1.0;
			gridBagConstraints64.weighty = 1.0;
			gridBagConstraints64.insets = new java.awt.Insets(11, 12, 0, 12);
			gridBagConstraints64.gridx = 1;
			GridBagConstraints gridBagConstraints63 = new GridBagConstraints();
			gridBagConstraints63.gridx = 0;
			gridBagConstraints63.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints63.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints63.gridy = 1;
			jLabelTaxonomyMapDefs = new JLabel();
			jLabelTaxonomyMapDefs.setText("Taxonomy definitions:");
			GridBagConstraints gridBagConstraints62 = new GridBagConstraints();
			gridBagConstraints62.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints62.gridy = 0;
			gridBagConstraints62.weightx = 1.0;
			gridBagConstraints62.insets = new java.awt.Insets(12, 12, 0, 12);
			gridBagConstraints62.gridx = 1;
			GridBagConstraints gridBagConstraints61 = new GridBagConstraints();
			gridBagConstraints61.gridx = 0;
			gridBagConstraints61.insets = new java.awt.Insets(12, 12, 0, 0);
			gridBagConstraints61.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints61.gridy = 0;
			jLabelTaxonoyMapLogfile = new JLabel();
			jLabelTaxonoyMapLogfile.setText("Log file:");
			jPanelTaxonomyMapping = new JPanel();
			jPanelTaxonomyMapping.setLayout(new GridBagLayout());
			jPanelTaxonomyMapping.add(jLabelTaxonoyMapLogfile, gridBagConstraints61);
			jPanelTaxonomyMapping.add(getJTextFieldTaxonomyMapLogfile(),
					gridBagConstraints62);
			jPanelTaxonomyMapping.add(jLabelTaxonomyMapDefs, gridBagConstraints63);
			jPanelTaxonomyMapping.add(getJScrollPaneTaxonomyMapDefs(),
					gridBagConstraints64);
			jPanelTaxonomyMapping.add(jLabelTaxonomyMapOutputExtension,
					gridBagConstraints65);
			jPanelTaxonomyMapping.add(getJTextFieldTaxonomyMapOutputExtension(),
					gridBagConstraints66);
		}
		return jPanelTaxonomyMapping;
	}

	/**
	 * This method initializes jPanelConversion
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelTransformation() {
		if (jPanelTransformation == null) {
			jPanelTransformation = new JPanel();
			jPanelTransformation.setLayout(new GridBagLayout());
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 2;
			gridBagConstraints21.insets = new java.awt.Insets(11, 5, 12, 12);
			gridBagConstraints21.gridy = 2;
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints20.gridy = 2;
			gridBagConstraints20.weightx = 1.0;
			gridBagConstraints20.insets = new java.awt.Insets(11, 12, 12, 0);
			gridBagConstraints20.gridx = 1;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.insets = new java.awt.Insets(11, 12, 12, 0);
			gridBagConstraints19.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints19.gridy = 2;
			jLabelTransformSessionVectorFile = new JLabel();
			jLabelTransformSessionVectorFile.setText("Session vector file:");
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints18.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints18.gridy = 1;
			gridBagConstraints18.weightx = 1.0;
			gridBagConstraints18.gridx = 1;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints17.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints17.gridy = 1;
			jLabelTransformTemplate = new JLabel();
			jLabelTransformTemplate.setText("Template:");
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			//gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints16.anchor = GridBagConstraints.WEST;
			gridBagConstraints16.insets = new java.awt.Insets(12, 12, 0, 0);
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints15.insets = new java.awt.Insets(12, 12, 0, 0);
			gridBagConstraints15.gridy = 0;
			jLabelTransformMode = new JLabel();
			jLabelTransformMode.setText("Transformation mode:");
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints14.gridy = 3;
			gridBagConstraints14.weightx = 1.0;
			gridBagConstraints14.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gridBagConstraints14.weighty = 1.0D;
			gridBagConstraints14.gridx = 1;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.anchor = java.awt.GridBagConstraints.NORTHEAST;
			gridBagConstraints13.insets = new java.awt.Insets(11, 12, 0, 0);
			gridBagConstraints13.gridy = 3;
			jLabelTransformOutputExtension = new JLabel();
			jLabelTransformOutputExtension.setText("Output extension:");
			jPanelTransformation.add(getJButtonTransformSessionVectorFileSelect(), gridBagConstraints21);
			jPanelTransformation.add(jLabelTransformMode, gridBagConstraints15);
			jPanelTransformation.add(getJComboBoxConversionTransformMode(),
					gridBagConstraints16);
			jPanelTransformation.add(jLabelTransformTemplate, gridBagConstraints17);
			jPanelTransformation.add(getJTextFieldTransformTemplate(),
					gridBagConstraints18);
			jPanelTransformation.add(jLabelTransformSessionVectorFile, gridBagConstraints19);
			jPanelTransformation.add(getJTextFieldTransformSessonVectorFile(), gridBagConstraints20);
			jPanelTransformation.add(jLabelTransformOutputExtension, gridBagConstraints13);
			jPanelTransformation.add(getJTextFieldTransformOutputExtension(), gridBagConstraints14);
		}
		return jPanelTransformation;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneLogfiles() {
		if (jScrollPaneLogfiles == null) {
			jScrollPaneLogfiles = new JScrollPane();
			jScrollPaneLogfiles.setViewportView(getJListLogfiles());
		}
		return jScrollPaneLogfiles;
	}

	/**
	 * This method initializes jScrollPaneLogfilterDuplicatesTimeout
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneLogfilterDuplicatesExtensions() {
		if (jScrollPaneLogfilterDuplicatesExtensions == null) {
			jScrollPaneLogfilterDuplicatesExtensions = new JScrollPane();
			jScrollPaneLogfilterDuplicatesExtensions
					.setViewportView(getJTextAreaLogfilterDuplicatesExtensions());
		}
		return jScrollPaneLogfilterDuplicatesExtensions;
	}

	/**
	 * This method initializes jScrollPaneLogfilterHosts
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneLogfilterHosts() {
		if (jScrollPaneLogfilterHosts == null) {
			jScrollPaneLogfilterHosts = new JScrollPane();
			jScrollPaneLogfilterHosts.setViewportView(getJTextAreaLogfilterHosts());
		}
		return jScrollPaneLogfilterHosts;
	}

	/**
	 * This method initializes jScrollPaneLogfilterPath
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneLogfilterPath() {
		if (jScrollPaneLogfilterPath == null) {
			jScrollPaneLogfilterPath = new JScrollPane();
			jScrollPaneLogfilterPath.setViewportView(getJTextAreaLogfilterPath());
		}
		return jScrollPaneLogfilterPath;
	}

	/**
	 * This method initializes jScrollPaneLogfilterStatusCodes
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneLogfilterStatusCodes() {
		if (jScrollPaneLogfilterStatusCodes == null) {
			jScrollPaneLogfilterStatusCodes = new JScrollPane();
			jScrollPaneLogfilterStatusCodes
					.setViewportView(getJTextAreaLogfilterStatusCodes());
		}
		return jScrollPaneLogfilterStatusCodes;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneServerFamily() {
		if (jScrollPaneServerFamily == null) {
			jScrollPaneServerFamily = new JScrollPane();
			jScrollPaneServerFamily.setViewportView(getJTextAreaServerFamily());
		}
		return jScrollPaneServerFamily;
	}

	/**
	 * This method initializes jScrollPaneSessionFilterHostIP
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneSessionFilterHostIP() {
		if (jScrollPaneSessionFilterHostIP == null) {
			jScrollPaneSessionFilterHostIP = new JScrollPane();
			jScrollPaneSessionFilterHostIP
					.setViewportView(getJTextAreaSessionFilterHostIP());
		}
		return jScrollPaneSessionFilterHostIP;
	}

	/**
	 * This method initializes jScrollPaneSessionFilterHostName
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneSessionFilterHostName() {
		if (jScrollPaneSessionFilterHostName == null) {
			jScrollPaneSessionFilterHostName = new JScrollPane();
			jScrollPaneSessionFilterHostName
					.setViewportView(getJTextAreaSessionFilterHostName());
		}
		return jScrollPaneSessionFilterHostName;
	}

	/**
	 * This method initializes jScrollPaneSessionFilterPath
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneSessionFilterPath() {
		if (jScrollPaneSessionFilterPath == null) {
			jScrollPaneSessionFilterPath = new JScrollPane();
			jScrollPaneSessionFilterPath
					.setViewportView(getJTextAreaSessionFilterPath());
		}
		return jScrollPaneSessionFilterPath;
	}

	/**
	 * This method initializes jScrollPaneTaxonomyMapDefs
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPaneTaxonomyMapDefs() {
		if (jScrollPaneTaxonomyMapDefs == null) {
			jScrollPaneTaxonomyMapDefs = new JScrollPane();
			jScrollPaneTaxonomyMapDefs.setViewportView(getJTextAreaTaxonomyMapDefs());
		}
		return jScrollPaneTaxonomyMapDefs;
	}

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("General", null, getJPanelGlobal(), null);
			jTabbedPane.addTab("Logfiles", null, getJPanelLogfiles(), null);
			jTabbedPane.addTab("DNS Lookup", null, getJPanelDnsLookup(), null);
			jTabbedPane.addTab("Statistics", null, getJPanelStatistics(), null);
			jTabbedPane.addTab("Anonymizer", null, getJPanelAnonymizer(), null);
			jTabbedPane.addTab("Conversion", null, getJPanelConversion(), null);
			jTabbedPane.addTab("Transformation", null, getJPanelTransformation(),
					null);
			jTabbedPane.addTab("Log Filter", null, getJPanelLogFilter(), null);
			jTabbedPane.addTab("Sessionizer", null, getJPanelSessionizer(), null);
			jTabbedPane
					.addTab("Session Filter", null, getJPanelSessionFilter(), null);
			jTabbedPane.addTab("Remove Robots", null, getJPanelRemoveRobots(), null);
			jTabbedPane.addTab("Taxonomy Mapping", null, getJPanelTaxonomyMapping(),
					null);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jTextAreaLogfilterDuplicatesExtensions
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaLogfilterDuplicatesExtensions() {
		if (jTextAreaLogfilterDuplicatesExtensions == null) {
			jTextAreaLogfilterDuplicatesExtensions = new JTextArea();
		}
		return jTextAreaLogfilterDuplicatesExtensions;
	}

	/**
	 * This method initializes jTextAreaLogfilterHosts
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaLogfilterHosts() {
		if (jTextAreaLogfilterHosts == null) {
			jTextAreaLogfilterHosts = new JTextArea();
		}
		return jTextAreaLogfilterHosts;
	}

	/**
	 * This method initializes jTextAreaLogfilterPath
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaLogfilterPath() {
		if (jTextAreaLogfilterPath == null) {
			jTextAreaLogfilterPath = new JTextArea();
		}
		return jTextAreaLogfilterPath;
	}

	/**
	 * This method initializes jTextAreaLogfilterStatusCodes
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaLogfilterStatusCodes() {
		if (jTextAreaLogfilterStatusCodes == null) {
			jTextAreaLogfilterStatusCodes = new JTextArea();
		}
		return jTextAreaLogfilterStatusCodes;
	}

	/**
	 * This method initializes jTextAreaServerFamily
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaServerFamily() {
		if (jTextAreaServerFamily == null) {
			jTextAreaServerFamily = new JTextArea();
		}
		return jTextAreaServerFamily;
	}

	/**
	 * This method initializes jTextAreaSessionFilterHostIP
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaSessionFilterHostIP() {
		if (jTextAreaSessionFilterHostIP == null) {
			jTextAreaSessionFilterHostIP = new JTextArea();
		}
		return jTextAreaSessionFilterHostIP;
	}

	/**
	 * This method initializes jTextAreaSessionFilterHostName
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaSessionFilterHostName() {
		if (jTextAreaSessionFilterHostName == null) {
			jTextAreaSessionFilterHostName = new JTextArea();
		}
		return jTextAreaSessionFilterHostName;
	}

	/**
	 * This method initializes jTextAreaSessionFilterPath
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaSessionFilterPath() {
		if (jTextAreaSessionFilterPath == null) {
			jTextAreaSessionFilterPath = new JTextArea();
		}
		return jTextAreaSessionFilterPath;
	}

	/**
	 * This method initializes jTextAreaTaxonomyMapDefs
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaTaxonomyMapDefs() {
		if (jTextAreaTaxonomyMapDefs == null) {
			jTextAreaTaxonomyMapDefs = new JTextArea();
		}
		return jTextAreaTaxonomyMapDefs;
	}

	/**
	 * This method initializes jTextFieldAnonymizerKeyFile
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldAnonymizerKeyFile() {
		if (jTextFieldAnonymizerKeyFile == null) {
			jTextFieldAnonymizerKeyFile = new JTextField();
		}
		return jTextFieldAnonymizerKeyFile;
	}

	/**
	 * This method initializes jTextFieldAnonymizerFilenameExtension
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldAnonymizerOutputExtension() {
		if (jTextFieldAnonymizerOutputExtension == null) {
			jTextFieldAnonymizerOutputExtension = new JTextField();
		}
		return jTextFieldAnonymizerOutputExtension;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldConversionOutputExtension() {
		if (jTextFieldConversionOutputExtension == null) {
			jTextFieldConversionOutputExtension = new JTextField();
			jTextFieldConversionOutputExtension.setEnabled(false);
		}
		return jTextFieldConversionOutputExtension;
	}

	/**
	 * This method initializes jTextFieldDnsLookupOutputExtension
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldDnsLookupOutputExtension() {
		if (jTextFieldDnsLookupOutputExtension == null) {
			jTextFieldDnsLookupOutputExtension = new JTextField();
		}
		return jTextFieldDnsLookupOutputExtension;
	}

	/**
	 * This method initializes jTextFieldDnsReverseLookupCache
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldDnsReverseLookupCache() {
		if (jTextFieldDnsReverseLookupCache == null) {
			jTextFieldDnsReverseLookupCache = new JTextField();
		}
		return jTextFieldDnsReverseLookupCache;
	}

	/**
	 * This method initializes jTextFieldDomain
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldDomain() {
		if (jTextFieldDomain == null) {
			jTextFieldDomain = new JTextField();
		}
		return jTextFieldDomain;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldHTMLTemplateDir() {
		if (jTextFieldHTMLTemplateDir == null) {
			jTextFieldHTMLTemplateDir = new JTextField();
		}
		return jTextFieldHTMLTemplateDir;
	}

	/**
	 * This method initializes jTextFieldLogfilterDuplicateTimeout
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldLogfilterDuplicatesTimeout() {
		if (jTextFieldLogfilterDuplicatesTimeout == null) {
			jTextFieldLogfilterDuplicatesTimeout = new JTextField();
		}
		return jTextFieldLogfilterDuplicatesTimeout;
	}

	/**
	 * This method initializes jTextFieldLogfilterOutputExtension
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldLogfilterOutputExtension() {
		if (jTextFieldLogfilterOutputExtension == null) {
			jTextFieldLogfilterOutputExtension = new JTextField();
		}
		return jTextFieldLogfilterOutputExtension;
	}

	/*
	 * public WUMprepConfiguration getConfiguration() { return null; }
	 */
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldOutputDirectory() {
		if (jTextFieldOutputDirectory == null) {
			jTextFieldOutputDirectory = new JTextField();
		}
		return jTextFieldOutputDirectory;
	}

	/**
	 * This method initializes jTextFieldRmRobotsDB
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldRmRobotsDB() {
		if (jTextFieldRmRobotsDB == null) {
			jTextFieldRmRobotsDB = new JTextField();
		}
		return jTextFieldRmRobotsDB;
	}

	/**
	 * This method initializes jTextFieldRmRobotsMaxViewTime
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldRmRobotsMaxViewTime() {
		if (jTextFieldRmRobotsMaxViewTime == null) {
			jTextFieldRmRobotsMaxViewTime = new JTextField();
		}
		return jTextFieldRmRobotsMaxViewTime;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldRmRobotsOutputExtension() {
		if (jTextFieldRmRobotsOutputExtension == null) {
			jTextFieldRmRobotsOutputExtension = new JTextField();
		}
		return jTextFieldRmRobotsOutputExtension;
	}

	/**
	 * This method initializes jTextFieldSessionFilterOutputExtension
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldSessionFilterOutputExtension() {
		if (jTextFieldSessionFilterOutputExtension == null) {
			jTextFieldSessionFilterOutputExtension = new JTextField();
		}
		return jTextFieldSessionFilterOutputExtension;
	}

	/**
	 * This method initializes jTextFieldSessionizerIdCookie
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldSessionizerIdCookie() {
		if (jTextFieldSessionizerIdCookie == null) {
			jTextFieldSessionizerIdCookie = new JTextField();
		}
		return jTextFieldSessionizerIdCookie;
	}

	/**
	 * This method initializes jTextFieldSessionizerMaxPageViewTime
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldSessionizerMaxPageViewTime() {
		if (jTextFieldSessionizerMaxPageViewTime == null) {
			jTextFieldSessionizerMaxPageViewTime = new JTextField();
		}
		return jTextFieldSessionizerMaxPageViewTime;
	}

	/**
	 * This method initializes jTextFieldSessionizerOutputExtension
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldSessionizerOutputExtension() {
		if (jTextFieldSessionizerOutputExtension == null) {
			jTextFieldSessionizerOutputExtension = new JTextField();
		}
		return jTextFieldSessionizerOutputExtension;
	}

	/**
	 * This method initializes jTextFieldSessionizerQueryReferrerName
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldSessionizerQueryReferrerName() {
		if (jTextFieldSessionizerQueryReferrerName == null) {
			jTextFieldSessionizerQueryReferrerName = new JTextField();
		}
		return jTextFieldSessionizerQueryReferrerName;
	}

	/**
	 * This method initializes jTextFieldSessionizerSeparator
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldSessionizerSeparator() {
		if (jTextFieldSessionizerSeparator == null) {
			jTextFieldSessionizerSeparator = new JTextField();
		}
		return jTextFieldSessionizerSeparator;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldStatisticsOutputExtension() {
		if (jTextFieldStatisticsOutputExtension == null) {
			jTextFieldStatisticsOutputExtension = new JTextField();
		}
		return jTextFieldStatisticsOutputExtension;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldStatisticsTemplateFile() {
		if (jTextFieldStatisticsTemplateFile == null) {
			jTextFieldStatisticsTemplateFile = new JTextField();
		}
		return jTextFieldStatisticsTemplateFile;
	}

	/**
	 * This method initializes jTextFieldTaxonomyMapLogFile
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldTaxonomyMapLogfile() {
		if (jTextFieldTaxonomyMapLogfile == null) {
			jTextFieldTaxonomyMapLogfile = new JTextField();
		}
		return jTextFieldTaxonomyMapLogfile;
	}

	/**
	 * This method initializes jTextFieldTaxonomyMapOutputExtension
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldTaxonomyMapOutputExtension() {
		if (jTextFieldTaxonomyMapOutputExtension == null) {
			jTextFieldTaxonomyMapOutputExtension = new JTextField();
		}
		return jTextFieldTaxonomyMapOutputExtension;
	}

	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldTransformOutputExtension() {
		if (jTextFieldTransformOutputExtension == null) {
			jTextFieldTransformOutputExtension = new JTextField();
		}
		return jTextFieldTransformOutputExtension;
	}

	/**
	 * This method initializes jTextFieldTransformSessonVectorFile
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldTransformSessonVectorFile() {
		if (jTextFieldTransformSessonVectorFile == null) {
			jTextFieldTransformSessonVectorFile = new JTextField();
		}
		return jTextFieldTransformSessonVectorFile;
	}

	/**
	 * This method initializes jTextFieldTransformTemplate
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextFieldTransformTemplate() {
		if (jTextFieldTransformTemplate == null) {
			jTextFieldTransformTemplate = new JTextField();
		}
		return jTextFieldTransformTemplate;
	}

	private void initialize() {
		try {
			controller = new WUMprepConfigEditorController(this);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints79 = new GridBagConstraints();
		gridBagConstraints79.insets = new java.awt.Insets(5, 5, 5, 5);
		gridBagConstraints79.fill = java.awt.GridBagConstraints.BOTH;
		this.add(getJTabbedPane(), gridBagConstraints79);
	}

	private javax.swing.filechooser.FileFilter getLogFileFilter() {
		if (logFileFilter == null) {
			logFileFilter = new FileFilter() {
				public boolean accept(File f) {
					if (f.getAbsolutePath().matches(".*\\.log"))
						return true;
					if (f.isDirectory())
						return true;

					return false;
				}
	
				public String getDescription() {
					return "Web log files (*.log)";
				}
			};
		}
		return this.logFileFilter;
	}

}

// @jve:decl-index=0:visual-constraint="6,6"
