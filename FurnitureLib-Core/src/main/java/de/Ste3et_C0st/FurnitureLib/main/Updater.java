package de.Ste3et_C0st.FurnitureLib.main;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Updater {

    String currentVersion;
    String newVersion;
    Long updateTime;
    UpdatePriority priority1 = UpdatePriority.NO_UPDATE;
    CurrentPriority priority2 = CurrentPriority.NORMAL;
    
    public Updater() {
        updateTime = getTime();
        currentVersion = FurnitureLib.getInstance().getDescription().getVersion();
        newVersion = getLatestVersionOnSpigot();

    }

    public void update() {
        if (updateTime - System.currentTimeMillis() <= 0) {
            updateTime = getTime();
            newVersion = getLatestVersionOnSpigot();
        }
    }

    public void sendPlayer(final Player p) {
        if (isUpdateAvailable()) {
            p.sendMessage("§7[§6FurnitureLib§7] §6Update §e" + newVersion + " §6is available");
            p.sendMessage("§7[§6FurnitureLib§7] §6Look at: §e" + "http://goo.gl/L7w1QQ");
        }
    }

    private boolean isUpdateAvailable() {
        if (FurnitureConfig.getFurnitureConfig().isUpdate()) {
			return !currentVersion.equalsIgnoreCase(newVersion);
        } else {
            return false;
        }
    }

    public String getLatestString() {
        String s = "";
        if (isUpdateAvailable()) {
            return newVersion;
        }
        return s;
    }

    public String getUpdate() {
        String s = "";
        if (isUpdateAvailable()) {
            s = "\n§c§lUpdate Available §2§lv" + newVersion;
        }
        return s;
    }

    public long getTime() {
        return System.currentTimeMillis() + (600000);
    }

    private String getLatestVersionOnSpigot() {
        if (FurnitureConfig.getFurnitureConfig().isUpdate()) {
            try {
                URL website = new URL("https://api.spiget.org/v2/resources/9368/versions/latest");
                URLConnection connection = website.openConnection();
                connection.addRequestProperty("User-Agent", "FurnitureLib");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) response.append(inputLine);
                in.close();
                JsonParser parser = new JsonParser();
                JsonObject o = parser.parse(response.toString()).getAsJsonObject();
                return o.get("name").getAsString();
            } catch (Exception ex) {
                System.err.println("Failed to check for an update on Spigot for FurnitureLib.");
            }
            return null;
        } else {
            return FurnitureLib.getInstance().getDescription().getVersion();
        }
    }

    private enum UpdatePriority {HIGH, NORMAL, NO_UPDATE}


    private enum CurrentPriority {NORMAL, SNAPSHOT}

}
