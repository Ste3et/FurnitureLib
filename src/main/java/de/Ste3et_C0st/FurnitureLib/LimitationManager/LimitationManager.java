package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.Utilitis.StringTranslator;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class LimitationManager {

    public List<LimitationObject> objectList = new ArrayList<LimitationObject>();
    private final List<LimitationType> limitationListener = new ArrayList<LimitationType>();
    FurnitureLib lib;
    private boolean global = false;

    public LimitationManager(FurnitureLib lib, LimitationType limitationType) {
        this.lib = lib;
        loadDefault();
    }
    
    public LimitationManager(FurnitureLib lib, LimitationType ... limitationType) {
    	this.lib = lib;
    	this.limitationListener.addAll(Arrays.asList(limitationType));
    	this.loadDefault();
    }

    public void setGlobal(boolean bool) {
        this.global = bool;
    }

    private Integer returnIntProject(Player p, Project pro) {
        if (Objects.isNull(pro)) return 0;
        if(Objects.isNull(p)) return 0;
        if(!p.isOnline()) return 0;
        return (int) FurnitureManager.getInstance().getFromPlayer(p.getUniqueId()).stream().filter(obj -> Objects.nonNull(pro) && obj.getProjectOBJ().equals(pro)).count();
    }

    private Integer returnIntProjectTotal(Player p) {
        return FurnitureManager.getInstance().getFromPlayer(p.getUniqueId()).size();
    }
    
    private Integer returnIntProjectChunk(int chunkX, int chunkZ, World world, Project pro) {
        int i = 0;
        if (pro == null) return i;
        return (int) FurnitureManager.getInstance().getInChunkByCoord(chunkX, chunkZ, world).stream().filter(obj -> obj.getProject().equals(pro.getName())).count();
    }

    private Integer returnProjectWorld(World w, Project pro) {
        int i = 0;
        if (w == null) return i;
        if (pro == null) return i;
        return (int) FurnitureManager.getInstance().getInWorld(w).stream().filter(obj -> obj.getProject().equals(pro.getName())).count();
    }

    public boolean canPlace(Player p, ObjectID obj) {
        if (p.isOp()) return true;
        if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.admin")) return true;
        if (FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.bypass.limit")) return true;
        if(Objects.isNull(obj.getWorld())) return false;
        Project pro = obj.getProjectOBJ();
        LimitationObject limitOBJ = getLimitOBJ(p, pro);
        World world = p.getWorld();
        
        if (limitOBJ != null) {
            if (limitOBJ.total && limitOBJ.totalAmount == -1) return true;
        }

        if(Objects.isNull(pro)) {
        	return true;
        }
        
        int limitGlobal = FurnitureConfig.getFurnitureConfig().getLimitGlobal();
        final List<LimitationInforamtion> informationList = new ArrayList<LimitationInforamtion>();
        
        if(limitGlobal > 1) {
        	int playerTotal = returnIntProjectTotal(p);
            if (limitGlobal > 0) {
                for (int i = limitGlobal; i > 0; i--) {
                    if (p.hasPermission("furniture.globallimit." + i)) {
                        informationList.add(new LimitationInforamtion("permission", i, playerTotal));
                    }
                }
            }
        }
        
        for(LimitationType type : this.limitationListener) {
        	final int maxSize, amountSize;
        	
        	switch(type) {
        		case PLAYER:
        			maxSize = limitOBJ.getAmountFromType(pro.getName());
            		amountSize = returnIntProject(p, pro);
        			break;
        		case WORLD:
        			maxSize = (Objects.nonNull(limitOBJ) && limitOBJ.total) ? limitOBJ.totalAmount : pro.getAmountWorld(obj.getWorld());
                	amountSize = this.global ? FurnitureManager.getInstance().getInWorld(world).size() : returnProjectWorld(world, pro);
        			break;
        		case CHUNK:
        			final int xChunk = obj.getStartLocation().getBlockX() >> 4, zChunk = obj.getStartLocation().getBlockZ() >> 4;
                	maxSize = (Objects.nonNull(limitOBJ) && limitOBJ.total) ? limitOBJ.totalAmount : pro.getAmountChunk();
                	amountSize = this.global ? FurnitureManager.getInstance().getInChunkByCoord(xChunk, zChunk, world).size() : returnIntProjectChunk(xChunk, zChunk, world, pro);
        			break;
        		default:
        			maxSize = -1;
        			amountSize = 0;
        			break;
        	}
        	
        	LimitationInforamtion inforamtion = new LimitationInforamtion(type.name().toLowerCase(), maxSize, amountSize);
            informationList.add(inforamtion);
        	FurnitureLib.debug("LimitationManager -> {" + inforamtion.getType() + "} " + amountSize + "/" + maxSize + " passed");
        }
        
        if(informationList.isEmpty() == false) {
        	informationList.stream().sorted((k1,k2) -> Integer.compare(k1.getMax(), k2.getMax())).collect(Collectors.toList());
        	Optional<LimitationInforamtion> canceldLimit = informationList.stream().filter(LimitationInforamtion::isCanceld).findFirst();
        	if(canceldLimit.isPresent()) {
        		LimitationInforamtion object = canceldLimit.get();
        		object.sendMessage(p, pro, object.getAmount());
            	return false;
        	}else {
        		LimitationInforamtion infoLimit = informationList.stream().findFirst().orElse(null);
        		if(Objects.nonNull(infoLimit)) {
        			infoLimit.sendMessage(p, pro, infoLimit.getAmount() + 1);
        		}
        	}
        }
        return true;
    }

    public void loadDefault() {
        if (this.limitationListener.contains(LimitationType.PLAYER)) {
            config c = new config(lib);
            FileConfiguration file = c.getConfig(LimitationType.PLAYER.name().toLowerCase(), "/limitation/");
            LimitationObject defaultSection = new LimitationObject(LimitationType.PLAYER, "default");
            if (file.isConfigurationSection("PlayerLimit")) {
                for (String s : file.getConfigurationSection("PlayerLimit").getKeys(false)) {
                    if (!s.equalsIgnoreCase("default")) {
                        LimitationObject limitOBJ = new LimitationObject(LimitationType.PLAYER, s);
                        if (!objectList.contains(limitOBJ)) {
                            objectList.add(limitOBJ);
                        }
                    }
                }
            }
            if (!objectList.contains(defaultSection)) {
                objectList.add(defaultSection);
            }
        }
    }

    public void loadDefault(String project) {
    	if (this.limitationListener.contains(LimitationType.PLAYER)) {
            objectList.forEach(obj -> {
                obj.addDefault(project);
                obj.loadProjects(project);
            });
        }
    }

    public LimitationObject getLimitOBJ(Player p, Project project) {
        LimitationObject lobj = null;
        if (this.limitationListener.contains(LimitationType.PLAYER)) {
            int i = -1;
            for (LimitationObject obj : this.objectList) {
                if (obj.def) {
                    if (obj.getAmountFromType(project.getName()) >= i) {
                        i = obj.getAmountFromType(project.getName());
                        lobj = obj;
                    }
                } else if (lib.getPermission().hasPerm(p, obj.permission)) {
                    if (obj.getAmountFromType(project.getName()) >= i) {
                        i = obj.getAmountFromType(project.getName());
                        lobj = obj;
                    }
                }
            }
        }
        return lobj;
    }

    public LimitationObject getDefault() {
        for (LimitationObject obj : this.objectList) {
            if (obj.def) {
                return obj;
            }
        }
        return null;
    }
    
    public List<LimitationType> getTypes(){
    	return this.limitationListener;
    }
}
