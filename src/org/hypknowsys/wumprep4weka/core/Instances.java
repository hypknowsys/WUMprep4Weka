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
 * Instances.java
 * Copyright (c) 2005 Carsten Pohle (cp AT cpohle de)
 */

package org.hypknowsys.wumprep4weka.core;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

/**
 * Instances class for parsing ARFF-encoded Web log files. This class overwrites
 * {@link weka.core.Instances#initTokenizer(java.io.StreamTokenizer)}. In WEKA's
 * implementation, '%' is interpreted as a comment character, while this is a
 * valid character in URIs and thus in HTTP logs.
 * 
 * If we wouldn't override the default <code>Instances</code>' behaviour, the
 * ARFF data lines containing '%' as part of an URI would cause parsing errors.
 * 
 * @author Carsten Pohle (cp@cpohle.de)
 * @version $Id: Instances.java,v 1.1 2005/10/16 13:25:43 cpohle Exp $
 */
public class Instances extends weka.core.Instances {

	/**  */
	private static final long serialVersionUID = 1L;

	/**
	 * @param reader
	 * @throws IOException
	 * @see weka.core.Instances#Instances(java.io.Reader)
	 */
	public Instances(Reader reader) throws IOException {
		super(reader);
	}

	@Override
	protected void initTokenizer(StreamTokenizer tokenizer) {

		tokenizer.resetSyntax();         
    tokenizer.whitespaceChars(0, ' ');    
    tokenizer.wordChars(' ' + 1,'\u00FF');
    tokenizer.whitespaceChars(',',',');
    tokenizer.quoteChar('"');
    tokenizer.quoteChar('\'');
    tokenizer.ordinaryChar('{');
    tokenizer.ordinaryChar('}');
    tokenizer.eolIsSignificant(true);
	}


}
