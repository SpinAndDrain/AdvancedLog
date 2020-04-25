package de.spinanddrain.advancedlog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import de.spinanddrain.advancedlog.AdvancedLog;
import de.spinanddrain.advancedlog.logging.LogSession;

public class CommandListener implements Listener {

	/**
	 * Listens for <code>PlayerCommandPreprocessEvent</code> and <code>ServerCommandEvent</code>.
	 * 
	 */
	public CommandListener() {
	}
	
	/**
	 * Triggered when a player sends a command
	 * 
	 * @param event
	 */
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		if(AdvancedLog.getInstance().getCommandLog().isStreamOpen()) {
			AdvancedLog.getInstance().getCommandLog().log("Player [" + p.getName() + "] sent command: " + event.getMessage());
		}
		LogSession session = AdvancedLog.getInstance().getSessions().get(p);
		if(session != null && session.isEachOpen()) {
			session.log(AdvancedLog.CHAT_COMMANDSLOG, p.getName() + " sent command: " + event.getMessage());
		}
	}
	
	/**
	 * Triggered when the console sends a command
	 * 
	 * @param event
	 */
	@EventHandler
	public void onConsoleCommand(ServerCommandEvent event) {
		if(AdvancedLog.getInstance().getGeneralLog().isStreamOpen()) {
			AdvancedLog.getInstance().getGeneralLog().log("[CONSOLE] > " + event.getCommand());
		}
	}
	
}
