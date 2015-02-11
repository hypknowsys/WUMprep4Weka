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
 * WUMprepConfigSection.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep.config;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * This is a container for a single section of a wumprep.conf file.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepConfigSection.java,v 1.4 2005/10/16 13:25:42 cpohle Exp $
 */
public class WUMprepConfigSection implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 211322720493908906L;

	/**
	 * Holds this section's comment
	 */
	private WUMprepConfigComment comment = new WUMprepConfigComment();

	/**
	 * Holds a reference to the <code>WUMprepConfiguration</code> this section
	 * belongs to.
	 */
	private WUMprepConfiguration config;

	/**
	 * This section's name.
	 */
	private String name = "";

	/**
	 * The executing platform's line separator.
	 */
	private String newline = System.getProperty("line.separator");

	/**
	 * This section's settings
	 */
	private LinkedHashMap<String, WUMprepConfigSetting> settingsMap = new LinkedHashMap<String, WUMprepConfigSetting>();

	/**
	 * Creates a new {@link WUMprepConfigSection} attached to
	 * {@link WUMprepConfiguration}.
	 * 
	 * @param config
	 *          The {@link WUMprepConfiguration} this section belongs to.
	 */
	public WUMprepConfigSection(WUMprepConfiguration config) {
		this.name = "";
		this.config = config;
	}

	/**
	 * Constructor for a new {@link WUMprepConfigSection} <tt>name</tt>, which
	 * is attached to {@link WUMprepConfiguration}.
	 * 
	 * @param name
	 *          Name of the section to create.
	 * @param config
	 *          The {@link WUMprepConfiguration} this section belongs to.
	 */
	public WUMprepConfigSection(String name, WUMprepConfiguration config) {
		this(config);
		this.name = name;
	}

	/**
	 * Constructor for a new commented {@link WUMprepConfigSection} <tt>name</tt>,
	 * which is attached to a new {@link WUMprepConfiguration}.
	 * 
	 * @param name
	 *          Name of the section to create.
	 * @param comment
	 *          The section's comment text
	 * @param config
	 *          The {@link WUMprepConfiguration} this section belongs to.
	 */
	public WUMprepConfigSection(String name, WUMprepConfigComment comment,
			WUMprepConfiguration config) {
		this(name, config);
		this.comment = comment;
	}

	/**
	 * Add a setting to this section.
	 * 
	 * @param setting
	 *          The setting to add.
	 */
	public void addSetting(WUMprepConfigSetting setting) {
		this.settingsMap.put(setting.getName(), setting);
	}

	/**
	 * Checks whether a certain setting is defined in this section.
	 * 
	 * @param name
	 *          Name of the setting to look for.
	 * @return True, if the setting is defined in this section, false otherwise.
	 */
	public boolean defined(String name) {
		return settingsMap.get(name) != null;
	}

	/**
	 * Getter of the property <tt>comment</tt>
	 * 
	 * @return Returns the comment.
	 */
	public WUMprepConfigComment getComment() {
		return comment;
	}

	/**
	 * Gets the <code>WUMprepConfiguration</code> this section belongs to.
	 * 
	 * @return The parent configuration.
	 */
	public WUMprepConfiguration getConfig() {
		return config;
	}

	/**
	 * Getter of the property <tt>name</tt>
	 * 
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets a setting from this section.
	 * 
	 * @param name
	 *          The name of the setting to be returned.
	 * @return The setting <tt>name</tt>.
	 */
	public WUMprepConfigSetting getSetting(String name) {
		if (settingsMap.get(name) == null)
			return new WUMprepConfigValue(name, "");
		else
			return (WUMprepConfigSetting) settingsMap.get(name);
	}

	/**
	 * Getter of the property <tt>settings</tt>
	 * 
	 * @return Returns the settingsMap.
	 */
	public LinkedHashMap<String, WUMprepConfigSetting> getSettings() {
		return settingsMap;
	}

	/**
	 * Setter of the property <tt>comment</tt>
	 * 
	 * @param comment
	 *          The comment to set.
	 */
	public void setComment(WUMprepConfigComment comment) {
		this.comment = comment;
	}

	/**
	 * Sets this section's <code>WUMprepConfiguration</code>
	 * 
	 * @param config
	 */
	public void setConfig(WUMprepConfiguration config) {
		this.config = config;
	}

	/**
	 * Setter of the property <tt>settings</tt>
	 * 
	 * @param settings
	 *          The settingsMap to set.
	 */
	public void setSettings(LinkedHashMap<String, WUMprepConfigSetting> settings) {
		this.settingsMap = settings;
	}

	public String toString() {
		String sectionText = new String();
		Iterator iter;

		if (this.getComment().length() > 0)
			sectionText += this.getComment().toString();

		if (this.getName().length() > 0)
			sectionText += "[" + this.getName() + "]" + newline + newline;

		for (iter = this.settingsMap.values().iterator(); iter.hasNext();)
			sectionText += iter.next().toString();

		return sectionText;
	}
}
