package de.spinanddrain.advancedlog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import de.spinanddrain.advancedlog.commands.ReloadCommand;
import de.spinanddrain.advancedlog.data.IndependentlyFile;
import de.spinanddrain.advancedlog.listeners.BlockListener;
import de.spinanddrain.advancedlog.listeners.ChatListener;
import de.spinanddrain.advancedlog.listeners.CommandListener;
import de.spinanddrain.advancedlog.listeners.ConnectionListener;
import de.spinanddrain.advancedlog.listeners.GeneralConnectionListener;
import de.spinanddrain.advancedlog.listeners.InteractorListener;
import de.spinanddrain.advancedlog.logging.Log;
import de.spinanddrain.advancedlog.logging.Log.LocalLogPrefix;
import de.spinanddrain.advancedlog.logging.LogSession;
import de.spinanddrain.advancedlog.plugin.Updater;

public class AdvancedLog extends JavaPlugin {

	private static AdvancedLog instance;
	
	/**
	 * Array index constants by the default order of a <code>LogSession</code>s files.
	 * 
	 */
	public static final int BLOCKLOG = 0, CHAT_COMMANDSLOG = 1, CONNECTIONLOG = 2, INTERACTORLOG = 3;
	
	private IndependentlyFile config;
	private Handler hook;
	private List<Listener> listeners;
	private Map<Player, LogSession> sessions;
	private Log log, chat, command, block, connection;
	private boolean checkForUpdate, enableChat, enableCommand, enableBlock, enableInteractor, enableConnection;
	private int interactorCooldown;
	private String prefix, noPermission, reloading, reloaded;

	@Override
	public void onEnable() {
		instance = this;
		sessions = new HashMap<Player, LogSession>();
		
		prepareFiles();
		
		prepareConfiguration();
		
		prepareHook();
		
		getLogger().log(Level.INFO, "Starting AdvancedLog...");
		
		String softVer = getSoftVersion();
		if(!isValidSoftVersion()) {
			getLogger().log(Level.WARNING, "Your server version is unknown to AdvancedLog. Please beware that this can cause errors and/or corrupt your logs!");
		}
		
		getServer().getConsoleSender().sendMessage("§7__________[§9AdvancedLog§7]__________");
		getServer().getConsoleSender().sendMessage(" ");
		getServer().getConsoleSender().sendMessage("§7   Current Version: §b" + getDescription().getVersion());
		getServer().getConsoleSender().sendMessage("§7   Plugin by §cSpinAndDrain");
		getServer().getConsoleSender().sendMessage("§7   Your Serverversion: §b" + (softVer != null ? softVer : "?"));
		getServer().getConsoleSender().sendMessage("§7_________________________________");
	
		if(checkForUpdate) {
			new Updater(this).check(false);
		}
		
		prepareRequiredListeners();
		
		getCommand("advancedlogreload").setExecutor(new ReloadCommand());
		
		verifyOnlinePlayers();
	}
	
	@Override
	public void onDisable() {
		if(hook != null) {
			getServer().getLogger().removeHandler(hook);
		}
		unregisterActiveListeners();
		closePlayerSessions();
		unloadFiles();
	}
	
	/**
	 * 
	 * @return the static singleton instance of this main class
	 */
	public static AdvancedLog getInstance() {
		return instance;
	}

	/**
	 * 
	 * @return the plugin's configuration (config.yml) file
	 */
	public IndependentlyFile getConfigFile() {
		return config;
	}
	
	/**
	 * 
	 * @return the logger hook to log the default console output
	 */
	public Handler getHook() {
		return hook;
	}
	
	/**
	 * 
	 * @return config entry => <code>Updater.check</code>
	 */
	public boolean getCheckForUpdate() {
		return checkForUpdate;
	}
	
	/**
	 * 
	 * @return config entry => <code>Log.block.enable</code>
	 */
	public boolean getEnableBlockLog() {
		return enableBlock;
	}
	
	/**
	 * 
	 * @return config entry => <code>Log.chat.enable</code>
	 */
	public boolean getEnableChatLog() {
		return enableChat;
	}
	
	/**
	 * 
	 * @return config entry => <code>Log.command.enable</code>
	 */
	public boolean getEnableCommandLog() {
		return enableCommand;
	}
	
	/**
	 * 
	 * @return config entry => <code>Log.connection.enable</code>
	 */
	public boolean getEnableConnectionLog() {
		return enableConnection;
	}
	
	/**
	 * 
	 * @return config entry => <code>Log.interactor.enable</code>
	 */
	public boolean getEnableInteractorLog() {
		return enableInteractor;
	}
	
	/**
	 * 
	 * @return config entry => <code>Log.interactor.cooldown</code>
	 */
	public int getInteractorLogCooldown() {
		return interactorCooldown;
	}
	
	/**
	 * 
	 * @return <code>Log</code> instance of the general <b>_log.txt</b> file
	 */
	public Log getGeneralLog() {
		return log;
	}
	
	/**
	 * 
	 * @return <code>Log</code> instance of the general <b>_chatlog.txt</b> file
	 */
	public Log getChatLog() {
		return chat;
	}
	
	/**
	 * 
	 * @return <code>Log</code> instance of the general <b>_commandlog.txt</b> file
	 */
	public Log getCommandLog() {
		return command;
	}
	
	/**
	 * 
	 * @return <code>Log</code> instance of the general <b>_blocklog.txt</b> file
	 */
	public Log getBlockLog() {
		return block;
	}
	
	/**
	 * 
	 * @return <code>Log</code> instance of the general <b>_connectionlog.txt</b> file
	 */
	public Log getConnectionLog() {
		return connection;
	}
	
	/**
	 * 
	 * @return collection of online players unique log file sessions
	 */
	public Map<Player, LogSession> getSessions() {
		return sessions;
	}
	
	/**
	 * 
	 * @return config entry => <code>Messages.prefix</code>
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * 
	 * @return config entry => <code>Messages.no-permission</code>
	 */
	public String getNoPermissionMessage() {
		return noPermission;
	}
	
	/**
	 * 
	 * @return config entry => <code>Messages.reloading</code>
	 */
	public String getReloadingMessage() {
		return reloading;
	}
	
	/**
	 * 
	 * @return config entry => <code>Messages.reloaded</code>
	 */
	public String getReloadedMessage() {
		return reloaded;
	}
	
	/**
	 * Inserts for all currently online players the default <code>LogSession</code>
	 * into the collection. (Only if not available)
	 * 
	 * @see {@link ConnectionListener#getPlayerFiles(Player)}
	 */
	public void verifyOnlinePlayers() {
		for(Player all : getServer().getOnlinePlayers()) {
			if(!sessions.containsKey(all)) {
				LogSession session = new LogSession(ConnectionListener.getPlayerFiles(all));
				try {
					session.openAll(ConnectionListener.getOrderedLogHeaders(all));
				} catch (IOException e) {
					e.printStackTrace();
				}
				sessions.put(all, session);
			}
		}
	}
	
	/**
	 * Reloads (and saves) all logging and configuration resources.
	 * 
	 */
	public void reload() {
		unregisterActiveListeners();
		closePlayerSessions();
		unloadFiles();
		YamlConfiguration c = config.reload();
		checkForUpdate = c.getBoolean("Updater.check");
		enableChat = c.getBoolean("Log.chat.enable");
		enableBlock = c.getBoolean("Log.block.enable");
		enableCommand = c.getBoolean("Log.command.enable");
		enableConnection = c.getBoolean("Log.connection.enable");
		enableInteractor = c.getBoolean("Log.interactor.enable");
		interactorCooldown = c.getInt("Log.interactor.cooldown");
		prefix = c.getString("Messages.prefix").replaceAll("&", "§");
		String mesPref = prefix.isEmpty() ? "" : prefix + " ";
		noPermission = mesPref + c.getString("Messages.no-permission").replaceAll("&", "§");
		reloading = mesPref + c.getString("Messages.reloading").replaceAll("&", "§");
		reloaded = mesPref + c.getString("Messages.reloaded").replaceAll("&", "§");
		prepareFiles();
		prepareRequiredListeners();
		verifyOnlinePlayers();
	}
	
	/**
	 * 
	 * @return the general server log home dir path
	 */
	public static String getGeneralPath() {
		return currentPath() + "/server/";
	}
	
	/**
	 * 
	 * @return the default player log home dir path
	 */
	public static String getPlayersPath() {
		return currentPath() + "/players/";
	}
	
	/**
	 * 
	 * @return the current server version (Format: <b>X.x</b>)
	 */
	public static String getSoftVersion() {
		String ver = instance.getServer().getBukkitVersion();
		if(ver.startsWith("1.7")) {
			return "1.7";
		} else if(ver.startsWith("1.8")) {
			return "1.8";
		} else if(ver.startsWith("1.9")) {
			return "1.9";
		} else if(ver.startsWith("1.10")) {
			return "1.10";
		} else if(ver.startsWith("1.11")) {
			return "1.11";
		} else if(ver.startsWith("1.12")) {
			return "1.12";
		} else if(ver.startsWith("1.13")) {
			return "1.13";
		} else if(ver.startsWith("1.14")) {
			return "1.14";
		} else if(ver.startsWith("1.15")) {
			return "1.15";
		} else
			return null;
	}
	
	/**
	 * 
	 * @return true if AdvancedLog directly supports this version, false if not
	 */
	public static boolean isValidSoftVersion() {
		return getSoftVersion() != null;
	}
	
	/**
	 * 
	 * @return the basic log home dir path
	 */
	private static String currentPath() {
		return "plugins/AdvancedLog/logs/" + new LocalLogPrefix("yyyy-MM-dd").getCurrentPrefix();
	}
	
	/**
	 * Closes all currently open player sessions and their streams.
	 * 
	 */
	private void closePlayerSessions() {
		for(Player all : sessions.keySet()) {
			LogSession session = sessions.get(all);
			if(session.isAnyOpen()) {
				try {
					session.closeOpen();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			sessions.remove(all);
		}
	}
	
	/**
	 * Creates and prepares all default log files and opens their stream.
	 * 
	 * @see {@link Log#openStream()}
	 */
	private void prepareFiles() {
		getLogger().log(Level.INFO, "Preparing files...");
		makeDirectory(getGeneralPath());
		makeDirectory(getPlayersPath());
		try {
			log = new Log(LocalLogPrefix.DEFAULT.raw(), createFileIfNotExist(getGeneralPath() + "_log.txt"),
					"<General log file for * on " + new LocalLogPrefix("yyyy-MM-dd").getCurrentPrefix() + ">");
			block = new Log(LocalLogPrefix.DEFAULT.raw(), createFileIfNotExist(getGeneralPath() + "_blocklog.txt"),
					"<Block log file for * on " + new LocalLogPrefix("yyyy-MM-dd").getCurrentPrefix() + ">");
			command = new Log(LocalLogPrefix.DEFAULT.raw(), createFileIfNotExist(getGeneralPath() + "_commandlog.txt"),
					"<Command log file for * on " + new LocalLogPrefix("yyyy-MM-dd").getCurrentPrefix() + ">");
			connection = new Log(LocalLogPrefix.DEFAULT.raw(), createFileIfNotExist(getGeneralPath() + "_connectionlog.txt"),
					"<Connection log file for * on " + new LocalLogPrefix("yyyy-MM-dd").getCurrentPrefix() + ">");
			chat = new Log(LocalLogPrefix.DEFAULT.raw(), createFileIfNotExist(getGeneralPath() + "_chatlog.txt"),
					"<Chat log file for * on " + new LocalLogPrefix("yyyy-MM-dd").getCurrentPrefix() + ">");
			log.openStream();
			block.openStream();
			command.openStream();
			connection.openStream();
			chat.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes the stream of each default log file.
	 * 
	 * @see {@link Log#close()}
	 */
	private void unloadFiles() {
		try {
			log.close();
			block.close();
			command.close();
			connection.close();
			chat.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates the specified directory path if it does not exist.
	 * 
	 * @param dir the directory path
	 */
	public static void makeDirectory(String dir) {
		File f = new File(dir);
		if(!f.exists()) {
			f.mkdirs();
		}
	}
	
	/**
	 * Creates a new file and the parent directory if not exist.
	 * 
	 * @param filePath path of file
	 * @return the new file
	 */
	public static File createFileIfNotExist(String filePath) {
		File file = new File(filePath);
		makeDirectory(file.getParent());
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	/**
	 * Creates and prepares the default AdvancedLog configuration file (config.yml).
	 * 
	 */
	private void prepareConfiguration() {
		if(config == null) {
			try {
				config = new IndependentlyFile("plugins/AdvancedLog/config.yml");
				config.applyDefaults(cfg -> {
					cfg.addDefault("Updater.check", true);
					cfg.addDefault("Log.chat.enable", true);
					cfg.addDefault("Log.block.enable", true);
					cfg.addDefault("Log.command.enable", true);
					cfg.addDefault("Log.connection.enable", true);
					cfg.addDefault("Log.interactor.enable", true);
					cfg.addDefault("Log.interactor.cooldown", 10);
					cfg.addDefault("Messages.prefix", "&7[&9AdvancedLog&7]");
					cfg.addDefault("Messages.no-permission", "&cYou do not have permission to perform this command.");
					cfg.addDefault("Messages.reloading", "&eReloading...");
					cfg.addDefault("Messages.reloaded", "&aReloaded!");
				});
				YamlConfiguration c = config.configure();
				checkForUpdate = c.getBoolean("Updater.check");
				enableChat = c.getBoolean("Log.chat.enable");
				enableBlock = c.getBoolean("Log.block.enable");
				enableCommand = c.getBoolean("Log.command.enable");
				enableConnection = c.getBoolean("Log.connection.enable");
				enableInteractor = c.getBoolean("Log.interactor.enable");
				interactorCooldown = c.getInt("Log.interactor.cooldown");
				prefix = c.getString("Messages.prefix").replaceAll("&", "§");
				String mesPref = prefix.isEmpty() ? "" : prefix + " ";
				noPermission = mesPref + c.getString("Messages.no-permission").replaceAll("&", "§");
				reloading = mesPref + c.getString("Messages.reloading").replaceAll("&", "§");
				reloaded = mesPref + c.getString("Messages.reloaded").replaceAll("&", "§");
			} catch(IOException e) {
				e.printStackTrace();
			}
		} else
			config.reload();
	}

	/**
	 * Registers all in the default configuration (config.yml) enabled <code>Listener</code>s.
	 * 
	 */
	private void prepareRequiredListeners() {
		listeners = new ArrayList<Listener>();
		listeners.add(new GeneralConnectionListener());
		if(enableChat) {
			listeners.add(new ChatListener());
		}
		if(enableBlock) {
			listeners.add(new BlockListener());
		}
		if(enableCommand) {
			listeners.add(new CommandListener());
		}
		if(enableConnection) {
			listeners.add(new ConnectionListener());
		}
		if(enableInteractor) {
			listeners.add(new InteractorListener());
		}
		for(Listener l : listeners) {
			getServer().getPluginManager().registerEvents(l, this);
		}
	}
	
	/**
	 * Unregisters all currently active <code>Listener</code>s.
	 * 
	 */
	private void unregisterActiveListeners() {
		for(Listener l : listeners) {
			HandlerList.unregisterAll(l);
		}
		listeners.clear();
	}
	
	/**
	 * Prepares and adds the console log hook to the default <code>Logger</code>.
	 * 
	 */
	private void prepareHook() {
		if(hook == null) {
			hook = new Handler() {
				
				@Override
				public void publish(LogRecord record) {
					if(log.isStreamOpen()) {
						log.log("[" + record.getLevel() + "] " + record.getMessage());
					}
				}
				
				@Override
				public void flush() {
				}
				
				@Override
				public void close() throws SecurityException {
				}
			};
		} else
			getServer().getLogger().removeHandler(hook);
		getServer().getLogger().addHandler(hook);
	}
	
}
