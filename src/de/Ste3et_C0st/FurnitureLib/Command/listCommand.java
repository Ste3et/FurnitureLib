package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.comphenix.protocol.concurrency.AbstractIntervalTree.Entry;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class listCommand extends iCommand {
	
    public listCommand(String subCommand, String... args) {
        super(subCommand);
        setTab("world:/player:/distance:", "world:/player:/distance:");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        Stream<ObjectID> objectList = FurnitureManager.getInstance().getObjectList().stream();
        AtomicInteger side = new AtomicInteger(0);
        boolean filter = false;
        String arguments = String.join(" ", args);
        String filterTypes = "";
        for(String argument : args) {
        	argument = argument.toLowerCase();
        	if(argument.startsWith("world:") && !filterTypes.contains("world")) {
        		String world = argument.replace("world:", "");
        		objectList = objectList.filter(entry -> entry.getWorldName().equalsIgnoreCase(world));
        		filter = true;
        		filterTypes +="§7world:§a" + world + "§8|";
        		continue;
        	}else if(argument.startsWith("player:") && !filterTypes.contains("player")) {
        		OfflinePlayer player = Bukkit.getOfflinePlayer(argument.replace("player:", ""));
        		if(Objects.nonNull(player)) {
        			objectList = objectList.filter(entry -> entry.getUUID().equals(player.getUniqueId()));
        			filter = true;
        			filterTypes +="§7player:§a" + player.getName() + "§8|";
        		}
        		continue;
        	}else if(argument.startsWith("distance:") && !filterTypes.contains("distance")) {
        		if(Player.class.isInstance(sender)) {
        			Player player = Player.class.cast(sender);
        			AtomicInteger distance = new AtomicInteger(0);
            		try {
            			distance.set(Integer.parseInt(argument.replace("distance:", "")));
            			World world = player.getWorld();
                		objectList = objectList.filter(entry -> entry.getWorldName().equalsIgnoreCase(world.getName())).filter(entry -> entry.getStartLocation().distance(player.getLocation()) < distance.get());
                		filter = true;
                		filterTypes +="§7distance:§a" + distance.get() + "§8|";
            		}catch (Exception e) {}
        		}
        		continue;
        	}else {
        		try {
        			side.set(Integer.parseInt(argument));
        			arguments = arguments.replace(argument, "");
        		}catch (Exception e) {}
        	}
        }
        
        if(filter) {
        	HashMap<String, AtomicInteger> projectCounter = new HashMap<String, AtomicInteger>();
        	FurnitureManager.getInstance().getProjects().forEach(entry -> projectCounter.put(entry.getName(), new AtomicInteger(0)));
        	objectList.forEach(entry -> {
        		AtomicInteger integer = projectCounter.get(entry.getProject());
        		if(Objects.nonNull(integer)){
        			integer.incrementAndGet();
        		}
        	});
        	long items = projectCounter.entrySet().stream().parallel().filter(entry -> entry.getValue().get() > 0).count();
        	
        	if(items > 0) {
        		List<ComponentBuilder> componentList = new ArrayList<ComponentBuilder>();
        		componentList.add(new ComponentBuilder("§7FilterTypes: [" + filterTypes.substring(0, filterTypes.length() - 1) + "§7]"));
        		projectCounter.entrySet().stream().sorted((k1,k2) -> Integer.compare(k1.getValue().get(), k2.getValue().get())).filter(entry -> entry.getValue().get() > 0).forEach(entry -> {
        			componentList.add(new ComponentBuilder("§7" + entry.getKey() + ": §e" + entry.getValue().get()));
        		});
        		new objectToSide(componentList, (Player) sender, side.get(), "/furniture list " + arguments, 16);
        	}
        }else {
        	List<ComponentBuilder> componentList = new ArrayList<ComponentBuilder>();
        	FurnitureManager.getInstance().getProjects().forEach(entry -> {
        		ComponentBuilder builder = new ComponentBuilder(" §8- §7" + ChatColor.stripColor(entry.getDisplayName()));
        		if(sender.hasPermission("furniture.command.debug")) {
        			ComponentBuilder debugInfos = new ComponentBuilder("§7Placed Objects: §e" + entry.getObjects().size() + "\n");
        			debugInfos.append("§7SystemID: §e" + entry.getName() + "\n");
        			debugInfos.append("§7Size: §e" + entry.getLength() + " §7|§e " + entry.getHeight() + " §7|§e " + entry.getWidth() + "\n");
        			debugInfos.append("§7Entities: §e" + entry.getModelschematic().getEntityMap().size() + "\n");
        			debugInfos.append("§7Blockcount: §e" + entry.getModelschematic().getBlockMap().size() + "\n");
        			debugInfos.append("§7Plugin: §e"  + entry.getPlugin().getName());
        			builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, debugInfos.create()));
        		}
        		if(sender.hasPermission("furniture.command.give")) {
        			builder.append(" §7[§2give§7]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, null)).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/furniture give " + entry.getName()));
        		}
        		if(sender.hasPermission("furniture.command.recipe")) {
        			builder.append(" §7[§erecipe§7]").event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, null)).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/furniture recipe " + entry.getName()));
        		}
        		componentList.add(builder);
        	});
        	if(!componentList.isEmpty()) {
        		new objectToSide(componentList, (Player) sender, side.get(), "/furniture list " + arguments, 15);
        	}
        }
    }
}
