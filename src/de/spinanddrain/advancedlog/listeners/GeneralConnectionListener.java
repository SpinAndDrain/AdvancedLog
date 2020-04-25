package de.spinanddrain.advancedlog.listeners;

import java.io.IOException;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.spinanddrain.advancedlog.AdvancedLog;
import de.spinanddrain.advancedlog.logging.LogSession;

public class GeneralConnectionListener implements Listener {

	/**
	 * Listens for <code>PlayerJoinEvent</code> and <code>PlayerQuitEvent</code>.
	 * Ensures that all files are still created when the connection log is disabled.
	 * 
	 */
	public GeneralConnectionListener() {
	}
	
	/**
	 * Triggered when a player joins the server
	 * 
	 * @param event
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		Map<Player, LogSession> sessions = AdvancedLog.getInstance().getSessions();
		if(sessions.containsKey(p)) {
			try {
				sessions.get(p).closeAll();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sessions.remove(p);
		}
		LogSession session = new LogSession(ConnectionListener.getPlayerFiles(p));
		try {
			session.openAll(ConnectionListener.getOrderedLogHeaders(p));
		} catch (IOException e) {
			e.printStackTrace();
		}
		sessions.put(p, session);
	}
	
	/**
	 * Triggered when a player leaves the server
	 * 
	 * @param event
	 */
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		Map<Player, LogSession> sessions = AdvancedLog.getInstance().getSessions();
		if(sessions.containsKey(p)) {
			try {
				sessions.get(p).closeAll();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sessions.remove(p);
		}
	}
	
}
