package me.kakalavala.linkedchests.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kakalavala.linkedchests.core.Core;

public class Command_GiveChestLinker implements CommandExecutor {
	
	private Core core;
	
	public Command_GiveChestLinker(final Core core) {
		this.core = core;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		if (sender instanceof Player) {
			if (sender.hasPermission("linkedchests.command.givechestlinker")) {
				((Player) sender).getInventory().addItem(core.getChestLinker());
				return true;
			} else {
				core.sendPluginMessage(sender, core.msgs.getMessage("lack_permission_command"));
				return false;
			}
		} else {
			core.sendPluginMessage(sender, core.msgs.getMessage("must_be_player"));
			return false;
		}
	}

}
