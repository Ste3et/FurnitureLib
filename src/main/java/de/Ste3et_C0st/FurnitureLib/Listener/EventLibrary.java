package de.Ste3et_C0st.FurnitureLib.Listener;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

public abstract class EventLibrary {
    private FurnitureManager manager = null;

    public EventLibrary() {
        this.manager = FurnitureLib.getInstance().getFurnitureManager();
    }

    @Deprecated
    public FurnitureManager getFurnitureMgr() {
        return this.getFurnitureManager();
    }

    public FurnitureManager getFurnitureManager() {
        return this.manager;
    }
    
}
