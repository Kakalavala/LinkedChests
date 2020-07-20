package me.kakalavala.linkedchests.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import me.kakalavala.linkedchests.assets.Locale;
import me.kakalavala.linkedchests.commands.Command_GiveChestLinker;
import me.kakalavala.linkedchests.listeners.Listeners;

public class Core extends JavaPlugin {
	
	public final PluginDescriptionFile pf = this.getDescription();
	public final Logger log = Logger.getLogger(pf.getName());
	
	public final Locale msgs = new Locale(this);
	
	public void onEnable() {
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		this.registerCommands();
		this.registerListeners();
		
		if (this.isCraftable())
			this.registerRecipes();
		else log.info(String.format("[%s] Recipes are disabled.", pf.getName()));
	}
	
	public void onDisable() {
		
	}

	private void registerCommands() {
		this.getCommand("givechestlinker").setExecutor(new Command_GiveChestLinker(this));
	}
	
	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
	}
	
	private void registerRecipes() {
		log.info(String.format("[%s] Registered \"chestlinker\" Recipe.", pf.getName()));
		
		final ShapedRecipe chestlinkerRecipe = new ShapedRecipe(new NamespacedKey(this, "chestlinker"), this.getChestLinker());
		
		chestlinkerRecipe.shape("C", "G", "G");
		chestlinkerRecipe.setIngredient('C', Material.CHEST);
		chestlinkerRecipe.setIngredient('G', Material.GOLD_INGOT);
		
		this.getServer().addRecipe(chestlinkerRecipe);
	}
	
	public void sendPluginMessage(final Object ply, final String msg) {
		if (msg.length() > 0) {
			try {
				if (ply instanceof Player)
					((Player) ply).sendMessage(String.format("%s%s", this.msgs.getMessage("prefix"), msg));
				else ((CommandSender) ply).sendMessage(String.format("%s%s", this.msgs.getMessage("prefix"), msg));
			} catch (ClassCastException exc) {
				log.warning("*************************");
				log.warning("Could not send message to user.");
				log.info(String.format("%s%s", this.msgs.getMessage("prefix"), msg));
				log.warning("*************************");
			} catch (Exception exc) {
				log.warning("*************************");
				log.warning("Could not send message.");
				log.warning("Object = " + ply);
				log.warning("MessageID = " + msg);
				log.warning("*************************");
			}
		}
	}
	
	private boolean isCraftable() {
		this.reloadConfig();
		return this.getConfig().getBoolean("chest-linker-craftable");
	}
	
	public boolean isValidId(final String id) {
		boolean letter = false;
		boolean number = false;
		
		try {
			for (int i = 0; i < id.length(); i += 1) {
				if (i % 2 != 0) {
					final int n = Integer.parseInt(((Character) id.charAt(i)).toString());
					number = (n > 0 && n < 10);
				} else {
					final String c = ((Character) id.charAt(i)).toString();
					boolean match = false;
	              
					for (final ColourLetter l : ColourLetter.values()) {
						match = (c.equals(l.letter));
	                
						if (match) break;
					}
					
					letter = match;
				}
			}
		} catch (Exception exc) {
			return false;
		}
		
		return (letter && number);
	}
	
	public ItemStack[] getTrueContents(final Inventory inv) {
		final List<ItemStack> items = new ArrayList<ItemStack>();
		
		for (int i = 0; i < inv.getSize(); i += 1) {
			if (inv.getItem(i) != null)
				items.add(inv.getItem(i));
		}
		
		final ItemStack[] _items = new ItemStack[items.size()];
		
		for (int i = 0; i < items.size(); i += 1)
			_items[i] = items.get(i);
		
		return _items;
	}
	
	public String itemsToChestId(final ItemStack[] items) {
		String chestId = "";
		
		for (final ItemStack it : items) {
			switch (it.getType()) {
				case WHITE_STAINED_GLASS_PANE:
					chestId += ColourLetter.WHITE.letter;
					break;
				case ORANGE_STAINED_GLASS_PANE:
					chestId += ColourLetter.ORANGE.letter;
					break;
				case MAGENTA_STAINED_GLASS_PANE:
					chestId += ColourLetter.MAGENTA.letter;
					break;
				case LIGHT_BLUE_STAINED_GLASS_PANE:
					chestId += ColourLetter.LIGHT_BLUE.letter;
					break;
				case YELLOW_STAINED_GLASS_PANE:
					chestId += ColourLetter.YELLOW.letter;
					break;
				case LIME_STAINED_GLASS_PANE:
					chestId += ColourLetter.LIGHT_GREEN.letter;
					break;
				case PINK_STAINED_GLASS_PANE:
					chestId += ColourLetter.PINK.letter;
					break;
				case GRAY_STAINED_GLASS_PANE:
					chestId += ColourLetter.DARK_GREY.letter;
					break;
				case LIGHT_GRAY_STAINED_GLASS_PANE:
					chestId += ColourLetter.GREY.letter;
					break;
				case CYAN_STAINED_GLASS_PANE:
					chestId += ColourLetter.CYAN.letter;
					break;
				case PURPLE_STAINED_GLASS_PANE:
					chestId += ColourLetter.PURPLE.letter;
					break;
				case BLUE_STAINED_GLASS_PANE:
					chestId += ColourLetter.BLUE.letter;
					break;
				case BROWN_STAINED_GLASS_PANE:
					chestId += ColourLetter.BROWN.letter;
					break;
				case GREEN_STAINED_GLASS_PANE:
					chestId += ColourLetter.GREEN.letter;
					break;
				case RED_STAINED_GLASS_PANE:
					chestId += ColourLetter.RED.letter;
					break;
				case BLACK_STAINED_GLASS_PANE:
					chestId += ColourLetter.BLACK.letter;
					break;
				default:
					break;
			}
			
			chestId += it.getAmount();
		}
		
		return chestId;
	}
	
	public ItemStack getPreviousColour(final ItemStack curColour) {
		ItemStack newColour = curColour;
		
		switch (curColour.getType()) {
			case WHITE_STAINED_GLASS_PANE:
				newColour.setType(Material.BLACK_STAINED_GLASS_PANE);
				return newColour;
			case ORANGE_STAINED_GLASS_PANE:
				newColour.setType(Material.WHITE_STAINED_GLASS_PANE);
				return newColour;
			case MAGENTA_STAINED_GLASS_PANE:
				newColour.setType(Material.ORANGE_STAINED_GLASS_PANE);
				return newColour;
			case LIGHT_BLUE_STAINED_GLASS_PANE:
				newColour.setType(Material.MAGENTA_STAINED_GLASS_PANE);
				return newColour;
			case YELLOW_STAINED_GLASS_PANE:
				newColour.setType(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
				return newColour;
			case LIME_STAINED_GLASS_PANE:
				newColour.setType(Material.YELLOW_STAINED_GLASS_PANE);
				return newColour;
			case PINK_STAINED_GLASS_PANE:
				newColour.setType(Material.LIME_STAINED_GLASS_PANE);
				return newColour;
			case GRAY_STAINED_GLASS_PANE:
				newColour.setType(Material.PINK_STAINED_GLASS_PANE);
				return newColour;
			case LIGHT_GRAY_STAINED_GLASS_PANE:
				newColour.setType(Material.GRAY_STAINED_GLASS_PANE);
				return newColour;
			case CYAN_STAINED_GLASS_PANE:
				newColour.setType(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
				return newColour;
			case PURPLE_STAINED_GLASS_PANE:
				newColour.setType(Material.CYAN_STAINED_GLASS_PANE);
				return newColour;
			case BLUE_STAINED_GLASS_PANE:
				newColour.setType(Material.PURPLE_STAINED_GLASS_PANE);
				return newColour;
			case BROWN_STAINED_GLASS_PANE:
				newColour.setType(Material.BLUE_STAINED_GLASS_PANE);
				return newColour;
			case GREEN_STAINED_GLASS_PANE:
				newColour.setType(Material.BROWN_STAINED_GLASS_PANE);
				return newColour;
			case RED_STAINED_GLASS_PANE:
				newColour.setType(Material.GREEN_STAINED_GLASS_PANE);
				return newColour;
			case BLACK_STAINED_GLASS_PANE:
				newColour.setType(Material.RED_STAINED_GLASS_PANE);
				return newColour;
			default:
				return newColour;
		}
	}
	
	public ItemStack getNextColour(final ItemStack curColour) {
		ItemStack newColour = curColour;
		
		switch (curColour.getType()) {
			case WHITE_STAINED_GLASS_PANE:
				newColour.setType(Material.ORANGE_STAINED_GLASS_PANE);
				return newColour;
			case ORANGE_STAINED_GLASS_PANE:
				newColour.setType(Material.MAGENTA_STAINED_GLASS_PANE);
				return newColour;
			case MAGENTA_STAINED_GLASS_PANE:
				newColour.setType(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
				return newColour;
			case LIGHT_BLUE_STAINED_GLASS_PANE:
				newColour.setType(Material.YELLOW_STAINED_GLASS_PANE);
				return newColour;
			case YELLOW_STAINED_GLASS_PANE:
				newColour.setType(Material.LIME_STAINED_GLASS_PANE);
				return newColour;
			case LIME_STAINED_GLASS_PANE:
				newColour.setType(Material.PINK_STAINED_GLASS_PANE);
				return newColour;
			case PINK_STAINED_GLASS_PANE:
				newColour.setType(Material.GRAY_STAINED_GLASS_PANE);
				return newColour;
			case GRAY_STAINED_GLASS_PANE:
				newColour.setType(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
				return newColour;
			case LIGHT_GRAY_STAINED_GLASS_PANE:
				newColour.setType(Material.CYAN_STAINED_GLASS_PANE);
				return newColour;
			case CYAN_STAINED_GLASS_PANE:
				newColour.setType(Material.PURPLE_STAINED_GLASS_PANE);
				return newColour;
			case PURPLE_STAINED_GLASS_PANE:
				newColour.setType(Material.BLUE_STAINED_GLASS_PANE);
				return newColour;
			case BLUE_STAINED_GLASS_PANE:
				newColour.setType(Material.BROWN_STAINED_GLASS_PANE);
				return newColour;
			case BROWN_STAINED_GLASS_PANE:
				newColour.setType(Material.GREEN_STAINED_GLASS_PANE);
				return newColour;
			case GREEN_STAINED_GLASS_PANE:
				newColour.setType(Material.RED_STAINED_GLASS_PANE);
				return newColour;
			case RED_STAINED_GLASS_PANE:
				newColour.setType(Material.BLACK_STAINED_GLASS_PANE);
				return newColour;
			case BLACK_STAINED_GLASS_PANE:
				newColour.setType(Material.WHITE_STAINED_GLASS_PANE);
				return newColour;
			default:
				return newColour;
		}
	}
	
	public ItemStack getChestLinker() {
		final ItemStack item = new ItemStack(Material.BLAZE_ROD, 1); {
			final ItemMeta m = item.getItemMeta();
			
			m.setDisplayName("§c§lChest§9§lLinker");
			m.setLore(Arrays.asList("§7Right-Click a chest to link it."));
			
			item.setItemMeta(m);
		};
		
		return item;
	}
	
	public String chestIdToColours(final String chestId) {
		if (!this.isValidId(chestId))
			return null;
		
		final String letters = chestId.replaceAll("[^A-Za-z]", "");
		final String numbers = chestId.replaceAll("[A-Za-z]", "");
		
		String c = "";
		
		for (int i = 0; i < letters.length(); i += 1) {
			final String l = ((Character) letters.charAt(i)).toString();
			
			c += String.format("%s%s%s", this.idToColour(l), l, numbers.charAt(i));
		}
			
		return c;
	}
	
	public Inventory getMenuInventory() {
		final Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§c§lLinking §9§lChest");
		
		ItemStack it = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1); {
			final ItemMeta m = it.getItemMeta();
			
			m.setDisplayName(this.msgs.getMessage("linking_chest_displayname"));
			m.setLore(Arrays.asList(this.msgs.getMessage("linking_chest_lore")));
			
			it.setItemMeta(m);
		};
		
		for (int i = 0; i < inv.getSize(); i += 1) {
			it = this.getNextColour(it);
			inv.addItem(it);
		}
		
		return inv;
	}
	
	public UUID getViewerByChestID(final Map<UUID, String> opened, final String chestId) {
		UUID _u = null;
		
		if (opened.containsValue(chestId)) {
			for (final UUID u : opened.keySet()) {
				if (opened.get(u).equals(chestId)) {
					_u = u;
					break;
				}
			}
		}
		
		return _u;
	}
	
	private String idToColour(final String lt) {
		String c = "§r";
		
		for (final ColourLetter l : ColourLetter.values()) {
			if (lt.equals(l.letter)) {
				c = l.colourCode;
				break;
			}
		}
		
		return c;
	}
	
	public enum ColourLetter {
		WHITE("W", "§f"),//0 White
		ORANGE("O", "§6"),//1 Orange
		MAGENTA("M", "§d"),//2 Magenta
		LIGHT_BLUE("A", "§b"),//3 Aqua
		YELLOW("Y", "§e"),//4 Yellow
		LIGHT_GREEN("L", "§a"), //5 Lime
		PINK("P", "§d"),//6 Pink
		DARK_GREY("D", "§8"),//7 Dark grey
		GREY("S", "§7"),//8 Silver
		CYAN("C", "§3"),//9 Cyan
		PURPLE("U", "§5"), //10 pUrple
		BLUE("B", "§9"), //11 Blue
		BROWN("N", "§6"), //12 browN
		GREEN("E", "§2"), //13 dark grEen
		RED("R", "§c"), //14 Red
		BLACK("K", "§0"), //15 blacK
		;
		
		String letter;
		String colourCode;
		
		ColourLetter(final String letter, final String colourCode) {
			this.letter = letter;
			this.colourCode = colourCode;
		}
	}

}
