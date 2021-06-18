package de.Ste3et_C0st.FurnitureLib.Listener.render;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class RenderWithProtocols extends RenderEventHandler{

	private final PacketListener packetListener;

	
	public RenderWithProtocols() {
		this.packetListener = getPacketListener();
	}
	
	@Override
	public void register() {
        ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
	}

	@Override
	public void remove() {
		ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
		
	}

	private PacketListener getPacketListener() {
		return new PacketAdapter(FurnitureLib.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Server.SPAWN_ENTITY) {
			public void onPacketSending(PacketEvent event) {
				int chunkX = event.getPacket().getIntegers().read(0);
				int chunkZ = event.getPacket().getIntegers().read(1);
				final Player player = event.getPlayer();
				Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), () -> {
					getFurnitureManager().updatePlayerView(player, chunkX, chunkZ);
				});
			}
		};
	}
	
}
