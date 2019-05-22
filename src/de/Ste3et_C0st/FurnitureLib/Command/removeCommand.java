package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.HashSet;
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
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class removeCommand extends iCommand{

	public removeCommand(String subCommand, String permissions, String ...args) {
		super(subCommand, permissions);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!hasCommandPermission(sender)) return;
		if(args.length==2){
			Project pro = getProject(args[1]);
			World world = getWorld(args[1]);
			String palyer = getUUID(args[1]);
			String plugin = getPlugin(args[1]);
			ObjectID serial = getSerial(args[1]);
			
			if(pro != null){
				if(!hasCommandPermission(sender, ".project")) return;
				int i = removeListObj(getObject(pro));
				String str = FurnitureLib.getInstance().getLangManager().getString("message.RemoveDistance");
				str = str.replace("#AMOUNT#", i+"");
				sender.sendMessage(str);
				return;
			}
			
			if(world != null){
				if(!hasCommandPermission(sender, ".world")) return;
				int i = removeListObj(getObject(world));
				String str = FurnitureLib.getInstance().getLangManager().getString("message.RemoveDistance");
				str = str.replace("#AMOUNT#", i+"");
				sender.sendMessage(str);
				return;
			}
			
			if(palyer != null){
				if(!hasCommandPermission(sender, ".player")) return;
				removeListObj(getObject(palyer));
				String str = FurnitureLib.getInstance().getLangManager().getString("message.RemovePlayer");
				str = str.replace("#PLAYER#", palyer);
				sender.sendMessage(str);
				return;
			}
			
			if(serial != null){
				if(!hasCommandPermission(sender, ".obj")) return;
				FurnitureLib.getInstance().getFurnitureManager().remove(serial);
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.RemoveID").replaceAll("#OBJID#", serial.getID()));
				return;
			}
			
			if(plugin != null){
				if(!hasCommandPermission(sender, ".plugin")) return;
				removeListObj(getObjectPlugin(plugin));
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.RemovePlugin").replaceAll("#PLUGIN#", plugin));
				return;
			}
			
			if(args[1].equalsIgnoreCase("all")){
				if(!hasCommandPermission(sender, ".all")) return;
				removeListObj(FurnitureLib.getInstance().getFurnitureManager().getObjectList());
				sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.RemoveAll"));
				return;
			}
			
			if(FurnitureLib.getInstance().isInt(args[1])){
				if(!hasCommandPermission(sender, ".distance")) return;
				if(sender instanceof Player){
					int distance = Integer.parseInt(args[1]);
					Player p = (Player) sender;
					World w = p.getWorld();
					HashSet<ObjectID> worldObjList = getObject(w);
					int i = removeListObj(getObject(p.getLocation(), worldObjList, distance));
					String s = FurnitureLib.getInstance().getLangManager().getString("message.RemoveDistance");
					s = s.replace("#AMOUNT#", i+"");
					p.sendMessage(s);return;
				}else if(sender instanceof CommandSender){
					int distance = Integer.parseInt(args[1]);
					BlockCommandSender commandBlock = (BlockCommandSender) sender;
					World w = commandBlock.getBlock().getWorld();
					HashSet<ObjectID> worldObjList = getObject(w);
					int i = removeListObj(getObject(commandBlock.getBlock().getLocation(), worldObjList, distance));
					String s = FurnitureLib.getInstance().getLangManager().getString("message.RemoveDistance");
					s = s.replace("#AMOUNT#", i+"");
					sender.sendMessage(s);return;
				}
				return;
			}
			
			if(args[1].equalsIgnoreCase("lookat")){
				if(!hasCommandPermission(sender, ".lookat")) return;
				if(sender instanceof Player){
					Player p = (Player) sender;
					ObjectID obj = getFromSight(p.getLocation());
					if(obj!=null){
						FurnitureLib.getInstance().getFurnitureManager().remove(obj);
						p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.RemoveLookat").replaceAll("#SERIAL#", obj.getSerial()));
					}
					return;
				}
				return;
			}
			
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.WrongArgument"));
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
				for(fEntity packet : obj.getPacketList()){
					if(packet.getLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())){
						Double d = packet.getLocation().toVector().distanceSquared(loc.toVector());
						if(d<=2.0){
							return packet.getObjID();
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

	private int removeListObj(HashSet<ObjectID> objList){
		int i = 0;
		if(objList==null){return i;}
		if(objList.isEmpty()){return i;}
		for(ObjectID obj : objList){
			obj.remove();
			i++;
		}
		return i;
	}
	
	private HashSet<ObjectID> getObject(Location loc, HashSet<ObjectID> objL, int distance){
		HashSet<ObjectID> objList = new HashSet<ObjectID>();
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
	
	private HashSet<ObjectID> getObject(Project pro){
		HashSet<ObjectID> objList = new HashSet<ObjectID>();
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getProjectOBJ().equals(pro)){
				if(obj.getSQLAction().equals(SQLAction.REMOVE)){continue;}
				objList.add(obj);
			}
		}
		return objList;
	}
	
	private HashSet<ObjectID> getObject(World world){
		HashSet<ObjectID> objList = new HashSet<ObjectID>();
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getWorld().equals(world)){
				if(obj.getSQLAction().equals(SQLAction.REMOVE)){continue;}
				objList.add(obj);
			}
		}
		return objList;
	}
	
	private HashSet<ObjectID> getObject(String playerName){
		HashSet<ObjectID> objList = new HashSet<ObjectID>();
		for(ObjectID obj : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
			if(obj.getPlayerName().equalsIgnoreCase(playerName)){
				if(obj.getSQLAction().equals(SQLAction.REMOVE)){continue;}
				objList.add(obj);
			}
		}
		return objList;
	}
	
	private HashSet<ObjectID> getObjectPlugin(String plugin){
		HashSet<ObjectID> objList = new HashSet<ObjectID>();
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
