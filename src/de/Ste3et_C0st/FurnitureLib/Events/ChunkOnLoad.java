package de.Ste3et_C0st.FurnitureLib.Events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.HiddenStringUtils;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class ChunkOnLoad implements Listener{
	
	public HashSet<Player> eventList = new HashSet<Player>();
	
	public FurnitureManager manager = FurnitureLib.getInstance().getFurnitureManager();

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		if (e.getTo().getBlock().getLocation().equals(e.getFrom().getBlock().getLocation())) return;
		Player player = e.getPlayer();
		if (player.getHealth() <= 0.0D) return;
		Chunk oldChunk = e.getFrom().getChunk();
		Chunk newChunk = e.getTo().getChunk();
		if (!oldChunk.equals(newChunk)) manager.updatePlayerView(player);
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event){
		final Player player = event.getPlayer();
		if(FurnitureLib.getInstance()==null) return;
		Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable(){
			@Override
			public void run() {
				manager.updatePlayerView(player);
			}
		},5);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable() {
			@Override
			public void run() {
				manager.updatePlayerView(player);
			}
		},5);
		
//		April fool ^^
//		if(player.isOp()){
//			FurnitureLib.getInstance().getUpdater().update();
//			FurnitureLib.getInstance().getUpdater().sendPlayer(player);
//			
//			ObjectID id = new ObjectID("Herobrine", FurnitureLib.getInstance().getName(), player.getLocation());
//			Location l = new Relative(player.getLocation(), -2, 0, 0, FurnitureLib.getInstance().getLocationUtil().yawToFace(player.getLocation().getYaw())).getSecondLocation();
//			l.setYaw(player.getLocation().getYaw());
//			fArmorStand stand = new fArmorStand(l, id);
//			stand.setName("§cHerobrine");
//			
//			ItemStack skull = new ItemStack(Material.SKULL_ITEM);
//			skull.setDurability((short) 3);
//			SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
//			skullMeta.setOwner("Herobrine");
//			skull.setItemMeta(skullMeta);
//			stand.setHelmet(skull);
//			stand.setArms(true);
//			
//			stand.setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
//			stand.setItemInOffHand(new ItemStack(Material.IRON_SWORD));
//			stand.setChestPlate(new ItemStack(Material.LEATHER_CHESTPLATE));
//			stand.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
//			stand.setBoots(new ItemStack(Material.LEATHER_BOOTS));
//			stand.setNameVasibility(true);
//			stand.setBasePlate(false);
//			id.addArmorStand(stand);
//			id.send(player);
//			
//			id.setPrivate(true);
//			id.setSQLAction(SQLAction.REMOVE);
//		}
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable() {
			@Override
			public void run() {
				manager.updatePlayerView(player);
			}
		},5);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		manager.removeFurniture(player);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), new Runnable() {
			@Override
			public void run() {
				manager.updatePlayerView(player);
			}
		},5);	
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		FurnitureLib.getInstance().getFurnitureManager().removeFurniture(player);
	}
	
	@EventHandler
	public void onClick(final PlayerInteractEvent event){
		final Player p = event.getPlayer();
		if(p==null) return;
		if(p.getGameMode().equals(GameMode.SPECTATOR)){return;}
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			if(event.getClickedBlock()==null){return;}
			if(event.getClickedBlock().getLocation()==null){return;}
			if(!event.getHand().equals(EquipmentSlot.HAND)){return;}
			ItemStack is = event.getItem();
			if(FurnitureLib.getInstance().getBlockManager()!=null){
				if(FurnitureLib.getInstance().getBlockManager().getList().contains(event.getClickedBlock().getLocation())){
					boolean b = true;
					if(event.getClickedBlock()!=null&&event.getClickedBlock().getState().getType().equals(Material.FLOWER_POT)){
						b = false;
					}
					ObjectID objID = null;
					for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
						if(obj.getBlockList().contains(event.getClickedBlock().getLocation())){
							objID = obj;
							break;
						}
					}
					
					if(objID!=null){
						if(objID.isPrivate()){return;}
					}else{
						return;
					}
					event.setCancelled(b);
					if(objID != null && !objID.getSQLAction().equals(SQLAction.REMOVE)){
						final ObjectID o = objID;
						if(p.getGameMode().equals(GameMode.CREATIVE)&&!FurnitureLib.getInstance().creativeInteract()){
							if(!FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.bypass.creative.interact")){
								return;
							}
						}
						if(FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(p.getUniqueId())){
							PostFurnitureGhostBlockClickEvent pEvent = new PostFurnitureGhostBlockClickEvent(p, event.getClickedBlock(), o);
							Bukkit.getPluginManager().callEvent(pEvent);
							if(!pEvent.isCancelled()){
								p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleEvent"));
							}
							return;
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
							@Override
							public void run() {
								PostFurnitureBlockClickEvent pEvent = new PostFurnitureBlockClickEvent(p, event.getClickedBlock(), o);
								Bukkit.getPluginManager().callEvent(pEvent);
								if(!pEvent.isCancelled()){
									FurnitureBlockClickEvent e = new FurnitureBlockClickEvent(p, event.getClickedBlock(), o);
									Bukkit.getPluginManager().callEvent(e);
								}
								
							}});
					}
				}
			}
			if(event.getItem()==null){return;}
			if(getProjectByItem(is)==null){return;}
			if(eventList.contains(event.getPlayer())) return;
			event.setCancelled(true);
			if(p.getGameMode().equals(GameMode.CREATIVE)&&!FurnitureLib.getInstance().creativePlace()){
				if(!FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.bypass.creative.place")){
					return;
				}
			}
			eventList.add(p);
			Project pro = getProjectByItem(is);
			
			final Player player = p;
			final ItemStack itemstack = is;
			final Project project = pro;
			final Location l = event.getClickedBlock().getLocation();
			final BlockFace face = event.getBlockFace();
			
			l.setYaw(FurnitureLib.getInstance().getLocationUtil().FaceToYaw(FurnitureLib.getInstance().getLocationUtil().yawToFace(p.getLocation().getYaw())));
			Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
				@Override
				public void run() {
					FurnitureItemEvent e = new FurnitureItemEvent(player, itemstack, project, l, face);
					Bukkit.getPluginManager().callEvent(e);
					if(!e.isCancelled()){
						if(e.canBuild()){
							if(e.isTimeToPlace()){
								if(e.sendAnouncer()){
									spawn(e);
								}
							}
						}
					}
				}
			});
			removePlayer(p);
		}else if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
			if(event.getClickedBlock()==null){return;}
			if(event.getClickedBlock().getLocation()==null){return;}
			if(FurnitureLib.getInstance()==null){return;}
			if(FurnitureLib.getInstance().getBlockManager()==null){return;}
			if(FurnitureLib.getInstance().getBlockManager().getList()==null){return;}
			if(FurnitureLib.getInstance().getBlockManager().getList().contains(event.getClickedBlock().getLocation())){
				ObjectID objID = null;
				for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
					if(obj.getBlockList().contains(event.getClickedBlock().getLocation())){
						objID = obj;
						break;
					}
				}
				if(objID!=null){
					if(objID.isPrivate()){return;}
				}else{
					return;
				}
				
				event.setCancelled(true);
				if(!objID.getSQLAction().equals(SQLAction.REMOVE)){
					final ObjectID o = objID;
					if(FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(p.getUniqueId())){
						PostFurnitureGhostBlockClickEvent pEvent = new PostFurnitureGhostBlockClickEvent(p, event.getClickedBlock(), o);
						Bukkit.getPluginManager().callEvent(pEvent);
						if(!pEvent.isCancelled()){
							p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleEvent"));
						}
						return;
					}
					Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
						@Override
						public void run() {
							PostFurnitureBlockBreakEvent pEvent = new PostFurnitureBlockBreakEvent(p, event.getClickedBlock(), o);
							Bukkit.getPluginManager().callEvent(pEvent);
							if(!pEvent.isCancelled()){
								FurnitureBlockBreakEvent e = new FurnitureBlockBreakEvent(p, event.getClickedBlock(), o);
								Bukkit.getPluginManager().callEvent(e);
							}
						}});
				}
			}
		}
	}
	
	@EventHandler
	public void explode(EntityExplodeEvent e){
		List<Block> blockList = new ArrayList<Block>(e.blockList());
		HashSet<Location> furnitureBlocks = FurnitureLib.getInstance().getBlockManager().getList();
		for(Block b : blockList){
			if(furnitureBlocks.contains(b.getLocation())){
				e.blockList().remove(b);
			}
		}
	}
	
	
	private void spawn(FurnitureItemEvent e){
		if(e.isCancelled()){return;}
		if(!e.getProject().hasPermissions(e.getPlayer())){return;}
		ObjectID obj = e.getObjID();
		if(!FurnitureLib.getInstance().getPermManager().canBuild(e.getPlayer(), obj.getStartLocation())){return;}
		if(FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(e.getPlayer().getUniqueId())){
			e.getPlayer().sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleEvent"));
			return;
		}
		FurnitureLib.getInstance().spawn(obj.getProjectOBJ(), obj);
		e.finish();
		e.removeItem();
	}
	
	private boolean hasPermissions(Player p, String name) {
		if(p.isOp()) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.admin")) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.player")) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.craft.*")) return true;
		if(FurnitureLib.getInstance().getPermission().hasPerm(p,"furniture.craft." + name)) return true;
		if(FurnitureLib.getInstance().getPermissionList()!=null){
			for(String s : FurnitureLib.getInstance().getPermissionList().keySet()){
				if(FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.craft.all." + s)){
					if(FurnitureLib.getInstance().getPermissionList().get(s).contains(name)){
						return true;
					}
				}
			}
		}
		return false;
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
				if(!hasPermissions(p, pro.getSystemID())){
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
		ItemStack stack = getItemStackCopy(is);
		if(stack==null) return null;
		String systemID = "";
		if(stack.hasItemMeta()){
			if(stack.getItemMeta().hasLore()){
				List<String> s = stack.getItemMeta().getLore();
				if(HiddenStringUtils.hasHiddenString(s.get(0))) systemID = HiddenStringUtils.extractHiddenString(s.get(0));
			}
		}
		
		for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
			if(pro==null) continue;
			if(pro.getSystemID()==null) continue;
			if(pro.getSystemID().equalsIgnoreCase(systemID)){
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
