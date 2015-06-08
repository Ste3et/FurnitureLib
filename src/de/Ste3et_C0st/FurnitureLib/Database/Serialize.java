package de.Ste3et_C0st.FurnitureLib.Database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class Serialize{
  private final ItemStack AIR = new ItemStack(Material.AIR);
  
  public String toBase64(ItemStack is){
	  try {
  		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
		if(is==null) is=AIR;
		dataOutput.writeObject(is);
		dataOutput.close();
        return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
  }

  public ItemStack fromBase64(String s){
		try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(s));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack is = (ItemStack) dataInput.readObject();
            dataInput.close();
            return is;
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
}
