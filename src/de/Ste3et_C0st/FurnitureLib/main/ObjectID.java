package de.Ste3et_C0st.FurnitureLib.main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Utilitis.RandomStringGenerator;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;

public class ObjectID{
	private String ObjectID, serial, Project, plugin;
	private Location loc;
	private Chunk c;
	private World w;
	private UUID uuid;
	private List<UUID> uuidList = new ArrayList<UUID>();
	private PublicMode publicMode = PublicMode.PRIVATE;
	private EventType memberType = EventType.INTERACT;
	private SQLAction sqlAction = SQLAction.SAVE;
	private List<ArmorStandPacket> packetList = new ArrayList<ArmorStandPacket>();
	
	private boolean finish=false, fixed=false;
	public String getID(){return this.ObjectID;}
	public String getProject(){return this.Project;}
	public Project getProjectOBJ(){return FurnitureLib.getInstance().getFurnitureManager().getProject(getProject());}
	public String getPlugin(){return this.plugin;}
	public String getSerial(){return this.serial;}
	public Location getStartLocation(){return this.loc;}
	public EventType getEventType(){return this.memberType;}
	public SQLAction getSQLAction(){return this.sqlAction;}
	public boolean isFixed(){return this.fixed;}
	public boolean isFinish() {return this.finish;}
	public void setFinish(){this.finish = true;}
	public void setEventTypeAccess(EventType type){this.memberType = type;}
	public void setSQLAction(SQLAction action){this.sqlAction=action;}
	public void setFixed(boolean b){fixed=b;}
	public void setMemberList(List<UUID> uuidList){this.uuidList=uuidList;}
	public List<UUID> getMemberList(){return this.uuidList;}
	public PublicMode getPublicMode(){return this.publicMode;}
	public UUID getUUID(){return this.uuid;}
	public World getWorld(){return this.w;}
	public Chunk getChunk(){return this.c;}
	public boolean isMember(UUID uuid) {return uuidList.contains(uuid);}
	public void addMember(UUID uuid){uuidList.add(uuid);}
	public void remMember(UUID uuid){uuidList.remove(uuid);}
	public List<ArmorStandPacket> getPacketList() {return packetList;}
	public void setPacketList(List<ArmorStandPacket> packetList) {this.packetList = packetList;}
	public boolean isInRange(Player player) {return getStartLocation().getWorld() == player.getLocation().getWorld() && (getStartLocation().distance(player.getLocation()) <= 48D);}
	public void addArmorStand(ArmorStandPacket packet) {packetList.add(packet);}
	
	public void setStartLocation(Location loc) {
		this.loc = loc;
		this.w = loc.getWorld();
		this.c = loc.getChunk();
	}
	
	public void setPublicMode(PublicMode publicMode){this.publicMode = publicMode;}
	public void setUUID(UUID uuid){
		if(this.uuid!=null&&!this.uuid.equals(uuid)){
			if(FurnitureLib.getInstance().getLimitManager()!=null){
				FurnitureLib.getInstance().getLimitManager().removePlayer(this);
			}
		}
		if(uuid!=null){
			if(FurnitureLib.getInstance().getLimitManager()!=null){
				FurnitureLib.getInstance().getLimitManager().addPlayer(this.uuid, this);
			}
		}
		this.uuid=uuid;
	}
	
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
			this.manager = FurnitureLib.getInstance().getFurnitureManager();
			if(startLocation!=null){
				this.loc = startLocation;
				this.w = startLocation.getWorld();
				this.c = startLocation.getChunk();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void remove(Player p){
		FurnitureLib.getInstance().getLimitManager().removePlayer(this);
		Location loc = getStartLocation();
		dropItem(p, loc.clone().add(0, 1, 0), getProjectOBJ());
		deleteEffect(manager.getArmorStandPacketByObjectID(this));
		manager.remove(this);
	}
	
	public void remove(Player p,boolean dropItem, boolean deleteEffect){
		FurnitureLib.getInstance().getLimitManager().removePlayer(this);
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
	
	public String getPlayerName(){
		String name = "§cUNKNOW";
		if(uuid!=null){
			OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
			name = p.getName();
		}
		return name;
	}
}
