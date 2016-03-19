package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class fProtocol{

	private ProtocolManager manager;
	private WrappedDataWatcher watcher;
	private PacketContainer container;
	private World w;
	private EntityType type;
	
	public World getWorld(){return this.w;}
	public EntityType getEntityType(){return this.type;}
	public ProtocolManager getManager(){return this.manager;}
	public WrappedDataWatcher getWatcher(){return this.watcher;}
	public PacketContainer getHandle(){return this.container;}
	
	public fProtocol(World w, EntityType type){
		this.manager = ProtocolLibrary.getProtocolManager();
		this.w = w;
		this.type = type;
		this.watcher = FurnitureLib.getInstance().getFurnitureManager().getDefaultWatcher(getWorld(), getEntityType());
		this.container = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
	}
}
