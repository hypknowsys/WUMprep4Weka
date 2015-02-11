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
 * WUMprepConfigurationCustomizer.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */

/*
 * TODO The dialog for saving changes does not appear reliably
 */

package org.hypknowsys.wumprep4weka.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Customizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.hypknowsys.util.SpringUtilities;
import org.hypknowsys.wumprep.WUMprepWrapper;
import org.hypknowsys.wumprep.config.WUMprepConfigSection;
import org.hypknowsys.wumprep.config.WUMprepConfigSetting;
import org.hypknowsys.wumprep.config.WUMprepConfigValue;
import org.hypknowsys.wumprep.config.WUMprepConfigValueList;
import org.hypknowsys.wumprep.config.WUMprepConfiguration;

/**
 * Provides a {@link java.beans.Customizer} GUI for WUMprep configuration files
 * represented by a {@link WUMprepConfiguration} object.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepConfigurationCustomizer.java,v 1.2 2005/09/14 19:42:57
 *          cpohle Exp $
 */
public class WUMprepConfigurationCustomizer extends JPanel implements
		FocusListener, Customizer, PropertyChangeListener, ItemListener {

	/**
	 * Implements a JTextArea that is transparently wrapped into a JScrollPane.
	 * While <tt>ScrollableJTextArea</tt> inherits from JScrollPane, it
	 * overwrites relevant methods and redirects their calls to the embedded
	 * JTextArea.
	 */
	private class ScrollableJTextArea extends JScrollPane {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7080090909116153845L;

		/**
		 * The text area
		 */
		JTextArea ta = new JTextArea();

		/**
		 * Creates a new <tt>ScrollableJTextArea</tt>.
		 */
		public ScrollableJTextArea() {
			super();

			JViewport vp = new JViewport();
			vp.setView(ta);

			this.setViewport(vp);
		}

		@Override
		public synchronized void addFocusListener(FocusListener arg0) {
			ta.addFocusListener(arg0);
		}

		@Override
		public String getName() {
			return ta.getName();
		}

		/**
		 * Returns the text contained in this <tt>ScrollableJTextArea</tt>.
		 * 
		 * @return the Text
		 * @see javax.swing.text.JTextComponent#getText()
		 */
		public String getText() {
			return ta.getText();
		}

		@Override
		public synchronized void removeFocusListener(FocusListener arg0) {
			ta.removeFocusListener(arg0);
		}

		@Override
		public void setName(String arg0) {
			ta.setName(arg0);
		}

		/**
		 * Sets the text contained in this <tt>ScrollableJTextArea</tt>.
		 * 
		 * @param text
		 *          The text to display.
		 * @see javax.swing.text.JTextComponent#setText(java.lang.String)
		 */
		public void setText(String text) {
			ta.setText(text);
		}
	}

	/**
	 * The resource to be used as Icon for file selector buttons.
	 */
	@SuppressWarnings("unused")
	private static final String FOLDER_ICON = "org/hypknowsys/wumprep/config/resources/folder16.gif";

	/**
	 * The maximum width for input/editor components like <tt>JTextField</tt>s.
	 */
	private static final int MAX_INPUT_WIDTH = 50;

	/**
	 * SUID for serialized objects.
	 */
	private static final long serialVersionUID = -6325686487245398864L;

	/**
	 * Helper function for loading GUI elements.
	 * 
	 * @param path
	 *          URL of the image resource to load
	 * @return The image loaded or null if load failed.
	 */
	@SuppressWarnings("unused")
	private static Image loadImage(String path) {
		Image pic = null;
		java.net.URL imageURL = ClassLoader.getSystemResource(path);
		if (imageURL == null) {
			System.err.println("Warning: unable to load " + path);
		} else {
			pic = Toolkit.getDefaultToolkit().getImage(imageURL);
		}
		return pic;
	}

	/**
	 * The <tt>WUMprepConfiguration</tt> exposed by this customizer.
	 */
	private WUMprepConfiguration config = null;

	/**
	 * Holds the JPanel displaying context help (the right part of the
	 * customizer's split pane).
	 */
	private JPanel contextHelpJPanel = null;

	/**
	 * Holds the <tt>JScrollPane</tt> wrapping the {@link #contextHelpJPanel}.
	 */
	private JScrollPane contextHelpJScrollPane = null;

	/**
	 * Holds the <tt>JTextArea</tt> displaying context help;
	 */
	private JTextArea contextHelpJTextArea = null;

	/** Holds the configuration editor component that currently has the focus */
	private Component currentFocus;

	/**
	 * Holds references to the customizer's GUI components for editing the
	 * configuration settings.
	 */
	private HashMap<String, JComponent> custComponents = null;

	/**
	 * Holds the component that contains the customization controls (<tt>JTextField</tt>s
	 * etc.), i.e. the left part of the customizer's split pane.
	 */
	private JComponent customizationJComponent = null;

	/**
	 * Holds a default <tt>WUMprepConfiguration</tt>. The context help texts
	 * are taken from this file, because a user-provided WUMprep configuration
	 * file might contain different or no comments at all.
	 */
	private WUMprepConfiguration defaultConfig = null;

	/**
	 * The <tt>JFileChooser</tt> used for loading and saving WUMprep
	 * configuration files.
	 */
	final JFileChooser fileChooser = new JFileChooser();

	/**
	 * Holds the <tt>JSplitPane</tt> containing the
	 * {@link #customizationJComponent} on its left and {@link #contextHelpJPanel}
	 * on its right.
	 */
	private JSplitPane jSplitPane = null;

	/**
	 * Set to a component's current content whenever it gains focus.
	 */
	private HashMap<String, String> oldValue;

	/**
	 * Indicates whether there are changes to the {@link #config} that have not
	 * been saved to the current WUMprep config file.
	 */
	private boolean unsavedChanges;

	/**
	 * Holds a list containing the subset of settings in a wumprep.conf file
	 * relevant for WUMprep4Weka
	 */
	private HashSet<String> wumprep4wekaConfigSettings = null;

	/**
	 * Holds the <tt>JCheckBox</tt> indicating whether only WUMprep4Weka related
	 * settings should be displayed
	 */
	private JCheckBox wumprep4wekaSettingsOnlyJcb = null;

	/**
	 * Creates a new and empty <tt>WUMprepConfigurationCustomizer</tt>
	 */
	public WUMprepConfigurationCustomizer() {
		super();
		initialize();
	}

	/**
	 * Creates a new <tt>WUMprepConfigurationCustomizer</tt> and initializes it
	 * to the given {@link WUMprepConfiguration}.
	 * 
	 * @param obj
	 *          Must be an object of {@link WUMprepConfiguration}.
	 */
	public WUMprepConfigurationCustomizer(Object obj) {
		super();
		initialize();
		setObject(obj);
	}

	/**
	 * This method knows and returns for every possible WUMprep configuration
	 * setting the right input method for use in customization dialogues.
	 * 
	 * @param name
	 *          The WUMprep config file's section and setting to be edited as
	 *          "section:setting"
	 * @return The JComponent to be used in a customization dialogue.
	 */
	private JComponent createComponent(String name) {
		JComponent comp = null;

		String[] settingInfo = name.split(":", 2);

		if (name.equals("global:operationMode")) {
			String[] options = { "File" };
			comp = new JComboBox(options);
			comp.setMaximumSize(new Dimension(MAX_INPUT_WIDTH, new Double(comp
					.getPreferredSize().getHeight()).intValue()));
			comp.setMinimumSize(new Dimension(MAX_INPUT_WIDTH, new Double(comp
					.getPreferredSize().getHeight()).intValue()));
			((JComboBox) comp).setSelectedIndex(0);
			comp.addFocusListener(this);

		} else if (name.equals("global:DNSlookups")) {
			String[] options = { "yes", "no" };
			comp = new JComboBox(options);
			comp.setMaximumSize(new Dimension(MAX_INPUT_WIDTH, new Double(comp
					.getPreferredSize().getHeight()).intValue()));
			comp.setMinimumSize(new Dimension(MAX_INPUT_WIDTH, new Double(comp
					.getPreferredSize().getHeight()).intValue()));
			comp.addFocusListener(this);

		} else if (name.equals("global:inputLogTemplate")) {
			comp = new FilePropertyEditor("Select a logfile template");
			comp.addPropertyChangeListener(this);
			comp.addFocusListener(this);

		} else if (name.equals("global:outputDirectory")) {
			comp = new DirectoryPropertyEditor(
					"Select the directory any output files should go into");
			comp.addPropertyChangeListener(this);
			comp.addFocusListener(this);

		} else if (name.equals("global:htmlTemplateDir")) {
			comp = new DirectoryPropertyEditor(
					"Select the directory containing HTML template files");
			comp.addPropertyChangeListener(this);
			comp.addFocusListener(this);

		} else if (name.equals("global:inputLogs")) {
			comp = new FilePropertyEditor("Select the logfile to import");
			comp.addPropertyChangeListener(this);
			comp.addFocusListener(this);

		} else if (name.equals("anonymizerSettings:anonKeyFile")) {
			comp = new FilePropertyEditor(
					"Select a file to store the anonymization keys into");
			comp.addPropertyChangeListener(this);
			comp.addFocusListener(this);

		} else if (name.equals("mapTaxonomiesSettings:taxonoMapLog")) {
			comp = new FilePropertyEditor(
					"Select a file for saving the concept mapping log");
			comp.addPropertyChangeListener(this);
			comp.addFocusListener(this);

		} else if (name.equals("reverseLookupSettings:rLookupCacheFile")) {
			comp = new FilePropertyEditor("Select a file to be used as DNS cache");
			comp.addPropertyChangeListener(this);
			comp.addFocusListener(this);

		} else if (name.equals("rmRobotsSettings:rmRobotsDB")) {
			comp = new FilePropertyEditor("Select the list of known robots");
			comp.addPropertyChangeListener(this);
			comp.addFocusListener(this);

		} else if (name.equals("statisticsSettings:statisticsTemplateFile")) {
			comp = new FilePropertyEditor(
					"Select an HTML filename for saving the log statistics");
			comp.addPropertyChangeListener(this);
			comp.addFocusListener(this);

		} else if (name.equals("transformSettings:transformSessionVectorFile")) {
			comp = new FilePropertyEditor(
					"Select the file for saving the vectorized log");
			comp.addPropertyChangeListener(this);
			comp.addFocusListener(this);

		} else if (name.equals("transformSettings:transformTemplate")) {
			comp = new FilePropertyEditor(
					"Select the transformation destination template");
			comp.addPropertyChangeListener(this);
			comp.addFocusListener(this);

		} else {
			if (defaultConfig.getSection(settingInfo[0]).getSetting(settingInfo[1]) instanceof WUMprepConfigValue) {
				comp = new JTextField();
				comp.setMaximumSize(new Dimension(MAX_INPUT_WIDTH, JTextField.HEIGHT));
				comp.addFocusListener(this);
			} else {
				comp = new ScrollableJTextArea();
				comp.addFocusListener(this);
			}
		}

		custComponents.put(name, comp);
		comp.setName(name);

		return comp;
	}

	/**
	 * Whenever a component named according to the scheme documented in
	 * {@link #getComponent}, its name is used to retrieve and display the
	 * according context help.
	 * 
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		if (e.getComponent().getName() != null) {
			String compName = e.getComponent().getName();
			String[] settingInfo = compName.split(":", 2);

			setContextHelp(settingInfo[1], this.config.getSection(settingInfo[0])
					.getSetting(settingInfo[1]).getComment().getHelpText());

			currentFocus = e.getComponent();
		}
	}

	/**
	 * Whenever an editor component named according to scheme documented in
	 * {@link #getComponent(String)} looses focus, its current value is stored in
	 * the underlying {@link #config}.
	 * 
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		Component comp = e.getComponent();

		if (comp.getName() != null) {
			String[] settingInfo = comp.getName().split(":", 2);

			WUMprepConfigSetting setting = config.getSection(settingInfo[0])
					.getSetting(settingInfo[1]);

			if (!oldValue.get(comp.getName()).equals(getComponentValue(comp))) {
				unsavedChanges = true;
				this.firePropertyChange("", null, config);
			}

			if (setting instanceof WUMprepConfigValue) {
				((WUMprepConfigValue) setting).setValue(getComponentValue(e
						.getComponent().getName()));
			} else if (setting instanceof WUMprepConfigValueList) {
				((WUMprepConfigValueList) setting).setValues(getComponentValue(e
						.getComponent().getName()));
			} else {
				System.err.println("Unhandled focus lost event from: "
						+ e.getComponent().getName() + ", "
						+ e.getComponent().getClass().getSimpleName());
			}
		}
	}

	/**
	 * Get the <tt>JComponent</tt> holding the <tt>WUMprepConfiguration</tt>'s
	 * property <tt>name</tt> exposed by the customizer.
	 * <p>
	 * The JComponents holding property values are named according to the scheme
	 * <tt>section:setting</tt>, referring to the section and setting lable in
	 * the WUMprep configuration file, respectively. This naming occurs during
	 * each component's initialization in {@link #createComponent(String)} and
	 * {@link #getConfigFileSelectorJComponent()}.
	 * 
	 * @param name
	 *          The name of the <tt>JComponent</tt> to be retrieved according to
	 *          the schema described above.
	 * @return The component.
	 */
	private JComponent getComponent(String name) {
		JComponent comp = null;

		if (custComponents.containsKey(name))
			comp = custComponents.get(name);
		else
			comp = createComponent(name);

		return comp;
	}

	/**
	 * Get the current <tt>String</tt> value of a customization component (e.g.,
	 * a <tt>JTextField</tt>).
	 * 
	 * @param comp
	 *          The JComponent to get the value of.
	 * 
	 * @return The components current value.
	 */
	private String getComponentValue(Component comp) {
		String result = null;

		if (comp instanceof JTextField) {
			result = ((JTextField) comp).getText();
		} else if (comp instanceof JComboBox) {
			result = ((JComboBox) comp).getSelectedItem().toString();
		} else if (comp instanceof ScrollableJTextArea) {
			result = ((ScrollableJTextArea) comp).getText();
		} else if (comp instanceof JTextArea) {
			result = ((JTextArea) comp).getText();
		} else if (comp instanceof AbstractFilePropertyEditor) {
			result = ((AbstractFilePropertyEditor) comp).getText();
		} else {
			System.err.println("Unhandled Component type '"
					+ comp.getClass().getSimpleName() + "' in getComponentValue");
		}

		return result;
	}

	/**
	 * Get the current <tt>String</tt> value of a customization component (e.g.,
	 * a <tt>JTextField</tt>).
	 * 
	 * @param name
	 *          The name of the JComponent to get the value of. See
	 *          {@link #getComponent(String)} for the documentation of the naming
	 *          scheme.
	 * @return The components current value.
	 */
	private String getComponentValue(String name) {
		if (name != null)
			return getComponentValue(getComponent(name));
		else
			return null;
	}

	/**
	 * Gets a JComponent that displays the currently edited WUMprep configuration
	 * file and provides a button for opening a file browser to open a different
	 * one.
	 * 
	 * @return The GUI component for file selection
	 */
	private JComponent getConfigFileSelectorJComponent() {
		JPanel panel = new JPanel(new SpringLayout());

		JLabel filePathJl = new JLabel("Configuration file:");

		// The text field for displaying and editing the file path
		FilePropertyEditor fpe = new FilePropertyEditor(
				"Select a WUMprep configuration file", new FileFilter() {
					public boolean accept(File arg0) {
						if (arg0.getName().endsWith(".conf") || arg0.isDirectory())
							return true;
						else
							return false;
					}

					public String getDescription() {
						return "WUMprep configuration files (*.conf)";
					}
				});

		fpe.setName(":configFile");
		fpe.addPropertyChangeListener(this);
		custComponents.put(fpe.getName(), fpe);

		panel.add(filePathJl);
		panel.add(fpe);
		panel.addFocusListener(this);

		SpringUtilities.makeCompactGrid(panel, 1, 2, 5, 5, 5, 5);

		return panel;
	}

	/**
	 * Get {@link #contextHelpJPanel} and initialize it if neccessary.
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getContextHelpJPanel() {
		if (contextHelpJPanel == null) {
			contextHelpJPanel = new JPanel();
			contextHelpJPanel.setLayout(new BoxLayout(getContextHelpJPanel(),
					BoxLayout.Y_AXIS));
			contextHelpJPanel.add(getContextHelpJScrollPane(), null);
		}
		return contextHelpJPanel;
	}

	/**
	 * Get the {@link #contextHelpJScrollPane} and initialize it if neccessary.
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getContextHelpJScrollPane() {
		if (contextHelpJScrollPane == null) {
			contextHelpJScrollPane = new JScrollPane();
			contextHelpJScrollPane.setBorder(BorderFactory.createTitledBorder(null,
					"Help", TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, null, null));
			contextHelpJScrollPane.setViewportView(getContextHelpJTextArea());
		}
		return contextHelpJScrollPane;
	}

	/**
	 * Get {@link #contextHelpJTextArea} and initialize it if neccessary.
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getContextHelpJTextArea() {
		if (contextHelpJTextArea == null) {
			contextHelpJTextArea = new JTextArea();
			contextHelpJTextArea.setBackground(new Color(0xff, 0xff, 0xcc));
			contextHelpJTextArea.setLineWrap(true);
			contextHelpJTextArea.setWrapStyleWord(true);
			contextHelpJTextArea.setEditable(false);
		}
		return contextHelpJTextArea;
	}

	/**
	 * Get the {@link #customizationJComponent} and initialize it if neccessary.
	 * 
	 * @return The customization controls
	 */
	private JComponent getCustomizationJComponent() {
		if (customizationJComponent == null) {
			customizationJComponent = new JTabbedPane();
		}

		return customizationJComponent;
	}

	/**
	 * Get the {@link #jSplitPane} and initialize it if neccessary.
	 * 
	 * The left pane takes the customization controls, the right pane displays the
	 * context help.
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setName("jSplitPane");
			jSplitPane
					.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
			jSplitPane.setResizeWeight(0.7D);
			jSplitPane.setPreferredSize(new java.awt.Dimension(500, 200));
			// jSplitPane.setDividerLocation(350);
			jSplitPane.setDividerSize(10);
			jSplitPane.setRightComponent(getContextHelpJPanel());
		}
		return jSplitPane;
	}

	/**
	 * Generates a component for editing a single section of a
	 * WUMprepConfiguration.
	 * 
	 * @param sectionLabel
	 *          The label of the section to edit
	 * @return The JPanel to be displayed in an editor GUI
	 */
	private JComponent getSectionJComponent(String sectionLabel) {
		int numRows = 0;
		JPanel sectionJPanel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		Insets insetsLeft = new Insets(0, 5, 5, 5);
		Insets insetsRight = new Insets(0, 0, 5, 5);

		c.anchor = GridBagConstraints.NORTHEAST;

		if (sectionLabel != null) {
			WUMprepConfigSection section = this.config.getSection(sectionLabel);

			for (String setting : section.getSettings().keySet()) {

				if (!getWumprep4WekaSettingsOnlyJCheckBox().isSelected()
						|| wumprep4wekaConfigSettings.contains(sectionLabel + ":" + setting)) {
					JLabel label = new JLabel(setting + ":", JLabel.TRAILING);
					JComponent component = getComponent(section.getName() + ":" + setting);

					if (numRows == 0)
						c.insets = new Insets(5, 5, 5, 5);
					else
						c.insets = insetsLeft;

					c.gridy = numRows;
					c.gridx = 0;
					c.fill = GridBagConstraints.EAST;
					c.weightx = 0;
					sectionJPanel.add(label, c);

					if (numRows == 0)
						c.insets = new Insets(5, 0, 5, 5);
					else
						c.insets = insetsRight;

					c.gridx = 1;
					c.weighty = 0;
					c.weightx = 0.1;
					if (component instanceof JTextField
							|| component instanceof AbstractFilePropertyEditor
							|| component instanceof JComboBox) {
						c.fill = GridBagConstraints.HORIZONTAL;
					} else {
						c.fill = GridBagConstraints.BOTH;
						c.weighty = 0.2;
					}

					sectionJPanel.add(component, c);

					numRows++;
				}
			}
		}

		c.gridy = numRows;
		c.gridwidth = 2;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.BOTH;
		sectionJPanel.add(javax.swing.Box.createVerticalGlue(), c);

		// return new JScrollPane(sectionJPanel);
		return sectionJPanel;
	}

	/**
	 * Get the component for choosing the set of settings to expose for
	 * configuration.
	 * 
	 * @return The <tt>JCheckBox</tt> for selecting which settings to display.
	 */
	private JCheckBox getWumprep4WekaSettingsOnlyJCheckBox() {
		if (wumprep4wekaSettingsOnlyJcb == null) {
			wumprep4wekaSettingsOnlyJcb = new JCheckBox(
					"Show WUMprep4Weka settings only.");
			wumprep4wekaSettingsOnlyJcb.setSelected(true);
			wumprep4wekaSettingsOnlyJcb.addItemListener(this);
		}

		return wumprep4wekaSettingsOnlyJcb;
	}

	/**
	 * Gets a <code>JComponent</code> for selecting the directory where WUMprep
	 * is installed on the local system.
	 * 
	 * @return The WUMprep installation directory selector.
	 */
	private JComponent getWUMprepLocalizerJComponent() {
		JPanel panel = new JPanel(new SpringLayout());

		JLabel dirJl = new JLabel("WUMprep directory:");

		// The text field for displaying and editing the file path
		DirectoryPropertyEditor dpe = new DirectoryPropertyEditor(
				"Select the directory where WUMprep is installed");

		dpe.setText(WUMprepWrapper.getWUMprepHome());
		dpe.setName(":WUMprepDir");
		dpe.addPropertyChangeListener(this);
		custComponents.put(dpe.getName(), dpe);

		panel.add(dirJl);
		panel.add(dpe);
		panel.addFocusListener(this);

		SpringUtilities.makeCompactGrid(panel, 1, 2, 5, 5, 5, 5);

		return panel;
	}

	/**
	 * Checks whether there are unsaved changes to the
	 * {@link WUMprepConfiguration} exposed by this customizer.
	 * 
	 * @return true if there are unsaved changes.
	 */
	public boolean hasUnsavedChanges() {
		return unsavedChanges;
	}

	/**
	 * Initializes a {@link FilePropertyEditor} with an assoziated current value
	 * in the customizer's WUMprepConfiguration.
	 * 
	 * @param control
	 *          The combo box to be initialized.
	 * @param sectionName
	 *          The initialization value's section in the WUMprep coniguration
	 *          file.
	 * @param settingName
	 *          The initialization value's label in the WUMprep coniguration file.
	 */
	private void initControl(AbstractFilePropertyEditor control,
			String sectionName, String settingName) {
		WUMprepConfigSection section = config.getSection(sectionName);

		if (!section.defined(settingName))
			section.addSetting((WUMprepConfigSetting) defaultConfig.getSection(
					sectionName).getSetting(settingName).getClone());

		control.setText(section.getSetting(settingName).getValue());
	}

	/**
	 * Initializes a {@link javax.swing.JComboBox} with an assoziated current
	 * value in the customizer's WUMprepConfiguration.
	 * 
	 * @param control
	 *          The combo box to be initialized.
	 * @param sectionName
	 *          The initialization value's section in the WUMprep coniguration
	 *          file.
	 * @param settingName
	 *          The initialization value's label in the WUMprep coniguration file.
	 */
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
	}

	/**
	 * Initializes a {@link javax.swing.JList} with an assoziated current value in
	 * the customizer's WUMprepConfiguration.
	 * 
	 * @param control
	 *          The combo box to be initialized.
	 * @param sectionName
	 *          The initialization value's section in the WUMprep coniguration
	 *          file.
	 * @param settingName
	 *          The initialization value's label in the WUMprep coniguration file.
	 */
	private void initControl(JList control, String sectionName, String settingName) {
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
	}

	/**
	 * Initializes a {@link javax.swing.JTextField} with an assoziated current
	 * value in the customizer's WUMprepConfiguration.
	 * 
	 * @param control
	 *          The combo box to be initialized.
	 * @param sectionName
	 *          The initialization value's section in the WUMprep coniguration
	 *          file.
	 * @param settingName
	 *          The initialization value's label in the WUMprep coniguration file.
	 */
	private void initControl(JTextField control, String sectionName,
			String settingName) {
		WUMprepConfigSection section = config.getSection(sectionName);

		if (!section.defined(settingName)) {
			section.addSetting((WUMprepConfigSetting) defaultConfig.getSection(
					sectionName).getSetting(settingName).getClone());
		}

		control.setText(section.getSetting(settingName).getValue());
	}

	/**
	 * Initializes a {@link WUMprepConfigurationCustomizer.ScrollableJTextArea}
	 * with an assoziated current value in the customizer's WUMprepConfiguration.
	 * 
	 * @param control
	 *          The combo box to be initialized.
	 * @param sectionName
	 *          The initialization value's section in the WUMprep coniguration
	 *          file.
	 * @param settingName
	 *          The initialization value's label in the WUMprep coniguration file.
	 */
	private void initControl(ScrollableJTextArea control, String sectionName,
			String settingName) {
		WUMprepConfigSection section = config.getSection(sectionName);

		if (!section.defined(settingName))
			section.addSetting((WUMprepConfigSetting) defaultConfig.getSection(
					sectionName).getSetting(settingName).getClone());

		control.setText(section.getSetting(settingName).getValue());
	}

	/**
	 * Iterates over the {@link #customizationJComponent}'s controls and
	 * initializes them with their current values from {@link #config}.
	 */
	private void initControls() {
		for (String name : custComponents.keySet()) {
			if (name.equals(":configFile"))
				continue;

			String[] settingInfo = name.split(":", -1);
			JComponent comp = getComponent(name);
			if (settingInfo.length == 2
					&& this.config.getSection(settingInfo[0]).defined(settingInfo[1])) {
				if (comp instanceof JComboBox)
					initControl((JComboBox) comp, settingInfo[0], settingInfo[1]);
				else if (comp instanceof JTextField)
					initControl((JTextField) comp, settingInfo[0], settingInfo[1]);
				else if (comp instanceof ScrollableJTextArea)
					initControl((ScrollableJTextArea) comp, settingInfo[0],
							settingInfo[1]);
				else if (comp instanceof AbstractFilePropertyEditor)
					initControl((AbstractFilePropertyEditor) comp, settingInfo[0],
							settingInfo[1]);
				else if (comp instanceof JList)
					initControl((JList) comp, settingInfo[0], settingInfo[1]);
				else
					System.err.println(this.getClass()
							+ "#initControls: Unhandled component '" + comp.getClass() + "'");

				oldValue.put(name, getComponentValue(comp));
			}
		}
		unsavedChanges = false;
	}

	/**
	 * Initializes all customization controls with the values of the current
	 * {@link #config}.
	 */
	private void initCustomizer() {
		// Reset the customizationJComponent
		JTabbedPane tabPane = (JTabbedPane) getCustomizationJComponent();
		tabPane.removeAll();
		String[] sections;

		if (config.getEditSections() == null || config.getEditSections().equals("")) {
			Vector<String> sv = new Vector<String>(0, 1);
			for (WUMprepConfigSection s : config.getSections())
				if (!s.getName().equals(""))
					sv.add(s.getName());

			sections = sv.toArray(new String[0]);
		} else
			sections = config.getEditSections().split(":");

		for (int i = 0; i < sections.length; i++) {
			tabPane.add(sections[i], getSectionJComponent(sections[i]));
		}

		initControls();
		getJSplitPane().setLeftComponent(tabPane);
	}

	/**
	 * Initializes <tt>this</tt>.
	 */
	private void initialize() {
		try {
			defaultConfig = new WUMprepConfiguration();

			// Initialize the hash holding the config settings relevant for
			// WUMprep4Weka
			Properties props = new Properties();
			props.load(getClass().getResourceAsStream(
					"WUMprepConfigurationEditor.props"));

			String[] tmp = props.getProperty("WUMprep4Weka.wumprepConfSettings")
					.split(",");

			wumprep4wekaConfigSettings = new HashSet<String>();

			for (int i = 0; i < tmp.length; i++) {
				wumprep4wekaConfigSettings.add(((String) tmp[i]).trim());
			}

			// TODO Remove defaultSection?
			custComponents = new HashMap<String, JComponent>();
			oldValue = new HashMap<String, String>();
			this.setLayout(new GridBagLayout());
			this.setMinimumSize(new java.awt.Dimension(550, 450));
			this.setPreferredSize(new java.awt.Dimension(550, 450));
			this.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE,
					Integer.MAX_VALUE));

			GridBagConstraints c = new GridBagConstraints();
			c.gridy = 1;
			c.gridx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			this.add(getConfigFileSelectorJComponent(), c);

			c.gridy = 2;
			c.weighty = 0.7;
			c.weightx = 0.7;
			c.fill = GridBagConstraints.BOTH;
			this.add(getJSplitPane(), c);

			c.gridy = 3;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0;
			c.weighty = 0;
			this.add(getWumprep4WekaSettingsOnlyJCheckBox(), c);

			c.gridy = 4;
			c.weighty = 0;
			c.weightx = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			this.add(getWUMprepLocalizerJComponent(), c);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		if (e.getItemSelectable() == wumprep4wekaSettingsOnlyJcb)
			initCustomizer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String sourceName = ((JComponent) evt.getSource()).getName();

		String[] settingInfo = sourceName.split(":", 2);

		WUMprepConfigSetting setting = config.getSection(settingInfo[0])
				.getSetting(settingInfo[1]);

		if (evt.getSource() == getComponent(":configFile")) {
			String editSection = null;
			boolean overwrite = false;

			if (this.config != null)
				editSection = this.config.getEditSections();

			if (new File(getComponentValue(":configFile")).isFile()) {
				// The file already exists - load or overwrite?
				String[] options = { "Load", "Overwrite" };
				int returnVal = JOptionPane
						.showOptionDialog(
								this,
								"Do you want to load the selected file, or do you want to overwrite the selected file with the current configuration?",
								"Load/overwrite file", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, UIManager
										.getIcon("OptionPane.questionIcon"), options, "Load");

				if (returnVal == 0) {
					// Load the selected file and discard the current config
					overwrite = false;

				} else if (returnVal == 1) {
					// Overwrite the selected file with the current config
					overwrite = true;
				}

			} else {
				// The user has selected a new file - create it
				overwrite = true;
			}

			if (overwrite) {
				if (evt.getNewValue() != null) {
					saveConfig((String) evt.getNewValue(), true);
				}

			} else {
				config = new WUMprepConfiguration(getComponentValue(":configFile"));
				config.setEditSections(editSection);
				this.setObject(config);
			}

		} else if (evt.getSource() == getComponent(":WUMprepDir")) {
			WUMprepWrapper.setWUMprepHome(getComponentValue(":WUMprepDir"));

		} else if (setting instanceof WUMprepConfigValue) {
			((WUMprepConfigValue) setting).setValue(getComponentValue(sourceName));

		} else if (setting instanceof WUMprepConfigValueList) {
			((WUMprepConfigValueList) setting)
					.setValues(getComponentValue(sourceName));

		} else {
			System.err.println("Unhandled focus lost event from: "
					+ ((JComponent) evt.getSource()).getName() + ", "
					+ evt.getSource().getClass().getSimpleName());
		}

		this.firePropertyChange("", null, config);
	}

	/**
	 * When <tt>this</tt> customizer get's removed from its parent container, it
	 * checks wether there are unsaved changes and ask the user to save them if
	 * neccessary.
	 * 
	 * @see java.awt.Component#removeNotify()
	 */
	@Override
	public void removeNotify() {
		super.removeNotify();

		// Simulate a focusLost event to ensure a correct unsavedChanges flag and
		// a correct configuration model
		if (currentFocus != null)
			this.focusLost(new FocusEvent(currentFocus, FocusEvent.FOCUS_LOST));

		if (unsavedChanges) {
			int answer;
			boolean filenameApproved = false;
			boolean cancel = false;

			// prepare the fileChooser for selecting a filename to save the config
			// under.
			fileChooser.setDialogTitle("Save WUMprep configuration");
			fileChooser.resetChoosableFileFilters();
			fileChooser.setFileFilter(new FileFilter() {
				public boolean accept(File arg0) {
					if (arg0.getName().endsWith(".conf") || arg0.isDirectory())
						return true;
					else
						return false;
				}

				public String getDescription() {
					return "WUMprep configuration files (*.conf)";
				}
			});
			fileChooser.setAcceptAllFileFilterUsed(true);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			// If the config has no file yet, ask the user if he wants to save it.
			if (config.getFilePath() == null) {
				answer = javax.swing.JOptionPane.showConfirmDialog(this,
						"Do you want to save the current configuration?", "Save changes?",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (answer == JOptionPane.YES_OPTION)
					answer = fileChooser.showOpenDialog(this);
			} else {
				filenameApproved = true;
				answer = JOptionPane.YES_OPTION;
				fileChooser.setSelectedFile(new File(config.getFilePath()));
			}

			if (!filenameApproved) {
				do {
					if (answer == JFileChooser.APPROVE_OPTION
							&& fileChooser.getSelectedFile().isFile()) {
						if (JOptionPane
								.showConfirmDialog(
										this,
										"The file already exists. Do you want to overwrite it with the current configuration?",
										"Confirm overwrite", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							filenameApproved = true;

						} else
							answer = fileChooser.showOpenDialog(this);

					} else if (answer == JFileChooser.CANCEL_OPTION) {
						cancel = true;

					} else
						filenameApproved = true;

				} while (!filenameApproved && !cancel);
			}

			if (filenameApproved) {
				saveConfig(fileChooser.getSelectedFile().getAbsolutePath());
				this.firePropertyChange("", null, config);
			}
		}
	}

	/**
	 * Saves the current configuration into a file.
	 * 
	 * @param filename
	 *          The name of the file to save the configuration into.
	 */
	private void saveConfig(String filename) {
		saveConfig(filename, false);
	}

	/**
	 * Saves the current configuration into a file.
	 * 
	 * @param filename
	 *          The name of the file to save the configuration into.
	 * @param silent
	 *          Whether the use should be notified about the saving via a message
	 *          box.
	 */
	private void saveConfig(String filename, boolean silent) {
		this.config.toFile(filename);

		if (!silent)
			JOptionPane
					.showMessageDialog(this,
							"The configuration was changed has been saved as '" + filename
									+ "'.", "Configuration saved",
							JOptionPane.INFORMATION_MESSAGE);

		initControls();
	}

	/**
	 * Set the context help displayed in the {@link #contextHelpJPanel}.
	 * 
	 * @param title
	 *          TODO
	 * @param text
	 *          The new help text to be displayed.
	 */
	private void setContextHelp(String title, String text) {
		JTextArea contextHelpJta = getContextHelpJTextArea();
		((TitledBorder) getContextHelpJScrollPane().getBorder()).setTitle("Help: "
				+ title);
		contextHelpJta.setText(text);
		contextHelpJta.scrollRectToVisible(new Rectangle(0, 0, 0, 0));
	}

	/**
	 * Set <tt>this</tt> customizer to edit the given
	 * {@link WUMprepConfiguration}.
	 * 
	 * @param obj
	 *          Must be an object of type {@link WUMprepConfiguration}.
	 * @see java.beans.Customizer#setObject(java.lang.Object)
	 */
	public void setObject(Object obj) {
		if (obj instanceof WUMprepConfiguration) {
			this.config = (WUMprepConfiguration) obj;
			((FilePropertyEditor) getComponent(":configFile")).setText(config
					.getFilePath());
			initCustomizer();
			unsavedChanges = false;
		}
	}

	/**
	 * Set the <tt>unsavedChanges</tt> status of the customizer. Setting this to
	 * <tt>true</tt> will force the customizer to ask the user to save his
	 * changes when the customization dialog gets {@link #removeNotify() removed}.
	 * 
	 * @param unsavedChanges
	 */
	// public void setUnsavedChanges(boolean unsavedChanges) {
	// this.unsavedChanges = unsavedChanges;
	// }
}