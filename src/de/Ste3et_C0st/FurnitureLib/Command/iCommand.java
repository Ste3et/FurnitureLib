package de.Ste3et_C0st.FurnitureLib.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.Ste3et_C0st.FurnitureLib.Utilitis.LanguageManager;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public abstract class iCommand {

	private String subCommand = "", permissions = "", languageID = "", helpClass = "", pluginName = "furniture";
	private String[] alias;
	private List<String> aliasList = new ArrayList<String>();
	private boolean b = false, console = false, hideFromHelp = false;
	
	public iCommand(String subCommand, String permissions, String ... alias) {
		this.setSubCommand(subCommand);
		this.setPermissions(permissions);
		this.setLanguageID(subCommand);
		if(alias.length > 0) {
			this.alias = alias;
			this.aliasList = Arrays.asList(alias);
		}
	}
	
	public iCommand(String subCommand, String permissions,String helpClass, String ... alias) {
		this.setSubCommand(subCommand);
		this.setPermissions(permissions);
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

	public boolean hasCommandPermission(CommandSender paramCommandSender, String paramString)
	{
	   if (!(paramCommandSender instanceof Player)) {
	     return true;
	   }
	   System.out.println("command." + getLanguageID() + ".permissions");
	   System.out.println(getLHandler().getString("command." + getLanguageID() + ".permissions") + paramString);
	   if (paramCommandSender.hasPermission(this.pluginName + ".command.*")) {
	     return true;
	   }
	   boolean b = paramCommandSender.hasPermission(getLHandler().getString(getLanguageID() + ".permissions") + paramString);
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

	public String getPermissions() {
		return permissions;
	}

	private void setPermissions(String permissions) {
		this.permissions = permissions;
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
}
