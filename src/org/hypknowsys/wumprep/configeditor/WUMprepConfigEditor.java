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
 * WUMprepConfigEditor.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep.configeditor;

import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.hypknowsys.wumprep.config.WUMprepConfiguration;
import org.hypknowsys.wumprep.configeditor.WUMprepConfigEditorJPanel.WUMprepConfigEditorController;

/**
 * This is the main class of the WUMprep configuration editor GUI. 
 * It's primary purpose is to display an @see{WUMprepConfigEditorJPanel}
 * to the user. 
 * 
 * @author Carsten Pohle
 * @version $Id: WUMprepConfigEditor.java,v 1.5 2005/10/16 13:25:42 cpohle Exp $
 *
 */
public class WUMprepConfigEditor extends JFrame implements
		ContextHelpChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2836046262638920830L;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WUMprepConfigEditor application = new WUMprepConfigEditor();
		application.setVisible(true);
	}

	private static final String applicationTitle = "WUMprep Configuration Editor";

	private JMenuItem helpAboutMenuItem = null;

	private WUMprepConfigEditorController controller = null;

	private JMenuItem fileExitMenuItem = null;

	private final JFileChooser fc = new JFileChooser();

	private JMenu fileMenu = null;

	private JMenuItem fileOpenMenuItem = null;

	private JMenu helpMenu = null;

	private JMenuBar jJMenuBar = null;

	private JMenuItem fileNewMenuItem = null;

	private JPanel jPanelHelpArea = null;

	private JScrollPane jScrollPane = null;

	private JSplitPane jSplitPane = null;

	private JTextArea jTextAreaContextHelp = null;

	private JMenuItem fileSaveAsMenuItem = null;

	private JMenuItem fileSaveMenuItem = null;

	private WUMprepConfigEditorJPanel WUMprepConfigEditorJPanel = null;

	/**
	 * This is the default constructor
	 */
	public WUMprepConfigEditor() {
		super();
		initialize();
	}

	/**
	 * Constructor opening the WUMprep configuration file <tt>filename</tt>
	 * in a new WUMprepConfigEditor.
	 * 
	 * @param filename The WUMprep configuration file to load into the GUI
	 */
	public WUMprepConfigEditor(String filename) {
		super();
		initialize(filename);
	}

	public void contextHelpChanged(String helpText) {
		getJTextAreaContextHelp().setText(helpText);
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getHelpAboutMenuItem() {
		if (helpAboutMenuItem == null) {
			helpAboutMenuItem = new JMenuItem();
			helpAboutMenuItem.setText("About");
			helpAboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// TODO Display WUMprep splash/about screen here
					new JDialog(WUMprepConfigEditor.this, "About", true)
							.setVisible(true);
				}
			});
		}
		return helpAboutMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getFileExitMenuItem() {
		if (fileExitMenuItem == null) {
			fileExitMenuItem = new JMenuItem();
			fileExitMenuItem.setText("Exit");
			fileExitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					Event.CTRL_MASK, true));
			fileExitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (controller.isConfigChanged()) {
						if (JOptionPane.showConfirmDialog(WUMprepConfigEditor.this,
								"Do you want to save the configuration before exiting?",
								"Save before exit?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							if (saveFile())
								System.exit(0);
						} else
							System.exit(0);
					} else {
						System.exit(0);
					}
				}
			});
		}
		return fileExitMenuItem;
	}

	private File getFileForSave() {
		FileFilter filter = new FileFilter() {
			public boolean accept(File f) {
				if (f.getAbsolutePath().matches(".*\\.conf"))
					return true;

				if (f.isDirectory())
					return true;

				return false;
			}

			public String getDescription() {
				return "WUMprep configuration file (*.conf)";
			}
		};

		fc.setFileFilter(filter);
		fc.setDialogTitle("Save the WUMprep configuration as");
		fc.setApproveButtonText("Save");
		boolean filenameApproved = false;
		int returnVal;
		do {
			returnVal = fc.showOpenDialog(WUMprepConfigEditor.this);
			if (returnVal == JFileChooser.APPROVE_OPTION
					&& fc.getSelectedFile().isFile()) {
				if (JOptionPane
						.showConfirmDialog(
								this,
								"The file already exists. Do you want to overwrite it with the current configuration?",
								"Confirm overwrite", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					filenameApproved = true;
			} else
				filenameApproved = true;
		} while (!filenameApproved);

		if (returnVal == JFileChooser.APPROVE_OPTION)
			return fc.getSelectedFile();
		else
			return null;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getFileNewMenuItem());
			fileMenu.add(getFileOpenMenuItem());
			fileMenu.add(getFileSaveMenuItem());
			fileMenu.add(getFileSaveAsMenuItem());
			fileMenu.add(getFileExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes fileOpenMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getFileOpenMenuItem() {
		if (fileOpenMenuItem == null) {
			fileOpenMenuItem = new JMenuItem();
			fileOpenMenuItem.setText("Open...");
			fileOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
					Event.CTRL_MASK, true));
			fileOpenMenuItem.addActionListener(new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (noUnsavedChanges()) {
						FileFilter filter = new FileFilter() {
							public boolean accept(File f) {
								if (f.getAbsolutePath().matches(".*\\.conf"))
									return true;

								if (f.isDirectory())
									return true;

								return false;
							}

							public String getDescription() {
								return "WUMprep configuration file (*.conf)";
							}
						};
						fc.setFileFilter(filter);
						fc.setApproveButtonText("Open");
						fc.setDialogTitle("Open WUMprep configuration file");
						int returnVal = fc.showOpenDialog(WUMprepConfigEditor.this);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							controller.setConfiguration(new WUMprepConfiguration(fc
									.getSelectedFile()));
							controller.setConfigChanged(false);
							WUMprepConfigEditor.this.setTitle(applicationTitle + " - "
									+ fc.getSelectedFile().getAbsolutePath());
						}
					}
				}
			});
		}
		return fileOpenMenuItem;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getHelpAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			//jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes fileNewMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getFileNewMenuItem() {
		if (fileNewMenuItem == null) {
			fileNewMenuItem = new JMenuItem();
			fileNewMenuItem.setText("New");
			fileNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
					Event.CTRL_MASK, true));
			fileNewMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (noUnsavedChanges()) {
							controller.setConfiguration(new WUMprepConfiguration());
							WUMprepConfigEditor.this.setTitle(applicationTitle
									+ " - <New Configuration>");
					}
				}
			});
		}
		return fileNewMenuItem;
	}

	/**
	 * This method initializes jPanelHelpArea
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelHelpArea() {
		if (jPanelHelpArea == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.insets = new java.awt.Insets(3, 5, 5, 5);
			jPanelHelpArea = new JPanel();
			jPanelHelpArea.setLayout(new GridBagLayout());
			jPanelHelpArea.add(getJScrollPane(), gridBagConstraints2);
		}
		return jPanelHelpArea;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
					"Help", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jScrollPane.setViewportView(getJTextAreaContextHelp());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setLeftComponent(getWUMprepConfigEditorJPanel());
			jSplitPane.setRightComponent(getJPanelHelpArea());
			jSplitPane.setDividerLocation(600);
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextAreaContextHelp() {
		if (jTextAreaContextHelp == null) {
			jTextAreaContextHelp = new JTextArea();
			jTextAreaContextHelp.setLineWrap(true);
			jTextAreaContextHelp.setEditable(false);
			jTextAreaContextHelp.setBackground(new java.awt.Color(238, 238, 238));
			jTextAreaContextHelp.setWrapStyleWord(true);
		}
		return jTextAreaContextHelp;
	}

	/**
	 * This method initializes saveAsMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getFileSaveAsMenuItem() {
		if (fileSaveAsMenuItem == null) {
			fileSaveAsMenuItem = new JMenuItem();
			fileSaveAsMenuItem.setText("Save as...");
			fileSaveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					File file = getFileForSave();
					if (file != null) {
						writeConfiguration(file.getAbsolutePath());
						controller.getConfiguration().setFilePath(file.getAbsolutePath());
						controller.setConfigChanged(false);
						WUMprepConfigEditor.this.setTitle(applicationTitle + " - "
								+ file.getAbsolutePath());
					}
				}
			});
		}
		return fileSaveAsMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getFileSaveMenuItem() {
		if (fileSaveMenuItem == null) {
			fileSaveMenuItem = new JMenuItem();
			fileSaveMenuItem.setText("Save");
			fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Event.CTRL_MASK, true));
			fileSaveMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					saveFile();
				}
			});
		}
		return fileSaveMenuItem;
	}

	/**
	 * This method initializes WUMprepConfigEditorJPanel
	 * 
	 * @return org.hypknowsys.wumprep.configeditor.WUMprepConfigEditorJPanel
	 */
	private WUMprepConfigEditorJPanel getWUMprepConfigEditorJPanel() {
		if (WUMprepConfigEditorJPanel == null) {
			WUMprepConfigEditorJPanel = new WUMprepConfigEditorJPanel();
			WUMprepConfigEditorJPanel.setLayout(new BoxLayout(
					getWUMprepConfigEditorJPanel(), BoxLayout.X_AXIS));
			WUMprepConfigEditorJPanel.setPreferredSize(new java.awt.Dimension(800,
					600));
		}
		return WUMprepConfigEditorJPanel;
	}

	private void initialize() {
		this.controller = getWUMprepConfigEditorJPanel().getController();

		this.initializeDefault();
		controller.setConfiguration(new WUMprepConfiguration());
		this.setContentPane(getJSplitPane());
		this.setJMenuBar(getJJMenuBar());
		controller.addContextHelpChangeListener(this);
	}

	private void initialize(String filename) {
		this.controller = getWUMprepConfigEditorJPanel().getController();

		this.initializeDefault();
		this.setTitle(applicationTitle + " - " + filename);
		controller.setConfiguration(new WUMprepConfiguration(filename));
		controller.addContextHelpChangeListener(this);
	}

	private void initializeDefault() {
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		this.setBounds(new java.awt.Rectangle(0, 0, 800, 600));
		this.setContentPane(getJSplitPane());
		this.setJMenuBar(getJJMenuBar());
		this.setTitle(applicationTitle + " - <New Configuration>");
		getJTextAreaContextHelp().setText(controller.getContextHelp());
	}

	private boolean noUnsavedChanges() {
		if (controller.isConfigChanged()) {
			int answer = JOptionPane.showConfirmDialog(this,
					"Would you like to save the current configuration file?",
					"Unsaved changes", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (answer == JOptionPane.NO_OPTION)
				return true;

			if (saveFile())
				return true;
			else
				return false;

		} else
			return true;
	}

	protected boolean saveFile() {
		if (controller.getConfiguration().getFilePath() == null) {
			File file = getFileForSave();
			if (file != null) {
				writeConfiguration(file.getAbsolutePath());
				controller.getConfiguration().setFilePath(file.getAbsolutePath());
				controller.setConfigChanged(false);
				this.setTitle(applicationTitle + " - " + file.getAbsolutePath());
				return true;
			} else
				return false;
		} else {
			writeConfiguration(controller.getConfiguration().getFilePath());
			controller.setConfigChanged(false);
			this.setTitle(applicationTitle + " - "
					+ controller.getConfiguration().getFilePath());
			return true;
		}
	}

	private void writeConfiguration(String filename) {
		FileWriter writer;
		try {
			writer = new FileWriter(new File(filename));
			writer.write(controller.getConfiguration().toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

} // @jve:decl-index=0:visual-constraint="23,12"
