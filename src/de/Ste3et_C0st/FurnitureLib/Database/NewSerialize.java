package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagIntArray;
import net.minecraft.server.v1_8_R3.NBTTagString;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.google.common.base.Charsets;

import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class NewSerialize {

	/**
	 * @author WolverinGER
	 * Last edit: 21.08.2015
	 */
	
	private NBTTagCompound serelizeItemstack(ItemStack is) {
		if (is == null || is.getType() == Material.AIR)
			return null;
		return CraftItemStack.asNMSCopy(is).save(new NBTTagCompound());
	}
	
	private ItemStack deserelizeItemstack(NBTTagCompound nbt) {
		return CraftItemStack.asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack.createStack(nbt));
	}
	
	private long compress(int a, int b) {
		long base = 0L;
		base += a;
		base = base << 32;
		base += b;
		return base;
	}
	
	private int[] decompress(long l) {
		int[] out = new int[2];
		out[0] = (int) (l >> 32);
		out[1] = (int) (l << 32 >> 32);
		return out;
	}
	
	private NBTTagIntArray serelizeLocation(Location loc) {
		int[] data = new int[(loc.getYaw() != 0F || loc.getPitch() != 0F) ? 8 : 6];
		int c[];
		c = decompress(Double.doubleToLongBits(loc.getX()));
		data[0] = c[0];
		data[1] = c[1];
		
		c = decompress(Double.doubleToLongBits(loc.getY()));
		data[2] = c[0];
		data[3] = c[1];
		
		c = decompress(Double.doubleToLongBits(loc.getZ()));
		data[4] = c[0];
		data[5] = c[1];
		
		if (loc.getYaw() != 0F || loc.getPitch() != 0F) {
			data[6] = Float.floatToIntBits(loc.getYaw());
			data[7] = Float.floatToIntBits(loc.getPitch());
		}
		return new NBTTagIntArray(data);
	}
	
	private Location deserelizeLocation(String world, NBTTagIntArray nbt) {
		World w;
		if ((w = Bukkit.getWorld(world)) == null)
			return null;
		double x = Double.longBitsToDouble(compress(nbt.c()[0], nbt.c()[1]));
		double y = Double.longBitsToDouble(compress(nbt.c()[2], nbt.c()[3]));
		double z = Double.longBitsToDouble(compress(nbt.c()[4], nbt.c()[5]));
		float yaw = 0F;
		float pitch = 0F;
		if (nbt.c().length == 8) {
			yaw = Float.intBitsToFloat(nbt.c()[6]);
			pitch = Float.intBitsToFloat(nbt.c()[7]);
		}
		return new Location(w, x, y, z, yaw, pitch);
	}
	
	private NBTTagIntArray serelizeAngle(EulerAngle a) {
		return serelizeLocation(new Location(null, a.getX(), a.getY(), a.getZ()));
	}
	
	private EulerAngle deserelizeAngle(NBTTagIntArray nbt) {
		Location loc = deserelizeLocation(null, nbt);
		return new EulerAngle(loc.getX(), loc.getY(), loc.getZ());
	}
	
	private NBTTagCompound serelizeAngle(ArmorStandPacket packet) {
		NBTTagCompound nbt = new NBTTagCompound();
		for (BodyPart p : BodyPart.values())
			nbt.set(p.getName(), serelizeAngle(packet.getAngle(p)));
		return nbt;
	}
	
	private ArmorStandPacket deserelizeAngle(ArmorStandPacket packet, NBTTagCompound nbt) {
		for (BodyPart p : BodyPart.values())
			if (nbt.hasKey(p.getName()))
				packet.setPose(deserelizeAngle((NBTTagIntArray) nbt.get(p.getName())), p);
			else
				System.err.println("nbt tag deserelize error: BodyPart \"" + p.getName() + "\" dont contains in NBTTagCompound");
		return packet;
	}
	
	private NBTTagCompound serelizeArmorstandPacket(ArmorStandPacket packet) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.set("oid", new NBTTagString(packet.getObjectId().getID()));
		nbt.set("startloc", serelizeLocation(packet.getObjectId().getStartLocation()));
		nbt.set("world", new NBTTagString(packet.getObjectId().getWorld().getName()));
		
		nbt.set("eid", new NBTTagInt(packet.getEntityId()));
		nbt.set("aid", new NBTTagInt(packet.getArmorID()));
		nbt.set("name", new NBTTagString(packet.getName()));
		nbt.set("loc", serelizeLocation(packet.getLocation()));
		nbt.set("angle", serelizeAngle(packet));
		nbt.set("arms", serelizeBoolean(packet.hasArms()));
		nbt.set("plate", serelizeBoolean(packet.hasBasePlate()));
		nbt.set("gravity", serelizeBoolean(packet.hasGravity()));
		nbt.set("fire", serelizeBoolean(packet.isFire()));
		nbt.set("invisible", serelizeBoolean(packet.isInvisible()));
		nbt.set("mini", serelizeBoolean(packet.isMini()));
		nbt.set("namevisiable", serelizeBoolean(packet.isNameVisible()));
		
		NBTTagCompound inv = new NBTTagCompound();
		inv.set("length", new NBTTagInt(packet.getInventory().getIS().length));
		int c = 0;
		for (ItemStack is : packet.getInventory().getIS()) {
			inv.set("itemstack_" + c, serelizeItemstack(is));
			c++;
		}
		nbt.set("invetory", inv);
		
		return nbt;
	}
	
	private NBTTagByte serelizeBoolean(Boolean b) {
		return new NBTTagByte((byte) (b ? 1 : 0));
	}
	
	private Boolean deserelizeBoolean(NBTBase nbt) {
		return ((NBTTagByte) nbt).f() == 1;
	}
	
	private ArmorStandPacket deserelizeArmorStandPacket(NBTTagCompound nbt) {
		int oid = nbt.getInt("oid");
		if (FurnitureLib.getInstance().getFurnitureManager().getLastID() < oid) {
			FurnitureLib.getInstance().getFurnitureManager().setLastID(oid);
		}
		
		ObjectID objID = null;
		for (ObjectID id : FurnitureLib.getInstance().getFurnitureManager().getObjectList())
			if (id.getID().equalsIgnoreCase(oid + ""))
				objID = id;
		
		if (objID == null)
		objID = new ObjectID("null", "null", null);
		objID.setID(oid + "");
		objID.setStartLocation(deserelizeLocation(nbt.getString("world"), (NBTTagIntArray) nbt.get("startloc")));
		objID.setFinish();
		ArmorStandPacket packet = FurnitureLib.getInstance().getFurnitureManager().createArmorStand(objID, deserelizeLocation(nbt.getString("world"), (NBTTagIntArray) nbt.get("loc")));
		
		packet.setID(nbt.getInt("aid"));
		packet.setName(nbt.getString("name"));
		packet.setArms(deserelizeBoolean(nbt.get("arms")));
		packet.setBasePlate(deserelizeBoolean(nbt.get("plate")));
		packet.setGravity(deserelizeBoolean(nbt.get("gravity")));
		packet.setFire(deserelizeBoolean(nbt.get("fire")));
		packet.setInvisible(deserelizeBoolean(nbt.get("invisible")));
		packet.setSmall(deserelizeBoolean(nbt.get("mini")));
		packet.setNameVasibility(deserelizeBoolean(nbt.get("namevisiable")));
		
		NBTTagCompound inv = nbt.getCompound("invetory");
		for (int i = 0; i < inv.getInt("length"); i++)
			packet.getInventory().setSlot(i, deserelizeItemstack(inv.getCompound("itemstack_" + i)));
		
		deserelizeAngle(packet, nbt.getCompound("angle"));
		objID.setSQLAction(SQLAction.NOTHING);
		return packet;
	}
	
	public ArmorStandPacket createArmorStandPacket(String in) {
		return createArmorStandPacket(in.getBytes(Charsets.UTF_8));
	}
	
	public ArmorStandPacket createArmorStandPacket(byte[] in) {
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(in);
			ArmorStandPacket p = deserelizeArmorStandPacket(NBTCompressedStreamTools.a((InputStream) bin));
			bin.close();
			return p;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] armorStandtoBytes(ArmorStandPacket packet) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			NBTCompressedStreamTools.a(serelizeArmorstandPacket(packet), out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
		return out.toByteArray();
	}
	
	public String armorStandtoString(ArmorStandPacket packet) {
		return new String(armorStandtoBytes(packet), Charsets.UTF_8);
	}
}