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
 * WUMprepFilterTest.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep4weka.filters;


/**
 * Wrapper for the test-filter <tt>WUMprep4WekaTest.pl</tt>. This script does
 * almost nothing but piping its input to <tt>stdout</tt>. Its only use is to
 * test whether the WUMprep/Weka integration works.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepFilterTest.java,v 1.3 2005/10/18 23:26:24 cpohle Exp $
 */
public class WUMprepFilterTest extends WUMprepFilter {

	/**  */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new wrapper for the WUMprep "WUMprep4WekaTest.pl" skript.
	 */
	public WUMprepFilterTest() {
		super();
		m_wumprepScript = "WUMprep4WekaTest.pl";
	}

	/* (non-Javadoc)
	 * @see org.hypknowsys.wumprep4weka.filters.WUMprepFilter#globalInfo()
	 */
	@Override
	public String globalInfo()  {
		return "An instance filter that does nothing but taking a WUMprep4Weka " +
			"log file in ARFF format as input, parses it, and returns it without " +
		  "any modification.\n" +
		  "This filter's primary purpose is to test if a WUMprep4Weka "+
		  "configuration is working in principle.";
}
}
