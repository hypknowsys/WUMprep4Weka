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
 * WUMprepConfigurableFilter.java
 * Copyright (c) 2005 Carsten Pohle (cp AT cpohle de)
 */

package org.hypknowsys.wumprep4weka.filters;

import javax.swing.JOptionPane;

import org.hypknowsys.wumprep.config.WUMprepConfiguration;
import org.hypknowsys.wumprep4weka.core.converters.WeblogLoader;

import weka.gui.beans.ConnectionNotificationConsumer;
import weka.gui.beans.Filter;
import weka.gui.beans.Loader;

/**
 * Abstract base class for creating filter adapters to WUMprep Perl scripts
 * <emph>requireing a WUMprep configuration file</emph>. Please see the
 * {@link org.hypknowsys.wumprep4weka.filters.WUMprepFilter} class'
 * documentation for a more detailed description.
 * 
 * @author Carsten Pohle (cp@cpohle.de)
 * @version $Id: WUMprepConfigurableFilter.java,v 1.1 2005/10/16 13:24:33 cpohle
 *          Exp $
 * @see org.hypknowsys.wumprep4weka.filters.WUMprepFilter
 */
public abstract class WUMprepConfigurableFilter extends WUMprepFilter implements
		ConnectionNotificationConsumer {

	/** The WUMprep configuration file to be used by the script */
	protected WUMprepConfiguration m_config = null;

	/** The config editor section-filter. */
	private String m_configEditSections = "";

	/**
	 * Creates a new <code>WUMprepConfigurableFilter</code> and initializes it
	 * with a blank {@link WUMprepConfiguration}.
	 */
	public WUMprepConfigurableFilter() {
		super();
		m_config = new WUMprepConfiguration();
	}

	public void connectionNotification(String eventName, Object source) {
		if (source instanceof Loader
				&& ((Loader) source).getLoader() instanceof WeblogLoader) {
			setConfig(((WeblogLoader) ((Loader) source).getLoader()).getConfig());
		} else if (source instanceof Filter
				&& ((Filter) source).getFilter() instanceof WUMprepConfigurableFilter)
			setConfig(((WUMprepConfigurableFilter) ((Filter) source).getFilter())
					.getConfig());
	}

	public void disconnectionNotification(String eventName, Object source) {
	}

	/**
	 * Gets the configuration to be used by this filter.
	 * 
	 * @return The configuration.
	 */
	public WUMprepConfiguration getConfig() {
		m_config.setEditSections(getConfigEditSections());
		return this.m_config;
	}

	/**
	 * Gets the config editor section-filter.
	 * 
	 * @return The sections to be editable for this filter in the format
	 *         "section1:section2:...".
	 */
	protected String getConfigEditSections() {
		return m_configEditSections;
	}

	/**
	 * Sets the configuration to be used by this filter.
	 * 
	 * @param config
	 *          The configuration.
	 */
	public void setConfig(WUMprepConfiguration config) {
		if (config != null) {
			config.setEditSections(getConfigEditSections());
			
			if (config.getFilePath() != null
					&& !config.getFilePath().equals(m_config.getFilePath()))
				JOptionPane.showMessageDialog(null, "Using configuration file '"
						+ config.getFilePath() + "'.",
						this.getClass().getSimpleName(), JOptionPane.INFORMATION_MESSAGE);

			this.m_config = config;
			m_args.put("-c", config.getFilePath());
		} else {
			m_args.remove("-c");
			m_config = new WUMprepConfiguration();
			m_config.setEditSections(m_configEditSections);
		}
		if (m_logger != null) {
			m_logger.logMessage(this.getClass().getName()
					+ ": Using configuration file " + getConfig().getFilePath());
		}
	}

	/**
	 * Sets the config editor section-filter.
	 * 
	 * @param sections
	 *          Must have the format "section1:section2:...".
	 */
	protected void setConfigEditSections(String sections) {
		m_configEditSections = sections;
		getConfig().setEditSections(m_configEditSections);
	}

}
