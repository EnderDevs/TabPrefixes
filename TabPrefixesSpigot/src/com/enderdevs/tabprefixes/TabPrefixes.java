package com.enderdevs.tabprefixes;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TabPrefixes extends JavaPlugin implements Listener{

public Scoreboard sb = null;
	
	public Permission perms;

	@Override
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
		if(getConfig().getConfigurationSection("teams") == null){
			getConfig().set("teams.default", "Default");
			saveConfig();
		}
		if(getConfig().getConfigurationSection("chat") == null){
			getConfig().set("chat.Owner", "&b");
			saveConfig();
		}
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		for(String s : getConfig().getConfigurationSection("teams").getKeys(false)){
			Team t = sb.registerNewTeam(s);
			t.setPrefix(getConfig().getString("teams." + s));
		}
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            perms = permissionProvider.getProvider();
        }
        if(perms == null){
        	getLogger().warning("Vault and a compatible Permissions plugin could not be found! Disabling");
        	setEnabled(false);
        }
	}

	@Override
	public void onDisable(){
		sb = null;
	}
	
	public String getGroup(Player p){
		return perms.getPrimaryGroup(p);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args){
		if(sender.hasPermission("tabprefixes.reload")){
			onDisable();
			onEnable();
			for(Player p : Bukkit.getOnlinePlayers()){
				sb.getTeam(getGroup(p)).addPlayer(p);
				p.setScoreboard(sb);
			}
			sender.sendMessage(ChatColor.GREEN + "Reload Successful.");
			return true;
		}
		return true;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		e.setMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("chat." + getGroup(e.getPlayer()))) + e.getMessage());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(sb.getTeam(getGroup(e.getPlayer())) != null){
		sb.getTeam(getGroup(e.getPlayer())).addPlayer(e.getPlayer());
		}else{
			getLogger().warning("Group " + getGroup(e.getPlayer()) + " doesn't exist!");
		}
		for(Player p : Bukkit.getOnlinePlayers()){
			p.setScoreboard(sb);
		}
	}
	
}
