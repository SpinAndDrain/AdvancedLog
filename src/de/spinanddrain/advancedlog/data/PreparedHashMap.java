package de.spinanddrain.advancedlog.data;

import java.util.HashMap;

public class PreparedHashMap<K, V> extends HashMap<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new <code>HashMap</code> with the specified array content.
	 * 
	 * @param keys
	 * @param values
	 * @throws ArrayIndexOutOfBoundsException if the array length of keys is unequal to the array length of values
	 */
	public PreparedHashMap(K[] keys, V[] values) {
		if(keys.length != values.length) {
			throw new ArrayIndexOutOfBoundsException("Unequal array size");
		}
		for(int i = 0; i < keys.length; i++) {
			put(keys[i], values[i]);
		}
	}
	
}
