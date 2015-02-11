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
 * WeblogLoader.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */

package org.hypknowsys.wumprep4weka.core.converters;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hypknowsys.wumprep.config.LogfileTemplate;
import org.hypknowsys.wumprep.config.WUMprepConfiguration;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.converters.AbstractLoader;
import weka.core.converters.BatchConverter;
import weka.gui.beans.BeanVisual;
import weka.gui.beans.Visible;

/**
 * Reads a Web server log file. The log file format must be specified in a
 * WUMprep log file template, which in turn is specified in the WUMprep
 * configuration file passed to this reader.
 * 
 * <p>
 * Available options are:<br>
 * 
 * -C <path>: The path of the configuration file to load.<br>
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WeblogLoader.java,v 1.6 2005/10/18 23:26:24 cpohle Exp $
 */
public class WeblogLoader extends AbstractLoader implements BatchConverter,
		OptionHandler, Visible {

	/**
	 * Standard logfile formats understood by the loader. See <a
	 * href="http://httpd.apache.org/docs/2.0/mod/mod_log_config.html">
	 * http://httpd.apache.org/docs/2.0/mod/mod_log_config.html</a>.
	 * 
	 * @author Carsten Pohle (cp@pohle.de)
	 */
	public enum LogfileFormat {
		/**
		 * Common Log Format (CLF)
		 */
		COMMON,

		/**
		 * Common Log Format with Virtual Host
		 */
		COMMON_VHOST,

		/**
		 * NCSA extended/combined log format
		 */
		EXTENDED_COMBINED
	}

	/**
	 * The extension of files read by this loader.
	 */
	public static String FILE_EXTENSION = ".log";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Gets the standard <code>BeanVisual</code> icon for displaying this loader
	 * in the GUI.
	 * 
	 * @return The GUI icon for <code>this</code>.
	 */
	private static BeanVisual defaultVisual() {
		return new BeanVisual("WeblogLoader",
				"org/hypknowsys/wumprep4weka/gui/icons/WeblogLoader.gif",
				"org/hypknowsys/wumprep4weka/gui/icons/WeblogLoader_animated.gif");
	};

	/**
	 * Holds the {@link weka.core.Attribute}s describing the log file's fields.
	 */
	private FastVector m_fields = new FastVector();

	/**
	 * Holds the log file to read.
	 */
	protected File m_logFile = null;

	/**
	 * The template for log file parsing
	 */
	private LogfileTemplate m_logfileTemplate;

	/**
	 * Holds the determined structure (header) of the data set.
	 */
	protected Instances m_structure = null;

	/**
	 * Visual for the WeblogLoader GUI-Bean
	 */
	protected BeanVisual m_visual = defaultVisual();

	// private File m_templateFile = new File(System.getProperty("user.dir"));

	/**
	 * The loader's configuration, provides the log file template to use and the
	 * log file to read.
	 */
	private WUMprepConfiguration m_wumPrepConfig = new WUMprepConfiguration();

	/** The config editor section-filter. */
	private String m_configEditSections;

	/**
	 * Default constructor, does basically nothing.
	 */
	public WeblogLoader() {
		super();
		m_wumPrepConfig = new WUMprepConfiguration();
		setConfigEditSections("global");
	}

	/**
	 * Reads a Web log file using a custom WUMprep template file. See the example
	 * m_logfileTemplate fiel that comes with WUMprep for a documentation of the
	 * template file format.
	 * 
	 * @param logfileTemplate
	 * 
	 * @see #WeblogLoader(LogfileFormat)
	 */
	public WeblogLoader(File logfileTemplate) {
		this();
		setTemplate(logfileTemplate);
		reset();
	}

	/**
	 * Loader for Web log file using one of the standard log formats COMMON,
	 * COMMON_VHOST or EXTENDED_COMBINED.
	 * 
	 * @param standardFormat
	 *          The format of the logfile this loader is intended to read.n
	 */
	public WeblogLoader(LogfileFormat standardFormat) {
		this();
		switch (standardFormat) {
		case COMMON_VHOST:
			setTemplate(getClass().getResource(
					"resources/m_logfileTemplate.common-vhost"));
			break;

		case EXTENDED_COMBINED:
			setTemplate(getClass().getResource(
					"resources/m_logfileTemplate.extended-combined"));
			break;

		default:
			setTemplate(getClass().getResource("resources/m_logfileTemplate.common"));

		}

		reset();
	}

	/**
	 * Get the WUMprepConfiguration the loader gets its settings from.
	 * 
	 * @return The configuration used by this loader.
	 */
	public WUMprepConfiguration getConfig() {
		m_wumPrepConfig.setEditSections(getConfigEditSections());
		return m_wumPrepConfig;
	}

	/**
	 * Return the full data set.
	 * 
	 * @return The full data set as a set of Instances
	 * @exception IOException
	 *              if there is no source or parsing fails
	 */
	public Instances getDataSet() throws IOException {
		Instances instances = new Instances(getStructure(), 100);
		Pattern logLinePattern = m_logfileTemplate.getPattern();
		Matcher m = null;
		int errorCount = 0;

		LineNumberReader reader = new LineNumberReader(new FileReader(getLogFile()));

		String line = reader.readLine();

		while (line != null) {
			try {
				m = logLinePattern.matcher(line);
				m.matches();
				Instance inst = new Instance(getStructure().numAttributes());
				inst.setDataset(instances);
				for (int i = 0; i < getStructure().numAttributes(); i++) {
					inst.setValue(i, m.group(i + 1));
				}
				instances.add(inst);
			} catch (java.lang.IllegalStateException e) {
				if (errorCount++ < 100) {
					System.err.println("Log line doesn't match regular expression:");
					System.err.println("  Regex: " + logLinePattern);
					System.err.println("    Log: " + line);
				} else {
					System.err.println("Too many errors - loading cancelled!");
					break;
				}
			}
			line = reader.readLine();
		}

		return instances;
	}

	/**
	 * Returns a description of the file type.
	 * 
	 * @return A short file description
	 */
	public String getFileDescription() {
		return "Web server log files";
	}

	/**
	 * Get the file extension for Web log files.
	 * 
	 * @return The file extension
	 */
	public String getFileExtension() {
		return FILE_EXTENSION;
	}

	/**
	 * Getter for m_logFile. Initializes the variable if neccessary.
	 * 
	 * @return The logfile to be read by this loader.
	 */
	private File getLogFile() {
		if (m_logFile == null) {
			String[] logFiles = m_wumPrepConfig.getSection("global").getSetting(
					"inputLogs").getValue().split(System.getProperty("line.separator"));

			m_logFile = new File(logFiles[0]);
		}
		return m_logFile;
	}

	/**
	 * WeblogLoader is unable to process a data set incrementally.
	 * 
	 * @return Never returns without throwing an exception
	 * @exception IOException
	 *              always. WeblogLoader is unable to process a data set
	 *              incrementally.
	 */
	public Instance getNextInstance() throws IOException {
		throw new IOException("WeblogLoader can't read data sets incrementally.");
	}

	public String[] getOptions() {
		Vector<String> options = new Vector<String>();

		options.add("-C");
		options.add(m_wumPrepConfig.getFilePath());

		return (String[]) options.toArray(new String[options.size()]);
	}

	/**
	 * Determines and returns the structure of the data set as an empty set of
	 * instances.
	 * 
	 * @return The structure of the data set as an empty set of Instances
	 * @throws IOException
	 */
	public Instances getStructure() throws IOException {
		if (this.m_logfileTemplate == null) {
			throw new IOException("No log format has been specified.");
		}

		if (m_structure == null && getLogFile() != null) {
			m_structure = new Instances(getLogFile().getName().replaceAll(
					"\\.[lL][oO][gG]$", ""), m_fields, 0);
		}

		return m_structure;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see weka.gui.beans.Visible#getVisual()
	 */
	public BeanVisual getVisual() {
		return this.m_visual;
	}

	/**
	 * Returns a string describing this Loader
	 * 
	 * @return A description of the Loader suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Reads Instances from a Web log file.";
	}

	/**
	 * Lists the available options
	 * 
	 * @return an enumeration of the available options
	 */
	public java.util.Enumeration listOptions() {

		FastVector newVector = new FastVector(1);

		newVector.addElement(new Option("\tThe WUMprep configuration file to use.",
				"C", 1, "-C <path>"));

		return newVector.elements();
	}

	/**
	 * Resets the loader ready to read a new data set
	 */
	public void reset() {
		m_structure = null;
		m_logFile = null;
		m_fields = null;
		setRetrieval(NONE);
	}

	/**
	 * Sets the WUMprepConfiguration the loader should read its settings from.
	 * 
	 * ATTENTION: The WUMprep Perl scripts allow to specify multiple log files at
	 * a time, which is reflected by the "inputLogs" setting in the config file
	 * being a multi-value setting. However, the current implementation of
	 * WUMprep4weka allows only to load a single log file (the main reason for
	 * this is that Weka doesn't support batch-processing in a similar way as
	 * WUMprep does). For this reason, ONLY THE FIRST LOGFILE DEFINED IN THE
	 * CONFIGURATION IS LOADED! Any other values are simply ignored by this
	 * loader.
	 * 
	 * @param config
	 */
	public void setConfig(WUMprepConfiguration config) {
		reset();
		m_wumPrepConfig = config;
		m_wumPrepConfig.setEditSections("global");
		setTemplate(new File(config.getSection("global").getSetting(
				"inputLogTemplate").getValue()));
	}

	/**
	 * Sets the options.
	 * 
	 * @param options
	 *          the options
	 * @throws Exception
	 *           if options cannot be set
	 */
	public void setOptions(String[] options) throws Exception {

		String configPath = Utils.getOption('C', options);
		if (configPath.length() > 0) {
			reset();
			setConfig(new WUMprepConfiguration(configPath));
		}
	}

	/**
	 * Loaads a Web log template file in WUMprep template format. This template is
	 * used to parse the log lines read by the loader.
	 * 
	 * @param templateFile
	 *          The template file to load.
	 */
	private void setTemplate(File templateFile) {
		try {
			this.m_logfileTemplate = new LogfileTemplate(templateFile);

			FastVector fields = new FastVector();

			for (String fieldName : m_logfileTemplate.getFields()) {
				fields.addElement(new Attribute(fieldName, (FastVector) null));
			}

			this.m_fields = fields;
		} catch (IllegalArgumentException e) {
			System.err.println(e);
		}
	}

	/**
	 * Sets the template to use for parsing the logfile
	 * 
	 * @param templateURL
	 *          URL of the template file
	 */
	private void setTemplate(URL templateURL) {
		try {
			setTemplate(new File(templateURL.toURI()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see weka.gui.beans.Visible#setVisual(weka.gui.beans.BeanVisual)
	 */
	public void setVisual(BeanVisual newVisual) {
		this.m_visual = newVisual;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see weka.gui.beans.Visible#useDefaultVisual()
	 */
	public void useDefaultVisual() {
		this.m_visual = defaultVisual();
	}
	
	/**
	 * Sets the config editor section-filter.
	 * 
	 * @param sections
	 *          Must have the format "section1:section2:...".
	 */
	protected void setConfigEditSections(String sections) {
		m_configEditSections = sections;
		getConfig().setEditSections(m_configEditSections);
	}

	/**
	 * Gets the config editor section-filter.
	 * 
	 * @return The sections to be editable for this filter in the format
	 *         "section1:section2:...".
	 */
	protected String getConfigEditSections() {
		return m_configEditSections;
	}
}
