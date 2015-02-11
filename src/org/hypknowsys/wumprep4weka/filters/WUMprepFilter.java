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
 * WUMprepFilter.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */

package org.hypknowsys.wumprep4weka.filters;

import java.io.IOException;
import java.io.PipedReader;
import java.io.Serializable;
import java.util.HashMap;

import org.hypknowsys.wumprep.WUMprepWrapper;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.gui.Logger;
import weka.gui.beans.LogWriter;

/**
 * Abstract base class for creating filter adapters to WUMprep Perl scripts.
 * 
 * For each WUMprep Perl script that should be available from the Weka
 * environment, a specialized wrapper class has to be written that inherits from
 * <code>this</code>. The primary purpose of this specialized class is to
 * parameterize <code>WUMprepWrapper</code>.
 * 
 * As practically all neccessary services are already implemented in
 * <code>WUMprepWrapper</code>, the code for a wrapper for
 * "WUMprep4WekaTest.pl" reduces to these few lines of code:
 * 
 * <pre>
 * package org.hypknowsys.wumprep4weka.filters;
 * 
 * public class WUMprepFilterTest extends WUMprepFilter {
 * 
 * 	public WUMprepFilterTest() {
 * 		super();
 * 		m_args = &quot;&quot;;
 * 		m_wumprepScript = &quot;WUMprep4WekaTest.pl&quot;;
 * 	}
 * }
 * </pre>
 * 
 * In general, derived classes must just overwrite {@link #m_wumprepScript} and
 * {@link #m_args}, which are the file name of the WUMprep Perl script relative
 * to the WUMprep "src" directory and the <code>String</code> with the command
 * line arguments to be passed to the script.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepFilter.java,v 1.4 2005/10/24 19:19:46 cpohle Exp $
 */
public abstract class WUMprepFilter extends Filter implements LogWriter,
		Serializable {

	/**
	 * Implements a thread for reading a script's output from {@link #m_reader}
	 * and sending it as an {@link Instance} to {@link #m_filter}.
	 */
	private class ScriptOutputReader extends Thread implements Serializable {
		/** Write output to {@link #m_filter} */
		private static final int INSTANCE_OUTPUT = 1;

		/** Write output to {@link #m_logger} */
		private static final int LOG_OUTPUT = 2;

		/**  */
		private static final long serialVersionUID = -1619352265466355514L;

		/** {@link WUMprepFilter} to send the parsed {@link Instance} to */
		private WUMprepFilter m_filter = null;

		/** A <code>weka.gui.Logger</code> for output writing */
		private Logger m_log = null;

		/**
		 * A label for identifying messages from this filter (prepended to the
		 * message)
		 */
		private String m_logId = "";

		/** Either {@link #INSTANCE_OUTPUT} or {@link #LOG_OUTPUT} */
		private int m_outputMode = 0;

		/** Reader for the script's output */
		private transient PipedReader m_outputPipe = null;

		/**
		 * Creates a new <code>ScriptOutputReader</code> that writes its input to
		 * a {@link weka.gui.Logger}. Use this constructor to process a WUMprep
		 * script's <tt>stderr</tt> output.
		 * 
		 * @param logger
		 *          The logger to send the input to
		 * @param outputPipe
		 *          Reader for the script's output.
		 * @param logId
		 *          A label for identifying messages from this filter (prepended to
		 *          the message)
		 */
		ScriptOutputReader(Logger logger, PipedReader outputPipe, String logId) {
			this.setName("WUMprepFilter output reader");
			m_log = logger;
			m_logId = logId;
			m_outputPipe = outputPipe;
			m_outputMode = LOG_OUTPUT;
		}

		/**
		 * Creates a new <code>ScriptOutputReader</code> that sends its input as
		 * {@link Instance}s to a <code>WUMprepFilter</code>. Use this
		 * constructor to process a WUMprep script's <tt>stdout</tt> output.
		 * 
		 * @param filter
		 *          The {@link WUMprepFilter} to send the parsed {@link Instance}
		 *          to.
		 * @param outputPipe
		 *          Reader for the script's output.
		 */
		ScriptOutputReader(WUMprepFilter filter, PipedReader outputPipe) {
			this.setName("WUMprepFilter output reader");
			this.m_filter = filter;
			m_outputPipe = outputPipe;
			m_outputMode = INSTANCE_OUTPUT;
		}

		public void run() {
			int character;
			StringBuffer buffer = new StringBuffer(1024);

			switch (m_outputMode) {
			case INSTANCE_OUTPUT:
				try {

					org.hypknowsys.wumprep4weka.core.Instances instances = new org.hypknowsys.wumprep4weka.core.Instances(
							m_outputPipe);

					for (int i = 0; i < instances.numInstances(); i++) {
						ScriptOutputReader.this.m_filter.push(instances.instance(i));
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				break;

			case LOG_OUTPUT:
				try {
					do {
						character = m_outputPipe.read();

						if (character != WUMprepWrapper.EOF) {
							buffer.append((char) character);

							if (buffer.toString().endsWith(
									System.getProperty("line.separator"))) {
								m_log.logMessage(m_logId + ": " + buffer.toString().trim());
								buffer = new StringBuffer(1024);
							}
						}
					} while (character != WUMprepWrapper.EOF);

				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			default:
				System.err.println(this.getName() + " Undefined output mode");
			}
		}
	}

	/**
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The arguments to be passed to the script as a <code>HashMap</code>. The
	 * keys of the HashMap are the argument 'names' (e.g., "-c"), the
	 * <code>HashMap</code>'s values are the values of the arguments (e.g.
	 * "somePath/wumprep.conf").
	 */
	protected HashMap<String, String> m_args = new HashMap<String, String>();

	/** The <code>weka.gui.Logger</code> this filter can write its messages to */
	protected Logger m_logger = null;

	/**
	 * Thread that reads the scipt's <tt>stderr</tt> output and sends it to the
	 * logging facility.
	 */
	private transient ScriptOutputReader m_logReader = null;

	/**
	 * Thread that reads the scipt's output and
	 * {@link Filter#push(weka.core.Instance) pushes} it to the filter output.
	 */
	private transient ScriptOutputReader m_reader = null;

	/** Wrapper for the WUMprep script to execute */
	private WUMprepWrapper m_wrapper = null;

	/** The WUMprep Perl script wrapped by this class */
	protected String m_wumprepScript = "NOT_DEFINED";

	/**
	 * Creates a new WUMprep filter and initializes the default values.
	 */
	public WUMprepFilter() {
		super();

		// Always send the '-arff' and '-filter' arguments when invoking WUMprep
		// scripts
		m_args.put("-arff", "");
		m_args.put("-filter", "");
	}

	@Override
	public boolean batchFinished() throws Exception {
		m_wrapper.push((Instance) null);
		m_reader.join();
		return super.batchFinished();
	}

	/**
	 * Returns a string describing this filter
	 * 
	 * @return A description of the filter suitable for displaying in the
	 *         explorer/experimenter gui.
	 */
	abstract public String globalInfo();

	/**
	 * Runs the WUMprep script and connects to it input and output streams.
	 */
	private void initWUMprepSession() {
		// Construct the args string
		String args = "";
		for (String s : m_args.keySet()) {
			args += " " + s;
			if (!m_args.get(s).equals(""))
				args += " \"" + m_args.get(s) + "\"";
		}

		m_wrapper = new WUMprepWrapper(m_wumprepScript, args);

		m_wrapper.start();

		m_reader = new ScriptOutputReader(this, m_wrapper.getOutputPipe());
		m_reader.start();

		m_logReader = new ScriptOutputReader(m_logger, m_wrapper.getErrorPipe(),
				m_wumprepScript);
		m_logReader.start();

		m_wrapper.push(getInputFormat().toString());
	}

	/**
	 * Input an instance for filtering. Ordinarily the instance is processed and
	 * made available for output immediately. Some filters require all instances
	 * be read before producing output.
	 * 
	 * @param instance
	 *          The input instance
	 * @return True if the filtered instance may now be collected with output().
	 * @exception IllegalStateException
	 *              If no input format has been defined.
	 */
	public boolean input(Instance instance) {

		if (getInputFormat() == null) {
			throw new IllegalStateException("No input instance format defined");
		}
		if (m_NewBatch) {
			resetQueue();
			m_NewBatch = false;
		}
		m_wrapper.push(instance);

		return true;
	}

	/**
	 * Sets the format of the input instances.
	 * 
	 * @param instanceInfo
	 *          An Instances object containing the input instance structure (any
	 *          instances contained in the object are ignored - only the structure
	 *          is required).
	 * @return True if the outputFormat may be collected immediately.
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean setInputFormat(Instances instanceInfo) throws Exception {
		// starts a new input batch
		super.setInputFormat(instanceInfo);

		// start a new WUMprep script session
		initWUMprepSession();

		// considered here automatically
		// TODO Change this for using the scritpt's true output format
		super.setOutputFormat(m_wrapper.getScriptOutputFormat(instanceInfo));

		return true;
	}

	public void setLog(Logger logger) {
		m_logger = logger;
	}
}
