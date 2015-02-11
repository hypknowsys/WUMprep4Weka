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
 * WeblogSaver.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep4weka.core.converters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.hypknowsys.wumprep.config.LogfileTemplate;
import org.hypknowsys.wumprep.config.WUMprepConfiguration;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.converters.AbstractSaver;
import weka.core.converters.BatchConverter;

/**
 * Saves an Arff file as (Web) log file. The log file's format is defined by a
 * template, which in turn is defined in a WUMprep configuration file passed to
 * the saver.
 * 
 * The template file contains a protoypical log line with field names as
 * placeholders. These placeholders are replaced by the saver with
 * correspondingly named fields from the Arff file. Template fields that do not
 * have an equivalent in the Arff file are not replaced.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WeblogSaver.java,v 1.4 2005/10/16 13:25:42 cpohle Exp $
 */
public class WeblogSaver extends AbstractSaver implements BatchConverter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The WUMprep configuration the saver takes its parameters from
	 */
	private WUMprepConfiguration config = new WUMprepConfiguration();

	/**
	 * The log file written by this saver
	 */
	private File file = null;

	/**
	 * The filename extension used for the output file
	 */
	private String filenameExtension = null;

	/**
	 * The log file template defining the output file's format.
	 */
	private LogfileTemplate logfileTemplate = null;

	/**
	 * The prefix attached to the output file
	 */
	private String prefix = null;

	/**
	 * The writer used for producing the output
	 */
	private BufferedWriter writer = null;

	/**
	 * Creates a new WeblogSaver with default settings.
	 */
	public WeblogSaver() {
		super();
		resetOptions();
	}

	/**
	 * Takes a single <code>Instance</code> and compiles it into a log line
	 * according to the WUMprep template defined in {@link #logfileTemplate}.
	 * 
	 * @param inst
	 *          The instance to write as a log line
	 * @return The log line
	 */
	private String compileLogLine(Instance inst) {
		String logLine = this.logfileTemplate.getTemplate();

		for (int i = 0; i < inst.numAttributes(); i++) {
			Attribute a = inst.attribute(i);

			logLine = logLine.replace("@" + a.name() + "@", inst.stringValue(a));
		}

		return logLine;
	}

	public String filePrefix() {
		return prefix;
	}

	/**
	 * Gets the WUMprep configuration this saver takes its parameters from.
	 * 
	 * @return The WUMprep configuration
	 */
	public WUMprepConfiguration getConfig() {
		return config;
	}

	/**
	 * Gets the output directory. This is just an alias for {@link #retrieveDir()}
	 * in ordert to make this property JavaBean conformant.
	 * 
	 * @return The output directory
	 */
	public File getDir() {
		if (file.isDirectory())
			return file.getAbsoluteFile();
		else
			return file.getParentFile();
	}

	/**
	 * Gets the file this <code>WeblogSaver</code> writes into.
	 * 
	 * @return The file to write into.
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Gets the filename extension for the output file.
	 * 
	 * @return The filename extension
	 */
	public String getFilenameExtension() {
		return filenameExtension;
	}

	/**
	 * Sets the log file template defining the format of the output file.
	 * 
	 * @return The template file
	 */
	public String getLogfileTemplate() {
		return logfileTemplate.getTemplateFile();
	}

	/**
	 * Gets the file's prefix.
	 * 
	 * @return The prefix.
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * Gets the writer.
	 * 
	 * @return The writer
	 */
	public BufferedWriter getWriter() {
		if (writer == null)
			try {
				writer = new BufferedWriter(new FileWriter(file));
			} catch (IOException e) {
				e.printStackTrace();
			}

		return writer;
	}

	/**
	 * Returns a string describing this Saver
	 * 
	 * @return A description of the Saver suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String globalInfo() {
		return "Writes instances from an ARFF file into a Web log file.";
	}

	/**
	 * Resets the Saver
	 */
	public void resetOptions() {

		super.resetOptions();
		logfileTemplate = new LogfileTemplate();
		filenameExtension = ".log";
		file = new File(System.getProperty("user.dir"));
		config = new WUMprepConfiguration();
		config.setEditSections("global");
		prefix = "weka-export";
	}

	/**
	 * Sets the writer to null.
	 */
	public void resetWriter() {
		writer = null;
	}

	@Override
	public String retrieveDir() {
		if (!(file == null))
			return file.getParent();
		else
			return "";
	}

	/**
	 * Get the file this saver writes to.
	 * 
	 * @return The output file.
	 */
	public File retrieveFile() {
		return file;
	}

	/**
	 * Set the WUMprep configuration file this saver should take its parameters
	 * from.
	 * 
	 * @param config
	 *          The WUMprep configuration
	 */
	public void setConfig(WUMprepConfiguration config) {
		this.config = config;
	}

	@Override
	public void setDestination(File file) {
		setFile(file);
	}

	@Override
	public void setDestination(OutputStream output) throws IOException {
		super.setDestination(output);
	}

	/**
	 * Sets the output directory.
	 * 
	 * @param dir
	 *          The output directory
	 * @throws IOException
	 *           If <code>dir</code> is not a directory.
	 */
	public void setDir(File dir) throws IOException {
		if (dir.isDirectory())
			setDir(dir.getAbsolutePath());
		else
			throw new IOException("'" + dir.getAbsolutePath()
					+ "' is not a directory.");
	}

	@Override
	public void setDir(String dir) {
		String fileSeparator = System.getProperty("file.separator");
		if (!dir.endsWith(fileSeparator))
			dir = dir + fileSeparator;

		file = new File(dir + file.getName());
	}

	@Override
	public void setDirAndPrefix(String relationName, String add) {
		try {
			if (getDir().equals(""))
				setDir(System.getProperty("user.dir"));
			
			if (getPrefix().equals(""))
				setFile(new File(getDir() + File.separator + relationName + add
						+ getFilenameExtension()));
			else
				setFile(new File(getDir() + File.separator + getPrefix() + "_" + relationName
						+ add + getFilenameExtension()));
			setDestination(getFile());
		} catch (Exception ex) {
			System.err
					.println("File prefix and/or directory could not have been set.");
			ex.printStackTrace();
		}
	}

	@Override
	public void setFile(File outputFile) {
		file = outputFile;
		writer = null;
	}

	/**
	 * Set the filename extension for the output file (defaults to ".log").
	 * 
	 * @param filenameExtension
	 *          The filename extension
	 */
	public void setFilenameExtension(String filenameExtension) {
		this.filenameExtension = filenameExtension;
	}

	/**
	 * Sets the prefix for the export file.
	 * 
	 * @param prefix The prefix.
	 *
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Sets the log file template defining the format of the output file.
	 * 
	 * @param templateFile
	 *          The template file
	 */
	public void setLogfileTemplate(String templateFile) {
		if (!templateFile.equals(""))
			if (this.logfileTemplate == null)
				this.logfileTemplate = new LogfileTemplate(templateFile);
			else
				this.logfileTemplate.setTemplateFile(templateFile);
	}

	@Override
	/**
	 * Writes a Batch of instances
	 * 
	 * @throws IOException
	 *           throws IOException if saving in batch mode is not possible
	 */
	public void writeBatch() throws IOException {

		if (getInstances() == null)
			throw new IOException("No instances to save");
		if (getRetrieval() == INCREMENTAL)
			throw new IOException("Batch and incremental saving cannot be mixed.");
		setRetrieval(BATCH);
		setWriteMode(WRITE);
		if (retrieveFile() == null || getWriter() == null) {
			// print out attribute names as first row
			for (int i = 0; i < getInstances().numAttributes(); i++) {
				System.out.print(getInstances().attribute(i).name());
				if (i < getInstances().numAttributes() - 1) {
					System.out.print(",");
				} else {
					System.out.println();
				}
			}
			for (int i = 0; i < getInstances().numInstances(); i++) {
				System.out.println(getInstances().instance(i));
			}
			setWriteMode(WAIT);
			return;
		}

		PrintWriter outW = new PrintWriter(getWriter());
		for (int i = 0; i < getInstances().numInstances(); i++) {
			outW.println(compileLogLine(getInstances().instance(i)));
		}
		outW.flush();
		outW.close();
		setWriteMode(WAIT);

	}
}
