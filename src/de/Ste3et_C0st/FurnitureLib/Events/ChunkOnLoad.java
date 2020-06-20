package de.Ste3et_C0st.FurnitureLib.Events;

import de.Ste3et_C0st.FurnitureLib.Command.listCommand;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.Events.ProjectBreakEvent;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.Events.ProjectClickEvent;
import de.Ste3et_C0st.FurnitureLib.Utilitis.HiddenStringUtils;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.Utilitis.callbacks.CallbackBoolean;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChunkOnLoad implements Listener {

    public HashSet<Player> eventList = new HashSet<Player>();

    /*
     * Spawn furniture from Project
     */

    public static Project getProjectByItem(ItemStack is) {
        if (is == null) return null;
        ItemStack stack = is.clone();
        if (stack.hasItemMeta()) {
            if (stack.getItemMeta().hasLore()) {
                String projectString = HiddenStringUtils.extractHiddenString(stack.getItemMeta().getLore().get(0));
                if (projectString != null)
                    return FurnitureManager.getInstance().getProjects().stream().filter(pro -> pro.getSystemID().equalsIgnoreCase(projectString)).findFirst().orElse(null);
            }
        }
        return null;
    }

    /*
     * RightClick Block
     */

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpawn(final PlayerInteractEvent e) {
        if (Action.RIGHT_CLICK_BLOCK == e.getAction()) {
            if (!e.hasBlock()) return;
            if (!e.hasItem()) return;
            if (e.useInteractedBlock().equals(Result.DENY)) return;
            if (e.useItemInHand().equals(Result.DENY)) return;
            if (e.isCancelled()) return;
            final Block b = e.getClickedBlock();
            final ItemStack stack = e.getItem();
            if (stack == null) return;
            final Project pro = getProjectByItem(stack);
            if (Objects.isNull(pro)) return;
            e.setCancelled(true);
            if (FurnitureLib.getInstance().getBlockManager().contains(b.getLocation())) return;
            if (eventList.contains(e.getPlayer())) return;
            if (b.isLiquid()) return;
            if (EquipmentSlot.HAND != e.getHand()) return;
            
            final Player player = e.getPlayer();
            
            if(FurnitureLib.getInstance().isWorldIgnored(e.getPlayer().getWorld().getName())) {
            	player.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.IgnoredWorld", new StringTranslator("%WORLD%", e.getPlayer().getWorld().getName())));
            	return;
            }
            
            eventList.add(player);
            
            final BlockFace face = e.getBlockFace();
            final Location loc = b.getLocation();
            
            loc.setYaw(FurnitureLib.getInstance().getLocationUtil().FaceToYaw(FurnitureLib.getInstance().getLocationUtil().yawToFace(player.getLocation().getYaw())));

			FurnitureItemEvent itemEvent = new FurnitureItemEvent(player, stack, pro, loc, face);
			FurnitureLib.debug("FurnitureLib -> Place Furniture Start (" + pro.getName() + ").");
			Bukkit.getPluginManager().callEvent(itemEvent);
			FurnitureLib.debug("FurnitureLib -> Call FurnitureItemEvent cancel (" + itemEvent.isCancelled() + ").");
			if (!itemEvent.isCancelled()) {
				if (itemEvent.canBuild()) {
					FurnitureLib.debug("FurnitureLib -> Can Place Model (" + pro.getName() + ") here");
					if (itemEvent.isTimeToPlace()) {
						itemEvent.debugTime("FurnitureLib -> {ChunkOnLoad} isTime to Place");
						if (itemEvent.sendAnnouncer()) {
							if (Objects.nonNull(itemEvent.getProject().getModelschematic())) {
								itemEvent.debugTime("FurnitureLib -> Model " + pro.getName() + " have Schematic place it.");
								if (pro.getModelschematic().isPlaceable(itemEvent.getObjID().getStartLocation())) {
									itemEvent.debugTime("FurnitureLib -> Model " + pro.getName() + " is Placeable");
									spawn(itemEvent);
								} else {
									player.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.NotEnoughSpace"));
								}
							} else {
								FurnitureLib.debug("FurnitureLib -> Can't place model [no Modelschematic (" + pro.getName() + ")]");
							}
						}
					}
				} else {
					FurnitureLib.debug("FurnitureLib -> Can't place model " + pro.getName() + " here canBuild(" + false + ")");
				}
			}
			removePlayer(player);
        } else if (Action.RIGHT_CLICK_AIR == e.getAction()) {
            final ItemStack stack = e.getItem();
            if (stack == null) return;
            final Project pro = getProjectByItem(stack);
            if (pro == null) return;
            e.setCancelled(true);
        }


    }
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
    	Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), () -> {
    		if(Objects.nonNull(event.getWorld())) {
    			FurnitureLib.getInstance().getSQLManager().getDatabase().loadWorld(SQLAction.NOTHING, event.getWorld());
    		}
    	}, 20L);
    }
    
    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
    	if(!event.isCancelled()) {
    		final World world = event.getWorld();
        	List<ObjectID> objects = FurnitureManager.getInstance().getObjectList().stream().filter(entry -> entry.getWorld().equals(world)).collect(Collectors.toList());
        	FurnitureLib.getInstance().getSQLManager().save(new CallbackBoolean() {
				@Override
				public void onResult(boolean paramBoolean) {
					if(paramBoolean) {
						FurnitureManager.getInstance().getObjectList().removeAll(objects);
					}
				}
			});
    	}
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRightClickBlock(final PlayerInteractEvent e) {
        if (Action.RIGHT_CLICK_BLOCK == e.getAction()) {
            final Block b = e.getClickedBlock();
            if (b == null) return;
            if (!FurnitureLib.getInstance().getBlockManager().contains(b.getLocation())) return;
            if(EquipmentSlot.HAND != e.getHand()) return;
            final Location loc = b.getLocation();
            final Player p = e.getPlayer();
            loc.setYaw(FurnitureLib.getInstance().getLocationUtil().FaceToYaw(FurnitureLib.getInstance().getLocationUtil().yawToFace(p.getLocation().getYaw())));
            Location blockLocation = b.getLocation();
            boolean bool = !b.getType().equals(Material.FLOWER_POT);
            final ObjectID objID = FurnitureManager.getInstance().getObjectList().stream().filter(obj -> obj.containsBlock(blockLocation)).findFirst().orElse(null);
            if (Objects.isNull(objID)) return;
            if (objID.isPrivate()) return;
            if (bool && SQLAction.REMOVE != objID.getSQLAction()) {
                if ((p.getGameMode() == GameMode.CREATIVE) && !FurnitureLib.getInstance().creativeInteract()) {
                    if (!FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.bypass.creative.interact")) {
                    	e.setCancelled(true);
                        return;
                    }
                }
                
                if (!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(p.getUniqueId())) {
                	ProjectClickEvent projectClickEvent = new ProjectClickEvent(p, objID);
                    Bukkit.getPluginManager().callEvent(projectClickEvent);
                    if (!projectClickEvent.isCancelled()) {
                    	objID.callFunction("onClick", p);
                    }
                    e.setCancelled(true);
                    return;
                } else {
                    e.getPlayer().sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureToggleEvent"));
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onClick(final PlayerInteractEvent event) {
        final Player p = event.getPlayer();
        if (p == null) return;
        if (GameMode.SPECTATOR == p.getGameMode())  return;
        if (event.isCancelled()) return;
        if (Action.LEFT_CLICK_BLOCK == event.getAction()) {
            if (event.getClickedBlock() == null) {
                return;
            }
            if (event.getClickedBlock().getLocation() == null) {
                return;
            }
            if (FurnitureLib.getInstance() == null) {
                return;
            }
            if (FurnitureLib.getInstance().getBlockManager() == null) {
                return;
            }
            if (FurnitureLib.getInstance().getBlockManager().getList() == null) {
                return;
            }
            if (FurnitureLib.getInstance().getBlockManager().contains(event.getClickedBlock().getLocation())) {
                ObjectID objID = null;
                for (ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()) {
                    if (obj.containsBlock(event.getClickedBlock().getLocation())) {
                        objID = obj;
                        break;
                    }
                }
                if (objID != null) {
                    if (objID.isPrivate()) {
                        return;
                    }
                } else {
                    return;
                }
                event.setCancelled(true);
                if (EquipmentSlot.HAND != event.getHand()) return;
                if (!objID.getSQLAction().equals(SQLAction.REMOVE)) {
                    final ObjectID o = objID;
                    if (!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(p.getUniqueId())) {
                    	ProjectBreakEvent projectBreakEvent = new ProjectBreakEvent(p, o);
                        Bukkit.getPluginManager().callEvent(projectBreakEvent);
                        if (!projectBreakEvent.isCancelled()) {
                            o.callFunction("onBreak", p);
                        }
                        return;
                    } else {
                        event.getPlayer().sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureToggleEvent"));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityRightClick(PlayerInteractEntityEvent e) {
        if (EquipmentSlot.HAND != e.getHand()) return;
        if (e.isCancelled()) return;
        if (e.getRightClicked() != null && e.getPlayer() != null) {
            PlayerInventory inv = e.getPlayer().getInventory();
            if (getProjectByItem(inv.getItemInMainHand()) != null) {
                e.setCancelled(true);
                return;
            }
            if (getProjectByItem(inv.getItemInOffHand()) != null) {
                e.setCancelled(true);
                return;
            }
        }
    }

    public void spawn(FurnitureItemEvent e) {
        if (e.isCancelled()) {
            return;
        }
        
        e.debugTime("FurnitureLib -> spawn Start " + e.getObjID().getProject());
        ObjectID obj = e.getObjID();
        if (FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureToggleEvent"));
            return;
        }
        if (FurnitureManager.getInstance().furnitureAlreadyExistOnBlock(obj.getStartLocation().getBlock())) {
            e.getPlayer().sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.FurnitureOnThisPlace"));
            return;
        }
        FurnitureLib.getInstance().spawn(obj.getProjectOBJ(), obj);
        e.finish();
        e.removeItem();
        FurnitureManager.getInstance().addObjectID(obj);
        
        if(FurnitureLib.useDebugMode()) {
        	FurnitureLib.debug("FurnitureLib -> Spawn Finish " + e.getObjID().getProject() + " it takes " + (System.currentTimeMillis() - e.getEventCallTime()) + "ms to spawn it");
        }
    }

    private void removePlayer(final Player p) {
        Bukkit.getScheduler().runTaskLater(FurnitureLib.getInstance(), () -> {
			if (eventList != null && !eventList.isEmpty() && p != null && p.isOnline()) {
				eventList.remove(p);
			}
		}, 1);
    }
}
