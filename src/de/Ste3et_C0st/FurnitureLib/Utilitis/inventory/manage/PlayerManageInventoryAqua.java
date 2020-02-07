package de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.Ste3et_C0st.FurnitureLib.Utilitis.ItemStackBuilder;
import de.Ste3et_C0st.FurnitureLib.Utilitis.callbacks.CallbackGUI;
import de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.InventoryHandler;
import de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.InventoryManager.InventoryMode;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class PlayerManageInventoryAqua extends InventoryHandler{

	private int side = 0, maxPages = 0;
	private final int maxItems = 45;
	private final ItemStack filler = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§c").build();
	private final ObjectID objectID;
	private final InventoryMode inventoryMode;
	
	public PlayerManageInventoryAqua(Player player,ObjectID objectID, InventoryMode inventoryMode) {
		super(player, 54, getLangManager().getName(inventoryMode.getInvTitle()));
		this.inventoryMode = inventoryMode;
		this.objectID = objectID;
		this.maxPages = (int) Math.floor((double) Bukkit.getOnlinePlayers().stream().filter(p -> !p.getUniqueId().equals(objectID.getUUID())).count() / maxItems);
		this.update();
		this.open(getPlayer());
		super.onClick(new CallbackGUI() {
			
			@Override
			public void onResult(ItemStack stack, Integer slot) {
				if(this.getInventoryPos() == ClickedInventory.TOP) {
					if(Objects.isNull(stack)) return;
					if(stack.getType().equals(Material.PLAYER_HEAD)) {
						SkullMeta skull = (SkullMeta) stack.getItemMeta();
						OfflinePlayer skullOwner = skull.getOwningPlayer();
						if(Objects.isNull(skullOwner)) return;
						if(inventoryMode == InventoryMode.SETOWNER) {
							objectID.setUUID(skullOwner.getUniqueId());
						}else if(inventoryMode == InventoryMode.ADDFRIEND) {
							objectID.addMember(skullOwner.getUniqueId());
						}else if(inventoryMode == InventoryMode.REMOVEFRIEND) {
							objectID.remMember(skullOwner.getUniqueId());
						}
						objectID.setSQLAction(SQLAction.UPDATE);
						update();
					}
				}
			}
		});
	}
	
	public void update() {
		this.getInventory().clear();
		this.setContent();
		this.setButtons();
	}

	@Override
	public void setButtons() {
		super.addItemStack(47, getItemStack("PrevPage"));
		super.addItemStack(49, getItemStack("PageItem"));
		super.addItemStack(51, getItemStack("NextPage"));
		AtomicInteger slot = new AtomicInteger(0);
		int skipPlayer = this.side * maxItems;
		if(inventoryMode == InventoryMode.SETOWNER) {
			Bukkit.getOnlinePlayers().stream()
				.filter(player -> !getObjectID().getUUID().equals(player.getUniqueId()))
				.sorted((k1, k2) -> k1.getName().compareTo(k2.getName()))
				.skip(skipPlayer)
				.forEach(player -> {
					ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
					SkullMeta skull = (SkullMeta) itemStack.getItemMeta();
					skull.setOwningPlayer(player);
					skull.setDisplayName(player.getName());
					itemStack.setItemMeta(skull);
					addItemStack(slot.getAndIncrement(), itemStack);
			});
		}else if(inventoryMode == InventoryMode.ADDFRIEND) {
			Bukkit.getOnlinePlayers().stream()
			.filter(player -> !getObjectID().getUUID().equals(player.getUniqueId()))
			.filter(player -> !getObjectID().getMemberList().contains(player.getUniqueId()))
			.sorted((k1, k2) -> k1.getName().compareTo(k2.getName()))
			.skip(skipPlayer)
			.forEach(player -> {
				ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta skull = (SkullMeta) itemStack.getItemMeta();
				skull.setOwningPlayer(player);
				skull.setDisplayName(player.getName());
				itemStack.setItemMeta(skull);
				addItemStack(slot.getAndIncrement(), itemStack);
			});
		}else if(inventoryMode == InventoryMode.REMOVEFRIEND) {
			getObjectID().getMemberList().stream().forEach(member -> {
				ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta skull = (SkullMeta) itemStack.getItemMeta();
				skull.setOwningPlayer(Bukkit.getOfflinePlayer(member));
				itemStack.setItemMeta(skull);
				addItemStack(slot.getAndIncrement(), itemStack);
			});
		}
	}

	@Override
	public void setContent() {
		for(int i = 0; i < getInventory().getSize(); i++) super.addItemStack(i, this.filler);
	}
	
	public ItemStack getItemStack(String s){
		ItemStackBuilder builder = new ItemStackBuilder(getLangManager().getMaterial(s)).setAmount(1).setName(getLangManager().getName(s).replace("#CURPAGE#", (this.side + 1) + "").replace("#MAXPAGE#", (this.maxPages + 1) + ""));
		if(Objects.nonNull(getLangManager().getStringList(s))){
			List<String> lore = new ArrayList<String>();
			
			getLangManager().getStringList(s).stream().forEach(entry -> {
				String string = entry.replaceAll("#OWNER#", getObjectID().getPlayerName());
				lore.add(string);
			});
			builder.setLore(lore);
		}
		return builder.build();
	}

	public ObjectID getObjectID() {
		return objectID;
	}
}
