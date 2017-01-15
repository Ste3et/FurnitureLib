package de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.Ste3et_C0st.FurnitureLib.ShematicLoader.ProjektInventory;
import de.Ste3et_C0st.FurnitureLib.main.FurnitureHelper;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.entity.fEntity;

public class FurnitureFunctions extends FurnitureHelper {

	private ProjektInventory inv;
	
	public FurnitureFunctions(ObjectID id, ProjektInventory inv) {
		super(id);
		this.inv = inv;
	}

	public ProjektInventory getInv(){return this.inv;}
	
	public void runFunction(Player p){
		if(this.inv!=null){
			if(this.inv.getPlayer()==null){
				this.inv.openInventory(p);
				return;	
			}
		}
		
		for(fEntity stand : getfAsList()){
			if(stand.getName().equalsIgnoreCase("#ITEM#")){
				if(p.getInventory().getItemInMainHand()!=null){
					Material m = p.getInventory().getItemInMainHand().getType();
					if(m.equals(Material.AIR) || !m.isBlock()){
						if(stand.getInventory().getItemInMainHand()!=null&&!stand.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
							ItemStack is = stand.getInventory().getItemInMainHand();
							is.setAmount(1);
							getWorld().dropItem(getLocation(), is);
						}
						ItemStack is = p.getInventory().getItemInMainHand().clone();
						is.setAmount(1);
						stand.setItemInMainHand(is);
						stand.update();
						consumeItem(p);	
					}
				}
			}else if(stand.getName().equalsIgnoreCase("#BLOCK#")){
				if(p.getInventory().getItemInMainHand()!=null){
					Material m = p.getInventory().getItemInMainHand().getType();
					if(m.equals(Material.AIR) || m.isBlock()){
						if(stand.getInventory().getHelmet()!=null&&!stand.getInventory().getHelmet().getType().equals(Material.AIR)){
							ItemStack is = stand.getInventory().getHelmet();
							is.setAmount(1);
							getWorld().dropItem(getLocation(), is);
						}
						ItemStack is = p.getInventory().getItemInMainHand().clone();
						is.setAmount(1);
						stand.setHelmet(is);
						stand.update();
						consumeItem(p);	
					}
				}
			}else if(stand.getName().startsWith("#Mount:") || stand.getName().startsWith("#SITZ#")){
				if(stand.getPassanger()==null){
					stand.setPassanger(p);
					return;
				}
			}else if(stand.getName().startsWith("/")){
				if(!stand.getName().startsWith("/op")){
					String str = stand.getName();
					str = str.replaceAll("@player", p.getName());
					str = str.replaceAll("@uuid", p.getUniqueId().toString());
					str = str.replaceAll("@world", p.getWorld().getName());
					str = str.replaceAll("@x", p.getLocation().getX() + "");
					str = str.replaceAll("@y", p.getLocation().getY() + "");
					str = str.replaceAll("@z", p.getLocation().getZ() + "");
					p.chat(str);
				}
			}
		}
	}
	
	public void toggleLight(boolean change){
		for(fEntity stand : getfAsList()){
			if(stand.getName().startsWith("#Light:")){
				String[] str = stand.getName().split(":");
				String lightBool = str[2];
				if(change){
					if(lightBool.equalsIgnoreCase("off#")){
						stand.setName(stand.getName().replace("off#", "on#"));
						if(!stand.isFire()){stand.setFire(true);}
					}else if(lightBool.equalsIgnoreCase("on#")){
						stand.setName(stand.getName().replace("on#", "off#"));
						if(stand.isFire()){stand.setFire(false);}
					}
				}else{
					if(lightBool.equalsIgnoreCase("on#")){if(!stand.isFire()){stand.setFire(true);}}
				}
			}
		}
		update();
	}
	
	public void checkItems(){
		if(inv != null){
			for(ItemStack stack : inv.getInv().getContents()){
				if(stack != null){
					getWorld().dropItemNaturally(getLocation().clone().add(0, .5, 0), stack);
				}
			}
		}
		for(fEntity entity : getfAsList()){
			if(entity.getName().equalsIgnoreCase("#ITEM#") || entity.getName().equalsIgnoreCase("#BLOCK#")){
				for(ItemStack stack : entity.getInventory().getIS()){
					if(stack != null){
						getWorld().dropItemNaturally(getLocation().clone().add(0, .5, 0), stack);
					}
				}
			}
		}
	}
}
