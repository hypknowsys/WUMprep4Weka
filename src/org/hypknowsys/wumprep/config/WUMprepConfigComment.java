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
 * WUMprepConfigComment.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep.config;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class holds comments from the WUMprep configuration file.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepConfigComment.java,v 1.5 2005/10/16 13:25:42 cpohle Exp $
 */
public class WUMprepConfigComment implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;

	/**
	 * @uml.property name="commentText"
	 */
	private String commentText = "";

	/**
	 * Holds the executing platform's line separator
	 */
	private String newline = System.getProperty("line.separator");

	/**
	 * Creates a new, empty <tt>WUMprepConfigComment</tt>.
	 */
	public WUMprepConfigComment() {
		commentText = "";
	}

	/**
	 * Creates a new <tt>WUMprepConfigComment</tt> and initializes it with
	 * <tt>text</tt>.
	 * 
	 * @param text
	 */
	public WUMprepConfigComment(String text) {
		commentText = text;
	}

	/**
	 * Getter of the property <tt>commentText</tt>
	 * 
	 * @return Returns the commentText. If commentText is null, it returns an
	 *         empty <tt>String</tt>.
	 */
	public String getCommentText() {
		if (commentText != null)
			return commentText;
		else
			return "";
	}

	/**
	 * @return Returns the comment text with comment signs removed and consecutive
	 *         lines concatenated.
	 */
	public String getHelpText() {
		Pattern p;
		Matcher m;
		String helpText = this.getCommentText();

		p = Pattern.compile("(?m)^#\\s");
		m = p.matcher(helpText);
		helpText = m.replaceAll("");

		p = Pattern.compile("(?m)^=+");
		m = p.matcher(helpText);
		helpText = m.replaceAll("");

		p = Pattern.compile("(?m)[\n\r]{1,2}");
		m = p.matcher(helpText);
		helpText = m.replaceAll(" ");

		p = Pattern.compile("(?m)-{5,}");
		m = p.matcher(helpText);
		helpText = m.replaceAll(newline + newline);

		p = Pattern.compile("[\n\r]{1,2}\\s");
		m = p.matcher(helpText);
		helpText = m.replaceAll(newline);

		p = Pattern.compile("^\\s");
		m = p.matcher(helpText);
		helpText = m.replaceAll("");

		return helpText;
	}

	/**
	 * Setter of the property <tt>commentText</tt>
	 * 
	 * @param commentText
	 *          The commentText to set.
	 */
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	/**
	 * Appends a line to an existing comment.
	 * 
	 * @param commentLine
	 *          The comment line to append.
	 */
	public void append(String commentLine) {
		commentText += commentLine + newline;
	}

	/**
	 * @return The lenght of the comment in characters.
	 */
	public int length() {
		return commentText.length();
	}

	/**
	 * @return The comment text
	 */
	public String toString() {
		return commentText;
	}

}
