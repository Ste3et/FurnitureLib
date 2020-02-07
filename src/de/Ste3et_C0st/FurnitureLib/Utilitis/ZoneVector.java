package de.Ste3et_C0st.FurnitureLib.Utilitis;

import org.bukkit.Location;

public class ZoneVector {

    private int x;
    private int y;
    private int z;

    public ZoneVector(int x, int y, int z) {
        this.x = x;
        this.z = z;
        this.y = y;
    }

    public ZoneVector(Location startLocation) {
        this.x = startLocation.getBlockX();
        this.z = startLocation.getBlockZ();
        this.y = startLocation.getBlockY();
    }

    public boolean isInAABB(ZoneVector min, ZoneVector max) {
        return ((this.x <= max.x) && (this.x >= min.x) && (this.z <= max.z) && (this.z >= min.z) && (this.y <= max.y) && (this.y >= min.y));
    }
}
