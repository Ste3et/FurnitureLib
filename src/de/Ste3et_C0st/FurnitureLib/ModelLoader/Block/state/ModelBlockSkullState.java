package de.Ste3et_C0st.FurnitureLib.ModelLoader.Block.state;

import java.lang.reflect.Field;
import java.util.Objects;

import org.bukkit.SkullType;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;

import com.comphenix.protocol.wrappers.WrappedGameProfile;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class ModelBlockSkullState extends ModelBlockState{

	private WrappedGameProfile gameProfile = null;
	
	public ModelBlockSkullState(WrappedGameProfile profile) {
		this.gameProfile = profile;
	}
	
	public boolean haveGameProfile() {
		return Objects.nonNull(gameProfile);
	}
	
	public WrappedGameProfile getProfile() {
		return gameProfile;
	}
	
	@Override
	public void updateState(BlockState state) {
		if(Skull.class.isInstance(state)) {
			if(haveGameProfile()) {
				Skull skull = Skull.class.cast(state);
				try {
					Class<?> craftSkullClazz = Class.forName("org.bukkit.craftbukkit." + FurnitureLib.getBukkitVersion() + ".block.CraftSkull");
					if(craftSkullClazz.isInstance(skull)) {
						Object craftSkull = craftSkullClazz.cast(skull);
						Field field = craftSkullClazz.getDeclaredField("profile");
						field.setAccessible(true);
						if(Objects.nonNull(gameProfile)) {
							skull.setSkullType(SkullType.PLAYER);
							field.set(craftSkull, gameProfile.getHandle());
							skull = Skull.class.cast(craftSkull);
							super.updateState(skull);
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
