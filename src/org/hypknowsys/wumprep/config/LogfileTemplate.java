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
 * LogfileTemplate.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */

package org.hypknowsys.wumprep.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates a WUMprep log file template.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: LogfileTemplate.java,v 1.2 2005/10/16 13:25:42 cpohle Exp $
 */
public class LogfileTemplate implements Serializable {

	/**  */
	private static final long serialVersionUID = 1L;

	/**
	 * The template file encapsulated by <code>this</code>.
	 */
	private File templateFile = null;

	/**
	 * The template line as defined in the WUMprep template file.
	 */
	private String template = null;

	/**
	 * The regular expression form of the template, suited for parsing a log line.
	 */
	private String logLineRegExp = null;

	/**
	 * The list of fields used in the template
	 */
	private Vector<String> fields = new Vector<String>();

	/**
	 * Gets the temlate file encapsulated by this <code>LogilfeTemplate</code>.
	 * 
	 * @return The template file's absolute path.
	 */
	public String getTemplateFile() {
		if (this.templateFile != null)
			return this.templateFile.getAbsolutePath();
		else
			return "";
	}

	/**
	 * Sets the template file encapsulated by this <code>LogfileTemplate</code>.
	 * 
	 * @param path
	 *          The absolute path of the template file to parse.
	 */
	public void setTemplateFile(String path) {
		File file = new File(path);
		if (file.exists())
			init(file);
		else
			init(null);
	}

	/**
	 * Creates a new, blank <code>LogfileTemplate</code>....
	 */
	public LogfileTemplate() {
		init(null);
	}

	/**
	 * Creates a new <code>LogfileTemplate</code> instance from a given WUMprep
	 * template file.
	 * 
	 * @param templateFile
	 *          The WUMprep configuration file to construct this
	 *          <code>LogfileTemplate</code> from.
	 * @throws IllegalArgumentException
	 *           If the <code>templateFile</code> is not a valid WUMprep
	 *           configuration file.
	 */
	public LogfileTemplate(File templateFile) throws IllegalArgumentException {
		init(templateFile);
	}

	/**
	 * Creates a new <code>LogfileTemplate</code> instance from a given WUMprep
	 * template file.
	 * 
	 * @param templateFilePath
	 *          The path of a WUMprep template file
	 * @throws IllegalArgumentException
	 *           If the <code>templateFile</code> is not a valid WUMprep
	 *           configuration file.
	 */
	public LogfileTemplate(String templateFilePath)
			throws IllegalArgumentException {
		if (templateFilePath != "")
			init(new File(templateFilePath));
		else
			init(null);
	}

	/**
	 * Initializes <code>this</code> by parsing a given WUMprep log file
	 * template and constructing a regular expression from it that can be used for
	 * parsing log lines conforming to the template.
	 * 
	 * @param templateFile
	 *          The WUMprep configuration file to construct this
	 *          <code>LogfileTemplate</code> from.
	 * @throws IllegalArgumentException
	 *           If the <code>templateFile</code> is not a valid WUMprep
	 *           configuration file.
	 */
	private void init(File templateFile) throws IllegalArgumentException {
		String logLineRegExp = "";
		Vector<String> fields = new Vector<String>();
		Pattern ignorePattern = Pattern.compile("^(?:#.*)|(?:\\s*)$");
		Pattern fieldName = Pattern.compile("@(.+?)@");
		Pattern placeholder = Pattern.compile("(@.+?@)");
		Pattern quotedPattern = Pattern.compile("\"\\(\\\\S\\+\\)\"");
		Pattern braces = Pattern.compile("(\\[|\\])");

		if (templateFile != null) {
			// Read the logfile template
			try {
				if (templateFile.exists()) {
					LineNumberReader reader = new LineNumberReader(new FileReader(
							templateFile));

					String line = reader.readLine();
					while (line != null) {
						Matcher m = ignorePattern.matcher(line);

						// ignore blank and commentary lines
						if (!m.matches()) {
							this.template = line;
							// extract field names from the template line
							m.usePattern(fieldName);
							m.reset();
							while (m.find()) {
								fields.addElement(m.group(1));
							}

							// convert template line into regular expression
							m.reset();
							m.usePattern(placeholder);
							logLineRegExp = m.replaceAll("(\\\\S+)");
							m.reset();
							m = quotedPattern.matcher(logLineRegExp);
							logLineRegExp = m.replaceAll("\"(.+)\"");
							m.reset();
							m = braces.matcher(logLineRegExp);
							logLineRegExp = m.replaceAll("\\\\$1");
						}
						line = reader.readLine();
					}
				}

				if (logLineRegExp != "") {
					this.logLineRegExp = logLineRegExp;
					this.fields = fields;

					// Store the file for later reference.
					this.templateFile = templateFile;
				} else if (templateFile.exists()) {
					throw new IllegalArgumentException("The file '"
							+ templateFile.getAbsolutePath()
							+ "' is not a valid WUMprep configuration file.");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// logfileTemplate == null (called from default constructor)
			this.templateFile = null;
			this.fields = fields;
			this.template = "";
			this.logLineRegExp = "";
		}
	}

	/**
	 * Gets the template line as defined in the WUMprep template file.
	 * 
	 * @return The template.
	 */
	public String getTemplate() {
		return this.template;
	}

	/**
	 * Sets the template line.
	 * 
	 * @param template
	 *          The template.
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * Gets the list of fields used in the template, ordered by appearance. This
	 * list may be used as an index for accessing selected fields from a log line
	 * parsed with the regular expression returned by {@link #getRegExp()}.
	 * 
	 * @return The field list.
	 */
	public Vector<String> getFields() {
		return this.fields;
	}

	/**
	 * Gets the regular expression built from the log line template. Every field
	 * matching sub-expression is enclosed in grouping parentheses, so that each
	 * field from a log line can easily be accessed using the
	 * {@link java.util.regex.Matcher#group(int)} method.
	 * 
	 * @return The regular expression that parses a log line formatted according
	 *         to the template.
	 */
	public String getRegExp() {
		return this.logLineRegExp;
	}

	/**
	 * Gets a <code>Pattern</code> that can be used to create a {@link Matcher}
	 * for parsing log lines as in the following example:
	 * 
	 * <pre>
	 *         LogfileTemplate lt = new LogfileTemplate(&quot;someTemplateFile&quot;);
	 *         Matcher m = lt.getPattern.matcher(&lt;String&gt; logLine);
	 *         m.matches();
	 *         
	 *         for (int i = 0; i &lt; getStructure().numAttributes(); i++)
	 *           System.out.println(&quot;Field No. &quot; + i + &quot;: &quot; + m.group(i + 1));
	 * </pre>
	 * 
	 * @return The pattern for parsing log lines.
	 */
	public Pattern getPattern() {
		return Pattern.compile(getRegExp());
	}
}
