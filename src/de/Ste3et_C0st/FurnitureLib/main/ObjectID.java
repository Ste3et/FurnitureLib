package de.Ste3et_C0st.FurnitureLib.main;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Utilitis.RandomStringGenerator;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;

public class ObjectID {
	private String ObjectID, serial, Project, plugin;
	private Location loc;
	public String getID(){return this.ObjectID;}
	public String getProject(){return this.Project;}
	public Project getProjectOBJ(){return FurnitureLib.getInstance().getFurnitureManager().getProject(getProject());}
	public String getPlugin(){return this.plugin;}
	public String getSerial(){return this.serial;}
	public Location getStartLocation(){return this.loc;}
	private FurnitureManager manager;
	
	public void setID(String s){
		this.ObjectID = s;
		try{
			if(s.contains(":")){
				String[] l = s.split(":");
				this.Project=l[0];
				this.serial=l[1];
				this.plugin=l[2];
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	public ObjectID(String name, String plugin, Location startLocation){
		try {
			this.Project = name;
			this.plugin = plugin;
			this.serial = RandomStringGenerator.generateRandomString(10,RandomStringGenerator.Mode.ALPHANUMERIC);
			this.ObjectID = name+":"+this.serial+":"+plugin;
			this.loc = startLocation;
			this.manager = FurnitureLib.getInstance().getFurnitureManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setStartLocation(Location loc) {this.loc = loc;}
	
	public void remove(Player p){
		Location loc = getStartLocation();
		dropItem(p, loc.clone().add(0, 1, 0), getProjectOBJ());
		deleteEffect(manager.getArmorStandPacketByObjectID(this));
		manager.remove(this);
	}
	
	public void remove(Player p,boolean dropItem, boolean deleteEffect){
		Location loc = getStartLocation();
		if(dropItem) dropItem(p, loc.clone().add(0, 1, 0), getProjectOBJ());
		if(deleteEffect) deleteEffect(manager.getArmorStandPacketByObjectID(this));
		manager.remove(this);
	}
	
	public void dropItem(Player p, Location loc, Project porject){
		if(FurnitureLib.getInstance().useGamemode()&&p.getGameMode().equals(GameMode.CREATIVE)){return;}
		World w = loc.getWorld();
		w.dropItemNaturally(loc, porject.getCraftingFile().getRecipe().getResult());
	}
	
	
	
	public void deleteEffect(List<ArmorStandPacket> asList){
		try{
			if(asList==null||asList.isEmpty()) return;
			 for (ArmorStandPacket packet : asList) {
				if(packet!=null){
					if(packet.getInventory() != null && packet.getInventory().getHelmet()!=null){
						if(packet.getInventory().getHelmet().getType()!=null&&!packet.getInventory().getHelmet().getType().equals(Material.AIR)){
							packet.getLocation().getWorld().playEffect(packet.getLocation(), Effect.STEP_SOUND, packet.getInventory().getHelmet().getType());
						}
					}
				}
			 }
		}catch(Exception e){}
	}
}
