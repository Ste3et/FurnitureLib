package de.Ste3et_C0st.FurnitureLib.main.Bed;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

public class sleepAnimation {
	
    private ProtocolManager manager;
    private Set<Player> sleeping = Collections.newSetFromMap(new WeakHashMap<Player, Boolean>());

    public sleepAnimation(){
    	this.manager = ProtocolLibrary.getProtocolManager();
    }
    
    public void toggleSleep(Player player){
    	if (sleeping.add(player)) {
    		playSleepAnimation(player);
    	} else {
            stopSleepAnimation(player);
            sleeping.remove(player);
        }
    }
    
    
    /**
    * Play the sleep animation for every nearby player.
    * @param alseep - the player asleep.
    */
    private void playSleepAnimation(Player asleep) {
        final PacketContainer bedPacket = manager.createPacket(PacketType.Play.Server.BED, false);
        final Location loc = asleep.getLocation();
 
        // [url]http://wiki.vg/Protocol#Use_Bed[/url]
        bedPacket.getEntityModifier(asleep.getWorld()).
            write(0, asleep);
        bedPacket.getIntegers().
            write(1, loc.getBlockX()).
            write(2, loc.getBlockY() + 1).
            write(3, loc.getBlockZ());
 
        broadcastNearby(asleep, bedPacket);
    }
 
    private void stopSleepAnimation(Player sleeping) {
        final PacketContainer animation = manager.createPacket(PacketType.Play.Server.ANIMATION, false);
 
        // [url]http://wiki.vg/Protocol#Animation[/url]
        animation.getEntityModifier(sleeping.getWorld()).
            write(0, sleeping);
        animation.getIntegers().
            write(1, 2);
 
        broadcastNearby(sleeping, animation);
    }
 
    private void broadcastNearby(Player asleep, PacketContainer bedPacket) {
        for (Player observer : manager.getEntityTrackers(asleep)) {
            try {
                manager.sendServerPacket(observer, bedPacket);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Cannot send packet.", e);
            }
        }
    }
}