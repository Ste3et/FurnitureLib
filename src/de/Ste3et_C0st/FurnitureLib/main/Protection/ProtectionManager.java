package de.Ste3et_C0st.FurnitureLib.main.Protection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import de.Ste3et_C0st.FurnitureLib.main.FurnitureLib;

public class ProtectionManager {

	Plugin plugin;
	PluginManager manager;
	IWorldGuard worldGuard;
	IPlotME plotMe;
	IPlotz plotz;
	IPlotSquare ploSquare;
	ILandLord landlord;
	IGriefPrevention griefPrevention;
	IPreciousStones preciousStones;
	IResidence residence;
	ITowny towny;
	
	boolean WorldGuard,PlotME,Plotz,PlotSquare,LandLord,GriefPrevention,PreciousStones,Residence,Towny;
	
	public ProtectionManager(Plugin plugin){
		this.plugin = plugin;
		this.manager = Bukkit.getPluginManager();
		this.WorldGuard = isEnable("WorldGuard");
		this.PlotME = isEnable("PlotME");
		this.Plotz = isEnable("Plotz");
		this.PlotSquare = isEnable("PlotSquare");
		this.LandLord = isEnable("LandLord");
		this.GriefPrevention = isEnable("GriefPrevention");
		this.PreciousStones = isEnable("PreciousStones");
		this.Residence = isEnable("Residence");
		this.Towny = isEnable("Towny");
		
		if(this.WorldGuard) this.worldGuard = new IWorldGuard(manager);
		if(this.PlotME) this.plotMe = new IPlotME(manager);
		if(this.Plotz) this.plotz = new IPlotz(manager);
		if(this.PlotSquare) this.ploSquare = new IPlotSquare(manager);
		if(this.LandLord) this.landlord = new ILandLord(manager);
		if(this.GriefPrevention) this.griefPrevention = new IGriefPrevention(manager);
		if(this.PreciousStones) this.preciousStones = new IPreciousStones(manager);
		if(this.Towny) this.towny = new ITowny(manager);
		if(this.Residence) this.residence = new IResidence(manager);
	}
	
	private boolean isEnable(String plugin){
		if(manager.isPluginEnabled(plugin)){
			FurnitureLib.getInstance().getLogger().fine("FurnitureLibary hook into " + plugin);
			return true;
		}
		return false;
	}
	
	public boolean canBuild(Player p, Location loc, Material m){
		if(loc.getBlock()!=null&&BlackList.materialBlackList.contains(loc.getBlock().getType())){
			if(m==null){
				return false;
			}
		}
		if(p.isOp()) return true;
		if(p.hasPermission("furniture.bypass.protection")) return true;
		if(worldGuard!=null) return worldGuard.canBuild(p, loc);
		if(plotMe!=null) return plotMe.canBuild(p, loc);
		if(plotz!=null) return plotz.canBuild(p, loc);
		if(ploSquare!=null) return ploSquare.canBuild(p, loc);
		if(griefPrevention!=null) return griefPrevention.canBuild(p, loc);
		if(preciousStones!=null) return preciousStones.canBuild(p, loc);
		if(towny!=null) return towny.canBuild(p, loc);
		if(residence!=null) return residence.canBuild(p, loc);
		
		return true;
	}
}
