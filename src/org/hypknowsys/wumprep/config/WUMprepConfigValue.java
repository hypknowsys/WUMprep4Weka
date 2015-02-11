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
 * WUMprepConfigValue.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep.config;


/**
 * This is class represents a value-key pair in wumprep.conf
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepConfigValue.java,v 1.4 2005/10/16 13:25:42 cpohle Exp $
 *
 */
public class WUMprepConfigValue extends WUMprepConfigSetting {
    /**  */
	private static final long serialVersionUID = 1L;

		/**
     * The executing system's line separator
     */
    private String newline = System.getProperty("line.separator");
    
    /**
     * This settings value
     */
    private String value = ""; 
    

    /**
     * Constructor for a key-value pair.
     * 
     * @param name Name of the setting
     * @param value Value of the setting
     */
    public WUMprepConfigValue(String name, String value) {
        super(name);
        this.setValue(value);
    }
    
    /**
     * Constructor of a key-value-pair with and a comment (help text).
     * 
     * @param name The setting's name
     * @param value The setting's value
     * @param comment The setting's comment
     */
    public WUMprepConfigValue(String name, String value, 
            WUMprepConfigComment comment) {
        this(name, value);
        this.setComment(comment);
    }
    
	/**
	 * Getter of the property <tt>value</tt>
	 * 
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Setter of the property <tt>value</tt>
	 * 
	 * @param value
	 *            The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

    /* (non-Javadoc)
     * @see org.hypknowsys.wumprep.configeditor.WUMprepConfigSetting#toString()
     */
    @Override
    public String toString() {
        String valueText = new String();
        
        if (this.getComment().length() > 0)
            valueText += this.getComment().toString();
        
        valueText += this.getName() + " = " + this.getValue()  + newline;
        
        return valueText + newline;
    }

}
