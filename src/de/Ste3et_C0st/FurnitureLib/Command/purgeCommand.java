package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class purgeCommand {

	public purgeCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		//furniture purge <days>
		if(!command.noPermissions(sender, "furniture.purge")) return;
		int purgeTime = FurnitureLib.getInstance().getPurgeTime();
		if(args.length==2){
			if(FurnitureLib.getInstance().isInt(args[1])){
				purgeTime = Integer.parseInt(args[1]);
			}else{
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
				return;
			}
		}else{
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
			return;
		}
		
		int i = 0;
		for(ObjectID id : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(id.getUUID()!=null){
				boolean b = FurnitureLib.getInstance().checkPurge(id, id.getUUID(), purgeTime);
				if(b) i++;
			}
		}
		
		if(!FurnitureLib.getInstance().isPurgeRemove()){
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("PurgeMarked").replace("#AMOUNT#", i + ""));
			return;
		}
		
		sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("RemoveDistance").replace("#AMOUNT#", i + ""));
		return;
	}
}
