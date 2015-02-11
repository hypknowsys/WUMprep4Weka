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
 * DetectRobots.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */

package org.hypknowsys.wumprep4weka.filters;

/**
 * Weka filter for running the WUMprep <tt>detectRobots.pl</tt> script.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: DetectRobots.java,v 1.2 2005/10/26 22:51:50 cpohle Exp $
 */
public class DetectRobots extends WUMprepConfigurableFilter {

	/**  */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new wrapper for the WUMprep "detectRobots.pl" script.
	 */
	public DetectRobots() {
		super();
		setConfigEditSections("global:rmRobotsSettings");
		m_wumprepScript = "detectRobots.pl";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hypknowsys.wumprep4weka.filters.WUMprepFilter#globalInfo()
	 */
	@Override
	public String globalInfo() {
		return "An instance filter that takes a WUMprep4Weka log file as input and"
				+ " applies heuristics for identifying robot requests in it.";
	}

}
