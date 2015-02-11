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
 * WeblogSaverBeanInfo.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep4weka.core.converters;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.hypknowsys.wumprep4weka.gui.DirectoryPropertyEditor;
import org.hypknowsys.wumprep4weka.gui.FilePropertyEditor;

/**
 * BeanInfo for the {@link WeblogSaver} class. This controls the configuration
 * dialogue for the WeblogSaver node in the Weka KnowledgeFlow.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WeblogSaverBeanInfo.java,v 1.4 2005/10/16 13:25:42 cpohle Exp $
 */
public class WeblogSaverBeanInfo extends SimpleBeanInfo {

	/**
	 * Class reference
	 */
	private final static Class weblogSaverClass = WeblogSaver.class;

	@Override
	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor desc = new BeanDescriptor(weblogSaverClass);
		desc.setName("Weblog Saver");
		desc.setShortDescription("Saves an ARFF file as a Web log.");
		return desc;
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor pdLogfileTemplate = null;
		PropertyDescriptor pdDir = null;
		PropertyDescriptor pdFilenameExtension = null;
		PropertyDescriptor pdPrefix = null;

		try {
			pdLogfileTemplate = new PropertyDescriptor("logfileTemplate",
					weblogSaverClass);
			pdLogfileTemplate.setPropertyEditorClass(FilePropertyEditor.class);

			pdDir = new PropertyDescriptor("dir", weblogSaverClass);
			pdDir.setPropertyEditorClass(DirectoryPropertyEditor.class);
			pdDir.setDisplayName("outputDirectory");

			pdFilenameExtension = new PropertyDescriptor("filenameExtension",
					weblogSaverClass);
			
			pdPrefix = new PropertyDescriptor("prefix", weblogSaverClass);

		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		PropertyDescriptor list[] = { pdPrefix, pdFilenameExtension, pdDir, pdLogfileTemplate };
		return list;
	}

}
