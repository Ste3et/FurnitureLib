package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class recipeCommand extends iCommand {

    public recipeCommand(String subCommand, String... args) {
        super(subCommand);
        setTab("installedModels", "edit/remove");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
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

        if (sender == null) return;

        if (args.length == 2) {
            if (sender instanceof Player) {
                if (!hasCommandPermission(sender)) return;
                FurnitureLib.getInstance().getCraftingInv().openCrafting((Player) sender, pro, false);
                return;
            } else {
            	getLHandler().sendMessage(sender, "message.WrongArgument");
                return;
            }
        } else if (args.length == 3) {
            if (!(sender instanceof Player)) {
                if (Bukkit.getPlayer(args[2]) != null) {
                    FurnitureLib.getInstance().getCraftingInv().openCrafting(Bukkit.getPlayer(args[2]), pro, false);
                    return;
                } else {
                    getLHandler().sendMessage(sender, "message.PlayerNotOnline", new StringTranslator("player", args[2]));
                    
                    return;
                }
            } else {
                if (args[2].equalsIgnoreCase("edit")) {
                    if (!hasCommandPermission(sender, ".edit")) return;
                    pro = getProject(args[1]);
                    if (pro == null) {
                        getLHandler().sendMessage(sender, "message.ProjectNotFound", new StringTranslator("project", args[1]));
                        return;
                    }
                    FurnitureLib.getInstance().getCraftingInv().openCrafting((Player) sender, pro, true);
                    return;
                } else if (args[2].equalsIgnoreCase("remove")) {
                    if (!hasCommandPermission(sender, ".remove")) return;
                    pro = getProject(args[1]);
                    if (pro == null) {
                        getLHandler().sendMessage(sender, "message.ProjectNotFound", new StringTranslator("project", args[1]));
                        return;
                    }
                    pro.getCraftingFile().removeCrafting(pro.getCraftingFile().getItemstack());
                    //pro.getCraftingFile().setCraftingDisabled(true);
                    getLHandler().sendMessage(sender, "message.CraftingRemove");
                    return;
                } else {
                    getLHandler().sendMessage(sender, "message.WrongArgument");
                    return;
                }
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
