package de.Ste3et_C0st.FurnitureLib.Events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.FlowerPot;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class ChunkOnLoad implements Listener{
	
	public List<Player> eventList = new ArrayList<Player>();
	
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
	public void onLoad(ChunkLoadEvent event){
		//FurnitureLib.getInstance().getFurnitureManager().refreshChunk(event.getChunk());
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
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable() {
			@Override
			public void run() {
				FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player);
			}
		}, 5L);
		
		if(player.isOp()){
			FurnitureLib.getInstance().getUpdater().update();
			FurnitureLib.getInstance().getUpdater().sendPlayer(player);
		}
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable() {
			@Override
			public void run() {
				FurnitureLib.getInstance().getFurnitureManager().updatePlayerView(player);
			}
		}, 5L);
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
	public void onClick(final PlayerInteractEvent event){
		final Player p = event.getPlayer();
		if(p.getGameMode().equals(GameMode.SPECTATOR)){return;}
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if(event.getClickedBlock()==null){return;}
			
			ItemStack is = event.getItem();
			if(FurnitureLib.getInstance().getBlockManager().getList().contains(event.getClickedBlock().getLocation())){
					boolean b = true;
					if(event.getClickedBlock()!=null&&event.getClickedBlock().getState().getData() instanceof FlowerPot){
						b = false;
					}
					event.setCancelled(b);
					ObjectID objID = null;
					for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
						if(obj.getBlockList().contains(event.getClickedBlock().getLocation())){
							objID = obj;
							break;
						}
					}
					
					if(objID != null && !objID.getSQLAction().equals(SQLAction.REMOVE)){
						final ObjectID o = objID;
						Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
							@Override
							public void run() {
								FurnitureBlockClickEvent e = new FurnitureBlockClickEvent(p, event.getClickedBlock(), o);
								Bukkit.getPluginManager().callEvent(e);
							}});
					}
			}
			if(event.getItem()==null){return;}
			if(getProjectByItem(is)==null){return;}
			if(eventList.contains(event.getPlayer())) return;
			eventList.add(p);
			event.setCancelled(true);
			Project pro = getProjectByItem(is);
			
			final Player player = p;
			final ItemStack itemstack = is;
			final Project project = pro;
			final Location l = event.getClickedBlock().getLocation();
			final BlockFace face = event.getBlockFace();
			l.setYaw(p.getLocation().getYaw());
			Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
				@Override
				public void run() {
					FurnitureItemEvent e = new FurnitureItemEvent(player, itemstack, project, l, face);
					Bukkit.getPluginManager().callEvent(e);
					spawn(e);
				}
			});
			removePlayer(p);
		}else if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
			if(event.getClickedBlock()==null){return;}
			if(FurnitureLib.getInstance().getBlockManager().getList().contains(event.getClickedBlock().getLocation())){
				event.setCancelled(true);
				ObjectID objID = null;
				for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
					if(obj.getBlockList().contains(event.getClickedBlock().getLocation())){
						objID = obj;
						break;
					}
				}
				if(objID != null && !objID.getSQLAction().equals(SQLAction.REMOVE)){
					final ObjectID o = objID;
					Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
						@Override
						public void run() {
							FurnitureBlockBreakEvent e = new FurnitureBlockBreakEvent(p, event.getClickedBlock(), o);
							Bukkit.getPluginManager().callEvent(e);
						}});
				}
			}
		}
	}
	
	private void spawn(FurnitureItemEvent e){
		if(e.isCancelled()){return;}
		if(!e.getProject().hasPermissions(e.getPlayer())){return;}
		ObjectID obj = e.getObjID();
		if(!e.canBuild()){return;}
		FurnitureLib.getInstance().spawn(obj.getProjectOBJ(), obj);
		e.finish();
		e.removeItem();
	}
	
	@EventHandler
	private void onCrafting(PrepareItemCraftEvent e){
		if(FurnitureLib.getInstance().getFurnitureManager().getProjects().isEmpty()){return;}
		Player p = (Player) e.getView().getPlayer();
		if(p.isOp()) return;
		if(e.getInventory()==null) return;
		if(e.getInventory().getResult()==null) return;
		ItemStack is = e.getInventory().getResult().clone();
		is.setAmount(1);
		for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
			if(is.equals(pro.getCraftingFile().getRecipe().getResult())){
				if(!FurnitureLib.getInstance().hasPerm(p,"furniture.craft." + pro.getName()) && !FurnitureLib.getInstance().hasPerm(p,"furniture.player") && !FurnitureLib.getInstance().hasPerm(p,"furniture.admin")){
					e.getInventory().setResult(null);
				}
			}
		}
	}
	
	private void removePlayer(final Player p){
		Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable() {
			@Override
			public void run() {
				if(eventList!=null&&!eventList.isEmpty()&&p!=null&&p.isOnline()&&eventList.contains(p)){
					eventList.remove(p);
				}
			}
		}, 1);
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
