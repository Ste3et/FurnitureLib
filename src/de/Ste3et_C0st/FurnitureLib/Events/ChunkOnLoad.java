package de.Ste3et_C0st.FurnitureLib.Events;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.HiddenStringUtils;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class ChunkOnLoad implements Listener{
	
	public HashSet<Player> eventList = new HashSet<Player>();
	
	/*
	 * Spawn furniture from Project
	 */
	
	@EventHandler
	public void onSpawn(final PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			final Block b = e.getClickedBlock();
			final ItemStack stack = e.getItem();
			if(stack == null) return;
			final Project pro = getProjectByItem(stack);
			if(pro == null) return;
			e.setCancelled(true);
			if(b == null) return;
			if(FurnitureLib.getInstance().getBlockManager().getList().contains(b.getLocation())) return;
			if(eventList.contains(e.getPlayer())) return;
			if(b.isLiquid()) return;
			if(!e.getHand().equals(EquipmentSlot.HAND)) return;
			eventList.add(e.getPlayer());
			final BlockFace face = e.getBlockFace();
			final Location loc = b.getLocation();
			final Player p = e.getPlayer();
			loc.setYaw(FurnitureLib.getInstance().getLocationUtil().FaceToYaw(FurnitureLib.getInstance().getLocationUtil().yawToFace(p.getLocation().getYaw())));
			Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
				@Override
				public void run() {
					FurnitureItemEvent e = new FurnitureItemEvent(p, stack, pro, loc, face);
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
		}else if(e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			final ItemStack stack = e.getItem();
			if(stack == null) return;
			final Project pro = getProjectByItem(stack);
			if(pro == null) return;
			e.setCancelled(true);
		}
		
		
	}
	
	/*
	 * RightClick Block
	 */
	
	@EventHandler
	public void onRightClickBlock(final PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			final Block b = e.getClickedBlock();
			if(b== null) return;
			if(!FurnitureLib.getInstance().getBlockManager().getList().contains(b.getLocation())) return;
			e.setCancelled(true);
			final Location loc = b.getLocation();
			final Player p = e.getPlayer();
			loc.setYaw(FurnitureLib.getInstance().getLocationUtil().FaceToYaw(FurnitureLib.getInstance().getLocationUtil().yawToFace(p.getLocation().getYaw())));
			Location blockLocation = b.getLocation();
			boolean bool = b.getType().equals(Material.FLOWER_POT) ? false : true;
			final ObjectID objID = FurnitureManager.getInstance().getObjectList().stream().filter(obj -> obj.getBlockList().contains(blockLocation)).findFirst().orElse(null);
			if(objID==null){return;}
			if(objID.isPrivate()){return;}
			e.setCancelled(bool);
			if(bool && !objID.getSQLAction().equals(SQLAction.REMOVE)) {
				if(p.getGameMode().equals(GameMode.CREATIVE)&&!FurnitureLib.getInstance().creativeInteract()){
					if(!FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.bypass.creative.interact")){
						return;
					}
				}
				
				if(FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(p.getUniqueId())){
					PostFurnitureGhostBlockClickEvent pEvent = new PostFurnitureGhostBlockClickEvent(p, b, objID);
					Bukkit.getPluginManager().callEvent(pEvent);
					if(!pEvent.isCancelled()) p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleEvent"));
					return;
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
					@Override
					public void run() {
						PostFurnitureBlockClickEvent pEvent = new PostFurnitureBlockClickEvent(p, b, objID);
						Bukkit.getPluginManager().callEvent(pEvent);
						if(!pEvent.isCancelled()){
							FurnitureBlockClickEvent e = new FurnitureBlockClickEvent(p, b, objID);
							Bukkit.getPluginManager().callEvent(e);
						}
						
					}});
			}
		}
	}
	
	@EventHandler
	public void onClick(final PlayerInteractEvent event){
		final Player p = event.getPlayer();
		if(p==null) return;
		if(p.getGameMode().equals(GameMode.SPECTATOR)){return;}
		if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
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
	public void onEntityRightClick(PlayerInteractEntityEvent e) {
		if(e.getRightClicked() != null && e.getPlayer() != null) {
			PlayerInventory inv = e.getPlayer().getInventory();
			if(getProjectByItem(inv.getItemInMainHand()) != null) {
				e.setCancelled(true);
				return;
			}
			if(getProjectByItem(inv.getItemInOffHand()) != null) {
				e.setCancelled(true);
				return;
			}
		}
	}
	
	public void spawn(FurnitureItemEvent e){
		if(e.isCancelled()){return;}
		if(!e.getProject().hasPermissions(e.getPlayer())){return;}
		ObjectID obj = e.getObjID();
		if(FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(e.getPlayer().getUniqueId())){
			e.getPlayer().sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleEvent"));
			return;
		}
		FurnitureLib.getInstance().spawn(obj.getProjectOBJ(), obj);
		e.finish();
		e.removeItem();
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
		ItemStack copy = is.clone();
		copy.setAmount(1);
		return copy;
	}
}
