package de.spinanddrain.advancedlog.listeners;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.spinanddrain.advancedlog.AdvancedLog;
import de.spinanddrain.advancedlog.logging.Log;
import de.spinanddrain.advancedlog.logging.LogSession;

public class ConnectionListener implements Listener {

	private Map<Player, Long> stayedTime;
	
	/**
	 * Listens for <code>PlayerJoinEvent</code> and <code>PlayerQuitEvent</code>.
	 * 
	 */
	public ConnectionListener() {
		this.stayedTime = new HashMap<Player, Long>();
	}
	
	/**
	 * Triggered when a player joins the server
	 * 
	 * @param event
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if(stayedTime.containsKey(p)) {
			stayedTime.remove(p);
		}
		stayedTime.put(p, System.currentTimeMillis());
		Log log = AdvancedLog.getInstance().getConnectionLog();
		if(log.isStreamOpen()) {
			log.log("Player [" + p.getName() + "] joined the server with uuid [" + p.getUniqueId().toString() + "] and address ["
					+ p.getAddress().getHostString() + "]");
		}
		LogSession session = AdvancedLog.getInstance().getSessions().get(p);
		if(session.isEachOpen()) {
			session.log(AdvancedLog.CONNECTIONLOG, p.getName() + " joined the server with uuid [" + p.getUniqueId().toString() + "] and address ["
					+ p.getAddress().getHostString() + "]");
		}
	}
	
	/**
	 * Triggered when a player leaves the server
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		long stayed = -1;
		if(stayedTime.containsKey(p)) {
			stayed = System.currentTimeMillis() - stayedTime.get(p);
			stayedTime.remove(p);
		}
		Log log = AdvancedLog.getInstance().getConnectionLog();
		if(log.isStreamOpen()) {
			log.log("Player [" + p.getName() + "] left the server" + (stayed == -1 ? "" : " (stayed for "
					+ convertLogical(stayed) + ")"));
		}
		LogSession session = AdvancedLog.getInstance().getSessions().get(p);
		if(session != null && session.isEachOpen()) {
			session.log(AdvancedLog.CONNECTIONLOG, p.getName() + " left the server"
					+ (stayed == -1 ? "" : " (stayed for " + convertLogical(stayed) + ")"));
		}
	}
	
	/**
	 * Converts the given milliseconds into a logical time format.
	 * 
	 * @param millis the given milliseconds
	 * @return the format as <code>String</code>
	 */
	private String convertLogical(long millis) {
		if(millis < 1000) {
			return millis + " millisecond" + suffix(millis);
		}
		long sec = millis / 1000;
		if(sec < 60) {
			return sec + " second" + suffix(sec);
		}
		long min = sec / 60;
		if(min < 60) {
			return min + " minute" + suffix(min);
		}
		long h = min / 60, restMin = min % 60;
		return h + " hour" + suffix(h) + (restMin == 0 ? "" : " " + restMin + " minute" + suffix(restMin));
	}
	
	/**
	 * Calculates the majority of the time key words with the given time value.
	 * Example: 1 second, 3 second<b>s</b>
	 * 
	 * @param l the specified time value
	 * @return a "s" if the specified <code>long</code> is not exactly one
	 */
	private String suffix(long l) {
		return (l != 1 ? "s" : "");
	}
	
	/**
	 * 
	 * @param player
	 * @return the default log files for the specified <b>player</b>
	 */
	public static File[] getPlayerFiles(Player player) {
		File[] files = new File[4];
		String name = player.getName();
		files[0] = AdvancedLog.createFileIfNotExist(AdvancedLog.getPlayersPath() + name + "/blocklog.txt");
		files[1] = AdvancedLog.createFileIfNotExist(AdvancedLog.getPlayersPath() + name + "/chat-commandslog.txt");
		files[2] = AdvancedLog.createFileIfNotExist(AdvancedLog.getPlayersPath() + name + "/connectionlog.txt");
		files[3] = AdvancedLog.createFileIfNotExist(AdvancedLog.getPlayersPath() + name + "/interactorlog.txt");
		return files;
	}
	
	/**
	 * 
	 * @param player
	 * @return an ordered <code>String[]</code> for the headers of each default player log file
	 */
	public static String[] getOrderedLogHeaders(Player player) {
		String[] headers = new String[4];
		headers[0] = "<Block log file for player '" + player.getName() + ":" + player.getUniqueId().toString() + "'"
				+ " on " + new Log.LocalLogPrefix("yyyy-MM-dd").getCurrentPrefix() + ">";
		headers[1] = "<Chat and Commands log file for player '" + player.getName() + ":" + player.getUniqueId().toString() + "'"
				+ " on " + new Log.LocalLogPrefix("yyyy-MM-dd").getCurrentPrefix() + ">";
		headers[2] = "<Connection log file for player '" + player.getName() + ":" + player.getUniqueId().toString() + "'"
				+ " on " + new Log.LocalLogPrefix("yyyy-MM-dd").getCurrentPrefix() + ">";
		headers[3] = "<Interactor log file for player '" + player.getName() + ":" + player.getUniqueId().toString() + "'"
				+ " on " + new Log.LocalLogPrefix("yyyy-MM-dd").getCurrentPrefix() + ">";
		return headers;
	}
	
}
