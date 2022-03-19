package de.Ste3et_C0st.FurnitureLib.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Utilitis.FloodgateManager;

public class FurniturePlayer {

	private final Player player;
	private final UUID uuid;
	private HashSet<ObjectID> receivedObjects = new HashSet<ObjectID>();
	private static final HashMap<UUID, FurniturePlayer> playerSet = new HashMap<UUID, FurniturePlayer>();
	
	public FurniturePlayer(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId();
		playerSet.put(this.uuid, this);
	}

	public Player getPlayer() {
		return player;
	}
	
	public CompletableFuture<Void> updatePlayerView(Location location) {
		String worldName = location.getWorld().getName();
		return makeFuture(() -> {
			HashSet<ObjectID> receivedObjects = new HashSet<ObjectID>();
			HashSet<ObjectID> destroyedObjects = new HashSet<ObjectID>();
			
			receivedObjects.stream().filter(entry -> entry.isInRange(location) == false).forEach(entry -> {
				destroyedObjects.add(entry);
			});
			
			FurnitureManager.getInstance().getObjectStreamFromWorld(worldName).filter(entry -> entry.isInRange(location)).forEach(entry -> {
				if(this.receivedObjects.contains(entry) == false) {
					entry.sendArmorStands(player);
					receivedObjects.add(entry);
				}
			});
			
			this.receivedObjects.removeAll(destroyedObjects);
			this.receivedObjects.addAll(receivedObjects);
		});
	}
	
	public boolean isInWorld(World world) {
		return player.getWorld().equals(world);
	}
	
    private CompletableFuture<Void> makeFuture(final Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (final Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        });
    }
    
    public static HashMap<UUID, FurniturePlayer> getFurniturePlayersMap(){
    	return playerSet;
    }
    
    public static Collection<FurniturePlayer> getFurniturePlayerColection(){
    	return playerSet.values();
    }
	
    public static FurniturePlayer wrap(Player player) {
    	return playerSet.getOrDefault(player, new FurniturePlayer(player));
    }
    
    public boolean containsObjectID(ObjectID objectID) {
    	return this.receivedObjects.contains(objectID);
    }
    
    public HashSet<ObjectID> getReceivedObjects(){
    	HashSet<ObjectID> objHashSet = new HashSet<ObjectID>();
    	objHashSet.addAll(this.receivedObjects);
    	return objHashSet;
    }
    
    public void clear() {
    	this.receivedObjects.clear();
    }
    
    public boolean isBedrockPlayer() {
    	FloodgateManager manager = FurnitureLib.getInstance().getFloodgateManager();
    	if(Objects.nonNull(manager)) {
    		manager.isBedrockPlayer(uuid);
    	}
    	return false;
    }
}
