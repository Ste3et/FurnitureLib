package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.Utilitis.cache.DiceOfflinePlayer;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class listCommand extends iCommand {

	private int itemsEachSide = 15;
	private static final Function<String, Predicate<ObjectID>> PLUGIN = (string) -> objectID -> objectID.getPlugin().equalsIgnoreCase(string);
	private static final Function<String, Predicate<ObjectID>> WORLD = (string) -> objectID -> objectID.getWorld().getName().equalsIgnoreCase(string);
	private static final Function<UUID, Predicate<ObjectID>> PLAYER = (string) -> objectID -> objectID.getUUID().equals(string);
	private static final Function<String, Optional<String[]>> STRINGS_PLITTER = (string) -> Optional.ofNullable(string.contains(":") ? string.toLowerCase().split(":") : null);
	private static final BiFunction<Location, Integer, Predicate<ObjectID>> DISTANCE = (location, distance) -> objectID -> objectID.getWorldName().equalsIgnoreCase(location.getWorld().getName()) && location.distance(objectID.getStartLocation()) < distance;
	private static final String filters = "world:/player:/distance:/plugin:";
	
	public listCommand(String subCommand, String... args) {
		super(subCommand);
		setTab(filters, filters);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!hasCommandPermission(sender)) return;
		final List<ObjectID> objectList = FurnitureManager.getInstance().getAllExistObjectIDs().collect(Collectors.toList());
		final HashMap<String, Predicate<ObjectID>> predicateHashMap = new HashMap<>();
		final AtomicInteger side = new AtomicInteger(0);
		final StringBuilder argumentBuilder = new StringBuilder();
		
		Arrays.asList(args).stream().map(String::toLowerCase)
		.filter(argument -> predicateHashMap.containsKey(argument) == false)
		.forEach(argument -> {
			STRINGS_PLITTER.apply(argument).ifPresent(returnArray -> {
				if(returnArray.length == 2) {
					if(filters.contains(returnArray[0])) {
						final String actualString = returnArray[0];
						if (hasCommandPermission(sender, actualString)) {
							final Predicate<ObjectID> selector;
							final boolean negate = returnArray[1].startsWith("!");
							final String plainString = returnArray[1].replaceFirst("!", "");
							
							switch(returnArray[0]) {
								case "plugin": selector = negate ? PLUGIN.apply(plainString).negate() : PLUGIN.apply(plainString);
									break;
								case "world": selector = negate ? WORLD.apply(plainString).negate() : WORLD.apply(plainString);
									break;
								case "player":
									final Optional<DiceOfflinePlayer> offlinePlayer = FurnitureLib.getInstance().getPlayerCache().getPlayer(plainString);
									final UUID uuid = offlinePlayer.isPresent() ? offlinePlayer.get().getUuid() : null;
									if(Objects.nonNull(uuid)) {
										selector = negate ? PLAYER.apply(uuid).negate() : PLAYER.apply(uuid);
									}else {
										selector = null;
									}
									break;
								case "distance":
									if (Player.class.isInstance(sender)) {
										final Integer distance = Integer.parseInt(plainString);
										final Location location = Player.class.cast(sender).getLocation();
										selector = DISTANCE.apply(location, distance);
									}else {
										selector = null;
									}
									break;
								default: selector = null; break;
							}
							
							if(Objects.nonNull(selector)) {
								predicateHashMap.put(actualString, selector);
								argumentBuilder.append(argument);
							}
						}
					}
				}
			});
			
			if(argument.matches("-?(0|[1-9]\\d*)")) {
				int selectedSide = Integer.parseInt(argument);
				selectedSide = selectedSide > 0 ? selectedSide : 1;
				side.set(selectedSide);
			}
		});
		
		List<Component> componentList = new ArrayList<Component>();
		double maxPages = 0;
		if (!predicateHashMap.isEmpty()) {
			final HashMap<String, AtomicInteger> projectCounter = new HashMap<String, AtomicInteger>();
			final Predicate<ObjectID> predicate = predicateHashMap.values().stream().reduce(Predicate::and).orElse(Objects::nonNull);
			final String filters = String.join(",", predicateHashMap.keySet().stream().collect(Collectors.toList()));
			final AtomicBoolean items = new AtomicBoolean(false);
			
			FurnitureManager.getInstance().getProjects().forEach(entry -> projectCounter.put(entry.getName(), new AtomicInteger(0)));
			
			objectList.stream().filter(predicate).forEach(entry -> {
				AtomicInteger integer = projectCounter.get(entry.getProject());
				if (Objects.nonNull(integer)) {
					integer.incrementAndGet();
					items.set(true);
				}
			});
			
			if (items.get()) {
				componentList.add(MiniMessage.miniMessage().deserialize("<gray>FilterTypes: [" + filters +"]"));
				final int currentSide = (side.get() > 0 ? side.get() : 1) - 1;
				projectCounter.entrySet().stream()
						.sorted((k1, k2) -> Integer.compare(k1.getValue().get(), k2.getValue().get()))
						.filter(entry -> entry.getValue().get() > 0)
						.skip(itemsEachSide * currentSide)
						.limit(itemsEachSide)
						.forEach(entry -> {
							final String name = entry.getKey();
							Component component = MiniMessage.miniMessage().deserialize("<dark_gray>- <yellow>" + name + " <gray>Models: <yellow>" + entry.getValue().get());
							if (sender.hasPermission("furniture.command.remove.project")) {
								component = component.append(getLHandler().getComponent("command.list.remove.button"))
										.hoverEvent(HoverEvent.showText(getLHandler().getComponent("command.list.remove.hover",
											new StringTranslator("project", entry.getKey()),
											new StringTranslator("filters", filters))
										)).clickEvent(ClickEvent.suggestCommand("/furniture remove " + ChatColor.stripColor(filters.replace("|", " ") + " project:" + name)));
							}
							componentList.add(component);
						});
				double count = projectCounter.values().stream().filter(entry -> entry.get() > 0).count();
				maxPages = Math.ceil(count / ((double) itemsEachSide));
			}else {
				getLHandler().sendMessage(sender, "command.list.nothing", new StringTranslator("filters", StringUtils.removeEnd(filters, "|")));
    			return;
			}
		} else {
			FurnitureManager.getInstance().getProjects().stream().sorted((k1, k2) -> k1.getDisplayName().compareTo(k2.getDisplayName()))
					.skip(itemsEachSide * (side.get() - 1))
					.limit(itemsEachSide)
					.forEach(entry -> {
						Component component = getLHandler().getComponent("command.list.main.message", new StringTranslator("project", entry.getDisplayName()));
						
						if (sender.hasPermission("furniture.command.debug")) {
							final Component hoverComponent = getLHandler().getComponent("command.list.main.debug_hover", 
									new StringTranslator("amount", entry.getObjectSize() + ""),
									new StringTranslator("project", entry.getName()),
									new StringTranslator("size", entry.getLength() + " §7|§e " + entry.getHeight() + " §7|§e " + entry.getWidth()),
									new StringTranslator("entities", entry.getModelschematic().getEntityMap().size() + ""),
									new StringTranslator("block_count", entry.getModelschematic().getBlockMap().size() + ""),
									new StringTranslator("plugin", entry.getPlugin().getName()),
									new StringTranslator("destroyable", entry.isDestroyable() + "")
							);
							component = component.hoverEvent(HoverEvent.showText(hoverComponent));
						}
						
						if (sender.hasPermission("furniture.command.give")) {
							final Component giveComponent = getLHandler().getComponent("command.list.give.button").clickEvent(ClickEvent.runCommand("/furniture give " + entry.getName())).hoverEvent(HoverEvent.showText(Component.empty()));
							component = component.append(giveComponent);
						}
						
						if (sender.hasPermission("furniture.command.recipe") && sender.hasPermission("furniture.command.recipe." + entry.getName().toLowerCase())) {
							final Component giveComponent = getLHandler().getComponent("command.list.recipe.button").clickEvent(ClickEvent.runCommand("/furniture recipe " + entry.getName())).hoverEvent(HoverEvent.showText(Component.empty()));
							component = component.append(giveComponent);
						}
						
						if (sender.hasPermission("furniture.command.remove.project")) {
							Component removeComponent = getLHandler().getComponent("command.list.remove.button");
							removeComponent = removeComponent.hoverEvent(HoverEvent.showText(getLHandler().getComponent("command.list.remove.hover", new StringTranslator("project", entry.getName()))));
							removeComponent = removeComponent.clickEvent(ClickEvent.runCommand("/furniture remove project:" + entry.getName()));
							component = component.append(removeComponent);
						}
						if (sender.hasPermission("furniture.command.delete.project")) {
							Component deleteComponent = getLHandler().getComponent("command.list.delete.button");
							deleteComponent = deleteComponent.hoverEvent(HoverEvent.showText(getLHandler().getComponent("command.list.delete.hover", new StringTranslator("project", entry.getName()))));
							deleteComponent = deleteComponent.clickEvent(ClickEvent.runCommand("/furniture delete " + entry.getName()));
							component = component.append(deleteComponent);
						}
						
						componentList.add(component);
					});
			double counts = FurnitureManager.getInstance().getProjects().size();
			double items = itemsEachSide;
			maxPages = Math.ceil(counts / items);
			argumentBuilder.append("list ");
		}
		
		if (!componentList.isEmpty()) {
			new objectToSide(componentList, sender, side.get(), "/furniture " + argumentBuilder.toString(), itemsEachSide, (int) maxPages);
		}else {
			LanguageManager.send(sender, "message.SideNotFound");
			LanguageManager.send(sender, "message.SideNavigation", new StringTranslator("max", (int) maxPages + ""));
            return;
		}
	}
}
