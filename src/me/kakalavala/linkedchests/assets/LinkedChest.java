package me.kakalavala.linkedchests.assets;

import java.io.File;
import java.util.List;

import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kakalavala.linkedchests.core.Core;

public class LinkedChest {
	
	private Core core;
	private String chestId;
	
	private File invCfg;
	private YamlConfiguration invYml;
	private File idCfg;
	private YamlConfiguration idYml;
	
	public LinkedChest(final Core core, final String chestId, final Player ply) {
		this.core = core;
		this.chestId = chestId;
		
		if (core.isValidId(chestId)) {
			this.invCfg = new File(core.getDataFolder() + "/linkedchests/" + chestId + ".yml");
			this.invYml = YamlConfiguration.loadConfiguration(this.invCfg);
			
			this.idCfg = new File(core.getDataFolder() + "/id_storage.yml");
			this.idYml = YamlConfiguration.loadConfiguration(this.idCfg);
		} else {
			core.sendPluginMessage(ply, core.msgs.getMessage("failed_setup"));
			return;
		}
	}
	
	private void saveId() {
		try {
			this.idYml.save(this.idCfg);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	private void saveInv() {
		try {
			this.invYml.save(this.invCfg);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public boolean removeChest() {
		if (this.doesChestIDAlreadyExist()) {
			final List<String> list = this.idYml.getStringList("CHEST_IDS_DONT_EDIT");
			
			list.remove(this.chestId);
			
			this.idYml.set("CHEST_IDS_DONT_EDIT", list);
			this.saveId();
			this.invCfg.delete();
			return true;
		} else return false;
	}
	
	public void setSecond(final boolean setTo) {
		this.invYml.set("hasSecond", setTo);
		this.saveInv();
	}
	
	public boolean hasFirst() {
		return (this.invYml.get("hasFirst") != null && this.invYml.getBoolean("hasFirst"));
	}
	
	public boolean hasSecond() {
		return (this.invYml.get("hasSecond") != null && this.invYml.getBoolean("hasSecond"));
	}
	
	public void firstTimeLink(final Chest chest) {
		if (!this.doesChestIDAlreadyExist()) {
			final List<String> list = this.idYml.getStringList("CHEST_IDS_DONT_EDIT");
			
			list.add(chestId);
			
			this.idYml.set("CHEST_IDS_DONT_EDIT", list);
			this.saveId();
		}
		
		this.invYml.set("hasFirst", true);
		this.invYml.set("hasSecond", false);
		
		this.saveInv();
		
		chest.setCustomName("§c§lLinked§9§lChest §8§l[" + core.chestIdToColours(this.chestId) + "§8§l]");
		chest.update();
	}
	
	public void secondTimeLink(final Chest chest) {
		this.invYml.set("hasFirst", true);
		this.invYml.set("hasSecond", true);
		
		this.saveInv();
		
		chest.setCustomName("§c§lLinked§9§lChest §8§l[" + core.chestIdToColours(this.chestId) + "§8§l]");
		chest.update();
	}
	
	public boolean doesChestIDAlreadyExist() {
		return this.idYml.getStringList("CHEST_IDS_DONT_EDIT").contains(this.chestId);
	}
	
	public void storeInventory(final Inventory inv) {
		for (int i = 0; i < inv.getSize(); i += 1)
			this.invYml.set("" + i, (inv.getItem(i) != null) ? inv.getItem(i) : null);
		
		this.saveInv();
	}
	
	public ItemStack[] getStoredInventory() {
		final ItemStack[] items = new ItemStack[27];
		
		for (int i = 0; i < 27; i += 1)
			items[i] = (this.invYml.getItemStack("" + i) != null) ? this.invYml.getItemStack("" + i) : null;
			
		return items;
	}

}
