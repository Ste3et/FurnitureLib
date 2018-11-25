package de.Ste3et_C0st.FurnitureLib.ShematicLoader;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Relative;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fInventory;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.comphenix.protocol.wrappers.EnumWrappers;

public class ProjectMetadata
{
  private NBTTagCompound metadata = new NBTTagCompound();
  
  public void setMetadata(String field, String value)
  {
    this.metadata.setString(field, value);
  }
  
  public void setMetadata(String field, Boolean value)
  {
    setMetadata(field, Integer.valueOf(value.booleanValue() ? 1 : 0));
  }
  
  public void setMetadata(String field, Integer value)
  {
    this.metadata.setInt(field, value.intValue());
  }
  
  public void setMetadata(String field, Byte value)
  {
    this.metadata.setByte(field, value.byteValue());
  }
  
  public void setMetadata(String field, Double value)
  {
    this.metadata.setDouble(field, value.doubleValue());
  }
  
  public void setMetadata(String field, Float value)
  {
    this.metadata.setFloat(field, value.floatValue());
  }
  
  public void setMetadata(String field, Long value)
  {
    this.metadata.setLong(field, value.longValue());
  }
  
  public void setMetadata(String field, Short value)
  {
    this.metadata.setShort(field, value.shortValue());
  }
  
  public void setMetadata(String field, byte[] value)
  {
    this.metadata.setByteArray(field, value);
  }
  
  public void setMetadata(String field, int[] value)
  {
    this.metadata.setIntArray(field, value);
  }
  
  public void setMetadata(Location value, Location Start)
  {
    set("Location", getFromLocation(value, Start));
  }
  
  public void setMetadata(fArmorStand value)
  {
    set("EulerAngle", getEulerAngle(value));
  }
  
  public void setMetadata(fInventory inventory)
  {
    set("Inventory", getFromInventory(inventory));
  }
  
  public void set(String field, NBTTagCompound value)
  {
    this.metadata.set(field, value);
  }
  
  public NBTTagCompound getMetaData(fArmorStand stand, Location Start)
  {
    setMetadata("Name", stand.getName());
    setMetadata("Arms", Boolean.valueOf(stand.hasArms()));
    setMetadata("BasePlate", Boolean.valueOf(stand.hasBasePlate()));
    setMetadata("Fire", Boolean.valueOf(stand.isFire()));
    setMetadata("Invisible", Boolean.valueOf(stand.isInvisible()));
    setMetadata("Small", Boolean.valueOf(stand.isSmall()));
    setMetadata("NameVisible", Boolean.valueOf(stand.isCustomNameVisible()));
    setMetadata("Marker", Boolean.valueOf(stand.isMarker()));
    setMetadata("Glowing", false);
    setMetadata(stand.getLocation(), Start);
    setMetadata(stand.getInventory());
    setMetadata(stand);
    return this.metadata;
  }
  
  private NBTTagCompound getFromLocation(Location loc, Location Start)
  {
	Relative relative = new Relative(loc, Start);
    NBTTagCompound location = new NBTTagCompound();
    location.setDouble("X-Offset", relative.getOffsetX());
    location.setDouble("Y-Offset", relative.getOffsetY());
    location.setDouble("Z-Offset", relative.getOffsetZ());
    location.setFloat("Yaw", loc.getYaw());
    return location;
  }
  
  private NBTTagCompound getEulerAngle(fArmorStand packet)
  {
    NBTTagCompound eulerAngle = new NBTTagCompound();
    Type.BodyPart[] arrayOfBodyPart;
    int j = (arrayOfBodyPart = Type.BodyPart.values()).length;
    for (int i = 0; i < j; i++)
    {
      Type.BodyPart part = arrayOfBodyPart[i];
      EulerAngle angle = packet.getPose(part);
      NBTTagCompound partAngle = new NBTTagCompound();
      partAngle.setDouble("X", angle.getX());
      partAngle.setDouble("Y", angle.getY());
      partAngle.setDouble("Z", angle.getZ());
      eulerAngle.set(part.toString(), partAngle);
    }
    return eulerAngle;
  }
  
  private NBTTagCompound getFromInventory(fInventory fInventory)
  {
    NBTTagCompound inventory = new NBTTagCompound();
    Object[] arrayOfObject;
    int j = (arrayOfObject = EnumWrappers.ItemSlot.values()).length;
    for (int i = 0; i < j; i++)
    {
      Object o = arrayOfObject[i];
      ItemStack is = fInventory.getSlot(o.toString());
      if ((is == null) || (is.getType().equals(Material.AIR))) {
        inventory.setString(o.toString(), "NONE");
      } else {
        try
        {
          inventory.set(o.toString(), new CraftItemStack().getNBTTag(is));
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
    return inventory;
  }
  
  public String toString(fArmorStand stand, Location Start)
  {
    NBTTagCompound nbt = getMetaData(stand, Start);
    return Base64.getEncoder().encodeToString(getByte(nbt));
  }
  
  public byte[] getByte(NBTTagCompound compound)
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try
    {
      NBTCompressedStreamTools.write(compound, out);
      out.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return new byte[0];
    }
    return out.toByteArray();
  }
}