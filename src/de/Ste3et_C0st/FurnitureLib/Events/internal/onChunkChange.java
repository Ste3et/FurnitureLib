package de.Ste3et_C0st.FurnitureLib.Events.internal;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureManager;

public class onChunkChange implements Listener{

	private FurnitureManager manager = FurnitureManager.getInstance();
//	private HashSet<ChunkData> data = new HashSet<ChunkData>();
	
	@EventHandler
	 public void onPlayerMove(PlayerMoveEvent e){
	 		if (e.getTo().getBlock().getLocation().equals(e.getFrom().getBlock().getLocation())) return;
	 		Player player = e.getPlayer();
	 		if (player.getHealth() <= 0.0D) return;
	 		Chunk oldChunk = e.getFrom().getChunk();
	 	    Chunk newChunk = e.getTo().getChunk();
	 		if (!oldChunk.equals(newChunk)) manager.updatePlayerView(player);
	 }
	
//	@EventHandler
//	public void onChunkLoad(ChunkLoadEvent e) {
//		Chunk c = e.getChunk();
//		ChunkData chunkData = data.stream().filter(chunk -> chunk.equals(c)).findFirst().orElse(new ChunkData(c));
//		if(!chunkData.isLoadet()) {
//			data.add(chunkData.load());
//			System.out.println("{x "+ chunkData.getX() +", z "+chunkData.getZ()+", loadet "+chunkData.isLoadet()+"}");
//		}
//	}
	
//	public class ChunkData{
//		
//		private int x, z;
//		private String world;
//		private HashSet<ObjectID> objectSet = new HashSet<ObjectID>();
//		private boolean loadet = false;
//		
//		public ChunkData(Chunk c) {
//			this.x = c.getX();
//			this.z = c.getZ();
//			this.world = c.getWorld().getName();
//		}
//		
//		public ChunkData(int x, int z, String world) {
//			this.x = x;
//			this.z = z;
//			this.world = world;
//		}
//		
//		public ChunkData load() {
//			FurnitureLib.getInstance().getSQLManager().loadAsynchron(getX(), getZ(), getWorld(), new CallbackBoolean() {
//				@Override
//				public void onResult(HashSet<ObjectID> idList) {
//					if(!idList.isEmpty()) {
//						objectSet = idList;
//						idList.stream().forEach(ObjectID::sendAll);
//						System.out.println(idList.size());
//					}
//				}
//			});
//			System.out.println("TPS: " + FurnitureLib.getInstance().getSQLManager().getTPS());
//			this.loadet = true;
//			return this;
//		}
//		
//		public boolean isLoadet() {
//			return this.loadet;
//		}
//		
//		public HashSet<ObjectID> getHashSet(){
//			return this.objectSet;
//		}
//		
//		public int getX() {
//			return this.x;
//		}
//		
//		public int getZ() {
//			return this.z;
//		}
//		
//		public String getWorld() {
//			return this.world;
//		}
//		
//		public boolean equals(Chunk c) {
//			return c.getX() == x && c.getZ() == z && world == c.getWorld().getName();
//		}
//	}
}
