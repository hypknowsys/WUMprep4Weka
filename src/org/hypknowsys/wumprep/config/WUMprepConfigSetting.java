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
 * WUMprepConfigSetting.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep.config;

import java.io.Serializable;


/**
 * A single setting from a WUMprep configuration file. This class defines
 * only the generic interface and must be subclassed for the several
 * kinds of settings like key-value-pair, value-list etc.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepConfigSetting.java,v 1.3 2005/10/16 13:25:42 cpohle Exp $
 */
public abstract class WUMprepConfigSetting implements Cloneable, Serializable {

    /**
     * @uml.property name="name" readOnly="true"
     */
    private String name = "";

    /**
     * @uml.property name="comment"
     * @uml.associationEnd inverse="wUMprepConfigSetting:org.hypknowsys.wumprep.configeditor.WUMprepConfigComment"
     */
    private WUMprepConfigComment comment = new WUMprepConfigComment();

    /**
     * The default constructor.
     */
    public WUMprepConfigSetting() {
    }
    
	/**
     * Creates a WUMprep configuration setting named <tt>name</tt>.
     * 
	 * @param name
     *          Name of the configuration option this setting object is 
     *          supposed to store. 
	 */
	public WUMprepConfigSetting(String name) {
        this.name = name;
    }
    
    /**
     * Creates a WUMprep configuration setting named <tt>name</tt> and
     * with a comment <tt>comment</tt>.
     * 
     * @param name
     *          Name of the configuration option this setting object is 
     *          supposed to store.
     * @param comment
     *          Comment explaining thist setting.          
     */
    public WUMprepConfigSetting(String name, WUMprepConfigComment comment) {
        this.name = name;
        this.comment = comment;
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
	 * Getter of the property <tt>comment</tt>
	 * 
	 * @return Returns the comment.
	 */
	public WUMprepConfigComment getComment() {
		return comment;
	}

    /**
     * Setter of the property <tt>comment</tt>
     * 
     * @param comment  The comment to set.
     */
    public void setComment(WUMprepConfigComment comment) {
        this.comment = comment;
    }
    
	/**
	 * Setter of the property <tt>comment</tt>
	 * 
	 * @param comment  The comment to set.
	 */
	public void setComment(String comment) {
		this.comment = new WUMprepConfigComment(comment);
	}

    public abstract String toString();
    
    /**
     * Get the setting's value.
     * 
     * @return The value of the setting as a String.
     */
    public abstract String getValue();

    /**
     * Creates a 1-to-1 copy of the setting.
     * 
     * @return The cloned setting.
     */
    public WUMprepConfigSetting getClone() {
    	WUMprepConfigSetting setting = null;
    	
    	try {
				setting = (WUMprepConfigSetting) this.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			return setting;
    }

}
