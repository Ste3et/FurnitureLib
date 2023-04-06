package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

public class spawnCommand extends iCommand {

    public spawnCommand(String subCommand, String... args) {
        super(subCommand);
        setTab("x", "y", "z", "yaw", "installedModels");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof BlockCommandSender) {
            if (args.length == 6) {
                BlockCommandSender bs = (BlockCommandSender) sender;
                boolean Yaw = FurnitureLib.getInstance().isInt(args[4]);
                if (FurnitureLib.getInstance().getFurnitureManager().getProject(args[5]) != null) {
                    int x = Integer.parseInt(relative((BlockCommandSender) sender, args[1], 0));
                    int y = Integer.parseInt(relative((BlockCommandSender) sender, args[2], 1));
                    int z = Integer.parseInt(relative((BlockCommandSender) sender, args[3], 2));
                    int yaw = 0;

                    if (Yaw) {
                        yaw = Integer.parseInt(args[4]);
                    }

                    World w = bs.getBlock().getWorld();
                    Location l = new Location(w, x, y, z).getBlock().getLocation();
                    l.setYaw(yaw);
                    Project pro = FurnitureLib.getInstance().getFurnitureManager().getProject(args[5]);
                    ObjectID objectID = FurnitureLib.getInstance().spawn(pro, l);
                    FurnitureManager.getInstance().addObjectID(objectID);
                }
            }
        }
    }

    private String relative(BlockCommandSender sender, String s, int i) {
        Location l = sender.getBlock().getLocation();
        Integer j = 0;
        if (s.startsWith("~")) {
            s = s.replace("~", "");
            if (i == 0) j = (int) l.getX();
            if (i == 1) j = (int) l.getY();
            if (i == 2) j = (int) l.getZ();
            if (s.isEmpty()) return j + "";
            if (s.startsWith("-")) {
                s = s.replace("-", "");
                if (!FurnitureLib.getInstance().isInt(s)) return j + "";
                j -= Integer.parseInt(s);
            } else if (s.startsWith("+")) {
                s = s.replace("+", "");
                if (!FurnitureLib.getInstance().isInt(s)) return j + "";
                j += Integer.parseInt(s);
            }
        } else {
            return s;
        }
        return j + "";
    }

}
