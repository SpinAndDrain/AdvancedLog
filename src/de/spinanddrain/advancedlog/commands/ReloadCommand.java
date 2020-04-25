package de.spinanddrain.advancedlog.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.spinanddrain.advancedlog.AdvancedLog;

public class ReloadCommand implements CommandExecutor {

	/**
	 * Command for saving and reloading the resources of this plugin.
	 * @permission al.reload
	 */
	public ReloadCommand() {
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
		if(sender.hasPermission("al.reload")) {
			sender.sendMessage(AdvancedLog.getInstance().getReloadingMessage());
			AdvancedLog.getInstance().reload();
			sender.sendMessage(AdvancedLog.getInstance().getReloadedMessage());
		} else
			sender.sendMessage(AdvancedLog.getInstance().getNoPermissionMessage());
		return false;
	}

}
