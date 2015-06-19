package de.Ste3et_C0st.FurnitureLib.Events;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class ChunkOnLoad implements Listener{
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getFrom().getWorld() == event.getTo().getWorld() &&
				event.getFrom().getBlockX() == event.getTo().getBlockX() &&
				event.getFrom().getBlockY() == event.getTo().getBlockY() &&
				event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;

		Player player = event.getPlayer();
		if (player.getHealth() <= 0.0D) return;

		Chunk oldChunk = event.getFrom().getChunk();
		Chunk newChunk = event.getTo().getChunk();

		if (oldChunk.getWorld() != newChunk.getWorld() || oldChunk.getX() != newChunk.getX() || oldChunk.getZ() != newChunk.getZ()) {
			FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		final Player player = event.getPlayer();

		Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable() {
			@Override
			public void run() {
				FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player);
			}
		}, 5L);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		FurnitureLib.getInstance().getFurnitureManager().removeFurniture(player);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();

		Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable() {
			@Override
			public void run() {
				FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player);
			}
		}, 5L);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		FurnitureLib.getInstance().getFurnitureManager().removeFurniture(player);
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event){
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if(event.getClickedBlock()==null||event.getItem()==null){return;}
			Player p = event.getPlayer();
			ItemStack is = event.getItem();
			if(getProjectByItem(is)==null){return;}
			Project pro = getProjectByItem(is);
			Location l = event.getClickedBlock().getLocation();
			FurnitureItemEvent e = new FurnitureItemEvent(p, is, pro, l);
			Bukkit.getPluginManager().callEvent(e);
		}
	}
	
	private Project getProjectByItem(ItemStack is){
		for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
			if(pro.getCraftingFile().getRecipe().getResult().equals(getItemStackCopy(is))){
				return pro;
			}
		}
		return null;
	}
	
	private ItemStack getItemStackCopy(ItemStack is){
		ItemStack copy = new ItemStack(is.getType());
		copy.setAmount(1);
		copy.setDurability(is.getDurability());
		copy.setItemMeta(is.getItemMeta());
		return copy;
	}
}
