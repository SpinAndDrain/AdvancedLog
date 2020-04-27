package de.spinanddrain.advancedlog.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

public class Updater {
	
	private Plugin plugin;
	private final String resource = "64268";
	public static final String prefix = "§7[§9AdvancedLog§7] §r";
	
	public Updater(Plugin plugin) {
		this.plugin=plugin;
	}
	
	/**
	 * Checks if a newer version of the plugin is available.
	 * 
	 * @return true if an update is available, false if not
	 * @deprecated This method sometimes does not work and causes errors.
	 */
	@Deprecated
	public boolean checkUpdate() {
		ConsoleCommandSender c = plugin.getServer().getConsoleSender();
		c.sendMessage(prefix+"§eChecking for updates...");
		try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource="+resource+"key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4").openConnection();
//            con.setDoOutput(true);
//            con.setRequestMethod("POST");
//            con.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource="+resource).getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if(version.equalsIgnoreCase(plugin.getDescription().getVersion())) {
            	c.sendMessage(prefix+"§eNo updates found.");
            	return false;
            } else {
            	c.sendMessage(prefix+"§eA newer version is available: §b"+version);
            	c.sendMessage(prefix+"§eDownload: §bhttps://bit.ly/2TilRD6");
            	return true;
            }
        }catch(IOException e) {
        	c.sendMessage(prefix+"§cAn error occurred while searching for updates!");
        	return false;
        }
	}
	
	/**
	 * Checks if a newer version of the plugin is available.
	 * 
	 * @param debug true = print debug (also errors)
	 */
	public void check(boolean debug) {
		ConsoleCommandSender c = plugin.getServer().getConsoleSender();
		c.sendMessage(prefix+"§eChecking for updates...");
		for(int i = 0; i < 2; i++) {
			try {
				HttpURLConnection con = (HttpURLConnection) (i == 0 ? new URL("https://api.spigotmc.org/legacy/update.php?resource=").openConnection() : new URL("https://api.spigotmc.org/legacy/update.php?resource="+resource+"key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4").openConnection());
				if(i == 0) {
					con.setDoOutput(true);
					con.setRequestMethod("POST");
					con.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource="+resource).getBytes("UTF-8"));
				}
				String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
				if(version.equalsIgnoreCase(plugin.getDescription().getVersion())) {
					c.sendMessage(prefix+"§eNo updates found.");
	            	return;
	            } else {
	            	c.sendMessage(prefix+"§eA newer version is available: §b"+version);
	            	c.sendMessage(prefix+"§eDownload: §bhttps://bit.ly/2TilRD6");
	            	return;
	            }
			} catch(Exception e) {
				if(debug) {
					System.out.println("First method passed, trying second.");
					e.printStackTrace();
				}
				continue;
			}
		}
		c.sendMessage(prefix+"§cAn error occurred while searching for updates!");
	}
	
}
