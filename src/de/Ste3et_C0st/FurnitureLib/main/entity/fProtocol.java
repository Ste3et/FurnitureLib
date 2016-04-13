package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.World;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fProtocol{

	private ProtocolManager manager;
	private WrappedDataWatcher watcher;
	private PacketContainer container;
	private World w;
	private EntityType type;
	private ObjectID id;
	
	public World getWorld(){return this.w;}
	public EntityType getEntityType(){return this.type;}
	public ProtocolManager getManager(){return this.manager;}
	public WrappedDataWatcher getWatcher(){return this.watcher;}
	public PacketContainer getHandle(){return this.container;}
	public ObjectID getObjID(){return this.id;}
	public void setObjectID(ObjectID id){this.id = id;}
	public fProtocol(World w, EntityType type, ObjectID id){
		this.manager = ProtocolLibrary.getProtocolManager();
		this.w = w;
		this.id = id;
		this.type = type;
		this.watcher = FurnitureLib.getInstance().getFurnitureManager().getDefaultWatcher(getWorld(), getEntityType());
		this.container = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
	}
}
