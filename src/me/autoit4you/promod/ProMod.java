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
		if(cmd.getName().equalsIgnoreCase("player")){
			if(!mods.containsKey(player.getName())){
				mods.put(player.getName(), true);
				player(player);
				sender.sendMessage(ChatColor.GREEN + "You can now play as normal user!");
				return true;
			}else{
				sender.sendMessage(ChatColor.RED + "You are already playing as normal user!");
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("mod")){
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
				//if(!player.hasPermission("promod.ignore")){
					player.sendMessage(ChatColor.RED + "You cant use that command while playing as normal user!");
					event.setCancelled(true);
				//}
			}
		}
	}
	
	private void mod(Player player){
		String inventory = inv.saveInventorytoString(player.getInventory());
		String path = "playersinv." + player.getName();
		config.set(path, inventory);
		player.getInventory().clear();
		player.setGameMode(getServer().getDefaultGameMode());
	}
	
	private void player(Player player){
		player.setGameMode(getServer().getDefaultGameMode());
		player.getInventory().clear();
		try{
			String path = "playersinv." + player.getName();
			if(config.isSet(path)){
				Object hash1 = config.get(path);
				if(!(hash1 instanceof String))
					return;
				Inventory inventory = inv.getInventoryfromString((String) hash1);
				
				int i = 0;
				for(ItemStack item : inventory){
					if(item == null)
						continue;
					player.getInventory().setItem(i, item);
				}
			}
		} catch(NullPointerException e){

		}
	}
}
