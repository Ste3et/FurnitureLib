package de.Ste3et_C0st.FurnitureLib.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.entity.Player;

public class Updater {

	private enum UpdatePriority{HIGH,NORMAL,NO_UPDATE}
	private enum CurrentPriority{NORMAL,SNAPSHOT}
	
	String currentVersion = "";
	String newVersion = "";
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
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=9368").getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (version.length() <= 7) {
                return version;
            }
        } catch (Exception ex) {
            System.err.println("Failed to check for a update on spigot for FurnitureLib");
        }
        return null;
    }
	
}
