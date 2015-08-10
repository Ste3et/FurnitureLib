package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import de.Ste3et_C0st.FurnitureLib.main.ArmorStandPacket;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

public class Serialize{
  private final ItemStack AIR = new ItemStack(Material.AIR);
  public String toBase64(ItemStack is){
	  try {
  		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
		if(is==null) is=AIR;
		dataOutput.writeObject(is);
		dataOutput.close();
        return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
  }

  public ItemStack fromBase64(String s){
		try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(s));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack is = (ItemStack) dataInput.readObject();
            dataInput.close();
            return is;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
  
  public String[] toArmorStandString(ArmorStandPacket as){
	  String s[] = new String[16];
	  
	  ObjectID objID = as.getObjectId();
	  
	  Location loc = as.getLocation();
	  String[] inventory = as.getInventory().getStringInv();
	  Boolean a = as.hasArms();
	  Boolean b = as.hasBasePlate();
	  Boolean c = as.hasGravity();
	  Boolean d = as.isFire();
	  Boolean e = as.isInvisible();
	  Boolean f = as.isMini();
	  Boolean g = as.isNameVisible();
	  
	  String id = as.getObjectId().getID();
	  String metadata = as.getName();
	  String bool = BtI(a) + ";" + BtI(b) + ";" + BtI(c) + ";" + BtI(d) + ";" + BtI(e) + ";" + BtI(f) + ";" + BtI(g);
	  String location = locationToString(loc);
	  String locationObjectID = locationToString(objID.getStartLocation());
	  s[0] = id;
	  s[1] = metadata;
	  s[2] = bool;
	  s[3] = location;
	  s[4] = locationObjectID;
	  
	  int i=0;
	  for(String str : inventory){
		  s[5+i] = str;
		  i++;
	  }
	  
	  //3-6
	  i=0;
	  for(BodyPart part : BodyPart.getList()){
		  EulerAngle angle = as.getAngle(part);
		  String rotation = angle.getX() + ";" + angle.getY() + ";" + angle.getZ();
		  s[10+i] = rotation;
		  i++;
	  }
	  return s;
  }
  
  private String locationToString(Location loc){
	  return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
  }
  
  private Location fromStringToLocation(String[] loc){
	  Location location = new Location(Bukkit.getWorld(loc[0]), Double.parseDouble(loc[1]), Double.parseDouble(loc[2]), Double.parseDouble(loc[3]));
	  location.setYaw(Float.parseFloat(loc[4]));
	  location.setPitch(Float.parseFloat(loc[5]));
	  return location;
  }
  
  private ObjectID getObjectID(String s){
	  for(ObjectID id : FurnitureLib.getInstance().getFurnitureManager().getObjectList()){
		  if(id.getID().equalsIgnoreCase(s)){
			  return id;
		  }
	  }return null;
  }
  
  public ArmorStandPacket fromArmorStandString(String[] s){

	  String id = s[0];
	  
	  if(FurnitureLib.getInstance().getFurnitureManager().getLastID()<Integer.parseInt(id)){
		  FurnitureLib.getInstance().getFurnitureManager().setLastID(Integer.parseInt(id));
	  }
	  
	  String objI = s[1];
	  String metdata = s[2];
	  
	  ObjectID ObjID = null;
	  
	  if(getObjectID(objI)==null){
		  ObjID = new ObjectID("null", "null", null);
	  }else{
		  ObjID = getObjectID(objI);
	  }
	  
	  String[] bool = s[3].split(";");
	  String[] loc = s[4].split(";");
	  String[] locObjectID = s[5].split(";");
	  
	  Boolean arms = ItB(Integer.parseInt(bool[0]));
	  Boolean basePlate = ItB(Integer.parseInt(bool[1]));
	  Boolean gravity = ItB(Integer.parseInt(bool[2]));
	  Boolean fire = ItB(Integer.parseInt(bool[3]));
	  Boolean invisible = ItB(Integer.parseInt(bool[4]));
	  Boolean mini = ItB(Integer.parseInt(bool[5]));
	  Boolean namevisible = ItB(Integer.parseInt(bool[6]));
	  
	  Location location = fromStringToLocation(loc);
	  Location locationObjectID = fromStringToLocation(locObjectID);
	  
	  ItemStack itemInHand = fromBase64(s[6]);
	  ItemStack boots = fromBase64(s[7]);
	  ItemStack chestplate = fromBase64(s[8]);
	  ItemStack leggings = fromBase64(s[9]);
	  ItemStack helm = fromBase64(s[10]);
	  
	  String sHead[] = s[11].split(";");
	  String sBody[] = s[12].split(";");
	  String sLeft_Arm[] = s[13].split(";");
	  String sRight_Arm[] = s[14].split(";");
	  String sLeft_Leg[] = s[15].split(";");
	  String sRight_Leg[] = s[16].split(";");
	  
	  EulerAngle head = fromStringArray(sHead);
	  EulerAngle Body = fromStringArray(sBody);
	  EulerAngle Left_Arm = fromStringArray(sLeft_Arm);
	  EulerAngle Right_Arm = fromStringArray(sRight_Arm);
	  EulerAngle Left_Leg = fromStringArray(sLeft_Leg);
	  EulerAngle Right_Leg = fromStringArray(sRight_Leg);
	  
	  ObjID.setID(objI);
	  ObjID.setStartLocation(locationObjectID);
	  
	  ArmorStandPacket asPacket = FurnitureLib.getInstance().getFurnitureManager().createArmorStand(ObjID, location);
	  asPacket.setID(Integer.parseInt(id));
	  asPacket.setName(metdata);
	  asPacket.setArms(arms);
	  asPacket.setBasePlate(basePlate);
	  asPacket.setGravity(gravity);
	  asPacket.setFire(fire);
	  asPacket.setInvisible(invisible);
	  asPacket.setSmall(mini);
	  asPacket.setNameVasibility(namevisible);
	  asPacket.getInventory().setItemInHand(itemInHand);
	  asPacket.getInventory().setHelmet(helm);
	  asPacket.getInventory().setChestPlate(chestplate);
	  asPacket.getInventory().setLeggings(leggings);
	  asPacket.getInventory().setBoots(boots);
	  asPacket.setPose(head, BodyPart.HEAD);
	  asPacket.setPose(Body, BodyPart.BODY);
	  asPacket.setPose(Left_Arm, BodyPart.LEFT_ARM);
	  asPacket.setPose(Right_Arm, BodyPart.RIGHT_ARM);
	  asPacket.setPose(Left_Leg, BodyPart.LEFT_LEG);
	  asPacket.setPose(Right_Leg, BodyPart.RIGHT_LEG);
	  FurnitureLib.getInstance().getFurnitureManager().getPreLoadetList().add(ObjID);
	  return asPacket;
  }
  
  private EulerAngle fromStringArray(String[] s){
	  return new EulerAngle(Double.parseDouble(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]));
  }
  
  private int BtI(boolean b){
  	if(b) return 1;
  	return 0;
  }
  
  private boolean ItB(int i){
  	if(i==1) return true;
  	return false;
  }
}
