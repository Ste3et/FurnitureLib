package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public abstract class iCommand {

	private String subCommand = "", languageID = "", helpClass = "";
	private String[] alias, tab;
	private List<String> aliasList = new ArrayList<String>();
	private boolean b = false, console = false, hideFromHelp = false;
	
	public iCommand(String subCommand, String ... alias) {
		this(subCommand, "", alias);
	}
	
	public iCommand(String subCommand,String helpClass, String ... alias) {
		this.setSubCommand(subCommand);
		this.setLanguageID(subCommand);
		this.helpClass = helpClass;
		if(alias.length > 0) {
			this.alias = alias;
			this.aliasList = Arrays.asList(alias);
		}
	}
	
	public String getHelpClass() {
		return this.helpClass;
	}
	
	public LanguageManager getLHandler() {
		return LanguageManager.getInstance();
	}
	
	public iCommand setTab(String ... str) {
		this.tab = str;
		return this;
	}
	
	public String[] getTabs() {
		return this.tab;
	}

	public boolean hasCommandPermission(CommandSender paramCommandSender, String paramString)
	{
	   if (!(paramCommandSender instanceof Player)) {
	     return true;
	   }
	   String perm = new StringBuilder(getPermissions()).append(paramString).toString().toLowerCase();
	   if (paramCommandSender.hasPermission("furniture.command.*")) {
	     return true;
	   }
	   boolean b = paramCommandSender.hasPermission(perm);
	   if(!b) FurnitureLib.debug("FurnitureLib: " + paramCommandSender.getName() + " is missing perm " + perm); 
	   return b;
	}
	
	public boolean hasCommandPermission(CommandSender paramCommandSender)
	{
	   return hasCommandPermission(paramCommandSender, "");
	}
	 
	public abstract void execute(CommandSender sender, String[] args);

	public String getSubCommand() {
		return subCommand;
	}
	
	public List<String> getAliasList(){
		return this.aliasList;
	}

	public String getFormatedPerms() {
		return "furniture.command." + getSubCommand();
	}

	private void setSubCommand(String subCommand) {
		this.subCommand = subCommand;
	}

	public String getLanguageID() {
		return languageID;
	}

	private void setLanguageID(String languageID) {
		this.languageID = languageID;
	}
	
	/*
	 * get all subcommands
	 */
	public String[] getAlias() {
		return this.alias;
	}

	/*
	 * is the player tab function enable
	 */
	public boolean isPlayerTab() {
		return this.b;
	}
	
	/*
	 * active the player tab complete function
	 */
	public void setPlayerTab(boolean b) {
		this.b = b;
	}
	
	/*
	 * can the sender execute the command
	 */
	public boolean canExecuteBySender(CommandSender sender) {
		if(sender instanceof Player == false) return console;
		return true;
	}
	
	/*
	 * if you set the boolean console to true the command is executable by Console
	 */
	public void executeByPlayer(boolean console) {
		this.console = console;
	}
	
	/*
	 * is the subcommand hidden from the plugin help list
	 */
	public boolean isHide() {
		return this.hideFromHelp;
	}
	
	/*
	 * hide the subcommand from the plugin help list
	 */
	public iCommand setHide(boolean b) {
		this.hideFromHelp = b;
		return this;
	}

	public String getPermissions() {
		return new StringBuilder("furniture.command.").append(getSubCommand()).toString().toLowerCase();
	}
}
