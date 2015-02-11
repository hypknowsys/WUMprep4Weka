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
 * WUMprepConfigurationBeanInfo.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep4weka.gui;

import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

import org.hypknowsys.wumprep.config.WUMprepConfiguration;

/**
 * BeanInfo for the {@link WUMprepConfiguration} class.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepConfigurationBeanInfo.java,v 1.3 2005/10/16 13:24:32 cpohle Exp $
 */
public class WUMprepConfigurationBeanInfo extends SimpleBeanInfo {

	/**
	 * Class reference
	 */
	private final static Class wumPrepConfigurationClass = WUMprepConfiguration.class;

	@Override
	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor desc = new BeanDescriptor(wumPrepConfigurationClass,
				WUMprepConfigurationCustomizer.class);
		desc.setName("WUMprep config editor");
		desc.setShortDescription("WUMprep configuration editor");
		return desc;
	}
}
