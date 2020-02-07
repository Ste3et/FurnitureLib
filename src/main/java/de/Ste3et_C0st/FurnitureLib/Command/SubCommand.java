package de.Ste3et_C0st.FurnitureLib.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Constructor;

public class SubCommand {

    private String HoverText = "";
    private String subcommand = "";
    private String Suggest_Command = "";
    private String command = "";
    private Class<?> cl;

    public SubCommand(String subcommand, Class<?> cl, String HoverText, String Suggest_Command, String command) {
        this.setHoverText(HoverText);
        this.setSubcommand(subcommand);
        this.setSuggest_Command(Suggest_Command);
        this.setCommand(command);
        this.setCl(cl);
    }

    public String getHoverText() {
        return HoverText;
    }

    public void setHoverText(String hoverText) {
        HoverText = hoverText;
    }

    public String getSubcommand() {
        return subcommand;
    }

    public void setSubcommand(String subcommand) {
        this.subcommand = subcommand;
    }

    public String getSuggest_Command() {
        return Suggest_Command;
    }

    public void setSuggest_Command(String suggest_Command) {
        Suggest_Command = suggest_Command;
    }

    public Class<?> getCl() {
        return cl;
    }

    public void setCl(Class<?> cl) {
        this.cl = cl;
    }

    public void runCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
        Constructor<?> ctor = getCl().getConstructors()[0];
        try {
            ctor.newInstance(sender, cmd, arg2, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

}
