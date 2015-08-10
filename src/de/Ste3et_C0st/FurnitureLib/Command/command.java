package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.ArrayList;
import java.util.List;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;
import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class command implements CommandExecutor, Listener{

	FurnitureManager manager;
	Plugin plugin;
	List<Player> playerList = new ArrayList<Player>();
	
	public command(FurnitureManager manager, Plugin plugin){
		this.manager = manager;
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onArmorRightClick(FurnitureClickEvent e){
		if(playerList.contains(e.getPlayer())){
			e.setCancelled(true);
			Player p = e.getPlayer();
			p.sendMessage("§6Furniture Info about§e " + e.getID().getSerial());
			p.sendMessage("§6Plugin:§e " + e.getID().getPlugin());
			p.sendMessage("§6Type:§e " + e.getID().getProject());
			playerList.remove(p);
		}
	}
	
	public boolean noPermissions(CommandSender sender, String s){
		if(sender.isOp()) return true;
		if(sender.hasPermission("furniture.admin")) return true;
		if(!sender.hasPermission(s)){
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NoPermissions"));
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("furniture")){
				if(args.length==0){
					sendHelp(p);
					return true;
				}else if(args.length==1){
					if(args[0].equalsIgnoreCase("list")){
						if(!noPermissions(sender, "furniture.list")) return true;
						getList("type", p, 0);return true;
					}else if(args[0].equalsIgnoreCase("debug")){
						if(!noPermissions(sender, "furniture.debug")) return true;
						p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("DebugModeEntered"));
						playerList.add(p);return true;
					}else{
						sendHelp(p);
						return true;
					}
				}else if(args.length==2){
					if(args[0].equalsIgnoreCase("list")){
						if(!noPermissions(sender, "furniture.list")) return true;
						if(args[1].equalsIgnoreCase("plugin")){
							if(!noPermissions(sender, "furniture.list.plugin")) return true;
							getList(null, p, 0);return true;
						}else if(args[1].equalsIgnoreCase("type")){
							if(!noPermissions(sender, "furniture.list.type")) return true;
							getList("type", p, 0);return true;
						}else if(args[1].equalsIgnoreCase("world")){
							if(!noPermissions(sender, "furniture.list.world")) return true;
							getList("world", p, 0);return true;
						}else if(FurnitureLib.getInstance().isInt(args[1])){
							int i = Integer.parseInt(args[1]);
							getList("type", p, i);return true;
						}
						return true;
					}else if(args[0].equalsIgnoreCase("remove")){
						if(!noPermissions(sender, "furniture.remove")) return true;
						if(FurnitureLib.getInstance().isInt(args[1])){
							if(!noPermissions(sender, "furniture.remove.distance")) return true;
							Integer Distance = Integer.parseInt(args[1]);
							List<ObjectID> objList = getFromDistance(Distance, p.getLocation());
							Integer i = objList.size();
							String s = FurnitureLib.getInstance().getLangManager().getString("RemoveDistance");
							s = s.replace("#AMOUNT#", i+"");
							if(objList!=null&&!objList.isEmpty()){
								removeListObj(objList);
								
							}
							p.sendMessage(s);
							return true;
						}else if(getID(args[1])!=null){
							if(!noPermissions(sender, "furniture.remove.obj")) return true;
							ObjectID id = getID(args[1]);
							String s = id.getSerial();
							if(id!=null){
								manager.remove(id);
							}
							p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("RemoveID").replaceAll("#OBJID#", s));
							return true;
						}else if(!getPlugin(args[1]).isEmpty()){
							if(!noPermissions(sender, "furniture.remove.plugin")) return true;
							List<ObjectID> objList = getPlugin(args[1]);
							if(objList!=null){
								removeListObj(objList);
							}
							p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("RemovePlugin").replaceAll("#PLUGIN#", args[1]));
							return true;
						}else if(!getType(args[1]).isEmpty()){
							if(!noPermissions(sender, "furniture.remove.type")) return true;
							List<ObjectID> objList = getType(args[1]);
							if(objList!=null){
								removeListObj(objList);
							}
							p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("RemoveType").replaceAll("#TYPE#", args[1]));
							return true;
						}else if(args[1].equalsIgnoreCase("all")){
							if(!noPermissions(sender, "furniture.remove.all")) return true;
							removeListObj(manager.getObjectList());
							p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("RemoveAll"));
							return true;
						}else if(args[1].equalsIgnoreCase("lookat")){
							if(!noPermissions(sender, "furniture.remove.lookat")) return true;
							ObjectID obj = getFromSight(p.getLocation());
							String s = obj.getSerial();
							if(obj!=null){
								manager.remove(obj);
							}
							p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("RemoveLookat").replaceAll("#SERIAL#", s));
							return true;
						}
					}else if(args[0].equalsIgnoreCase("give")){
						if(!noPermissions(sender, "furniture.give")) return true;
						give(sender, p, args[1], 1);
					}else if(args[0].equalsIgnoreCase("recipe")){
						if(!noPermissions(sender, "furniture.recipe")) return true;
						FurnitureLib.getInstance().getCraftingInv().openCrafting(p, args[1]);
					}else if(args[0].equalsIgnoreCase("respawn")){
						if(!noPermissions(sender, "furniture.respawn")) return true;
						String s = args[1];
						if(!getType(s).isEmpty()){
							for(ObjectID id : getType(s)){
								Location loc = id.getStartLocation();
								Project project = manager.getProject(id.getProject());
								manager.remove(id);
								FurnitureLib.getInstance().spawn(project, loc);
							}
						}
					}else{
						sendHelp(p);
						return true;
					}
					return true;
				}else if(args.length==3){
					if(args[0].equalsIgnoreCase("give")){
						//          0    1      2        3
						//furniture give <type> <player> <amount>
						if(!noPermissions(sender, "furniture.give.player")) return true;
						Player player = Bukkit.getPlayer(args[2]);
						if(player == null || !player.isOnline()){
							sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("PlayerNotOnline").replaceAll("#PLAYER#", args[2]));
							return true;
						}
						give(sender, player, args[1], 1);
					}else if(args[0].equalsIgnoreCase("list")){
						if(!FurnitureLib.getInstance().isInt(args[2])){
							p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
							return true;
						}
						if(!noPermissions(sender, "furniture.list")) return true;
						if(args[1].equalsIgnoreCase("plugin")){
							if(!noPermissions(sender, "furniture.list.plugin")) return true;
							getList(null, p, Integer.parseInt(args[2]));return true;
						}else if(args[1].equalsIgnoreCase("type")){
							if(!noPermissions(sender, "furniture.list.type")) return true;
							getList("type", p, Integer.parseInt(args[2]));return true;
						}else if(args[1].equalsIgnoreCase("world")){
							if(!noPermissions(sender, "furniture.list.world")) return true;
							getList("world", p, Integer.parseInt(args[2]));return true;
						}
						return true;
					}else{
						sendHelp(p);
						return true;
					}
					return true;
				}else if(args.length==4){
					if(args[0].equalsIgnoreCase("give")){
						if(!noPermissions(sender, "furniture.give.player")) return true;
						String pl = args[2];
						String ty = args[1];
						String am = args[3];
						Player player = Bukkit.getPlayer(pl);
						if(player==null){
							sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("PlayerNotOnline").replaceAll("#PLAYER#", pl));
							return true;
						}
						
						Project pro = manager.getProject(ty);
						if(pro==null){
							sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("ProjectNotFound").replaceAll("#PROJECT#", pl));
							return true;
						}
						
						if(!FurnitureLib.getInstance().isInt(am)){
							sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
							return true;
						}
						
						
						Integer i = Integer.parseInt(am);
						ItemStack is = pro.getCraftingFile().getRecipe().getResult().clone();
						is.setAmount(i);
						player.getInventory().addItem(is);
						player.updateInventory();
						String str = FurnitureLib.getInstance().getLangManager().getString("GivePlayer");
						str = str.replace("#PLAYER#", player.getName());
						str = str.replace("#PROJECT#", ty);
						str = str.replace("#AMOUNT#", i+"");
						sender.sendMessage(str);
						return true;
					}
				}else{
					sendHelp(p);
					return true;
				}
				
				return true;
			}
		}else if(sender instanceof BlockCommandSender){
				if(cmd.getName().equalsIgnoreCase("furniture")){
					BlockCommandSender bs = (BlockCommandSender) sender;
					if(args.length==6){
						if(args[0].equalsIgnoreCase("spawn")){
							Boolean Yaw = FurnitureLib.getInstance().isInt(args[4]);
							if(FurnitureLib.getInstance().getFurnitureManager().getProject(args[5])!=null){
								Integer x = Integer.parseInt(relativ((BlockCommandSender) sender, args[1], 0));
								Integer y = Integer.parseInt(relativ((BlockCommandSender) sender, args[2], 1));
								Integer z = Integer.parseInt(relativ((BlockCommandSender) sender, args[3], 2));
								Integer yaw = 0;
								
								if(Yaw){
									yaw = Integer.parseInt(args[4]);
								}
								
									World w = bs.getBlock().getWorld();
									Location l = new Location(w, x, y, z).getBlock().getLocation();
									l.setYaw(yaw);
									Project pro = FurnitureLib.getInstance().getFurnitureManager().getProject(args[5]);
									FurnitureLib.getInstance().spawn(pro, l);
							}
						}
					}else if(args.length==3){
						if(args[0].equalsIgnoreCase("crafting")){
							if(Bukkit.getPlayer(args[1])==null){return true;}
							if(FurnitureLib.getInstance().getFurnitureManager().getProject(args[2])==null){return true;}
							FurnitureLib.getInstance().getCraftingInv().openCrafting(Bukkit.getPlayer(args[1]), args[2]);
						}
					}else if(args.length==4){
						if(args[0].equalsIgnoreCase("give")){
							String pl = args[2];
							String ty = args[1];
							String am = args[3];
							Player player = Bukkit.getPlayer(pl);
							if(player==null){
								sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("PlayerNotOnline").replaceAll("#PLAYER#", pl));
								return true;
							}
							
							Project pro = manager.getProject(ty);
							if(pro==null){
								sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("ProjectNotFound").replaceAll("#PROJECT#", pl));
								return true;
							}
							
							if(!FurnitureLib.getInstance().isInt(am)){
								sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
								return true;
							}
							Integer i = Integer.parseInt(am);
							ItemStack is = pro.getCraftingFile().getRecipe().getResult().clone();
							is.setAmount(i);
							player.getInventory().addItem(is);
						}
					}
				}
		}else if(sender instanceof ConsoleCommandSender){
			if(cmd.getName().equalsIgnoreCase("furniture")){
				if(args.length==4){
					if(args[0].equalsIgnoreCase("give")){
						String pl = args[2];
						String ty = args[1];
						String am = args[3];
						Player player = Bukkit.getPlayer(pl);
						if(player==null){
							sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("PlayerNotOnline").replaceAll("#PLAYER#", pl));
							return true;
						}
						
						Project pro = manager.getProject(ty);
						if(pro==null){
							sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("ProjectNotFound").replaceAll("#PROJECT#", pl));
							return true;
						}
						
						if(!FurnitureLib.getInstance().isInt(am)){
							sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongArgument"));
							return true;
						}
						Integer i = Integer.parseInt(am);
						ItemStack is = pro.getCraftingFile().getRecipe().getResult().clone();
						is.setAmount(i);
						player.getInventory().addItem(is);
						//furniture give [PLAYER] <TYPE> <AMOUNT>
					}
				}
			}
		}
		return false;
	}
	
	private String relativ(BlockCommandSender sender, String s, int i){
		Location l = sender.getBlock().getLocation();
		Integer j = 0;
		if(s.startsWith("~")){
			s = s.replace("~", "");
			if(i==0) j = (int) l.getX();
			if(i==1) j = (int) l.getY();
			if(i==2) j = (int) l.getZ();
			if(s.isEmpty()) return j+"";
			if(s.startsWith("-")){
				s = s.replace("-", "");
				if(!FurnitureLib.getInstance().isInt(s)) return j+"";
				j-=Integer.parseInt(s);
			}else if(s.startsWith("+")){
				s = s.replace("+", "");
				if(!FurnitureLib.getInstance().isInt(s)) return j+"";
				j+=Integer.parseInt(s);
			}
		}else{
			return s;
		}
		return j+"";
	}
	
	private void give(CommandSender sender, Player p2, String string, int i) {
		Project project = null;
		for(Project pro : manager.getProjects()){
			if(pro.getName().equalsIgnoreCase(string)) project = pro;
		}
		if(project==null){
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("ProjectNotFound").replaceAll("#PROJECT#", string));
			return;
		}
		
		p2.getInventory().addItem(project.getCraftingFile().getRecipe().getResult());
		if(sender instanceof Player && sender.equals(p2)){return;}
		String str = FurnitureLib.getInstance().getLangManager().getString("GivePlayer");
		str = str.replace("#PLAYER#", p2.getName());
		str = str.replace("#PROJECT#", string);
		str = str.replace("#AMOUNT#", i+"");
		sender.sendMessage(str);
	}
	
	private int getPage(int i){
        int a = (((i+9)/10)*10);
        return a;
	}
	
	private int getMaxPage(String option){
		int j = 10;
		switch (option) {
		case "Plugin":
			List<String> l = new ArrayList<String>();
			for(Project pro : manager.getProjects()){
				if(!l.contains(pro.getPlugin().getName())){
					l.add(pro.getPlugin().getName());
				}
			}
			j = getPage(l.size());
			break;
		case "World":
			j = getPage(Bukkit.getWorlds().size());
			break;
		case "type":
			j = getPage(manager.getProjects().size());
			break;
		}
		return j;
	}

	private void getList(String option, Player p, int page){
		if(page==0)page=1;
		int objects = 10;
		int min = page*objects-objects;
		int max = page*objects;
		int maxPage = getMaxPage(option)/10;
		String a = "";
		String b = "";
		if(maxPage<10){
			a+="0"+maxPage;
		}else{
			a=maxPage+"";
		}
		
		if(page<10){
			b+="0"+page;
		}else{
			b=page+"";
		}
		
		if(page>maxPage){
			p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("SideNotFound"));
			p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("SideNavigation").replaceAll("#MAX#", maxPage + ""));
			return;
		}
		
		String s = "§7§m+--------------------------------------------+§8[§e" + b + "§8/§a" + a + "§8]";
		
		p.sendMessage(s);
		if(option==null){option="Plugin";}
		if(option.equalsIgnoreCase("Plugin")){
			p.sendMessage("§ePlugins: ");
			List<String> plugins = new ArrayList<String>();
			int j = 0;
			for(ObjectID id : manager.getObjectList()){
				if(j>=min&&j<max){
					if(!plugins.contains(id.getPlugin())){
						String plugin = id.getPlugin();
						String toolTip = "§aObjects: " + getPlugin(plugin).size() + "\n" + getTypes(plugin);
						try{
							new FancyMessage("§6- " +plugin).tooltip(toolTip).send(p);
						}catch(Exception e){
							p.sendMessage("§6- " + plugin + ":" + toolTip);
						}
						
						plugins.add(plugin);
					}
				}
				j++;
			}
		}else if(option.equalsIgnoreCase("type")){
			p.sendMessage("§eTypes: ");
			List<String> types = new ArrayList<String>();
			int j = 0;
			for(ObjectID id : manager.getObjectList()){
				if(j>=min&&j<max){
					if(!types.contains(id.getProject())){
						String plugin = id.getPlugin();
						String toolTip = "§aObjects: " + getPlugin(plugin).size() + "\n" + "§aPlugin: " + plugin;
						
						try{
							new FancyMessage("§6- " +id.getProject()).tooltip(toolTip).send(p);
						}catch(Exception e){
							p.sendMessage("§6- " + id.getProject() + ":" + toolTip);
						}
						
						types.add(id.getProject());
						j++;
					}
				}
			}
			
			for(Project pro : manager.getProjects()){
				if(!types.contains(pro.getName())){
					if(j>=min&&j<max){
						String plugin = pro.getPlugin().getName();
						String toolTip = "§aObjects: " + getPlugin(plugin).size() + "\n" + "§aPlugin: " + plugin;
						
						try{
							new FancyMessage("§6- " +pro.getName()).tooltip(toolTip).send(p);
						}catch(Exception e){
							p.sendMessage("§6- " + pro.getName() + ":" + toolTip);
						}
						types.add(pro.getName());
					}
					j++;
				}
			}
		}else if(option.equalsIgnoreCase("world")){
			p.sendMessage("§eWorlds: ");
			int j = 0;
			for(World w : Bukkit.getWorlds()){
				if(j>=min&&j<max){
					try{
						new FancyMessage("§6- " + w.getName()).tooltip("§aObjects: " + getWInt(w.getName())).send(p);
					}catch(Exception e){
						p.sendMessage("§6- " + w.getName() + ":" + getWInt(w.getName()));
					}
				}
				j++;
			}
		}
		s = "§7§m+-------------------------------------------------+";
		p.sendMessage(s);
		s = FurnitureLib.getInstance().getLangManager().getString("SideNavigation").replaceAll("#MAX#", maxPage + "");
		p.sendMessage(s);
	}
	
	private String getTypes(String plugin){
		String types="";
		for(ObjectID obj : manager.getObjectList()){
			if(!types.contains(obj.getProject())){
				types += "§a- " + obj.getProject() + " \n";
			}
		}
		return types;
	}
	
	private void sendHelp(Player player){
		if(!player.hasPermission("furniture.help")) return;
		
		try{
			new FancyMessage("§7§m+--------------------§7[")
			.then("§2Furniture").tooltip("")
			.then("§7]§m---------------------+\n")
			.then("§6/furniture list §e(Option) §c(side)\n").tooltip("§6list all available furniture\n\n§cOption:\n§6Plugin\n§6Type\n§6World").suggest("/furniture list (Option)")
			.then("§6/furniture give §e<furniture> §c(player) §d(Amount)\n").tooltip("§6if the §cPlayer not set:\n"
					+ "§cyou become a furniture\n" + 
					"§6if the Player set:\n" + 
					"§cgive the player one furniture").suggest("/furniture give <FURNITURE> (player)")
			.then("§6/furniture debug \n").tooltip("§6You can become some information about\n§6abaout the furniture you are rightclicked").suggest("/furniture debug")
			.then("§6/furniture recipe §e<type>\n").tooltip("§6View recipe from a furniture").suggest("/furniture recipe §e<type>")
			.then("§6/furniture remove §e<type>\n").tooltip("§6It's remove only one type of the \n§6Furniture").suggest("/furniture remove <type>")
			.then("§6/furniture remove §e<plugin>\n").tooltip("§6It's remove only all Furniture from one Plugin").suggest("/furniture remove <plugin>")
			.then("§6/furniture remove §e<ID>\n").tooltip("§6Remove a furniture by id").suggest("/furniture remove <ID>")
			.then("§6/furniture remove §e<Distance>\n").tooltip("§6Remove all furniture in Distance").suggest("/furniture remove <Distance>")
			.then("§6/furniture remove §elookat\n").tooltip("§6Remove a furniture at you looked").suggest("/furniture remove lookat")
			.then("§6/furniture remove §eall\n").tooltip("§6Remove all furniture and reset database").suggest("/furniture remove all")
			.then("§6/furniture respawn §e<type>\n").tooltip("§6Respawn the all furniture by type\n§c§lUse it with the own risk").suggest("/furniture respawn <type>")
			.then("\n").then("§e§lTIP: §r§7Try to §e§nclick§7 or §e§nhover§7 the commands").
			then("§7§m+--------------------------------------------------+").send(player);
		}catch(Exception e){
			String str = "§7§m+--------------------§7[§2Furniture§7]§m---------------------+\n";
			str+="§6/furniture list §e(Plugin/Type/World) §c(side) | list all available furniture\n";
			str+="§6/furniture give §e<furniture> §c(player) §d(Amount)\n";
			str+="§6/furniture debug | when you rightclick an furniture §6you become some infos about it\n";
			str+="§6/furniture recipe §e<type> §6| You become an crafting recipe about the Type\n";
			str+="§6/furniture remove §e<type> §6| You can remove all furniture by type\n";
			str+="§6/furniture remove §e<plugin> §6| You can remove all furniture by plugin\n";
			str+="§6/furniture remove §e<ID> §6| You can remove an furniture by ID\n";
			str+="§6/furniture remove §e<Distance> §6| You can remove all furniture by Distance\n";
			str+="§6/furniture remove §elookat §6| You can remove an furniture are you lookat\n";
			str+="§6/furniture remove §eall §6| Remove all furniture ID\n\n";
			str+="§6/furniture respawn §e<type> | §6Respawn all furniture by type (§c§lEXPEREMENTEL)";
			str+="§7§m+--------------------------------------------------+";
			player.sendMessage(str);
		}

	}
	
	private ObjectID getID(String serial){
		for(ObjectID id : manager.getObjectList()){
			if(serial.equalsIgnoreCase(id.getSerial())){
				return id;
			}
		}
		return null;
	}
	
	private Integer getWInt(String s){
		Integer j = 0;
		for(ArmorStandPacket aPacket : manager.getAsList()){
			if(aPacket.getLocation().getWorld().getName().equalsIgnoreCase(s)){
				j++;
			}
		}
		return j;
	}
	
	private List<ObjectID> getPlugin(String plugin){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID id : manager.getObjectList()){
			if(id.getPlugin().equalsIgnoreCase(plugin)){
				objList.add(id);
			}
		}
		return objList;
	}
	
	private List<ObjectID> getType(String type){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ObjectID obj : manager.getObjectList()){
			if(obj.getProject().equalsIgnoreCase(type)){
				objList.add(obj);
			}
		}
		return objList;
	}
	
	private List<ObjectID> getFromDistance(Integer i, Location l){
		List<ObjectID> objList = new ArrayList<ObjectID>();
		for(ArmorStandPacket packet : manager.getAsList()){
			if(packet.getLocation().getWorld().getName().equalsIgnoreCase(l.getWorld().getName())){
				if(packet.getLocation().toVector().distance(l.toVector())<=i){
					objList.add(packet.getObjectId());
				}
			}
		}
		return objList;
	}
	
	private ObjectID getFromSight(Location l){
		Integer i = 10;
		BlockFace face = FurnitureLib.getInstance().getLocationUtil().yawToFace(l.getYaw());
		for(int j = 0; j<=i;j++){
			Location loc = FurnitureLib.getInstance().getLocationUtil().getRelativ(l, face,(double) j, 0D);
			if(loc.getBlock()!=null&&loc.getBlock().getType()!=Material.AIR){return null;}
			for(ArmorStandPacket packet : manager.getAsList()){
				if(packet.getLocation().getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())){
					Double d = packet.getLocation().toVector().distanceSquared(loc.toVector());
					if(d<=2.0){
						return packet.getObjectId();
					}
				}
			}
		}
		return null;
	}
	
	private void removeListObj(List<ObjectID> objList){
		if(objList==null){return;}
		try{
			for(ObjectID obj : objList){
				manager.remove(obj);
			}
		}catch(Exception e){}

	}
}
