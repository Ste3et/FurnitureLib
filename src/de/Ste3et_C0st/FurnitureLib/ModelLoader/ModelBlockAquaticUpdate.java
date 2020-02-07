package de.Ste3et_C0st.FurnitureLib.ModelLoader;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import de.Ste3et_C0st.FurnitureLib.main.Type;
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

import java.util.Objects;

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

            if (Type.version.equalsIgnoreCase("1.14") || Type.version.equalsIgnoreCase("1.15")) {
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

            loc.getBlock().setBlockData(data, false);
        }
    }

    public boolean isPlaceable() {
        return false;
    }
}
