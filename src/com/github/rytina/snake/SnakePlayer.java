package com.github.rytina.snake;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SnakePlayer {
	Snake plugin;
	String name;
	int status = 0;
	Block firstCorner;
	Block secondCorner;
	Block upArrow;
	Block downArrow;
	Block leftArrow;
	Block rightArrow;
	Block startButton;
	Block leaderBoard;
	String arenaName;

	public SnakePlayer(Snake plugin, String name) {
		this.plugin = plugin;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public int getStatus() {
		return this.status;
	}

	public Block getFirstCorner() {
		return this.firstCorner;
	}

	public Block getSecondCorner() {
		return this.secondCorner;
	}

	public Block getUpArrow() {
		return this.upArrow;
	}

	public Block getDownArrow() {
		return this.downArrow;
	}

	public Block getLeftArrow() {
		return this.leftArrow;
	}

	public Block getRightArrow() {
		return this.rightArrow;
	}

	public Block getStartButton() {
		return this.startButton;
	}

	public Block getLeaderBoard() {
		return this.leaderBoard;
	}

	public void nextStep(String aName) {
		Player p = Bukkit.getServer().getPlayer(this.name);
		this.arenaName = aName;
		if (this.status == 0) {
			p.sendMessage("Click the top left corner of your arena.");
		}

		++this.status;
	}

	public boolean clickedBlock(Block block) {
		Player p = Bukkit.getServer().getPlayer(this.name);
		if (this.status > 0) {
			if (this.status == 1) {
				this.firstCorner = block;
				p.sendMessage("Click the bottom right corner of your arena.");
			} else if (this.status == 2) {
				this.secondCorner = block;
				p.sendMessage("Click the up arrow of your gamepad.");
			} else if (this.status == 3) {
				this.upArrow = block;
				p.sendMessage("Click the down arrow of your gamepad.");
			} else if (this.status == 4) {
				this.downArrow = block;
				p.sendMessage("Click the left arrow of your gamepad.");
			} else if (this.status == 5) {
				this.leftArrow = block;
				p.sendMessage("Click the right arrow of your gamepad.");
			} else if (this.status == 6) {
				this.rightArrow = block;
				p.sendMessage("Click the sign you want to be your leaderboard.");
			} else if (this.status == 7) {
				if (block.getType() != Material.OAK_SIGN) {
					p.sendMessage("Click a sign.");
					return true;
				}

				this.leaderBoard = block;
				p.sendMessage("Click the start button of your arena.");
			}  
			else if (this.status == 8) {
				if (block.getType() != Material.OAK_BUTTON && block.getType() != Material.STONE_BUTTON) {
					p.sendMessage("Click a button.");
					return true;
				}

				this.startButton = block;
				p.sendMessage("Snake Created.");

				try {
					this.plugin.createSnakeArena(this);
				} catch (Exception var4) {
					var4.printStackTrace();
				}

				this.status = 0;
				return true;
			}

			++this.status;
			return true;
		} else {
			return false;
		}
	}
}