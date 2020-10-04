package de.Ste3et_C0st.FurnitureLib.ModelLoader;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class ModelVector implements Cloneable {

    private final float yaw, pitch;
    private double x, y, z;

    public ModelVector() {
        this(0, 0, 0, 0, 0);
    }

    public ModelVector(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public ModelVector(double x, double y, double z, float yaw) {
        this(x, y, z, yaw, 0);
    }

    public ModelVector(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public ModelVector(Location loc) {
        this(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public ModelVector(Vector v) {
        this(v.getX(), v.getY(), v.getZ());
    }

    public ModelVector(NBTTagCompound nbtTagCompound) {
        this.x = nbtTagCompound.getDouble("X-Offset");
        this.y = nbtTagCompound.getDouble("Y-Offset");
        this.z = nbtTagCompound.getDouble("Z-Offset");
        this.yaw = nbtTagCompound.getFloat("Yaw");
        this.pitch = nbtTagCompound.getFloat("Pitch");
    }
    
    public NBTTagCompound toNBTTagCompound() {
    	NBTTagCompound tagCompound = new NBTTagCompound();
    	tagCompound.setDouble("X-Offset", this.x);
    	tagCompound.setDouble("Y-Offset", this.y);
    	tagCompound.setDouble("Z-Offset", this.z);
    	tagCompound.setFloat("Yaw", this.yaw);
    	tagCompound.setFloat("Pitch", this.pitch);
    	return tagCompound;
    }

    public ModelVector add(Vector vector) {
        return this.add(vector.getX(), vector.getY(), vector.getZ());
    }

    public ModelVector add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public Location toLocation(World world) {
        return toVector().toLocation(world, yaw, pitch);
    }

    @Override
    public String toString() {
        return "ModelVector [x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ", yaw=" + getYaw() + ", pitch=" + getPitch() + "]";
    }

    @Override
    protected ModelVector clone() throws CloneNotSupportedException {
        return new ModelVector(getX(), getY(), getZ(), getYaw(), getPitch());
    }
    
    public Vector getMinPoint(Vector vector) {
    	double x = Math.min(vector.getX(), this.getX() + .5);
    	double y = Math.min(vector.getY(), this.getY() + 1.975);
    	double z = Math.min(vector.getZ(), this.getZ() + .5);
    	return new Vector(x, y, z);
    }
    
    public Vector getMaxPoint(Vector vector) {
    	double x = Math.max(vector.getX(), this.getX() + .5);
    	double y = Math.max(vector.getY(), this.getY() + 1.975);
    	double z = Math.max(vector.getZ(), this.getZ() + .5);
    	return new Vector(x, y, z);
    }
}
