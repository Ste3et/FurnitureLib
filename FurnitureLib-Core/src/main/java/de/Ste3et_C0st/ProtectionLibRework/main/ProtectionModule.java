package de.Ste3et_C0st.ProtectionLibRework.main;

import java.util.HashMap;

import de.Ste3et_C0st.ProtectionLib.main.ProtectionPluginFilter;
import de.Ste3et_C0st.ProtectionLib.main.protectionObj;

public abstract class ProtectionModule {

	public abstract HashMap<Class<? extends protectionObj>, ProtectionPluginFilter> generatePluginMap();
	
}
