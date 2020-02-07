package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class removeCommand extends iCommand {

    public removeCommand(String subCommand, String... args) {
        super(subCommand);
        //setTab("project/world/player/lookat/distance/all/obj");
        //all
        setTab("-pro:/-world:/-player:/-distance:/-obj:/lookat");
    }

    public void execute(CommandSender sender, String[] args) {
        if (!hasCommandPermission(sender)) return;
        if (args.length == 2) {
            String argument = args[1].toLowerCase();
            String langID = "";
            List<ObjectID> removeList = new ArrayList<>();
            List<StringTranslator> translators = new ArrayList<>();

            if (argument.startsWith("-pro:")) {
                if (!hasCommandPermission(sender, ".pro")) return;
                String str = argument.replace("-pro:", "");
                Project pro = FurnitureManager.getInstance().getProject(str);
                if (Objects.nonNull(pro))
                    removeList = FurnitureManager.getInstance().getObjectList().stream().filter(obj -> obj.getProject().equalsIgnoreCase(pro.getName())).collect(Collectors.toList());
                langID = "message.RemoveType";
                translators.add(new StringTranslator("#TYPE#", pro.getName()));
            }

            if (argument.startsWith("-world:")) {
                if (!hasCommandPermission(sender, ".world")) return;
                String str = argument.replace("-world", "");
                World w = Bukkit.getWorlds().stream().filter(world -> world.getName().equalsIgnoreCase(str)).findFirst().orElse(null);
                if (Objects.nonNull(w))
                    removeList = FurnitureManager.getInstance().getObjectList().stream().filter(obj -> obj.getWorld().equals(w)).collect(Collectors.toList());
                langID = "message.RemoveWorld";
                translators.add(new StringTranslator("#WORLD#", str));
            }

            if (argument.startsWith("-player:")) {
                if (!hasCommandPermission(sender, ".player")) return;
                String str = argument.replace("-player:", "");
                String player = getByPlayerName(str);
                if (Objects.nonNull(player))
                    removeList = FurnitureManager.getInstance().getObjectList().stream().filter(obj -> obj.getPlayerName().equalsIgnoreCase(str)).collect(Collectors.toList());
                langID = "message.RemovePlayer";
                translators.add(new StringTranslator("#PLAYER#", player));
            }

            if (argument.startsWith("-distance:")) {
                if (!hasCommandPermission(sender, ".distance")) return;
                String str = argument.replace("-distance:", "");
                int distance = Integer.parseInt(str);
                Player p = (Player) sender;
                World w = p.getWorld();
                Location loc = p.getLocation();
                List<ObjectID> worldObjList = FurnitureManager.getInstance().getInWorld(w);
                if (Objects.nonNull(worldObjList) && !worldObjList.isEmpty()) {
                    removeList = worldObjList.stream().filter(obj -> obj.getStartLocation().distance(loc) <= distance).collect(Collectors.toList());
                }
                langID = "message.RemoveDistance";
                translators.add(new StringTranslator("#AMOUNT#", removeList.size() + ""));
            }

            if (argument.startsWith("-obj:")) {
                String str = argument.replace("-obj:", "");
                ObjectID serial = getSerial(str);
                if (Objects.nonNull(serial)) removeList = Collections.singletonList(serial);
                langID = "message.RemoveID";
                translators.add(new StringTranslator("#OBJID#", serial.getID()));
            }

            if (argument.equalsIgnoreCase("lookat")) {
                if (!hasCommandPermission(sender, ".lookat")) return;
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    ObjectID obj = getFromSight(p.getLocation());
                    if (Objects.nonNull(obj)) removeList = Collections.singletonList(obj);
                    translators.add(new StringTranslator("#SERIAL#", obj.getID()));
                }
                langID = "message.RemoveLookat";
            }

            if (argument.equalsIgnoreCase("all")) {
                if (!hasCommandPermission(sender, ".all")) return;
                removeList = new ArrayList<ObjectID>(FurnitureManager.getInstance().getObjectList());
            }

            if (Objects.nonNull(removeList) && !removeList.isEmpty()) {
                int i = removeListObj(removeList);
                sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString(langID, translators.toArray(new StringTranslator[0])));
                removeList.forEach(obj -> {
                    obj.remove(false);
                    obj.setSQLAction(SQLAction.REMOVE);
                });
            }
        }
    }

//	@Override
//	public void execute(CommandSender sender, String[] args) {
//		if(!hasCommandPermission(sender)) return;
//		if(args.length==2){
//			Project pro = getProject(args[1]);
//			World world = getWorld(args[1]);
//			String player = getByPlayerName(args[1]);
//			String plugin = getPlugin(args[1]);
//			ObjectID serial = getSerial(args[1]);
//			
//			if(pro != null){
//				if(!hasCommandPermission(sender, ".project")) return;
//				int i = removeListObj(getObject(pro));
//				String str = FurnitureLib.getInstance().getLangManager().getString("message.RemoveDistance");
//				str = str.replace("#AMOUNT#", i+"");
//				sender.sendMessage(str);
//				return;
//			}
//			
//			if(world != null){
//				if(!hasCommandPermission(sender, ".world")) return;
//				int i = removeListObj(getObject(world));
//				String str = FurnitureLib.getInstance().getLangManager().getString("message.RemoveDistance");
//				str = str.replace("#AMOUNT#", i+"");
//				sender.sendMessage(str);
//				return;
//			}
//			
//			if(player != null){
//				if(!hasCommandPermission(sender, ".player")) return;
//				removeListObj(getObject(player));
//				String str = FurnitureLib.getInstance().getLangManager().getString("message.RemovePlayer");
//				str = str.replace("#PLAYER#", player);
//				sender.sendMessage(str);
//				return;
//			}
//			
//			if(serial != null){
//				if(!hasCommandPermission(sender, ".obj")) return;
//				FurnitureLib.getInstance().getFurnitureManager().remove(serial);
//				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.RemoveID").replaceAll("#OBJID#", serial.getID()));
//				return;
//			}
//			
//			if(plugin != null){
//				if(!hasCommandPermission(sender, ".plugin")) return;
//				removeListObj(getObjectPlugin(plugin));
//				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.RemovePlugin").replaceAll("#PLUGIN#", plugin));
//				return;
//			}
//			
//			if(args[1].equalsIgnoreCase("all")){
//				if(!hasCommandPermission(sender, ".all")) return;
//				removeListObj(FurnitureLib.getInstance().getFurnitureManager().getObjectList());
//				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.RemoveAll"));
//				return;
//			}
//			
//			if(FurnitureLib.getInstance().isInt(args[1])){
//				if(!hasCommandPermission(sender, ".distance")) return;
//				if(sender instanceof Player){
//					int distance = Integer.parseInt(args[1]);
//					Player p = (Player) sender;
//					World w = p.getWorld();
//					HashSet<ObjectID> worldObjList = getObject(w);
//					int i = removeListObj(getObject(p.getLocation(), worldObjList, distance));
//					String s = FurnitureLib.getInstance().getLangManager().getString("message.RemoveDistance");
//					s = s.replace("#AMOUNT#", i+"");
//					p.sendMessage(s);return;
//				}else if(sender instanceof CommandSender){
//					int distance = Integer.parseInt(args[1]);
//					BlockCommandSender commandBlock = (BlockCommandSender) sender;
//					World w = commandBlock.getBlock().getWorld();
//					HashSet<ObjectID> worldObjList = getObject(w);
//					int i = removeListObj(getObject(commandBlock.getBlock().getLocation(), worldObjList, distance));
//					String s = FurnitureLib.getInstance().getLangManager().getString("message.RemoveDistance");
//					s = s.replace("#AMOUNT#", i+"");
//					sender.sendMessage(s);return;
//				}
//				return;
//			}
//			
//			if(args[1].equalsIgnoreCase("lookat")){
//				if(!hasCommandPermission(sender, ".lookat")) return;
//				if(sender instanceof Player){
//					Player p = (Player) sender;
//					ObjectID obj = getFromSight(p.getLocation());
//					if(obj!=null){
//						FurnitureLib.getInstance().getFurnitureManager().remove(obj);
//						p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.RemoveLookat").replaceAll("#SERIAL#", obj.getSerial()));
//					}
//					return;
//				}
//				return;
//			}
//			
//			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.WrongArgument"));
//			return;
//		}
//	}

    private ObjectID getFromSight(Location l) {
        if (FurnitureLib.getInstance().getFurnitureManager().getObjectList().isEmpty()) {
            return null;
        }
        int i = 10;
        BlockFace face = FurnitureLib.getInstance().getLocationUtil().yawToFace(l.getYaw());
        for (int j = 0; j <= i; j++) {
            Location loc = FurnitureLib.getInstance().getLocationUtil().getRelative(l, face, j, 0D);
            if (loc.getBlock() != null && loc.getBlock().getType() != Material.AIR) {
                return null;
            }
            for (ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()) {
                for (fEntity packet : obj.getPacketList()) {
                    if (packet.getLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
                        double d = packet.getLocation().toVector().distanceSquared(loc.toVector());
                        if (d <= 2.0) {
                            return packet.getObjID();
                        }
                    }
                }
            }
        }
        return null;
    }


    private String getPlugin(String string) {
        return FurnitureManager.getInstance().getObjectList().stream().filter(Objects::nonNull).anyMatch(obj -> obj.getPlugin().equalsIgnoreCase(string)) ? string : null;
    }

    private ObjectID getSerial(String string) {
        return FurnitureManager.getInstance().getObjectList().stream().filter(Objects::nonNull).filter(obj -> obj.getSerial().equalsIgnoreCase(string)).findFirst().orElse(null);
    }

    private String getByPlayerName(String string) {
        return FurnitureManager.getInstance().getObjectList().stream().filter(Objects::nonNull).anyMatch(obj -> obj.getPlayerName().equalsIgnoreCase(string)) ? string : null;
    }

    private int removeListObj(List<ObjectID> objList) {
        int i = 0;
        if (objList == null) {
            return i;
        }
        if (objList.isEmpty()) {
            return i;
        }
        for (ObjectID obj : objList) {
            obj.remove();
            i++;
        }
        return i;
    }

    private HashSet<ObjectID> getObject(Location loc, HashSet<ObjectID> objL, int distance) {
        HashSet<ObjectID> objList = new HashSet<ObjectID>();
        Vector v1 = loc.toVector();
        for (ObjectID obj : objL) {
            Vector v2 = obj.getStartLocation().toVector();
            if (v1.distance(v2) <= distance) {
                if (obj.getSQLAction().equals(SQLAction.REMOVE)) {
                    continue;
                }
                objList.add(obj);
            }
        }
        return objList;
    }

    private HashSet<ObjectID> getObject(Project pro) {
        HashSet<ObjectID> objList = new HashSet<ObjectID>();
        for (ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()) {
            if (obj.getProjectOBJ().equals(pro)) {
                if (obj.getSQLAction().equals(SQLAction.REMOVE)) {
                    continue;
                }
                objList.add(obj);
            }
        }
        return objList;
    }

    private HashSet<ObjectID> getObject(World world) {
        HashSet<ObjectID> objList = new HashSet<>();
        for (ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()) {
            if (obj.getWorld().equals(world)) {
                if (obj.getSQLAction().equals(SQLAction.REMOVE)) {
                    continue;
                }
                objList.add(obj);
            }
        }
        return objList;
    }

    private HashSet<ObjectID> getObject(String playerName) {
        HashSet<ObjectID> objList = new HashSet<>();
        for (ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()) {
            if (obj.getPlayerName().equalsIgnoreCase(playerName)) {
                if (obj.getSQLAction().equals(SQLAction.REMOVE)) {
                    continue;
                }
                objList.add(obj);
            }
        }
        return objList;
    }

    private HashSet<ObjectID> getObjectPlugin(String plugin) {
        HashSet<ObjectID> objList = new HashSet<>();
        for (ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()) {
            if (obj.getPlugin().equalsIgnoreCase(plugin)) {
                if (obj.getSQLAction().equals(SQLAction.REMOVE)) {
                    continue;
                }
                objList.add(obj);
            }
        }
        return objList;
    }

    private World getWorld(String world) {
        return Bukkit.getWorld(world);
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
