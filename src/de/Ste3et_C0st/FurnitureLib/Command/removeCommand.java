package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class removeCommand extends iCommand {

    public removeCommand(String subCommand, String... args) {
        super(subCommand);
        String tab = "project:/plugin:/world:/player:/distance:/obj:/lookat/all";
        setTab(tab,tab,tab);
    }

    @SuppressWarnings("deprecation")
	public void execute(CommandSender sender, String[] args) {
    	if (!hasCommandPermission(sender)) return;
    	List<ObjectID> objectList = new ArrayList<ObjectID>(FurnitureManager.getInstance().getObjectList());
		Stream<ObjectID> objectStream = objectList.stream().filter(entry -> SQLAction.REMOVE != entry.getSQLAction());
        String filterTypes = "";
        boolean shouldClose = false;
        if(args.length < 2) {
        	FurnitureLib.getInstance().getLangManager().getString("message.WrongArgument");
        	objectStream.close();
        	return;
        }
        
        for(String argument : args) {
        	if(shouldClose) break;
        	argument = argument.toLowerCase();
        	if(argument.startsWith("plugin:") && !filterTypes.contains("plugin")) {
        		if (!hasCommandPermission(sender, ".plugin")) {
        			shouldClose = true;
        			break;
        		}
        		String objectStr = argument.replace("plugin:", "");
        		filterTypes ="§7object:§a" + objectStr + "§8|";
        		objectStream = objectStream.filter(entry -> entry.getPlugin().equalsIgnoreCase(objectStr));
        	}else if(argument.startsWith("obj:") && !filterTypes.contains("object")) {
        		if (!hasCommandPermission(sender, ".object")) {
        			shouldClose = true;
        			break;
        		}
        		String objectStr = argument.replace("obj:", "");
        		filterTypes ="§7object:§a" + objectStr + "§8|";
        		objectStream = objectStream.filter(entry -> entry.getSerial().equalsIgnoreCase(objectStr));
        		break;
        	}else if(argument.equalsIgnoreCase("all")) {
        		if (!hasCommandPermission(sender, ".all")) {
        			shouldClose = true;
        			break;
        		}
        		filterTypes = "§aall";
        		break;
        	}else if(argument.equalsIgnoreCase("lookat")) {
        		if (!hasCommandPermission(sender, ".lookat")) {
        			shouldClose = true;
        			break;
        		}
        		Player p = (Player) sender;
                ObjectID obj = getFromSight(p.getLocation());
                if (Objects.nonNull(obj)) {
                	filterTypes = "§alookat";
                	objectStream.close();
                	objectStream = Collections.singletonList(obj).stream();
                	break;
                }else {
                	filterTypes = "§clookat";
                	shouldClose = true;
                	break;
                }
        	}else if(argument.startsWith("project:") && !filterTypes.contains("project")){
        		if (!hasCommandPermission(sender, ".project")) {
        			shouldClose = true;
        			break;
        		}
        		String project = argument.replace("project:", "");
        		filterTypes += "§7project:§a" + project + "§8|";
        		objectStream = objectStream.filter(entry -> entry.getProject().equalsIgnoreCase(project));
        	}else if(argument.startsWith("world:") && !filterTypes.contains("world")) {
        		if (!hasCommandPermission(sender, ".world")) {
        			shouldClose = true;
        			break;
        		}
        		String world = argument.replace("world:", "");
        		objectStream = objectStream.filter(entry -> entry.getWorldName().equalsIgnoreCase(world));
        		filterTypes +="§7world:" + world + "§8|";
        	}else if(argument.startsWith("player:") && !filterTypes.contains("player")) {
        		if (!hasCommandPermission(sender, ".player")) {
        			shouldClose = true;
        			break;
        		}
				OfflinePlayer player = Bukkit.getOfflinePlayer(argument.replace("player:", ""));
        		if(Objects.nonNull(player)) {
        			filterTypes +="§7player:§a" + player.getName() + "§8|";
        			objectStream = objectStream.filter(entry -> entry.getUUID().equals(player.getUniqueId()));
        		}else {
        			filterTypes +="§7player:§c" + argument.replace("player:", "") + "§8|";
        			shouldClose = true;
        			break;
        		}
        	}else if(argument.startsWith("distance:") && !filterTypes.contains("distance")) {
        		if (!hasCommandPermission(sender, ".distance")) {
        			shouldClose = true;
        			break;
        		}
        		if(Player.class.isInstance(sender)) {
        			Player player = Player.class.cast(sender);
        			AtomicInteger distance = new AtomicInteger(0);
            		try {
            			distance.set(Integer.parseInt(argument.replace("distance:", "")));
            			World world = player.getWorld();
            			objectStream = objectStream.filter(entry -> entry.getWorldName().equalsIgnoreCase(world.getName())).filter(entry -> entry.getStartLocation().distance(player.getLocation()) < distance.get());
                		filterTypes +="§7distance:§a" + argument.replace("distance:", "") + "§8|";
            		}catch (Exception e) {
            			filterTypes +="§7distance:§c" + argument.replace("distance:", "") + "§8|";
            		}
        		}else {
        			shouldClose = true;
        		}
        	}
        }
        
        if(shouldClose || filterTypes.isEmpty()) {
        	objectStream.close();
        	return;
        }else {
        	AtomicInteger count = new AtomicInteger(0);
        	objectStream.forEach(entry -> {
    			if(SQLAction.REMOVE != entry.getSQLAction()) {
    				entry.setSQLAction(SQLAction.REMOVE);
        			entry.remove(false);
        			count.incrementAndGet();
    			}
    		});
    		if(count.get() > 0) {
    			sender.sendMessage("§7Remove §2" + count.get() + " §7furniture models");
    			sender.sendMessage("§7With applied filters -> {" + StringUtils.removeEnd(filterTypes, "|") + "§7}");
    		}else {
    			sender.sendMessage("§7There are no furniture models are found.");
    			sender.sendMessage("§7With applied filters -> {" + StringUtils.removeEnd(filterTypes, "|") + "§7}");
    		}
        }
	}

    private ObjectID getFromSight(Location l) {
        if (FurnitureLib.getInstance().getFurnitureManager().getObjectList().isEmpty()) {
            return null;
        }
        int i = 10;
        BlockFace face = FurnitureLib.getInstance().getLocationUtil().yawToFace(l.getYaw());
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
