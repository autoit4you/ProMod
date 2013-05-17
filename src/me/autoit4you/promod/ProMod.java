package me.autoit4you.promod;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ProMod extends JavaPlugin implements Listener{
	private static final Logger log = Logger.getLogger("Minecraft");
	private saveInventory inv = null;
	private HashMap<String, Boolean> mods = new HashMap<String, Boolean>();
	private List<String> commands = null;
	private List<String> commandsmod = null;
	private FileConfiguration config;
	
	@Override
	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		if(!new File("plugins/ProMod/config.yml").exists()){
			saveDefaultConfig();
		}
		if(!new File("plugins/ProMod/system.yml").exists()){
			try {
				new File("plugins/ProMod/system.yml").createNewFile();
				config = YamlConfiguration.loadConfiguration(new File("plugins/ProMod/system.yml"));
				config.createSection("playersinv");
				config.createSection("playersarmor");
				config.createSection("playershealth");
				config.save(new File("plugins/ProMod/system.yml"));
			} catch (IOException e) {
				e.printStackTrace();
				log.severe("Could not create or load 'plugins/ProMod/system.yml'! Disabling...");
				pm.disablePlugin(this);
				return;
			}
		}else{
			config = YamlConfiguration.loadConfiguration(new File("plugins/ProMod/system.yml"));
		}
		commands = getConfig().getStringList("blocked-commands");
		commandsmod = getConfig().getStringList("blocked-commands-mod");
		if(commands == null){
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		inv = new saveInventory();
		pm.registerEvents(this, this);
	}
	
	@Override
	public void onDisable(){
		try {
			config.save(new File("plugins/ProMod/system.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage("You can't use this command as non-player!");
			return true;
		}
		Player player = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("player") && player.hasPermission("promod.watched")){
			if(!mods.containsKey(player.getName())){
				mods.put(player.getName(), true);
				player(player);
				sender.sendMessage(ChatColor.GREEN + "You can now play as normal user!");
				return true;
			}else{
				sender.sendMessage(ChatColor.RED + "You are already playing as normal user!");
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("mod") && player.hasPermission("promod.watched")){
			if(mods.containsKey(player.getName())){
				mods.remove(player.getName());
				mod(player);
				sender.sendMessage(ChatColor.GREEN + "You are now in Moderator mode!");
				return true;
			}else{
				sender.sendMessage(ChatColor.RED + "You are already in Moderator mode!");
				return true;
			}
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		if(player.hasPermission("promod.watched") && mods.containsKey(player.getName())){
			String message = null;
			if(event.getMessage().contains("/")){
				int index = event.getMessage().length();
				if(event.getMessage().contains(" ")){
					index = event.getMessage().indexOf(" ");
				}
				message = event.getMessage().substring(1, index);
			}else{
				return;
			}
			if(commands.contains(message)){
				if(!player.hasPermission("promod.ignore")){
					player.sendMessage(ChatColor.RED + "You cant use that command while playing as normal user!");
					event.setCancelled(true);
				}
			}
		}
		if(player.hasPermission("promod.watched") && !mods.containsKey(player.getName())){
			String message = null;
			if(event.getMessage().contains("/")){
				int index = event.getMessage().length();
				if(event.getMessage().contains(" ")){
					index = event.getMessage().indexOf(" ");
				}
				message = event.getMessage().substring(1, index);
			}else{
				return;
			}
			if(commandsmod.contains(message)){
				if(!player.hasPermission("promod.ignore")){
					player.sendMessage(ChatColor.RED + "You cant use that command while playing as moderator!");
					event.setCancelled(true);
				}
			}
		}
	}
	
	private void mod(Player player){
		String inventory = inv.saveInventorytoString(player.getInventory());
		String armor = inv.saveArmortoString(player.getInventory().getHelmet(), player.getInventory().getChestplate(), player.getInventory().getLeggings(), player.getInventory().getBoots());
		String health = inv.saveHealthtoString(player);
		
		config.set("playersinv." + player.getName(), inventory);
		config.set("playersarmor." + player.getName(), armor);
		config.set("playershealth." + player.getName(), health);
		
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(0));
		player.getInventory().setChestplate(new ItemStack(0));
		player.getInventory().setLeggings(new ItemStack(0));
		player.getInventory().setBoots(new ItemStack(0));
		player.setGameMode(getServer().getDefaultGameMode());
		player.setHealth(20);
		player.setFallDistance(0);
		player.setFireTicks(0);
		player.setFoodLevel(20);
		player.setExp(0);
		player.setTotalExperience(0);
		//player.teleport(player.getWorld().getSpawnLocation());
	}
	
	private void player(Player player){
		player.setGameMode(getServer().getDefaultGameMode());
		player.getInventory().clear();
		try{
			if(config.isSet("playersinv." + player.getName())){
				Object hash1 = config.get("playersinv." + player.getName());
				if(!(hash1 instanceof String))
					return;
				Inventory inventory = inv.getInventoryfromString((String) hash1);
				
				int i = 0;
				for(ItemStack item : inventory){
					if(item == null){
						player.getInventory().setItem(i, new ItemStack(0));
					}
					player.getInventory().setItem(i, item);
					i++;
				}
			}
			if(config.isSet("playersarmor." + player.getName())){
				Object hash2 = config.get("playersarmor." + player.getName());
				if(!(hash2 instanceof String))
					return;
				Inventory inventory = inv.getArmorfromString((String) hash2);
				
				int i = 0;
				for(ItemStack item : inventory){
					if(item == null){
						if(i == 0)
							player.getInventory().setHelmet(new ItemStack(0));
						if(i == 1)
							player.getInventory().setChestplate(new ItemStack(0));
						if(i == 2)
							player.getInventory().setLeggings(new ItemStack(0));
						if(i == 3)
							player.getInventory().setBoots(new ItemStack(0));
					}
					if(i == 0)
						player.getInventory().setHelmet(item);
					if(i == 1)
						player.getInventory().setChestplate(item);
					if(i == 2)
						player.getInventory().setLeggings(item);
					if(i == 3)
						player.getInventory().setBoots(item);
					i++;
				}
			}
			if(config.isSet("playershealth." + player.getName())){
				Object hash3 = config.get("playershealth." + player.getName());
				if(!(hash3 instanceof String))
					return;
				inv.restorePlayerfromString((String) hash3);
			}
		} catch(NullPointerException e){

		}
	}
}
