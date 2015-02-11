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
 * WeblogLoaderBeanInfo.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep4weka.core.converters;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorManager;
import java.beans.SimpleBeanInfo;

import org.hypknowsys.wumprep.config.WUMprepConfiguration;
import org.hypknowsys.wumprep4weka.gui.WUMprepConfigurationEditor;

/**
 * BeanInfo for the {@link WeblogLoader} class. This controls the configuration
 * dialogue for the WeblogLoader node in the Weka KnowledgeFlow.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WeblogLoaderBeanInfo.java,v 1.4 2005/10/16 13:25:42 cpohle Exp $
 */
public class WeblogLoaderBeanInfo extends SimpleBeanInfo {

	/**
	 * Class reference
	 */
	private final static Class weblogLoaderClass = WeblogLoader.class;

	@Override
	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor desc = new BeanDescriptor(weblogLoaderClass);
		desc.setName("Weblog Loader");
		desc.setShortDescription("Loads a Web log file into Weka.");
		return desc;
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor pdConfigSection = null;

		PropertyEditorManager.registerEditor(WUMprepConfiguration.class,
				WUMprepConfigurationEditor.class);

		try {
			pdConfigSection = new PropertyDescriptor("config", weblogLoaderClass);
			pdConfigSection.setDisplayName("WUMprep Configuration");

		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		PropertyDescriptor list[] = { pdConfigSection };
		return list;
	}

}
