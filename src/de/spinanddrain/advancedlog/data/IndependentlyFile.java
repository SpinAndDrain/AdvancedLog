package de.spinanddrain.advancedlog.data;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

public class IndependentlyFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private YamlConfiguration configuration;
	
	/**
	 * Creates a new instance of this class with the specified pathname.
	 * 
	 * @param pathname
	 * @throws IOException
	 * @see {@link File#File(String)}
	 */
	public IndependentlyFile(String pathname) throws IOException {
		super(pathname);
		File parent = getParentFile();
		if(!parent.exists()) {
			parent.mkdirs();
		}
		if(!exists()) {
			createNewFile();
		}
		this.reload();
	}

	/**
	 * Reloads the based configuration.
	 * 
	 * @return the based configuration
	 */
	public YamlConfiguration reload() {
		return configuration = YamlConfiguration.loadConfiguration(this);
	}
	
	/**
	 * 
	 * @return the currently loaded session of the based configuration
	 */
	public YamlConfiguration configure() {
		return configuration;
	}

	/**
	 * Saves the based configuration.
	 * (rare IOException is caught -> {@link YamlConfiguration#save(File)})
	 * 
	 */
	public void save() {
		try {
			configuration.save(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds the content of the <code>HashMap</code> <b>defaults</b> as defaults of the <code>FileConfiguration</code>.
	 * Hint: Use {@link PreparedHashMap} to fill the content easier
	 * 
	 * @param defaults
	 */
	public void applyDefaults(Map<String, Object> defaults) {
		configuration.options().copyDefaults(true);
		for(String keys : defaults.keySet()) {
			configuration.addDefault(keys, defaults.get(keys));
		}
		this.save();
	}
	
	/**
	 * Runs the {@link Defaults#setDefaults(YamlConfiguration)} method and
	 * sets the defaults.
	 * 
	 * @param d
	 */
	public void applyDefaults(Defaults d) {
		configuration.options().copyDefaults(true);
		d.setDefaults(configuration);
		this.save();
	}
	
	public static interface Defaults {
		
		/**
		 * Method to add configuration defaults.
		 * 
		 * @param configuration
		 */
		void setDefaults(YamlConfiguration configuration);
		
	}
	
}
