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
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class listCommand extends iCommand {
	
    public listCommand(String subCommand, String... args) {
        super(subCommand);
        setTab("type/world/plugin/models/distance");
    }

    public void run(CommandSender sender, String[] args) {
    	if(Player.class.isInstance(sender)) {
    		Player player = Player.class.cast(sender);
    		//"furniture list player:xy distance:10"
    		List<String> argList = Arrays.asList(args);
    		List<Project> projects = FurnitureLib.getInstance().getFurnitureManager().getProjects();
    		
    		
    		
    	}
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        List<ComponentBuilder> objList = new ArrayList<>();
        List<String> strList = new ArrayList<>();
        HashMap<String, String> proList = new HashMap<>();
        for (Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()) {
            strList.add(pro.getName());
            String name = "";
            if (pro.getCraftingFile().getRecipe().getResult() != null) {
                if (pro.getCraftingFile().getRecipe().getResult().hasItemMeta()) {
                    if (pro.getCraftingFile().getRecipe().getResult().getItemMeta().hasDisplayName()) {
                        name = ChatColor.stripColor(pro.getCraftingFile().getRecipe().getResult().getItemMeta().getDisplayName());
                    }
                }
            }
            proList.put(pro.getName(), name);
        }
        
        SortedSet<String> keys = new TreeSet<String>(proList.keySet());
        SortedSet<String> values = new TreeSet<String>(proList.values());


        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;
        if (args.length == 1) {
            if (!hasCommandPermission(sender)) return;
            boolean recipe = false, give = false, detail = true;
            if (FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.command.recipe")) {
                recipe = true;
            }
            if (FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.command.give")) {
                give = true;
            }
            if (FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.command.debug")) {
                detail = false;
            }
            
            for (String str : getProjects(keys, values, proList, give)) {
                String s = "";
                Project pro = FurnitureLib.getInstance().getFurnitureManager().getProject(str);
                String name = pro.getName();
                if (detail) {
                    List<ObjectID> objectList = getByType(pro);
                    s = "§eObjects: §c" + objectList.size();
                    s += "\n§eSystemID: §c" + pro.getName();
                }

                if (pro.getCraftingFile().getRecipe().getResult() != null) {
                    if (pro.getCraftingFile().getRecipe().getResult().hasItemMeta()) {
                        if (pro.getCraftingFile().getRecipe().getResult().getItemMeta().hasDisplayName()) {
                            name = ChatColor.stripColor(pro.getCraftingFile().getRecipe().getResult().getItemMeta().getDisplayName());
                        }
                    }
                }

                if (give) {
                    objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/furniture give " + pro.getName())));
                } else if (recipe) {
                    objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/furniture recipe " + pro.getName())));
                } else {
                    objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())));
                }
            }
            new objectToSide(objList, p, 1, "/furniture list");
        } else if (args.length == 2) {
            String subcommand = "";
            if (args[1].equalsIgnoreCase("Type")) {
                if (!hasCommandPermission(sender, ".type")) return;
                for (Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()) {
                    List<ObjectID> objectList = getByType(pro);
                    objList.add(new ComponentBuilder("§6- " + pro.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));
                }
                subcommand = " type";
            } else if (args[1].equalsIgnoreCase("World")) {
                if (!hasCommandPermission(sender, ".world")) return;
                for (World w : Bukkit.getWorlds()) {
                    List<ObjectID> objectList = getByWorld(w);
                    objList.add(new ComponentBuilder("§6- " + w.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));
                }
                subcommand = " world";
            } else if (args[1].equalsIgnoreCase("Plugin")) {
                if (!hasCommandPermission(sender, ".plugin")) return;
                List<String> plugins = new ArrayList<String>();
                for (Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()) {
                    String plugin = pro.getPlugin().getName();
                    if (!plugins.contains(plugin)) {
                        objList.add(new ComponentBuilder("§c" + plugin));
                        for (Project project : getByPlugin(plugin)) {
                            objList.add(new ComponentBuilder("§7- " + project.getName()));
                        }
                        plugins.add(plugin);
                    }
                }
                subcommand = " plugin";
            } else if (args[1].equalsIgnoreCase("models")) {
                if (!hasCommandPermission(sender, ".models")) return;
                for (Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()) {
                    if (pro.isEditorProject()) {
                        List<ObjectID> objectList = getByModel(pro);
                        objList.add(new ComponentBuilder("§6- " + pro.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));
                    }
                }
                subcommand = " models";
            } else if (FurnitureLib.getInstance().isInt(args[1])) {
                if (!hasCommandPermission(sender)) return;
                if (!(sender instanceof Player)) return;
                boolean recipe = false, give = false, detail = true;
                if (FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.command.recipe")) {
                    recipe = true;
                }
                if (FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.command.give")) {
                    give = true;
                }
                if (FurnitureLib.getInstance().getPermission().hasPerm(sender, "furniture.command.debug")) {
                    detail = false;
                }

                for (String str : getProjects(keys, values, proList, give)) {
                    String s = "";
                    Project pro = FurnitureLib.getInstance().getFurnitureManager().getProject(str);
                    String name = pro.getName();
                    if (detail) {
                        List<ObjectID> objectList = getByType(pro);
                        s = "§eObjects: §c" + objectList.size();
                        s += "\n§eSystemID: §c" + pro.getName();
                    }

                    if (pro.getCraftingFile().getRecipe().getResult() != null) {
                        if (pro.getCraftingFile().getRecipe().getResult().hasItemMeta()) {
                            if (pro.getCraftingFile().getRecipe().getResult().getItemMeta().hasDisplayName()) {
                                name = ChatColor.stripColor(pro.getCraftingFile().getRecipe().getResult().getItemMeta().getDisplayName());
                            }
                        }
                    }

                    if (give) {
                        objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/furniture give " + pro.getName())));
                    } else if (recipe) {
                        objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/furniture recipe " + pro.getName())));
                    } else {
                        objList.add(new ComponentBuilder("§6- " + name).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(s).create())));
                    }
                }
                new objectToSide(objList, p, Integer.parseInt(args[1]), "/furniture list");
                return;
            } else {
                command.sendHelp(p);
                return;
            }
            new objectToSide(objList, p, 1, "/furniture list " + subcommand);
        } else if (args.length == 3) {
            String subcommand = "";
            if (args[1].equalsIgnoreCase("Type")) {
                if (!hasCommandPermission(sender, ".type")) return;
                for (Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()) {
                    List<ObjectID> objectList = getByType(pro);
                    objList.add(new ComponentBuilder("§6- " + pro.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));
                }
                subcommand = " type";
            } else if (args[1].equalsIgnoreCase("World")) {
                if (!hasCommandPermission(sender, ".world")) return;
                for (World w : Bukkit.getWorlds()) {
                    List<ObjectID> objectList = getByWorld(w);
                    objList.add(new ComponentBuilder("§6- " + w.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));
                }
                subcommand = " world";
            } else if (args[1].equalsIgnoreCase("models")) {
                if (!hasCommandPermission(sender, ".models")) return;
                for (Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()) {
                    if (pro.isEditorProject()) {
                        List<ObjectID> objectList = getByModel(pro);
                        objList.add(new ComponentBuilder("§6- " + pro.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eObjecte: §c" + objectList.size()).create())));
                    }
                }
                subcommand = " models";
            } else if (args[1].equalsIgnoreCase("Plugin")) {
                if (!hasCommandPermission(sender, ".plugin")) return;
                if (sender instanceof Player == false) return;
                List<String> plugins = new ArrayList<String>();
                for (Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()) {
                    String plugin = pro.getPlugin().getName();
                    if (!plugins.contains(plugin)) {
                        objList.add(new ComponentBuilder("§c" + plugin));
                        for (Project project : getByPlugin(plugin)) {
                            objList.add(new ComponentBuilder("§7- " + project.getName()));
                        }
                        plugins.add(plugin);
                    }
                }
                subcommand = " plugin";
            }
            new objectToSide(objList, p, Integer.parseInt(args[2]), "/furniture list " + subcommand);
        } else {
            command.sendHelp(p);
        }
    }

    private SortedSet<String> getProjects(SortedSet<String> key, SortedSet<String> values, HashMap<String, String> hash, boolean detail) {
        SortedSet<String> proList = new TreeSet<>();
        //return admin SystemID sort
        if (!detail) {
            for (String str : values) {
                for (String k : hash.keySet()) {
                    String v = hash.get(k);
                    if (v.equalsIgnoreCase(str)) proList.add(k);
                }
            }
            return key;
        }
        return key;
    }

    private List<Project> getByPlugin(String plugin) {
        return FurnitureManager.getInstance().getProjects().stream().filter(project -> project.getPlugin().getName().equalsIgnoreCase(plugin)).collect(Collectors.toList());
    }

    private List<ObjectID> getByWorld(World w) {
        String name = w.getName();
        return FurnitureManager.getInstance().getObjectList().stream().filter(obj -> obj.getWorldName().equalsIgnoreCase(name)).collect(Collectors.toList());
    }

    private List<ObjectID> getByType(Project pro) {
        return pro.getObjects();
    }

    private List<ObjectID> getByModel(Project pro) {
    	if(!pro.isEditorProject()) return null;
    	return pro.getObjects();
    }

}
