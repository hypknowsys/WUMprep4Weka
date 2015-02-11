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
 * ContextHelpChangeListener.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep.configeditor;

/**
 * Interface to be implemented by classes that should be notified about
 * a change of the context help text describing the currently selected item
 * of a {@link WUMprepConfigEditor}.
 * 
 * @author Carsten Pohle
 * @version $Id: ContextHelpChangeListener.java,v 1.3 2005/10/16 13:25:42 cpohle Exp $
 */

public interface ContextHelpChangeListener {
	/**
	 * Invoked whenever the current context help text for the 
	 * {@link WUMprepConfigEditor} changes. 
	 * 
	 * @param helpText The new context help text
	 */
	public void contextHelpChanged(String helpText);
}
