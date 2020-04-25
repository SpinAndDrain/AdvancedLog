package de.spinanddrain.advancedlog.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.spinanddrain.advancedlog.AdvancedLog;
import de.spinanddrain.advancedlog.data.PreparedHashMap;
import de.spinanddrain.advancedlog.logging.LogSession;

public class InteractorListener implements Listener {

	private Map<Player, Map<Entity, Long>> right, left;

	/**
	 * Listens for <code>PlayerInteractAtEntityEvent</code> and
	 * <code>EntityDamageByEntityEvent</code>.
	 * 
	 */
	public InteractorListener() {
		this.right = new HashMap<Player, Map<Entity, Long>>();
		this.left = new HashMap<Player, Map<Entity,Long>>();
	}

	/**
	 * Triggered when a player right-clicks another entity
	 * 
	 * @param event
	 */
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		Entity e = event.getRightClicked();
		Location l = e.getLocation();
		LogSession session = AdvancedLog.getInstance().getSessions().get(p);
		Map<Entity, Long> playersMap = right.get(p);
		if(playersMap != null) {
			if(playersMap.containsKey(e)) {
				if((System.currentTimeMillis() - playersMap.get(e)) / 1000 < AdvancedLog.getInstance().getInteractorLogCooldown()) {
					return;
				} else
					playersMap.remove(e);
			} else
				playersMap.put(e, System.currentTimeMillis());
		} else
			right.put(p, new PreparedHashMap<Entity, Long>(new Entity[] { e }, new Long[] { System.currentTimeMillis() }));
		if(session != null && session.isEachOpen()) {
			session.log(AdvancedLog.INTERACTORLOG, p.getName() + " right-clicked entity [" + e.getType().toString() 
					+ ":" + e.getName() + "] in world [" + e.getWorld().getName() + "] at [x: " + l.getBlockX() + ","
							+ " y: " + l.getBlockY() + ", z: " + l.getBlockZ() + "]");
		}
	}

	/**
	 * Triggered when a player left-clicks another entity
	 * 
	 * @param event
	 */
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Player)) {
			return;
		}
		Player p = (Player) event.getDamager();
		Entity e = event.getEntity();
		Location l = e.getLocation();
		LogSession session = AdvancedLog.getInstance().getSessions().get(p);
		Map<Entity, Long> playersMap = left.get(p);
		if(playersMap != null) {
			if(playersMap.containsKey(e)) {
				if((System.currentTimeMillis() - playersMap.get(e)) / 1000 < AdvancedLog.getInstance().getInteractorLogCooldown()) {
					return;
				} else
					playersMap.remove(e);
			} else
				playersMap.put(e, System.currentTimeMillis());
		} else
			left.put(p, new PreparedHashMap<Entity, Long>(new Entity[] { e }, new Long[] { System.currentTimeMillis() }));
		if(session != null && session.isEachOpen()) {
			session.log(AdvancedLog.INTERACTORLOG, p.getName() + " left-clicked entity [" + e.getType().toString() 
					+ ":" + e.getName() + "] in world [" + e.getWorld().getName() + "] at [x: " + l.getBlockX() + ","
							+ " y: " + l.getBlockY() + ", z: " + l.getBlockZ() + "]");
		}
	}

}
