package de.Ste3et_C0st.FurnitureLib.main;

import de.Ste3et_C0st.FurnitureLib.Utilitis.RandomStringGenerator;

public class ObjectID {
	private String ObjectID, serial, Project, plugin;
	
	public String getID(){return this.ObjectID;}
	public String getProject(){return this.Project;}
	public String getPlugin(){return this.plugin;}
	public String getSerial(){return this.serial;}
	
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
	public ObjectID(String name, String plugin){
		try {
			this.Project = name;
			this.plugin = plugin;
			this.serial = RandomStringGenerator.generateRandomString(10,RandomStringGenerator.Mode.ALPHANUMERIC);
			this.ObjectID = name+":"+this.serial+":"+plugin;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
