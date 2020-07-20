package me.kakalavala.linkedchests.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.kakalavala.linkedchests.assets.LinkedChest;
import me.kakalavala.linkedchests.core.Core;
import net.md_5.bungee.api.ChatColor;

public class Listeners implements Listener {
	
	private Core core;
	
	private final Map<UUID, String> opened = new HashMap<UUID, String>();
	private final Map<UUID, Chest> linking = new HashMap<UUID, Chest>();
	
	public Listeners(final Core core) {
		this.core = core;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void readyLinkChest(final PlayerInteractEvent e) {
		try {
			final Player ply = e.getPlayer();
			final Block bk = e.getClickedBlock();
			final ItemStack item = e.getItem();
			
			if (e.isCancelled())
				return;
			
			if (bk.getType() == Material.CHEST) {
				final Chest chest = (Chest) bk.getState();
				final Inventory inv = chest.getBlockInventory();
				
				if (this.linking.containsValue(chest)) {
					core.sendPluginMessage(ply, core.msgs.getMessage("already_being_linked"));
					e.setCancelled(true);
					return;
				}
				
				if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
					if (item.getItemMeta().getDisplayName().equals(core.getChestLinker().getItemMeta().getDisplayName())) {
						if (!ply.hasPermission("linkedchests.create")) {
							core.sendPluginMessage(ply, core.msgs.getMessage("lack_permission_link_chests"));
							e.setCancelled(true);
							return;
						}
						
						if (inv.getTitle().startsWith("§c§lLinked§9§lChest §8§l[")) {
							core.sendPluginMessage(ply, core.msgs.getMessage("already_linked"));
							e.setCancelled(true);
							return;
						}
						
						if (this.linking.containsValue(chest)) {
							core.sendPluginMessage(ply, core.msgs.getMessage("already_being_linked"));
							e.setCancelled(true);
							return;
						}
						
						if (chest.getInventory().getSize() != 27) {
							core.sendPluginMessage(ply, core.msgs.getMessage("cannot_be_double_chest"));
							e.setCancelled(true);
							return;
						}
						
						if (inv.getViewers().size() > 0) {
							core.sendPluginMessage(ply, core.msgs.getMessage("must_be_closed"));
							e.setCancelled(true);
							return;
						}
						
						if (core.getTrueContents(inv).length > 0) {
							core.sendPluginMessage(ply, core.msgs.getMessage("must_be_empty"));
							e.setCancelled(true);
							return;
						}
						
						this.linking.put(ply.getUniqueId(), chest);
						
						ply.openInventory(core.getMenuInventory());
						
						e.setCancelled(true);
						return;
					} else return;
				} else return;
			} else return;
		} catch (Exception exc) {}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChestOpen(final InventoryOpenEvent e) {
		try {
			final Inventory inv = e.getInventory();
			final Player ply = (Player) e.getPlayer();
			final String strippedTitle = ChatColor.stripColor(inv.getTitle());
			
			if (e.isCancelled())
				return;
			
			if (inv.getType() == InventoryType.CHEST) {
				if (inv.getTitle().startsWith("§c§lLinked§9§lChest §8§l[")) {
					final String chestId = strippedTitle.substring(13, strippedTitle.length() - 1);
					
					if (!core.isValidId(chestId)) {
						core.sendPluginMessage(ply, core.msgs.getMessage("invalid_chest_id"));
						return;
					}
					
					if (!this.opened.containsValue(chestId)) {
						final LinkedChest lc = new LinkedChest(core, chestId, ply);
						
						if (!lc.doesChestIDAlreadyExist()) {
							core.sendPluginMessage(ply, String.format(core.msgs.getMessage("attached_id_doesnt_exist"), core.chestIdToColours(chestId)));
							e.setCancelled(true);
							return;
						}
						
						for (int i = 0; i < inv.getSize(); i += 1)
							inv.setItem(i, lc.getStoredInventory()[i]);
						
						this.opened.put(ply.getUniqueId(), chestId);
					} else {
						core.sendPluginMessage(ply, String.format(core.msgs.getMessage("being_viewed"), Bukkit.getPlayer(core.getViewerByChestID(opened, chestId)).getName()));
						e.setCancelled(true);
						return;
					}
				} else return;
			} else  return;
		} catch (Exception exc) {}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(final InventoryCloseEvent e) {
		try {
			final Inventory inv = e.getInventory();
			final Player ply = (Player) e.getPlayer();
			final String strippedTitle = ChatColor.stripColor(inv.getTitle());
			
			if (inv.getType() == InventoryType.CHEST) {
				if (inv.getTitle().startsWith("§c§lLinked§9§lChest §8§l[")) {
					final String chestId = strippedTitle.substring(13, strippedTitle.length() - 1);
					
					if (!core.isValidId(chestId)) {
						core.sendPluginMessage(ply, core.msgs.getMessage("invalid_chest_id"));
						return;
					}
					
					final LinkedChest lc = new LinkedChest(core, chestId, ply);
					
					lc.storeInventory(inv);
					
					this.opened.remove(ply.getUniqueId());
				} else return;
			} else if (inv.getType() == InventoryType.HOPPER) {
				if (inv.getTitle().equals("§c§lLinking §9§lChest")) {
					final String chestId = core.itemsToChestId(inv.getContents());
					
					if (!core.isValidId(chestId)) {
						core.sendPluginMessage(ply, core.msgs.getMessage("invalid_chest_id"));
						this.linking.remove(ply.getUniqueId());
						return;
					}
					
					final LinkedChest lc = new LinkedChest(core, chestId, ply);
					
					if (!lc.hasFirst()) {
						lc.firstTimeLink(this.linking.get(ply.getUniqueId()));
						core.sendPluginMessage(ply, String.format(core.msgs.getMessage("partially_linked"), core.chestIdToColours(chestId)));
						core.sendPluginMessage(ply, core.msgs.getMessage("complete_link_reminder"));
						this.linking.remove(ply.getUniqueId());
						return;
					} else {
						if (!lc.hasSecond()) {
							lc.secondTimeLink(this.linking.get(ply.getUniqueId()));
							core.sendPluginMessage(ply, String.format(core.msgs.getMessage("completed_link"), core.chestIdToColours(chestId)));
							this.linking.remove(ply.getUniqueId());
							return;
						} else {
							core.sendPluginMessage(ply, String.format(core.msgs.getMessage("cannot_use_id"), core.chestIdToColours(chestId)));
							this.linking.remove(ply.getUniqueId());
							return;
						}
					}
				} else return;
			} else return;
		} catch (Exception exc) {}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(final InventoryClickEvent e) {
		try {
			final Inventory inv = e.getClickedInventory();
			final ClickType ck = e.getClick();
			
			final InventoryType[] bTypes = { InventoryType.ANVIL, InventoryType.BREWING, InventoryType.FURNACE, InventoryType.MERCHANT, InventoryType.WORKBENCH };
			
			if (e.isCancelled())
				return;
			
			ItemStack item = e.getCurrentItem();
			
			if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(core.getChestLinker().getItemMeta().getDisplayName())) {
				for (final InventoryType t : bTypes) {
					if (e.getInventory().getType() == t) {
						core.sendPluginMessage(e.getWhoClicked(), core.msgs.getMessage("cannot_use_chestlinker"));
						e.setCancelled(true);
						e.setResult(Result.DENY);
						return;
					}
				}
			}
			
			if (inv.getType() == InventoryType.HOPPER) {
				if (inv.getTitle().equals("§c§lLinking §9§lChest")) {
					switch (ck) {
						case LEFT:
							item.setAmount((item.getAmount() + 1 > 9) ? 1 : item.getAmount() + 1);
							break;
						case RIGHT:
							item.setAmount((item.getAmount() -1 < 1) ? 9 : item.getAmount() - 1);
							break;
						case SHIFT_LEFT:
							item = core.getNextColour(item);
							break;
						case SHIFT_RIGHT:
							item = core.getPreviousColour(item);
							break;
						default:
							break;
					}
					
					e.setCancelled(true);
					e.setResult(Result.DENY);
					inv.setItem(e.getSlot(), item);
				} else return;
			} else return;
		} catch (Exception exc) {}
	}
	
	// If linked chest breaks -> set hasSecond = false
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChestBreak(final BlockBreakEvent e) {
		try {
			final Player ply = e.getPlayer();
			final Block bk = e.getBlock();
			
			if (e.isCancelled())
				return;
			
			if (bk.getType() == Material.CHEST) {
				final Chest chest = (Chest) bk.getState();
				final Inventory inv = chest.getBlockInventory();
				final String strippedTitle = ChatColor.stripColor(inv.getTitle());
				
				if (inv.getTitle().startsWith("§c§lLinked§9§lChest §8§l[")) {
					if (inv.getViewers().size() > 0) {
						core.sendPluginMessage(ply, core.msgs.getMessage("cannot_break_in_use"));
						e.setCancelled(true);
						return;
					}
					
					final String chestId = strippedTitle.substring(13, strippedTitle.length() - 1);
					final LinkedChest lc = new LinkedChest(core, chestId, ply);
					
					if (lc.hasSecond()) {
						lc.setSecond(false);
						inv.clear();
						
						if (ply.getGameMode() == GameMode.SURVIVAL || ply.getGameMode() == GameMode.ADVENTURE)
							e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.CHEST, 1));
						
						core.sendPluginMessage(ply, String.format(core.msgs.getMessage("severed_link"), core.chestIdToColours(chestId)));
						return;
					} else {
						if (lc.removeChest()) {
							inv.clear();
				            
							for (final ItemStack item : lc.getStoredInventory()) {
								if (item != null)
									e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
							}
				            
							if (ply.getGameMode() == GameMode.SURVIVAL || ply.getGameMode() == GameMode.ADVENTURE)
								e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.CHEST, 1));
				            
							core.sendPluginMessage(ply, String.format(core.msgs.getMessage("removed_linked_chest"), core.chestIdToColours(chestId)));
							return;
						} else {
							e.setCancelled(true);
							
							core.sendPluginMessage(ply, String.format(core.msgs.getMessage("cannot_remove_linked_chest"), core.chestIdToColours(chestId)));
							return;
						}
					}
				} else return;
			} else return;
		} catch (Exception exc) {}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChestPlace(final BlockPlaceEvent e) {
		try {
			final Player ply = e.getPlayer();
			final Block bk = e.getBlock();
			final Location loc = bk.getLocation();
			
			if (e.isCancelled() || bk.getType() != Material.CHEST)
				return;
			
			final Block chestNORTH = loc.getWorld().getBlockAt(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - 1));
			final Block chestEAST = loc.getWorld().getBlockAt(new Location(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ()));
			final Block chestSOUTH = loc.getWorld().getBlockAt(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + 1));
			final Block chestWEST = loc.getWorld().getBlockAt(new Location(loc.getWorld(), loc.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ()));
			
			if (chestEAST.getType() == Material.CHEST || chestWEST.getType() == Material.CHEST || chestNORTH.getType() == Material.CHEST || chestSOUTH.getType() == Material.CHEST) {
				if (chestNORTH.getState() instanceof Chest && ((Chest) chestNORTH.getState()).getInventory().getTitle().startsWith("§c§lLinked§9§lChest §8§l[")) {
					core.sendPluginMessage(ply, core.msgs.getMessage("cannot_place_next_to_linked_chests"));
					e.setCancelled(true);
					return;
				} else if (chestEAST.getState() instanceof Chest && ((Chest) chestEAST.getState()).getInventory().getTitle().startsWith("§c§lLinked§9§lChest §8§l[")) {
					core.sendPluginMessage(ply, core.msgs.getMessage("cannot_place_next_to_linked_chests"));
					e.setCancelled(true);
					return;
				} else if (chestSOUTH.getState() instanceof Chest && ((Chest) chestSOUTH.getState()).getInventory().getTitle().startsWith("§c§lLinked§9§lChest §8§l[")) {
					core.sendPluginMessage(ply, core.msgs.getMessage("cannot_place_next_to_linked_chests"));
					e.setCancelled(true);
					return;
				} else if (chestWEST.getState() instanceof Chest && ((Chest) chestWEST.getState()).getInventory().getTitle().startsWith("§c§lLinked§9§lChest §8§l[")) {
					core.sendPluginMessage(ply, core.msgs.getMessage("cannot_place_next_to_linked_chests"));
					e.setCancelled(true);
					return;
				} else return;
			} else return;
		} catch (Exception exc) {}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHopperMoveItem(final InventoryMoveItemEvent e) {
		try {
			if (e.getSource().getTitle().startsWith("§c§lLinked§9§lChest §8§l[") || e.getDestination().getTitle().startsWith("§c§lLinked§9§lChest §8§l["))
				e.setCancelled(true);
		} catch (Exception exc) {}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void preventCraftingWithChestLinker(final CraftItemEvent e) {
		try {
			final Player ply = (Player) e.getWhoClicked();
			
			if (e.getInventory() instanceof CraftingInventory) {
				final CraftingInventory inv = (CraftingInventory) e.getInventory();
				final ItemStack[] mtx = inv.getMatrix();
		        
		        if (inv.getSize() == 5) {
		        	for (final ItemStack it : mtx) {
		        		if (it != null && it.hasItemMeta() && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equals(core.getChestLinker().getItemMeta().getDisplayName())) {
		        			core.sendPluginMessage(ply, core.msgs.getMessage("cannot_use_chestlinker"));
		        			e.setCancelled(true);
		        			e.setResult(Result.DENY);
		        			return;
		        		}
		        	}
		        } else return;
			} else return;
		} catch (Exception exc) {}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void explosionEvent(final EntityExplodeEvent e) {
		for (final Block bk : e.blockList().toArray(new Block[e.blockList().size()])) {
			if (bk.getState() instanceof Chest) {
				if (((Chest) bk.getState()).getCustomName().startsWith("§c§lLinked§9§lChest §8§l["))
					e.blockList().remove(bk);
			}
		}
	}
	
}
