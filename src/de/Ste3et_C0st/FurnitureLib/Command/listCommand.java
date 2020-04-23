package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class listCommand extends iCommand {

	private int itemsEachSide = 10;
	
	public listCommand(String subCommand, String... args) {
		super(subCommand);
		setTab("world:/player:/distance:/plugin:", "world:/player:/distance:/plugin:");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!hasCommandPermission(sender))
			return;
		Stream<ObjectID> objectList = FurnitureManager.getInstance().getObjectList().stream().filter(Objects::nonNull).filter(entry -> SQLAction.REMOVE != entry.getSQLAction());
		AtomicInteger side = new AtomicInteger(0);
		boolean filter = false, shouldClose = false;
		String arguments = String.join(" ", args);
		String filterTypes = "";

		for (String argument : args) {
			if (shouldClose)
				break;
			argument = argument.toLowerCase();
			if (argument.startsWith("plugin:") && !filterTypes.contains("plugin")) {
				if (!hasCommandPermission(sender, ".plugin")) {
					shouldClose = true;
					break;
				}
				String plugin = argument.replace("plugin:", "");
				objectList = objectList.filter(entry -> entry.getPlugin().equalsIgnoreCase(plugin));
				filter = true;
				filterTypes += "§7plugin:§a" + plugin + "§8|";
			} else if (argument.startsWith("world:") && !filterTypes.contains("world")) {
				if (!hasCommandPermission(sender, ".world")) {
					shouldClose = true;
					break;
				}
				String world = argument.replace("world:", "");
				objectList = objectList.filter(entry -> entry.getWorldName().equalsIgnoreCase(world));
				filter = true;
				filterTypes += "§7world:§a" + world + "§8|";
				continue;
			} else if (argument.startsWith("player:") && !filterTypes.contains("player")) {
				if (!hasCommandPermission(sender, ".player")) {
					shouldClose = true;
					break;
				}
				OfflinePlayer player = Bukkit.getOfflinePlayer(argument.replace("player:", ""));
				if (Objects.nonNull(player)) {
					objectList = objectList.filter(entry -> entry.getUUID().equals(player.getUniqueId()));
					filter = true;
					filterTypes += "§7player:§a" + player.getName() + "§8|";
				}
				continue;
			} else if (argument.startsWith("distance:") && !filterTypes.contains("distance")) {
				if (!hasCommandPermission(sender, ".distance")) {
					shouldClose = true;
					break;
				}
				if (Player.class.isInstance(sender)) {
					Player player = Player.class.cast(sender);
					AtomicInteger distance = new AtomicInteger(0);
					try {
						distance.set(Integer.parseInt(argument.replace("distance:", "")));
						World world = player.getWorld();
						objectList = objectList.filter(entry -> entry.getWorldName().equalsIgnoreCase(world.getName()))
								.filter(entry -> entry.getStartLocation().distance(player.getLocation()) < distance
										.get());
						filter = true;
						filterTypes += "§7distance:§a" + distance.get() + "§8|";
					} catch (Exception e) {
					}
				}
				continue;
			} else {
				if (!hasCommandPermission(sender)) {
					shouldClose = true;
					break;
				}
				try {
					side.set(Integer.parseInt(argument));
					arguments = arguments.replace(argument, "");
				} catch (Exception e) {
				}
			}
		}
		if (shouldClose) {
			objectList.close();
			return;
		} else if (filter) {
			HashMap<String, AtomicInteger> projectCounter = new HashMap<String, AtomicInteger>();
			FurnitureManager.getInstance().getProjects()
					.forEach(entry -> projectCounter.put(entry.getName(), new AtomicInteger(0)));
			objectList.forEach(entry -> {
				AtomicInteger integer = projectCounter.get(entry.getProject());
				if (Objects.nonNull(integer)) {
					integer.incrementAndGet();
				}
			});
			long items = projectCounter.entrySet().stream().parallel().filter(entry -> entry.getValue().get() > 0)
					.count();

			if (items > 0) {
				List<ComponentBuilder> componentList = new ArrayList<ComponentBuilder>();
				String filters = filterTypes.substring(0, filterTypes.length() - 1);
				componentList.add(new ComponentBuilder("§7FilterTypes: [" + filters + "§7]"));
				projectCounter.entrySet().stream()
						.sorted((k1, k2) -> Integer.compare(k1.getValue().get(), k2.getValue().get()))
						.filter(entry -> entry.getValue().get() > 0).forEach(entry -> {
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
				new objectToSide(componentList, (Player) sender, side.get(), "/furniture " + arguments, itemsEachSide + 1);
			}
		} else {
			List<ComponentBuilder> componentList = new ArrayList<ComponentBuilder>();
			FurnitureManager.getInstance().getProjects().stream().sorted((k1, k2) -> ChatColor
					.stripColor(k1.getDisplayName()).compareTo(ChatColor.stripColor(k2.getDisplayName())))
					.forEach(entry -> {
						ComponentBuilder builder = new ComponentBuilder(
								" §8- §7" + ChatColor.stripColor(entry.getDisplayName()));
						if (sender.hasPermission("furniture.command.debug")) {
							ComponentBuilder debugInfos = new ComponentBuilder(
									"§7Placed Objects: §e" + entry.getObjects().size() + "\n");
							debugInfos.append("§7SystemID: §e" + entry.getName() + "\n");
							debugInfos.append("§7Size: §e" + entry.getLength() + " §7|§e " + entry.getHeight()
									+ " §7|§e " + entry.getWidth() + "\n");
							debugInfos
									.append("§7Entities: §e" + entry.getModelschematic().getEntityMap().size() + "\n");
							debugInfos
									.append("§7Blockcount: §e" + entry.getModelschematic().getBlockMap().size() + "\n");
							debugInfos.append("§7Plugin: §e" + entry.getPlugin().getName());
							builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, debugInfos.create()));
						}
						if (sender.hasPermission("furniture.command.give")) {
							ComponentBuilder give = new ComponentBuilder(" §7[§2give§7]");
							give.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/furniture give " + entry.getName()));
							builder.reset().append(give.create());
						}
						
						if (sender.hasPermission("furniture.command.recipe") && sender.hasPermission("furniture.command.recipe." + entry.getName().toLowerCase())) {
							ComponentBuilder give = new ComponentBuilder(" §7[§erecipe§7]");
							give.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/furniture recipe " + entry.getName()));
							builder.reset().append(give.create());
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
			if (!componentList.isEmpty()) {
				new objectToSide(componentList, (Player) sender, side.get(), "/furniture " + arguments, itemsEachSide);
			}
		}
	}
}
