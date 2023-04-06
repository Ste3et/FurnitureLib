package de.Ste3et_C0st.FurnitureLib.ModelLoader.Block;

import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelVector;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.Block.state.ModelBlockSkullState;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

public class ModelBlockCombatUpdate extends ModelBlock {

    public static final String CONFIGKEY = "ProjectModels.Block";
    private Material blockMaterial;
    private byte blockbyte = 0;
    private BlockFace blockFace = null;

    public ModelBlockCombatUpdate(ModelVector vector, Material blockMaterial) {
        super(vector);
        this.blockMaterial = blockMaterial;
    }

    public ModelBlockCombatUpdate(YamlConfiguration yamlConfiguration, String key) {
        super(yamlConfiguration, key);
        double x = yamlConfiguration.getDouble(key + ".X-Offset");
        double y = yamlConfiguration.getDouble(key + ".Y-Offset");
        double z = yamlConfiguration.getDouble(key + ".Z-Offset");
        Material blockMaterial = Material.valueOf(yamlConfiguration.getString(key + ".Type"));
        byte blockbyte = (byte) yamlConfiguration.getInt(key + ".Data", 0);
        ModelVector vector = new ModelVector(x, y, z);

        if (yamlConfiguration.contains(key + ".Rotation")) {
            this.blockFace = BlockFace.valueOf(yamlConfiguration.getString(key + ".Rotation").toUpperCase());
        }

        if(blockMaterial.name().contains("SKULL") && blockbyte == 1) {
    		if(yamlConfiguration.contains(key + ".gameProfile")) {
    			String gameProfileName = yamlConfiguration.getString(key + ".gameProfile.name", null);
				UUID uuid = UUID.fromString(yamlConfiguration.getString(key + ".gameProfile.uuid"));
    			WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, gameProfileName);
    			if(yamlConfiguration.contains(key + ".gameProfile.textures")) {
    				String value = yamlConfiguration.getString(key + ".gameProfile.textures.value");
    				String signature = yamlConfiguration.getString(key + ".gameProfile.signature.value", null);
    				wrappedGameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", value, signature));
    			}
    			this.blockState = new ModelBlockSkullState(wrappedGameProfile);
    		}
    	}
        
        this.vector = vector;
        this.blockMaterial = blockMaterial;
        this.blockbyte = blockbyte;
    }

    @Override
    public Material getMaterial() {
        return this.blockMaterial;
    }

    @Override
    public void place(Location loc) {
        Block block = loc.getBlock();
        block.setType(getMaterial(), false);
        if (this.blockbyte != 0) {
            setBlockByte(block, this.blockbyte);
        }
        this.applyBlockState(loc);
    }

    @Override
    public void place(Location loc, BlockFace face) {
        Block block = loc.getBlock();
        block.setType(getMaterial(), false);
        if (this.blockbyte != 0) setBlockByte(block, this.blockbyte);
        if (Objects.nonNull(this.blockFace)) {
            BlockState state = block.getState();
            BlockFace originalBlockFace = BlockFace.NORTH;
            float originalYaw = FurnitureLib.getInstance().getLocationUtil().FaceToYaw(originalBlockFace);
            float yawDirection = FurnitureLib.getInstance().getLocationUtil().FaceToYaw(face);
            float newYaw = originalYaw + yawDirection;
            final BlockFace newFace = LocationUtil.yawToFace(newYaw);

            this.applyBlockState(loc);
            if (block.getType().name().contains("SIGN")) {
                LocationUtil util = FurnitureLib.getInstance().getLocationUtil();
                state.setRawData(util.getFacebyte(util.yawToFace(newYaw - 90)));
                state.update(true, false);
            } else if (block.getType().name().contains("BED_BLOCK")) {
                LocationUtil util = FurnitureLib.getInstance().getLocationUtil();
                int offset = blockbyte;

                switch (face) {
                    case NORTH:
                        break;
                    case EAST:
                        offset += 1;
                        break;
                    case SOUTH:
                        offset += 2;
                        break;
                    case WEST:
                        offset += 3;
                        break;
                    default:
                        break;
                }

                state.setRawData((byte) offset);
                state.update(true, false);
            } else {
                Directional directional = (Directional) state.getData();
                directional.setFacingDirection(newFace);
                state.setData((MaterialData) directional);
            }
            
            
        }
    }

    private void setBlockByte(Block block, byte b) {
        try {
            Class<?> blockClass = Block.class;
            Method setBlockByte = blockClass.getDeclaredMethod("setData", byte.class, boolean.class);
            if (Objects.nonNull(setBlockByte)) {
                setBlockByte.invoke(block, b, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
