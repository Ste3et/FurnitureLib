package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.ProtocolFields;

public class fProtocol{

	private ProtocolManager manager;
	private WrappedDataWatcher watcher;
	private PacketContainer container, metadata;
	private EntityType type;
	private ObjectID id;
	private ProtocolFields fields = ProtocolFields.Spigot19;
	
	public EntityType getEntityType(){return this.type;}
	public ProtocolManager getManager(){return this.manager;}
	public WrappedDataWatcher getWatcher(){return this.watcher;}
	public PacketContainer getHandle(){return this.container;}
	public ObjectID getObjID(){return this.id;}
	public void setObjectID(ObjectID id){this.id = id;}
	public ProtocolFields getField(){return this.fields;}
	public Project getProject(){return getObjID().getProjectOBJ();}
	protected PacketContainer getMetadata() {return this.metadata;}
	
	public fProtocol(EntityType type, ObjectID id){
		this.manager = ProtocolLibrary.getProtocolManager();
		this.id = id;
		this.type = type;
		this.container = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		this.container.getModifier().writeDefaults();
		this.metadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
		this.watcher = FurnitureLib.getInstance().getFurnitureManager().getDefaultWatcher(Bukkit.getWorlds().get(0),getEntityType());
		this.watcher = new WrappedDataWatcher();
		this.fields = FurnitureLib.getInstance().getField();
	}
}
