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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.HiddenStringUtils;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class ChunkOnLoad implements Listener{
	
	public HashSet<Player> eventList = new HashSet<Player>();
	
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
				final Block block = event.getClickedBlock();
				Location blockLocation = block.getLocation();
				if(FurnitureLib.getInstance().getBlockManager().getList().contains(blockLocation)){
					boolean b = true;
					if(block.getState().getType().equals(Material.FLOWER_POT)){
						b = false;
					}
					ObjectID objID = null;
					for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
						if(obj.getBlockList().contains(blockLocation)){
							objID = obj;
							break;
						}
					}

					if(objID==null){return;}
					if(objID.isPrivate()){return;}
					event.setCancelled(b);
					if(objID != null && !objID.getSQLAction().equals(SQLAction.REMOVE)){
						final ObjectID o = objID;
						if(p.getGameMode().equals(GameMode.CREATIVE)&&!FurnitureLib.getInstance().creativeInteract()){
							if(!FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.bypass.creative.interact")){
								return;
							}
						}
						if(FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(p.getUniqueId())){
							PostFurnitureGhostBlockClickEvent pEvent = new PostFurnitureGhostBlockClickEvent(p, block, o);
							Bukkit.getPluginManager().callEvent(pEvent);
							if(!pEvent.isCancelled()) p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("FurnitureToggleEvent"));
							return;
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(FurnitureLib.getInstance(), new Runnable() {
							@Override
							public void run() {
								PostFurnitureBlockClickEvent pEvent = new PostFurnitureBlockClickEvent(p, block, o);
								Bukkit.getPluginManager().callEvent(pEvent);
								if(!pEvent.isCancelled()){
									FurnitureBlockClickEvent e = new FurnitureBlockClickEvent(p, block, o);
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
