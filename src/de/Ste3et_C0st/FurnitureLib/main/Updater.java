package de.Ste3et_C0st.FurnitureLib.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.entity.Player;

public class Updater {

	private enum UpdatePriority{HIGH,NORMAL,NO_UPDATE}
	private enum CurrentPriority{NORMAL,SNAPSHOT}
	
	String currentVersion = "";
	String newVersion = "N/A";
	String downloadLink = "http://www.spigotmc.org/resources/furniturelibary-free-alpha.9368/download?version=" + newVersion;
	Long updateTime = 0L;
	UpdatePriority priority1 = UpdatePriority.NO_UPDATE;
	CurrentPriority priority2 = CurrentPriority.NORMAL;
	
	public Updater(){
		updateTime = getTime();
		currentVersion = FurnitureLib.getInstance().getDescription().getVersion();
		newVersion = getLatestVersionOnSpigot();
	}
	
	public void update(){
		if(updateTime-System.currentTimeMillis()<=0){
			updateTime = getTime();
			newVersion = getLatestVersionOnSpigot();
		}
	}
	
	public void sendPlayer(Player p){
		if(isUpdateAvaible()){
			p.sendMessage("§7[§6FurnitureLib§7] §6Update §e" + newVersion + " §6is avaible");
			p.sendMessage("§7[§6FurnitureLib§7] §6Lookat: §e" + "http://goo.gl/L7w1QQ");
		}
	}
	
	private boolean isUpdateAvaible(){
		if(!FurnitureLib.getInstance().isUpdate()){return false;}
		if(!currentVersion.equalsIgnoreCase(newVersion)){
			return true;
		}else{
			return false;
		}
	}
	
	public String getLatestString(){
		String s = "";
		if(isUpdateAvaible()){
			return newVersion;
		}
		return s;
	}
	
	public String getUpdate(){
		String s = "";
		if(isUpdateAvaible()){
			s = "\n§c§lUpdate Avaible §2§lv" + newVersion;
		}
		return s;
	}
	
	public long getTime(){return System.currentTimeMillis() + (600000);}
	
	
	private String getLatestVersionOnSpigot() {
        try {
	        URL website = new URL("http://dicecraft.de/API/furnitureLibVersion.txt");
	        URLConnection connection = website.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        StringBuilder response = new StringBuilder();
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) response.append(inputLine);
	        in.close();
	        return response.toString();
        } catch (Exception ex) {
            System.err.println("Failed to check for a update on spigot for FurnitureLib");
        }
        return null;
    }
	
}
