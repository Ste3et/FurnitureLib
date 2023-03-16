package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.NBT.MathHelper;
import de.Ste3et_C0st.FurnitureLib.Utilitis.ExecuteTimer;
import de.Ste3et_C0st.FurnitureLib.Utilitis.RandomStringGenerator;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Wrapper.ChatComponentWrapper;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.util.concurrent.AtomicDouble;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class debugCommand extends iCommand {

	public static HashMap<String, ExecuteTimer> debugMap = new HashMap<String, ExecuteTimer>();
	
    public debugCommand(String subCommand, String... args) {
        super(subCommand);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if(sender.hasPermission("furniture.databasedebug")) {
            	if (args[1].equalsIgnoreCase("database")) {
            		sender.sendMessage("Furniture DebugDatabase mode Started");
            		sender.sendMessage("Please look at the console for instructions");
            		try {
            			String key = RandomStringGenerator.generateRandomString(25, RandomStringGenerator.Mode.ALPHANUMERIC);
            			debugMap.put(key, new ExecuteTimer());
            			CommandSender console = Bukkit.getConsoleSender();
                		console.sendMessage("FurnitureLib: " + sender.getName() + " start the database debug");
                		console.sendMessage("Please enter this command to confirm it: '/furniture debug database " + key + "' you have 60 secounds");
            		}catch (Exception e) {
						e.printStackTrace();
					}
                }else if(args[1].equalsIgnoreCase("regen")) {
                	sender.sendMessage("Furniture ModelFile regen mode Started");
            		sender.sendMessage("Please look at the console for instructions");
            		try {
            			String key = RandomStringGenerator.generateRandomString(25, RandomStringGenerator.Mode.ALPHANUMERIC);
            			debugMap.put(key, new ExecuteTimer());
            			CommandSender console = Bukkit.getConsoleSender();
                		console.sendMessage("FurnitureLib: " + sender.getName() + " start the database debug");
                		console.sendMessage("Please enter this command to confirm it: '/furniture debug regen " + key + "' you have 60 secounds");
            		}catch (Exception e) {
						e.printStackTrace();
					}
                }else if(args[1].equalsIgnoreCase("fixmodel")) {
                	sender.sendMessage("Furniture ModelFile fixmodel mode Started");
            		sender.sendMessage("Please look at the console for instructions");
            		try {
            			String key = RandomStringGenerator.generateRandomString(25, RandomStringGenerator.Mode.ALPHANUMERIC);
            			debugMap.put(key, new ExecuteTimer());
            			CommandSender console = Bukkit.getConsoleSender();
                		console.sendMessage("FurnitureLib: " + sender.getName() + " start the fixmodel mode");
                		console.sendMessage("Please enter this command to confirm it: '/furniture debug fixmodel " + key + "' you have 60 secounds");
            		}catch (Exception e) {
						e.printStackTrace();
					}
                }
            }
        } else if(args.length == 3) {
            	if (args[1].equalsIgnoreCase("database")) {
            		if(sender.hasPermission("furniture.debug.database")) {
	            		String key = args[2];
	            		if(debugMap.containsKey(key)) {
	            			long dif = debugMap.get(key).difference();
	            			if(dif < (60 * 1000)) {
	            				Integer models = 100000;
	                            AtomicInteger aInt = new AtomicInteger(models);
	                            Integer stepSize = 10000;
	                            AtomicInteger aInt2 = new AtomicInteger(stepSize);
	                            ExecuteTimer timer = new ExecuteTimer();
	                            int i = FurnitureManager.getInstance().getProjects().size() - 1;
	                            sender.sendMessage("§7Database Manipulation start");
	                            Player player = (Player) sender;
	                            World w = player.getWorld();
	                            int currentX = player.getLocation().getBlockX();
	                            int currentZ = player.getLocation().getBlockZ();
	                            UUID uuid = player.getUniqueId();
	                            Project pro = FurnitureManager.getInstance().getProject("Chair");
	                            FurnitureManager manager = FurnitureManager.getInstance();
	                            HashSet<ObjectID> objectIdSet = new HashSet<ObjectID>();
	                            new BukkitRunnable() {
	                                @Override
	                                public void run() {
	                                    if (aInt2.get() > 0) {
	                                        while (aInt2.getAndDecrement() > 0) {
	                                            double x = MathHelper.a(new Random(), currentX -1000d, currentX + 1000d);
	                                            double y = MathHelper.a(new Random(), 0, 256);
	                                            double z = MathHelper.a(new Random(), currentZ - 1000d, currentZ + 1000d);
	                                            ObjectID obj = new ObjectID(pro.getName(), pro.getPlugin().getName(), new Location(w, x, y, z));
	                                            FurnitureLib.getInstance().spawn(obj.getProjectOBJ(), obj);
	                                            obj.setSQLAction(SQLAction.SAVE);
	                                            obj.setUUID(uuid);
	                                            objectIdSet.add(obj);
	                                            ChatComponentWrapper.sendChatComponent(player,ChatMessageType.ACTION_BAR, new ComponentBuilder("§2" + aInt.get() + "§7/§e" + 100000).create());
	                                        }
	                                    } else {
	                                    	aInt2.set(stepSize);
	                                        aInt.set(aInt.get() - stepSize);
	                                        if (aInt.get() <= 0) {
	                                        	cancel();
	                                        	manager.addObjectID(objectIdSet);
	                                            sender.sendMessage("§7Database Manipulation §2finish §7" + timer.getDifference());
	                                            sender.sendMessage("§7The Client receive: §e" + models + " §7ObjectIDs");
	                                            sender.sendMessage("§7These are: §e" + (models * pro.getModelschematic().getEntityMap().size()) + " ArmorStands");
	                                        }
	                                    }
	                                }
	                            }.runTaskTimerAsynchronously(FurnitureLib.getInstance(), 0, 2);
	            			}else {
	            				sender.sendMessage("You are to slow to start the database debug !");
	            				sender.sendMessage("Please try again !");
	            			}
	            			debugMap.remove(key);
	            		}else {
	            			sender.sendMessage("There are no Security tokens generated for Database Manipulation !");
	            		}
            		}
            	}else if(args[1].equalsIgnoreCase("regen")) {
            		if(sender.hasPermission("furniture.debug.regen")) {
            			String key = args[2];
	            		if(debugMap.containsKey(key)) {
	            			long dif = debugMap.get(key).difference();
	            			if(dif < (60 * 1000)) {
	            				AtomicDouble aDouble = new AtomicDouble(0);
	            				AtomicInteger integer = new AtomicInteger(0);
	            				sender.sendMessage("§fRegen §dProject Files §fplease wait...");
	            				FurnitureManager.getInstance().getProjects().forEach(entry -> {
	            					aDouble.addAndGet(entry.updateFile());
	            				});
	            				double size = Math.round(aDouble.get() * 10d) / 10d;
	            				sender.sendMessage("§d" + FurnitureManager.getInstance().getProjects().size() + " §fProjects have migrated this save: §d" + size + " §fkb filespace");
	            				sender.sendMessage("§fRegen §aModels §fDatabase please wait...");
	            				
	            				FurnitureManager.getInstance().getObjectList().stream().filter(entry -> SQLAction.NOTHING == entry.getSQLAction()).forEach(entry -> {
	            					entry.setSQLAction(SQLAction.UPDATE);
	            					integer.incrementAndGet();
	            				});
	            				
	            				sender.sendMessage("§d" + integer.get() + " §fModels have migrated.");
	            				sender.sendMessage("§7Please use §9/furniture save §7this can take a short time.");
	            			}
	            		}
            		}
            	}else if(args[1].equalsIgnoreCase("fixmodel")) {
            		String key = args[2];
            		if(debugMap.containsKey(key)) {
            			long dif = debugMap.get(key).difference();
            			if(dif < (60 * 1000)) {
            				if(sender.hasPermission("furniture.debug.fixmodel")) {
                    			ExecuteTimer timer = new ExecuteTimer();
                    			sender.sendMessage("try to fix §d" + FurnitureManager.getInstance().getAllExistObjectIDs().count() + " §fModels");
                    			Bukkit.getScheduler().runTaskAsynchronously(FurnitureLib.getInstance(), () -> {
                    				FurnitureManager.getInstance().getAllExistObjectIDs().filter(Objects::nonNull).forEach(entry -> {
                        				Project project = FurnitureManager.getInstance().getProject(entry.getProject());
                        				if(Objects.nonNull(project)) {
                        					project.fixMetadata(entry);
                            				entry.setSQLAction(SQLAction.UPDATE);
                        				}
                        			});
                        			sender.sendMessage("finish after " + timer.getMilliString());
                        			sender.sendMessage("please use /furniture save");
                    			});
                    		}
            			}
            		}
            	}
        } else {
            if (args.length != 1) {
                getLHandler().sendMessage(sender, "message.WrongArgument");
                return;
            }
            if (sender instanceof Player) {
                if (hasCommandPermission(sender)) {
                    command.playerList.add((Player) sender);
                    getLHandler().sendMessage(sender, "message.DebugModeEntered");
                } else {
                    getLHandler().sendMessage(sender, "message.NoPermissions");
                }
            }
        }
    }
}
