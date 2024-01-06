package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.Utilitis.Relative;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;
import java.util.HashMap;
import java.util.Objects;

public class fArmorStand extends fContainerEntity{

    public static EntityType type = EntityType.ARMOR_STAND;
    private int armorstandID;
    
    private DefaultKey<Boolean> arms = new DefaultKey<Boolean>(false), small = new DefaultKey<Boolean>(false), marker = new DefaultKey<Boolean>(true), basePlate = new DefaultKey<Boolean>(true);
    
    private HashMap<BodyPart, DefaultKey<EulerAngle>> angle = new HashMap<Type.BodyPart, DefaultKey<EulerAngle>>();
    private ArmorStand entity = null;
    private final DefaultKey<EntitySize> entitySize = new DefaultKey<EntitySize>(new EntitySize(0.5, 1.975));
    
    @SuppressWarnings("deprecation")
    public fArmorStand(Location loc, ObjectID obj) {
        super(loc, type, FurnitureLib.isNewVersion() ? 1 : type.getTypeId(), obj);
        angle.put(BodyPart.BODY, new DefaultKey<EulerAngle>(BodyPart.BODY.getDefAngle()));
        angle.put(BodyPart.HEAD, new DefaultKey<EulerAngle>(BodyPart.HEAD.getDefAngle()));
        angle.put(BodyPart.LEFT_ARM, new DefaultKey<EulerAngle>(BodyPart.LEFT_ARM.getDefAngle()));
        angle.put(BodyPart.RIGHT_ARM, new DefaultKey<EulerAngle>(BodyPart.RIGHT_ARM.getDefAngle()));
        angle.put(BodyPart.LEFT_LEG, new DefaultKey<EulerAngle>(BodyPart.LEFT_LEG.getDefAngle()));
        angle.put(BodyPart.RIGHT_LEG, new DefaultKey<EulerAngle>(BodyPart.RIGHT_LEG.getDefAngle()));
    }

    public EulerAngle getBodyPose() {
        return getPose(BodyPart.BODY);
    }

    public fArmorStand setBodyPose(EulerAngle a) {
        setPose(a, BodyPart.BODY);
        return this;
    }

    public EulerAngle getLeftArmPose() {
        return getPose(BodyPart.LEFT_ARM);
    }

    public fArmorStand setLeftArmPose(EulerAngle a) {
        setPose(a, BodyPart.LEFT_ARM);
        return this;
    }

    public EulerAngle getRightArmPose() {
        return getPose(BodyPart.RIGHT_ARM);
    }

    public fArmorStand setRightArmPose(EulerAngle a) {
        setPose(a, BodyPart.RIGHT_ARM);
        return this;
    }

    public EulerAngle getLeftLegPose() {
        return getPose(BodyPart.LEFT_LEG);
    }

    public fArmorStand setLeftLegPose(EulerAngle a) {
        setPose(a, BodyPart.LEFT_LEG);
        return this;
    }

    public EulerAngle getRightLegPose() {
        return getPose(BodyPart.RIGHT_LEG);
    }

    public fArmorStand setRightLegPose(EulerAngle a) {
        setPose(a, BodyPart.RIGHT_LEG);
        return this;
    }

    public EulerAngle getHeadPose() {
        return getPose(BodyPart.HEAD);
    }

    public fArmorStand setHeadPose(EulerAngle a) {
        setPose(a, BodyPart.HEAD);
        return this;
    }

    public int getArmorID() {
        return this.armorstandID;
    }

    public boolean hasArms() {
        return this.arms.getOrDefault();
    }

    public boolean hasBasePlate() {
        return this.basePlate.getOrDefault();
    }

    public boolean isMarker() {
        return this.marker.getOrDefault();
    }

    public fArmorStand setMarker(boolean marker) {
        setBitMask(!marker, Type.field.getBitMask(), 4);
        this.marker.setValue(Boolean.valueOf(marker));
        return this;
    }

    public boolean isSmall() {
        return this.small.getOrDefault();
    }

    public fArmorStand setSmall(boolean small) {
        setBitMask(small, Type.field.getBitMask(), 0);
        this.small.setValue(Boolean.valueOf(small));
        if(small) {
        	this.entitySize.setValue(new EntitySize(0.25, 0.9875));
        }else {
        	this.entitySize.setValue(this.entitySize.getDefault());
        }
        return this;
    }

    public boolean isRealEntity() {
		return entity != null;
	}

    public void setEntity(Entity entity) {
        if (entity instanceof ArmorStand) this.entity = (ArmorStand) entity;
    }

    public fArmorStand clone(Relative relative) {
        return clone(relative.getSecondLocation());
    }

    public fArmorStand clone(Location location) {
        fArmorStand entity = clone();
        entity.setLocation(location);
        return entity;
    }

    public EulerAngle getPose(BodyPart part) {
        if (!angle.containsKey(part)) {
            return part.getDefAngle();
        }
        return angle.get(part).getOrDefault();
    }

    public fArmorStand setPose(EulerAngle angle, BodyPart part) {
        if (angle == null) {
            return this;
        }
        if (part == null) {
            return this;
        }
        DefaultKey<EulerAngle> defaultAngle = this.angle.get(part);
        defaultAngle.setValue(angle);
        angle = FurnitureLib.getInstance().getLocationUtil().Radtodegress(angle);
        getWatcher().setObject(new WrappedDataWatcherObject(part.getField(), Registry.getVectorSerializer()), new Vector3F((float) angle.getX(), (float) angle.getY(), (float) angle.getZ()));
        return this;
    }

    public fArmorStand setArms(boolean arms) {
        setBitMask(arms, Type.field.getBitMask(), 2);
        this.arms.setValue(Boolean.valueOf(arms));
        return this;
    }

    public fArmorStand setBasePlate(boolean basePlate) {
        setBitMask(!basePlate, Type.field.getBitMask(), 3);
        this.basePlate.setValue(Boolean.valueOf(basePlate));
        return this;
    }

    @Override
    public fArmorStand clone() {
        fArmorStand nStand = new fArmorStand(null, getObjID());
        this.copyMetadata(nStand);
        return nStand;
    }

    public ArmorStand toRealEntity() {
        if (entity != null) {
            if (!entity.isDead()) {
                return entity;
            }
        }
        entity = (ArmorStand) getObjID().getWorld().spawnEntity(getLocation(), getEntityType());
        entity.setArms(this.hasArms());
        entity.setVisible(!this.isInvisible());
        entity.setSmall(isSmall());
        entity.setArms(hasArms());
        entity.setGravity(hasGravity());
        entity.setGlowing(isGlowing());
        entity.setAI(false);
        entity.setHeadPose(getHeadPose());
        entity.setLeftArmPose(getLeftArmPose());
        entity.setRightArmPose(getRightArmPose());
        entity.setLeftLegPose(getLeftLegPose());
        entity.setRightLegPose(getRightLegPose());
        entity.setBodyPose(getBodyPose());

        entity.setCustomName(getCustomName());
        entity.setCustomNameVisible(isCustomNameVisible());

        entity.setBasePlate(hasBasePlate());
        entity.setHelmet(getHelmet());
        entity.setChestplate(getChestPlate());
        entity.setLeggings(getLeggings());
        entity.setBoots(getBoots());
        return entity;
    }

    public NBTTagCompound getMetaData() {
    	super.getMetaData();
    	if(!this.arms.isDefault()) setMetadata("ShowArms", this.hasArms());
    	if(!this.basePlate.isDefault()) setMetadata("NoBasePlate", this.hasBasePlate());
    	if(!this.gravity.isDefault()) setMetadata("Gravity", this.hasGravity());
    	if(this.marker.isDefault()) setMetadata("Marker", this.marker.getOrDefault());
    	if(!this.small.isDefault()) setMetadata("Small", this.isSmall());
    	
    	NBTTagCompound eulerAngle = new NBTTagCompound();
    	this.angle.entrySet().stream().filter(entry -> !entry.getValue().isDefault()).forEach(entry -> {
    		 EulerAngle angle = entry.getValue().getValue();
             NBTTagCompound partAngle = new NBTTagCompound();
             partAngle.setDouble("X", angle.getX());
             partAngle.setDouble("Y", angle.getY());
             partAngle.setDouble("Z", angle.getZ());
             eulerAngle.set(entry.getKey().getMojangName(), partAngle);
    	});
    	
    	if(!eulerAngle.isEmpty()) set("Pose", eulerAngle);
    	
        return getNBTField();
    }

    @SuppressWarnings("unchecked")
	@Override
    public void loadMetadata(NBTTagCompound metadata) {
        super.loadMetadata(metadata);
        
        if(metadata.hasKeyOfType("EulerAngle", 10)) {
        	NBTTagCompound euler = metadata.getCompound("EulerAngle");
        	euler.c().stream().forEach(entry -> {
        		String name = (String) entry;
        		BodyPart part = BodyPart.valueOf(name.toUpperCase());
        		this.setPose(eulerAngleFetcher(euler.getCompound(name)), part);
        	});
        }else if(metadata.hasKeyOfType("Pose", 10)) {
        	NBTTagCompound euler = metadata.getCompound("Pose");
        	euler.c().stream().forEach(entry -> {
        		BodyPart.match((String) entry).ifPresent(bodyPart -> {
        			this.setPose(eulerAngleFetcher(euler.getCompound((String) entry)), bodyPart);
        		});
        	});
        }

        this.setBasePlate((metadata.getInt("NoBasePlate", metadata.getInt("BasePlate")) == 1))
        	.setSmall((metadata.getInt("Small") == 1))
        	.setMarker((metadata.getInt("Marker") == 1))
        	.setArms(metadata.getInt("ShowArms", metadata.getInt("Arms")) == 1)
        	.setGravity(metadata.getInt("Gravity") == 1);
    }

    private EulerAngle eulerAngleFetcher(NBTTagCompound eularAngle) {
        double X = eularAngle.getDouble("X");
        double Y = eularAngle.getDouble("Y");
        double Z = eularAngle.getDouble("Z");
        return new EulerAngle(X, Y, Z);
    }

	@Override
	public void copyMetadata(final fEntity entity) {
		fArmorStand stand = this.getClass().cast(entity);
		
		for (int i = 0; i < 7; i++) {
            if (this.getInventory().getSlot(i) == null) continue;
            stand.getInventory().setSlot(i, this.getInventory().getSlot(i).clone());
        }
		
		stand.setSmall(this.isSmall());
		stand.setInvisible(this.isInvisible());
		stand.setMarker(this.isMarker());
        stand.setGlowing(this.isGlowing());
        stand.setArms(this.hasArms());
        stand.setBasePlate(this.hasBasePlate());
        stand.setFire(this.isFire());
        stand.setName(this.getCustomName());
        stand.setNameVisibility(this.isCustomNameVisible());
        
        for (BodyPart part : BodyPart.values()) {
        	stand.setPose(this.getPose(part), part);
        }
	}
	
	public EntitySize getEntitySize() {
		return this.entitySize.getOrDefault();
	}

	@Override
	protected Material getDestroyMaterial() {
		if(Objects.nonNull(getHelmet())) {
			if(getHelmet().getType().isBlock()) {
				return getHelmet().getType();
			}
		}else if(Objects.nonNull(getItemInMainHand())) {
			if(getItemInMainHand().getType().isBlock()) {
				return getItemInMainHand().getType();
			}
		}
		return Material.AIR;
	}
    
//	@Override
//	public BoundingBox getBoundingBox() {
//		return this.isSmall() ? new BoundingBox(x1, y1, z1) : BoundingBox.;
//	}
	
//	public void debug() {
//		Stream.of(BodyPart.values()).forEach(entry -> {
//			if(entry == BodyPart.HEAD) {
//				ParticleBuilder builder = new ParticleBuilder(Particle.FLAME);
//				builder.location(getLocation().add(0, 1.3, 0));
//				builder.offset(0, 0, 0);
//				builder.count(1);
//				builder.allPlayers();
//				builder.spawn();
//			}
//		});
//	}
}
