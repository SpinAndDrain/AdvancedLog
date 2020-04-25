package de.spinanddrain.advancedlog.event;

import java.io.File;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LogPerpetuateEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancel;
	
	private File logFile;
	private String log;
	
	/**
	 * This event is called before a log message gets written into a file.
	 * 
	 * @param log the message
	 * @param logFile the file
	 */
	public LogPerpetuateEvent(String log, File logFile) {
		this.log = log;
		this.logFile = logFile;
	}
	
	public File getLogFile() {
		return logFile;
	}
	
	public String getLog() {
		return log;
	}
	
	public void setLog(String log) {
		this.log = log;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
