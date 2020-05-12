package de.Ste3et_C0st.FurnitureLib.LimitationManager;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.config;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.LimitationType;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LimitationManager {

    public List<LimitationObject> objectList = new ArrayList<LimitationObject>();
    public LimitationType type = null;
    FurnitureLib lib;
    private boolean global = false;

    public LimitationManager(FurnitureLib lib, LimitationType limitationType) {
        this.lib = lib;
        this.type = limitationType;
        loadDefault();
    }

    public void setGlobal(boolean bool) {
        this.global = bool;
    }

    private Integer returnIntProject(Player p, Project pro) {
        if (Objects.isNull(pro)) return 0;
        if(Objects.isNull(p)) return 0;
        return (int) FurnitureManager.getInstance().getFromPlayer(p.getUniqueId()).stream().filter(obj -> obj.getProjectOBJ().equals(pro)).count();
    }

    private Integer returnIntProjectTotal(Player p) {
        return FurnitureManager.getInstance().getFromPlayer(p.getUniqueId()).size();
    }

    private Integer returnIntProjectChunk(Chunk c, Project pro) {
        int i = 0;
        if (pro == null) return i;
        return (int) FurnitureManager.getInstance().getInChunk(c).stream().filter(obj -> obj.getProject().equals(pro.getName())).count();
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

        if (limitOBJ != null) {
            if (limitOBJ.total && limitOBJ.totalAmount == -1) return true;
        }

        if(Objects.isNull(type) || Objects.isNull(pro)) {
        	return true;
        }
        
        if (LimitationType.PLAYER == this.type) {
            int player = returnIntProject(p, pro);
            int playerTotal = returnIntProjectTotal(p);
            int limitGlobal = this.lib.getLimitGlobal();
            //Permissions range check start
            if (limitGlobal > 0) {
                for (int i = limitGlobal; i > 0; i--) {
                    if (p.hasPermission("furniture.globallimit." + i)) {
                        if (playerTotal < i) {
                            String s = lib.getLangManager().getString("message.LimitAnnouncer");
                            s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", player + 1 + "").replace("#MAX#", i + "");
                            p.sendMessage(s);
                            return true;
                        } else {
                            p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.LimitReachedMaximum"));
                            return false;
                        }
                    }
                }
            }
            //Permissions range check end

            int maxPlayer = limitOBJ.getAmountFromType(pro.getName());
            FurnitureLib.debug("LimitationManager -> {Player} " + player + "/" + maxPlayer);
            if (maxPlayer < 0) return true;
            if (player < maxPlayer) {
                String s = lib.getLangManager().getString("message.LimitAnnouncer");
                s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", player + 1 + "").replace("#MAX#", maxPlayer + "");
                p.sendMessage(s);
                return true;
            } else {
                p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.LimitReachedMaximum"));
                return false;
            }
        } else if (LimitationType.WORLD == this.type) {
            int maxWorld = (Objects.nonNull(limitOBJ) && limitOBJ.total) ? limitOBJ.totalAmount : pro.getAmountWorld(obj.getWorld());
            int world = this.global ? FurnitureManager.getInstance().getInWorld(obj.getWorld()).size() : returnProjectWorld(obj.getWorld(), pro);
            FurnitureLib.debug("LimitationManager -> {World} " + world + "/" + maxWorld);
            if (maxWorld < 0) return true;
            if (world < maxWorld) {
                String s = lib.getLangManager().getString("message.LimitAnnouncer");
                s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", world + 1 + "").replace("#MAX#", maxWorld + "");
                p.sendMessage(s);
                return true;
            } else {
                p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.LimitReachedWorld"));
                return false;
            }
        } else if (LimitationType.CHUNK == this.type) {
            int maxChunk = (Objects.nonNull(limitOBJ) && limitOBJ.total) ? limitOBJ.totalAmount : pro.getAmountChunk();
            int chunk = this.global ? FurnitureManager.getInstance().getInChunk(obj.getChunk()).size() : returnIntProjectChunk(obj.getChunk(), pro);
            FurnitureLib.debug("LimitationManager -> {Chunk} " + chunk + "/" + maxChunk);
            if (maxChunk < 0) return true;
            if (chunk < maxChunk) {
                String s = lib.getLangManager().getString("message.LimitAnnouncer");
                s = s.replace("#TYPE#", pro.getName()).replace("#CURRENT#", chunk + 1 + "").replace("#MAX#", maxChunk + "");
                p.sendMessage(s);
                return true;
            } else {
                p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.LimitReachedChunk"));
                return false;
            }
        }
        return true;
    }

    public void loadDefault() {
        if (LimitationType.PLAYER == this.type) {
            config c = new config(lib);
            FileConfiguration file = c.getConfig(this.type.name().toLowerCase(), "/limitation/");
            LimitationObject defaultSection = new LimitationObject(type, "default");
            if (file.isConfigurationSection("PlayerLimit")) {
                for (String s : file.getConfigurationSection("PlayerLimit").getKeys(false)) {
                    if (!s.equalsIgnoreCase("default")) {
                        LimitationObject limitOBJ = new LimitationObject(type, s);
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
        if (LimitationType.PLAYER == this.type) {
            objectList.forEach(obj -> {
                obj.addDefault(project);
                obj.loadProjects(project);
            });
        }
    }

    public LimitationObject getLimitOBJ(Player p, Project project) {
        LimitationObject lobj = null;
        if (LimitationType.PLAYER == this.type) {
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
    
    public LimitationType getType() {
    	return this.type;
    }
}
