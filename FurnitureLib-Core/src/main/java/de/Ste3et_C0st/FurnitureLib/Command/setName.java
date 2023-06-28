package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.Optional;

import org.bukkit.command.CommandSender;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import net.md_5.bungee.api.ChatColor;

public class setName extends iCommand {

	public setName(String subCommand, String... args) {
        super(subCommand);
        setTab("installedModels");
    }
	
    @Override
    public void execute(CommandSender sender, String[] args) {
    	if (!hasCommandPermission(sender)) return;
    	if (args.length > 1) {
    		final Optional<Project> projectOpt = getProjectOptional(args[1]);
    		if(projectOpt.isPresent()) {
    			final Project project = projectOpt.get();
    			if(args.length > 2) {
    				final String name = ChatColor.translateAlternateColorCodes('&', args[2]);
    				project.getCraftingFile().setName(name);
    				getLHandler().sendMessage(sender, "command.setname.success", new StringTranslator("model", project.getName()), new StringTranslator("name", ChatColor.stripColor(name)));
    				return;
    			}
    		}else {
    			getLHandler().sendMessage(sender, "message.ProjectNotFound", new StringTranslator("project", args[1]));
    		}
    	}else {
    		getLHandler().sendMessage(sender, "message.WrongArgument");
    	}
    }
    
    private Optional<Project> getProjectOptional(String project) {
    	return FurnitureLib.getInstance().getFurnitureManager().getProjects().stream().filter(entry -> entry.getName().equalsIgnoreCase(project)).findFirst();
    }

}
