package de.Ste3et_C0st.FurnitureLib.Command;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.NBT.MathHelper;
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

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class debugCommand extends iCommand {

    public debugCommand(String subCommand, String... args) {
        super(subCommand);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("database")) {
            	//Spawn 1.000.000 Test Chairs on random location only for testing
                AtomicInteger aInt = new AtomicInteger(100000);
                Integer stepSize = 5000;
                AtomicInteger aInt2 = new AtomicInteger(stepSize);
                
                int i = FurnitureManager.getInstance().getProjects().size() - 1;
                sender.sendMessage("§7Database Manipulation start");
                Player player = (Player) sender;
                World w = player.getWorld();
                int currentX = player.getLocation().getBlockX();
                int currentZ = player.getLocation().getBlockZ();
                UUID uuid = player.getUniqueId();
                Project pro = FurnitureManager.getInstance().getProject("Chair");
                FurnitureManager manager = FurnitureManager.getInstance();
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
                                manager.addObjectID(obj);
                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("§2" + aInt.get() + "§7/§e" + 100000).create());
                            }
                        } else {
                            if (aInt.get() > 0) {
                                aInt2.set(stepSize);
                                aInt.set(aInt.get() - stepSize);
                            } else {
                                cancel();
                                sender.sendMessage("§7Database Manipulation §2finish");
                            }
                        }
                    }
                }.runTaskTimerAsynchronously(FurnitureLib.getInstance(), 0, 20);
            }
        } else {
            if (args.length != 1) {
                sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.WrongArgument"));
                return;
            }
            if (sender instanceof Player) {
                if (hasCommandPermission(sender)) {
                    command.playerList.add((Player) sender);
                    sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.DebugModeEntered"));
                } else {
                    sender.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.NoPermissions"));
                }
            }
        }
    }
}
