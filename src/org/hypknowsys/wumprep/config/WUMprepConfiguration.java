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
 * WUMprepConfiguration.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hypknowsys.wumprep4weka.gui.WUMprepConfigurationCustomizer;
import org.hypknowsys.wumprep4weka.gui.WUMprepConfigurationEditor;

/**
 * The main interface for handling WUMprep configuration files.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepConfiguration.java,v 1.4 2005/10/16 13:25:42 cpohle Exp $
 */
public class WUMprepConfiguration implements Serializable {

	/**
	 * Indicators standing for the several sections that a WUMprep configuration
	 * file is comprised of.
	 */
	private enum ElementType {
		/**
		 * Blank line.
		 */
		BLANK,
		/**
		 * Comment (line starts with "#")
		 */
		COMMENT,
		/**
		 * Key ("keyName =...")
		 */
		KEY,
		/**
		 * Key-value-pair ("keyName = value")
		 */
		KEY_VALUE,
		/**
		 * Section label ("[sectionName]")
		 */
		SECTION_LABEL,
		/**
		 * Unknown element (doesn't match any implemented regexp)
		 */
		UNDEFINED,
		/**
		 * Value ("someString")
		 */
		VALUE
	}

	/**  */
	private static final long serialVersionUID = 1L;;

	/**
	 * Holds the file header (usually the copyright-notice and some about-text)
	 */
	private WUMprepConfigComment configHeader = new WUMprepConfigComment();

	/**
	 * Filter that is used by {@link WUMprepConfigurationCustomizer} (and
	 * {@link WUMprepConfigurationEditor})
	 */
	private String editSections = "";

	/**
	 * Holds the URI (fully qualified path) of the configuration file modeled by
	 * the instance.
	 * 
	 */
	private String filePath = "";

	/**
	 * The executing platform's line separator.
	 */
	private String newline = System.getProperty("line.separator");

	/**
	 * This configuration's sections.
	 */
	private Vector<WUMprepConfigSection> sections = new Vector<WUMprepConfigSection>();

	/**
	 * Load a new, default configuration.
	 */
	public WUMprepConfiguration() {
		try {
			this.filePath = null;
			this.fromStream(getClass().getResourceAsStream(
					"resources/wumprep_default.conf"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the WUMprep configuration from <tt>file</tt>.
	 * 
	 * @param file
	 */
	public WUMprepConfiguration(File file) {
		this.filePath = file.getAbsolutePath();
		try {
			this.fromStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the WUMprep configuration from the file given by <tt>path</tt>.
	 * 
	 * @param path
	 *          Path of the WUMprep configuration file
	 */
	public WUMprepConfiguration(String path) {
		this(new File(path));
	}

	/**
	 * Loads the WUMprep configuration from the file given by <tt>resource</tt>.
	 * 
	 * @param resource
	 *          The resource to load as WUMprep cofiguration file.
	 * @throws URISyntaxException
	 */
	public WUMprepConfiguration(URL resource) throws URISyntaxException {
		this(new File(resource.toURI()));
	}

	/**
	 * Appends the {@link WUMprepConfigSection} <tt>section</tt> to the
	 * configuration.
	 * 
	 * @param section
	 */
	public void addSection(WUMprepConfigSection section) {
		this.sections.addElement(section);
		section.setConfig(this);
	}

	/**
	 * Parses a WUMprep configuration file from an input stream and initializes
	 * <code>this WUMprepConfiguration</code> with the data read.
	 * 
	 * @param stream
	 *          The streamed WUMprep configuration
	 * @throws IOException
	 *           if a read error occurs.
	 */
	private void fromStream(InputStream stream) throws IOException {
		ElementType prevElementType = ElementType.UNDEFINED;
		ElementType currElementType = ElementType.UNDEFINED;
		WUMprepConfigComment currComment = new WUMprepConfigComment();
		WUMprepConfigSection currSection = new WUMprepConfigSection(this);
		WUMprepConfigValueList currValueList = new WUMprepConfigValueList();
		Matcher matcher = null;

		// Initialize patterns for line/element type detection
		Pattern pattBlank = Pattern.compile("^\\s*$");
		Pattern pattComment = Pattern.compile("^(#\\s.*|#$)");
		Pattern pattLabel = Pattern.compile("^\\[(\\S+)\\]$");
		Pattern pattKey = Pattern.compile("^(\\S+)\\s*=\\.{3}\\s*$");
		Pattern pattKeyValue = Pattern.compile("^([(^#)(\\S)]+)\\s*=\\s*(.+)$");
		Pattern pattValue = Pattern.compile("^(.+)$");

		// Open the file for line-by-line reading
		LineNumberReader lnReader = new LineNumberReader(new InputStreamReader(
				stream));

		String currLine = lnReader.readLine();
		while (currLine != null) {
			// Determine the type of the current line
			if (pattBlank.matcher(currLine).matches()) {
				currElementType = ElementType.BLANK;
			} else if (pattComment.matcher(currLine).matches()) {
				currElementType = ElementType.COMMENT;
			} else if (pattLabel.matcher(currLine).matches()) {
				currElementType = ElementType.SECTION_LABEL;
			} else if (pattKey.matcher(currLine).matches()) {
				currElementType = ElementType.KEY;
			} else if (pattKeyValue.matcher(currLine).matches()) {
				currElementType = ElementType.KEY_VALUE;
			} else if (pattValue.matcher(currLine).matches()) {
				currElementType = ElementType.VALUE;
			} else
				currElementType = ElementType.UNDEFINED;

			// System.err.printf("%s\n", currElementType + ": " + currLine);

			// Check wheter we've a finished value list
			if (prevElementType == ElementType.VALUE
					&& currElementType != ElementType.VALUE) {
				currSection.addSetting(currValueList);
			}

			switch (currElementType) {
			case COMMENT:
				matcher = pattComment.matcher(currLine);
				matcher.matches();
				currComment.append(matcher.group(1));
				break;

			case BLANK:
				if (currSection.getName().equals("") && this.getHeader().length() == 0) {
					this.setHeader(currComment);
					currComment = new WUMprepConfigComment();
				}
				break;

			case SECTION_LABEL:
				// add the current section to the configuration
				this.addSection(currSection);

				// start a new section and add the preceding comment to it
				matcher = pattLabel.matcher(currLine);
				matcher.matches();
				currSection = new WUMprepConfigSection(matcher.group(1), currComment,
						this);
				currComment = new WUMprepConfigComment();
				break;

			case KEY_VALUE:
				matcher = pattKeyValue.matcher(currLine);
				matcher.matches();
				currSection.addSetting(new WUMprepConfigValue(matcher.group(1), matcher
						.group(2), currComment));
				currComment = new WUMprepConfigComment();
				break;

			case KEY:
				matcher = pattKey.matcher(currLine);
				matcher.matches();
				currValueList = new WUMprepConfigValueList(matcher.group(1),
						currComment);
				currComment = new WUMprepConfigComment();
				break;

			case VALUE:
				matcher = pattValue.matcher(currLine);
				matcher.matches();
				currValueList.addValue(matcher.group(1));
				break;

			default:

			} // end of switch

			prevElementType = currElementType;
			currLine = lnReader.readLine();

		} // end of while

		// add the last section to config
		this.addSection(currSection);
	}

	/**
	 * Signals an editor interface that only a certain section should be presented
	 * to the user.
	 * 
	 * @return The section label to be editet.
	 */
	public String getEditSections() {
		return editSections;
	}

	/**
	 * Gets the full path of <this> WUMprep configuration file.
	 * 
	 * @return Returns the filePath.
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Getter of the property <tt>header</tt>
	 * 
	 * @return Returns the configHeader.
	 */
	public WUMprepConfigComment getHeader() {
		return configHeader;
	}

	/**
	 * Returns an entire section from the WUMprep configuration file.
	 * 
	 * @param sectionName
	 *          Name of the section to retrieve
	 * @return The section <tt>sectionName</tt> if it exists, else null.
	 */
	public WUMprepConfigSection getSection(String sectionName) {
		WUMprepConfigSection s = null;
		Iterator iter = getSections().iterator();

		while (iter.hasNext()) {
			s = (WUMprepConfigSection) iter.next();
			if (s.getName().equals(sectionName))
				return s;
		}
		return s;
	}

	/**
	 * Getter of the property <tt>sections</tt>
	 * 
	 * @return Returns the sections.
	 */
	public Vector<WUMprepConfigSection> getSections() {
		return sections;
	}

	/**
	 * Flag for signaling an editor interface that only certain sections of the
	 * configuration should be presented to the user for modification. Set this to
	 * null if the whole configuration might be edited. If you want multiple, but
	 * not all sections to be available in the editor, separate the section names
	 * by ':'.
	 * 
	 * @param editSections
	 *          The section(s) to be available in an editor interface.
	 */
	public void setEditSections(String editSections) {
		this.editSections = editSections;
	}

	/**
	 * Setter of the property <tt>uri</tt>
	 * 
	 * @param uri
	 *          The filePath to set.
	 */
	public void setFilePath(String uri) {
		this.filePath = uri;
	}

	/**
	 * Sets the configuration file header text.
	 * 
	 * @param headerText
	 *          The text to set as the WUMprep configuration file header.
	 */
	public void setHeader(String headerText) {
		setHeader(new WUMprepConfigComment(headerText));
	}

	/**
	 * Setter of the property <tt>header</tt>
	 * 
	 * @param header
	 *          The configHeader to set.
	 */
	public void setHeader(WUMprepConfigComment header) {
		configHeader = header;
	}

	/**
	 * Updates the given <tt>section</tt>. If <tt>section</tt> is not already
	 * part of the configuration, nothing happens.
	 * 
	 * @param section
	 *          The section to update
	 */
	public void setSection(WUMprepConfigSection section) {
		for (WUMprepConfigSection s : sections) {
			if (s.getName().equals(section.getName())) {
				s = section;
			}
		}
	}

	/**
	 * Setter of the property <tt>sections</tt>
	 * 
	 * @param sections
	 *          The sections to set.
	 */
	public void setSections(Vector<WUMprepConfigSection> sections) {
		this.sections = sections;
	}

	/**
	 * Saves itself into a file that can be loaded again with the
	 * {@link #WUMprepConfiguration(File)} constructor at a later time.
	 * 
	 * @param filename
	 *          The filename to save this configuration as.
	 */
	public void toFile(String filename) {
		FileWriter writer;
		try {
			writer = new FileWriter(new File(filename));
			writer.write(toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		String configText = new String();

		if (configHeader.length() > 0)
			configText += configHeader.toString() + newline;

		for (WUMprepConfigSection section : sections)
			configText += section.toString();

		return configText;
	}

}
