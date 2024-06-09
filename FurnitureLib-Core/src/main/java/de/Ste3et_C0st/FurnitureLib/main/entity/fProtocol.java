package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.ProtocolFields;

import org.bukkit.entity.EntityType;

public class fProtocol {

    private final static ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    private final static ProtocolFields fields = FurnitureLib.getInstance().getField();
    private final PacketContainer container;
    private final WrappedDataWatcher watcher;
    
    private EntityType type;
    private ObjectID id;
    private final static PacketType packetType;
    
    static {
    	packetType = FurnitureLib.getVersionInt() > 18 ? PacketType.Play.Server.SPAWN_ENTITY : PacketType.Play.Server.SPAWN_ENTITY_LIVING;
    }

    public fProtocol(EntityType type, ObjectID id) {
        this.id = id;
        this.type = type;
        this.container = new PacketContainer(packetType);
        this.container.getModifier().writeDefaults();
        this.watcher = new WrappedDataWatcher();
    }

    public EntityType getEntityType() {
        return this.type;
    }

    protected ProtocolManager getManager() {
        return manager;
    }

    public WrappedDataWatcher getWatcher() {
        return this.watcher;
    }

    public PacketContainer getHandle() {
        return this.container;
    }

    public ObjectID getObjID() {
        return this.id;
    }

    public void setObjectID(ObjectID id) {
        this.id = id;
    }

    protected ProtocolFields getField() {
        return fields;
    }

    public Project getProject() {
        return getObjID().getProjectOBJ();
    }
}
