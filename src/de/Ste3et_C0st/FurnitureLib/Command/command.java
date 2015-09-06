package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Events.FurnitureClickEvent;
import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder;
import de.Ste3et_C0st.FurnitureLib.Utilitis.ManageInv;
import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder.ClickAction;
import de.Ste3et_C0st.FurnitureLib.Utilitis.JsonBuilder.HoverAction;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

public class command implements CommandExecutor, Listener, TabCompleter{
	
	static FurnitureLib lib = FurnitureLib.getInstance();
	FurnitureManager manager = lib.getFurnitureManager();
	Plugin plugin;
	public static List<Player> playerList = new ArrayList<Player>();
	public static List<Player> manageList = new ArrayList<Player>();
	
	public command(Plugin plugin){
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
			p.sendMessage("§6PublicMode:§e " + e.getID().getPublicMode().name().toLowerCase());
			p.sendMessage("§6Owner: §2" + e.getID().getPlayerName());
			p.sendMessage("§6PublicEventAccess: §e" + e.getID().getEventType().name().toLowerCase());
			p.sendMessage("§6ArmorStands: §e" + e.getID().getPacketList().size());
			p.sendMessage("§6FromDatabse: §c" + e.getID().isFromDatabase());
			p.sendMessage("§6Members: §e" + e.getID().getMemberList().size());
			p.sendMessage("§6SQL State: §e" + e.getID().getSQLAction().name().toLowerCase());
			playerList.remove(p);
		}else if(manageList.contains(e.getPlayer())){
			e.setCancelled(true);
			Player p = e.getPlayer();
			manageList.remove(p);
			if(!e.getID().getUUID().equals(p.getUniqueId())){
				if(!lib.hasPerm(p, "furniture.admin") && !p.isOp() && !lib.hasPerm(p, "furniture.manage.other")){
					p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("WrongOwner"));
					return;
				}
			}
			new ManageInv(p, e.getID());
			
		}
	}
	
	public static boolean noPermissions(CommandSender sender, String s){
		if(sender.isOp()) return true;
		if(lib.hasPerm(sender,"furniture.admin")) return true;
		if(!lib.hasPerm(sender,s)){
			sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("NoPermissions"));
			return false;
		}
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
			if(cmd.getName().equalsIgnoreCase("furniture")){
				Player p = null;
				if(sender instanceof Player){p=(Player) sender;}
				if(args.length==0){
					sendHelp(p);
					return true;
				}else{
					switch (args[0]) {
					case "list": new listCommand(sender, cmd, arg2, args); return true;
					case "give": new giveCommand(sender, cmd, arg2, args); return true;
					case "recipe": new recipeCommand(sender, cmd, arg2, args); return true;
					case "debug": new debugCommand(sender, cmd, arg2, args); return true;
					case "manage": new manageCommand(sender, cmd, arg2, args); return true;
					case "remove": new removeCommand(sender, cmd, arg2, args); return true;
					case "spawn": new spawnCommand(sender, cmd, arg2, args); return true;
					default: sendHelp(p); return true;
					}
				}
			}
		return false;
	}
	
	public static void sendHelp(Player player){
		if(player==null) return;
		
		String version = FurnitureLib.getInstance().getDescription().getVersion();
		String Author = FurnitureLib.getInstance().getDescription().getAuthors().get(0);
		String update = FurnitureLib.getInstance().getUpdater().getUpdate();
		
		if(!lib.hasPerm(player,"furniture.help")) return;
		new JsonBuilder("§7§m+--------------------§7[")
		.withText("§2Furniture").withHoverEvent(HoverAction.SHOW_TEXT, "§6§lVersion: §7" + version + "\n"
				+ "§6§lAuthor: §2" + Author +  update)
		.withText("§7]§m---------------------+\n")
		.withText("§6/furniture list §e(Option) §c(side)\n").withHoverEvent(HoverAction.SHOW_TEXT,"§6list all available furniture\n\n§cOption:\n§6Plugin\n§6Type\n§6World").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture list (Option)")
		.withText("§6/furniture give §e<furniture> §c(player) §d(Amount)\n").withHoverEvent(HoverAction.SHOW_TEXT,"§6if the §cPlayer not set:\n"
				+ "§cyou become a furniture\n" + 
				"§6if the Player set:\n" + 
				"§cgive the player one furniture").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture give <FURNITURE> (player)")
		.withText("§6/furniture debug \n").withHoverEvent(HoverAction.SHOW_TEXT,"§6You can become some information about\n§6abaout the furniture you are rightclicked").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture debug")
		.withText("§6/furniture manage \n").withHoverEvent(HoverAction.SHOW_TEXT,"§6You can config the furniture\n§6that you are rightclicked").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture manage")
		.withText("§6/furniture recipe §e<type>\n").withHoverEvent(HoverAction.SHOW_TEXT,"§6View recipe from a furniture").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture recipe §e<type>")
		.withText("§6/furniture remove §e<type>\n").withHoverEvent(HoverAction.SHOW_TEXT,"§6It's remove only one type of the \n§6Furniture").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture remove <type>")
		.withText("§6/furniture remove §e<player>\n").withHoverEvent(HoverAction.SHOW_TEXT,"§6Remove all furniture from an player").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture remove <player>")
		.withText("§6/furniture remove §e<world>\n").withHoverEvent(HoverAction.SHOW_TEXT,"§6Remove all furniture from an world").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture remove <world>")
		.withText("§6/furniture remove §e<plugin>\n").withHoverEvent(HoverAction.SHOW_TEXT,"§6It's remove only all Furniture from one Plugin").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture remove <plugin>")
		.withText("§6/furniture remove §e<ID>\n").withHoverEvent(HoverAction.SHOW_TEXT,"§6Remove a furniture by id").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture remove <ID>")
		.withText("§6/furniture remove §e<Distance>\n").withHoverEvent(HoverAction.SHOW_TEXT,"§6Remove all furniture in Distance").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture remove <Distance>")
		.withText("§6/furniture remove §elookat\n").withHoverEvent(HoverAction.SHOW_TEXT,"§6Remove a furniture at you looked").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture remove lookat")
		.withText("§6/furniture remove §eall\n").withHoverEvent(HoverAction.SHOW_TEXT,"§6Remove all furniture and reset database").withClickEvent(ClickAction.SUGGEST_COMMAND, "/furniture remove all")
		.withText("\n").withText("§e§lTIP: §r§7Try to §e§nclick§7 or §e§nhover§7 the commands").
		withText("§7§m+--------------------------------------------------+").sendJson(player);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender,Command cmd, String string,String[] args) {
		if(sender instanceof Player){
			if(cmd!=null&&cmd.getName().equalsIgnoreCase("furniture")){
				if(args.length==1){return Arrays.asList("list","give","debug","manage","recipe","remove");}
				if(args.length==2){
					if(args[0].equalsIgnoreCase("list")){
						return Arrays.asList("type","world","plugin");
					}else if(args[0].equalsIgnoreCase("give")){
						return getProjectNames();
					}else if(args[0].equalsIgnoreCase("remove")){
						List<String> stringList = getProjectNames();
						stringList.add("all");
						stringList.add("distance");
						stringList.add("lookat");
						stringList.add(new Random(10).nextInt()+"");
						return getProjectPlugins(stringList);
					}else if(args[0].equalsIgnoreCase("recipe")){
						return getProjectNames();
					}
				}
			}
		}
		return null;
	}
	
	private List<String> getProjectPlugins(List<String> s){
		for(Project pro : manager.getProjects()){
			if(!s.contains(pro.getPlugin().getName())){
				s.add(pro.getPlugin().getName());
			}
		}
		return s;
	}
	
	private List<String> getProjectNames(){
		List<String> projectName = new ArrayList<String>();
		for(Project pro : manager.getProjects()){
			if(!projectName.contains(pro.getName())){
				projectName.add(pro.getName());
			}
		}
		return projectName;
	}
}
