package de.Ste3et_C0st.FurnitureLib.Listener;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureItemEvent;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.Events.ProjectBreakEvent;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.Events.ProjectClickEvent;
import de.Ste3et_C0st.FurnitureLib.Utilitis.HiddenStringUtils;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.Utilitis.SchedularHelper;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.Utilitis.callbacks.CallbackBoolean;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChunkOnLoad implements Listener {

    public HashSet<Player> eventList = new HashSet<Player>();

    public static Project getProjectByItem(ItemStack is) {
        return FurnitureLib.getInstance().getFurnitureManager().getProject(getProjectString(is));
    }
    
    public static String getProjectString(ItemStack stack) {
    	if (Objects.isNull(stack)) return null;
    	if (stack.hasItemMeta() == false) return null;
    	if(FurnitureLib.getVersionInt() > 13) {
    		ItemMeta meta = stack.getItemMeta();
    		if (meta.hasLore()) {
    			if(HiddenStringUtils.hasHiddenString(stack.getItemMeta().getLore().get(0))) {
    				return HiddenStringUtils.extractHiddenString(stack.getItemMeta().getLore().get(0));
    			}
    		}
    		org.bukkit.NamespacedKey key = new org.bukkit.NamespacedKey(FurnitureLib.getInstance(), "model");
    		return meta.getPersistentDataContainer().getOrDefault(key, org.bukkit.persistence.PersistentDataType.STRING, null);
    	}else if (stack.getItemMeta().hasLore()) {
    		String projectString = HiddenStringUtils.extractHiddenString(stack.getItemMeta().getLore().get(0));
            if (projectString != null) return projectString;
    	}
    	return null;
    }

    /*
     * RightClick Block
     */

    @EventHandler(priority = EventPriority.HIGH)
    public void onSpawn(final PlayerInteractEvent event) {
    	final Action action = event.getAction();
    	final EquipmentSlot equipmentSlot = event.getHand();
    	final Player player = event.getPlayer();
    	if(event.hasItem()) {
    		final ItemStack stack = event.getItem();
    		final String projectString = getProjectString(stack);
    		if(Objects.isNull(projectString)) return;
    		if(projectString.isEmpty()) return;
    		
    		final Project project = FurnitureManager.getInstance().getProject(projectString);
    		event.setCancelled(true);
    		
    		if(Objects.isNull(project)) {
    			LanguageManager.getInstance().sendMessage(player, "message.ProjectNotFound", new StringTranslator("project", projectString));
    			return;
    		}
    		
    		final String projectName = project.getName();
    		
    		if(EquipmentSlot.HAND != equipmentSlot) {
    			return;
    		}
    		
    		if(Action.RIGHT_CLICK_BLOCK != action) {
    			return;
    		}
    		
    		if(event.hasBlock() == false) {
    			return;
    		}
    		
    		final BlockFace blockFace = event.getBlockFace();
    		final Block block = event.getClickedBlock();
    		final Location location = block.getLocation();
    		final World world = location.getWorld();
    		
    		if(block.isLiquid() == true) {
    			return;
    		}
    		
    		if(this.eventList.contains(player) == true) {
    			return;
    		}
    		
    		if(FurnitureLib.getInstance().getBlockManager().contains(location) == true) {
    			return;
    		}
    		
    		if(FurnitureConfig.getFurnitureConfig().isWorldIgnored(world.getName()) == true) {
    			LanguageManager.send(player, "message.IgnoredWorld", new StringTranslator("world", world.getName()));
    			return;
    		}
    		
    		this.eventList.add(player);
    		
    		location.setYaw(FurnitureLib.getInstance().getLocationUtil().FaceToYaw(LocationUtil.yawToFace(player.getLocation().getYaw())));
    		
    		FurnitureItemEvent itemEvent = new FurnitureItemEvent(player, stack, project, location, blockFace);
			FurnitureLib.debug("FurnitureLib -> Place Furniture Start (" + projectName + ").");
			Bukkit.getPluginManager().callEvent(itemEvent);
			FurnitureLib.debug("FurnitureLib -> Call FurnitureItemEvent cancel (" + itemEvent.isCancelled() + ").");
			if (!itemEvent.isCancelled()) {
				if (itemEvent.canBuild()) {
					FurnitureLib.debug("FurnitureLib -> Can Place Model (" + projectName + ") here");
					if (itemEvent.isTimeToPlace()) {
						itemEvent.debugTime("FurnitureLib -> {ChunkOnLoad} isTime to Place");
						if (Objects.nonNull(itemEvent.getProject().getModelschematic())) {
							itemEvent.debugTime("FurnitureLib -> Model " + projectName + " have Schematic place it.");
							if (project.getModelschematic().isPlaceable(itemEvent.getObjID().getStartLocation())) {
								itemEvent.debugTime("FurnitureLib -> Model " + projectName + " is Placeable");
								spawn(itemEvent);
							} else {
								LanguageManager.send(player, "message.NotEnoughSpace");
							}
						} else {
							FurnitureLib.debug("FurnitureLib -> Can't place model [no Modelschematic (" + projectName + ")]");
						}
					}
				} else {
					FurnitureLib.debug("FurnitureLib -> Can't place model " + projectName + " here canBuild(" + false + ")");
				}
			}
    		
    		this.removePlayer(player);
    	}
    }
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
    	SchedularHelper.runLater(() -> {
    		if(Objects.nonNull(event.getWorld())) {
    			FurnitureLib.getInstance().getSQLManager().getDatabase().loadWorld(SQLAction.NOTHING, event.getWorld());
    		}
    	}, 20, true);
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
            final Player player = e.getPlayer();
            if (!FurnitureLib.getInstance().getBlockManager().contains(b.getLocation())) return;
            if(EquipmentSlot.HAND != e.getHand()) return;
            final Location loc = b.getLocation();
            loc.setYaw(FurnitureLib.getInstance().getLocationUtil().FaceToYaw(LocationUtil.yawToFace(player.getLocation().getYaw())));
            Location blockLocation = b.getLocation();
            boolean bool = FurnitureLib.isNewVersion() ? !org.bukkit.Tag.FLOWER_POTS.isTagged(b.getType()) : !b.getType().equals(Material.FLOWER_POT);
            final ObjectID objID = FurnitureManager.getInstance().getObjectList().stream().filter(obj -> obj.containsBlock(blockLocation)).findFirst().orElse(null);
            if (Objects.isNull(objID)) return;
            if (objID.isPrivate()) return;
            if (bool && SQLAction.REMOVE != objID.getSQLAction()) {
                if ((player.getGameMode() == GameMode.CREATIVE) && !FurnitureConfig.getFurnitureConfig().creativeInteract()) {
                    if (!FurnitureLib.getInstance().getPermission().hasPerm(player, "furniture.bypass.creative.interact")) {
                    	e.setCancelled(true);
                        return;
                    }
                }
                
                if (!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(player.getUniqueId())) {
                	ProjectClickEvent projectClickEvent = new ProjectClickEvent(player, objID);
                    Bukkit.getPluginManager().callEvent(projectClickEvent);
                    if (!projectClickEvent.isCancelled()) {
                    	Furniture furniture = objID.getFurnitureObject();
                    	if(Objects.nonNull(furniture)) {
                    		furniture.onClick(player);
                    	}
                    }
                    e.setCancelled(true);
                    return;
                } else {
                    LanguageManager.send(player, "message.FurnitureToggleEvent");
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onClick(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (player == null) return;
        if (GameMode.SPECTATOR == player.getGameMode())  return;
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
            final Location location = event.getClickedBlock().getLocation();
            if (FurnitureLib.getInstance().getBlockManager().contains(location)) {
            	FurnitureLib.getInstance().getFurnitureManager().getAllExistObjectIDs()
            		.filter(entry -> entry.containsBlock(location))
            		.findFirst()
            		.ifPresent(objectID -> {
	                	if(objectID.isPrivate()) return;
	                	event.setCancelled(true);
	            		if (!FurnitureLib.getInstance().getFurnitureManager().getIgnoreList().contains(player.getUniqueId())) {
	                    	ProjectBreakEvent projectBreakEvent = new ProjectBreakEvent(player, objectID);
	                        Bukkit.getPluginManager().callEvent(projectBreakEvent);
	                        if (!projectBreakEvent.isCancelled()) {
	                        	Furniture furniture = objectID.getFurnitureObject();
	                        	if(Objects.nonNull(furniture)) {
	                        		furniture.onBreak(player);
	                        	}
	                        }
	                    } else {
	                        LanguageManager.send(event.getPlayer(), "message.FurnitureToggleEvent");
	                    }
                });
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
            LanguageManager.send(e.getPlayer(), "message.FurnitureToggleEvent");
            return;
        }
        if (FurnitureManager.getInstance().furnitureAlreadyExistOnBlock(obj.getStartLocation().getBlock())) {
            LanguageManager.send(e.getPlayer(), "message.FurnitureOnThisPlace");
            return;
        }
        
        if (e.sendAnnouncer()) {
        	FurnitureLib.getInstance().spawn(obj.getProjectOBJ(), obj);
            e.finish();
            e.removeItem();
            FurnitureManager.getInstance().addObjectID(obj);
            
            if(FurnitureLib.useDebugMode()) {
            	FurnitureLib.debug("FurnitureLib -> Spawn Finish " + e.getObjID().getProject() + " it takes " + (System.currentTimeMillis() - e.getEventCallTime()) + "ms to spawn it");
            }
		}
    }

    private void removePlayer(final Player p) {
    	SchedularHelper.runLater(() -> {
    		if (eventList != null && !eventList.isEmpty() && p != null && p.isOnline()) {
				eventList.remove(p);
			}
    	}, 1, true);
    }
}
