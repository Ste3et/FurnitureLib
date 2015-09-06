package de.Ste3et_C0st.FurnitureLib.Utilitis;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;

public class ManageInv  implements Listener{
	Plugin plugin;
	Player p;
	ObjectID obj;
	Inventory inv;
	LanguageManager lang = FurnitureLib.getInstance().getLangManager();
	Object[] objects = new Object[45];
	int side = 1;
	String s;
	boolean update = false;
	public ManageInv(Player p, ObjectID obj){
		this.plugin = FurnitureLib.getInstance();
		this.obj = obj;
		this.p = p;
		buildHub();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	private void buildHub(){
		if(p==null){return;}
		this.inv = Bukkit.createInventory(null, 27, lang.getName("manageInvName"));
		setItemHub();
		p.openInventory(inv);
		p.updateInventory();
	}
	
	@EventHandler
	private void onClick(InventoryClickEvent e){
		if(p==null){return;}
		if(p.equals(e.getWhoClicked())){
			if(e.getCurrentItem()==null){return;}
			if(e.getClickedInventory()==null){return;}
			if(!e.getClickedInventory().equals(inv)){e.setCancelled(true);return;}
			if(e.getCurrentItem().getType().equals(Material.AIR)){return;}
			p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
			e.setCancelled(true);
			ItemStack is = e.getCurrentItem();
			ItemStack a = obj.getPublicMode().getItemStack();
			ItemStack b = obj.getEventType().getItemStack();
			ItemStack c = getItemStack("add");
			ItemStack d = getItemStack("remove");
			ItemStack f = getItemStack("setOwner");
			ItemStack prevPage = getItemStack("PrevPage");
			ItemStack nextPage = getItemStack("NextPage");
			String invTitle = inv.getName();
			String addTitle = lang.getName("playerAddInvName");
			String remTitle = lang.getName("playerRemoveInvName");
			String setTitle = lang.getName("playerSetInvName");
			if(is.equals(a)){
				switch (obj.getPublicMode()) {
				case PRIVATE:
					obj.setPublicMode(PublicMode.MEMBERS);
					break;
				case MEMBERS:
					obj.setPublicMode(PublicMode.PUBLIC);
					break;
				case PUBLIC:
					obj.setPublicMode(PublicMode.PRIVATE);
					break;
				}
				setItemHub();
				return;
			}else if(is.equals(b)){
				switch (obj.getEventType()) {
				case INTERACT:
					obj.setEventTypeAccess(EventType.BREAK);
					break;
				case BREAK:
					obj.setEventTypeAccess(EventType.BREAK_INTERACT);
					break;
				case BREAK_INTERACT:
					obj.setEventTypeAccess(EventType.NONE);
					break;
				case NONE:
					obj.setEventTypeAccess(EventType.INTERACT);
					break;
				default:break;
			}
				setItemHub();
				return;
			}else if(is.equals(c)){
				update("playerAddInvName");
				return;
			}else if(is.equals(nextPage)){
				Integer max = getMaxSides(objects);
				if(side<max){
					side++;
					update(s);
				}
			}else if(is.equals(prevPage)){
				if(side>1){
					side--;
					update(s);
				}
				return;
			}else if(invTitle.equalsIgnoreCase(addTitle)){
				if(is.getType().equals(Material.SKULL_ITEM)){
					UUID uuid = (UUID) objects[e.getSlot()];
					obj.addMember(uuid);
					update(s);
				}
				return;
			}else if(invTitle.equalsIgnoreCase(remTitle)){
				if(is.getType().equals(Material.SKULL_ITEM)){
					UUID uuid = (UUID) objects[e.getSlot()];
					obj.remMember(uuid);
					update(s);
				}
				return;
			}else if(is.equals(d)){
				update("playerRemoveInvName");
				return;
			}else if(is.equals(f)){
				update("playerSetInvName");
				return;
			}else if(invTitle.equalsIgnoreCase(setTitle)){
				if(is.getType().equals(Material.SKULL_ITEM)){
					UUID uuid = (UUID) objects[e.getSlot()];
					obj.setUUID(uuid);
					update(s);
				}
				return;
			}
		}
	}
	
	private void update(String s){
		if(p==null){return;}
		if(inv==null){return;}
		update = true;
		p.closeInventory();
		this.s = s;
		this.inv = Bukkit.createInventory(null, 54, lang.getName(s));
		clear();
		if(!s.equalsIgnoreCase("playerRemoveInvName")){
			objects = getAvaiblePlayer(Bukkit.getOnlinePlayers().toArray(), true);
		}else{
			objects = getAvaiblePlayer(obj.getMemberList().toArray(), false);
		}
		
		addPanel(objects);
		setPlayer(objects);
		p.openInventory(inv);
		p.updateInventory();
		update = false;
	}
	
	private void clear(){
		if(p==null){return;}
		for(int i = 0; i<inv.getSize();i++){
			inv.setItem(i, new ItemStack(Material.AIR));
		}
	}
	
	public void setPlayer(Object[] objects){
		if(p==null){return;}
		for(UUID uuid : getPlayerList(objects, side)){
			ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
			SkullMeta skull = (SkullMeta) itemStack.getItemMeta();
			Player p = null;
			if(Bukkit.getPlayer(uuid)!=null){
				p = Bukkit.getPlayer(uuid);
			}else if(Bukkit.getOfflinePlayer(uuid)!=null){
				p = Bukkit.getOfflinePlayer(uuid).getPlayer();
			}else{
				this.p.sendMessage("§c§lERROR");
				break;
			}
			if(!obj.getUUID().equals(uuid)){
				skull.setOwner(p.getName());
				skull.setDisplayName(p.getName());
				itemStack.setItemMeta(skull);
				itemStack.setDurability((short) 3);
				inv.addItem(itemStack);
			}
		}
	}
	
	private Player getPlayerFromUUID(UUID uuid){
		if(Bukkit.getPlayer(uuid)!=null){
			return Bukkit.getPlayer(uuid);
		}else if(Bukkit.getOfflinePlayer(uuid)!=null){
			return Bukkit.getOfflinePlayer(uuid).getPlayer();
		}
		return null;
	}
	
	public Object[] getAvaiblePlayer(Object[] objects, Boolean b){
			List<UUID> pList = new ArrayList<UUID>();
			for(Object p : objects){
				Player player = null;
				if(p instanceof Player){player = (Player) p;}
				if(p instanceof UUID){player = getPlayerFromUUID((UUID) p);}
				UUID uuid = player.getUniqueId();
				if(!obj.isMember(uuid)||!b){
					if(!uuid.equals(obj.getUUID())){
						pList.add(uuid);
					}
				}
			}
			return pList.toArray();
	}
	
	public void addPanel(Object[] objects){
		if(p==null){return;}
		ItemStack prevPage = getItemStack("PrevPage");
		ItemStack nextPage = getItemStack("NextPage");
		ItemStack page = getItemStack("PageItem");
		ItemMeta im = page.getItemMeta();
		String s = im.getDisplayName();
		s = s.replace("#CURPAGE#", side + "");
		s = s.replace("#MAXPAGE#", getMaxSides(objects) + "");
		im.setDisplayName(s);
		page.setItemMeta(im);
		inv.setItem(47, prevPage);
		inv.setItem(49, page);
		inv.setItem(51, nextPage);
	}
	
	public List<UUID> getUUIDList(Object[] objects, Integer page){
		List<UUID> uuidList = new ArrayList<UUID>();
		for(int i = side*9-9; i<=page;i++){
			if(i<objects.length){
				if(objects[i]!=null){
					uuidList.add((UUID) objects[i]);
				}
			}
		}
		return uuidList;
	}
	
	public List<UUID> getPlayerList(Object[] objects, Integer page){
		List<UUID> uuidList = new ArrayList<UUID>();
		for(int i = side*45-45; i<page*45;i++){
			if(i<objects.length){
				if(objects[i]!=null){
					uuidList.add((UUID) objects[i]);
				}
			}
		}
		return uuidList;
	}
	
	public Integer getMaxSides(Object[] objects){
		return (int) Math.ceil((double) objects.length/45.0);
	}
	
	public ItemStack getItemStack(String s){
		ItemStack is = new ItemStack(lang.getMaterial(s), 1, lang.getShort(s));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(lang.getName(s));
		if(lang.getStringList(s)!=null){
			List<String> strL = new ArrayList<String>();
			for(String str : lang.getStringList(s)){
				String st = str;
				st = st.replaceAll("#OWNER#", obj.getPlayerName());
				strL.add(st);
			}
			im.setLore(strL);
		}
		is.setItemMeta(im);
		return is;
	}
	
	@EventHandler
	private void onClose(InventoryCloseEvent e){
		if(p==null){return;}
		if(e.getPlayer().equals(p)){
			if(!update){
				FurnitureLib.getInstance().getFurnitureManager().updateFurniture(obj);
				this.p = null;
			}
		}
	}
	
	@EventHandler
	private void onQuit(PlayerQuitEvent e){
		if(p==null){return;}
		if(e.getPlayer().equals(p)){
			this.p = null;
		}
	}
	
	public void setItemHub(){
		if(p==null){return;}
		ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE, 1,(short) 15);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("§c");
		is.setItemMeta(im);
		for(int i = 0; i<=8;i++){
			inv.setItem(i, is);
		}
		
		inv.setItem(9, is);
		inv.setItem(17, is);
		for(int i = 18; i<=26;i++){
			inv.setItem(i, is);
		}
		
		if(FurnitureLib.getInstance().hasPerm(p,"furniture.setOwner")||FurnitureLib.getInstance().hasPerm(p,"furniture.admin")){
			inv.setItem(12, new ItemStack(getItemStack("setOwner")));
		}
		Integer Mode1 = 10;
		Integer Mode2 = 11;
		Integer Mode3 = 14;
		Integer Mode4 = 16;
		inv.setItem(15, is);
		inv.setItem(13, is);
		inv.setItem(Mode1, obj.getPublicMode().getItemStack());
		inv.setItem(Mode2, obj.getEventType().getItemStack());
		inv.setItem(Mode3, getItemStack("add"));
		inv.setItem(Mode4, getItemStack("remove"));
		p.updateInventory();
	}
}
