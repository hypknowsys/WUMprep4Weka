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
 * WUMprepWrapper.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */

package org.hypknowsys.wumprep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import weka.core.Instance;
import weka.core.Instances;

/**
 * This class provides functionality for executing WUMprep Perl scripts from
 * Java. It takes {@link Instance}s via the {@link #push(Instance) push()}
 * method and sends them to the standard input of the wrapped Perl script. The
 * script's output can be read via the {@link java.io.PipedReader}s available
 * via {@link #getOutputPipe()} and {@link #getErrorPipe()}.
 * 
 * This class is used by the filters in the package
 * {@link org.hypknowsys.wumprep4weka.filters}.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepWrapper.java,v 1.5 2005/10/24 19:19:46 cpohle Exp $
 */
public class WUMprepWrapper implements Serializable {
	/**  */
	private static final long serialVersionUID = -2676342872471842039L;

	// TODO Leaves Perl.exe running when the script is in an endless loop and this
	// WUMprepWrapper gets destroyed. (cpohle, 2005-09-21)

	/**
	 * Implements a thread for writing input from {@link #m_inputReader} to a
	 * Perls script via {@link #m_inputWriter}.
	 */
	private class ScriptInputWriter extends Thread implements Serializable {
		/**  */
		private static final long serialVersionUID = -1678976008453756781L;

		/** The queue holding the inputs to be sent to the script */
		private LinkedBlockingQueue<String> m_inputQueue = null;

		/**
		 * The stream with the script's input coming from
		 * <code>WUMprepWrapper</code>'s user
		 */
		private transient BufferedReader m_inputReader = null;

		/** The stream for outputting the input data to the Perl script */
		private transient OutputStreamWriter m_inputWriter = null;

		/**
		 * Creates a <code>ScriptInputWriter</code>.
		 * 
		 * @param name
		 *          This <code>Thread</code>'s name
		 * @param inputQueue
		 *          Queue with the script's input.
		 * @param inputWriter
		 *          Writer for outputting the input data to the Perl script.
		 */
		ScriptInputWriter(String name, LinkedBlockingQueue<String> inputQueue,
				OutputStreamWriter inputWriter) {
			this.setName(name);
			m_inputWriter = inputWriter;
			m_inputQueue = inputQueue;
		}

		public void run() {
			try {
				String line;
				do {
					// line = this.inputReader.readLine();
					line = m_inputQueue.poll(10, TimeUnit.SECONDS);
					if (line != null) {
						if (!line.equals(String.valueOf(WUMprepWrapper.EOF))) {
							this.m_inputWriter.write(line);
							this.m_inputWriter.write(System.getProperty("line.separator"));
						} else {
							m_inputWriter.close();
							line = null;
						}
					}
				} while ((line != null));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Implements a thread for reading a script's output from
	 * {@link #m_outputReader} and sending it to {@link #m_outputWriter}.
	 */
	private class ScriptOutputReader extends Thread implements Serializable { 
		/**  */
		private static final long serialVersionUID = -5350154244511037218L;

		/** Reader for the script's output */
		private transient InputStreamReader m_outputReader = null;

		/** Writer for forwarding the script's output */
		private transient PipedWriter m_outputWriter = null;

		/**
		 * Creates a <code>ScriptOutputReader</code>.
		 * 
		 * @param name
		 *          The thread's name
		 * @param outputReader
		 *          Reader for the script's output
		 * @param outputWriter
		 *          Writer for forwarding the script's output
		 */
		public ScriptOutputReader(String name, InputStreamReader outputReader,
				PipedWriter outputWriter) {
			this.setName(name);
			m_outputReader = outputReader;
			m_outputWriter = outputWriter;
		}

		public void run() {
			try {
				int character;

				do {
					character = m_outputReader.read();
					if (character >= 0)
						m_outputWriter.write(character);

				} while ((character >= 0));

				// Signal EOF
				m_outputWriter.close();
				// m_outputWriter.write(WUMprepWrapper.EOF);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** The <code>String</code> that signals the end of a stream */
	public static final int EOF = StreamTokenizer.TT_EOF;

	/**
	 * The {@link Preferences#get} <code>key</code> under which the path to the
	 * WUMprep installation directory is stored.
	 */
	public final static String WUMPREP_HOME_PREFERENCE = "wumprep_home";

	/**
	 * Gets the full path to the WUMprep installation directory as defined in the
	 * {@link Preferences} storage.
	 * 
	 * @return The path to the WUMprep installation directory
	 */
	public static String getWUMprepHome() {
		return Preferences.systemNodeForPackage(WUMprepWrapper.class).get(
				WUMPREP_HOME_PREFERENCE, "UNDEFINED - PLEASE SET!");
	}

	/**
	 * Gets the full prefix (path of WUMprep home plus the scripts' source
	 * directory inside the WUMprep distribution).
	 * 
	 * @return The path prefix of WUMprep scripts.
	 */
	public static String getWUMprepPrefix() {
		return getWUMprepHome() + File.separator + "src" + File.separator;
	}

	/**
	 * The main function for testing purposes.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		System.out.println("Perl: " + WUMprepWrapper.perlInterpreter());
	}

	/**
	 * Get the full path to the Perl interpreter (either "perl.exe" or "perl").
	 * The method first checks whether there exists an environment variable
	 * <tt>PERL_BIN</tt>. If <tt>PERL_BIN</tt> is not set, the method checks
	 * the directories defined in the <tt>PATH</tt> environment variable.
	 * 
	 * @return The path to the Perl interpreter or <code>null</code>, if no
	 *         Perl interpreter could not be found.
	 */
	public static String perlInterpreter() {
		File dir;
		File[] perlFiles = null;

		String dirs[] = System.getenv("PATH").split(File.pathSeparator);

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().equals("perl")
						|| name.toLowerCase().equals("perl.exe"))
					return true;
				else
					return false;
			}
		};

		if (System.getenv("PERL_BIN") != null) {
			dir = new File(System.getenv("PERL_BIN"));
			perlFiles = dir.listFiles(filter);
			if (perlFiles.length > 0)
				return perlFiles[0].getAbsolutePath();
		}

		for (int i = 0; i < dirs.length; i++) {
			dir = new File(dirs[i]);
			perlFiles = dir.listFiles(filter);
			if (perlFiles.length > 0)
				return perlFiles[0].getAbsolutePath();
		}

		return null;
	}

	/**
	 * Saves the WUMprep installation directory in the {@link Preferences}
	 * storage.
	 * 
	 * @param wumPrepHome
	 *          The full path to the WUMprep installation directory on the local
	 *          system.
	 */
	public static void setWUMprepHome(String wumPrepHome) {
		if (wumPrepHome.equals(""))
			wumPrepHome = "UNDEFINED - PLEASE SET!";

		Preferences.systemNodeForPackage(WUMprepWrapper.class).put(
				WUMPREP_HOME_PREFERENCE, wumPrepHome);
	}

	/** The command line arguments passed to the script */
	private String m_args;

	/**
	 * The <code>PipedReader</code> given to clients for receiving the script's
	 * error output
	 */
	private transient PipedReader m_clientErrorReader = null;

	/**
	 * The <code>PipedReader</code> given to clients for receiving the script's
	 * output
	 */
	private transient PipedReader m_clientOutputReader = null;

	/** The stderr stream received from the script */
	private transient InputStreamReader m_errorReader;

	/** The thread receving the script's stderr output */
	private transient Thread m_errorReaderThread = null;

	/** Writer for sending the script's errors to <code>this</code>' user */
	private transient PipedWriter m_errorWriter;

	/** The queue holding the inputs to be sent to the script */
	private transient LinkedBlockingQueue<String> m_inputQueue = null;

	/** The input stream passed to the script */
	private transient OutputStreamWriter m_inputWriter;

	/** The thread processing the script's input */
	private transient Thread m_inputWriterThread = null;

	/** The output stream received from the script */
	private transient InputStreamReader m_outputReader;

	/** The thread receiving the script's stdout output */
	private transient Thread m_outputReaderThread = null;

	/** Writer for sending the script's output to <code>this</code>' user */
	private transient PipedWriter m_outputWriter;

	/** The Perl script's filename */
	private String m_scriptName;

	/** The script process */
	private transient Process m_script = null;

	/**
	 * Crates a wrapper for executing a WUMprep Perl script in a subshell.
	 * 
	 * @param scriptName
	 *          The name of the WUMprep script to execute (the path relative to
	 *          the WUMprep home directory returned by {@link #getWUMprepHome()}.
	 * @param args
	 *          The arguments to be passed to the script.
	 */
	public WUMprepWrapper(String scriptName, String args) {
		// Create the connectors
		m_scriptName = scriptName;
		m_args = args;

		m_outputWriter = new PipedWriter();
		m_errorWriter = new PipedWriter();
		m_inputQueue = new LinkedBlockingQueue<String>();
	}

	/**
	 * Gets the <code>PipedReader</code> providing the script's error output.
	 * 
	 * @return The script's stderr output.
	 */
	public PipedReader getErrorPipe() {
		if (m_clientErrorReader == null)
			try {
				// Thread for reading the script's errors
				m_errorReader = new InputStreamReader(m_script.getErrorStream());
				m_clientErrorReader = new PipedReader(m_errorWriter);

				m_errorReaderThread = new ScriptOutputReader(
						"WUMprepWrapper Error Reader", m_errorReader, m_errorWriter);
				m_errorReaderThread.start();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return m_clientErrorReader;
	}

	/**
	 * Gets the <code>PipedReader</code> providing the script's output.
	 * 
	 * @return The script's stdout output.
	 */
	public PipedReader getOutputPipe() {
		if (m_clientOutputReader == null)
			try {
				// Thread for reading the script's output
				m_outputReader = new InputStreamReader(m_script.getInputStream());
				m_clientOutputReader = new PipedReader(m_outputWriter);

				m_outputReaderThread = new ScriptOutputReader(
						"WUMprepWrapper Output Reader", m_outputReader, m_outputWriter);
				m_outputReaderThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return m_clientOutputReader;
	}

	/**
	 * Semds a given <code>String</code> to the script.
	 * 
	 * @param scriptInput
	 */
	public void push(String scriptInput) {
		m_inputQueue.add(scriptInput);
	}

	/**
	 * Adds an {@link Instance instance} to the script's input queue.
	 * 
	 * @param instance
	 *          An instance to be sent to the WUMprep script for processing.
	 */
	public void push(Instance instance) {
		if (instance != null)
			m_inputQueue.add(instance.toString());
		else
			m_inputQueue.add(String.valueOf(WUMprepWrapper.EOF));
	}

	
	/**
	 * Runs the script wrapped by <code>this WUMprepWrapper</code>.
	 */
	public void start() {
		m_script = runWUMprepScript();

		m_inputWriter = new OutputStreamWriter(m_script.getOutputStream());

		// Thread for writing the script's input
		m_inputWriterThread = new ScriptInputWriter("WUMprepWrapper Input Writer",
				m_inputQueue, m_inputWriter);
		m_inputWriterThread.start();

	}

	/**
	 * Generates the command line and invokes the WUMprep perl script
	 * 
	 * @return The WUMprep process.
	 */
	private Process runWUMprepScript() {
		Process wumPrepProcess = null;
		String commandLine = perlInterpreter() + " \""
				+ WUMprepWrapper.getWUMprepPrefix() + m_scriptName + "\" " + m_args;

		// Run the script
		try {
			wumPrepProcess = Runtime.getRuntime().exec(commandLine);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return wumPrepProcess;
	}

	/**
	 * Creates a dummy dataset from the input format, sends it to the script and
	 * reads the script output's ARFF information that in turn is used to set
	 * <code>this</code>' output format.
	 * 
	 * This mechanism allows a WUMprep script to alter the recordset layout as
	 * long as this change is documented by the output ARFF header. For example,
	 * the <tt>dnsLookup.pl</tt> script changes the <code>host_ip</code> field
	 * to <code>host_dns</code> when performing IP lookups.
	 * 
	 * @param instanceInfo
	 *          The input format.
	 * @return Object containing the output instance structure.
	 */
	public Instances getScriptOutputFormat(Instances instanceInfo) {
		Instances outputFormat = instanceInfo;
		Instances testData = new Instances(instanceInfo);
		Instance testInstance = new Instance(testData.numAttributes());

		testData.delete();
		testInstance.setDataset(testData);

		// Initialize the testInstance's attribute values
		for (int i = 0; i < testInstance.numAttributes(); i++) {
			String aName = testInstance.attribute(i).name();
			if (aName.equals("host_ip"))
				testInstance.setValue(i, "127.0.0.1");
			else if (aName.equals("ts_day"))
				testInstance.setValue(i, "01");
			else if (aName.equals("ts_month"))
				testInstance.setValue(i, "Jan");
			else if (aName.equals("ts_year"))
				testInstance.setValue(i, "2005");
			else if (aName.equals("ts_hour"))
				testInstance.setValue(i, "11");
			else if (aName.equals("ts_minutes"))
				testInstance.setValue(i, "55");
			else if (aName.equals("ts_seconds"))
				testInstance.setValue(i, "00");
			else if (aName.equals("tz"))
				testInstance.setValue(i, "+0200");
			else
				testInstance.setValue(i, aName + "-dummy");
		}
		
		testData.add(testInstance);
		
		WUMprepWrapper testWrapper = new WUMprepWrapper(m_scriptName, m_args);
		testWrapper.start();
		testWrapper.push(testData.toString());
		testWrapper.push((Instance) null);
		
		class ErrorReader extends Thread implements Serializable {
			/**  */
			private static final long serialVersionUID = -488779846603045891L;
			PipedReader m_input = null;
			
			/**
			 * Helper class for reading stderr output from the WUMprep script
			 * 
			 * @param input The script's wrapper's stderr pipe reader
			 */
			ErrorReader(PipedReader input) {
				m_input = input;
				this.start();
			}
			
			public void run() {
				try {
					while (m_input.read() >= 0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// read the stderr output
		new ErrorReader(testWrapper.getErrorPipe());
		
		try {
			// ignore the stderr output
			outputFormat = new  org.hypknowsys.wumprep4weka.core.Instances(testWrapper.getOutputPipe());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outputFormat;
	}

	/**
	 * This should stop the Perl script when the user (or whatever) interrupts
	 * KnowledgeFlow execution.
	 * 
	 * <em>At the moment, this does not seem to work!</em>
	 */
	public void stop() {
		// TODO Stop doesn't work yet
		m_inputWriterThread.interrupt();
		m_outputReaderThread.interrupt();
		m_errorReaderThread.interrupt();
	}
}
