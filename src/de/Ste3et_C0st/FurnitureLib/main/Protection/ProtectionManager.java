package de.Ste3et_C0st.FurnitureLib.main.Protection;

import de.Ste3et_C0st.FurnitureLib.Events.FurnitureRegionClear;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelBlockAquaticUpdate;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PlaceableSide;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.ProtectionLib.main.ProtectionLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.UUID;

public class ProtectionManager {

    Plugin plugin, FP;
    PluginManager manager;
    FurnitureLib lib;
    HashMap<Player, EventType> playerList = new HashMap<>();

    public ProtectionManager(Plugin plugin) {
        this.lib = FurnitureLib.getInstance();
        this.plugin = plugin;
        this.manager = Bukkit.getPluginManager();
        if (Bukkit.getPluginManager().isPluginEnabled("ProtectionLib")) {
            this.FP = Bukkit.getPluginManager().getPlugin("ProtectionLib");
            new FurnitureRegionClear();
        }
    }

    public boolean isSolid(Material mat, PlaceableSide side, Block block) {
        if (!checkPlaceable(mat, side, block)) {
            return false;
        }
        {
            return mat.isSolid();
        }
    }

    private boolean checkPlaceable(Material mat, PlaceableSide side, Block block) {
        return mat.isBlock() && mat.isSolid() && (mat.isOccluding() || blockStateParser(block));
    }

    private boolean blockStateParser(Block block) {
		return !FurnitureLib.isNewVersion() || ModelBlockAquaticUpdate.isSolid(block);
    }

    public int getSize() {
        if (FP == null) {
            return 0;
        }
        ProtectionLib fp = (ProtectionLib) this.FP;
        return fp.getWatchers().size();
    }

    public boolean useProtectionLib() {
    	return FP != null;
    }

    public boolean canBuild(Player p, Location loc) {
        if (FP == null) {
            return true;
        }
        ProtectionLib fp = (ProtectionLib) this.FP;
        return fp.canBuild(loc, p);
    }

    public Boolean isOwner(Player p, Location loc) {
        if (FP == null) {
            return true;
        }
        ProtectionLib fp = (ProtectionLib) this.FP;
        return fp.isOwner(loc, p);
    }

    public boolean canBuild(Player p, ObjectID id, EventType type) {
        return canBuild(p, id, type, true);
    }

    public boolean canBuild(Player p, ObjectID id, EventType type, boolean sendMessage) {
        if (p.isOp() || FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.bypass.protection") || FurnitureLib.getInstance().getPermission().hasPerm(p, "furniture.admin")) {
            return true;
        }
        PublicMode publicMode = id.getPublicMode();
        UUID userID = p.getUniqueId();
        UUID ownerID = id.getUUID();
        if (ownerID == null) {
            return true;
        }
        boolean b = canBuild(p, id);
        if (b) return true;
        if (publicMode.equals(PublicMode.PRIVATE)) {
            b = ownerID.equals(userID);
        } else if (publicMode.equals(PublicMode.MEMBERS)) {
            if (id.getMemberList().isEmpty()) {
                b = false;
            }
            if (id.getMemberList().contains(userID)) {
                b = isEventType(id, type);
            }
        } else if (publicMode.equals(PublicMode.PUBLIC)) {
            b = isEventType(id, type);
        }
        if (!b && sendMessage) {
            p.sendMessage(FurnitureLib.getInstance().getLangManager().getString("message.NoPermissions"));
        }
        return b;
    }

    private boolean canBuild(Player p, ObjectID id) {
        if (p.getUniqueId().equals(id.getUUID())) return true;
        if (FP == null) return false;
        if (getSize() == 0) return false;
        boolean memberOfRegion = canBuild(p, id.getStartLocation());
        boolean ownerOfRegion = isOwner(p, id.getStartLocation());

        if (memberOfRegion && !ownerOfRegion) {
			return p.getUniqueId().equals(id.getUUID());
		}

        if (ownerOfRegion) {
            if (!p.getUniqueId().equals(id.getUUID())) {
                return true;
            }
        }

        if (memberOfRegion && ownerOfRegion && FurnitureLib.getInstance().haveRegionMemberAccess()) {
            if (!p.getUniqueId().equals(id.getUUID())) {
                return true;
            }
            return true;
        }
        return false;
    }

    private boolean isEventType(ObjectID objID, EventType type) {
		return objID.getEventType().equals(type) || objID.getEventType().equals(EventType.BREAK_INTERACT);
	}
}
