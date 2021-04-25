package com.github.rytina.snake;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class GameLoop extends BukkitRunnable {
	private final Snake plugin;
	private SnakeArena arena;
	private BlockLocation a;
	private BlockLocation b;
	private BlockLocation food;
	private int score = 0;
	int frozenAxis;
	private Player player;
	ArrayList<BlockLocation> blockList = new ArrayList<BlockLocation>();
	
	private static final String SCORE = "score: ";

	public GameLoop(Snake plugin, SnakeArena arena, Player p) {
		this.player = p;
		arena.player = p.getName();
		initializeScoreBoard();
		this.plugin = plugin;
		this.arena = arena;
		this.a = new BlockLocation(arena.getFirstCorner());
		this.b = new BlockLocation(arena.getSecondCorner());
		int inc = 1;
		int inc2 = 1;
		int y;
		int x;
		Block c;
		if (this.a.getX() == this.b.getX()) {
			this.frozenAxis = 1;
			if (this.a.getY() > this.b.getY()) {
				arena.upDirection = new BlockLocation(0, 1, 0);
				inc = -1;
			}

			for (y = this.a.getY(); y != this.b.getY() + inc; y += inc) {
				arena.leftDirection = new BlockLocation(0, 0, -1);
				arena.currentDirection = new BlockLocation(0, 0, 1);
				if (this.a.getZ() > this.b.getZ()) {
					arena.leftDirection = new BlockLocation(0, 0, 1);
					arena.currentDirection = new BlockLocation(0, 0, -1);
					inc2 = -1;
				}

				for (x = this.a.getZ(); x != this.b.getZ() + inc2; x += inc2) {
					c = Bukkit.getWorld(this.a.getWorld()).getBlockAt(this.a.getX(), y, x);
					if (c.getType() != Material.IRON_BLOCK) {
						c.setType(Material.IRON_BLOCK);
					}
				}
			}
		} else if (this.a.getZ() == this.b.getZ()) {
			this.frozenAxis = 3;
			if (this.a.getY() > this.b.getY()) {
				arena.upDirection = new BlockLocation(0, 1, 0);
				inc = -1;
			}

			for (y = this.a.getY(); y != this.b.getY() + inc; y += inc) {
				arena.leftDirection = new BlockLocation(-1, 0, 0);
				arena.currentDirection = new BlockLocation(1, 0, 0);
				if (this.a.getX() > this.b.getX()) {
					arena.leftDirection = new BlockLocation(1, 0, 0);
					arena.currentDirection = new BlockLocation(-1, 0, 0);
					inc2 = -1;
				}

				for (x = this.a.getX(); x != this.b.getX() + inc2; x += inc2) {
					c = Bukkit.getWorld(this.a.getWorld()).getBlockAt(x, y, this.a.getZ());
					if (c.getType() != Material.IRON_BLOCK) {
						c.setType(Material.IRON_BLOCK);
					}
				}
			}
		}

		BlockLocation start = new BlockLocation(Math.round((float) ((this.a.getX() + this.b.getX()) / 2)),
				Math.round((float) ((this.a.getY() + this.b.getY()) / 2)),
				Math.round((float) ((this.a.getZ() + this.b.getZ()) / 2)), this.a.getWorld());
		BlockLocation secondBlock = start.add(arena.leftDirection);
		BlockLocation thirdBlock = secondBlock.add(arena.leftDirection);
		this.addBlock(start);
		this.addBlock(secondBlock);
		this.addBlock(thirdBlock);
		this.blockList.add(start);
		this.blockList.add(secondBlock);
		this.blockList.add(thirdBlock);
		arena.lastDirection = arena.currentDirection;
		this.addFood();
	}

	private void initializeScoreBoard() {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		String boardTitle = "Score";
		Objective objective = board.registerNewObjective(boardTitle, "dummy", boardTitle);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score = objective.getScore(SCORE);
		score.setScore(0);
		player.setScoreboard(board);		
	}

	private void addFood() {
		int x = (int) ((long) this.b.getX() + Math.round(Math.random() * (double) (this.a.getX() - this.b.getX())));
		int y = (int) ((long) this.b.getY() + Math.round(Math.random() * (double) (this.a.getY() - this.b.getY())));
		int z = (int) ((long) this.b.getZ() + Math.round(Math.random() * (double) (this.a.getZ() - this.b.getZ())));
		this.food = new BlockLocation(x, y, z, this.a.getWorld());
		Bukkit.getWorld(this.a.getWorld()).getBlockAt(x, y, z).setType(Material.GOLD_BLOCK);
	}

	private void addBlock(BlockLocation bL) {
		Block b = Bukkit.getWorld(bL.getWorld()).getBlockAt(bL.getX(), bL.getY(), bL.getZ());
		b.setType(Material.OBSIDIAN);
	}

	private void removeBlock(BlockLocation bL) {
		Block b = Bukkit.getWorld(bL.getWorld()).getBlockAt(bL.getX(), bL.getY(), bL.getZ());
		b.setType(Material.IRON_BLOCK);
	}

	private void advanceSnake() {
		BlockLocation newHead = ((BlockLocation) this.blockList.get(0)).add(this.arena.currentDirection);
		if (!this.checkFood(newHead)) {
			if (!this.checkBounds(newHead)) {
				this.addBlock(newHead);
				this.removeBlock((BlockLocation) this.blockList.get(this.blockList.size() - 1));
				this.blockList.add(0, newHead);
				this.blockList.remove(this.blockList.size() - 1);
				this.arena.lastDirection = this.arena.currentDirection;
			}
		}
	}

	private Boolean checkBounds(BlockLocation test) {
		if ((test.getX() <= this.a.getX() || test.getX() <= this.b.getX())
				&& (test.getY() <= this.a.getY() || test.getY() <= this.b.getY())
				&& (test.getZ() <= this.a.getZ() || test.getZ() <= this.b.getZ())
				&& (test.getX() >= this.a.getX() || test.getX() >= this.b.getX())
				&& (test.getY() >= this.a.getY() || test.getY() >= this.b.getY())
				&& (test.getZ() >= this.a.getZ() || test.getZ() >= this.b.getZ())) {
			for (int i = 0; i < this.blockList.size(); ++i) {
				if (test.equivalent((BlockLocation) this.blockList.get(i))) {
					this.end();
					return true;
				}
			}

			return false;
		} else {
			this.end();
			return true;
		}
	}

	private void end() {
		BlockLocation death = (BlockLocation) this.blockList.get(0);
		Bukkit.getWorld(death.getWorld()).getBlockAt(death.getX(), death.getY(), death.getZ())
				.setType(Material.REDSTONE_BLOCK);
		this.arena.end(this.score, this.arena.player);
		this.plugin.end(this);
	}

	private Boolean checkFood(BlockLocation test) {
		for (int i = 0; i < this.blockList.size(); ++i) {
			BlockLocation t = (BlockLocation) this.blockList.get(i);
			if (t.equivalent(this.food)) {
				this.blockList.add(0, test);
				this.addBlock(test);
				this.addFood();
				this.score += 10;
				player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(SCORE).setScore(score);
				return true;
			}
		}

		return false;
	}

	public void run() {
		this.advanceSnake();
	}
}