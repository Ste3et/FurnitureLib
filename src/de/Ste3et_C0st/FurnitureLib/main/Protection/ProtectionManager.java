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
	IFactions factions;
	IDiceChunk diceChunk;
	FurnitureLib lib;
	
	boolean WorldGuard,PlotME,Plotz,PlotSquare,LandLord,GriefPrevention,PreciousStones,Residence,Towny,DiceChunk, Factions;
	
	public ProtectionManager(Plugin plugin){
		this.lib = FurnitureLib.getInstance();
		this.plugin = plugin;
		this.manager = Bukkit.getPluginManager();
		this.WorldGuard = isEnable("WorldGuard");
		this.PlotME = isEnable("PlotME");
		this.Plotz = isEnable("Plotz");
		this.PlotSquare = isEnable("PlotSquared");
		this.LandLord = isEnable("LandLord");
		this.GriefPrevention = isEnable("GriefPrevention");
		this.PreciousStones = isEnable("PreciousStones");
		this.Residence = isEnable("Residence");
		this.Towny = isEnable("Towny");
		this.DiceChunk = isEnable("DiceChunk");
		this.Factions = isEnable("factions");
		
		if(this.WorldGuard) this.worldGuard = new IWorldGuard(manager);
		if(this.PlotME) this.plotMe = new IPlotME(manager);
		if(this.Plotz) this.plotz = new IPlotz(manager);
		if(this.PlotSquare) this.ploSquare = new IPlotSquare(manager);
		if(this.LandLord) this.landlord = new ILandLord(manager);
		if(this.GriefPrevention) this.griefPrevention = new IGriefPrevention(manager);
		if(this.PreciousStones) this.preciousStones = new IPreciousStones(manager);
		if(this.Towny) this.towny = new ITowny(manager);
		if(this.Residence) this.residence = new IResidence(manager);
		if(this.DiceChunk) this.diceChunk = new IDiceChunk(manager);
		if(this.Factions) this.factions = new IFactions(manager);
	}
	
	private boolean isEnable(String plugin){
		if(manager.isPluginEnabled(plugin)){
			FurnitureLib.getInstance().getLogger().fine("FurnitureLibary hook into " + plugin);
			return true;
		}
		return false;
	}
	private boolean isSolid(Material m, int subID){if(!checkPlaceable(m, subID)) return false;return m.isSolid();}
	
	private boolean checkPlaceable(Material m, int subID){
	    switch (m) {
			case WOOD_STAIRS: 
			if(subID>=4) return true;
			return false;
			case COBBLESTONE_STAIRS:
			if(subID>=4) return true;
			return false;
			case BRICK_STAIRS:
			if(subID>=4) return true;
			return false;
			case SMOOTH_STAIRS:
			if(subID>=4) return true;
			return false;
			case QUARTZ_STAIRS:
			if(subID>=4) return true;
			return false;
			case NETHER_BRICK_STAIRS:
			if(subID>=4) return true;
			return false;
			case SANDSTONE_STAIRS:
			if(subID>=4) return true;
			return false;
			case SPRUCE_WOOD_STAIRS:
			if(subID>=4) return true;
			return false;
			case BIRCH_WOOD_STAIRS:
			if(subID>=4) return true;
			return false;
			case JUNGLE_WOOD_STAIRS:
			if(subID>=4) return true;
			return false;
			case ACACIA_STAIRS:
			if(subID>=4) return true;
			return false;
			case DARK_OAK_STAIRS:
			if(subID>=4) return true;
			return false;
			case RED_SANDSTONE_STAIRS:
			if(subID>=4) return true;
			return false;
			case WOOD_STEP:
			if(subID>=8) return true;
			return false;
			case STEP:
			if(subID>=8) return true; 
			return false;
			case STONE_SLAB2:
			if(subID>=8) return true; 
			return false;
			case SOIL: return false;
			case ICE: return false;
			case GLOWSTONE: return false;
			case TNT: return false;
			case PISTON_BASE: return false;
			case SNOW: 
			if(subID==7) return true;
			return false;
			default: return true;
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public boolean canBuild(Player p, Location loc){
		if(loc.getBlock()!=null&&!isSolid(loc.getBlock().getType(), loc.getBlock().getData())) return false;
		if(p.isOp()) return true;
		if(p.hasPermission("furniture.bypass.protection") || p.hasPermission("furniture.admin")) return true;
		
		boolean wg = true, pm = true, pz = true, ps = true, gp = true, pst = true, to = true, re = true;
		boolean land = true, diceC = true, fact = true;
		
		if(WorldGuard) wg= worldGuard.canBuild(p, loc);
		if(PlotME) pm= plotMe.canBuild(p, loc);
		if(Plotz) pz= plotz.canBuild(p, loc);
		if(PlotSquare) ps= ploSquare.canBuild(p, loc);
		if(GriefPrevention) gp= griefPrevention.canBuild(p, loc);
		if(PreciousStones) pst= preciousStones.canBuild(p, loc);
		if(Towny) to= towny.canBuild(p, loc);
		if(Residence) re= residence.canBuild(p, loc);
		if(LandLord) land = landlord.check(p, loc);
		if(DiceChunk) diceC = diceChunk.check(p, loc);
		if(Factions) fact = factions.canBuild(p, loc);
		
		if(wg&&pm&&ps&&gp&&pst&&to&&re&&pz&&land&&diceC&&fact){
			return true;
		}else{
			return false;
		}
	}
}
