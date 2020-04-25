package de.spinanddrain.advancedlog.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import de.spinanddrain.advancedlog.AdvancedLog;
import de.spinanddrain.advancedlog.logging.LogSession;

public class BlockListener implements Listener {

	/**
	 * Listens for <code>BlockPlaceEvent</code> and <code>BlockBreakEvent</code>.
	 * 
	 */
	public BlockListener() {
	}
	
	/**
	 * Triggered when a player places a block
	 * 
	 * @param event
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Location l = event.getBlock().getLocation();
		if(AdvancedLog.getInstance().getBlockLog().isStreamOpen()) {
			AdvancedLog.getInstance().getBlockLog().log("Player [" + p.getName() + "] placed block in world ["
					+ event.getBlock().getWorld().getName() + "] at [x: " + l.getBlockX() + ", y: " + l.getBlockY()
					+ ", z: " + l.getBlockZ() + "]");
		}
		LogSession session = AdvancedLog.getInstance().getSessions().get(p);
		if(session != null && session.isEachOpen()) {
			session.log(AdvancedLog.BLOCKLOG, p.getName() + " placed block in world ["
					+ event.getBlock().getWorld().getName() + "] at [x: " + l.getBlockX() + ", y: " + l.getBlockY()
					+ ", z: " + l.getBlockZ() + "]");
		}
	}
	
	/**
	 * Triggered when a player breaks a block
	 * 
	 * @param event
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Location l = event.getBlock().getLocation();
		if(AdvancedLog.getInstance().getBlockLog().isStreamOpen()) {
			AdvancedLog.getInstance().getBlockLog().log("Player [" + p.getName() + "] breaked block in world ["
					+ event.getBlock().getWorld().getName() + "] at [x: " + l.getBlockX() + ", y: " + l.getBlockY()
					+ ", z: " + l.getBlockZ() + "]");
		}
		LogSession session = AdvancedLog.getInstance().getSessions().get(p);
		if(session != null && session.isEachOpen()) {
			session.log(AdvancedLog.BLOCKLOG, p.getName() + " breaked block in world ["
					+ event.getBlock().getWorld().getName() + "] at [x: " + l.getBlockX() + ", y: " + l.getBlockY()
					+ ", z: " + l.getBlockZ() + "]");
		}
	}
	
}
