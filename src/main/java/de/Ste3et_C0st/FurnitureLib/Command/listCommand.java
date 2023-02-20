package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.Utilitis.cache.DiceOfflinePlayer;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
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
				if(plugin.startsWith("!")) {
					filterPredicate = filterPredicate.and(entry -> !entry.getPlugin().equalsIgnoreCase(plugin.replaceFirst("!", "")));
				}else {
					filterPredicate = filterPredicate.and(entry -> entry.getPlugin().equalsIgnoreCase(plugin));
				}
				filterTypes += "§7plugin:§a" + plugin + "§8|";
			} else if (argument.startsWith("world:") && !filterTypes.contains("world")) {
				if (!hasCommandPermission(sender, ".world")) return;
				String world = argument.replace("world:", "");
				if(world.startsWith("!")) {
					filterPredicate = filterPredicate.and(entry -> !entry.getWorldName().equalsIgnoreCase(world.replaceFirst("!", "")));
				}else {
					filterPredicate = filterPredicate.and(entry -> entry.getWorldName().equalsIgnoreCase(world));
				}
				filterTypes += "§7world:§a" + world + "§8|";
				continue;
			} else if (argument.startsWith("player:") && !filterTypes.contains("player")) {
				if (!hasCommandPermission(sender, ".player")) return;
				String playerName = argument.replace("player:", "").replaceFirst("!", "");
				Optional<DiceOfflinePlayer> offlinePlayer = FurnitureLib.getInstance().getPlayerCache().getPlayer(playerName);
				if (offlinePlayer.isPresent()) {
					if(argument.startsWith("player:!")) {
						filterPredicate = filterPredicate.and(entry -> !entry.getUUID().equals(offlinePlayer.get().getUuid()));
					}else {
						filterPredicate = filterPredicate.and(entry -> entry.getUUID().equals(offlinePlayer.get().getUuid()));
					}
					filterTypes += "§7player:§a" + offlinePlayer.get().getName() + "§8|";
				}else {
					filterTypes += "§7player:§c" + offlinePlayer.get().getName() + "§8|";
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
					int selectedSide = Integer.parseInt(argument);
					selectedSide = selectedSide > 0 ? selectedSide : 1;
					side.set(selectedSide - 1);
					arguments = arguments.replace(argument, "");
				} catch (Exception e) {
				}
			}
		}
		
		List<BaseComponent[]> componentList = new ArrayList<BaseComponent[]>();
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
				componentList.add(new ComponentBuilder("§7FilterTypes: [" + filters + "§7]").create());
				final int currentSide = (side.get() > 0 ? side.get() : 1) - 1;
				projectCounter.entrySet().stream()
						.sorted((k1, k2) -> Integer.compare(k1.getValue().get(), k2.getValue().get()))
						.filter(entry -> entry.getValue().get() > 0)
						.skip(itemsEachSide * currentSide)
						.limit(itemsEachSide)
						.forEach(entry -> {
							String name = ChatColor.stripColor(entry.getKey());
							ComponentBuilder builder = new ComponentBuilder(
									" §8- §e" + name + " §7Models: §e" + entry.getValue().get());
							if (sender.hasPermission("furniture.command.remove.project")) {
								builder.append(getLHandler().getString("command.list.remove.button"))
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder(
												getLHandler().getString("command.list.remove.hover", 
														new StringTranslator("project", entry.getKey()),
														new StringTranslator("filters", filters))).create())
									  ).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
												"/furniture remove " + ChatColor.stripColor(filters.replace("|", " "))
														+ " project:" + name));
							}
							componentList.add(builder.create());
						});
				double count = projectCounter.values().stream().filter(entry -> entry.get() > 0).count();
				maxPages = Math.ceil(count / ((double) itemsEachSide));
			}else {
				sender.sendMessage(getLHandler().getString("command.list.nothing", new StringTranslator("filters", StringUtils.removeEnd(filterTypes, "|"))));
    			return;
			}
		} else {
			FurnitureManager.getInstance().getProjects().stream().sorted((k1, k2) -> k1.getDisplayName().compareTo(k2.getDisplayName()))
					.skip(itemsEachSide * side.get())
					.limit(itemsEachSide)
					.forEach(entry -> {
						TextComponent builder = new TextComponent(getLHandler().getString("command.list.main.message", new StringTranslator("project", ChatColor.stripColor(entry.getDisplayName()))));
						
						if (sender.hasPermission("furniture.command.debug")) {
							ComponentBuilder devInfos = new ComponentBuilder(
									getLHandler().getString("command.list.main.debug_hover",
											new StringTranslator("amount", entry.getObjectSize() + ""),
											new StringTranslator("project", entry.getName()),
											new StringTranslator("size", entry.getLength() + " §7|§e " + entry.getHeight() + " §7|§e " + entry.getWidth()),
											new StringTranslator("entities", entry.getModelschematic().getEntityMap().size() + ""),
											new StringTranslator("block_count", entry.getModelschematic().getBlockMap().size() + ""),
											new StringTranslator("plugin", entry.getPlugin().getName()),
											new StringTranslator("destroyable", entry.isDestroyable() + "")
									));
							builder.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, devInfos.create()));
						}
						
						if (sender.hasPermission("furniture.command.give")) {
							TextComponent give = new TextComponent(getLHandler().getString("command.list.give.button"));
							give.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/furniture give " + entry.getName()));
							give.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()));
							builder.addExtra(give);
						}
						
						if (sender.hasPermission("furniture.command.recipe") && sender.hasPermission("furniture.command.recipe." + entry.getName().toLowerCase())) {
							TextComponent recipe = new TextComponent(getLHandler().getString("command.list.recipe.button"));
							recipe.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/furniture recipe " + entry.getName()));
							recipe.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()));
							builder.addExtra(recipe);
						}
						
						if (sender.hasPermission("furniture.command.remove.project")) {
							TextComponent remove = new TextComponent(getLHandler().getString("command.list.remove.button"));
							remove.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(getLHandler().getString("command.list.remove.hover", new StringTranslator("project", entry.getName()))).create()));
							remove.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/furniture remove project:" + entry.getName()));
							builder.addExtra(remove);
						}
						if (sender.hasPermission("furniture.command.delete.project")) {
							TextComponent delete = new TextComponent(getLHandler().getString("command.list.delete.button"));
							delete.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(getLHandler().getString("command.list.delete.hover", new StringTranslator("project", entry.getName()))).create()));
							delete.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/furniture delete " + entry.getName()));
							builder.addExtra(delete);
						}
						componentList.add(new BaseComponent[] {builder});
					});
			double counts = FurnitureManager.getInstance().getProjects().size();
			double items = itemsEachSide;
			maxPages = Math.ceil(counts / items);
		}
		
		if (!componentList.isEmpty()) {
			new objectToSide(componentList, sender, side.get(), "/furniture " + arguments, itemsEachSide, (int) maxPages);
		}else {
			LanguageManager.send(sender, "message.SideNotFound");
			LanguageManager.send(sender, "message.SideNavigation", new StringTranslator("max", (int) maxPages + ""));
            return;
		}
	}
}
