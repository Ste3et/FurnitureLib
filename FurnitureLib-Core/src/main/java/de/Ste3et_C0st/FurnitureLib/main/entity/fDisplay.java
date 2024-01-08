package de.Ste3et_C0st.FurnitureLib.main.entity;

import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.util.Transformation;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type.ProtocolFieldsDisplay;

public abstract class fDisplay extends fSize{
	
	private DefaultKey<Vector3f> translation = new DefaultKey<Vector3f>(new Vector3f()), scale = new DefaultKey<Vector3f>(new Vector3f());
	private DefaultKey<Quaternionf> leftRotation = new DefaultKey<Quaternionf>(new Quaternionf()), rightRotation = new DefaultKey<Quaternionf>(new Quaternionf());
	
	private DefaultKey<Billboard> billboard = new DefaultKey<Billboard>(Billboard.FIXED);
	private DefaultKey<Float> viewRange = new DefaultKey<Float>(1f), shadowRadius = new DefaultKey<Float>(0f), shadowStrength = new DefaultKey<Float>(1f);
	private DefaultKey<Integer> interpolationDelay = new DefaultKey<Integer>(0), interpolationDuration = new DefaultKey<Integer>(0), brightness = new DefaultKey<Integer>(-1), glow_override = new DefaultKey<Integer>(-1);
	
	public final static ProtocolFieldsDisplay displayField = FurnitureLib.getVersion(new MinecraftVersion("1.20.2")) ? ProtocolFieldsDisplay.Spigot120_2 : ProtocolFieldsDisplay.Spgiot120;
	
	public fDisplay(Location loc, EntityType type, int entityID, ObjectID id) {
		super(loc, type, entityID, id, 0F, 0F);
		this.writeTransformation();
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
	public void copyMetadata(final fEntity entity) {
		if(entity instanceof fDisplay) {
			super.copyMetadata(entity);
			final fDisplay display = this.getClass().cast(entity);
			display.setBillboard(this.getBillboard());
			display.setScale(this.getScale());
			display.setLeftRotation(this.getLeftRotationObj());
			display.setRightRotation(this.getRightRotationObj());
			display.setTranslation(this.getTranslation());
			display.setInterpolationDelay(this.getInterpolationDelay());
			display.setInterpolationDuration(this.getInterpolationDuration());
			display.setViewRange(this.getViewRange());
			display.setShadowRadius(this.getShadowRadius());
			display.setShadowStrength(this.getShadowStrength());
			display.setBrightness(this.getBrightness());
			display.setGlowOverride(this.getGlowOverride());
		}
	}

	@Override
	public void setEntity(Entity e) {
		
	}
	
	protected void clone(fEntity entity) {}
	
	public fDisplay setBlockLight(int lightLevel) {
		final Brightness brightness = new Brightness(lightLevel, this.getSkyLight());
		this.setBrightness(brightness);
		return this;
	}
	
	public fDisplay setSkyLight(int skyLight) {
		final Brightness brightness = new Brightness(this.getBlockLight(), skyLight);
		this.setBrightness(brightness);
		return this;
	}
	
	public fDisplay setBrightness(Brightness brightness) {
		if(brightness.getBlockLight() == 0 && brightness.getSkyLight() == 0) {
			return this.setBrightness(-1);
		}
		return this.setBrightness(brightness.getBlockLight() << 4 | brightness.getSkyLight() << 20);
	}
	
	public Brightness getBrightnessObject() {
	    int var1 = this.brightness.getOrDefault() >> 4 & 0xFFFF;
	    int var2 = this.brightness.getOrDefault() >> 20 & 0xFFFF;
	    return new Brightness(0 <= var1 && var1 <= 15 ? var1 : 0, 0 <= var2 && var2 <= 15 ? var2 : 0);
	}
	
	public int getSkyLight() {
		return getBrightnessObject().getSkyLight();
	}
	
	public int getBlockLight() {
		return getBrightnessObject().getBlockLight();
	}
	
	public void setScale(Vector3f vector) {
		this.scale.setValue(vector);
		this.writeTransformation();
	}
	
	public void setTranslation(Vector3f vector) {
		this.translation.setValue(vector);
		this.writeTransformation();
	}
	
	public void setLeftRotation(AxisAngle4f rotAngle4f) {
		this.setLeftRotation(new Quaternionf(rotAngle4f));
	}
	
	public void setLeftRotation(Quaternionf quaternionf) {
		this.leftRotation.setValue(quaternionf);
		this.writeTransformation();
	}
	
	public void setRightRotation(AxisAngle4f rotAngle4f) {
		this.setLeftRotation(new Quaternionf(rotAngle4f));
	}
	
	public void setRightRotation(Quaternionf quaternionf) {
		this.rightRotation.setValue(quaternionf);
		this.writeTransformation();
	}
	
	public int getInterpolationDelay() {
		return this.interpolationDelay.getOrDefault();
	}
	
	public Quaternionf getLeftRotationObj() {
		return this.leftRotation.getOrDefault();
	}
	
	public Quaternionf getRightRotationObj() {
		return this.rightRotation.getOrDefault();
	}
	
	public Vector3f getScale() {
		return this.scale.getOrDefault();
	}
	
	public Vector3f getTranslation() {
		return this.translation.getOrDefault();
	}
	
	public Quaternionf getLeftRotation() {
		return this.getTransformation().getLeftRotation();
	}
	
	public Quaternionf getRightRotation() {
		return this.getTransformation().getRightRotation();
	}
	
	public Transformation getTransformation() {
		return new Transformation(this.translation.getOrDefault(), this.leftRotation.getOrDefault(), this.scale.getOrDefault(), this.rightRotation.getOrDefault());
	}
		  
	public int getInterpolationDuration() {
		return this.interpolationDuration.getOrDefault();
	}
	
	public Billboard getBillboard() {
		return this.billboard.getOrDefault();
	}
	
	public int getBrightness() {
		return this.brightness.getOrDefault();
	}
	
	public float getViewRange() {
		return this.viewRange.getOrDefault();
	}
	
	public float getShadowRadius() {
		return this.shadowRadius.getOrDefault();
	}
	
	public float getShadowStrength() {
		return this.shadowStrength.getOrDefault();	
	}
	
	public int getGlowOverride() {
		return glow_override.getOrDefault();
	}
	
	public fDisplay setBillboard(int billboard) {
		final Billboard billEnum = Billboard.values()[billboard];
		return this.setBillboard(billEnum);
	}
	
	public fDisplay setInterpolationDelay(int var0) {
		this.interpolationDelay.setValue(var0);
		getWatcher().setObject(new WrappedDataWatcherObject(8, Registry.get(Integer.class)), this.getInterpolationDelay());
		return this;
	}
	
    public fDisplay setInterpolationDuration(int var0) {
    	this.interpolationDuration.setValue(var0);
    	getWatcher().setObject(new WrappedDataWatcherObject(8, Registry.get(Integer.class)), this.getInterpolationDuration());
    	return this;
    }
	
	private void writeTransformation() {
		final Transformation transformation = getTransformation();
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 10, Registry.get(Vector3f.class)), transformation.getTranslation());
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 11, Registry.get(Vector3f.class)), transformation.getScale());
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 12, Registry.get(Quaternionf.class)), transformation.getLeftRotation());
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 13, Registry.get(Quaternionf.class)), transformation.getRightRotation());
	}
	
	public fDisplay setTransformation(Transformation transformation) {
		this.translation.setValue(transformation.getTranslation());
		this.scale.setValue(transformation.getScale());
		this.leftRotation.setValue(transformation.getLeftRotation());
		this.rightRotation.setValue(transformation.getRightRotation());
		this.writeTransformation();
		return this;
	}
	
	public fDisplay setBillboard(Billboard billboard) {
		this.billboard.setValue(billboard);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 14, Registry.get(Byte.class)), (byte) this.getBillboard().ordinal());
		return this;
	}
	
	public fDisplay setBrightness(int i) {
		this.brightness.setValue(i);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 15, Registry.get(Integer.class)), this.getBrightness());
		return this;
	}
	
	public fDisplay setViewRange(float f) {
		this.viewRange.setValue(f);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 16, Registry.get(Float.class)), this.getViewRange());
		return this;
	}
	
	public fDisplay setShadowRadius(float f) {
		this.shadowRadius.setValue(f);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 17, Registry.get(Float.class)), this.getShadowRadius());
		return this;
	}
	
	public fDisplay setShadowStrength(float f) {
		this.shadowStrength.setValue(f);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 18, Registry.get(Float.class)), this.getShadowStrength());
		return this;
	}
	
	public fDisplay setGlowOverride(int i) {
		this.glow_override.setValue(i);
		getWatcher().setObject(new WrappedDataWatcherObject(displayField.getVersionIndex() + 21, Registry.get(Integer.class)), this.getGlowOverride());
		return this;
	}
	
    public NBTTagCompound getMetaData() {
    	super.getMetaData();
    	if(!this.viewRange.isDefault()) setMetadata("view_range", this.getViewRange());
    	if(!this.shadowRadius.isDefault()) setMetadata("shadow_radius", this.getShadowRadius());
    	if(!this.shadowStrength.isDefault()) setMetadata("shadow_strength", this.getShadowStrength());
    	if(!this.glow_override.isDefault()) setMetadata("glow_color_override", this.getGlowOverride());
    	if(!this.interpolationDelay.isDefault()) setMetadata("teleport_duration", this.getInterpolationDelay());
    	if(!this.interpolationDuration.isDefault()) setMetadata("interpolation_duration", this.getInterpolationDuration());
    	if(!this.brightness.isDefault()) setMetadata("brightness", this.getBrightness());
    	if(!this.billboard.isDefault()) setMetadata("billboard", this.getBillboard().name());
    	
    	NBTTagCompound transformation = new NBTTagCompound();
    	
    	if(this.translation.isDefault() == false) {
    		final NBTTagCompound translation = new NBTTagCompound();
    		final Vector3f vector = this.translation.getOrDefault();
    		translation.setFloat("x", vector.x);
    		translation.setFloat("y", vector.y);
    		translation.setFloat("z", vector.z);
        	transformation.set("translation", translation);
    	}
    	
    	if(this.scale.isDefault() == false) {
    		final NBTTagCompound scale = new NBTTagCompound();
    		final Vector3f vector = this.scale.getOrDefault();
    		scale.setFloat("x", vector.x);
    		scale.setFloat("y", vector.y);
    		scale.setFloat("z", vector.z);
    		transformation.set("scale", scale);
    	}
    	
    	//this.writeRotation(getLeftRotationObj(), transformation, "leftRotation");
    	this.writeRotation(getRightRotationObj(), transformation, "right_Rotation");
    	
    	//System.out.println(transformation.toString());
    	
    	if(!transformation.isEmpty()) set("transformation", transformation);
    	
        return getNBTField();
    }
    
    private void writeRotation(final Quaternionf quaternionf, final NBTTagCompound transformation, final String name) {
    	final NBTTagCompound rotation = new NBTTagCompound();
    	rotation.setFloat("angle", quaternionf.angle());
    	rotation.setFloat("x", quaternionf.x);
    	rotation.setFloat("y", quaternionf.y);
    	rotation.setFloat("z", quaternionf.z);
    	transformation.set(name, rotation);
    }

	@Override
    public void loadMetadata(NBTTagCompound metadata) {
        super.loadMetadata(metadata);
        this.setBillboard(Stream.of(Billboard.values()).filter(entry -> entry.name().equalsIgnoreCase(metadata.getString("billboard"))).findFirst().orElse(Billboard.FIXED));
        this.setBrightness(metadata.getInt("brightness"));
        this.setGlowOverride(metadata.getInt("glow_color_override", metadata.getInt("glow_override", 0)));
        this.setViewRange(metadata.getFloat("view_range", metadata.getFloat("viewRange", 1F)));
        this.setInterpolationDuration(metadata.getInt("interpolation_duration", metadata.getInt("interpolationDuration", 0)));
        this.setInterpolationDelay(metadata.getInt("teleport_duration", metadata.getInt("interpolationDelay", 0)));
        this.setShadowRadius(metadata.getFloat("shadow_radius", metadata.getFloat("shadowRadius")));
        this.setShadowStrength(metadata.getFloat("shadow_strength", metadata.getFloat("shadowStrength")));
        
        if(metadata.hasKeyOfType("transformation", 10)) {
        	final NBTTagCompound transformation = metadata.getCompound("transformation");
        	
        	if(transformation.hasKeyOfType("translation", 10)) {
        		final NBTTagCompound translation = transformation.getCompound("translation");
        		final Vector3f vector3f = new Vector3f(translation.getFloat("x"), translation.getFloat("y"), translation.getFloat("z"));
        		this.translation.setValue(vector3f);
        	}
        	
        	if(transformation.hasKeyOfType("scale", 10)) {
        		final NBTTagCompound scale = transformation.getCompound("scale");
        		final Vector3f vector3f = new Vector3f(scale.getFloat("x"), scale.getFloat("y"), scale.getFloat("z"));
        		this.scale.setValue(vector3f);
        	}
        	
        	if(transformation.hasKeyOfType("leftRotation", 10)) {
        		final NBTTagCompound leftRotation = transformation.getCompound("leftRotation");
        		final AxisAngle4f axisAngle4f = new AxisAngle4f(leftRotation.getFloat("angle"), leftRotation.getFloat("x"), leftRotation.getFloat("y"), leftRotation.getFloat("z"));
        		//System.out.println(axisAngle4f.toString());
        		this.leftRotation.setValue(new Quaternionf(axisAngle4f));
        	}else if(transformation.hasKeyOfType("left_Rotation", 10)) {
        		final NBTTagCompound leftRotation = transformation.getCompound("left_Rotation");
        		final AxisAngle4f axisAngle4f = new AxisAngle4f(leftRotation.getFloat("angle"), leftRotation.getFloat("x"), leftRotation.getFloat("y"), leftRotation.getFloat("z"));
        		//System.out.println(axisAngle4f.toString());
        		this.leftRotation.setValue(new Quaternionf(axisAngle4f));
        	}
        	
        	if(transformation.hasKeyOfType("rightRotation", 10)) {
        		final NBTTagCompound rightRotation = transformation.getCompound("rightRotation");
        		final AxisAngle4f axisAngle4f = new AxisAngle4f(rightRotation.getFloat("angle"), rightRotation.getFloat("x"), rightRotation.getFloat("y"), rightRotation.getFloat("z"));
        		//System.out.println(axisAngle4f.toString());
        		this.rightRotation.setValue(new Quaternionf(axisAngle4f));
        	}else if(transformation.hasKeyOfType("right_Rotation", 10)) {
        		final NBTTagCompound rightRotation = transformation.getCompound("right_Rotation");
        		final AxisAngle4f axisAngle4f = new AxisAngle4f(rightRotation.getFloat("angle"), rightRotation.getFloat("x"), rightRotation.getFloat("y"), rightRotation.getFloat("z"));
        		//System.out.println(axisAngle4f.toString());
        		this.rightRotation.setValue(new Quaternionf(axisAngle4f));
        	}
        	
        	this.writeTransformation();
        }
    }

	@Override
	protected Material getDestroyMaterial() {
		return Material.AIR;
	}
	
	@Override
	protected int widthField() {
		return displayField.getVersionIndex() + 19;
	}

	@Override
	protected int heightField() {
		return displayField.getVersionIndex() + 20;
	}
}
