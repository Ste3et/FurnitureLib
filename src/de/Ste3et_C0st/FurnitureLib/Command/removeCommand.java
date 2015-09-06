package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class removeCommand {

	public removeCommand(CommandSender sender, Command cmd, String arg2,String[] args) {
		if(!command.noPermissions(sender, "furniture.remove")) return;
		if(args.length==2){
			Project pro = getProject(args[1]);
			World world = getWorld(args[1]);
			String palyer = getUUID(args[1]);
			String plugin = getPlugin(args[1]);
			ObjectID serial = getSerial(args[1]);
			
			if(pro != null){
				if(!command.noPermissions(sender, "furniture.remove.project")) return;
				int i = removeListObj(getObject(pro));
				String str = FurnitureLib.getInstance().getLangManager().getString("RemoveDistance");
				str = str.replace("#AMOUNT#", i+"");
				sender.sendMessage(str);
				return;
			}
			
			if(world != null){
				if(!command.noPermissions(sender, "furniture.remove.world")) return;
				int i = removeListObj(getObject(world));
				String str = FurnitureLib.getInstance().getLangManager().getString("RemoveDistance");
				str = str.replace("#AMOUNT#", i+"");
				sender.sendMessage(str);
				return;
			}
			
			if(palyer != null){
				if(!command.noPermissions(sender, "furniture.remove.player")) return;
				removeListObj(getObject(palyer));
				String str = FurnitureLib.getInstance().getLangManager().getString("RemovePlayer");
				str = str.replace("#PLAYER#", palyer);
				sender.sendMessage(str);
				return;
			}
			
			if(serial != null){
				if(!command.noPermissions(sender, "furniture.remove.obj")) return;
				FurnitureLib.getInstance().getFurnitureManager().remove(serial);
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("RemoveID").replaceAll("#OBJID#", serial.getID()));
				return;
			}
			
			if(plugin != null){
				if(!command.noPermissions(sender, "furniture.remove.plugin")) return;
				removeListObj(getObjectPlugin(plugin));
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("RemovePlugin").replaceAll("#PLUGIN#", plugin));
				return;
			}
			
			if(args[1].equalsIgnoreCase("all")){
				if(!command.noPermissions(sender, "furniture.remove.all")) return;
				removeListObj(FurnitureLib.getInstance().getFurnitureManager().getObjectList());
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("RemoveAll"));
				return;
			}
			
			if(FurnitureLib.getInstance().isInt(args[1])){
				if(!command.noPermissions(sender, "furniture.remove.distance")) return;
				if(sender instanceof Player){
					int distance = Integer.parseInt(args[1]);
					Player p = (Player) sender;
					World w = p.getWorld();
					List<ObjectID> worldObjList = getObject(w);
					int i = removeListObj(getObject(p.getLocation(), worldObjList, distance));
					String s = FurnitureLib.getInstance().getLangManager().getString("RemoveDistance");
					s = s.replace("#AMOUNT#", i+"");
					p.sendMessage(s);return;
				}else if(sender instanceof CommandSender){
					int distance = Integer.parseInt(args[1]);
					BlockCommandSender commandBlock = (BlockCommandSender) sender;
					World w = commandBlock.getBlock().getWorld();
					List<ObjectID> worldObjList = getObject(w);
					int i = removeListObj(getObject(commandBlock.getBlock().getLocation(), worldObjList, distance));
					String s = FurnitureLib.getInstance().getLangManager().getString("RemoveDistance");
					s = s.replace("#AMOUNT#", i+"");
					sender.sendMessage(s);return;
				}
				return;
			}
			
			if(args[1].equalsIgnoreCase("lookat")){
				if(!command.noPermissions(sender, "furniture.remove.lookat")) return;
				if(sender instanceof Player){
					Player p = (Player) sender;
					ObjectID obj = getFromSight(p.getLocation());
					if(obj!=null){
						FurnitureLib.getInstance().getFurnitureManager().remove(obj);
						p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("RemoveLookat").replaceAll("#SERIAL#", obj.getSerial()));
					}
					return;
				}
				return;
			}
			
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
			return;
		}
	}
	
	private ObjectID getFromSight(Location l){
		if(FurnitureLib.getInstance().getFurnitureManager().getObjectList().isEmpty()){return null;}
		Integer i = 10;
		BlockFace face = FurnitureLib.getInstance().getLocationUtil().yawToFace(l.getYaw());
		for(int j = 0; j<=i;j++){
			Location loc = FurnitureLib.getInstance().getLocationUtil().getRelativ(l, face,(double) j, 0D);
			if(loc.getBlock()!=null&&loc.getBlock().getType()!=Material.AIR){return null;}
			for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
				for(ArmorStandPacket packet : obj.getPacketList()){
					if(packet.getLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())){
						Double d = packet.getLocation().toVector().distanceSquared(loc.toVector());
						if(d<=2.0){
							return packet.getObjectId();
						}
					}
				}
			}
		}
		return null;
	}
	
	
	private String getPlugin(String string) {
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getPlugin().equalsIgnoreCase(string)){
				return string;
			}
		}
		return null;
	}

	private ObjectID getSerial(String string) {
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getSerial().equalsIgnoreCase(string)){
				return obj;
			}
		}
		return null;
	}

	private String getUUID(String string) {
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getPlayerName().equalsIgnoreCase(string)){
				return string;
			}
		}
		
		return null;
	}

	private int removeListObj(List<ObjectID> objList){
		int i = 0;
		if(objList==null){return i;}
		if(objList.isEmpty()){return i;}
		for(ObjectID obj : objList){
			FurnitureLib.getInstance().getFurnitureManager().remove(obj);
			i++;
		}
		return i;
	}
	
	private List<ObjectID> getObject(Location loc, List<ObjectID> objL, int distance){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		Vector v1 = loc.toVector();
		for(ObjectID obj : objL){
			Vector v2 = obj.getStartLocation().toVector();
			if(v1.distance(v2)<=distance){
				if(obj.getSQLAction().equals(SQLAction.REMOVE)){continue;}
				objList.add(obj);
			}
		}
		return objList;
	}
	
	private List<ObjectID> getObject(Project pro){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getProjectOBJ().equals(pro)){
				if(obj.getSQLAction().equals(SQLAction.REMOVE)){continue;}
				objList.add(obj);
			}
		}
		return objList;
	}
	
	private List<ObjectID> getObject(World world){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getWorld().equals(world)){
				if(obj.getSQLAction().equals(SQLAction.REMOVE)){continue;}
				objList.add(obj);
			}
		}
		return objList;
	}
	
	private List<ObjectID> getObject(String playerName){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getPlayerName().equalsIgnoreCase(playerName)){
				if(obj.getSQLAction().equals(SQLAction.REMOVE)){continue;}
				objList.add(obj);
			}
		}
		return objList;
	}
	
	private List<ObjectID> getObjectPlugin(String plugin){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getPlugin().equalsIgnoreCase(plugin)){
				if(obj.getSQLAction().equals(SQLAction.REMOVE)){continue;}
				objList.add(obj);
			}
		}
		return objList;
	}
	
	private World getWorld(String world){
		return Bukkit.getWorld(world);
	}
	
	private Project getProject(String project){
		for(Project pro : FurnitureLib.getInstance().getFurnitureManager().getProjects()){
			if(pro.getName().equalsIgnoreCase(project)){
				return pro;
			}
		}
		return null;
	}

}
