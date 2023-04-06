package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class NamespacedKeyWrapper {

	final String key, namespace;
	
	public static NamespacedKeyWrapper of(Plugin plugin, String key) {
		return of(plugin.getName(), key);
	}
	
	public static NamespacedKeyWrapper of(String namespace, String key) {
		return new NamespacedKeyWrapper(namespace, key);
	}
	
	public NamespacedKeyWrapper(String namespace, String key) {
		this.namespace = namespace;
		this.key = key;
	}
	
	public NamespacedKey toKey() {
		return new NamespacedKey(namespace, key);
	}
	
}
