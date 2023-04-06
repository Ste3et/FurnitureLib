package de.Ste3et_C0st.FurnitureLib.Utilitis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.Ste3et_C0st.FurnitureLib.NBT.NBTTagCompound;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.io.*;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class MaterialConverter {

    public static HashMap<String, String> nmsMap = new HashMap<String, String>();

    private static boolean loadJsonObject() {
        if (nmsMap.isEmpty()) {
            try {
                JsonParser parser = new JsonParser();

                File json = new File("plugins/" + FurnitureLib.getInstance().getName() + "/id.json");
                if (!json.exists()) {
                    try {
                        File targetFile = new File("plugins/" + FurnitureLib.getInstance().getName() + "/id.json");
                        OutputStream outStream = new FileOutputStream(targetFile);
                        BufferedOutputStream bos = new BufferedOutputStream(outStream);
                        InputStream fis = FurnitureLib.getInstance().getResource("id.json");
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        byte[] buffer = new byte[1024 * 1024 * 10];
                        int n = -1;
                        while ((n = bis.read(buffer)) != -1) {
                            bos.write(buffer, 0, n);
                        }
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                JsonArray array = parser.parse(new FileReader("plugins/" + FurnitureLib.getInstance().getName() + "/id.json")).getAsJsonArray();
                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    String id = obj.get("Item ID").getAsString();
                    if (id.contains(":")) {
                        id = id.split(":")[1];
                    } else {
                        id = "0";
                    }
                    nmsMap.put(obj.get("Minecraft ID").getAsString() + ":" + id + "s", obj.get("1.13 ID").getAsString());
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return true;
        }
        return false;
    }

    public static NBTTagCompound convertNMSItemStack(NBTTagCompound compound) {
        if (loadJsonObject()) {
            String s = compound.getString("id") + ":" + compound.getString("Damage");
            if (nmsMap.containsKey(s)) {
                compound.setString("id", nmsMap.get(s));
            }
        }

        return compound;

    }

    public static Material getMaterialFromOld(String material) {
        if (Objects.nonNull(material)) {
            Material mat = Material.AIR;
            String subID = "0";
            int sub = 0, matID = 0;
            if (material.contains(":")) {
                String[] arr = material.split(":");
                material = arr[0];
                subID = arr[1];
            }

            if (!subID.equalsIgnoreCase("0")) {
                try {
                    sub = Integer.parseInt(subID);
                } catch (NumberFormatException e) {
				}
            }

            try {
                matID = Integer.parseInt(material);
            } catch (NumberFormatException e) {
			}

            try {
                Material materi = Material.valueOf(material.toUpperCase());
                if (materi != null) {
                    return materi;
                }
            } catch (Exception e) {
            }

            if (matID != 0) {
                mat = convertMaterial(matID, (byte) sub);
            } else {
                if (material.equalsIgnoreCase("0")) return Material.AIR;
                Material m = Material.valueOf("LEGACY_" + material.toUpperCase());
                if (m == null) {
                    mat = convertMaterial(matID, (byte) sub);
                    if (mat == null) {
                        return mat;
                    }
                } else return convertMaterial(m.getId(), (byte) sub);
            }
            return mat;
        }
        return null;
    }

    public static Material convertMaterial(int ID, byte Data) {
        if (FurnitureLib.isNewVersion()) {
            Material mat = EnumSet.allOf(Material.class).stream().filter(Material::isLegacy).filter(m -> m.getId() == ID).findFirst().orElse(null);
            if (mat != null) mat = Bukkit.getUnsafe().fromLegacy(new MaterialData(mat, Data));
            return mat;
        } else {
            try {
                Class<?> materialClass = Material.class;
                Method m = materialClass.getMethod("getMaterial", int.class);
				return (Material) m.invoke(null, ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static String updateChangedMaterial(String string) {
    	if(FurnitureLib.getVersionInt() > 16) {
    		string = string.replace("grass_path", "dirt_path");
    	}
    	return string;
    }
}
