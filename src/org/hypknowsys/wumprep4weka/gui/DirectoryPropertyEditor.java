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
 * DirectoryPropertyEditor.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep4weka.gui;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Provides a component comprised of an editable JTextField and a button that
 * opens a directory selection dialog.
 * 
 * @author Carsen Pohle (cp AT cpohle de)
 * @version $Id: DirectoryPropertyEditor.java,v 1.3 2005/10/16 13:24:32 cpohle Exp $
 */
public class DirectoryPropertyEditor extends AbstractFilePropertyEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a default <code>DirectoryPropertyEditor</code>.
	 */
	public DirectoryPropertyEditor() {
		super("Select a directory", JFileChooser.DIRECTORIES_ONLY);
	}

	/**
	 * Creates in initializes a <DirectoryPropertyEditor>.
	 * 
	 * @param dialogTitle
	 *          The title to display on the file selection dialog.
	 * @param filter
	 *          The file filter to use for file selection.
	 */
	public DirectoryPropertyEditor(String dialogTitle, FileFilter filter) {
		super(dialogTitle, filter, JFileChooser.DIRECTORIES_ONLY);
	}

	/**
	 * Creates a new <tt>DirectoryPropertyEditor</tt> component.
	 * 
	 * @param dialogTitle
	 *          The title to be used for the directory selection dialog.
	 */
	public DirectoryPropertyEditor(String dialogTitle) {
		super(dialogTitle, JFileChooser.DIRECTORIES_ONLY);
	}
}
