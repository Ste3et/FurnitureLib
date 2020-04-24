package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class listCommand extends iCommand {

	private int itemsEachSide = 15;
	
	public listCommand(String subCommand, String... args) {
		super(subCommand);
		setTab("world:/player:/distance:/plugin:", "world:/player:/distance:/plugin:");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!hasCommandPermission(sender))
			return;
		List<ObjectID> objectList = new ArrayList<ObjectID>(FurnitureManager.getInstance().getObjectList());
		AtomicInteger side = new AtomicInteger(0);
		String arguments = String.join(" ", args);
		String filterTypes = "";

		Predicate<ObjectID> filterPredicate = Objects::nonNull;
		filterPredicate = filterPredicate.and(entry -> SQLAction.REMOVE != entry.getSQLAction());
		
		for (String argument : args) {
			if(argument.equalsIgnoreCase("list")) continue;
			argument = argument.toLowerCase();
			if (argument.startsWith("plugin:") && !filterTypes.contains("plugin")) {
				if (!hasCommandPermission(sender, ".plugin")) return;
				String plugin = argument.replace("plugin:", "");
				filterPredicate = filterPredicate.and(entry -> entry.getPlugin().equalsIgnoreCase(plugin));
				filterTypes += "§7plugin:§a" + plugin + "§8|";
			} else if (argument.startsWith("world:") && !filterTypes.contains("world")) {
				if (!hasCommandPermission(sender, ".world")) return;
				String world = argument.replace("world:", "");
				filterPredicate = filterPredicate.and(entry -> entry.getWorldName().equalsIgnoreCase(world));
				filterTypes += "§7world:§a" + world + "§8|";
				continue;
			} else if (argument.startsWith("player:") && !filterTypes.contains("player")) {
				if (!hasCommandPermission(sender, ".player")) return;
				
				OfflinePlayer player = Bukkit.getOfflinePlayer(argument.replace("player:", ""));
				if (Objects.nonNull(player)) {
					sender.sendMessage(player.getName());
					filterPredicate = filterPredicate.and(entry -> entry.getUUID().equals(player.getUniqueId()));
					filterTypes += "§7player:§a" + player.getName() + "§8|";
				}else {
					filterTypes += "§7player:§c" + player.getName() + "§8|";
				}
				continue;
			} else if (argument.startsWith("distance:") && !filterTypes.contains("distance")) {
				if (!hasCommandPermission(sender, ".distance")) return;
				if (Player.class.isInstance(sender)) {
					Player player = Player.class.cast(sender);
					AtomicInteger distance = new AtomicInteger(0);
					try {
						distance.set(Integer.parseInt(argument.replace("distance:", "")));
						World world = player.getWorld();
						String worldName = world.getName();
						Location location = player.getLocation();
						filterPredicate = filterPredicate.and(entry -> entry.getWorldName().equalsIgnoreCase(worldName) && entry.getStartLocation().distance(location) < distance.get());
						filterTypes += "§7distance:§a" + distance.get() + "§8|";
					} catch (Exception e) {
					}
				}
				continue;
			} else {
				if (!hasCommandPermission(sender)) return;
				try {
					side.set(Integer.parseInt(argument));
					arguments = arguments.replace(argument, "");
				} catch (Exception e) {
				}
			}
		}
		
		List<ComponentBuilder> componentList = new ArrayList<ComponentBuilder>();
		double maxPages = 0;
		if (!filterTypes.isEmpty()) {
			HashMap<String, AtomicInteger> projectCounter = new HashMap<String, AtomicInteger>();
			FurnitureManager.getInstance().getProjects()
					.forEach(entry -> projectCounter.put(entry.getName(), new AtomicInteger(0)));
			AtomicBoolean items = new AtomicBoolean(false);
			
			objectList.stream().filter(filterPredicate).forEach(entry -> {
				AtomicInteger integer = projectCounter.get(entry.getProject());
				if (Objects.nonNull(integer)) {
					integer.incrementAndGet();
					items.set(true);
				}
			});
			if (items.get()) {
				String filters = filterTypes.substring(0, filterTypes.length() - 1);
				componentList.add(new ComponentBuilder("§7FilterTypes: [" + filters + "§7]"));
				projectCounter.entrySet().stream()
						.sorted((k1, k2) -> Integer.compare(k1.getValue().get(), k2.getValue().get()))
						.filter(entry -> entry.getValue().get() > 0)
						.skip(itemsEachSide * side.get())
						.limit(itemsEachSide)
						.forEach(entry -> {
							String name = ChatColor.stripColor(entry.getKey());
							ComponentBuilder builder = new ComponentBuilder(
									" §8- §e" + name + " §7Models: §e" + entry.getValue().get());
							if (sender.hasPermission("furniture.command.remove.project")) {
								builder.append(" §7[§cremove§7]")
										.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												new ComponentBuilder("§7Remove all §c" + name + " §7models from\n"
														+ "§7These filters: " + filters + "\n"
														+ "§7that §c§ncan't be make undo").create()))
										.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
												"/furniture remove " + ChatColor.stripColor(filters.replace("|", " "))
														+ " project:" + name));
							}
							componentList.add(builder);
						});
				double count = projectCounter.values().stream().filter(entry -> entry.get() > 0).count();
				maxPages = Math.ceil(count / ((double) itemsEachSide));
			}else {
				sender.sendMessage("§7There are no furniture models are found.");
    			sender.sendMessage("§7With applied filters -> {" + StringUtils.removeEnd(filterTypes, "|") + "§7}");
    			return;
			}
		} else {
			FurnitureManager.getInstance().getProjects().stream().sorted((k1, k2) -> k1.getDisplayName().compareTo(k2.getDisplayName()))
					.skip(itemsEachSide * side.get())
					.limit(itemsEachSide)
					.forEach(entry -> {
						ComponentBuilder builder = new ComponentBuilder(" §8- §7" + ChatColor.stripColor(entry.getDisplayName()));
						
						if (sender.hasPermission("furniture.command.debug")) {
							ComponentBuilder debugInfos = new ComponentBuilder("§7Placed Objects: §e" + entry.getObjectSize() + "\n");
								debugInfos.append("§7SystemID: §e" + entry.getName() + "\n");
								debugInfos.append("§7Size: §e" + entry.getLength() + " §7|§e " + entry.getHeight() + " §7|§e " + entry.getWidth() + "\n");
								debugInfos.append("§7Entities: §e" + entry.getModelschematic().getEntityMap().size() + "\n");
								debugInfos.append("§7Blockcount: §e" + entry.getModelschematic().getBlockMap().size() + "\n");
								debugInfos.append("§7Plugin: §e" + entry.getPlugin().getName());
							builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, debugInfos.create()));
						}
						
						if (sender.hasPermission("furniture.command.give")) {
							ComponentBuilder give = new ComponentBuilder(" §7[§2give§7]");
							give.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/furniture give " + entry.getName()));
							builder.append(give.create(), FormatRetention.FORMATTING);
						}
						
						if (sender.hasPermission("furniture.command.recipe") && sender.hasPermission("furniture.command.recipe." + entry.getName().toLowerCase())) {
							ComponentBuilder give = new ComponentBuilder(" §7[§erecipe§7]");
							give.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/furniture recipe " + entry.getName()));
							builder.append(give.create(), FormatRetention.FORMATTING);
						}
						
						if (sender.hasPermission("furniture.command.remove.project")) {
							ComponentBuilder give = new ComponentBuilder(" §7[§cremove§7]");
							give.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder("§7Remove all §c" + entry.getName()
													+ " §7models from\n" + "§7All worlds that §c§ncan't be make undo")
															.create()))
									.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/furniture remove project:" + entry.getName()));
							builder.append(give.create());
						}
						if (sender.hasPermission("furniture.command.delete.project")) {
							ComponentBuilder give = new ComponentBuilder(" §7[§4✘§7]");
							give.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder("§7Remove all §c" + entry.getName()
													+ " §7models from\n" + "§7All worlds that §c§ncan't be make undo\n"
													+ "§7And §cremove §7the model from your Server or §cdisable §7it.")
															.create()))
									.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/furniture delete " + entry.getName()));
							builder.append(give.create());
						}
						componentList.add(builder);
					});
			double counts = FurnitureManager.getInstance().getProjects().size();
			double items = itemsEachSide;
			maxPages = Math.ceil(counts / items);
		}
		
		if (!componentList.isEmpty()) {
			new objectToSide(componentList, (Player) sender, side.get(), "/furniture " + arguments, itemsEachSide, (int) maxPages);
		}else {
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.SideNotFound"));
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.SideNavigation").replaceAll("#MAX#", maxPages + ""));
            return;
		}
	}
}
