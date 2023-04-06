package de.Ste3et_C0st.FurnitureLib.SchematicLoader;

import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelHandler;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.Block.ModelBlock;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fContainerEntity;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

public class ProjectLoader extends Furniture {

    public String header;
    private ProjectInventory inv = null;

    public ProjectLoader(ObjectID id) {
        this(id, true);
    }

    public ProjectLoader(ObjectID id, boolean rotate) {
        super(id);
        try {
            ModelHandler schematic = getProject().getModelschematic();
            if (Objects.nonNull(schematic)) {
                if (id.isFromDatabase()) {
                    BlockFace direction = LocationUtil.yawToFace(id.getStartLocation().getYaw()).getOppositeFace();
                    HashMap<Location, ModelBlock> locationMap = schematic.getBlockData(id.getStartLocation(), direction);
                    getObjID().getBlockList().addAll(locationMap.keySet());
                    FurnitureLib.getInstance().getBlockManager().getList().addAll(locationMap.keySet());
                }
            }
            this.registerInventory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerInventory() {
        for (fEntity stand : getfAsList()) {
            if (stand.getName().startsWith("#Inventory:")) {
                if (inv == null) {
                    String[] split = stand.getName().split(":");
                    if (split.length > 1) {
                        int i = Integer.parseInt(split[2].replace("#", ""));
                        this.inv = new ProjectInventory(i, getObjID());
                        this.inv.load();
                    }
                }
            }
        }
    }

    @Override
    public void spawn(Location arg0) {
    }

    @Override
    public void onClick(Player player) {
        if (getObjID() == null) return;
        if (getObjID().getSQLAction().equals(SQLAction.REMOVE)) return;
        if (player == null) return;
        boolean canInteract = canInteract(player, false);
        boolean function = hasFunction();
        FurnitureLib.debug("ProjectLoader -> onClick[project:" + getObjID().getProject() + "]");
        FurnitureLib.debug("ProjectLoader -> onClick[hasFunction:" + function + "]");
        FurnitureLib.debug("ProjectLoader -> onClick[canInteract:" + canInteract + "]");

        if ((function || Objects.nonNull(this.inv)) && canInteract) {
            if (Objects.nonNull(this.inv)) {
            	if(FurnitureLib.getVersionInt() > 16) player.swingMainHand();
                this.inv.openInventory(player);
                return;
            }
            if (runFunction(player)) {
            	if(FurnitureLib.getVersionInt() > 16) player.swingMainHand();
                update();
                return;
            }
        } else if (function) {
            if (!runPublicFunctions(player)) {
                LanguageManager.send(player, "message.NoPermissions");
            }
            return;
        }
        runPublicFunctions(player);
    }

    /**
     * This is the called Function onBreak
     */
    @Override
    public void onBreak(Player player) {
        if (getObjID() == null) return;
        if (getObjID().getSQLAction().equals(SQLAction.REMOVE)) return;
        if (player == null) return;
        if (canBuild(player)) {
        	final Location dropLocation = getLocation().clone().add(0, .5, 0);
        	
            if (Objects.nonNull(this.inv)) Stream.of(inv.getInv().getContents()).filter(Objects::nonNull).forEach(entry -> getWorld().dropItemNaturally(dropLocation, entry));
     
            getfAsList().stream().filter(entity -> entity.getName().equalsIgnoreCase("#ITEM#") || entity.getName().equalsIgnoreCase("#BLOCK#"))
            		.filter(fContainerEntity.class::isInstance)
            		.map(fContainerEntity.class::cast)
                    .forEach(entity -> {
                    	Stream.of(entity.getInventory().getContents()).filter(Objects::nonNull).forEach(stack -> getWorld().dropItemNaturally(dropLocation, stack));
                    });
            
            if(Objects.nonNull(this.inv)) this.inv.getViewers().stream().forEach(HumanEntity::closeInventory);
            
            this.destroy(player);
        }
    }
}
