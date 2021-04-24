package de.Ste3et_C0st.FurnitureLib.Events;

import de.Ste3et_C0st.FurnitureLib.Crafting.Project;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureConfig;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public final class FurnitureItemEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private ItemStack is;
    private Project pro;
    private ObjectID obj;
    private Player p;
    private Location l;
    private BlockFace clickedFace;
    private long timestamp;
    private boolean cancelled;

    public FurnitureItemEvent(Player p, ItemStack is, Project pro, Location l, BlockFace face) {
        this.p = p;
        this.pro = pro;
        this.is = is;
        this.l = l;
        this.clickedFace = face;
        this.obj = getStartLocation();
        this.obj.setUUID(p.getUniqueId());
        this.timestamp = System.currentTimeMillis();
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public BlockFace getFace() {
        return this.clickedFace;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ItemStack getItemStack() {
        return this.is;
    }

    public Project getProject() {
        return this.pro;
    }

    public Player getPlayer() {
        return this.p;
    }

    public Location getLocation() {
        return this.l;
    }

    public ObjectID getObjID() {
        return this.obj;
    }

    public BlockFace getClickedFace() {
        return this.clickedFace;
    }

    private ObjectID getStartLocation() {
        switch (clickedFace) {
            case UP:
                return new ObjectID(pro.getName(), pro.getPlugin().getName(), l.clone().add(0, 1, 0));
            case DOWN:
                return new ObjectID(pro.getName(), pro.getPlugin().getName(), l.clone().add(0, -1, 0));
            default:
                l = FurnitureLib.getInstance().getLocationUtil().getRelative(l.add(0, -1, 0), clickedFace, -1, 0);
                l.setYaw(FurnitureLib.getInstance().getLocationUtil().FaceToYaw(clickedFace) + 180);
                ObjectID obj = new ObjectID(pro.getName(), pro.getPlugin().getName(), l);
                return obj;
        }
    }


    public boolean canBuild() {
        if (p == null || obj == null || getProject() == null) return true;
        BlockFace face = isOnTheRightSide();
        if (face == null) {
            p.sendMessage(LanguageManager.getInstance().getString("message.NotONThisSide"));
            return false;
        }
        this.debugTime("FurnitureLib {FurnitureItemEvent} -> Start canBuild check");
        if (!FurnitureLib.getInstance().getPermManager().canBuild(p, obj.getStartLocation())) {
            FurnitureLib.debug("FurnitureLib -> ProtectionLib can't build here (" + getProject().getName() + ")");
            return false;
        }
        this.debugTime("FurnitureLib {FurnitureItemEvent} -> ProtectionLib("+ FurnitureLib.getInstance().getPermManager().useProtectionLib() +") canBuild on Position = true ");
        if (getBlock() == null) {
            FurnitureLib.debug("FurnitureLib -> Didn't find block (" + getProject().getName() + ")");
            return false;
        }
        if (!FurnitureLib.getInstance().getPermManager().isSolid(getBlock().getType(), getProject().getPlaceableSide(), getBlock())) {
            FurnitureLib.debug("FurnitureLib -> Didn't find solid block (" + getProject().getName() + ")" + getBlock().getType().name());
            return false;
        }
        this.debugTime("FurnitureLib {FurnitureItemEvent} -> placed on solid block = true ");
        if (p.isOp()) return true;
        if (!pro.hasPermissions(p)) {
            FurnitureLib.debug("FurnitureLib -> Player " + p.getName() + " didn't have permissions to place it");
            return false;
        }
        this.debugTime("FurnitureLib {FurnitureItemEvent} -> Player hasPermissions = true ");
        return true;
    }

    public boolean sendAnnouncer() {
        return FurnitureConfig.getFurnitureConfig().getLimitManager().canPlace(p, obj);
    }

    public boolean isTimeToPlace() {
        if (FurnitureConfig.getFurnitureConfig().isSpamPlace()) {
            if (!FurnitureLib.getInstance().getPermission().hasPerm(getPlayer(), "furniture.admin") && !FurnitureLib.getInstance().getPermission().hasPerm(getPlayer(), "furniture.bypass.placeSpam")) {
                long current = System.currentTimeMillis();
                if (FurnitureLib.getInstance().getTimePlace().containsKey(getPlayer().getUniqueId())) {
                    long since = FurnitureLib.getInstance().getTimePlace().get(getPlayer().getUniqueId());
                    long newCurrent = current - since;
                    long dif = FurnitureConfig.getFurnitureConfig().getPlaceTime();
                    if (newCurrent < dif) {
                        String str = FurnitureLib.getInstance().getTimeDif(since, dif, FurnitureConfig.getFurnitureConfig().getTimePattern());
                        String msg = LanguageManager.getInstance().getString("message.FurnitureToFastPlace");
                        msg = msg.replace("#TIME#", str);
                        getPlayer().sendMessage(msg);
                        return false;
                    }
                }
                FurnitureLib.getInstance().getTimePlace().put(getPlayer().getUniqueId(), current);
            }
        }
        return true;
    }

    private Block getBlock() {
        BlockFace face = isOnTheRightSide();
        switch (face) {
            case UP:
                return l.getBlock();
            case DOWN:
                return l.getBlock();
            default:
                return l.getBlock().getRelative(face.getOppositeFace());
        }
    }

    private BlockFace isOnTheRightSide() {
        switch (getProject().getPlaceableSide()) {
            case TOP:
			case WATER:
				if (clickedFace.equals(BlockFace.UP)) {
                    return BlockFace.DOWN;
                }
                break;
            case SIDE:
                if (!(clickedFace.equals(BlockFace.UP) && clickedFace.equals(BlockFace.DOWN))) {
                    if (clickedFace.equals(BlockFace.UP) || clickedFace.equals(BlockFace.DOWN)) {
                        return null;
                    }
                    return clickedFace;
                }
                break;
            case BOTTOM:
                if (clickedFace.equals(BlockFace.DOWN)) {
                    return BlockFace.UP;
                }
                break;
		}
        return null;
    }

    public void removeItem() {
        Boolean useGameMode = FurnitureConfig.getFurnitureConfig().useGamemode();
        if (useGameMode && p.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        int slot = p.getInventory().getHeldItemSlot();
        ItemStack itemStack = is.clone();
        itemStack.setAmount(itemStack.getAmount() - 1);
        p.getInventory().setItem(slot, itemStack);
        p.updateInventory();
    }

    public void finish() {
        this.obj.setFinish();
        FurnitureLateSpawnEvent lateSpawn = new FurnitureLateSpawnEvent(p, obj, pro, l);
        Bukkit.getPluginManager().callEvent(lateSpawn);
    }
    
    public long getEventCallTime() {
    	return Objects.nonNull(this.timestamp) ? this.timestamp : 0;
    }
    
    public void debugTime(String str) {
    	if(FurnitureLib.useDebugMode()) {
    		long timeMS = System.currentTimeMillis() - getEventCallTime();
    		FurnitureLib.debug(str + " [" + timeMS + "ms]");
    	}
    }
}