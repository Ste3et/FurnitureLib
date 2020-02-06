package de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.Ste3et_C0st.FurnitureLib.Utilitis.ItemStackBuilder;
import de.Ste3et_C0st.FurnitureLib.Utilitis.callbacks.CallbackGUI;
import de.Ste3et_C0st.FurnitureLib.Utilitis.callbacks.CallbackGUIClose;
import de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.InventoryHandler;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.EventType;
import de.Ste3et_C0st.FurnitureLib.main.Type.PublicMode;

public class ManageInventoryCombat extends InventoryHandler{

	private final ObjectID objectID;
	private final PublicMode publicMode;
	private final EventType eventType;
	private final ItemStack filler = new ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§c").build();
	
	public ManageInventoryCombat(Player player, ObjectID objectID) {
		super(player, 27, getLangManager().getName("manageInvName"));
		this.objectID = objectID;
		this.publicMode = getObjectID().getPublicMode();
		this.eventType = getObjectID().getEventType();
		
		super.onClick(new CallbackGUI() {
			@Override
			public void onResult(ItemStack stack, Integer slot) {
				if(Objects.isNull(stack)) return;
				if(stack.equals(filler)) return;
				if(getInventoryPos() == ClickedInventory.TOP) {
					getPlayer().playSound(getPlayer().getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
				}
			}
		});
		
		this.update();
		this.open(getPlayer());
		
		super.onClose(new CallbackGUIClose() {
			@Override
			public void onResult(InventoryHandler gui) {
				
			}
		});
	}
	
	public ObjectID getObjectID() {
		return objectID;
	}
	
	public void update() {
		this.setContent();
		this.setButtons();
	}
	
	@Override
	public void setButtons() {
		super.addItemStack(10, this.publicMode.getItemStack());
		super.addItemStack(11, this.eventType.getItemStack());
		if(FurnitureLib.getInstance().getPermission().hasPerm(getPlayer(),"furniture.setOwner")||FurnitureLib.getInstance().getPermission().hasPerm(getPlayer(),"furniture.admin")){
			super.addItemStack(12, new ItemStack(getItemStack("setOwner")));
		}
		super.addItemStack(14, getItemStack("add"));
		super.addItemStack(16, getItemStack("remove"));
	}

	@Override
	public void setContent() {
		for(int i = 0; i < getInventory().getSize(); i++) super.addItemStack(i, this.filler);
		
	}
	
	public ItemStack getItemStack(String s){
		ItemStackBuilder builder = new ItemStackBuilder(getLangManager().getMaterial(s)).setAmount(1).setName(getLangManager().getName(s));
		if(Objects.nonNull(getLangManager().getStringList(s))){
			List<String> lore = new ArrayList<String>();
			
			getLangManager().getStringList(s).stream().forEach(entry -> {
				lore.add(entry.replaceAll("#OWNER#", getObjectID().getPlayerName()));
			});
			
			builder.setLore(lore);
		}
		
		return builder.build();
	}
	
	
}
