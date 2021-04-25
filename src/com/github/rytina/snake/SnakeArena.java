package com.github.rytina.snake;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SnakeArena {
	public BlockLocation currentDirection;
	public BlockLocation lastDirection;
	public BlockLocation upDirection;
	public BlockLocation leftDirection;
	public String player;
	Block firstCorner;
	Block secondCorner;
	Block upArrow;
	Block downArrow;
	Block leftArrow;
	Block rightArrow;
	Block startButton;
	Block leaderBoard;
	boolean gameInProgress = false;
	Snake plugin;
	String[] highScoreNames = new String[3];
	File file;
	int[] highScores = new int[3];

	public SnakeArena(Snake plugin, SnakePlayer p, File file) {
		this.file = file;
		this.plugin = plugin;
		this.firstCorner = p.getFirstCorner();
		this.secondCorner = p.getSecondCorner();
		this.upArrow = p.getUpArrow();
		this.downArrow = p.getDownArrow();
		this.leftArrow = p.getLeftArrow();
		this.rightArrow = p.getRightArrow();
		this.startButton = p.getStartButton();
		this.leaderBoard = p.getLeaderBoard();
		YamlConfiguration config = new YamlConfiguration();

		try {
			config.load(file);
		} catch (Exception var6) {
			var6.printStackTrace();
		}

		for (int i = 0; i < 3; ++i) {
			this.highScoreNames[i] = config.getString("HighScores." + (i + 1) + ".Name");
			this.highScores[i] = config.getInt("HighScores." + (i + 1) + ".Score");
		}
		this.updateLeaderBoard();
	}
	
	private void updateLeaderBoard() {
		Sign sign = (Sign) this.leaderBoard.getState();
		sign.setLine(0, "[LeaderBoard]");

		for (int i = 0; i < 3; ++i) {
			sign.setLine(i + 1, this.highScoreNames[i] + ":" + this.highScores[i]);
		}

		sign.update();
	}

	public SnakeArena(Snake plugin, File file) {
		this.plugin = plugin;
		this.file = file;
		YamlConfiguration config = new YamlConfiguration();

		try {
			config.load(file);
		} catch (Exception var5) {
			var5.printStackTrace();
		}

		World world = Bukkit.getWorld(config.getString("World"));
		this.firstCorner = world.getBlockAt(config.getInt("FirstCorner.X"), config.getInt("FirstCorner.Y"),
				config.getInt("FirstCorner.Z"));
		this.secondCorner = world.getBlockAt(config.getInt("SecondCorner.X"), config.getInt("SecondCorner.Y"),
				config.getInt("SecondCorner.Z"));
		this.upArrow = world.getBlockAt(config.getInt("UpArrow.X"), config.getInt("UpArrow.Y"),
				config.getInt("UpArrow.Z"));
		this.downArrow = world.getBlockAt(config.getInt("DownArrow.X"), config.getInt("DownArrow.Y"),
				config.getInt("DownArrow.Z"));
		this.leftArrow = world.getBlockAt(config.getInt("LeftArrow.X"), config.getInt("LeftArrow.Y"),
				config.getInt("LeftArrow.Z"));
		this.rightArrow = world.getBlockAt(config.getInt("RightArrow.X"), config.getInt("RightArrow.Y"),
				config.getInt("RightArrow.Z"));
		this.startButton = world.getBlockAt(config.getInt("StartButton.X"), config.getInt("StartButton.Y"),
				config.getInt("StartButton.Z"));
		this.leaderBoard = world.getBlockAt(config.getInt("LeaderBoard.X"), config.getInt("LeaderBoard.Y"),
				config.getInt("LeaderBoard.Z"));

		for (int i = 0; i < 3; ++i) {
			this.highScoreNames[i] = config.getString("HighScores." + (i + 1) + ".Name");
			this.highScores[i] = config.getInt("HighScores." + (i + 1) + ".Score");
		}
	}

	public boolean checkStartButton(Block clickedBlock, Player player) {
		if (clickedBlock.equals(this.startButton) && !this.gameInProgress) {
			this.plugin.createRepeater(this, player);
			this.gameInProgress = true;
			return true;
		} else {
			return false;
		}
	}

	public Block getFirstCorner() {
		return this.firstCorner;
	}

	public Block getSecondCorner() {
		return this.secondCorner;
	}

	public boolean blockPunched(Block block, Player p) {
		if (p.getName().equals(this.player)) {
			if (block.equals(this.upArrow)) {
				if (!this.lastDirection.equivalent(this.upDirection.invert())) {
					this.currentDirection = this.upDirection;
				}
			} else if (block.equals(this.downArrow)) {
				if (!this.lastDirection.equivalent(this.upDirection)) {
					this.currentDirection = this.upDirection.invert();
				}
			} else if (block.equals(this.leftArrow)) {
				if (!this.lastDirection.equivalent(this.leftDirection.invert())) {
					this.currentDirection = this.leftDirection;
				}
			} else {
				if (!block.equals(this.rightArrow)) {
					return false;
				}

				if (!this.lastDirection.equivalent(this.leftDirection)) {
					this.currentDirection = this.leftDirection.invert();
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public void end(int score, String player) {
		for (int i = 0; i < 3; ++i) {
			if (score > this.highScores[i]) {
				for (i = 2; i > i; --i) {
					this.highScores[i] = this.highScores[i - 1];
					this.highScoreNames[i] = this.highScoreNames[i - 1];
				}

				this.highScores[i] = score;
				this.highScoreNames[i] = player;
				break;
			}
		}

		YamlConfiguration config = new YamlConfiguration();

		try {
			config.load(this.file);
		} catch (Exception var6) {
			var6.printStackTrace();
		}

		for (int i = 0; i < 3; ++i) {
			config.set("HighScores." + (i + 1) + ".Name", this.highScoreNames[i]);
			config.set("HighScores." + (i + 1) + ".Score", this.highScores[i]);
		}

		try {
			config.save(this.file);
		} catch (IOException var5) {
			var5.printStackTrace();
		}

		this.gameInProgress = false;
		updateLeaderBoard();
	}
}