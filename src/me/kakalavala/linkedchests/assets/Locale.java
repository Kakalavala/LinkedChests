package me.kakalavala.linkedchests.assets;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import me.kakalavala.linkedchests.core.Core;

public class Locale {
	
	private Core core;
	private File cfg;
	private YamlConfiguration yml;
	
	public Locale(final Core core) {
		this.core = core;
		this.cfg = new File(core.getDataFolder() + "/messages.yml");
		this.yml = YamlConfiguration.loadConfiguration(this.cfg);
		
		if (!this.cfg.exists()) {
			this.yml.set("already_being_linked", "&cThat chest is already being linked.");
			this.yml.set("already_linked", "&cCannot link an already linked chest.");
			this.yml.set("attached_id_doesnt_exist", "&cThe attached ID (%s&c) does not exist!");
			this.yml.set("being_viewed", "&cChest is being viewed by &6%s");
			this.yml.set("cannot_be_double_chest", "&cThe chest cannot be a double chest.");
			this.yml.set("cannot_break_in_use", "&cCannot break Linked Chest, it's currently in use.");
			this.yml.set("cannot_interact_being_linked", "&cCannot interact with a chest being linked.");
			this.yml.set("cannot_place_next_to_linked_chests", "&cCannot place next to Linked Chests.");
			this.yml.set("cannot_remove_linked_chest", "&4Failed to remove Linked Chest (%s&4)");
			this.yml.set("cannot_use_chestlinker", "&cYou cannot use the &c&lChest&9&lLinker");
			this.yml.set("cannot_use_id", "&cCannot use that ID! %s &cis already linked!");
			this.yml.set("chestlinker_lore", "&7Right-Click a chest to link it.");
			this.yml.set("complete_link_reminder", "&cRemember to complete the link! Before another player links to it!");
			this.yml.set("completed_link", "&a&lCompleted link to &r%s");
			this.yml.set("failed_setup", "&cFailed Linked Chest setup in constructor.");
			this.yml.set("invalid_chest_id", "&cInvalid ChestID.");
			this.yml.set("lack_permission_command", "&cYou lack permission to use that command!");
			this.yml.set("lack_permission_link_chests", "&cYou lack permission to create Linked Chests!");
			this.yml.set("linking_chest_displayname", "&rLeft/Right Click to +/- Number");
			this.yml.set("linking_chest_lore", "&bShift+Left/Right Click to change colour");
			this.yml.set("must_be_closed", "&cThe chest must be closed.");
			this.yml.set("must_be_empty", "&cThe chest must be empty.");
			this.yml.set("must_be_player", "&cYou must be a player to use this command.");
			this.yml.set("partially_linked", "&aPartially linked to %s");
			this.yml.set("prefix", "&8&l[&c&lLinked&9&lChests&8&l] &r");
			this.yml.set("removed_linked_chest", "&cRemoved Linked Chest (%s&c)");
			this.yml.set("severed_link", "&cSevered link with %s");
			
			this.save();
		}
	}

	private void save() {
		try {
			this.yml.save(this.cfg);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public String getMessage(final String id) {
		try {
			this.cfg = new File(core.getDataFolder() + "/messages.yml");
			this.yml = YamlConfiguration.loadConfiguration(this.cfg);
			
			return (this.yml.getString(id).length() > 0) ? ChatColor.translateAlternateColorCodes('&', this.yml.getString(id)) : "";
		} catch (Exception exc) {
			return String.format("§cCould not find the message (§6%s§c) in the messages.yml!", id);
		}
	}

}
