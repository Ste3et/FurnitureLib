package de.Ste3et_C0st.ProtectionLibRework.loader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.google.common.collect.Lists;

public class ProtectionModulLoader {

	private final static Predicate<String> namePredicate = string -> string.isEmpty() || !string.endsWith(".class");
	
	@SuppressWarnings("resource")
	public static <T> Class<? extends T> addToClassPath(File jarFile, final Class<T> clazz) throws Exception {
		if (jarFile.exists() == Boolean.FALSE) return null;

		final URL jarURL = jarFile.toURI().toURL();
		final URLClassLoader loader = new URLClassLoader(new URL[] { jarURL }, clazz.getClassLoader());
		final List<String> matches = Lists.newArrayList();
		final List<Class<? extends T>> classes = Lists.newArrayList();
		
		try (final JarInputStream stream = new JarInputStream(jarURL.openStream())) {
			
			final AtomicReference<JarEntry> entry = new AtomicReference<JarEntry>(null);

			do {
				entry.set(stream.getNextJarEntry());
				final String name = entry.get().getName();
				
				if (namePredicate.test(name)) continue;
				matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
				
			}while(Optional.ofNullable(entry.get()).isPresent());
			
			matches.stream().forEach(match -> {
				try {
					final Class<?> loaded = loader.loadClass(match);
					if (clazz.isAssignableFrom(loaded)) {
						classes.add(loaded.asSubclass(clazz));
					}
				} catch (final NoClassDefFoundError | ClassNotFoundException ignored) {
					
				}
			});
		}
		
		if (classes.isEmpty()) {
			loader.close();
			return null;
		}
		
		return classes.get(0);
	}

}
