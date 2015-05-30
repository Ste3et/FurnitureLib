package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class Save {
	
	public String toString(ArmorStandPacket packet){
		Integer id = packet.getEntityId();
		String ObjID = packet.getObjectId().getID();
		String name = packet.getName();
		
		//location
		double X = packet.getLocation().getX();
		double Y = packet.getLocation().getY();
		double Z = packet.getLocation().getZ();
		float yaw = packet.getLocation().getYaw();
		float pitch = packet.getLocation().getPitch();
		String worldName = packet.getLocation().getWorld().getName();
		
		//Inventory
		ArmorStandInventory inv = packet.getInventory();
		ItemStack helmet = inv.getHelmet();
		ItemStack chestplate = inv.getChestPlate();
		ItemStack leggings = inv.getLeggings();
		ItemStack boots = inv.getBoots();
		ItemStack itemInHand = inv.getItemInHand();

		//EulerAngles
		EulerAngle Head = packet.getAngle(BodyPart.HEAD);
		EulerAngle Body = packet.getAngle(BodyPart.BODY);
		EulerAngle LeftArm = packet.getAngle(BodyPart.LEFT_ARM);
		EulerAngle RightArm = packet.getAngle(BodyPart.RIGHT_ARM);
		EulerAngle LeftLeg = packet.getAngle(BodyPart.LEFT_LEG);
		EulerAngle RightLeg = packet.getAngle(BodyPart.RIGHT_LEG);
		
		//Booleans
		boolean small = packet.isMini();
		boolean invisible = packet.isInvisible();
		boolean fire = packet.isFire();
		boolean nameVisible = packet.isNameVisible();
		boolean baseplate = packet.hasBasePlate();
		boolean arms = packet.hasArms();
		boolean grafiti = packet.hasGraviti();
		
		String l = "";
		l+="ArmorStandPacket [Id=" + id + ", ";
		l+="ObjID="+ObjID+", ";
		l+="Name="+name+", ";
		l+="x="+X+", ";
		l+="Y="+Y+", ";
		l+="Z="+Z+", ";
		l+="Yaw="+yaw+", ";
		l+="Pitch="+pitch+", ";
		l+="WorldName="+worldName+", ";
		l+=isToString(helmet, "helmet=");
		l+=isToString(chestplate, "chestplate=");
		l+=isToString(leggings, "leggings=");
		l+=isToString(boots, "boots=");
		l+=isToString(itemInHand, "itemInHand=");
		l+=EularToString(Head, "Head=");
		l+=EularToString(Body, "Body=");
		l+=EularToString(LeftArm, "LeftArm=");
		l+=EularToString(RightArm, "RightArm=");
		l+=EularToString(LeftLeg, "LeftLeg=");
		l+=EularToString(RightLeg, "RightLeg=");
		l+="small="+small+", ";
		l+="invisible="+invisible+", ";
		l+="fire="+fire+", ";
		l+="nameVisible="+nameVisible+", ";
		l+="baseplate="+baseplate+", ";
		l+="arms="+arms+", ";
		l+="grafiti="+grafiti;
		l+="]";
		
		return l;
	}
	
	private String isToString(ItemStack is, String s){
		if(is==null){return s + "NULL, ";}
		return s + is.toString() + ", ";
	}
	
	private String EularToString(EulerAngle is, String s){
		if(is==null){return s + "NULL, ";}
		return s + is.toString() + ", ";
	}
}
