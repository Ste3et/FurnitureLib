package de.Ste3et_C0st.FurnitureLib.ModelLoader.function;

import org.bukkit.entity.Player;

public abstract class FunctionObject {

    private String name;

    public FunctionObject(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public abstract boolean run(Player player);

    public abstract void parse(String string);

    public abstract String toString();
}
