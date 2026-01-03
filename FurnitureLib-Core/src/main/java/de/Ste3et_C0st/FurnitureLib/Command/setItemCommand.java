package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.Objects;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class setItemCommand extends iCommand {

    public setItemCommand(String subCommand, String... args) {
        super(subCommand);
        setTab("installedModels");
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
        		getLHandler().sendMessage(sender, "message.ProjectNotFound", new StringTranslator("project", query));
                return;
        	}else {
        		final ItemStack stack = player.getInventory().getItemInMainHand().clone();
        		final ItemMeta meta = stack.getItemMeta();
        		if(meta.hasDisplayName() == false) {
        			meta.setDisplayName(project.getDisplayName());
        		}
        		if(meta.getPersistentDataContainer().isEmpty() == false) {
        			meta.getPersistentDataContainer().getKeys().iterator().forEachRemaining(namespace -> {
        				meta.getPersistentDataContainer().remove(namespace);
        			});
        		}
        		meta.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(FurnitureLib.getInstance(), "model"), PersistentDataType.STRING, project.getSystemID());
        		
        		stack.setItemMeta(meta);
        		stack.setAmount(1);
        		project.getCraftingFile().updateResult(project.getDisplayNameComponent(), stack);
        		getLHandler().sendMessage(sender, "command.setitem.success", new StringTranslator("model", project.getName()), new StringTranslator("name", ""));
        	}
        	
        }
	}
	
}
