package de.Ste3et_C0st.ProtectionLibRework.main;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import de.Ste3et_C0st.ProtectionLibRework.main.ProtectionPluginFilter;

public class ProtectionPluginFilter{

	private final String pluginName;
	private Predicate<PluginDescriptionFile> fileFilter;
	private Supplier<String> className = () -> "";
	
	public ProtectionPluginFilter(String pluginName) {
		this(pluginName, file -> file.getName().equalsIgnoreCase(pluginName));
	}
	
	public ProtectionPluginFilter isVersion(int version) {
		Predicate<PluginDescriptionFile> fileFilter = file -> file.getVersion().startsWith(version + ".");
		this.fileFilter = this.fileFilter.and(fileFilter);
		return this;
	}
	
	public ProtectionPluginFilter containsAuthor(String author) {
		return containsAuthor(author, true);
	}
	
	public ProtectionPluginFilter containsClass(String packetName) {
		this.className = () -> packetName;
		return this;
	}
	
	public ProtectionPluginFilter containsAuthor(String author, boolean bool) {
		Predicate<PluginDescriptionFile> fileFilter = file -> file.getAuthors().contains(author) == bool;
		this.fileFilter = this.fileFilter.and(fileFilter);
		return this;
	}
	
	public ProtectionPluginFilter(String pluginname, Predicate<PluginDescriptionFile> fileFilter) {
		this.pluginName = pluginname;
		this.fileFilter = fileFilter;
	}

	public Predicate<PluginDescriptionFile> getFileFilter() {
		return fileFilter;
	}

	public String getPluginName() {
		return pluginName;
	}

	public boolean match() {
		final Plugin plugin = Bukkit.getPluginManager().getPlugin(this.getPluginName());
		if(Objects.nonNull(plugin)) {
			if(plugin.isEnabled()) {
				Predicate<PluginDescriptionFile> predicate = this.getFileFilter();
				if(className.get().isEmpty() == false)
					try {
						return Optional.ofNullable(Class.forName(className.get())).isPresent();
					} catch (ClassNotFoundException e) {
						return false;
					}
				if(predicate.test(plugin.getDescription())) {
					return true;
				}
			}
		}
		return false;
	}
	
}
