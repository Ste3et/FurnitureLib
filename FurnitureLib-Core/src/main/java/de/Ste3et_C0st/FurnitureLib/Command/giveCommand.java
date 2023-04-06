package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class giveCommand extends iCommand {

    public giveCommand(String subCommand, String... args) {
        super(subCommand);
        setTab("installedModels", "players");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!hasCommandPermission(sender)) {
            return;
        }
        Project pro = null;
        if (args.length > 1) {
            pro = getProject(args[1]);
            if (pro == null) {
                getLHandler().sendMessage(sender, "message.ProjectNotFound", new StringTranslator("project", args[1]));
                return;
            }
        } else {
            getLHandler().sendMessage(sender, "message.WrongArgument");
            return;
        }
        if (args.length == 2) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.getInventory().addItem(pro.getCraftingFile().getItemstack());
                player.updateInventory();
                return;
            } else {
                getLHandler().sendMessage(sender, "message.WrongArgument");
                return;
            }
        } else if (args.length == 3) {
            if (FurnitureLib.getInstance().isInt(args[2])) {
                if (sender instanceof Player) {
                    int i = Integer.parseInt(args[2]);
                    if (i == 0) i = 1;
                    Player player = (Player) sender;
                    ItemStack is = pro.getCraftingFile().getItemstack().clone();
                    is.setAmount(i);
                    player.getInventory().addItem(is);
                    player.updateInventory();
                    return;
                } else {
                    getLHandler().sendMessage(sender, "message.WrongArgument");
                    return;
                }
            } else if (Bukkit.getPlayer(args[2]) != null) {
                if (!hasCommandPermission(sender, ".other")) {
                    return;
                }
                Player p2 = Bukkit.getPlayer(args[2]);
                if (p2 != null && p2.isOnline()) {
                    p2.getInventory().addItem(pro.getCraftingFile().getItemstack());
                    p2.updateInventory();
                    if (sender.equals(p2)) {
                        return;
                    }

                    getLHandler().sendMessage(sender, "message.GivePlayer", 
                    		new StringTranslator("project", pro.getName()), 
                    		new StringTranslator("player", p2.getName()), 
                    		new StringTranslator("amount", 1 + "")
                    );
                    
                    return;
                } else {
                    getLHandler().sendMessage(sender, "message.PlayerNotOnline",
                    		new StringTranslator("player", args[2])
                    );
                    return;
                }
            } else {
            	getLHandler().sendMessage(sender, "message.PlayerNotOnline",
                		new StringTranslator("player", args[2])
                );
                return;
            }
        } else if (args.length == 4) {
            if (Bukkit.getPlayer(args[2]) != null) {
                if (!hasCommandPermission(sender, ".other")) {
                    return;
                }
                Player p2 = Bukkit.getPlayer(args[2]);
                if(Objects.isNull(p2)) {
                	getLHandler().sendMessage(sender, "message.PlayerNotOnline",
                    		new StringTranslator("player", args[2])
                    );
                    return;
                }
                if (p2.isOnline()) {
                    if (FurnitureLib.getInstance().isInt(args[3])) {
                        int i = Integer.parseInt(args[3]);
                        if (i == 0) i = 1;
                        ItemStack is = pro.getCraftingFile().getItemstack();
                        is.setAmount(i);
                        p2.getInventory().addItem(is);
                        p2.updateInventory();
                        if (sender.equals(p2)) {
                            return;
                        }
                        getLHandler().sendMessage(sender, "message.GivePlayer", 
                        		new StringTranslator("project", pro.getName()), 
                        		new StringTranslator("player", p2.getName()), 
                        		new StringTranslator("amount", i + "")
                        );
                        return;
                    } else {
                    	getLHandler().sendMessage(sender, "message.WrongArgument");
                        return;
                    }
                } else {
                    getLHandler().sendMessage(sender, "message.PlayerNotOnline", new StringTranslator("player", args[2]));
                    return;
                }
            } else {
            	getLHandler().sendMessage(sender, "message.PlayerNotOnline", new StringTranslator("player", args[2]));
                return;
            }
        } else {
        	getLHandler().sendMessage(sender, "message.WrongArgument");
        	return;
        }
    }

    private Project getProject(String project) {
        for (Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()) {
            if (pro.getName().equalsIgnoreCase(project)) {
                return pro;
            }
        }
        return null;
    }

}
