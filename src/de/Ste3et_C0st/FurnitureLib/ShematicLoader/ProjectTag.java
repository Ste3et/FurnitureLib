package de.Ste3et_C0st.FurnitureLib.ShematicLoader;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;

public class ProjectTag{
	
	public static enum Tag{
		DOORS(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("_DOOR")).collect(Collectors.toList())),
		PLANKS(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("PLANKS")).collect(Collectors.toList())),
		SAPLING(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("SAPLING")).collect(Collectors.toList())),
		LEAVES(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("LEAVES")).collect(Collectors.toList())),
		BED(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("BED")).collect(Collectors.toList())),
		WOOL(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("WOOL")).collect(Collectors.toList())),
		TRAPDOOR(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("TRAP_DOOR")).collect(Collectors.toList())),
		FENCE(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("_FENCE")).collect(Collectors.toList())),
		
		CARPET(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("CARPET")).collect(Collectors.toList())),
		STAINED_GLASS_PANE(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("_STAINED_GLASS_PANE")).collect(Collectors.toList())),
		STAINED_GLASS(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("STAINED_GLASS")).collect(Collectors.toList())),
		SHULKER_BOX(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("SHULKER_BOX")).collect(Collectors.toList())),
		CONCRETE(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("CONCRETE")).collect(Collectors.toList())),
		CONCRETE_POWDER(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("CONCRETE_POWDER")).collect(Collectors.toList())),
		CORAL_BLOCK(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("CORAL_BLOCK")).collect(Collectors.toList())),
		ICE(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("ICE")).collect(Collectors.toList())),
		SLAB(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("SLAB")).collect(Collectors.toList())),
		STAIRS(Arrays.stream(Material.values()).filter(mat -> mat.name().endsWith("STAIRS")).collect(Collectors.toList())),
		WOOD_SLAB(Material.OAK_SLAB,Material.SPRUCE_SLAB,Material.BIRCH_SLAB,Material.JUNGLE_SLAB,Material.ACACIA_SLAB,Material.DARK_OAK_SLAB),
		WOOD_STAIRS(Material.OAK_STAIRS,Material.SPRUCE_STAIRS,Material.BIRCH_STAIRS,Material.JUNGLE_STAIRS,Material.ACACIA_STAIRS,Material.DARK_OAK_STAIRS),
		SOLID_BLOCKS(Arrays.stream(Material.values()).filter(mat -> mat.isBlock() && mat.isSolid()).collect(Collectors.toList()));
		
		List<Material> matList = null;
		
		Tag(Material ... material){
			this.matList = Arrays.stream(material).collect(Collectors.toList());
		}
		
		Tag(List<Material> material){
			this.matList = material;
		}
		
		public List<Material> getList(){
			return this.matList;
		}
	}
}
