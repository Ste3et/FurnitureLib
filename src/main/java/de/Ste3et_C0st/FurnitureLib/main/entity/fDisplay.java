package de.Ste3et_C0st.FurnitureLib.main.entity;

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

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;

public abstract class fDisplay extends fSize{
	
	private DefaultKey<Vector3f> translation = new DefaultKey<Vector3f>(new Vector3f()), scale = new DefaultKey<Vector3f>(new Vector3f());
	private DefaultKey<AxisAngle4f> leftRotation = new DefaultKey<AxisAngle4f>(new AxisAngle4f()), rightRotation = new DefaultKey<AxisAngle4f>(new AxisAngle4f());
	
	private DefaultKey<Billboard> billboard = new DefaultKey<Billboard>(Billboard.FIXED);
	private DefaultKey<Float> viewRange = new DefaultKey<Float>(1f), shadowRadius = new DefaultKey<Float>(0f), shadowStrength = new DefaultKey<Float>(1f);
	private DefaultKey<Integer> interpolationDelay = new DefaultKey<Integer>(0), interpolationDuration = new DefaultKey<Integer>(0), brightness = new DefaultKey<Integer>(-1), glow_override = new DefaultKey<Integer>(-1);
	
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
		this.leftRotation.setValue(rotAngle4f);
		this.writeTransformation();
	}
	
	public void setRightRotation(AxisAngle4f rotAngle4f) {
		this.rightRotation.setValue(rotAngle4f);
		this.writeTransformation();
	}
	
	public int getInterpolationDelay() {
		return this.interpolationDelay.getOrDefault();
	}
	
	public AxisAngle4f getLeftRotationObj() {
		return this.leftRotation.getOrDefault();
	}
	
	public AxisAngle4f getRightRotationObj() {
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
		getWatcher().setObject(new WrappedDataWatcherObject(10, Registry.get(Vector3f.class)), transformation.getTranslation());
		getWatcher().setObject(new WrappedDataWatcherObject(11, Registry.get(Vector3f.class)), transformation.getScale());
		getWatcher().setObject(new WrappedDataWatcherObject(12, Registry.get(Quaternionf.class)), transformation.getLeftRotation());
		getWatcher().setObject(new WrappedDataWatcherObject(13, Registry.get(Quaternionf.class)), transformation.getRightRotation());
	}
	
	public fDisplay setBillboard(Billboard billboard) {
		this.billboard.setValue(billboard);
		getWatcher().setObject(new WrappedDataWatcherObject(14, Registry.get(Byte.class)), (byte) this.getBillboard().ordinal());
		return this;
	}
	
	public fDisplay setBrightness(int i) {
		this.brightness.setValue(i);
		getWatcher().setObject(new WrappedDataWatcherObject(15, Registry.get(Integer.class)), this.getBrightness());
		return this;
	}
	
	public fDisplay setViewRange(float f) {
		this.viewRange.setValue(f);
		getWatcher().setObject(new WrappedDataWatcherObject(16, Registry.get(Float.class)), this.getViewRange());
		return this;
	}
	
	public fDisplay setShadowRadius(float f) {
		this.shadowRadius.setValue(f);
		getWatcher().setObject(new WrappedDataWatcherObject(17, Registry.get(Float.class)), this.getShadowRadius());
		return this;
	}
	
	public fDisplay setShadowStrength(float f) {
		this.shadowStrength.setValue(f);
		getWatcher().setObject(new WrappedDataWatcherObject(18, Registry.get(Float.class)), this.getShadowStrength());
		return this;
	}
	
	public fDisplay setGlowOverride(int i) {
		this.glow_override.setValue(i);
		getWatcher().setObject(new WrappedDataWatcherObject(21, Registry.get(Integer.class)), this.getGlowOverride());
		return this;
	}
	
    public NBTTagCompound getMetaData() {
    	super.getMetaData();
    	if(!this.viewRange.isDefault()) setMetadata("viewRange", this.getViewRange());
    	if(!this.shadowRadius.isDefault()) setMetadata("shadowRadius", this.getShadowRadius());
    	if(!this.shadowStrength.isDefault()) setMetadata("shadowStrength", this.getShadowStrength());
    	if(!this.glow_override.isDefault()) setMetadata("glow_override", this.getGlowOverride());
    	if(!this.interpolationDelay.isDefault()) setMetadata("interpolationDelay", this.getInterpolationDelay());
    	if(!this.interpolationDuration.isDefault()) setMetadata("interpolationDuration", this.getInterpolationDuration());
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
    	
    	if(this.leftRotation.isDefault() == false) {
    		final NBTTagCompound leftRotation = new NBTTagCompound();
        	final AxisAngle4f axis = this.rightRotation.getOrDefault();
    		leftRotation.setFloat("angle", axis.angle);
    		leftRotation.setFloat("x", axis.x);
    		leftRotation.setFloat("y", axis.y);
    		leftRotation.setFloat("z", axis.z);
    		transformation.set("leftRotation", leftRotation);
    	}
    	
    	if(this.rightRotation.isDefault() == false) {
    		final NBTTagCompound rightRotation = new NBTTagCompound();
        	final AxisAngle4f axis = this.rightRotation.getOrDefault();
        	rightRotation.setFloat("angle", axis.angle);
        	rightRotation.setFloat("x", axis.x);
        	rightRotation.setFloat("y", axis.y);
        	rightRotation.setFloat("z", axis.z);
        	transformation.set("rightRotation", rightRotation);
    	}
    	
    	if(!transformation.isEmpty()) set("transformation", transformation);
    	
        return getNBTField();
    }

	@Override
    public void loadMetadata(NBTTagCompound metadata) {
        super.loadMetadata(metadata);
        if(metadata.hasKeyOfType("billboard", 8)) this.setBillboard((Billboard.valueOf(metadata.getString("billboard"))));
        if(metadata.hasKeyOfType("viewRange", 5)) this.setViewRange((metadata.getFloat("viewRange")));
        if(metadata.hasKeyOfType("shadowRadius", 5)) this.setShadowRadius(metadata.getFloat("shadowRadius"));
        if(metadata.hasKeyOfType("shadowStrength", 5)) this.setShadowStrength((metadata.getFloat("shadowStrength")));
        if(metadata.hasKeyOfType("width", 5)) this.setWidth(metadata.getFloat("width"));
        if(metadata.hasKeyOfType("height", 5)) this.setHeight(metadata.getFloat("height"));
        if(metadata.hasKeyOfType("glow_override", 3)) this.setGlowOverride(metadata.getInt("glow_override"));
        if(metadata.hasKeyOfType("interpolationDelay", 3)) this.setInterpolationDelay(metadata.getInt("interpolationDelay"));
        if(metadata.hasKeyOfType("interpolationDuration", 3)) this.setInterpolationDuration(metadata.getInt("interpolationDuration"));
        if(metadata.hasKeyOfType("brightness", 3)) this.setBrightness(metadata.getInt("brightness"));
        
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
        		this.leftRotation.setValue(axisAngle4f);
        	}
        	
        	if(transformation.hasKeyOfType("rightRotation", 10)) {
        		final NBTTagCompound rightRotation = transformation.getCompound("rightRotation");
        		final AxisAngle4f axisAngle4f = new AxisAngle4f(rightRotation.getFloat("angle"), rightRotation.getFloat("x"), rightRotation.getFloat("y"), rightRotation.getFloat("z"));
        		this.rightRotation.setValue(axisAngle4f);
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
		return 19;
	}

	@Override
	protected int heightField() {
		return 20;
	}
}
