package de.Ste3et_C0st.FurnitureLib.ShematicLoader.Events;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
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
	
	@SuppressWarnings("deprecation")
	public void runFunction(Player p){
		if(this.inv!=null){
			if(this.inv.getPlayer()==null){
				this.inv.openInventory(p);
				return;	
			}
		}
		
		boolean j = false;
		for(fEntity stand : getfAsList()){
			if(!stand.getName().isEmpty()){
				if(p.getInventory().getItemInMainHand() != null){
					if(p.getInventory().getItemInMainHand().getAmount() > 0){
						String command = stand.getName();
						command = command.toUpperCase();
						if(command.startsWith("#DYE")){
							String[] selector = command.split(":");
							String arg1 = selector[0].replace("#DYE_", "");
							String arg2 = selector[1].replace("CONSUME_", "");
							arg2 = arg2.replace("#", "");
							
							Material a = Material.AIR;
							
							if(containsNumber(arg1)){
								try{
									//a = Material.getMaterial(Integer.parseInt(arg1));
								}catch(Exception ex){
									return;
								}
							}else{
								a = Material.valueOf(arg1);
							}
							
							boolean consume = false;
							if(arg2.equalsIgnoreCase("DYE")){
								Material m = p.getInventory().getItemInMainHand().getType();
								if(m.equals(Material.INK_SAC)){
									DyeColor color = DyeColor.getByDyeData((byte) p.getInventory().getItemInMainHand().getDurability());
									for(fEntity stand2 : getfAsList()){
										if(stand2.getHelmet() == null) continue;
										if(stand2.getHelmet().getType().equals(a)){
											ItemStack dyedStack = stand2.getHelmet();
											if(dyedStack.getType().name().contains("WOOL") || dyedStack.getType().name().contains("CARPET") || dyedStack.getType().name().contains("STAINED_GLASS") || dyedStack.getType().name().contains("TERRACOTTA") || dyedStack.getType().name().contains("STAINED_GLASS_PANE")){
												if(dyedStack.getDurability()!=color.getWoolData()){
													dyedStack.setDurability(color.getWoolData());
													consume = true;
												}
											}else if(dyedStack.getType().name().contains("BANNER")){
												BannerMeta meta = (BannerMeta) dyedStack.getItemMeta();
												if(meta == null) continue;
												if(meta.getBaseColor() == null || !meta.getBaseColor().equals(color)){
													meta.setBaseColor(color);
													dyedStack.setItemMeta(meta);
													consume = true;
												}
											}
											if(consume){
												stand2.setHelmet(dyedStack);
												stand2.update();
											}
										}
									}
								}
							}else{
								Material b  = Material.AIR;
								if(containsNumber(arg2)){
									try{
										//b = Material.getMaterial(Integer.parseInt(arg2));
									}catch(Exception ex){
										return;
									}
								}else{
									b = Material.valueOf(arg2);
								}
								if(b.equals(p.getInventory().getItemInMainHand().getType())){
									for(fEntity entity2 : getfAsList()){
										if(entity2.getHelmet() != null){
											if(entity2.getHelmet().getType().equals(a)){
												ItemStack newIS = p.getInventory().getItemInMainHand().clone();
												entity2.setHelmet(newIS);
												entity2.update();
												consume = true;
											}
										}
									}
								}
							}
							
							if(selector[1].contains("CONSUME_") && consume){
								consumeItem(p);
							}
							
							if(consume){
								j = true;
							}
						}
					}
				}
			}
			
			if(stand.getName().equalsIgnoreCase("#ITEM#")){
				if(p.getInventory().getItemInMainHand()!=null){
					Material m = p.getInventory().getItemInMainHand().getType();
					if(m.equals(Material.AIR) || !m.isBlock()){
						if(stand.getInventory().getItemInMainHand()!=null&&!stand.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
							ItemStack is = stand.getInventory().getItemInMainHand();
							is.setAmount(1);
							getWorld().dropItem(getLocation(), is);
							j = true;
						}
						if(p.getInventory().getItemInMainHand()!=null){
							j = true;
							ItemStack is = p.getInventory().getItemInMainHand().clone();
							if(is.getAmount()<=0){
								is.setAmount(0);
							}else{
								is.setAmount(1);
							}
							stand.setItemInMainHand(is);
							stand.update();
							consumeItem(p);	
						}
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
							j = true;
						}
						j = true;
						ItemStack is = p.getInventory().getItemInMainHand().clone();
						if(is.getAmount()<=0){
							is.setAmount(0);
						}else{
							is.setAmount(1);
						}
						stand.setHelmet(is);
						stand.update();
						consumeItem(p);	
					}
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
					j = true;
				}
			}
		}
		
		if(!j){
			for(fEntity stand : getfAsList()){
				if(stand.getName().startsWith("#Mount:") || stand.getName().startsWith("#SITZ")){
					if(stand.getPassanger()==null){
						stand.setPassanger(p);
						return;
					}else{
						if(stand.getPassanger().getUniqueId().equals(p.getUniqueId())){
							stand.eject();
						}
					}
				}else if(stand.getName().startsWith("#T_VISIBLE#")){
					if(stand.isInvisible()){
						stand.setInvisible(false);
					}else{
						stand.setInvisible(true);
					}
				}else if(stand.getName().startsWith("#T_HEAD_")){
					String string = stand.getName();
					string = string.replace("#", "");
					string = string.replace("T_HEAD_", "");
					short durability = 0;
					Material m = null;
					if(string.contains(":")){
//						String[] split = string.split(":");
//						m = Material.getMaterial(Integer.parseInt(split[0]));
//						durability = Short.parseShort(split[1]);
					}else{
						m = Material.getMaterial(string);
					}
					if(m == null){continue;}
					if(stand.getHelmet() == null){
						stand.setHelmet(new ItemStack(m, 1, durability));
					}else if(stand.getHelmet().getType().equals(m) && stand.getHelmet().getDurability() == durability){{
						stand.setHelmet(null);
					}
					stand.update();
				}
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
		
		getfAsList().stream().filter(entity -> entity.getName().equalsIgnoreCase("#ITEM#") || entity.getName().equalsIgnoreCase("#BLOCK#")).
			forEach(entity -> {
				for(ItemStack stack : entity.getInventory().getIS()) {
					if(stack != null){
						getWorld().dropItemNaturally(getLocation().clone().add(0, .5, 0), stack);
					}
				}
			});
	}
	
	private boolean containsNumber(String str) {                
	    if(str == null || str.isEmpty()) return false;
	    for(char c : str.toCharArray()){
	        if(Character.isDigit(c)) return true; 
	    }
	    return false;
	}
}
