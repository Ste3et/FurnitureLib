package de.Ste3et_C0st.FurnitureLib.Listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import de.Ste3et_C0st.FurnitureLib.SchematicLoader.Events.ProjectBreakEvent;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.Events.ProjectClickEvent;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EntityMoving;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class FurnitureProtocolListener {

	public FurnitureProtocolListener(FurnitureLib instance, final FurnitureManager manager) {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(instance, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
					public void onPacketReceiving(PacketEvent event) {
						if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
							Integer PacketID = event.getPacket().getIntegers().read(0);
							if (Objects.isNull(PacketID))
								return;
							ObjectID objID = manager.getfArmorStandByID(PacketID);
							if (Objects.nonNull(objID)) {
								event.setCancelled(true);
								if (objID.getSQLAction().equals(SQLAction.REMOVE)) {
									return;
								}
								if (objID.isPrivate()) {
									return;
								}

								Player player = event.getPlayer();
								if (Objects.isNull(player)) {
									return;
								}
								
								EntityUseAction action = event.getPacket().getEntityUseActions().readSafely(0);

								if (FurnitureLib.getVersionInt() > 16) {
									com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction wrappedEnumEntityUseAction = event.getPacket().getEnumEntityUseActions().readSafely(0);
									if(Objects.nonNull(wrappedEnumEntityUseAction)) {
										action = wrappedEnumEntityUseAction.getAction();
									}
								}

								switch (action) {
								case ATTACK:
									FurnitureProtocolListener.this.onLeftClick(player, objID);
									break;
								case INTERACT_AT:
									FurnitureProtocolListener.this.onRightClick(player, objID);
									break;
								default:
									break;
								}

							}
						}
					}
				});

		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(instance, ListenerPriority.HIGHEST, PacketType.Play.Client.STEER_VEHICLE) {
					public void onPacketReceiving(PacketEvent event) {
						if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
							final Player p = event.getPlayer();
							EntityMoving moving = event.getPacket().getBooleans().read(1) ? EntityMoving.SNEAKING
									: null;
							if (moving != null && moving.equals(EntityMoving.SNEAKING)) {
								List<fEntity> e = FurnitureManager.getInstance().getArmorStandFromPassenger(p);
								if (e != null && !e.isEmpty()) {
									fEntity f = e.stream().findFirst().get();
									if (f != null) {
										f.eject();
									}
								}
							}
						}
					}
				});
	}

	private void onLeftClick(Player player, ObjectID objectID) {
		if (GameMode.SPECTATOR == player.getGameMode())
			return;
		if (!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(player.getUniqueId())) {
			Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), () -> {
				ProjectBreakEvent projectBreakEvent = new ProjectBreakEvent(player, objectID);
				Bukkit.getPluginManager().callEvent(projectBreakEvent);
				if (!projectBreakEvent.isCancelled()) {
					Furniture furnitureOject = objectID.getFurnitureObject();
					if (Objects.nonNull(furnitureOject)) {
						furnitureOject.onBreak(player);
					}
				}
			});
		} else {
			player.sendMessage(LanguageManager.getInstance().getString("message.FurnitureToggleEvent"));
		}
	}

	private void onRightClick(Player player, ObjectID objectID) {
		if (GameMode.SPECTATOR == player.getGameMode())
			return;
		if (!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(player.getUniqueId())) {
			Bukkit.getScheduler().runTask(FurnitureLib.getInstance(), () -> {
				ProjectClickEvent projectBreakEvent = new ProjectClickEvent(player, objectID);
				Bukkit.getPluginManager().callEvent(projectBreakEvent);
				if (!projectBreakEvent.isCancelled()) {
					Furniture furnitureOject = objectID.getFurnitureObject();
					if (Objects.nonNull(furnitureOject)) {
						furnitureOject.onClick(player);
					}
				}
			});
		} else {
			player.sendMessage(LanguageManager.getInstance().getString("message.FurnitureToggleEvent"));
		}
	}
}
