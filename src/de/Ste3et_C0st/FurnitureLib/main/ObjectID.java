package de.Ste3et_C0st.FurnitureLib.main;

import org.bukkit.Location;

import de.Ste3et_C0st.FurnitureLib.Utilitis.RandomStringGenerator;

public class ObjectID {
	private String ObjectID, serial, Project, plugin;
	private Location loc;
	public String getID(){return this.ObjectID;}
	public String getProject(){return this.Project;}
	public String getPlugin(){return this.plugin;}
	public String getSerial(){return this.serial;}
	public Location getStartLocation(){return this.loc;}
	
	public void setID(String s){
		this.ObjectID = s;
		try{
			if(s.contains(":")){
				String[] l = s.split(":");
				this.Project=l[0];
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setStartLocation(Location loc) {this.loc = loc;}
}
