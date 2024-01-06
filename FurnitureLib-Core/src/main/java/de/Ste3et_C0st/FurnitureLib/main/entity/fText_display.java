package de.Ste3et_C0st.FurnitureLib.main.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay.TextAlignment;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageConverter;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class fText_display extends fDisplay {

	public static EntityType type = EntityType.valueOf("TEXT_DISPLAY");
	
	private final DefaultKey<EntitySize> entitySize = new DefaultKey<EntitySize>(new EntitySize(0, 0));
	private final DefaultKey<Integer> lineWitdth = new DefaultKey<Integer>(200), background_color = new DefaultKey<Integer>(1073741824);
	private final DefaultKey<Byte> text_opacity = new DefaultKey<Byte>((byte) -1);
	private final DefaultKey<TextAlignment> style_flags = new DefaultKey<TextAlignment>(TextAlignment.CENTER);
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
		final fText_display display = new fText_display(null, getObjID());
		this.copyMetadata(display);
		return display;
	}

	@Override
	public void copyMetadata(final fEntity entity) {
		if (entity instanceof fText_display) {
			super.copyMetadata(entity);
			final fText_display display = fText_display.class.cast(entity);
			display.setSeeThrough(this.isSeeThrough());
			display.setDefaultBackground(this.isDefaultBackground());
			display.setText(this.getText());
			display.setBackgroundColor(this.getBackgroundColorInt());
			display.setLineWidth(this.getLineWidth());
			display.setTextOpacity(this.getTextOpacity());
			display.setAlignment(this.getTextAligment());
			display.setShadowed(this.isShadowed());
		}
	}

	@Override
	public void setEntity(Entity e) {

	}

	public int getLineWidth() {
		return this.lineWitdth.getOrDefault();
	}

	public int getBackgroundColorInt() {
		return background_color.getOrDefault();
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

	public TextAlignment getTextAligment() {
		return this.style_flags.getOrDefault();
	}

	public fText_display setText(final String text) {
		this.text.setValue(text);
		final String workString = LanguageConverter.serializeLegacyColors(text);
		final Component textComponent = MiniMessage.miniMessage().deserialize(workString);
		final Object wrappedChat = WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(textComponent)).getHandle();
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 22, Registry.getChatComponentSerializer(false)), wrappedChat);
		return this;
	}

	public fText_display setLineWidth(int width) {
		this.lineWitdth.setValue(width);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 23, Registry.get(Integer.class)), this.getLineWidth());
		return this;
	}

	public fText_display setBackgroundColor(int color) {
		this.background_color.setValue(color);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 24, Registry.get(Integer.class)), color);
		return this;
	}

	public fText_display setTextOpacity(byte opacity) {
		this.text_opacity.setValue(opacity);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 25, Registry.get(Byte.class)), this.getTextOpacity());
		return this;
	}

	public fText_display setAlignment(TextAlignment aligment) {
		this.style_flags.setValue(aligment);
		switch (aligment) {
		case LEFT:
			setFlag(8, true);
			setFlag(16, false);
			return this;
		case RIGHT:
			setFlag(8, false);
			setFlag(16, true);
			return this;
		default:
			setFlag(8, false);
			setFlag(16, false);
			return this;
		}
	}

	public boolean isShadowed() {
		return shadowed.getOrDefault();
	}

	public void setShadowed(boolean shadow) {
		this.shadowed.setValue(shadow);
		setFlag(1, shadow);
	}

	public boolean isSeeThrough() {
		return seeThrough.getOrDefault();
	}

	public void setSeeThrough(boolean seeThrough) {
		this.seeThrough.setValue(seeThrough);
		setFlag(2, seeThrough);
	}

	public boolean isDefaultBackground() {
		return defaultBackground.getOrDefault();
	}

	public void setDefaultBackground(boolean defaultBackground) {
		this.defaultBackground.setValue(defaultBackground);
		setFlag(4, defaultBackground);
	}

	@Override
	public NBTTagCompound getMetaData() {
		super.getMetaData();
		if (!this.lineWitdth.isDefault())
			setMetadata("line_width", this.lineWitdth.getOrDefault());
		if (!this.background_color.isDefault())
			setMetadata("background", this.background_color.getOrDefault());
		if (!this.text_opacity.isDefault())
			setMetadata("text_opacity", this.text_opacity.getOrDefault());
		if (!this.style_flags.isDefault())
			setMetadata("alignment", this.style_flags.getOrDefault().name());
		if (!this.text.isDefault())
			setMetadata("text", this.text.getOrDefault());
		if (!this.shadowed.isDefault())
			setMetadata("shadow", this.shadowed.getOrDefault());
		if (!this.seeThrough.isDefault())
			setMetadata("see_through", this.seeThrough.getOrDefault());
		if (!this.defaultBackground.isDefault())
			setMetadata("default_background", this.defaultBackground.getOrDefault());
		return getNBTField();
	}

	@Override
	public void loadMetadata(NBTTagCompound metadata) {
		super.loadMetadata(metadata);
		
		this.setLineWidth(metadata.getInt("line_width", metadata.getInt("lineWitdth", 200)));
		this.setBackgroundColor(metadata.getInt("background", metadata.getInt("background_color", 1073741824)));
		this.setTextOpacity(metadata.getByte("text_opacity", (byte) -1));
		this.setText(metadata.getString("text"));
		this.setShadowed(metadata.getInt("shadow", metadata.getInt("shadowed", 0)) == 1);
		this.setSeeThrough(metadata.getInt("see_through", metadata.getInt("seeThrough", 0)) == 1);
		this.setDefaultBackground(metadata.getInt("default_background", metadata.getInt("defaultBackground", 2)) == 1);
		
		if (metadata.hasKeyOfType("style_flags", 8)) {
			try {
				final TextAlignment aligment = TextAlignment.valueOf(metadata.getString("style_flags"));
				this.setAlignment(aligment);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(metadata.hasKeyOfType("alignment", 8)) {
			try {
				final TextAlignment aligment = TextAlignment.valueOf(metadata.getString("alignment"));
				this.setAlignment(aligment);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected Material getDestroyMaterial() {
		return Material.AIR;
	}
	
	//see org.bukkit.craftbukkit.v1_19_R3.entity.setFlag
	private void setFlag(int flag, boolean set) {
		final int field = displayField.getVersionIndex() + 26;
		byte flagBits = (byte) 0;

		if (getWatcher().hasIndex(field)) {
			flagBits = (byte) getWatcher().getObject(new WrappedDataWatcherObject(field, Registry.get(Byte.class)));
        }
		
		if (set) {
			flagBits = (byte) (flagBits | flag);
		} else {
			flagBits = (byte) (flagBits & (flag ^ 0xFFFFFFFF));
		}

		getWatcher().setObject(new WrappedDataWatcherObject(field, Registry.get(Byte.class)), flagBits);
	}
}
