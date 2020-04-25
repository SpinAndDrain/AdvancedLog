package de.spinanddrain.advancedlog.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.spinanddrain.advancedlog.AdvancedLog;
import de.spinanddrain.advancedlog.logging.LogSession;

public class ChatListener implements Listener {

	/**
	 * Listens for <code>AsyncPlayerChatEvent</code>.
	 * 
	 */
	public ChatListener() {
	}
	
	/**
	 * Triggered when a player sends a chat message
	 * 
	 * @param event
	 */
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Bukkit.getScheduler().runTask(AdvancedLog.getInstance(), () -> {
			Player p = event.getPlayer();
			if(AdvancedLog.getInstance().getChatLog().isStreamOpen()) {
				AdvancedLog.getInstance().getChatLog().log("Player [" + p.getName() + "] sent chat message: " + event.getMessage());
			}
			LogSession session = AdvancedLog.getInstance().getSessions().get(p);
			if(session != null && session.isEachOpen()) {
				session.log(AdvancedLog.CHAT_COMMANDSLOG, p.getName() + " sent chat message: " + event.getMessage());
			}
		});
	}
	
}
