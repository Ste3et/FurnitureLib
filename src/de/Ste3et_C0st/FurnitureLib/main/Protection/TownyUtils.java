package de.Ste3et_C0st.FurnitureLib.main.Protection;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownBlockOwner;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownyUtils
{
  public static boolean isResident(Player player, Location location)
  {
    try
    {
      return TownyUniverse.getTownBlock(location).getTown().hasResident(player.getName());
    }
    catch (NotRegisteredException ex) {}
    return false;
  }
  
  public static boolean isResident(Player player, Location... locations)
  {
    for (Location location : locations) {
      if (!isResident(player, location)) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isPlotOwner(Player player, Location location)
  {
    try
    {
      TownBlockOwner owner = TownyUniverse.getDataSource().getResident(player.getName());
      return TownyUniverse.getTownBlock(location).isOwner(owner);
    }
    catch (NotRegisteredException ex) {}
    return false;
  }
  
  public static boolean isPlotOwner(Player player, Location... locations)
  {
    for (Location location : locations) {
      if (!isPlotOwner(player, location)) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isInWilderness(Location location)
  {
    return TownyUniverse.isWilderness(location.getBlock());
  }
  
  public static boolean isInWilderness(Location... locations)
  {
    for (Location location : locations) {
      if ((location != null) && (!isInWilderness(location))) {
        return false;
      }
    }
    return true;
  }
}