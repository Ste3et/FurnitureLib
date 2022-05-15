package de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper.packet;

import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper.AbstractPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class WrapperPlayServerEntityDestroy extends AbstractPacket{

	public static final PacketType TYPE = PacketType.Play.Server.ENTITY_DESTROY;
	public static final BiConsumer<ObjectID, Player[]> KILLFUNCTION;
	
	static {
		if(FurnitureLib.getVersionInt() < 17) {
			KILLFUNCTION = (ObjectID, Player) -> {
				int[] integers = ObjectID.getPacketList().stream().mapToInt(fEntity::getEntityID).toArray();
				WrapperPlayServerEntityDestroy massDestruction = new WrapperPlayServerEntityDestroy();
				massDestruction.handle.getIntegerArrays().write(0, integers);
				for(Player player : Player) massDestruction.sendPacket(player);
			};
		}else if(FurnitureLib.getBukkitVersion().equalsIgnoreCase("v1_17_R1")) {
			if(Bukkit.getVersion().contains("1.17.1")) {
				KILLFUNCTION = (ObjectID, Player) -> {
					List<Integer> integers = ObjectID.getPacketList().stream().map(fEntity::getEntityID).map(Integer::valueOf).collect(Collectors.toList());
					WrapperPlayServerEntityDestroy massDestruction = new WrapperPlayServerEntityDestroy();
					massDestruction.handle.getIntLists().write(0, integers);
					for(Player player : Player) massDestruction.sendPacket(player);
				};
			}else {
				KILLFUNCTION = (ObjectID, Player) -> {
					ObjectID.getPacketList().stream().map(fEntity::getEntityID).forEach(entry -> {
						WrapperPlayServerEntityDestroy massDestruction = new WrapperPlayServerEntityDestroy();
						massDestruction.getHandle().getIntegers().write(0, entry);
						for(Player player : Player) massDestruction.sendPacket(player);
					});
				};
			}
		}else {
			KILLFUNCTION = (ObjectID, Player) -> {
				List<Integer> integers = ObjectID.getPacketList().stream().map(fEntity::getEntityID).map(Integer::valueOf).collect(Collectors.toList());
				WrapperPlayServerEntityDestroy massDestruction = new WrapperPlayServerEntityDestroy();
				massDestruction.handle.getIntLists().write(0, integers);
				for(Player player : Player) massDestruction.sendPacket(player);
			};
		}
	}
	
	public WrapperPlayServerEntityDestroy() {
		super(new PacketContainer(TYPE), TYPE);
	}
	
	public void sendPacket(Player receiver) {
		try {
			super.sendPacket(receiver);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void destroyPackets(ObjectID objectID, Player ... player) {
		KILLFUNCTION.accept(objectID, player);
	}

	public static void destroyPackets(ObjectID objectID, HashSet<Player> playerList) {
		destroyPackets(objectID, playerList.stream().toArray(size -> new Player[size]));
	}
}
