package de.Ste3et_C0st.FurnitureLib.Database;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTCompressedStreamTools;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagString;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import org.bukkit.Location;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

public class Serializer {

    public static byte[] armorStandtoBytes(NBTTagCompound compound) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            NBTCompressedStreamTools.write(compound, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
        return out.toByteArray();
    }

    public String SerializeObjectID(ObjectID obj) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("EventType", obj.getEventType().toString());
        compound.setString("PublicMode", obj.getPublicMode().toString());
        compound.setString("Owner-UUID", getOwnerUUID(obj));
        compound.set("Members", getMemberList(obj));
        compound.set("Location", getFromLocation(obj.getStartLocation()));
        compound.setInt("ArmorStands", obj.getPacketList().size());
        NBTTagCompound armorStands = new NBTTagCompound();
        obj.getPacketList().stream().filter(Objects::nonNull).forEach(packet -> {
            armorStands.set(packet.getEntityID() + "", packet.getMetaData());
        });
        compound.set("entities", armorStands);
        return Base64.getEncoder().encodeToString(armorStandtoBytes(compound));
    }

    private String getOwnerUUID(ObjectID obj) {
        String s = "NULL";
        if (obj.getUUID() != null) {
            try {
                s = obj.getUUID().toString();
            } catch (Exception e) {
            }

        }
        return s;
    }

    private NBTTagList getMemberList(ObjectID obj) {
        NBTTagList memberList = new NBTTagList();
        for (UUID uuid : obj.getMemberList()) {
            NBTTagString string = new NBTTagString(uuid.toString());
            memberList.add(string);
        }
        return memberList;
    }

    private NBTTagCompound getFromLocation(Location loc) {
        NBTTagCompound location = new NBTTagCompound();
        location.setDouble("X", loc.getX());
        location.setDouble("Y", loc.getY());
        location.setDouble("Z", loc.getZ());
        location.setFloat("Yaw", loc.getYaw());
        location.setFloat("Pitch", loc.getPitch());
        location.setString("World", loc.getWorld().getUID().toString());
        return location;
    }
}