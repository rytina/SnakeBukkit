package com.github.rytina.snake;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Snake extends JavaPlugin {
	public final SnakeListener snakeListener = new SnakeListener(this);
	public ArrayList<SnakePlayer> playerList = new ArrayList<SnakePlayer>();
	public ArrayList<SnakeArena> snakeList = new ArrayList<SnakeArena>();

	@Override
	public void onEnable() {
		this.loadArenas();
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(this.snakeListener, this);

		Bukkit.getLogger().info("Snake enabled.");
	}

	@Override
	public void onDisable() {
		Bukkit.getLogger().info("Snake disabled.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		SnakePlayer pp = this.checkPlayer(p);
		if (!cmd.getName().equalsIgnoreCase("snake")) {
			return false;
		}
		if (args.length == 1) {
			this.startSnakeCreation(pp, args[0]);
		} else {
			p.sendMessage("Usage: /snake NAME");
		}
		return true;
	}

	private void startSnakeCreation(SnakePlayer pp, String name) {
		Player p = Bukkit.getPlayer(pp.getName());
		File newArenaFile = new File(this.getDataFolder() + "/Arenas/", name + ".yml");
		Bukkit.getLogger().info("NEw area file is " + newArenaFile.getAbsolutePath());
		if (newArenaFile.exists()) {
			p.sendMessage("Snake Arena under that name already exists.");
		} else {
			if (pp.getStatus() == 0) {
				pp.nextStep(name);
			}

		}
	}

	public SnakePlayer checkPlayer(Player player) {
		for (int i = 0; i < this.playerList.size(); ++i) {
			if (((SnakePlayer) this.playerList.get(i)).getName().equals(player.getName())) {
				return (SnakePlayer) this.playerList.get(i);
			}
		}

		SnakePlayer newPlayer = new SnakePlayer(this, player.getName());
		this.playerList.add(newPlayer);
		return newPlayer;
	}

	private void loadArenas() {
		File folder = new File(this.getDataFolder() + "/Arenas/");
		if (!folder.exists()) {
			folder.mkdirs();
		}

		File[] files = folder.listFiles();

		for (int i = 0; i < files.length; ++i) {
			SnakeArena newArena = new SnakeArena(this, files[i]);
			this.snakeList.add(newArena);
		}

	}

	public void createSnakeArena(SnakePlayer snakePlayer)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		File newArenaFile = new File(this.getDataFolder() + "/Arenas/", snakePlayer.arenaName + ".yml");
		new YamlConfiguration();
		if (!newArenaFile.exists()) {
			newArenaFile.getParentFile().mkdirs();
			this.copy(this.getResource("arenaTemplate.yml"), newArenaFile);
			this.setCoords(newArenaFile, "FirstCorner", snakePlayer.getFirstCorner());
			this.setCoords(newArenaFile, "SecondCorner", snakePlayer.getSecondCorner());
			this.setCoords(newArenaFile, "UpArrow", snakePlayer.getUpArrow());
			this.setCoords(newArenaFile, "DownArrow", snakePlayer.getDownArrow());
			this.setCoords(newArenaFile, "LeftArrow", snakePlayer.getLeftArrow());
			this.setCoords(newArenaFile, "RightArrow", snakePlayer.getRightArrow());
			this.setCoords(newArenaFile, "StartButton", snakePlayer.getStartButton());
			this.setCoords(newArenaFile, "LeaderBoard", snakePlayer.getLeaderBoard());
		}

		SnakeArena newArena = new SnakeArena(this, snakePlayer, newArenaFile);
		this.snakeList.add(newArena);
	}

	private void setCoords(File file, String string, Block block)
			throws FileNotFoundException, IOException, InvalidConfigurationException {
		FileConfiguration config = new YamlConfiguration();
		config.load(file);
		config.set("World", block.getWorld().getName());
		config.set(string + ".X", block.getX());
		config.set(string + ".Y", block.getY());
		config.set(string + ".Z", block.getZ());
		config.save(file);
	}

	public void buttonPressed(Block clickedBlock, Player player) {
		for (int i = 0; i < this.snakeList.size(); ++i) {
			if (((SnakeArena) this.snakeList.get(i)).checkStartButton(clickedBlock, player)) {
				return;
			}
		}

	}

	public void createRepeater(SnakeArena snakeArena, Player player) {
		(new GameLoop(this, snakeArena, player)).runTaskTimer(this, 10L, 10L);
	}

	public boolean blockPunched(Block block, Player p) {
		for (int i = 0; i < this.snakeList.size(); ++i) {
			if (((SnakeArena) this.snakeList.get(i)).blockPunched(block, p)) {
				return true;
			}
		}

		return false;
	}

	public void end(GameLoop sRepeat) {
		sRepeat.cancel();
	}

	public void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];

			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}

			out.close();
			in.close();
		} catch (Exception var6) {
			var6.printStackTrace();
		}

	}
}