package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.Objects;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class setItemCommand extends iCommand {

    public setItemCommand(String subCommand, String... args) {
        super(subCommand);
    }

	@Override
	public void execute(CommandSender sender, String[] args) {
        if (!hasCommandPermission(sender)) {
            return;
        }
        if(Player.class.isInstance(sender)) {
        	final Player player = Player.class.cast(sender);
        	final String query = args.length > 1 ? args[1] : "";
        	final Project project = FurnitureLib.getInstance().getFurnitureManager().getProject(query);
        	if(query.isEmpty() || Objects.isNull(project)) {
        		getLHandler().sendMessage(sender, "message.ProjectNotFound", new StringTranslator("project", args[1]));
                return;
        	}
        	
        }
	}
	
}
