package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import org.bukkit.command.CommandSender;

public class purgeCommand extends iCommand {

    public purgeCommand(String subCommand, String... args) {
        super(subCommand);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        //furniture purge <days>
        if (!hasCommandPermission(sender)) return;
        int purgeTime;
        if (args.length == 2) {
            if (FurnitureLib.getInstance().isInt(args[1])) {
                purgeTime = Integer.parseInt(args[1]);
            } else {
                getLHandler().sendMessage(sender, "message.WrongArgument");
                return;
            }
        } else {
            getLHandler().sendMessage(sender, "message.WrongArgument");
            return;
        }

        int i = 0;
        for (ObjectID id : FurnitureLib.getInstance().getFurnitureManager().getObjectList()) {
            if (id.getUUID() != null) {
                boolean b = FurnitureLib.getInstance().checkPurge(id, id.getUUID(), purgeTime);
                if (b) i++;
            }
        }

        if (!FurnitureConfig.getFurnitureConfig().isPurgeRemove()) {
            getLHandler().sendMessage(sender, "message.PurgeMarked", new StringTranslator("amount", Integer.toString(i)));
            return;
        }
        getLHandler().sendMessage(sender, "message.RemoveDistance", new StringTranslator("amount", Integer.toString(i)));
        return;
    }
}
