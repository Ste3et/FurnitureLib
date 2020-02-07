package de.Ste3et_C0st.FurnitureLib.main;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Type {
    public static String version = "1." + FurnitureLib.getBukkitVersion().split("_")[1];
    public static ProtocolFields field = ProtocolFields.getField(version);
    static LanguageManager lang = FurnitureLib.getInstance().getLangManager();
    static List<Material> swords, spades, axt, pickaxe, hoes, weapons, tools;

    static {
        List<Material> matList = Arrays.asList(Material.values());
        swords = matList.stream().filter(mat -> mat.name().contains("SWORD")).collect(Collectors.toList());
        spades = matList.stream().filter(mat -> mat.name().contains("SHOVEL")).collect(Collectors.toList());
        axt = matList.stream().filter(mat -> mat.name().endsWith("_AXE")).collect(Collectors.toList());
        pickaxe = matList.stream().filter(mat -> mat.name().contains("PICKAXE")).collect(Collectors.toList());
        hoes = matList.stream().filter(mat -> mat.name().contains("HOE")).collect(Collectors.toList());
        weapons = Stream.concat(swords.stream(), axt.stream()).collect(Collectors.toList());
        tools = Stream.concat(axt.stream(), pickaxe.stream()).collect(Collectors.toList());
        tools.addAll(Stream.concat(hoes.stream(), spades.stream()).collect(Collectors.toList()));
    }

    public enum DataBaseType {MySQL, SQLite}

    public enum ColorType {BLOCK, BANNER}

    public enum LimitationType {PLAYER, CHUNK, WORLD}

    public enum SQLAction {SAVE, UPDATE, REMOVE, PURGE, NOTHING}

    public enum CenterType {LEFT, RIGHT, CENTER, FRONT}

    public enum PlaceableSide {TOP, BOTTOM, SIDE, WATER}
    public enum Reason {BLOCK, ENTITY}

    public enum DyeColor {
        WHITE("BONE_MEAL", "WHITE_DYE"),
        ORANGE("ORANGE_DYE", "ORANGE_DYE"),
        MAGENTA("MAGENTA_DYE", "MAGENTA_DYE"),
        LIGHT_BLUE("LIGHT_BLUE_DYE", "LIGHT_BLUE_DYE"),
        YELLOW("DANDELION_YELLOW", "YELLOW_DYE"),
        LIME("LIME_DYE", "LIME_DYE"),
        PINK("PINK_DYE", "PINK_DYE"),
        LIGHT_GRAY("LIGHT_GRAY_DYE", "LIGHT_GRAY_DYE"),
        GRAY("GRAY_DYE", "GRAY_DYE"),
        CYAN("CYAN_DYE", "CYAN_DYE"),
        PURPLE("PURPLE_DYE", "PURPLE_DYE"),
        BLUE("LAPIS_LAZULI", "BLUE_DYE"),
        BROWN("COCOA_BEANS", "BROWN_DYE"),
        GREEN("CACTUS_GREEN", "GREEN_DYE"),
        RED("ROSE_RED", "RED_DYE"),
        BLACK("INK_SAC", "BLACK_DYE");

        private String material113, material114;

        DyeColor(String material113, String material114) {
            this.material113 = material113;
            this.material114 = material114;
        }

        public static DyeColor getDyeColor(Material mat) {
            return EnumSet.allOf(DyeColor.class).stream().filter(color -> color.getMaterial().equals(mat)).findFirst().orElse(null);
        }

        public static DyeColor getDyeToReplace(Material mat) {
            return EnumSet.allOf(DyeColor.class).stream().filter(color -> mat.name().contains(color.name())).findFirst().orElse(null);
        }

        public Material getMaterial() {
            return Material.getMaterial(version.equalsIgnoreCase("1.13") ? this.material113 : this.material114);
        }

        public org.bukkit.DyeColor getDyeColor() {
            return EnumSet.allOf(org.bukkit.DyeColor.class).stream().filter(color -> color.name().equalsIgnoreCase(this.name())).findFirst().orElse(org.bukkit.DyeColor.WHITE);
        }

        public Material replaceMaterial(Material startMaterial) {
            String str = startMaterial.name().toUpperCase();
            DyeColor toReplace = EnumSet.allOf(DyeColor.class).stream().filter(color -> startMaterial.name().startsWith(color.name())).findFirst().orElse(DyeColor.WHITE);
            str = str.replace(toReplace.name(), this.name());
            return Material.valueOf(str);
        }

        public ItemStack applyToBannerBase(ItemStack banner) {
            ItemMeta meta = banner.getItemMeta();
            if (meta instanceof BannerMeta) {
                BannerMeta bannerMeta = (BannerMeta) meta;
                banner.setType(replaceMaterial(banner.getType()));
                banner.setItemMeta(bannerMeta);
                return banner;
            }
            banner.setItemMeta(meta);
            return banner;
        }

        public ItemStack applyToItemStack(ItemStack stack) {
            ItemMeta meta = stack.getItemMeta();
            stack.setType(replaceMaterial(stack.getType()));
            stack.setItemMeta(meta);
            return stack;
        }
    }

    public enum BodyPart {
        HEAD("Head", Type.field.getHeadRotation(), new EulerAngle(0D, 0D, 0D)),
        BODY("Body", Type.field.getBodyRotation(), new EulerAngle(0D, 0D, 0D)),
        LEFT_ARM("Left_Arm", Type.field.getLeftArmRotation(), new EulerAngle(-0.174533, 0.0D, -0.174533)),
        RIGHT_ARM("Right_Arm", Type.field.getRightArmRotation(), new EulerAngle(-0.261799, 0.0D, 0.174533)),
        LEFT_LEG("Left_Leg", Type.field.getLeftLegRotation(), new EulerAngle(-0.0174533, 0.0D, -0.0174533)),
        RIGHT_LEG("Right_Leg", Type.field.getRightLegRotation(), new EulerAngle(0.0174533, 0.0D, 0.0174533));

        String name;
        EulerAngle angle;
        int field;

        BodyPart(String name, int field, EulerAngle angle) {
            this.name = name;
            this.field = field;
            this.angle = angle;
        }

        public String getName() {
            return name;
        }

        public int getField() {
            return field;
        }

        public EulerAngle getDefAngle() {
            return angle;
        }
    }


    public enum ProtocolFields {
        Spigot19(10, 11, 12, 13, 14, 15, 16, 9, 7),
        Spigot110(11, 12, 13, 14, 15, 16, 17, 10, 7),
        Spigot114(13, 14, 15, 16, 17, 18, 19, 11, 8),
        Spigot115(14, 15, 16, 17, 18, 19, 20, 12, 9);

        int bitMask, HeadRotation, BodyRotation, LeftArmRotation, RightArmRotation, LeftLegRotation, RightLegRotation, wrapperBit, healthField;

        ProtocolFields(int a, int b, int c, int d, int e, int f, int g, int h, int health) {
            this.bitMask = a;
            this.HeadRotation = b;
            this.BodyRotation = c;
            this.LeftArmRotation = d;
            this.RightArmRotation = e;
            this.LeftLegRotation = f;
            this.RightLegRotation = g;
            this.wrapperBit = h;
            this.healthField = health;
        }

        public static ProtocolFields getField(String s) {
            if (s.startsWith("1.9")) {
                return Spigot19;
            }
            if (s.startsWith("1.10")) {
                return Spigot110;
            }
            if (s.startsWith("1.14")) {
                return Spigot114;
            }
            if (s.startsWith("1.15")) {
                return Spigot115;
            }
            return Spigot110;
        }

        public int getBitMask() {
            return this.bitMask;
        }

        public int getHeadRotation() {
            return this.HeadRotation;
        }

        public int getBodyRotation() {
            return this.BodyRotation;
        }

        public int getLeftArmRotation() {
            return this.LeftArmRotation;
        }

        public int getRightArmRotation() {
            return this.RightArmRotation;
        }

        public int getLeftLegRotation() {
            return this.LeftLegRotation;
        }

        public int getRightLegRotation() {
            return this.RightLegRotation;
        }

        public int getWrapperBit() {
            return this.wrapperBit;
        }

        public int getHealth() {
            return this.healthField;
        }

        public int getFieldFromPose(BodyPart part) {
            switch (part) {
                case HEAD:
                    return getHeadRotation();
                case BODY:
                    return getBodyRotation();
                case LEFT_ARM:
                    return getLeftArmRotation();
                case RIGHT_ARM:
                    return getRightArmRotation();
                case LEFT_LEG:
                    return getLeftLegRotation();
                case RIGHT_LEG:
                    return getRightLegRotation();
            }
            return BodyRotation;
        }
    }

    public enum EventType {
        PLACE(null, null, null),
        BREAK(lang.getName("Break"), lang.getMaterial("Break"), 1),
        INTERACT(lang.getName("Interact"), lang.getMaterial("Interact"), 1),
        BREAK_INTERACT(lang.getName("Break_interact"), lang.getMaterial("Break_interact"), 1),
        NONE(lang.getName("None"), lang.getMaterial("None"), 1);

        String name;
        Material material;
        Integer amount;

        EventType(String name, Material material, Integer amount) {
            this.name = name;
            this.material = material;
            this.amount = amount;
        }

        public String getName() {
            return this.name;
        }

        public Material getMaterial() {
            return this.material;
        }

        public Integer getAmount() {
            return this.amount;
        }

        public ItemStack getItemStack() {
            ItemStack is = new ItemStack(material, amount);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(name);
            is.setItemMeta(im);
            return is;
        }
    }

    public enum ToolType {
        SWORD(swords),
        SPADE(spades),
        AXE(axt),
        PICKAXE(pickaxe),
        HOE(hoes),
        WEAPON(weapons),
        TOOLS(tools);

        List<Material> matList;

        ToolType(List<Material> matList) {
            this.matList = matList;
        }

        public List<Material> getMaterialList() {
            return this.matList;
        }
    }

    public enum PublicMode {
        PRIVATE(lang.getName("Private"), lang.getMaterial("Private"), 1, lang.getStringList("Private")),
        MEMBERS(lang.getName("Member"), lang.getMaterial("Member"), 1, lang.getStringList("Member")),
        PUBLIC(lang.getName("Public"), lang.getMaterial("Public"), 1, lang.getStringList("Public"));

        String name;
        Material material;
        Integer amount;
        List<String> stringl;

        PublicMode(String name, Material material, Integer amount, List<String> stringl) {
            this.name = name;
            this.material = material;
            this.amount = amount;
            this.stringl = stringl;
        }

        public String getName() {
            return this.name;
        }

        public Material getMaterial() {
            return this.material;
        }

        public Integer getAmount() {
            return this.amount;
        }

        public List<String> getStringList() {
            return this.stringl;
        }

        public ItemStack getItemStack() {
            ItemStack is = new ItemStack(material, amount);
            ItemMeta im = is.getItemMeta();
            im.setLore(stringl);
            im.setDisplayName(name);
            is.setItemMeta(im);
            return is;
        }
    }

    public enum EntityMoving {
        LEFT(1, 0, false),
        RIGHT(-1, 0, false),
        FORWARD(0, 1, false),
        BACKWARD(0, -1, false),
        JUMPING(0, 0, true),
        SNEAKING(0, 0, false),
        LEFT_FORWARD(1, 1, false),
        RIGHT_FORWARD(-1, 1, false),
        LEFT_BACKWARD(1, -1, false),
        RIGHT_BACKWARD(-1, -1, false);

        private float a = 0;
        private float b = 0;
        private boolean c = false;
        EntityMoving(float a, float b, boolean c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public void setValues(float a, float b, boolean c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public float getFieldA() {
            return this.a;
        }

        public float getFieldB() {
            return this.b;
        }

        public boolean getFieldC() {
            return this.c;
        }
    }
}
