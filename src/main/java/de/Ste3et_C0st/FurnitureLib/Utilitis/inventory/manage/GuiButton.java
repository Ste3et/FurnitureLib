package de.Ste3et_C0st.FurnitureLib.Utilitis.inventory.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import de.Ste3et_C0st.FurnitureLib.Utilitis.ItemStackBuilder;

public class GuiButton {

	public static final NamespacedKey KEY_UUID = new NamespacedKey(JavaPlugin.getProvidingPlugin(GuiButton.class), "IF-uuid");
	private Consumer<InventoryClickEvent> action;
	private boolean visible = true;
	private UUID uuid = UUID.randomUUID();
	private List<Object> properties;
	private ItemStack item = new ItemStack(Material.AIR);
	
	public GuiButton(ItemStackBuilder builder, Consumer<InventoryClickEvent> action) {
		this(builder.build(), action);
	}
	
	public GuiButton(ItemStack item, Consumer<InventoryClickEvent> action) {
		this.action = action;
		this.setVisible(true);
		this.properties = new ArrayList<>();
		this.setItem(item);
	}
	
    public GuiButton copy() {
    	GuiButton guiItem = new GuiButton(item.clone(), action);

        guiItem.visible = visible;
        guiItem.uuid = uuid;
        guiItem.properties = new ArrayList<>(properties);
        ItemMeta meta = guiItem.item.getItemMeta();

        if (meta == null) {
            throw new IllegalArgumentException("item must be able to have ItemMeta (it mustn't be AIR)");
        }

        meta.getPersistentDataContainer().set(KEY_UUID, UUIDTagType.INSTANCE, guiItem.uuid);
        guiItem.item.setItemMeta(meta);

        return guiItem;
    }
    
    public void callAction(InventoryClickEvent event) {
        if (action == null) {
            return;
        }

        try {
            action.accept(event);
        } catch (Throwable t) {
            Logger logger = JavaPlugin.getProvidingPlugin(getClass()).getLogger();
            logger.log(Level.SEVERE, "Exception while handling click event in inventory '"
                    + event.getView().getTitle() + "', slot=" + event.getSlot() + ", item=" + item.getType(), t);
        }
    }
    
    public void setAction(Consumer<InventoryClickEvent> action) {
        this.action = action;
    }
    
    public List<Object> getProperties(){
        return properties;
    }
    
    public void setProperties(List<Object> properties){
        this.properties = properties;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public void setItem(ItemStack item) {
    	ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.getPersistentDataContainer().set(KEY_UUID, UUIDTagType.INSTANCE, uuid);
            item.setItemMeta(meta);
        }

        this.item = item;
    }

    public UUID getUUID() {
        return uuid;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isStack(ItemStack stack) {
    	if(stack.hasItemMeta()) {
    		ItemMeta meta = stack.getItemMeta();
    		if(meta.getPersistentDataContainer().has(KEY_UUID, UUIDTagType.INSTANCE)) {
    			return meta.getPersistentDataContainer().get(KEY_UUID,  UUIDTagType.INSTANCE).equals(getUUID());
    		}
    	}
    	return false;
    }
}
