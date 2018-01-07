package de.Ste3et_C0st.FurnitureLib.Events.internal;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

public abstract class EventLibary{
	
	private FurnitureManager manager = null;
	
	public EventLibary() {
		this.manager = FurnitureLib.getInstance().getFurnitureManager();
	}
	
	public FurnitureManager getFurnitureMgr() {return this.manager;}
	
}
