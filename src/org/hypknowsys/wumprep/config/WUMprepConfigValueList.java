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
 * WUMprepConfigValueList.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep.config;

import java.util.LinkedList;
import java.util.List;


/**
 * A value list configuration setting from a WUMprep configuration file.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepConfigValueList.java,v 1.4 2005/10/16 13:25:42 cpohle Exp $
 */
public class WUMprepConfigValueList extends
		org.hypknowsys.wumprep.config.WUMprepConfigSetting {

	/**  */
	private static final long serialVersionUID = 1L;

	/**
	 * The executing system's line separator 
	 */
	private static String newline = System.getProperty("line.separator");

	/**
	 * This setting's values
	 */
	private LinkedList<String> values = new LinkedList<String>();

	/**
	 * Default constructor for an anonymous value list setting (the name has to
	 * be set later!).
	 */
	public WUMprepConfigValueList() {
		super("");
	}

	/**
	 * Constructor for a new value list setting <tt>name</tt>.
	 * 
	 * @param name The setting's name 
	 */
	public WUMprepConfigValueList(String name) {
		super(name);
	}

	/**
	 * Constructor for an empty value list setting called <tt>name</tt> with
	 * a comment. 
	 * 
	 * @param name The new value list setting's name
	 * @param comment The comment to be attached to the setting
	 */
	public WUMprepConfigValueList(String name, WUMprepConfigComment comment) {
		super(name);
		this.setComment(comment);
	}

	/**
	 * Adds a single value to a value list.
	 * 
	 * @param value The value to add to the value list
	 */
	public void addValue(String value) {
		values.add(value);
	}

	/**
	 * Remove all values from the value list.
	 */
	public void flush() {
		this.values = new LinkedList<String>();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypknowsys.wumprep.configeditor.WUMprepConfigSetting#getValue()
	 */
	@Override
	public String getValue() {
		String valueListText = new String();

		for (String value : values)
			valueListText += value + newline;

		return valueListText;
	}

	/**
	 * Getter of the property <tt>values</tt>
	 * 
	 * @return Returns the values.
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * Setter of the property <tt>values</tt>
	 * 
	 * @param values
	 *            The values to set.
	 */
	public void setValues(List<String> values) {
		this.values = new LinkedList<String>(values);
	}

	/**
	 * Sets the value list's values to a number of strings seperated by newlines.
	 * 
	 * @param values The values as String containing newlines as separators
	 */
	public void setValues(String values) {
		String valueArray[] = values.split(newline);
		
		flush();
		for (int i = 0; i < valueArray.length; i++) {
			this.values.add(valueArray[i]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypknowsys.wumprep.configeditor.WUMprepConfigSetting#toString()
	 */
	@Override
	public String toString() {
		String valueListText = new String();

		if (this.getComment().length() > 0)
			valueListText = this.getComment().toString() + newline;

		valueListText += this.getName() + " =..." + newline;

		for (String value : values)
			valueListText += value + newline;

		return valueListText + newline;
	}

}
