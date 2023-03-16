package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay.TextAligment;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public class fText_display extends fDisplay{

	public static EntityType type = EntityType.TEXT_DISPLAY;
	private final DefaultKey<EntitySize> entitySize = new DefaultKey<EntitySize>(new EntitySize(0, 0));
	
	private final DefaultKey<Integer> lineWitdth = new DefaultKey<Integer>(200), background_color = new DefaultKey<Integer>(1073741824);
	private final DefaultKey<Byte> text_opacity = new DefaultKey<Byte>((byte) -1);
	private final DefaultKey<TextAligment> style_flags = new DefaultKey<TextAligment>(TextAligment.CENTER);
	private final DefaultKey<String> text = new DefaultKey<String>("");
	private final DefaultKey<Boolean> shadowed = new DefaultKey<Boolean>(false), seeThrough = new DefaultKey<Boolean>(false), defaultBackground = new DefaultKey<Boolean>(true);
	
	public fText_display(Location loc, ObjectID id) {
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
		return null;
	}

	@Override
	public void copyMetadata(fEntity entity) {
		
	}

	@Override
	public void setEntity(Entity e) {
		
	}
	
	public int getLineWidth() {
		return this.lineWitdth.getOrDefault();
	}
	
	public int getBackgroundColorInt() {
		return this.background_color.getOrDefault();
	}
	
	public byte getTextOpacity() {
		return this.text_opacity.getOrDefault();
	}
	
	public EntitySize getEntitySize() {
		return this.entitySize.getOrDefault();
	}
	
	public String getText() {
		return this.text.getOrDefault();
	}
	
	public TextAligment getTextAligment() {
		return this.style_flags.getOrDefault();
	}
	
	public Color getBackgroundColor() {
		int color = getBackgroundColorInt();
		return (color == -1) ? null : Color.fromRGB(color);
	}
	
	public fText_display setText(String text) {
		this.text.setValue(text);
		getWatcher().setObject(new WrappedDataWatcherObject(22, Registry.getChatComponentSerializer(false)), WrappedChatComponent.fromText(text).getHandle());
		return this;
	}
	
	public fText_display setLineWidth(int width) {
		this.lineWitdth.setValue(width);
		getWatcher().setObject(new WrappedDataWatcherObject(23, Registry.get(Integer.class)), this.getLineWidth());
		return this;
	}
	
	public fText_display setBackgroundColor(Color color) {
		return this.setBackgroundColor(color.asRGB());
	}
	
	public fText_display setBackgroundColor(int color) {
		this.background_color.setValue(color);
		getWatcher().setObject(new WrappedDataWatcherObject(24, Registry.get(Integer.class)), color);
		return this;
	}
	
	public fText_display setTextOpacity(byte opacity) {
		this.text_opacity.setValue(opacity);
		getWatcher().setObject(new WrappedDataWatcherObject(25, Registry.get(Byte.class)), this.getTextOpacity());
		return this;
	}
	
	public fText_display setAlignment(TextAligment aligment) {
		this.style_flags.setValue(aligment);
	    switch (aligment) {
	      case LEFT:
	    	setBitMask(true, 26, 8);  
	    	setBitMask(false, 26, 16);
	        return this;
	      case RIGHT:
	        setBitMask(false, 26, 8);  
	    	setBitMask(true, 26, 16);
	        return this;
	      case CENTER:
	    	setBitMask(true, 26, 8);  
		    setBitMask(true, 26, 16);
		    return this;
	      default:
	    	setBitMask(false, 26, 8);  
		    setBitMask(false, 26, 16);
		    return this;
	    } 
	}
	
	public boolean isShadowed() {
		return shadowed.getOrDefault();
	}

	public void setShadowed(boolean shadow) {
		this.shadowed.setValue(shadow);
		setBitMask(shadow, 26, 1);
	}

	public boolean isSeeThrough() {
		return seeThrough.getOrDefault();
	}

	public void setSeeThrough(boolean seeThrough) {
		this.seeThrough.setValue(seeThrough);
		setBitMask(seeThrough, 26, 2);
	}

	public boolean isDefaultBackground() {
		return defaultBackground.getOrDefault();
	}

	public void setDefaultBackground(boolean defaultBackground) {
		this.defaultBackground.setValue(defaultBackground);
		setBitMask(defaultBackground, 26, 4);
	}
	
	@Override
	public NBTTagCompound getMetaData() {
		super.getMetaData();
		if(!this.lineWitdth.isDefault()) setMetadata("lineWitdth", this.lineWitdth.getOrDefault());
		if(!this.background_color.isDefault()) setMetadata("background_color", this.background_color.getOrDefault());
		if(!this.text_opacity.isDefault()) setMetadata("text_opacity", this.text_opacity.getOrDefault());
		if(!this.style_flags.isDefault()) setMetadata("style_flags", this.style_flags.getOrDefault().name());
		if(!this.text.isDefault()) setMetadata("text", this.text.getOrDefault());
		if(!this.shadowed.isDefault()) setMetadata("shadowed", this.shadowed.getOrDefault());
		if(!this.seeThrough.isDefault()) setMetadata("seeThrough", this.seeThrough.getOrDefault());
		if(!this.defaultBackground.isDefault()) setMetadata("defaultBackground", this.defaultBackground.getOrDefault());
		
		return getNBTField();
	}
	
	@Override
    public void loadMetadata(NBTTagCompound metadata) {
        super.loadMetadata(metadata);
        if(metadata.hasKeyOfType("lineWitdth", 8)) this.setLineWidth(metadata.getInt("lineWitdth"));
        if(metadata.hasKeyOfType("background_color", 8)) this.setBackgroundColor(metadata.getInt("background_color"));
        if(metadata.hasKeyOfType("text_opacity", 8)) this.setTextOpacity(metadata.getByte("text_opacity"));
        
        if(metadata.hasKeyOfType("text", 8)) this.setText(metadata.getString("text"));
        if(metadata.hasKeyOfType("shadowed", 8)) this.setShadowed(metadata.getBoolean("shadowed"));
        if(metadata.hasKeyOfType("seeThrough", 8)) this.setSeeThrough(metadata.getBoolean("seeThrough"));
        if(metadata.hasKeyOfType("defaultBackground", 8)) this.setDefaultBackground(metadata.getBoolean("defaultBackground"));
        
        if(metadata.hasKeyOfType("style_flags", 8)) {
        	try {
        		final TextAligment aligment = TextAligment.valueOf(metadata.getString("style_flags"));
        		this.setAlignment(aligment);
        	}catch (Exception e) {
				e.printStackTrace();
			}
        }
	}

}
