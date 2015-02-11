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
 * DnsLookupBeanInfo.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */

package org.hypknowsys.wumprep4weka.filters;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorManager;
import java.beans.SimpleBeanInfo;

import org.hypknowsys.wumprep.config.WUMprepConfiguration;
import org.hypknowsys.wumprep4weka.gui.WUMprepConfigurationEditor;

/**
 * BeanInfo for the
 * {@link org.hypknowsys.wumprep4weka.filters.WUMprepConfigurableFilter} class.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepConfigurableFilterBeanInfo.java,v 1.1 2005/10/16 13:24:33 cpohle Exp $
 */
public class WUMprepConfigurableFilterBeanInfo extends SimpleBeanInfo {

	/**
	 * Class reference
	 */
	private final static Class wumPrepConfigurableFilterClass = WUMprepConfigurableFilter.class;

	@Override
	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor desc = new BeanDescriptor(wumPrepConfigurableFilterClass);
		desc.setName("WUMprep configurable filter");
		desc.setShortDescription("Runs a WUMprep filter");
		return desc;
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor pdConfigSection = null;

		PropertyEditorManager.registerEditor(WUMprepConfiguration.class,
				WUMprepConfigurationEditor.class);

		try {
			pdConfigSection = new PropertyDescriptor("config",
					wumPrepConfigurableFilterClass);
			pdConfigSection.setDisplayName("WUMprep Configuration");

		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

		PropertyDescriptor list[] = { pdConfigSection };
		return list;
	}

}
