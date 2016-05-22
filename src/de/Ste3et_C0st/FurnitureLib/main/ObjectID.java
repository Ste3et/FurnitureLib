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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import de.Ste3et_C0st.FurnitureLib.Utilitis.RandomStringGenerator;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.MoveType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fArmorStand;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;

public class ObjectID{
	private FurnitureManager manager = FurnitureLib.getInstance().getFurnitureManager();
	private String ObjectID, serial, Project, plugin;
	private List<Location> locList = new ArrayList<Location>();
	private Location loc;
	private UUID uuid;
	private MoveType moving = MoveType.NOTHING;
	private double speed = 0;
	private List<UUID> uuidList = new ArrayList<UUID>();
	private PublicMode publicMode = FurnitureLib.getInstance().getDefaultPublicType();
	private EventType memberType = FurnitureLib.getInstance().getDefaultEventType();
	private SQLAction sqlAction = SQLAction.SAVE;
	private List<fEntity> packetList = new ArrayList<fEntity>();
	private List<Player> players = new ArrayList<Player>();
	private boolean finish=false, fixed=false, fromDatabase=false, Private=false;
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
	public World getWorld(){return this.loc.getWorld();}
	public Chunk getChunk(){return this.loc.getChunk();}
	public List<Player> getPlayerList(){return this.players;}
	public boolean isMember(UUID uuid) {return uuidList.contains(uuid);}
	public void setFromDatabase(){this.fromDatabase=true;}
	public boolean isFromDatabase(){return this.fromDatabase;}
	public boolean isPrivate(){return this.Private;}
	public void addMember(UUID uuid){uuidList.add(uuid);}
	public void remMember(UUID uuid){uuidList.remove(uuid);}
	public List<fEntity> getPacketList() {return packetList;}
	public void setPacketList(List<fEntity> packetList) {this.packetList = packetList;}
	public boolean isInRange(Player player) {return isInWorld(player) && (getStartLocation().distance(player.getLocation()) <= 100D);}
	public boolean isInWorld(Player player) {return getStartLocation().getWorld() == player.getLocation().getWorld();}
	public void addArmorStand(fEntity packet) {packetList.add(packet);}
	public void setPublicMode(PublicMode publicMode){this.publicMode = publicMode;}
	public void setPrivate(boolean b){this.Private = b;}
	public double getSpeed(){return this.speed;}
	public void setSpeed(double f){this.speed = f;}
	public void setMoving(MoveType type){this.moving = type;}
	
	private boolean hasSearch = false;
	
	public void checkDrivable(){
		if(hasSearch == false){
			hasSearch = true;
			getProjectOBJ().checkDriveable(getPacketList());
		}
	}
	
	public boolean isCar(){return getProjectOBJ().isDriveable();}
	
	public fEntity getFront(){
		if(!isCar()) return null;
		if(!hasSearch){checkDrivable();}
		if(isCar()){
			return getPacketList().get(getProjectOBJ().getFront());
		}
		return null;
	}
	
	public fEntity getMiddle(){
		if(!isCar()){return null;}
		if(!hasSearch){checkDrivable();}
		if(isCar()){
			return getPacketList().get(getProjectOBJ().getMiddle());
		}
		return null;
	}
	
	public fEntity getBackside(){
		if(!isCar()) return null;
		if(!hasSearch){checkDrivable();}
		if(isCar()){
			return getPacketList().get(getProjectOBJ().getBackside());
		}
		return null;
	}
	
	public int getMaxSpeed(){
		if(!isCar()) return 0;
		if(!hasSearch){checkDrivable();}
		if(isCar()){
			return getProjectOBJ().getMaxSpeed();
		}
		return 0;
	}
	
	public int getGear(){
		if(!isCar()) return 0;
		if(!hasSearch){checkDrivable();}
		if(isCar()){
			return getProjectOBJ().getGear();
		}
		return 0;
	}
	
	@Deprecated
	public void setVelocity(Vector v){this.loc.add(v);}
	
	public MoveType getMoveType(){return this.moving;}
	
	public void setStartLocation(Location loc) {this.loc = loc;}
	
	public void updatePlayerView(Player player){
		if(isPrivate()){return;}
		if(getPacketList().isEmpty()){return;}
		if(getSQLAction().equals(SQLAction.REMOVE)){return;}
		if(isInRange(player)){
			if(players.contains(player)) return;
			for(fEntity stand : getPacketList()){stand.send(player);}
			players.add(player);
		}else{
			if(!players.contains(player)) return;
			for(fEntity stand : getPacketList()){stand.kill(player, false);}
			players.remove(player);
		}
	}
	
	public void removePacket(Player p){
		if(isPrivate()){return;}
		if(getPacketList().isEmpty()){return;}
		if(getSQLAction().equals(SQLAction.REMOVE)){return;}
		for(fEntity stand : getPacketList()){stand.kill(p, false);}
		players.remove(p);
	}
	
	
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
			if(startLocation!=null){this.loc = startLocation;}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addBlock(List<Block> bl){
		if(bl==null||bl.isEmpty()){return;}
		for(Block b : bl){
			FurnitureLib.getInstance().getBlockManager().addLocation(b.getLocation());
			this.locList.add(b.getLocation());
		}
	}

	
	public void remove(Player p){
		FurnitureLib.getInstance().getLimitManager().removePlayer(this);
		Location loc = getStartLocation();
		dropItem(p, loc.clone().add(0, 1, 0), getProjectOBJ());
		deleteEffect(packetList);
		FurnitureLib.getInstance().getBlockManager().destroy(getBlockList(), false);
		removeAll();
		locList.clear();
		manager.remove(this);
		FurnitureLib.getInstance().getLimitManager().removePlayer(this);
	}
	
	public void remove(Player p,boolean dropItem, boolean deleteEffect){
		FurnitureLib.getInstance().getLimitManager().removePlayer(this);
		Location loc = getStartLocation();
		if(dropItem) dropItem(p, loc.clone().add(0, 1, 0), getProjectOBJ());
		if(deleteEffect) deleteEffect(packetList);
		removeAll();
		manager.remove(this);
		FurnitureLib.getInstance().getLimitManager().removePlayer(this);
	}
	
	public void dropItem(Player p, Location loc, Project porject){
		if(FurnitureLib.getInstance().useGamemode()&&p.getGameMode().equals(GameMode.CREATIVE)){return;}
		World w = loc.getWorld();
		w.dropItemNaturally(loc, porject.getCraftingFile().getRecipe().getResult());
	}
	
	public void deleteEffect(List<fEntity> asList){
		int i = 0;
		try{
			if(asList==null||asList.isEmpty()) return;
			 for (fEntity packet : asList) {
				if(packet!=null && packet instanceof fArmorStand){
					if(packet.getInventory() != null && packet.getInventory().getHelmet()!=null){
						if(packet.getInventory().getHelmet().getType()!=null&&!packet.getInventory().getHelmet().getType().equals(Material.AIR)){
							if(i<6){
								packet.getLocation().getWorld().playEffect(packet.getLocation(), Effect.STEP_SOUND, packet.getInventory().getHelmet().getType());
								i++;
							}
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
	public List<Location> getBlockList() {
		return this.locList;
	}
	
	public void send(Player p){updatePlayerView(p);}
	public void sendAll(){for(Player p : Bukkit.getOnlinePlayers()) send(p);}
	public void update() {
		if(isPrivate()){return;}
		if(getPacketList().isEmpty()){return;}
		if(getSQLAction().equals(SQLAction.REMOVE)){return;}
		for(Player p : getPlayerList()){
			for(fEntity stand : getPacketList()){stand.update(p);}
		}
	}
	
	public void removeAll(){
		if(isPrivate()){return;}
		if(getPacketList().isEmpty()){return;}
		if(getSQLAction().equals(SQLAction.REMOVE)){return;}
		for(Player p : getPlayerList()){
			for(fEntity stand : getPacketList()){stand.kill(p, false);}
		}
		this.players.clear();
	}
}
