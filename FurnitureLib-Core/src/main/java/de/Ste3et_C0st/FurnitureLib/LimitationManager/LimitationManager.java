package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class LimitationManager {

    private final List<Limitation> limitationListener = Arrays.asList(new ChunkLimitation(), new WorldLimitation(), new PlayerLimitation(), new PermissionLimitation());
	private boolean global = false;
    
    public LimitationManager(FurnitureLib lib, LimitationType ... limitationType) {
    	Stream.of(limitationType).forEach(type -> this.setActivate(true, type));
    }
    
    public void setActivate(boolean bool, LimitationType type) {
    	this.limitationListener.stream().filter(entry -> entry.getEnum() == type).findFirst().ifPresent(limit -> limit.setActivate(bool));
    }
    
    public static File getLimitationFolder() {
    	final File folder = new File(FurnitureLib.getInstance().getDataFolder(), "limitation");
    	if(folder.exists() == Boolean.FALSE) folder.mkdirs();
    	return folder;
    }

    public void setGlobal(boolean bool) {
        this.global = bool;
    }
    
    public boolean isGlobal() {
    	return this.global;
    }

    public boolean canPlace(Player player, ObjectID obj) {
        if (player.isOp()) return true;
        if (FurnitureLib.getInstance().getPermission().hasPerm(player, "furniture.admin")) return true;
        if (FurnitureLib.getInstance().getPermission().hasPerm(player, "furniture.bypass.limit")) return true;
        if (Objects.isNull(obj.getWorld())) return false;
        final Project project = obj.getProjectOBJ();
        final Location location = obj.getStartLocation();
        if(Objects.isNull(project)) return false;
        
        final List<LimitationInforamtion> informationList = new ArrayList<LimitationInforamtion>();
        
        limitationListener.stream().filter(Limitation::isActivate).forEach(entry -> {
        	final int maxSize = entry.getLimit(project, location, player), amountSize = entry.getAmount(entry.buildFilter(obj.getStartLocation(), project, player));
        	final Optional<LimitationInforamtion> inforamtion = entry.buildInforamtion(player, obj.getStartLocation(), project);
        	if(inforamtion.isPresent()) informationList.add(inforamtion.get());
        	FurnitureLib.debug("LimitationManager -> {" + entry.getEnum().name() + "} " + amountSize + "/" + maxSize + " passed");
        });
        
        if(informationList.isEmpty() == false) {
        	Optional<LimitationInforamtion> canceldLimit = informationList.stream().filter(LimitationInforamtion::isCanceld).findFirst();
        	if(canceldLimit.isPresent()) {
        		LimitationInforamtion object = canceldLimit.get();
        		object.sendMessage(player, project, object.getAmount());
            	return false;
        	}else {
        		LimitationInforamtion infoLimit = informationList.stream().sorted((k1,k2) -> Integer.compare(k1.getMax(), k2.getMax())).findFirst().orElse(null);
        		if(Objects.nonNull(infoLimit) && infoLimit.isInfinite() == false) {
        			infoLimit.sendMessage(player, project, infoLimit.getAmount() + 1);
        		}
        	}
        }
        return true;
    }
    
    public List<Limitation> getTypes(){
    	return this.limitationListener;
    }
}
