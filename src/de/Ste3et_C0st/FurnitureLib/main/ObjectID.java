package de.Ste3et_C0st.FurnitureLib.main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Utilitis.RandomStringGenerator;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;
import de.Ste3et_C0st.FurnitureLib.Crafting.Project;

public class ObjectID{
	
	private String ObjectID, serial, Project, plugin, worldName;
	private HashSet<Location> locList = new HashSet<Location>();
	private Location loc;
	private UUID uuid;
	private HashSet<UUID> uuidList = new HashSet<UUID>();
	private PublicMode publicMode = FurnitureLib.getInstance().getDefaultPublicType();
	private EventType memberType = FurnitureLib.getInstance().getDefaultEventType();
	private SQLAction sqlAction = SQLAction.SAVE;
	private List<fEntity> packetList = new ArrayList<fEntity>();
	private HashSet<Player> players = new HashSet<Player>();
	private boolean finish=false, fixed=false, fromDatabase=false, Private=false;

	public String getWorldName(){return this.worldName;}
	public String getID(){return this.ObjectID;}
	public String getProject(){return this.Project;}
	public Project getProjectOBJ(){return FurnitureManager.getInstance().getProject(getProject());}
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
	public void setMemberList(HashSet<UUID> uuidList){this.uuidList=uuidList;}
	public HashSet<UUID> getMemberList(){return this.uuidList;}
	public PublicMode getPublicMode(){return this.publicMode;}
	public UUID getUUID(){return this.uuid;}
	public World getWorld(){return this.loc.getWorld();}
	public Chunk getChunk(){return this.loc.getChunk();}
	public HashSet<Player> getPlayerList(){return this.players;}
	public boolean isMember(UUID uuid) {return uuidList.contains(uuid);}
	public void setFromDatabase(boolean b){this.fromDatabase=b;}
	public boolean isFromDatabase(){return this.fromDatabase;}
	public boolean isPrivate(){return this.Private;}
	public void addMember(UUID uuid){uuidList.add(uuid);}
	public void remMember(UUID uuid){uuidList.remove(uuid);}
	public List<fEntity> getPacketList() {return packetList;}
	public void setPacketList(List<fEntity> packetList) {this.packetList = packetList;}
	public boolean isInRange(Player player) {return (getStartLocation().distance(player.getLocation()) <= viewDistance);}
	public boolean isInWorld(Player player) {return getWorld().equals(player.getWorld());}
	public void addArmorStand(fEntity packet) {packetList.add(packet);}
	public void setPublicMode(PublicMode publicMode){this.publicMode = publicMode;}
	public void setPrivate(boolean b){this.Private = b;}
	public Object functionObject = null;
	
	public int viewDistance = 100;
	
	public void setStartLocation(Location loc) {this.loc = loc;this.worldName = loc.getWorld().getName();}
	
	public void updatePlayerView(Player player){
		if(FurnitureManager.getInstance().getIgnoreList().contains(player.getUniqueId())){return;}
		if(isPrivate()){return;}
		if(getPacketList().isEmpty()){return;}
		if(getSQLAction().equals(SQLAction.REMOVE)){return;}
		//player.sendMessage(getID());
		//if(!isFinish()) return;
		if(!isInWorld(player)){
			if(players.contains(player)) players.remove(player);
			return;
		}else{
			if(isInRange(player)){
				if(players.contains(player)){return;}
				this.packetList.stream().forEach(stand -> stand.send(player));
				players.add(player);
			}else{
				if(!players.contains(player)) return;
				this.packetList.stream().forEach(stand -> stand.kill(player, false));
				players.remove(player);
			}
		}
	}
	
	public Object getFunctionObject() {
		return this.functionObject;
	}
	
	public void setFunctionObject(Object obj) {
		this.functionObject = obj;
	}
	
	public void callFunction(String function, Player player) {
		if(getFunctionObject() == null) return;
		try {
			Method m = getFunctionObject().getClass().getDeclaredMethod(function, Player.class);
			m.invoke(getFunctionObject(), player);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void removePacket(Player p){
		if(isPrivate()){return;}
		if(getPacketList().isEmpty()){return;}
		if(getSQLAction().equals(SQLAction.REMOVE)){return;}
		this.packetList.stream().forEach(stand -> stand.kill(p, false));
		players.remove(p);
	}
	
	
	public void setUUID(UUID uuid){
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
			if(Objects.nonNull(startLocation)) setStartLocation(startLocation);
			this.viewDistance = FurnitureLib.getInstance().getViewDistance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addBlock(List<Block> bl){
		if(bl==null||bl.isEmpty()){return;}
		for(Block b : bl){
			FurnitureLib.getInstance().getBlockManager().addBlock(b);
			this.locList.add(b.getLocation());
		}
	}

	
	public void remove(Player p){remove(p, true, true);}
	public void remove(){remove(null, false, false);}
	public void remove(boolean effect){remove(null, false, effect);}
	
	public void remove(Player p,boolean dropItem, boolean deleteEffect){
		this.packetList.stream().filter(entity -> entity.isFire()).forEach(entity -> entity.setFire(false));
		if(p!=null) if(dropItem) dropItem(p, loc.clone().add(0, 1, 0), getProjectOBJ());
		if(deleteEffect) deleteEffect(packetList);
		removeAll();
		FurnitureManager.getInstance().remove(this);
		this.setFunctionObject(null);
	}
	
	public void dropItem(Player p, Location loc, Project porject){
		if(FurnitureLib.getInstance().useGamemode()&&p.getGameMode().equals(GameMode.CREATIVE)){return;}
		World w = loc.getWorld();
		w.dropItemNaturally(loc, porject.getCraftingFile().getRecipe().getResult());
	}
	
	public void deleteEffect(List<fEntity> asList){
		if(getProjectOBJ().isSilent()){return;}
		asList.stream().filter(entity -> entity.getHelmet() != null && entity.getHelmet().getType().isBlock()).limit(6).forEach(
				entity -> entity.sendParticle()
		);
	}

	public String getPlayerName(){
		String name = "§cUNKNOW";
		if(uuid!=null){
			OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
			name = p.getName();
		}
		return name;
	}
	public HashSet<Location> getBlockList() {return this.locList;}
	public void send(Player p){updatePlayerView(p);}
	public void sendAll(){for(Player p : Bukkit.getOnlinePlayers()) send(p);}
	
	public void update() {
		if(isPrivate()){return;}
		if(getPacketList().isEmpty()){return;}
		if(getSQLAction().equals(SQLAction.REMOVE)){return;}
		for(Player p : getPlayerList()) getPacketList().stream().forEach(entity -> entity.update(p));
	}
	
	public void removeAll(){
		if(isPrivate()){return;}
		if(getPacketList().isEmpty()){return;}
		if(getSQLAction().equals(SQLAction.REMOVE)){return;}
		for(Player p : getPlayerList()) getPacketList().stream().forEach(entity -> entity.kill(p, false));
		this.players.clear();
	}
	
}
