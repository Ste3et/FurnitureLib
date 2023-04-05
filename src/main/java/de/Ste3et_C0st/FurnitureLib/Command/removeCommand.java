package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.Utilitis.cache.DiceOfflinePlayer;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class removeCommand extends iCommand {

    public removeCommand(String subCommand, String... args) {
        super(subCommand);
        String tab = "project:/plugin:/world:/player:/distance:/obj:/lookat/all";
        setTab(tab,tab,tab);
    }

    public void execute(CommandSender sender, String[] args) {
    	if (!hasCommandPermission(sender)) return;
    	List<ObjectID> objectList = new ArrayList<ObjectID>(FurnitureManager.getInstance().getObjectList());
        String filterTypes = "";
        boolean remove = true;
        if(args.length < 2) {
        	getLHandler().sendMessage(sender, "message.WrongArgument");
        	return;
        }
        
        Predicate<ObjectID> filterPredicate = Objects::nonNull;
		filterPredicate = filterPredicate.and(entry -> SQLAction.REMOVE != entry.getSQLAction());
        
        for(String argument : args) {
        	argument = argument.toLowerCase();
        	if(argument.startsWith("plugin:") && !filterTypes.contains("plugin")) {
        		if (!hasCommandPermission(sender, ".plugin")) return;
        		String objectStr = argument.replace("plugin:", "");
        		filterTypes ="§7object:§a" + objectStr + "§8|";
        		filterPredicate = filterPredicate.and(entry -> entry.getPlugin().equalsIgnoreCase(objectStr));
        	}else if(argument.startsWith("obj:") && !filterTypes.contains("object")) {
        		if (!hasCommandPermission(sender, ".object")) return;
        		String objectStr = argument.replace("obj:", "");
        		filterTypes ="§7object:§a" + objectStr + "§8|";
        		filterPredicate = filterPredicate.and(entry -> entry.getSerial().equalsIgnoreCase(objectStr));
        		break;
        	}else if(argument.equalsIgnoreCase("all")) {
        		if (!hasCommandPermission(sender, ".all")) return;
        		filterTypes = "§aall";
        		filterPredicate = Objects::nonNull;
        		break;
        	}else if(argument.equalsIgnoreCase("lookat")) {
        		if (!hasCommandPermission(sender, ".lookat")) return;
        		Player p = (Player) sender;
                ObjectID obj = getFromSight(p.getLocation());
                if (Objects.nonNull(obj)) {
                	filterTypes = "§alookat";
                	filterPredicate = filterPredicate.and(entry -> entry.equals(obj));
                	break;
                }else {
                	filterTypes = "§clookat";
                	remove = false;
                	break;
                }
        	}else if(argument.startsWith("project:") && !filterTypes.contains("project")){
        		if (!hasCommandPermission(sender, ".project")) return;
        		String project = argument.replace("project:", "");
        		filterTypes += "§7project:§a" + project + "§8|";
        		filterPredicate = filterPredicate.and(entry -> entry.getProject().equalsIgnoreCase(project));
        	}else if(argument.startsWith("world:") && !filterTypes.contains("world")) {
        		if (!hasCommandPermission(sender, ".world")) return;
        		String world = argument.replace("world:", "");
        		filterPredicate = filterPredicate.and(entry -> entry.getWorldName().equalsIgnoreCase(world));
        		filterTypes +="§7world:" + world + "§8|";
        	}else if(argument.startsWith("player:") && !filterTypes.contains("player")) {
        		if (!hasCommandPermission(sender, ".player")) return;
        		String username = argument.replace("player:", "");
        		Optional<DiceOfflinePlayer> offlinePlayer = FurnitureLib.getInstance().getPlayerCache().getPlayer(username);
        		if(offlinePlayer.isPresent()) {
        			filterTypes +="§7player:§a" + username + "§8|";
        			filterPredicate = filterPredicate.and(entry -> entry.getUUID().equals(offlinePlayer.get().getUuid()));
        		}else {
        			filterTypes +="§7player:§c" + argument.replace("player:", "") + "§8|";
        			remove = false;
        			break;
        		}
        	}else if(argument.startsWith("distance:") && !filterTypes.contains("distance")) {
        		if (!hasCommandPermission(sender, ".distance")) return;
        		if(Player.class.isInstance(sender)) {
        			Player player = Player.class.cast(sender);
        			AtomicInteger distance = new AtomicInteger(0);
            		try {
            			distance.set(Integer.parseInt(argument.replace("distance:", "")));
            			World world = player.getWorld();
            			filterPredicate = filterPredicate.and(entry -> entry.getWorldName().equalsIgnoreCase(world.getName())).and(entry -> entry.getStartLocation().distance(player.getLocation()) < distance.get());
            			filterTypes +="§7distance:§a" + argument.replace("distance:", "") + "§8|";
            		}catch (Exception e) {
            			filterTypes +="§7distance:§c" + argument.replace("distance:", "") + "§8|";
            		}
        		}
        	}
        }
        
        if(!filterTypes.isEmpty() && remove) {
        	AtomicInteger count = new AtomicInteger(0);
        	objectList.stream().filter(filterPredicate).forEach(entry -> {
    			if(SQLAction.REMOVE != entry.getSQLAction()) {
    				entry.setSQLAction(SQLAction.REMOVE);
        			entry.remove(false);
        			count.incrementAndGet();
    			}
    		});
        	
        	getLHandler().sendMessage(sender, "command.remove.result",
        			new StringTranslator("filters", StringUtils.removeEnd(filterTypes, "|")),
					new StringTranslator("size", count.get() + ""));
        }else if(!remove){
        	getLHandler().sendMessage(sender, "command.remove.result",
        			new StringTranslator("filters", StringUtils.removeEnd(filterTypes, "|")));
        }
	}

    private ObjectID getFromSight(Location l) {
        if (FurnitureLib.getInstance().getFurnitureManager().getObjectList().isEmpty()) {
            return null;
        }
        int i = 10;
        final BlockFace face = LocationUtil.yawToFace(l.getYaw());
        for (int j = 0; j <= i; j++) {
            Location loc = FurnitureLib.getInstance().getLocationUtil().getRelative(l, face, j, 0D);
            if (loc.getBlock() != null && loc.getBlock().getType() != Material.AIR) {
                return null;
            }
            for (ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()) {
                for (fEntity packet : obj.getPacketList()) {
                    if (packet.getLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
                        double d = packet.getLocation().toVector().distanceSquared(loc.toVector());
                        if (d <= 2.0) {
                            return packet.getObjID();
                        }
                    }
                }
            }
        }
        return null;
    }
}
