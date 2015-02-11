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
 * AbstractFilePropertyEditor.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */

package org.hypknowsys.wumprep4weka.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import weka.gui.CustomPanelSupplier;

/**
 * Provides a component comprised of an editable JTextField and a button that
 * opens a file or directory selection dialog.
 * 
 * This abstract class provides the general functionality. The
 * {@link FilePropertyEditor} and {@DirectoryPropertyEditor} classes provide
 * concrete implementations for selecting files or directories only,
 * respectively.
 * 
 * You can inherit from this class for providing more specialized file or
 * directory selection controls. Besides providing the necessary constructors,
 * you have to override the {@link #initialize()} method.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: AbstractFilePropertyEditor.java,v 1.4 2005/10/16 13:24:32
 *          cpohle Exp $
 */

public abstract class AbstractFilePropertyEditor extends JPanel implements
		PropertyEditor, CustomPanelSupplier, FocusListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2488492978544073759L;

	/**
	 * Whether the "*.*" filter should be available
	 */
	protected boolean acceptAllFiles = true;

	/**
	 * The button for opening the file selection dialog.
	 */
	private JButton button = null;

	/**
	 * The dialog title.
	 */
	protected String dialogTitle = null;

	/**
	 * Stores whether <t>this</tt> works with a <tt>String</tt> or with a <tt>File</tt>
	 * object
	 */
	private Class classMode = null;

	/**
	 * 
	 */
	private final JFileChooser fileChooser = new JFileChooser();

	/**
	 * One of JFileChooser.FILES_AND_DIRECTORIES, JFileChooser.FILES_ONLY and
	 * JFileChooser.DIRECTORIES_ONLY.
	 */
	protected int fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;

	/**
	 * File filter, might remain null
	 */
	protected FileFilter filter = null;

	/**
	 * Helper for implementing the PropertyEditor interface.
	 */
	private final PropertyEditorSupport pes = new PropertyEditorSupport(this);

	/**
	 * Text field showing the selected file or directory.
	 */
	private JTextField tf = null;

	/**
	 * Creates a default AbstractFilePropertyEditor.
	 */
	public AbstractFilePropertyEditor() {
		super();
		this.dialogTitle = "Select a file or directory";
		this.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;
		initialize();
	}

	/**
	 * Creates a new <tt>AbstractFilePropertyEditor</tt> component for selecting
	 * files of type <tt>fd</tt>.
	 * 
	 * @param dialogTitle
	 *          The title to be used for the file selection dialog.
	 * @param filter
	 *          The file filter for the type of files that can be selected using
	 *          this component.
	 * @param fileSelectionMode
	 *          One of JFileChooser.FILES_AND_DIRECTORIES, JFileChooser.FILES_ONLY
	 *          and JFileChooser.DIRECTORIES_ONLY.
	 */
	public AbstractFilePropertyEditor(String dialogTitle, FileFilter filter,
			int fileSelectionMode) {
		super();
		this.dialogTitle = dialogTitle;
		this.filter = filter;
		this.fileSelectionMode = fileSelectionMode;
		initialize();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		pes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		pes.removePropertyChangeListener(l);
	}

	/**
	 * Creates a new <tt>AbstractFilePropertyEditor</tt> component.
	 * 
	 * @param dialogTitle
	 *          The title to be used for the file selection dialog.
	 * @param fileSelectionMode
	 *          One of JFileChooser.FILES_AND_DIRECTORIES, JFileChooser.FILES_ONLY
	 *          and JFileChooser.DIRECTORIES_ONLY
	 */
	public AbstractFilePropertyEditor(String dialogTitle, int fileSelectionMode) {
		super();
		this.dialogTitle = dialogTitle;
		this.fileSelectionMode = fileSelectionMode;
		initialize();
	}

	@Override
	public synchronized void addFocusListener(FocusListener arg0) {
		tf.addFocusListener(arg0);
		button.addFocusListener(arg0);
	}

	public String getAsText() {
		return null;
	}

	public Component getCustomEditor() {
		return pes.getCustomEditor();
	}

	public JPanel getCustomPanel() {
		// this.setPreferredSize(new Dimension(100,
		// this.getPreferredSize().height));
		return this;
	}

	public String getJavaInitializationString() {
		return pes.getJavaInitializationString();
	}

	// @Override
	// public String getName() {
	// return tf.getName();
	// }

	public String[] getTags() {
		return pes.getTags();
	}

	/**
	 * Returns the text contained in this <tt>ScrollableJTextArea</tt>.
	 * 
	 * @return the Text
	 * @see javax.swing.text.JTextComponent#getText()
	 */
	public String getText() {
		return tf.getText();
	}

	public Object getValue() {
		if (this.classMode == File.class)
			return new File(getText());
		else
			return getText();
	}

	/**
	 * Initializes <code>this</code> AbstractFilePropertyEditor. You might
	 * overwrite this method as shown in the following example in order to
	 * implement your specialized file or directory selection components:
	 * 
	 * <pre>
	 * void initialize() {
	 * 	// Your stuff here, e.g.:
	 * 	this.dialogTitle = &quot;Select your home directory&quot;;
	 * 	this.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY;
	 * 
	 * 	// Don't forget this at the end!!!
	 * 	super.initialize();
	 * }
	 * </pre>
	 * 
	 */
	protected void initialize() {
		// Initialize the components comprising a AbstractFilePropertyEditor
		this.setLayout(new GridBagLayout());

		tf = new JTextField();
		Dimension buttonSize = new Dimension(20, tf.getPreferredSize().height);

		tf.addFocusListener(this);
		this.pes.setSource(this);
		this.pes.setValue(this);

		button = new JButton("...");
		button.setMinimumSize(buttonSize);
		button.setPreferredSize(buttonSize);
		button.setMaximumSize(buttonSize);

		// Add an ActionListener to the button that opens the File dialogue
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				File file = null;

				AbstractFilePropertyEditor pe = AbstractFilePropertyEditor.this;
				fileChooser.resetChoosableFileFilters();
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				if (pe.filter != null) {
					fileChooser.setAcceptAllFileFilterUsed(pe.acceptAllFiles);
					fileChooser.setFileFilter(pe.filter);
				} else
					fileChooser.setAcceptAllFileFilterUsed(true);

				fileChooser.setFileSelectionMode(pe.fileSelectionMode);
				fileChooser.setDialogTitle(pe.dialogTitle);

				// Set the current directory
				if (pe.getText() != "")
					file = new File(pe.getText());
				else
					file = new File(System.getProperty("user.dir"));

				if (file.isFile())
					fileChooser.setCurrentDirectory(file.getParentFile());
				else if (file.isDirectory())
					fileChooser.setCurrentDirectory(file.getAbsoluteFile());

				int returnVal = fileChooser.showOpenDialog(pe);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fileChooser.getSelectedFile();

					if ((pe.fileSelectionMode == JFileChooser.FILES_ONLY && file.isFile())
							|| (pe.fileSelectionMode == JFileChooser.DIRECTORIES_ONLY && file
									.isDirectory())
							|| (pe.fileSelectionMode == JFileChooser.FILES_AND_DIRECTORIES && (file
									.isDirectory() || file.isFile()))) {
						pe.setText(file.getAbsolutePath());

						AbstractFilePropertyEditor.this.pes.firePropertyChange();
					} else {
						System.err
								.println("AbstractFilePropertyEditor: Non-existing file or directory selected!");
					}
				}
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		this.add(tf, c);

		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		this.add(button, c);

	}

	public boolean isPaintable() {
		return true;
	}

	public void paintValue(Graphics gfx, Rectangle box) {
	}

	@Override
	public synchronized void removeFocusListener(FocusListener arg0) {
		tf.removeFocusListener(arg0);
		button.removeFocusListener(arg0);
	}

	public void setAsText(String text) throws IllegalArgumentException {
		setText(text);
	}

	@Override
	public void setMaximumSize(Dimension arg0) {
		tf.setMaximumSize(arg0);
	}

	@Override
	public void setMinimumSize(Dimension arg0) {
		tf.setMinimumSize(arg0);
	}

	// @Override
	// public void setName(String arg0) {
	// tf.setName(arg0);
	// button.setName(arg0);
	// }

	@Override
	public void setPreferredSize(Dimension arg0) {
		tf.setPreferredSize(arg0);
	}

	/**
	 * Sets the text value of this <code>AbstractFilePropertyEditor</code>.
	 * 
	 * @param text
	 *          The path of the file or directory.
	 * @see javax.swing.text.JTextComponent#setText(java.lang.String)
	 */
	public void setText(String text) {
		tf.setText(text);
	}

	public void setValue(Object value) {
		if (value instanceof String) {
			this.classMode = String.class;
			setText((String) value);
		} else if (value instanceof File) {
			this.classMode = File.class;
			if (value != null)
				setText(((File) value).getAbsolutePath());
			else
				setText("");
		}
	}

	public boolean supportsCustomEditor() {
		return true;
	}

	public void focusLost(FocusEvent e) {
		if (e.getSource() == this.tf) {
			this.pes.firePropertyChange();
		}
	}

	public void focusGained(FocusEvent e) {
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		tf.setName(name);
		button.setName(name);
	}
}