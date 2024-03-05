package de.Ste3et_C0st.FurnitureLib.main.entity;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagList;
import de.Ste3et_C0st.FurnitureLib.Utilitis.BoundingBox;
import de.Ste3et_C0st.FurnitureLib.Utilitis.DefaultKey;
import de.Ste3et_C0st.FurnitureLib.Utilitis.EntitySize;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import de.Ste3et_C0st.FurnitureLib.main.Type.BodyPart;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class fArmorStand extends fContainerEntity implements SizeableEntity, Interactable{

    public static EntityType type = EntityType.ARMOR_STAND;
    private int armorstandID;
    
    private DefaultKey<Boolean> arms = new DefaultKey<Boolean>(false), small = new DefaultKey<Boolean>(false), marker = new DefaultKey<Boolean>(true), basePlate = new DefaultKey<Boolean>(true);
    
    private HashMap<BodyPart, DefaultKey<EulerAngle>> angle = new HashMap<Type.BodyPart, DefaultKey<EulerAngle>>();
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

    public fArmorStand copyEntity(LivingEntity entity) {
        if (entity instanceof ArmorStand) {
        	final ArmorStand stand = ArmorStand.class.cast(entity);
        	this.setSmall(stand.isSmall());
        	this.setBasePlate(stand.hasBasePlate());
        	this.setMarker(stand.isMarker());
        	this.setArms(stand.hasArms());
        	this.setHeadPose(stand.getHeadPose());
        	this.setBodyPose(stand.getBodyPose());
        	this.setLeftArmPose(stand.getLeftArmPose());
        	this.setRightArmPose(stand.getRightArmPose());
        	this.setLeftLegPose(stand.getLeftLegPose());
        	this.setRightLegPose(stand.getRightLegPose());
        	this.setEntityEquipment(stand.getEquipment());
        }
        return this;
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
    protected void writeAdditionalSaveData() {
    	super.writeInventoryData();
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
    }

    @SuppressWarnings("unchecked")
	@Override
	protected void readAdditionalSaveData(NBTTagCompound metadata) {
    	super.readInventorySaveData(metadata);
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
        			final String key = (String) entry;
        			
        			euler.getCompound(key, NBTTagCompound.class, compound -> {
        				this.setPose(eulerAngleFetcher(euler.getCompound((String) entry)), bodyPart);
        			});
        			
        			euler.getCompound(key, NBTTagList.class, tagList -> {
        				EulerAngle defEngle = bodyPart.getDefAngle();
        				double eulerAngles[] = {defEngle.getX(), defEngle.getY(), defEngle.getZ()};
        				for(int i  = 0; i < 3; i++) {
        					if(i >= tagList.size()) break;
        					eulerAngles[i] = tagList.getFloat(i);
        				}
        				this.setPose(eulerAngleFetcher(eulerAngles), bodyPart);
        			});
        		});
        	});
        }

        this.setBasePlate((metadata.getInt("NoBasePlate", metadata.getInt("BasePlate")) == 1))
        	.setSmall((metadata.getInt("Small") == 1))
        	.setMarker((metadata.getInt("Marker") == 1))
        	.setArms(metadata.getInt("ShowArms", metadata.getInt("Arms")) == 1)
        	.setGravity(metadata.getInt("Gravity") == 1);
    }
    
    private EulerAngle eulerAngleFetcher(double[] eulerAngle) {
    	EulerAngle eulerAngleReturn = FurnitureLib.getInstance().getLocationUtil().degresstoRad(new EulerAngle(eulerAngle[0], eulerAngle[1], eulerAngle[2]));
    	return eulerAngleReturn;
    }

    private EulerAngle eulerAngleFetcher(NBTTagCompound eularAngle) {
        double X = eularAngle.getDouble("X");
        double Y = eularAngle.getDouble("Y");
        double Z = eularAngle.getDouble("Z");
        return new EulerAngle(X, Y, Z);
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

	@Override
	public BoundingBox getBoundingBox() {
		return this.entitySize.getOrDefault().toBoundingBox();
	}

	@Override
	public boolean canInteractWith() {
		return this.isMarker();
	}
	
//	public Optional<BoundingBox> getTipLocation(BodyPart bodyPart) {
//		final Location entityLocation = this.getLocation().clone();
//		entityLocation.setYaw(this.getLocation().getYaw() + 90);
//		final Vector direction = entityLocation.getDirection();
//		final EulerAngle eulerAngle = this.getPose(bodyPart);
//		
//		entityLocation.setX(entityLocation.getX() + 5f / 16f * direction.getX());
//		entityLocation.setY(entityLocation.getY() + 22f / 16f);
//		entityLocation.setZ(entityLocation.getZ() + 5f / 16f * direction.getZ());
//        Vector armDir = getDirection(eulerAngle.getY(), eulerAngle.getX(), -eulerAngle.getZ());
//        armDir = rotateAroundAxisY(armDir, Math.toRadians(entityLocation.getYaw()-90f));
//        entityLocation.setX(entityLocation.getX() + 10f / 16f * armDir.getX());
//        entityLocation.setY(entityLocation.getY() + 10f / 16f * armDir.getY());
//        entityLocation.setZ(entityLocation.getZ() + 10f / 16f * armDir.getZ());
//		
//		return Optional.of(BoundingBox.of(entityLocation.clone().add(.25, .25, .25), entityLocation.clone().add(- .25, - .25, - .25)));
//	}
//	
//    private static Vector getDirection(Double yaw, Double pitch, Double roll) {
//        Vector v = new Vector(0, -1, 0);
//        v = rotateAroundAxisX(v, pitch);
//        v = rotateAroundAxisY(v, yaw);
//        v = rotateAroundAxisZ(v, roll);
//        return v;
//    }
//    
//    private static Vector rotateAroundAxisX(Vector v, double angle) {
//        double y, z, cos, sin;
//        cos = Math.cos(angle);
//        sin = Math.sin(angle);
//        y = v.getY() * cos - v.getZ() * sin;
//        z = v.getY() * sin + v.getZ() * cos;
//        return v.setY(y).setZ(z);
//    }
//
//    private static Vector rotateAroundAxisY(Vector v, double angle) {
//        angle = -angle;
//        double x, z, cos, sin;
//        cos = Math.cos(angle);
//        sin = Math.sin(angle);
//        x = v.getX() * cos + v.getZ() * sin;
//        z = v.getX() * -sin + v.getZ() * cos;
//        return v.setX(x).setZ(z);
//    }
//
//    private static Vector rotateAroundAxisZ(Vector v, double angle) {
//        double x, y, cos, sin;
//        cos = Math.cos(angle);
//        sin = Math.sin(angle);
//        x = v.getX() * cos - v.getY() * sin;
//        y = v.getX() * sin + v.getY() * cos;
//        return v.setX(x).setY(y);
//    }
}
