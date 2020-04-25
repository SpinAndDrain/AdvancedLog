package de.spinanddrain.advancedlog.logging;

import java.io.File;
import java.io.IOException;

import de.spinanddrain.advancedlog.logging.Log.LocalLogPrefix;

public class LogSession {

	private Log[] logs;
	
	/**
	 * Creates a new <code>LogSession</code> with the default prefix.
	 * 
	 * @param files all custom log files
	 * @see {@link LocalLogPrefix#DEFAULT}
	 */
	public LogSession(File... files) {
		this(LocalLogPrefix.DEFAULT.raw(), files);
	}
	
	/**
	 * Creates a new <code>LogSession</code> with the specified prefix.
	 * 
	 * @param logPrefix {@link Log#Log(String, File)}
	 * @param files all custom log files
	 */
	public LogSession(String logPrefix, File... files) {
		logs = new Log[files.length];
		for(int i = 0; i < files.length; i++) {
			logs[i] = new Log(logPrefix, files[i]);
		}
	}
	
	/**
	 * 
	 * @return the specified log files as <code>Log</code> instance in a array
	 */
	public Log[] getLogs() {
		return logs;
	}

	/**
	 * Opens each stream of the specified log files.
	 * 
	 * @throws IOException
	 * @see {@link Log#openStream()}
	 */
	public void openAll() throws IOException {
		for(int i = 0; i < logs.length; i++) {
			logs[i].openStream();
		}
	}
	
	/**
	 * Opens each stream of the specified log files with the parallel
	 * header index.
	 * 
	 * @param headers
	 * @throws IOException
	 * @throws IndexOutOfBoundsException if the length of the logs is unequal to the length of the headers
	 * @see {@link Log#openStream()}
	 */
	public void openAll(String... headers) throws IOException {
		if(logs.length != headers.length) {
			throw new IndexOutOfBoundsException();
		}
		for(int i = 0; i < logs.length; i++) {
			logs[i].header = headers[i];
			logs[i].openStream();
		}
	}
	
	/**
	 * Closes each stream of the specified log files.
	 * 
	 * @throws IOException
	 * @see {@link Log#close()}
	 */
	public void closeAll() throws IOException {
		for(int i = 0; i < logs.length; i++) {
			logs[i].close();
		}
	}
	
	/**
	 * Closes all remaining open streams of the specified log files.
	 * 
	 * @throws IOException
	 * @see {@link Log#close()}
	 */
	public void closeOpen() throws IOException {
		for(int i = 0; i < logs.length; i++) {
			if(logs[i].isStreamOpen()) {
				logs[i].close();
			}
		}
	}
	
	/**
	 * Checks whether each stream of the specified log files is open.
	 * 
	 * @return true if each stream is open
	 * @see {@link Log#isStreamOpen()}
	 */
	public boolean isEachOpen() {
		for(int i = 0; i < logs.length; i++) {
			if(!logs[i].isStreamOpen()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks whether any stream of the specified log files is open.
	 * 
	 * @return true if any stream is open
	 */
	public boolean isAnyOpen() {
		for(int i = 0; i < logs.length; i++) {
			if(logs[i].isStreamOpen()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Writes a log into the log file by the specified array <b>index</b>.
	 * 
	 * @param index the log file's index in the array
	 * @param message the log
	 * @param useRawPrefix
	 * @see {@link Log#log(String, boolean)}
	 */
	public void log(int index, String message, boolean useRawPrefix) {
		logs[index].log(message, useRawPrefix);
	}
	
	/**
	 * Writes a log into the log file by the specified array <b>index</b> with the
	 * <b>useRawPrefix</b> option disabled.
	 * 
	 * @param index the log file's index in the array
	 * @param message the log
	 * @see {@link Log#log(String)}
	 */
	public void log(int index, String message) {
		this.log(index, message, false);
	}
	
}
