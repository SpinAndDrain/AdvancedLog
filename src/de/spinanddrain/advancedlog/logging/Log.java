package de.spinanddrain.advancedlog.logging;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;

import de.spinanddrain.advancedlog.event.LogPerpetuateEvent;
import de.spinanddrain.advancedlog.exception.QuietIOException;

public class Log implements Closeable {

	private File file;
	private BufferedWriter writer;
	protected String prefix, header;
	
	/**
	 * Creates a new <code>Log</code> instance with the specified <b>file</b> as log file
	 * and the default prefix {@link LocalLogPrefix#DEFAULT}.
	 * 
	 * @param file
	 * @see LocalLogPrefix
	 */
	public Log(File file) {
		this.file = file;
		this.prefix = LocalLogPrefix.DEFAULT.prefix;
		this.header = null;
	}
	
	/**
	 * Creates a new <code>Log</code> instance with the specified <b>file</b> as log file
	 * and the specified <b>prefix</b> as prefix.
	 * 
	 * @param file
	 * @see LocalLogPrefix
	 */
	public Log(String logPrefix, File file) {
		this(file);
		this.prefix = logPrefix;
	}
	
	/**
	 * Creates a new <code>Log</code> instance with the specified <b>file</b> as log file,
	 * the specified <b>prefix</b> as prefix and the specified <b>header</b> as first-log header.
	 * 
	 * @param file
	 * @see LocalLogPrefix
	 */
	public Log(String logPrefix, File file, String header) {
		this(logPrefix, file);
		this.header = header;
	}
	
	/**
	 * 
	 * @return the raw log file
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Writes a log into the specified log file.
	 * 
	 * @param log the message that should be written
	 * @param useRawPrefix if true, the prefix gets not converted and is used as its raw value
	 * @throws QuietIOException if the stream is closed or something went wrong while writing the log
	 * @see FileWriter#write(String)
	 */
	public void log(String log, boolean useRawPrefix) {
		LogPerpetuateEvent event = new LogPerpetuateEvent(log, file);
		Bukkit.getPluginManager().callEvent(event);
		if(!event.isCancelled()) {
			String now = useRawPrefix ? prefix : new LocalLogPrefix(prefix).getCurrentPrefix();
			if(isStreamOpen()) {
				try {
					writer.write((prefix.isEmpty() ? "" : now + " ") + event.getLog());
					writer.newLine();
					writer.flush();
				} catch (IOException e) {
					throw new QuietIOException(e.getMessage());
				}
			} else
				throw new QuietIOException("stream not opened yet");
		}
	}
	
	/**
	 * Writes a log into the specified log file without using the raw content of the log prefix.
	 * 
	 * @param log the message that should be written
	 * @see Log#log(String, boolean)
	 */
	public void log(String log) {
		this.log(log, false);
	}
	
	/**
	 * Opens the writing stream of the specified file.
	 * No logs are written before this method is called.
	 * @throws IOException {@link Log#close()}
	 * 
	 */
	public void openStream() throws IOException {
		this.close();
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8));
		if(header != null && file.length() == 0) {
			String saved = prefix;
			prefix = new String();
			this.log(header, true);
			prefix = saved;
		}
	}
	
	/**
	 * 
	 * @return true if the writing stream is currently open, false if not
	 */
	public boolean isStreamOpen() {
		return writer != null;
	}
	
	@Override
	public void close() throws IOException {
		if(writer != null) {
			writer.close();
			writer = null;
		}
	}
	
	public static class LocalLogPrefix {
		
		/**
		 * The default prefix time pattern <b>hh:mm:ss</b>
		 * 
		 */
		public static final LocalLogPrefix DEFAULT = new LocalLogPrefix("[HH:mm:ss]");
		
		/**
		 * The default prefix date pattern <b>yyyy-mm-dd</b>
		 * 
		 */
		public static final LocalLogPrefix DATE = new LocalLogPrefix("[yyyy-MM-dd]");
		
		/**
		 * A combination of the default time and date pattern <b>yyyy-mm-dd hh:mm:ss</b>
		 * 
		 */
		public static final LocalLogPrefix COMBINED = new LocalLogPrefix("[yyyy-MM-dd HH:mm:ss]");
		
		private String prefix;
		
		/**
		 * 
		 * @param prefix string for {@link SimpleDateFormat#SimpleDateFormat(String)}
		 */
		public LocalLogPrefix(String prefix) {
			this.prefix = prefix;
		}
		
		/**
		 * 
		 * @return the currently populated string of the specified pattern
		 */
		public String getCurrentPrefix() {
			return new SimpleDateFormat(prefix).format(new Date());
		}
		
		/**
		 * 
		 * @return the raw value of the prefix
		 */
		public String raw() {
			return prefix;
		}
		
	}
	
}
