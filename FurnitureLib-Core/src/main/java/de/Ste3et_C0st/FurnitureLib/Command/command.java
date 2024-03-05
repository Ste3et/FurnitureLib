package de.Ste3et_C0st.FurnitureLib.Command;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.Events.ProjectClickEvent;
import de.Ste3et_C0st.FurnitureLib.Utilitis.BoundingBox;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.SchedularHelper;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.Utilitis.cache.DiceOfflinePlayer;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fBlock_display;
import de.Ste3et_C0st.FurnitureLib.main.entity.fDisplay;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import de.Ste3et_C0st.FurnitureLib.main.entity.fInteraction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fSize;

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
		commands.add(new dumpCommand("dump"));
		commands.add(new setName("setName"));
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
			Project project = e.getID().getProjectOBJ();
			
			p.sendMessage("§6Furniture Info about§e " + e.getID().getSerial());
			p.sendMessage("§6Plugin:§e " + e.getID().getPlugin());
			p.sendMessage("§6Class: §c" + e.getID().getFurnitureObject().getClass().getSimpleName());
			p.sendMessage("§6Type:§e " + project.getName());
			p.sendMessage("§6PublicMode:§e " + e.getID().getPublicMode().name().toLowerCase());
			p.sendMessage("§6Owner: §2" + e.getID().getPlayerName());
			p.sendMessage("§6PublicEventAccess: §e" + e.getID().getEventType().name().toLowerCase());
			p.sendMessage("§6Entities: §e" + e.getID().getPacketList().size());
			p.sendMessage("§6FromDatabase: §c" + e.getID().isFromDatabase());
			p.sendMessage("§6Object Finish: §c" + e.getID().isFinish());
			p.sendMessage("§6Members: §e" + e.getID().getMemberList().size());
			p.sendMessage("§6SQL State: §e" + e.getID().getSQLAction().name().toLowerCase());
			p.sendMessage("§6Size: §e[Length:" + project.getLength()+",Height:"+ project.getHeight()+",Width:"+project.getWidth() + "]");
			p.sendMessage("§6Blocks: §a" + e.getID().getBlockList().size());
			p.sendMessage("§6Project-Model: §e" + project.isEditorProject());
			p.sendMessage("§6Placeable Side: §e" + project.getPlaceableSide().name());
			if(e.getID().getUUID()!=null){
				Optional<DiceOfflinePlayer> offlinePlayer = FurnitureLib.getInstance().getPlayerCache().getPlayer(e.getID().getUUID());
				if(offlinePlayer.isPresent()) {
					if(!offlinePlayer.get().isOnline()) {
						long mili1 = System.currentTimeMillis();
						long mili2 = offlinePlayer.get().getLastSeen();
						long mili3 = mili1-mili2;
						SimpleDateFormat time = new SimpleDateFormat("D:HH:mm:ss.SSS");
				    	String timeStr = time.format(mili3);
				    	p.sendMessage("§6Player Offline: " + timeStr);
					}
				}
			}
			
			for(fEntity stand : e.getID().getPacketList()){
				stand.setGlowing(true);
				
				//render boundingboxes!!
				
//				if(stand instanceof fSize) {
//					fSize size = fSize.class.cast(stand);
//					final BoundingBox boundingBox = size.getBoundingBox().shift(stand.getLocation());
//					final ObjectID objectID = e.getID();
//					
//					if(stand instanceof fBlock_display) {
//						new BukkitRunnable() {
//							@Override
//							public void run() {
//								if(objectID.getPacketList().isEmpty() == false) {
//									boundingBox.debugParticle(p.getWorld());
//								}else {
//									this.cancel();
//								}
//							}
//						}.runTaskTimer(lib, 0, 5);
//						
//						System.out.println(boundingBox.toString());
//					}
//				}
//				
//				if(stand instanceof fArmorStand) {
//					final ObjectID objectID = e.getID();
//					new BukkitRunnable() {
//						@Override
//						public void run() {
//							if(objectID.getPacketList().isEmpty() == false) {
//								fArmorStand armorStand = (fArmorStand) stand;
//								armorStand.getTipLocation(BodyPart.RIGHT_ARM).ifPresent(box -> {
//									box.debugParticle(p.getWorld());
//								});
//							}else {
//								this.cancel();
//							}
//						}
//					}.runTaskTimer(lib, 0, 5);;
//				}
			}
			
			final SQLAction action = e.getID().getSQLAction();
			manager.updateFurniture(e.getID());
			playerList.remove(p);
			
			
			
			
			
			SchedularHelper.runLater(() -> {
				for(fEntity stand : e.getID().getPacketList()){
					stand.setGlowing(false);
				}
				manager.updateFurniture(e.getID());
				e.getID().setSQLAction(action);
			}, 20*5, false);
		}else if(manageList.contains(e.getPlayer())){
			e.setCancelled(true);
			Player p = e.getPlayer();
			manageList.remove(p);
			if(!e.getID().getUUID().equals(p.getUniqueId())){
				if(!lib.getPermission().hasPerm(p, "furniture.admin") && !p.isOp() && !lib.getPermission().hasPerm(p, "furniture.manage.other")){
					LanguageManager.getInstance().sendMessage(p, "message.WrongOwner");
					return;
				}
			}
			//new ManageInv(p, e.getID());
			FurnitureLib.getInstance().getInventoryManager().openInventory("manage", e.getPlayer(), e.getID());
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
			if(cmd.getName().equalsIgnoreCase("furniture")){
				/* Deprecated
				if(!sender.hasPermission("furniture.command.help")) return true;
				*/
				if(args.length > 0) {
					iCommand comm = commands.stream()
							.filter(command -> command.getSubCommand().equalsIgnoreCase(args[0]) || command.getAliasList().contains(args[0].toLowerCase()))
							.findFirst().orElse(null);
					if(comm != null) {
						if(comm.getSubCommand().equalsIgnoreCase("create")) {
							if(lib.getFurnitureManager().getIgnoreList().contains(((Player) sender).getUniqueId())){
								LanguageManager.send(sender, "message.FurnitureToggleEvent");
								return true;
							}
						}
						comm.execute(sender, args);
						return true;
					}
				}else {
					sendHelp(sender);
			}
			return true;
			}
		return false;
	}
	
	public static void sendHelp(CommandSender sender){
		if(sender==null) return;
		final Component header = LanguageManager.getInstance().getComponent("command.help.header")
				.hoverEvent(HoverEvent.showText(LanguageManager.getInstance().getComponent("command.help.hover", 
							new StringTranslator("version", FurnitureLib.getInstance().getDescription().getVersion()),
							new StringTranslator("author", "Ste3et_C0st")
						)));
		
		LanguageManager.sendChatMessage(sender, header);
		commands.stream().forEach(str -> {
			if(str.hasCommandPermission(sender) && !str.isHide()){
				LanguageManager.sendChatMessage(sender, jsonText(str.getLanguageID()));
			}
		});
		
		LanguageManager.send(sender, "command.help.footer");
	}
	
	public static Component jsonText(String key, de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator ... stringTranslators) {
		final Component cmd = LanguageManager.getInstance().getComponent("command." + key + ".help_name", stringTranslators);
		final Component hover = LanguageManager.getInstance().getComponent("command." + key + ".help_name", stringTranslators);
		return cmd.color(NamedTextColor.GOLD).hoverEvent(hover).clickEvent(ClickEvent.suggestCommand(PlainTextComponentSerializer.plainText().serialize(cmd)));
	}
	
	public boolean sendMessages(Player p, String helpClass){
		boolean b = commands.stream().filter(str -> !str.getHelpClass().isEmpty() && str.getHelpClass().equalsIgnoreCase(helpClass)).findFirst().isPresent();
		if(!b) return false;
		commands.stream().filter(str -> !str.getHelpClass().isEmpty() && str.getHelpClass().equalsIgnoreCase(helpClass)).forEach(str -> {
			if(str.hasCommandPermission(p, str.getPermissions())) {
				LanguageManager.getInstance().sendChatMessage(jsonText(str.getLanguageID().replaceAll("commands.", "")), p);
			}
		});
		return true;
	}
}
