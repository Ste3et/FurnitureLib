package de.Ste3et_C0st.FurnitureLib.main;

import com.google.gson.JsonObject;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.functions.functionManager;
import de.Ste3et_C0st.FurnitureLib.SchematicLoader.functions.projectFunction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Furniture extends FurnitureHelper implements Listener {

    public Furniture(ObjectID id) {
        super(id);
    }

    public fArmorStand spawnArmorStand(Location loc) {
        return getManager().createArmorStand(getObjID(), loc);
    }

    public abstract void spawn(Location location);

    public boolean runPublicFunctions(Player p) {
        if (!p.isSneaking()) {
            if (p.hasPermission("furniture.sit." + getProject().getName().toLowerCase())) {
            	fEntity entity = getfAsList().stream()
            		.filter(stand -> stand.getName().startsWith("#Mount:") || stand.getName().startsWith("#SITZ"))
            		.filter(stand -> !stand.getPassenger().contains(p.getEntityId()))
            		.sorted((k1,k2) ->  Double.compare(k1.getLocation().distance(p.getLocation()), k2.getLocation().distance(p.getLocation()))).findFirst().orElse(null);
            	
            	if(Objects.nonNull(entity)) {
            		  FurnitureManager.getInstance().getArmorStandFromPassenger(p).stream()
                      .filter(s -> !s.getPassenger().isEmpty())
                      .forEach(s -> s.removePassenger(p.getEntityId()));
            		  entity.setPassenger(p);
            		  return true;
            	}
            }
        }
        return false;
    }

    public boolean hasFunction() {
        List<JsonObject> functions = getObjID().getProjectOBJ().getFunctions();
        if (!functions.isEmpty()) {
            return true;
        } else {
            AtomicBoolean b = new AtomicBoolean(false);
            getfAsList().stream().filter(fEntity::hasCustomName).forEach(as -> {
                if (as.getName().toUpperCase().startsWith("#ITEM") || as.getName().startsWith("/")) {b.set(true);}
                if (as.getName().toUpperCase().startsWith("#LIGHT")) {b.set(true);}
            });
            return b.get();
        }
    }

    public boolean runFunction(Player p) {
        List<JsonObject> functions = getObjID().getProjectOBJ().getFunctions();
        if (!functions.isEmpty()) {
            boolean update = false;
            for (JsonObject function : functions) {
                if (function.has("function") && function.has("data")) {
                    projectFunction pFunction = functionManager.getByName(function.get("function").getAsString());
                    if (Objects.nonNull(pFunction)) {
                        boolean b = pFunction.parse(function.getAsJsonObject("data"), getObjID(), p);
                        if (b) update = true;
                    }
                }
            }
            if (update) {
                return update;
            }
        } else {
            return runOldFunctions(p);
        }
        return false;
    }

    public boolean runOldFunctions(Player p) {
        ItemStack stack = p.getInventory().getItemInMainHand();
        if (stack != null) {
            Material m = stack.getType();
            if (m.equals(Material.AIR) || !m.isBlock()) {
                for (fEntity stand : getfAsList()) {
                    if (stand.getName().startsWith("#ITEM")) {
                        if (stand.getInventory().getItemInMainHand() != null
                                && !stand.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                            ItemStack is = stand.getInventory().getItemInMainHand();
                            is.setAmount(1);
                            getWorld().dropItem(getLocation(), is);
                        }
                        if (p.getInventory().getItemInMainHand() != null) {
                            ItemStack is = p.getInventory().getItemInMainHand().clone();
                            if (is.getAmount() <= 0) {
                                is.setAmount(0);
                            } else {
                                is.setAmount(1);
                            }
                            stand.setItemInMainHand(is);
                            stand.update();
                            consumeItem(p);
                        }
                        return true;
                    }
                    if (stand.getName().startsWith("/")) {
                        if (!stand.getName().startsWith("/op")) {
                            String str = stand.getName();
                            str = str.replaceAll("@player", p.getName());
                            str = str.replaceAll("@uuid", p.getUniqueId().toString());
                            str = str.replaceAll("@world", p.getWorld().getName());
                            str = str.replaceAll("@x", p.getLocation().getX() + "");
                            str = str.replaceAll("@y", p.getLocation().getY() + "");
                            str = str.replaceAll("@z", p.getLocation().getZ() + "");
                            if (str.endsWith("!console!")) {
                                str = str.replaceAll("!console!", "");
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str);
                            } else {
                                p.chat(str);
                            }
                        }
                        return true;
                    }
                }
                this.toggleLight();
            }
        }
        return false;
    }

    public Project getProject() {
        return this.getObjID().getProjectOBJ();
    }
    
    public void receive(Player player) {}
}
