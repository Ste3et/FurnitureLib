package de.Ste3et_C0st.FurnitureLib.Command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events.ProjectClickEvent;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.ManageInv;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslater;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class command implements CommandExecutor, Listener{

	static FurnitureLib lib = FurnitureLib.getInstance();
	FurnitureManager manager = lib.getFurnitureManager();
	Plugin plugin;
	public static List<Player> playerList = new ArrayList<Player>();
	public static List<Player> manageList = new ArrayList<Player>();
	
	public static List<iCommand> commands = new ArrayList<iCommand>();
	
	public command(Plugin plugin){
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		commands.add(new debugCommand("debug"));
		commands.add(new deleteCommand("delete"));
		commands.add(new downloadCommand("download"));
		commands.add(new giveCommand("give"));
		commands.add(new listCommand("list"));
		commands.add(new manageCommand("manage"));
		commands.add(new purgeCommand("purge"));
		commands.add(new recipeCommand("recipe"));
		commands.add(new reloadCommand("reload"));
		commands.add(new removeCommand("remove"));
		commands.add(new saveCommand("save"));
		commands.add(new spawnCommand("spawn").setHide(true));
		commands.add(new toggleCommand("toggle"));
		commands.add(new versionCommand("version"));
	}
	
	public static void addCommand(iCommand command){
		if(commands.contains(command)) return;
		commands.add(command);
	}
	
	@EventHandler
	public void onArmorRightClick(final ProjectClickEvent e){
		if(playerList.contains(e.getPlayer())){
			e.setCancelled(true);
			Player p = e.getPlayer();
			p.sendMessage("§6Furniture Info about§e " + e.getID().getSerial());
			p.sendMessage("§6Plugin:§e " + e.getID().getPlugin());
			p.sendMessage("§6Class: §c" + e.getID().getProjectOBJ().getFunctionClazz().getSimpleName());
			p.sendMessage("§6Type:§e " + e.getID().getProject());
			p.sendMessage("§6PublicMode:§e " + e.getID().getPublicMode().name().toLowerCase());
			p.sendMessage("§6Owner: §2" + e.getID().getPlayerName());
			p.sendMessage("§6PublicEventAccess: §e" + e.getID().getEventType().name().toLowerCase());
			p.sendMessage("§6ArmorStands: §e" + e.getID().getPacketList().size());
			p.sendMessage("§6FromDatabase: §c" + e.getID().isFromDatabase());
			p.sendMessage("§6Object Finish: §c" + e.getID().isFinish());
			p.sendMessage("§6Members: §e" + e.getID().getMemberList().size());
			p.sendMessage("§6SQL State: §e" + e.getID().getSQLAction().name().toLowerCase());
			p.sendMessage("§6Size: §e" + e.getID().getProjectOBJ().getLength()+":"+ e.getID().getProjectOBJ().getHeight()+":"+e.getID().getProjectOBJ().getWitdh());
			p.sendMessage("§6Blocks: §a" + e.getID().getBlockList().size());
			p.sendMessage("§6Project-Model: §e" + e.getID().getProjectOBJ().isEditorProject());
			p.sendMessage("§6Placeable Side: §e" + e.getID().getProjectOBJ().getPlaceableSide().name());
			if(e.getID().getUUID()!=null){
				OfflinePlayer player = Bukkit.getOfflinePlayer(e.getID().getUUID());
				if(player.hasPlayedBefore()&&!player.isOnline()){
					long mili1 = System.currentTimeMillis();
					long mili2 = player.getLastPlayed();
					long mili3 = mili1-mili2;
					SimpleDateFormat time = new SimpleDateFormat("D:HH:mm:ss.SSS");
			    	String timeStr = time.format(mili3);
			    	p.sendMessage("§6Player Offline: " + timeStr);
				}
			}
			
			for(fEntity stand : e.getID().getPacketList()){
				stand.setGlowing(true);
			}
			final SQLAction action = e.getID().getSQLAction();
			manager.updateFurniture(e.getID());
			playerList.remove(p);
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				for(fEntity stand : e.getID().getPacketList()){
					stand.setGlowing(false);
				}
				manager.updateFurniture(e.getID());
				e.getID().setSQLAction(action);
			}, 20*5);
			
			
		}else if(manageList.contains(e.getPlayer())){
			e.setCancelled(true);
			Player p = e.getPlayer();
			manageList.remove(p);
			if(!e.getID().getUUID().equals(p.getUniqueId())){
				if(!lib.getPermission().hasPerm(p, "furniture.admin") && !p.isOp() && !lib.getPermission().hasPerm(p, "furniture.manage.other")){
					p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.WrongOwner"));
					return;
				}
			}
			new ManageInv(p, e.getID());
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
			if(cmd.getName().equalsIgnoreCase("furniture")){
				if(!sender.hasPermission("furniture.command.help")) return true;
				if(sender instanceof Player) {
					if(args.length > 0) {
						iCommand comm = commands.stream()
								.filter(command -> command.getSubCommand().equalsIgnoreCase(args[0]) || command.getAliasList().contains(args[0].toLowerCase()))
								.findFirst().orElse(null);
						if(comm != null) {
							if(comm.getSubCommand().equalsIgnoreCase("create")) {
								if(lib.getFurnitureManager().getIgnoreList().contains(((Player) sender).getUniqueId())){
									sender.sendMessage(lib.getLangManager().getString("message.FurnitureToggleEvent"));
									return true;
								}
							}
							comm.execute(sender, args);
							return true;
						}
					}else {
						sendHelp((Player) sender);
					}
				}
				return true;
			}
		return false;
	}
	
	public static void sendHelp(Player p){
		if(p==null) return;
		p.spigot().sendMessage(
				new ComponentBuilder(LanguageManager.getInstance().getString("command.help.header"))
					.event(new HoverEvent(Action.SHOW_TEXT, 
						   new ComponentBuilder(LanguageManager.getInstance().getString("command.help.hover",
								   	new StringTranslater("#VERSION#", FurnitureLib.getInstance().getDescription().getVersion()),
								   	new StringTranslater("#AUTHOR#", "Ste3et_C0st")))
						   .create()
					)
				).create()
		);
		
		commands.stream().forEach(str -> {
			if(str.hasCommandPermission(p) && !str.isHide()){
				p.spigot().sendMessage(jsonText(str.getLanguageID()));
			}
		});
		p.sendMessage(LanguageManager.getInstance().getString("command.help.footer"));
	}
	
	public static BaseComponent[] jsonText(String key) {
		String cmd = LanguageManager.getInstance().getString("command." + key + ".help_name");
		String hover = LanguageManager.getInstance().getString("command." + key + ".help_hover");
		return new ComponentBuilder("§6" + cmd).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create())).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(cmd))).create();
	}
	
	public static BaseComponent[] jsonText(String key, de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslater ... stringTranslaters) {
		String cmd = LanguageManager.getInstance().getString("command." + key + ".help_name", stringTranslaters);
		String hover = LanguageManager.getInstance().getString("command." + key + ".help_hover", stringTranslaters);
		return new ComponentBuilder("§6" + cmd).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create())).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(cmd))).create();
	}
	
	public boolean sendMessages(Player p, String helpClass){
		boolean b = commands.stream().filter(str -> !str.getHelpClass().isEmpty() && str.getHelpClass().equalsIgnoreCase(helpClass)).findFirst().isPresent();
		if(!b) return false;
		commands.stream().filter(str -> !str.getHelpClass().isEmpty() && str.getHelpClass().equalsIgnoreCase(helpClass)).forEach(str -> {
			if(str.hasCommandPermission(p, str.getPermissions())) {
				p.spigot().sendMessage(jsonText(str.getLanguageID().replaceAll("commands.", "")));
			}
		});
		return true;
	}
}
