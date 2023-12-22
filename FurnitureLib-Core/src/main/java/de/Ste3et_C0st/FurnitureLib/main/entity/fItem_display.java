package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.CraftItemStack;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fItem_display extends fDisplay{

	public static EntityType type = EntityType.valueOf("ITEM_DISPLAY");
	private final DefaultKey<EntitySize> entitySize = new DefaultKey<EntitySize>(new EntitySize(0, 0));
	private final DefaultKey<ItemStack> stack = new DefaultKey<ItemStack>(new ItemStack(Material.AIR));
	private final DefaultKey<ItemDisplayTransform> display = new DefaultKey<ItemDisplayTransform>(ItemDisplayTransform.NONE);
	
	public fItem_display(Location loc, ObjectID id) {
		super(loc, type, 0, id);
	}
	
	@Override
	public Entity toRealEntity() {
		return null;
	}

	@Override
	public boolean isRealEntity() {
		return false;
	}

	@Override
	public fEntity clone() {
		final fItem_display display = new fItem_display(null, getObjID());
		this.copyMetadata(display);
		return display;
	}

	@Override
	public void copyMetadata(final fEntity entity) {
		if(entity instanceof fItem_display) {
			super.copyMetadata(entity);
			final fItem_display display = this.getClass().cast(entity);
			display.setItemDisplay(this.getItemDisplay());
			display.setItemStack(this.getStack());
		}
	}

	@Override
	public void setEntity(Entity e) {
		
	}
	
	public ItemStack getStack() {
		return this.stack.getOrDefault();
	}
	
	public ItemDisplayTransform getItemDisplay() {
		return this.display.getOrDefault();
	}
	
	public fItem_display setItemStack(final ItemStack stack) {
		this.stack.setValue(stack);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 22, Registry.getItemStackSerializer(false)), stack);
		return this;
	}
	
	public fItem_display setItemDisplay(ItemDisplayTransform display) {
		this.display.setValue(display);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 23, Registry.get(Byte.class)), (byte) display.ordinal());
		return this;
	}
	
	public EntitySize getEntitySize() {
		return this.entitySize.getOrDefault();
	}
	
	@Override
	public NBTTagCompound getMetaData() {
		super.getMetaData();
		if(!this.stack.isDefault()) setMetadata(getStack());
		if(!this.display.isDefault()) setMetadata("display", this.display.getOrDefault().name());
		return getNBTField();
	}
	
	@Override
    public void loadMetadata(NBTTagCompound metadata) {
        super.loadMetadata(metadata);
        if(metadata.hasKeyOfType("stack", 10)) {
        	try {
        		final ItemStack stack = new CraftItemStack().getItemStack(metadata.getCompound("stack"));
        		this.setItemStack(stack);
        	}catch (Exception e) {
				e.printStackTrace();
			}
        };
        
        if(metadata.hasKeyOfType("display", 8)) {
        	try {
        		final ItemDisplayTransform displayTransform = ItemDisplayTransform.valueOf(metadata.getString("display"));
        		this.setItemDisplay(displayTransform);
        	}catch (Exception e) {
				e.printStackTrace();
			}
        }
	}
	
	@Override
	protected Material getDestroyMaterial() {
		return getStack().getType().isBlock() ? getStack().getType() : Material.AIR;
	}
}
