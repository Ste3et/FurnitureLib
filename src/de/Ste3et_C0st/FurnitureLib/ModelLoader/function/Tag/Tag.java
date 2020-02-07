package de.Ste3et_C0st.FurnitureLib.ModelLoader.function.Tag;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class Tag {

    public String tagString;
    public HashSet<MaterialData> materialDatas = new HashSet<MaterialData>();

    public Tag(String str, Material... material) {
        this.tagString = str;
        for (Material mat : material) {
            materialDatas.add(new MaterialData(mat));
        }
    }

    public Tag(String str, MaterialData... data) {
        this.tagString = str;
        Collections.addAll(this.materialDatas, data);
    }

    public Tag(String str, String... query) {
        this.tagString = str;
        for (String q : query) {
            MaterialData data = null;
            if (!q.chars().allMatch(Character::isLetter) && FurnitureLib.getVersionInt() < 13) {
                int id = 0;
                int subId = 0;
                if (q.contains(":")) {
                    String[] split = q.split(":");
                    id = split.length > 0 ? parseStringToInt(split[0]) : 0;
                    subId = split.length > 1 ? parseStringToInt(split[1]) : 0;
                } else {
                    id = parseStringToInt(q);
                }
                data = new MaterialData(getMaterial(id), (byte) subId);
            } else {
                try {
                    Material material = Material.valueOf(q);
                    data = new MaterialData(material);
                } catch (Exception e) {
                }
            }
            if (Objects.nonNull(data)) {
                this.materialDatas.add(data);
            }
        }
    }

    private int parseStringToInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    private Material getMaterial(int i) {
        Class<Material> materialClass = Material.class;
        try {
            Method method = materialClass.getMethod("getMaterial", int.class);
            return (Material) method.invoke(i);
        } catch (Exception e) {
            return null;
        }
    }
}