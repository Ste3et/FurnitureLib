package de.Ste3et_C0st.FurnitureLib.ModelLoader.Block;

import de.Ste3et_C0st.FurnitureLib.ModelLoader.ModelVector;
import de.Ste3et_C0st.FurnitureLib.ModelLoader.Block.state.ModelBlockSkullState;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import java.util.Objects;
import java.util.UUID;

public class ModelBlockAquaticUpdate extends ModelBlock {

    public static final String CONFIGKEY = "projectData.blockList";
    private BlockData blockData;
    
    
    public ModelBlockAquaticUpdate(@NotNull ModelVector vector, @NotNull String str) {
        super(vector);
        this.blockData = Bukkit.createBlockData(str);
    }

    public ModelBlockAquaticUpdate(YamlConfiguration yamlConfiguration, String key) {
        super(yamlConfiguration, key);
        if (!key.isEmpty()) {
            double x = yamlConfiguration.getDouble(key + ".xOffset");
            double y = yamlConfiguration.getDouble(key + ".yOffset");
            double z = yamlConfiguration.getDouble(key + ".zOffset");
            String str = yamlConfiguration.getString(key + ".blockData", "");
            String materialStr = yamlConfiguration.getString(key + ".material", "");
            ModelVector vector = new ModelVector(x, y, z);

            if (FurnitureLib.getVersionInt() > 13) {
                if (materialStr.startsWith("WALL_SIGN")) {
                    materialStr = materialStr.replace("WALL_SIGN", "OAK_WALL_SIGN");
                    yamlConfiguration.set(key + ".material", materialStr);
                }
            }

            if (str.isEmpty()) {
                if (!materialStr.isEmpty()) {
                    String blockDataString = "minecraft:" + materialStr.toLowerCase();
                    if (yamlConfiguration.isSet(key + ".Rotation")) {
                        blockDataString += "[facing=" + yamlConfiguration.getString(key + ".Rotation") + "]";
                    }
                    str = blockDataString;
                }
            }

            try {
                this.blockData = Bukkit.createBlockData(str.toLowerCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Furniture Model File: " + yamlConfiguration.getCurrentPath() + " make Problems with:");
                System.out.println("Parsing of: " + str + " -> (" + key + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.vector = vector;
            
            if(Objects.nonNull(this.blockData)) {
            	if(Material.PLAYER_HEAD == this.blockData.getMaterial()) {
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
            }
        }
    }

    public static boolean isSolid(Block b) {
        boolean bool = false;
        BlockData blockData = b.getBlockData();
        if (blockData.getMaterial().name().contains("_FENCE")) return true;
        if (blockData.getMaterial().name().contains("_WALL")) return true;
        if (blockData instanceof Slab) {
            return !((Slab) blockData).getType().name().equalsIgnoreCase("BOTTOM");
        }
        if (blockData instanceof Stairs) {
            return !((Stairs) blockData).getHalf().name().equalsIgnoreCase("BOTTOM");
        }
        return bool;
    }

    @Override
    public Material getMaterial() {
        return Objects.nonNull(blockData) ? blockData.getMaterial() : null;
    }

    @Override
    public void place(Location loc) {
        loc.getBlock().setBlockData(this.blockData, false);
        this.applyBlockState(loc);
    }

    @Override
    public void place(Location loc, BlockFace face) {
        if (Objects.nonNull(blockData)) {
            BlockData data = this.blockData.clone();

            if (data instanceof Directional) {
                Directional directional = (Directional) data;
                BlockFace originalBlockFace = directional.getFacing();

                float originalYaw = FurnitureLib.getInstance().getLocationUtil().FaceToYaw(originalBlockFace);
                float yawDirection = FurnitureLib.getInstance().getLocationUtil().FaceToYaw(face);
                float newYaw = originalYaw + yawDirection;

                directional.setFacing(FurnitureLib.getInstance().getLocationUtil().yawToFace(newYaw));
            }

            /*
             * PaperLib.getChunkAtAsync(loc).thenRun(new Runnable() {
				@Override
				public void run() {
					loc.getBlock().setBlockData(data, false);
				}
			});
             * 
             */
            
            loc.getBlock().setBlockData(data, false);
            this.applyBlockState(loc);
        }
    }
    
    public boolean isPlaceable() {
        return false;
    }
}
